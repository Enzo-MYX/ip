package surveyprogram.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import surveyprogram.DeviceG;

/**
 * A GUI for Duke using FXML.
 */
public class Main extends Application {

    private final DeviceG deviceG = new DeviceG();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setDevice(deviceG);  // inject the Device instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
