package Code.Controller.home;

import Code.Controller.Controller;
import Code.Controller.IntegerValue;
import Code.Controller.study_session.ScheduleSessions;
import Code.Controller.study_session.StudySesionListCell;
import Code.Controller.study_session.StudySet;
import Code.Model.Model;
import Code.Model.Quizzes;
import Code.Model.StudySession;
import Code.Model.Subject;
import Code.View.View;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.Duration;


public class HomeFeaturedController {

    @FXML protected ListView<StudySet> studySessions;

    Controller controller;
    Model model;

    IntegerValue sessionsIndex = new IntegerValue(-1);

    public void initialize(){

        this.controller = Controller.getInstance();
        this.model = Model.getInstance();

        ScheduleSessions scheduleSessions = new ScheduleSessions(studySessions);
        this.controller.setScheduleSessions(scheduleSessions);
        this.model.addRefreshSubjectsController(scheduleSessions);
        this.model.addRefreshIdeasController(scheduleSessions);

        studySessions.setOrientation(Orientation.HORIZONTAL);
        studySessions.setCellFactory(new Callback<ListView<StudySet>, ListCell<StudySet>>() {
            @Override
            public ListCell<StudySet> call(ListView<StudySet> param) {
                StudySesionListCell cell =  new StudySesionListCell(scheduleSessions);
                cell.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if(menu!=null)
                            menu.hide();

                        if(event.getButton() == MouseButton.SECONDARY){
                            cell.getStylesheets().add("/Code/View/css/context-menu.css");
                            menu = new StudySessionRightClickMenu(cell.getItem().getSession());
                            menu.show(cell,event.getScreenX(),event.getScreenY());
                        }
                    }
                });
                return cell;
            }
        });

        Timeline timer = new Timeline(new KeyFrame(Duration.seconds(4*60), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                scheduleSessions.initialise(studySessions);
            }
        }));

        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();

        View.setUpListForArrowManipulation(studySessions,sessionsIndex);


    }


    @FXML protected void handleLeftClick(ActionEvent e){
        View.handleLeftClick(studySessions,sessionsIndex);
    }

    @FXML protected void handleRightClick(ActionEvent e){
        View.handleRightClick(studySessions,sessionsIndex);
    }


    ContextMenu menu;

}



class StudySessionRightClickMenu extends ContextMenu {

    public StudySessionRightClickMenu(StudySession session) {

        this.getStyleClass().add("amenu");
        MenuItem remove = new MenuItem("Remove");
        remove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Model model = Model.getInstance();
                Controller.getInstance().getSessionsScheduler().removeSession(session);
            }
        });

        MenuItem restart = new MenuItem("Restart");
        restart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Model model = Model.getInstance();
                Controller.getInstance().restartStudySession(session);
            }
        });

        this.getItems().add(restart);
        this.getItems().add(remove);
    }
}