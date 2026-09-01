package surveyprogram.processor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import surveyprogram.object.Deadline;
import surveyprogram.object.Event;
import surveyprogram.object.TaskList;
import surveyprogram.object.Todo;

/**
 * Interprets supported task commands and returns the dialogue for each command.
 */
public class TaskCommandProcessor {
    private static final List<DateTimeFormatter> DATETIME_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy-M-d HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-M-d HHmm"),
            DateTimeFormatter.ofPattern("yyyy/M/d HH:mm"),
            DateTimeFormatter.ofPattern("yyyy/M/d HHmm"),
            DateTimeFormatter.ofPattern("d-M-yyyy HH:mm"),
            DateTimeFormatter.ofPattern("d-M-yyyy HHmm"),
            DateTimeFormatter.ofPattern("d/M/yyyy HH:mm"),
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm")
    );

    private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy-M-d"),
            DateTimeFormatter.ofPattern("yyyy/M/d"),
            DateTimeFormatter.ofPattern("d-M-yyyy"),
            DateTimeFormatter.ofPattern("d/M/yyyy")
    );

    private final TaskList taskList;
    /**
     * Creates a processor that applies commands to a task list.
     *
     * @param taskList task list to query and modify
     */
    public TaskCommandProcessor(TaskList taskList) {
        this.taskList = taskList;
    }

    /**
     * Processes one command.
     *
     * @param input the exact line entered by the user
     * @return dialogue and whether the application should accept another command
     */
    public CommandResult process(String input) {
        String lowerCaseInput = input.toLowerCase();
        if (lowerCaseInput.trim().equals("bye")) {
            return new CommandResult("", false);
        }
        String response;
        if (lowerCaseInput.trim().equals("list")) {
            response = "VERY WELL. HERE IS YOUR LIST:\n" + taskList.read();
        } else if (lowerCaseInput.trim().equals("find") || lowerCaseInput.startsWith("find ")) {
            response = taskList.find(input.substring(4).trim());
        } else if (lowerCaseInput.startsWith("date ")) {
            response = handleDate(input);
        } else if (lowerCaseInput.startsWith("mark ") || lowerCaseInput.startsWith("unmark ")) {
            response = handleMark(input);
        } else if (lowerCaseInput.startsWith("todo ")) {
            response = taskList.add(new Todo(input.substring(5).trim()));
        } else if (lowerCaseInput.startsWith("deadline ")) {
            response = addDeadline(input);
        } else if (lowerCaseInput.startsWith("event ")) {
            response = addEvent(input);
        } else if (lowerCaseInput.startsWith("delete ")) {
            response = handleDelete(input);
        } else {
            response = "WELL, THAT IS NO LONGER A COMMAND.";
        }
        return new CommandResult(response, true);
    }

    /**
     * Parses a date command and prints tasks occurring on that date.
     *
     * @param input complete date command entered by the user
     */
    private String handleDate(String input) {
        String[] parts = input.split(" ", 2);
        if (parts.length < 2) {
            return "BUT, THERE WAS NOT A DATE TO CHECK.";
        }
        try {
            // parseDateTime returns LocalDateTime (time defaults to 00:00 if absent)
            LocalDateTime dateTime = parseDateTime(parts[1].trim());
            LocalDate date = dateTime.toLocalDate();
            return taskList.listByDate(date);
        } catch (DateTimeParseException exception) {
            return "BUT, THE DATE IS INVALID.";
        }
    }

    /**
     * Parses a one-based task index and marks or unmarks the selected task.
     *
     * @param input complete mark or unmark command entered by the user
     */
    private String handleMark(String input) {
        String[] commandParts = input.trim().split("\\s+", 2);
        if (commandParts.length < 2) {
            return "BUT, THE OBJECT IS NOT SPECIFIED.";
        }
        try {
            int itemIndex = Integer.parseInt(commandParts[1]) - 1;
            return taskList.mark(itemIndex, commandParts[0].equals("unmark"));
        } catch (NumberFormatException exception) {
            return "BUT, IT IS INVALID.";
        }
    }

    /**
     * Parses a one-based task index and deletes the selected task.
     *
     * @param input complete delete command entered by the user
     */
    private String handleDelete(String input) {
        String[] commandParts = input.trim().split("\\s+", 2);
        if (commandParts.length < 2) {
            return "BUT, THE OBJECT IS NOT SPECIFIED.";
        }
        try {
            int itemIndex = Integer.parseInt(commandParts[1]) - 1;
            return taskList.delete(itemIndex);
        } catch (NumberFormatException exception) {
            return "BUT, IT IS INVALID.";
        }
    }

    /**
     * Parses and adds a deadline, falling back to a todo when its date is invalid.
     *
     * @param input complete deadline command entered by the user
     */
    private String addDeadline(String input) {
        String[] parts = input.substring(9).split("(?i) /by ", 2);
        if (parts.length == 2) {
            try {
                LocalDateTime by = parseDateTime(parts[1].trim());
                return taskList.add(new Deadline(parts[0].trim(), by));
            } catch (DateTimeParseException exception) {
                // fall through to error
            }
        }
        return mistakeWith(taskList.add(new Todo(input.substring(9).trim())));
    }

    /**
     * Parses and adds an event, falling back to a todo when its range is invalid.
     *
     * @param input complete event command entered by the user
     */
    private String addEvent(String input) {
        String[] parts = input.substring(6).split("(?i) /from |(?i) /to ", 3);
        if (parts.length == 3) {
            try {
                LocalDateTime from = parseDateTime(parts[1].trim());
                LocalDateTime to = parseDateTime(parts[2].trim());
                return taskList.add(new Event(parts[0].trim(), from, to));
            } catch (DateTimeParseException exception) {
                // fall through
            }
        }
        return mistakeWith(taskList.add(new Todo(input.substring(6).trim())));
    }

    /** Returns the dated-task error followed by its fallback result. */
    private String mistakeWith(String fallbackResponse) {
        return "YOU MUST BE\nMISTAKEN.\n\nHERE.\n" + fallbackResponse;
    }

    /**
     * Tries all supported date/time patterns in order and returns the first successful parse.
     *
     * @param dateTimeString date or date-time text to parse
     * @return parsed date-time, with midnight used when no time is supplied
     * @throws DateTimeParseException if none of the patterns match
     */
    private LocalDateTime parseDateTime(String dateTimeString) throws DateTimeParseException {
        for (DateTimeFormatter formatter : DATETIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(dateTimeString, formatter);
            } catch (DateTimeParseException exception) {
                // try next formatter
            }
        }
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(dateTimeString, formatter).atStartOfDay();
            } catch (DateTimeParseException exception) {
                // try next formatter
            }
        }
        throw new DateTimeParseException("Unable to parse: " + dateTimeString, dateTimeString, 0);
    }
}
