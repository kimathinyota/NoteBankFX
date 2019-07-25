package Code.Controller;

import javafx.fxml.FXML;

import java.awt.event.ActionEvent;

public class Controller {
    @FXML void handleHomeClick(ActionEvent e){
        System.out.println("All bold people are too sexy");
    }

    @FXML void handleIdeasClick(ActionEvent e){
        System.out.println("All bold people are not sexy");
    }

    @FXML void handleStudyClick(ActionEvent e){
        System.out.println("All bold people are sexy");
    }
}
