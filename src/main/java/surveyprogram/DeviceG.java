package surveyprogram;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import surveyprogram.object.TaskList;
import surveyprogram.processor.CommandLoop;
import surveyprogram.processor.TaskCommandProcessor;
import surveyprogram.ui.Secret;

/**
 * Starts the application and owns its presentation-only introduction.
 */
public class DeviceG {
    private static final String DIVIDER =
            "____________________________________________________________________________________";
    private final TaskCommandProcessor commandProcessor;

    /**
     * Creates the application backend and loads previously saved tasks.
     */
    public DeviceG() {
        TaskList taskList = new TaskList(100);
        taskList.load();
        commandProcessor = new TaskCommandProcessor(taskList, DIVIDER);
    }

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

    /**
     * Returns the introductory dialogue for the GUI.
     *
     * @return introductory dialogue
     */
    public String getOpeningDialogue() {
        return Secret.getOpeningDialogue();
    }

    /**
     * Processes one GUI command and returns all output it produces.
     *
     * @param input command entered by the user
     * @return response produced by the command
     */
    public String getResponse(String input) {
        ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        boolean shouldContinue;
        synchronized (DeviceG.class) {
            try (PrintStream capturedOutput =
                         new PrintStream(outputBuffer, true, StandardCharsets.UTF_8)) {
                System.setOut(capturedOutput);
                shouldContinue = commandProcessor.process(input);
            } finally {
                System.setOut(originalOutput);
            }
        }

        String response = outputBuffer.toString(StandardCharsets.UTF_8).strip();

        if (!shouldContinue) {
            return Secret.getClosingDialogue();
        }

        return response;
    }
}
