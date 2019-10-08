package Code.View.components.Date.PickDate;

import Code.Controller.Dialogs.ViewNotes.ViewNotesController;
import Code.View.View;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SelectDate extends VBox{

    @FXML protected Button date;
    @FXML protected Label dateLabel;

    SimpleDateFormat formatter;

    private DateAndTimePicker picker;

    private PopOver popOver;


    Date first;


    public void setMinimumDate(Date minimumDate) {
        this.minimumDate = minimumDate;
    }

    public void setMaximumDate(Date maximumDate) {
        this.maximumDate = maximumDate;
    }



    Date minimumDate;
    Date maximumDate;


    public Date getDate(){
        return first;
    }





    public boolean setDate(Date date){
        if(!(minimumDate!=null && date.getTime() >= minimumDate.getTime() && (maximumDate!=null && date.getTime() <= maximumDate.getTime()))){
            return false;
        }
        selectDate(date);
        return true;
    }



    public SelectDate(Date start){

        formatter = new SimpleDateFormat("dd MMM yyyy (HH:mm)");
        this.first = start;
        picker = new DateAndTimePicker(start);
        popOver = new PopOver(picker);

        loadFXML();

        selectDate(start);

    }

    protected void selectDate(Date d){
        dateLabel.setText(formatter.format(d));
        long diff = new Date().getTime() - d.getTime();
        date.setText(ViewNotesController.getOverview(diff," year(s)"," month(s)"," week(s)"," day(s)"," hour(s)"," minute(s)", " second(s)","NOW"," later"," ago")  );
        picker.setDate(d);
        first = d;
    }



    public void handleDateSelection(ActionEvent e){
        Date d = picker.getDate();

        SimpleDateFormat simple = new SimpleDateFormat("HH:mm dd/MMM/yyyy");


        if( (minimumDate!=null && d.getTime() < minimumDate.getTime()) ||
                (maximumDate!=null && d.getTime() > maximumDate.getTime())){

            View.displayPopUpForTime("Information","Date chosen isn't between " + simple.format(minimumDate) + " and" + simple.format(maximumDate),2,picker.getSelect(),150);
            return;
        }

        first = d;
        selectDate(first);



    }

    protected void handleTimerChange(ActionEvent e){
        long diff = new Date().getTime() - first.getTime();
        date.setText(ViewNotesController.getOverview(diff," year(s)"," month(s)",
                " week(s)"," day(s)"," hour(s)"," minute(s)",
                " second(s)","NOW"," later"," ago")  );

    }


    int tickCount = 5 ;

    public void setTickCount(int seconds){
        if(timeline!=null)
            this.timeline.stop();
        Timeline hourTimer = new Timeline(new KeyFrame(Duration.seconds(tickCount), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleTimerChange(event);
            }
        }));

        hourTimer.setCycleCount(Timeline.INDEFINITE);
        hourTimer.play();

        this.timeline = hourTimer;
        this.tickCount = seconds;

    }


    public void close(){
        timeline.stop();
    }

    Timeline timeline;

    public void initialize(){


        date.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               popOver.show(date);
            }
         });

        setUp();

    }


    public void setUp(){
        picker.getSelect().setOnAction(this::handleDateSelection);
        setTickCount(tickCount);
    }

    private void loadFXML() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Code/View/components/Date/PickDate/SelectDate.fxml"));

            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }







}
