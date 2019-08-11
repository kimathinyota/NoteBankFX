package Code.Controller;

/**
 * Designed to handle inter-controller communication
 */
public class Controller {

    MainWindowController controller;

    public void switchToPage(Page page){
        controller.setPage(page);
    }

    public void disable(AppFeatures features, boolean disable){
        switch (features){
            case SubjectList:
                controller.disableSubjectList(disable);
                break;
        }
    }

    public void setMainWindowController(MainWindowController controller){
        this.controller = controller;
    }

    private final static Controller instance = new Controller();


    public static Controller getInstance(){
        return instance;
    }

}
