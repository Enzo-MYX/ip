package surveyprogram.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import surveyprogram.processor.CommandResult;
import surveyprogram.processor.TaskCommandProcessor;
import surveyprogram.ui.Secret;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    private static final Duration CLOSING_DELAY = Duration.seconds(3);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private TaskCommandProcessor commandProcessor;

    private Image userImage = new Image(this.getClass().getResourceAsStream(
            "/images/Double Green Gaster from Deltarune.png"));
    private Image dukeImage = new Image(this.getClass().getResourceAsStream(
            "/images/(D)UI.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the command processor and displays the introductory dialogue.
     *
     * @param commandProcessor processor that handles text-field commands
     */
    public void setCommandProcessor(TaskCommandProcessor commandProcessor) {
        this.commandProcessor = commandProcessor;
        dialogContainer.getChildren().add(
                DialogBox.getDukeDialog(Secret.getOpeningDialogue(), dukeImage));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        CommandResult result = commandProcessor.process(input);
        String response = result.shouldContinue()
                ? result.response()
                : Secret.getClosingDialogue();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getDukeDialog(response, dukeImage)
        );
        userInput.clear();

        if (!result.shouldContinue()) {
            closeAfterFarewell();
        }
    }

    /** Disables further interaction and closes the application after the farewell delay. */
    private void closeAfterFarewell() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition closingPause = new PauseTransition(CLOSING_DELAY);
        closingPause.setOnFinished(event -> Platform.exit());
        closingPause.play();
    }
}
