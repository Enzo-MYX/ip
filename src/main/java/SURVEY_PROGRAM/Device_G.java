/**
 * Starts the application and owns its presentation-only introduction.
 */
package SURVEY_PROGRAM;

import SURVEY_PROGRAM.INTERFACE.Secret;
import SURVEY_PROGRAM.OBJECT.Obj_List;
import SURVEY_PROGRAM.PROCESSOR.CommandLoop;
import SURVEY_PROGRAM.PROCESSOR.TaskCommandProcessor;

public class Device_G {
    private static final String DIVIDER = "____________________________________________________________________________________";

    /**
     * Displays the introduction before handing control to the command interface.
     *
     * @param args unused command-line arguments
     * @throws InterruptedException if the introductory pauses are interrupted
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
        Obj_List taskList = new Obj_List(100);
        taskList.load(); // restore saved tasks, if any
        new CommandLoop(System.in, new TaskCommandProcessor(taskList, DIVIDER)).run();
    }
}
