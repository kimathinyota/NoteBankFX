package Code.Controller.study_session;

import Code.Controller.Controller;
import Code.Controller.Dialogs.ViewNotes.ViewNotesController;
import Code.Model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class StudySesionListCell extends ListCell<StudySet> {

    @FXML protected Button play;
    @FXML protected Label sessionName, days, hours, mins, seconds, setNumber, progressText;
    @FXML protected ProgressBar progress;


    public void clear(){
        this.days.setText( "" );
        this.hours.setText("");
        this.mins.setText("" );
        this.seconds.setText( "" );
    }



    private void setTimer(long time){
        clear();
        Date date = new Date(time);

       HashMap<Long,Integer> regions = ViewNotesController.getRegions(time, FXCollections.observableArrayList(Quizzes.d,Quizzes.h,Quizzes.m, Quizzes.s));


/*
        this.days.setText( ViewNotesController.getDay(date) + "" );
        this.hours.setText( ViewNotesController.getHour(date) + "");
        this.mins.setText( ViewNotesController.getMinute(date) + "" );
        this.seconds.setText( ViewNotesController.getSeconds(date) + "" );
        */


        this.days.setText( regions.get(Quizzes.d) + "" );
        this.hours.setText( regions.get(Quizzes.h) + "");
        this.mins.setText( regions.get(Quizzes.m) + "" );
        this.seconds.setText( regions.get(Quizzes.s) + "" );



    }

    ScheduleSessions scheduleSessions;

    public StudySesionListCell(ScheduleSessions scheduleSessions){
        loadFXML();
        this.scheduleSessions = scheduleSessions;
    }


    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Code/View/StudySession/StudySession.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Timeline timer;


    private void setup(StudySet set){
        setTimer(set.getTimeToKill());
        sessionName.setText(set.getName());
        if(set.isBonus()){
            setNumber.setText("BONUS");
        }else{
            setNumber.setText((set.getSetNumber()+1) + "/" + set.getSetTotal());
        }

        String progressText = "You have completed "+(set.getNumberOfIdeasStudied())+" of "+set.getTotalNumberOfIdeas()+" Ideas";
        this.progressText.setText(progressText);



        progress.setProgress( ( (double) set.getNumberOfIdeasStudied()/ (double) set.getTotalNumberOfIdeas()) );

        play.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                List<Idea> quiz = scheduleSessions.getQuizIdeas(set);
                Controller.getInstance().startSession(quiz,set);
            }
        });

        if(timer!=null){
            timer.stop();
        }

        timer = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if(set.getSession()==null || set.getSession().getActivation()==null){
                    timer.stop();
                    return;
                }

                long ttk = set.getTimeToKill();

                //System.out.println("LOOOOL: " + ViewNotesController.getOverview(ttk,"years","months","weeks","days"," hours"," mins"," secs","0","",""));



                if(ttk>Quizzes.s){
                    setTimer(set.getTimeToKill());
                }else{

                    //System.out.println("Generate it bro");
                    //we need to generate the next set
                    System.out.println("Done baby");
                    scheduleSessions.generateNextStudySet(set);

                }

            }
        }));

        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();

    }




    @Override
    protected void updateItem(StudySet item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
        else {
            setup(item);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }




}
