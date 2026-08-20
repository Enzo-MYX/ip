/**
 * Interprets supported task commands and preserves the application's console responses.
 */
public class TaskCommandProcessor {
    private final Obj_List taskList;
    private final String divider;

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

    private void printList() {
        System.out.println(divider);
        System.out.println("VERY WELL. HERE IS YOUR LIST:");
        taskList.read();
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
            taskList.add(new Deadline(parts[0].trim(), parts[1].trim()));
            return;
        }
        printMistake();
        taskList.add(new Todo(input.substring(9).trim()));
    }

    private void addEvent(String input) {
        System.out.println(divider);
        String[] parts = input.substring(6).split("(?i) /from |(?i) /to ", 3);
        if (parts.length == 3) {
            taskList.add(new Event(parts[0].trim(), parts[1].trim(), parts[2].trim()));
            return;
        }
        printMistake();
        taskList.add(new Todo(input.substring(6).trim()));
    }

    private void printMistake() {
        System.out.println("YOU MUST BE\nMISTAKEN.\n\nHERE.");
    }
}
