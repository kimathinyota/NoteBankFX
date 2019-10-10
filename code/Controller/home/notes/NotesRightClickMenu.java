package Code.Controller.home.notes;

import Code.Controller.Controller;
import Code.Controller.Dialogs.ViewNotes.ViewMode;
import Code.Model.*;
import Code.View.View;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;


public class NotesRightClickMenu extends ContextMenu {

    public NotesRightClickMenu(Note note,ListCell cell) {

        this.getStyleClass().add("amenu");
        MenuItem view = new MenuItem("View");
        view.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Controller.getInstance().displayNotes(FXCollections.singletonObservableList(note), ViewMode.ViewFull);
            }
        });
        MenuItem edit = new MenuItem("Edit");
        edit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!(note instanceof Text) ){
                    View.displayPopUpForTime("NON-TEXT NOTE", "You can only modify Text notes",2,cell,250);
                    return;
                }
                Controller.getInstance().editNote(note);
            }
        });

        Model model = Model.getInstance();


        MenuItem remove = new MenuItem("Remove");
        remove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(model.isRootSubject()){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to permenantly remove " + note + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                    alert.showAndWait();
                    if (alert.getResult() == ButtonType.YES) {
                        model.remove(note);
                    }
                    return;
                }
                model.remove(note);
            }
        });


        Menu subjects = new Menu("Subjects");

        for(Subject s: model.getAllSubjects()){
            if(!s.getName().equals("All")){
                CheckMenuItem subject = new CheckMenuItem(s.getName());
                subject.setSelected(s.memberOf(note));
                subject.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        if(subject.isSelected()){
                            //need to add note to subject
                            if(!s.memberOf(note)){
                                model.addToSubject(s.getName(),note);
                            }
                        }else{
                            //remove note from subject
                            model.removeFromSubject(s.getName(),note);

                        }

                    }
                });

                subjects.getItems().add(subject);

            }
        }



        this.getItems().add(view);
        this.getItems().add(edit);
        this.getItems().add(remove);
        this.getItems().add(subjects);
    }
}