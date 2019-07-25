package Code.Controller;

import javafx.fxml.FXML;

import javafx.event.ActionEvent;

public class MenuController {
    @FXML protected void handleHomeClick(ActionEvent e){
        System.out.println("All bold people are too sexy");
    }

    @FXML void handleIdeasClick(ActionEvent e){
        System.out.println("All bold people are not sexy");
    }

    @FXML void handleStudyClick(ActionEvent e){
        System.out.println("All bold people are sexy");
    }
}
