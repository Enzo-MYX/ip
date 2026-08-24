package SURVEY_PROGRAM.PROCESSOR;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import SURVEY_PROGRAM.OBJECT.*;

/**
 * Interprets supported task commands and preserves the application's console responses.
 */
public class TaskCommandProcessor {
    private final Obj_List taskList;
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

    public TaskCommandProcessor(Obj_List taskList, String divider) {
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
        } else if (lowerCaseInput.trim().equals("find") || lowerCaseInput.startsWith("find ")) {
            handleFind(input);
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

    private void handleDate(String input) {
        System.out.println(divider);
        String[] parts = input.split(" ", 2);
        if (parts.length < 2) {
            System.out.println("BUT, THERE WAS NOT A DATE TO CHECK.");
            return;
        }
        try {
            // parseDateTime returns LocalDateTime (time defaults to 00:00 if absent)
            LocalDateTime dt = parseDateTime(parts[1].trim());
            LocalDate date = dt.toLocalDate();
            taskList.listByDate(date);
        } catch (DateTimeParseException e) {
            System.out.println("BUT, THE DATE IS INVALID.");
        }
    }

    private void printList() {
        System.out.println(divider);
        System.out.println("VERY WELL. HERE IS YOUR LIST:");
        taskList.read();
    }

    /** Finds tasks whose descriptions contain the supplied keyword. */
    private void handleFind(String input) {
        System.out.println(divider);
        String keyword = input.substring(4).trim();
        taskList.find(keyword);
    }

    /**
     * @return whether the caller should skip the command's trailing divider
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

    private void addDeadline(String input) {
        System.out.println(divider);
        String[] parts = input.substring(9).split("(?i) /by ", 2);
        if (parts.length == 2) {
            try {
                LocalDateTime by = parseDateTime(parts[1].trim());
                taskList.add(new Deadline(parts[0].trim(), by));
                return;
            } catch (DateTimeParseException e) {
                // fall through to error
            }
        }
        printMistake();
        taskList.add(new Todo(input.substring(9).trim()));
    }

    private void addEvent(String input) {
        System.out.println(divider);
        String[] parts = input.substring(6).split("(?i) /from |(?i) /to ", 3);
        if (parts.length == 3) {
            try {
                LocalDateTime from = parseDateTime(parts[1].trim());
                LocalDateTime to = parseDateTime(parts[2].trim());
                taskList.add(new Event(parts[0].trim(), from, to));
                return;
            } catch (DateTimeParseException e) {
                // fall through
            }
        }
        printMistake();
        taskList.add(new Todo(input.substring(6).trim()));
    }

    private void printMistake() {
        System.out.println("YOU MUST BE\nMISTAKEN.\n\nHERE.");
    }

    /**
     * Tries all supported date/time patterns in order and returns the first successful parse.
     * @throws DateTimeParseException if none of the patterns match
     */
    private LocalDateTime parseDateTime(String dateTimeStr) throws DateTimeParseException {
        for (DateTimeFormatter formatter : DATETIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(dateTimeStr, formatter);
            } catch (DateTimeParseException e) {
                // try next formatter
            }
        }
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(dateTimeStr, formatter).atStartOfDay();
            } catch (DateTimeParseException e) {
                // try next formatter
            }
        }
        throw new DateTimeParseException("Unable to parse: " + dateTimeStr, dateTimeStr, 0);
    }
}
