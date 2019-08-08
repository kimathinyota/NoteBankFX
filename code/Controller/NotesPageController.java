package Code.Controller;

import Code.Model.Model;
import Code.Model.Note;
import Code.Model.Subject;
import Code.View.menus.NoteCellFactory;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

import java.util.List;

public class NotesPageController implements RefreshNotesController, RefreshSubjectsController {
    @FXML protected Button openFilter;
    @FXML protected Pane filter;

    @FXML protected ListView<Note> underusedNotes;

    @FXML protected ChoiceBox<String> sortBy;
    @FXML protected ChoiceBox<String> order;
    @FXML protected ChoiceBox<String> view;

    @FXML protected Label subjectLine;

    Model model;
    public void initialize(){

        System.out.println("Initialise notes page");
        underusedNotes.setCellFactory(new NoteCellFactory(underusedNotes));
        underusedNotes.setOrientation(Orientation.HORIZONTAL);

        filter.setVisible(false);
        sortBy.getItems().setAll("Date","Name");
        sortBy.getSelectionModel().select(0);
        order.getItems().setAll("Asc.","Desc.");
        order.getSelectionModel().select(0);
        view.getItems().setAll("Large","Med","Small");
        view.getSelectionModel().select(0);
        model = Model.getInstance();
        model.addRefreshSubjectsController(this);
        model.addRefreshNotesController(this);
        currentSubject = model.getCurrentSubject();

        refreshSubjects();



    }

    @FXML protected void handleOpenFilterAction(ActionEvent e){
        filter.setVisible(true);
    }

    @FXML protected void handleCloseFilterAction(ActionEvent e){
        filter.setVisible(false);
    }

    Subject currentSubject;

    @Override
    public void refreshNotes() {
        refreshSubjects();
    }


    int i=0;
    @Override
    public void refreshSubjects() {

        System.out.println("Refresh subjects: " + i );
        i++;
        this.currentSubject = model.getCurrentSubject();
        subjectLine.setText(this.currentSubject.getName());

        System.out.println(currentSubject.getNotePaths());

        System.out.println("Subject size: " + model.getNotes(currentSubject.getName()).size());

        underusedNotes.setItems(FXCollections.observableArrayList(model.getNotes(currentSubject.getName())));
    }


}
