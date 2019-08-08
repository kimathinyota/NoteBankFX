package Code.Controller;

import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class Controller {

    MainWindowController controller;

    public void switchToPage(Page page){
        controller.setPage(page);
    }


    public void setMainWindowController(MainWindowController controller){
        this.controller = controller;
    }

    private final static Controller instance = new Controller();


    public static Controller getInstance(){
        return instance;
    }

}
