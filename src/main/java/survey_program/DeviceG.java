package survey_program;

import survey_program.ui.Secret;
import survey_program.object.TaskList;
import survey_program.processor.CommandLoop;
import survey_program.processor.TaskCommandProcessor;

/** Starts the application and owns its presentation-only introduction. */
public class DeviceG {
    private static final String DIVIDER = "____________________________________________________________________________________";

    /**
     * Displays the introduction before handing control to the command interface.
     *
     * @param args Unused command-line arguments.
     * @throws InterruptedException If the introductory pauses are interrupted.
     */
    public static void main(String[] args) throws InterruptedException {
        String banner = """
                ░▒▓███████▓▒░░▒▓████████▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓██████▓▒░░▒▓████████▓▒░▒▓██████▓▒░ \s
                ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░     ░▒▓█▓▒░░▒▓█▓▒░\s
                ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░       ░▒▓█▓▒▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░     ░▒▓█▓▒░       \s
                ░▒▓█▓▒░░▒▓█▓▒░▒▓██████▓▒░  ░▒▓█▓▒▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓██████▓▒░░▒▓█▓▒▒▓███▓▒░\s
                ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░        ░▒▓█▓▓█▓▒░ ░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░     ░▒▓█▓▒░░▒▓█▓▒░\s
                ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░        ░▒▓█▓▓█▓▒░ ░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░     ░▒▓█▓▒░░▒▓█▓▒░\s
                ░▒▓███████▓▒░░▒▓████████▓▒░  ░▒▓██▓▒░  ░▒▓█▓▒░░▒▓██████▓▒░░▒▓████████▓▒░▒▓██████▓▒░ \s
                """;
        System.out.println(DIVIDER);
        System.out.println(banner.stripTrailing());
        System.out.println(DIVIDER);
        Secret.run(DIVIDER);
    }

    /** Starts the functional console interface after the introduction. */
    public static void boot() {
        System.out.println(DIVIDER);
        TaskList taskList = new TaskList(100);
        taskList.load(); // restore saved tasks, if any
        new CommandLoop(System.in, new TaskCommandProcessor(taskList, DIVIDER)).run();
    }
}
