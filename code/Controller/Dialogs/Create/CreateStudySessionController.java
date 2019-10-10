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
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;


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

    @FXML protected Button create;

    @FXML protected Label title;

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

        finalTime.setMin(0);

        finalTime.setMax(23*Quizzes.h);
        finalTime.setMajorTickUnit(Quizzes.h);
        finalTime.setShowTickMarks(true);


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


        if(!isEditing){
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

            if(ideasLists.getSecondList().isEmpty()){
                View.displayPopUpForTime("Empty Lists","You haven't chosen any ideas",2,this.ideasLists,175);
                return;
            }
        }




        StudyPlan plan = studyPlan.getValue();

        //StudySession session = new StudySession(name.getText(),plan.getID(), selectFromToDates.getFirstDate(),selectFromToDates.getSecondDate());


        Date finalDate = (new Date((long) finalTime.getValue()));

        HashMap<Long,Integer> map = ViewNotesController.getRegions(finalDate.getTime(), FXCollections.observableArrayList(Quizzes.h,Quizzes.m));




        List<Idea> ideas = new ArrayList<>();
        List<String> topics = new ArrayList<>();
        List<String> subjects = new ArrayList<>();



        ideasLists.getSecondList().forEach( object -> { if(object instanceof Idea) ideas.add((Idea) object);
                                                        else if(object instanceof Topic) topics.add( ((Topic) object).getID());
                                                        else if(object instanceof Subject){
                                                            subjects.add(((Subject) object).getName());
                                                        }
        });

        this.window.setVisible(false);


        if(isEditing){
            model.modify(session,topics,subjects,Study.fromIdeas(ideas),selectFromToDates.getFirstDate(),selectFromToDates.getSecondDate(),plan,map.get(Quizzes.h),map.get(Quizzes.m));
            controller.getSessionsScheduler().removeTemporarilySession(session);
            model.refreshIdeas();

            return;
        }



        StudySession session = new StudySession(name.getText(),plan.getID(),selectFromToDates.getFirstDate().getTime(),selectFromToDates.getSecondDate().getTime(),map.get(Quizzes.h),map.get(Quizzes.m),
                Study.fromIdeas(ideas),topics,subjects,null);


        controller.getSessionsScheduler().addSession(session);


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
        this.name.setDisable(false);
        this.window.setVisible(true);
        this.ideasLists.getSecondListView().getItems().clear()
        ;
        List<ObservableObject> list = new ArrayList<>();
        list.addAll(model.getAllIdeas());
        list.addAll(model.getAllTopics());
        list.addAll(model.getAllSubjects());

        this.ideasLists.setFirstList(list);

        this.studyPlan.getItems().clear();
        this.studyPlan.getItems().addAll(model.getStudyPlans());
        this.studyPlan.getSelectionModel().selectFirst();

        setDates();
    }

    private void show(StudySession session){
        clear();
        this.window.setVisible(true);
        this.ideasLists.getSecondListView().getItems().clear()
        ;
        List<ObservableObject> list = new ArrayList<>();
        list.addAll(model.getAllIdeas());
        list.addAll(model.getAllTopics());
        list.addAll(model.getAllSubjects());

        this.ideasLists.setFirstList(list);

        List<ObservableObject> objects = new ArrayList<>();
        objects.addAll(Study.toIdeas(session.getIdeas()));

        List<Topic> topics = new ArrayList<>();
        session.getTopics().forEach( t -> {topics.add(model.getTopic(t));});
        topics.removeAll(Collections.singleton(null));

        List<Subject> subjects = new ArrayList<>();
        session.getSubjects().forEach( s -> {subjects.add(model.getSubject(s));});
        subjects.removeAll(Collections.singleton(null));

        objects.addAll(topics);
        objects.addAll(subjects);

        ideasLists.setSecondList(objects);

        this.studyPlan.getItems().clear();
        this.studyPlan.getItems().addAll(model.getStudyPlans());
        this.studyPlan.getSelectionModel().selectFirst();
        this.studyPlan.getSelectionModel().select(model.findPlan(session.getStudyPlan()));
        setDates(new Date(session.getEndDate()));

        this.name.setText(session.getName());
        this.name.setDisable(true);


        finalTime.setValue( Quizzes.h*session.getLastHour() + Quizzes.m*session.getLastMinute()    );

    }




    private void setDates(Date endDate){
        if(selectFromToDates!=null){
            selectFromToDates.close();
        }

        selectFromToDates = new SelectDates(
                new Date(),
                endDate,
                new Date(System.currentTimeMillis() - Quizzes.m),
                new Date(System.currentTimeMillis() + 8*Quizzes.mth)
        );

        studyPeriod.setCenter(selectFromToDates);
    }

    private void setDates(){
        setDates(new Date(System.currentTimeMillis() + Quizzes.mth));
    }

    public void create(){
        show();
    }



    public void create(StudyPlan plan){
        show();
        isEditing = false;
        title.setText("Study Session");
        this.studyPlan.getSelectionModel().select(plan);
    }


    StudySession session;
    boolean isEditing;


    public void edit(StudySession session){
        show(session);
        this.session = session;
        title.setText("Restart Study Session");
        isEditing = true;
    }



}
