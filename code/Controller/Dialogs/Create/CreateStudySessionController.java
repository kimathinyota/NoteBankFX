package Code.Controller.Dialogs.Create;

import Code.Controller.Controller;
import Code.Controller.Dialogs.ViewNotes.ViewNotesController;
import Code.Model.*;
import Code.View.ObservableObject;
import Code.View.components.Date.DateRangePicker.SelectFromToDates;
import Code.View.View;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;



class SelectDates extends SelectFromToDates{

    @Override
    protected void handleLowerDateSelection() {
        super.handleLowerDateSelection();

    }

    @Override
    protected void handleHigherDateSelection() {
        super.handleHigherDateSelection();
    }

    @Override
    protected void handleLowerDateTimerChange() {
        super.handleLowerDateTimerChange();

        if(this.first.getDate().getTime() < System.currentTimeMillis()){
            this.first.setDate(new Date());
            this.first.setMinimumDate(new Date());
        }

    }


    @Override
    protected void handleHigherDateTimerChange() {
        super.handleHigherDateTimerChange();
    }

    public SelectDates(Date firstDate, Date secondDate, Date minimumDate, Date maximumDate) {
        super(firstDate, secondDate, minimumDate, maximumDate);
    }
}


public class CreateStudySessionController {

    @FXML protected TextField name;

    @FXML protected BorderPane pickIdeaPane, window, studyPeriod;

    @FXML protected Slider finalTime;

    @FXML protected ChoiceBox<StudyPlan> studyPlan;

    protected SpecialSelectLists<ObservableObject> ideasLists;

    protected SelectFromToDates selectFromToDates;


    Model model;
    Controller controller;

    public void initialize(){

        model = Model.getInstance();
        controller = Controller.getInstance();
        controller.setCreateStudySessionController(this);


        this.ideasLists = new SpecialSelectLists<ObservableObject>();
        pickIdeaPane.setCenter(ideasLists);

        finalTime.setSnapToTicks(true);

        finalTime.setMin(0);

        finalTime.setMax(23*Quizzes.h);
        finalTime.setMajorTickUnit(Quizzes.h);
        finalTime.setMinorTickCount(0);
        finalTime.setShowTickMarks(true);


        finalTime.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(!oldValue.equals(newValue)){
                System.out.println(new SimpleDateFormat("HH:mm").format(new Date(newValue.longValue())));
            }
        });


        finalTime.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                return new SimpleDateFormat("HH:mm").format(new Date(object.longValue() - Quizzes.h));
            }

            @Override
            public Double fromString(String string) {
                return null;
            }
        });




    }




    @FXML
    protected void handleCreateAction(ActionEvent e){


        if(!(this.name.getText().length()>2)){

            View.displayPopUpForTime("Invalid Name","Length must be greater than 2",2,this.name,175);
            return;
        }

        if(!(StringUtils.isAlphanumericSpace(this.name.getText()))){

            View.displayPopUpForTime("Invalid Name","Special characters aren't allowed",2,this.name,175);
            return;
        }

        if((model.studySessionNameExists(this.name.getText()))){

            View.displayPopUpForTime("Invalid Name","Name already exists",2,this.name,175);
            return;
        }




        StudyPlan plan = studyPlan.getValue();

        //StudySession session = new StudySession(name.getText(),plan.getID(), selectFromToDates.getFirstDate(),selectFromToDates.getSecondDate());


        Date finalDate = (new Date((long) finalTime.getValue()));

        HashMap<Long,Integer> map = ViewNotesController.getRegions(finalDate.getTime(), FXCollections.observableArrayList(Quizzes.h,Quizzes.m));




        List<Idea> ideas = new ArrayList<>();
        ideasLists.getSecondList().forEach( object -> { if(object instanceof Idea) ideas.add((Idea) object); else if(object instanceof Topic) ideas.addAll( ((Topic) object).getAllIdeas()); });



        StudySession session = new StudySession(name.getText(),plan.getID(),selectFromToDates.getFirstDate().getTime(),selectFromToDates.getSecondDate().getTime(),map.get(Quizzes.h),map.get(Quizzes.m),
                Study.fromIdeas(ideas),null);


        controller.getSessionsScheduler().addSession(session);

        this.window.setVisible(false);


    }




    @FXML protected void handleCancelAction(ActionEvent e){
        this.window.setVisible(false);
    }

    public void clear(){
        finalTime.setValue(21*Quizzes.h);

        this.ideasLists.getSecondListView().getItems().clear();

        this.name.setText("");


    }

    private void show(){
        clear();
        this.window.setVisible(true);
        this.ideasLists.getSecondListView().getItems().clear();
        List<ObservableObject> list = new ArrayList<>();
        list.addAll(model.getAllIdeas());
        list.addAll(model.getAllTopics());

        List<Subject> subjects = model.getAllSubjects();
        for(Subject s: subjects){
            if(!s.getName().equals("All")){
                Topic topic = model.filterTopicBySubject(s);

                Topic t = new Topic(s.getName());
                for(Topic p: topic.getSubTopics()){
                    t.add(p);
                }
                for(Idea i: topic.getIdeas()){
                    t.add(i);
                }

                list.add(t);
            }
        }

        this.ideasLists.setFirstList(list);

        this.studyPlan.getItems().clear();
        this.studyPlan.getItems().addAll(model.getStudyPlans());
        this.studyPlan.getSelectionModel().selectFirst();

        if(selectFromToDates!=null){
            selectFromToDates.close();
        }

        selectFromToDates = new SelectDates(
                new Date(),
                new Date(System.currentTimeMillis() + Quizzes.mth),
                new Date(System.currentTimeMillis() - Quizzes.m),
                new Date(System.currentTimeMillis() + 8*Quizzes.mth)
        );

        studyPeriod.setCenter(selectFromToDates);


    }

    public void create(){
        show();
    }



    public void create(StudyPlan plan){
        show();
        this.studyPlan.getSelectionModel().select(plan);
    }



}
