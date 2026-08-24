package survey_program.processor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import survey_program.object.Deadline;
import survey_program.object.Event;
import survey_program.object.TaskList;
import survey_program.object.Todo;

/**
 * Interprets supported task commands and preserves the application's console responses.
 */
public class TaskCommandProcessor {
    private final TaskList taskList;
    private final String divider;

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

    /**
     * Creates a processor that applies commands to a task list.
     *
     * @param taskList task list to query and modify
     * @param divider line printed between console responses
     */
    public TaskCommandProcessor(TaskList taskList, String divider) {
        this.taskList = taskList;
        this.divider = divider;
    }

    /**
     * Processes one command.
     *
     * @param input the exact line entered by the user
     * @return {@code false} only when the application should exit
     */
    public boolean process(String input) {
        String lowerCaseInput = input.toLowerCase();
        if (lowerCaseInput.trim().equals("bye")) {
            return false;
        }
        if (lowerCaseInput.trim().equals("list")) {
            printList();
        } else if (lowerCaseInput.startsWith("date ")) {
            handleDate(input);
        } else if (lowerCaseInput.startsWith("mark ") || lowerCaseInput.startsWith("unmark ")) {
            handleMark(input);
        } else if (lowerCaseInput.startsWith("todo ")) {
            System.out.println(divider);
            taskList.add(new Todo(input.substring(5).trim()));
        } else if (lowerCaseInput.startsWith("deadline ")) {
            addDeadline(input);
        } else if (lowerCaseInput.startsWith("event ")) {
            addEvent(input);
        } else if (lowerCaseInput.startsWith("delete ")) {
            handleDelete(input);
        } else {
            System.out.println(divider);
            System.out.println("WELL, THAT IS NO LONGER A COMMAND.");
        }
        System.out.println(divider);
        return true;
    }

    /**
     * Parses a date command and prints tasks occurring on that date.
     *
     * @param input complete date command entered by the user
     */
    private void handleDate(String input) {
        System.out.println(divider);
        String[] parts = input.split(" ", 2);
        if (parts.length < 2) {
            System.out.println("BUT, THERE WAS NOT A DATE TO CHECK.");
            return;
        }
        try {
            // parseDateTime returns LocalDateTime (time defaults to 00:00 if absent)
            LocalDateTime dateTime = parseDateTime(parts[1].trim());
            LocalDate date = dateTime.toLocalDate();
            taskList.listByDate(date);
        } catch (DateTimeParseException exception) {
            System.out.println("BUT, THE DATE IS INVALID.");
        }
    }

    /** Prints the heading and current task list. */
    private void printList() {
        System.out.println(divider);
        System.out.println("VERY WELL. HERE IS YOUR LIST:");
        taskList.read();
    }

    /**
     * Parses a one-based task index and marks or unmarks the selected task.
     *
     * @param input complete mark or unmark command entered by the user
     */
    private void handleMark(String input) {
        String[] commandParts = input.trim().split("\\s+", 2);
        if (commandParts.length < 2) {
            System.out.println(divider);
            System.out.println("BUT, THE OBJECT IS NOT SPECIFIED.");
            return;
        }
        try {
            int itemIndex = Integer.parseInt(commandParts[1]) - 1;
            System.out.println(divider);
            taskList.mark(itemIndex, commandParts[0].equals("unmark"));
        } catch (NumberFormatException exception) {
            System.out.println(divider);
            System.out.println("BUT, IT IS INVALID.");
        }
    }

    /**
     * Parses a one-based task index and deletes the selected task.
     *
     * @param input complete delete command entered by the user
     */
    private void handleDelete(String input) {
        String[] commandParts = input.trim().split("\\s+", 2);
        if (commandParts.length < 2) {
            System.out.println(divider);
            System.out.println("BUT, THE OBJECT IS NOT SPECIFIED.");
            return;
        }
        try {
            int itemIndex = Integer.parseInt(commandParts[1]) - 1;
            System.out.println(divider);
            taskList.delete(itemIndex);
        } catch (NumberFormatException exception) {
            System.out.println(divider);
            System.out.println("BUT, IT IS INVALID.");
        }
    }

    /**
     * Parses and adds a deadline, falling back to a todo when its date is invalid.
     *
     * @param input complete deadline command entered by the user
     */
    private void addDeadline(String input) {
        System.out.println(divider);
        String[] parts = input.substring(9).split("(?i) /by ", 2);
        if (parts.length == 2) {
            try {
                LocalDateTime by = parseDateTime(parts[1].trim());
                taskList.add(new Deadline(parts[0].trim(), by));
                return;
            } catch (DateTimeParseException exception) {
                // fall through to error
            }
        }
        printMistake();
        taskList.add(new Todo(input.substring(9).trim()));
    }

    /**
     * Parses and adds an event, falling back to a todo when its range is invalid.
     *
     * @param input complete event command entered by the user
     */
    private void addEvent(String input) {
        System.out.println(divider);
        String[] parts = input.substring(6).split("(?i) /from |(?i) /to ", 3);
        if (parts.length == 3) {
            try {
                LocalDateTime from = parseDateTime(parts[1].trim());
                LocalDateTime to = parseDateTime(parts[2].trim());
                taskList.add(new Event(parts[0].trim(), from, to));
                return;
            } catch (DateTimeParseException exception) {
                // fall through
            }
        }
        printMistake();
        taskList.add(new Todo(input.substring(6).trim()));
    }

    /** Prints the message used when a dated task cannot be parsed. */
    private void printMistake() {
        System.out.println("YOU MUST BE\nMISTAKEN.\n\nHERE.");
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