package MainApp;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import logic.Branch;

import java.awt.*;
import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("MainApp.fxml");
        fxmlLoader.setLocation(url);
        BorderPane root = fxmlLoader.load(url.openStream());
        MainAppController controller = fxmlLoader.getController();
        controller.SetPrimaryStage(primaryStage);
        primaryStage.setTitle("Magit");
        primaryStage.setScene(new Scene(root));//, 950, 600));
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
