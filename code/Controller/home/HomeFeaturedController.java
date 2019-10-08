package Code.Controller.home;

import Code.Controller.Controller;
import Code.Controller.study_session.ScheduleSessions;
import Code.Controller.study_session.StudySesionListCell;
import Code.Controller.study_session.StudySet;
import Code.Model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;




public class HomeFeaturedController {

    @FXML protected ListView<StudySet> studySessions;

    Controller controller;
    Model model;

    public void initialize(){

        System.out.println("Let's go champ");
        this.controller = Controller.getInstance();
        this.model = Model.getInstance();

        ScheduleSessions scheduleSessions = new ScheduleSessions(studySessions);
        this.controller.setScheduleSessions(scheduleSessions);

        studySessions.setOrientation(Orientation.HORIZONTAL);
        studySessions.setCellFactory(new Callback<ListView<StudySet>, ListCell<StudySet>>() {
            @Override
            public ListCell<StudySet> call(ListView<StudySet> param) {
                return new StudySesionListCell(scheduleSessions);
            }
        });

    }


    @FXML protected void handleLeftClick(ActionEvent e){

    }

    @FXML protected void handleRightClick(ActionEvent e){

    }









}
