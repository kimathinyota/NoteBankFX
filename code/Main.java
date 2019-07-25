package code;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("main-window.fxml"));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();


        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, (int) (screenSize.getWidth()*0.6), (int) (0.9*screenSize.getHeight())));
        primaryStage.show();



    }


    public static void main(String[] args) {
        launch(args);
    }
}
