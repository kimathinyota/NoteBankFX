package Code;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");

        Parent root = FXMLLoader.load(getClass().getResource("View/main-window.fxml"));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();


        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, (int) (screenSize.getWidth()*0.65), (int) (0.9*screenSize.getHeight())));

        primaryStage.setMinWidth((int) (screenSize.getWidth()*0.65));
        primaryStage.setMinHeight( (int) (0.85*screenSize.getHeight())  );
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
