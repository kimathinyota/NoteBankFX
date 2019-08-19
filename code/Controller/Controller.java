package Code.Controller;

import Code.Model.Note;
import Code.View.ListCellCode;
import javafx.scene.control.ListView;

import java.util.HashMap;
import java.util.List;

/**
 * Designed to handle inter-controller communication
 */
public class Controller {

    MainWindowController controller;

    ViewNotesController viewNotesController;

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


    public void displayNotes(List<Note>notes, ViewMode mode){
        this.viewNotesController.displayNotes(notes, mode);
    }

    public void displayAndSelectNotes(List<Note>notes, ListView<Note> listForAddingSelectedNotes){
        this.viewNotesController.displayAndSelectNotes(notes,listForAddingSelectedNotes);
    }

    public void displayAndSelectNotes(List<Note>notes){
        this.viewNotesController.displayAndSelectNotes(notes);
    }


    public List<Note> finish(){
        return viewNotesController.finish();
    }


    public void setViewNotesController(ViewNotesController viewNotesController){
        this.viewNotesController = viewNotesController;
    }

    public void setMainWindowController(MainWindowController controller){
        this.controller = controller;
    }

    private final static Controller instance = new Controller();


    public static Controller getInstance(){
        return instance;
    }




    public static ListCellCode emptyListCellCode(){
        return new ListCellCode() {
            @Override
            public void onAddClick() {

            }

            @Override
            public void onRemoveClick() {

            }

            @Override
            public void onOptionChange() {

            }
        };
    }

}
