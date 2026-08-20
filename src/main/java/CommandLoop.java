import java.io.InputStream;
import java.util.Scanner;

/**
 * Repeatedly reads console input and delegates each command for processing.
 */
public class CommandLoop {
    private final InputStream inputStream;
    private final TaskCommandProcessor commandProcessor;

    public CommandLoop(InputStream inputStream, TaskCommandProcessor commandProcessor) {
        this.inputStream = inputStream;
        this.commandProcessor = commandProcessor;
    }

    /** Runs until the user enters the exit command. */
    public void run() {
        try (Scanner scanner = new Scanner(inputStream)) {
            while (commandProcessor.process(scanner.nextLine())) {
                // The processor handles all user-facing output for a command.
            }
        }
    }
}
