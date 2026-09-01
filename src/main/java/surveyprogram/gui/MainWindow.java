package surveyprogram.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import surveyprogram.DeviceG;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private DeviceG deviceG;

    private Image userImage = new Image(this.getClass().getResourceAsStream(
            "/images/Double Green Gaster from Deltarune.png"));
    private Image dukeImage = new Image(this.getClass().getResourceAsStream(
            "/images/(D)UI.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the backend and displays its introductory dialogue.
     *
     * @param deviceG application backend
     */
    public void setDevice(DeviceG deviceG) {
        this.deviceG = deviceG;
        dialogContainer.getChildren().add(
                DialogBox.getDukeDialog(deviceG.getOpeningDialogue(), dukeImage));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = deviceG.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getDukeDialog(response, dukeImage)
        );
        userInput.clear();
    }
}
