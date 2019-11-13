package Code.Controller.study;

import Code.Controller.Controller;
import Code.Controller.Dialogs.ViewNotes.ViewNotesController;
import Code.Controller.RefreshInterfaces.RefreshDataController;
import Code.Controller.RefreshInterfaces.RefreshIdeasController;
import Code.Controller.RefreshInterfaces.RefreshSubjectsController;
import Code.Model.Idea;
import Code.Model.Model;
import Code.Model.Quizzes;
import Code.Model.Topic;
import Code.View.components.MindMap.MindMapCell;
import Code.View.components.Date.DateRangePicker.SelectFromToDates;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import org.controlsfx.control.PopOver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class StudyOverviewController implements RefreshDataController, RefreshSubjectsController, RefreshIdeasController {

    @FXML protected ChoiceBox<String> timeInterval;
    @FXML protected LineChart<Number,Number> studyChart;

    @FXML protected Label subjectLine;

    /*
    @FXML protected Button startDate;
    @FXML protected Label startDateLabel;
    @FXML protected Button endDate;
    @FXML protected Label endDateLabel, subjectLine;

   */

    @FXML protected NumberAxis yAxis;

    @FXML protected  NumberAxis xAxis;



    protected PopOver pickStartDate, pickEndDate;

    @FXML protected BorderPane graphPane, dates;
    @FXML protected ScrollPane graphScrollPane;
    @FXML protected Label totalTime;

    Date start, end;

    Model model;
    Controller controller;

    long s = (1000L);
    long m = (60L * s);
    long h = (60L * m);
    long d = (24L * h);
    long w = (7L * d);
    long mth = (30L * d);
    long y = (365L * d);
    long[] times = {h,d,w,2*w,mth,3*mth,y};

    HashMap<Long,String> timeToSuffix, yAxisTimeToSuffix;

    public void initialize(){

        timeToSuffix = new HashMap<>();
        timeToSuffix.put(s,"secs");
        timeToSuffix.put(m,"mins");
        timeToSuffix.put(h,"hr");
        timeToSuffix.put(d,"days");
        timeToSuffix.put(w,"weeks");
        timeToSuffix.put(mth,"mths");
        timeToSuffix.put(y,"yrs");

        yAxisTimeToSuffix = new HashMap<>();
        yAxisTimeToSuffix.put(s,"s");
        yAxisTimeToSuffix.put(m,"m");
        yAxisTimeToSuffix.put(h,"h");
        yAxisTimeToSuffix.put(d,"d");

        model = Model.getInstance();
        model.addRefreshDataController(this);
        model.addRefreshSubjectsController(this);
        model.addRefreshIdeasController(this);

        controller = Controller.getInstance();


        class SelectPastDate extends SelectFromToDates {

            @Override
            protected void handleLowerDateSelection() {
                super.handleLowerDateSelection();
                start = this.first.getDate();
                refreshData();


            }

            @Override
            protected void handleHigherDateSelection() {
                super.handleHigherDateSelection();
                end = this.second.getDate();
                refreshData();
            }

            @Override
            protected void handleLowerDateTimerChange() {
                super.handleLowerDateTimerChange();

            }


            @Override
            protected void handleHigherDateTimerChange() {
                super.handleHigherDateTimerChange();
                this.second.setMaximumDate(new Date(System.currentTimeMillis() + Quizzes.m));
            }

            public SelectPastDate(Date firstDate, Date secondDate, Date minimumDate, Date maximumDate) {
                super(firstDate, secondDate, minimumDate, maximumDate);
                start = this.first.getDate();
                end = this.second.getDate();
            }
        }

        this.dates.setCenter(
                new SelectPastDate(new Date(System.currentTimeMillis() - Quizzes.w),
                        new Date(),
                        new Date(System.currentTimeMillis() - Quizzes.y),
                        new Date(System.currentTimeMillis() + Quizzes.m) ));


        /*
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy (HH:mm)");

        second = new Date();
        first = new Date(second.getTime() - w);

        DateAndTimePicker start, end;
        start = new DateAndTimePicker(first);
        end = new DateAndTimePicker(second);

        startDateLabel.setText(formatter.format(first));
        endDateLabel.setText(formatter.format(second));

        startDate.setText(ViewNotesController.getOverview(w," year(s)"," month(s)"," week(s)"," day(s)"," hour(s)"," minute(s)", " second(s)","NOW"," from now"," ago") );

        endDate.setText(ViewNotesController.getOverview(0," year(s)"," month(s)"," week(s)"," day(s)"," hour(s)"," minute(s)", " second(s)","NOW"," from now"," ago") );



        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                start.getSelect().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        Date date = start.getDate();
                        if(date.getTime() > second.getTime()){
                            View.displayPopUpForTime("Information","Start date must be before final date",2,start,150);

                            return;
                        }

                        first = date;

                        startDateLabel.setText(formatter.format(date));

                        long diff = new Date().getTime() - date.getTime();

                        startDate.setText(ViewNotesController.getOverview(diff," year(s)"," month(s)"," week(s)"," day(s)"," hour(s)"," minute(s)", " second(s)","NOW"," from now"," ago")  );

                        refreshData();
                    }
                });
                end.getSelect().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Date date = end.getDate();
                        if(date.getTime() < first.getTime()){
                            View.displayPopUpForTime("Information","Start date must be before final date",2,end,100);
                            return;
                        }

                        second = date;
                        endDateLabel.setText(formatter.format(date));

                        long diff = new Date().getTime() - date.getTime();

                        endDate.setText(ViewNotesController.getOverview(diff," year(s)"," month(s)"," week(s)"," day(s)"," hour(s)"," minute(s)", " second(s)","NOW"," from now"," ago")  );

                        refreshData();

                    }
                });
            }
        });
        pickStartDate = new PopOver(start);
        pickStartDate.setStyle("-fx-background-color: transparent");
        pickEndDate = new PopOver(end);

        startDate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pickEndDate.hide();
                pickStartDate.show(startDate);
            }
        });

        endDate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pickStartDate.hide();
                pickEndDate.show(endDate);
            }
        });


        */


        timeInterval.getItems().add("HOUR");
        timeInterval.getItems().add("DAY");
        timeInterval.getItems().add("WEEK");
        timeInterval.getItems().add("FORTNIGHT");
        timeInterval.getItems().add("MONTH");
        timeInterval.getItems().add("QUARTER");


        timeInterval.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null && !newValue.equals(oldValue)){
                Label label = (Label) timeInterval.lookup(".label");
                if(label!=null)
                    timeInterval.setPrefWidth(MindMapCell.getTextWidth(label.getText(),label.getFont())+40);
                refreshChart();
            }
        });

        timeInterval.getSelectionModel().select(1);
        timeInterval.setPrefWidth(MindMapCell.getTextWidth(timeInterval.getSelectionModel().getSelectedItem(), Font.font("Inter",18))+40);


/*
        Timeline hourTimer = new Timeline(new KeyFrame(Duration.seconds(60), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                long diff = new Date().getTime() - first.getTime();
                long diff2 = new Date().getTime() - second.getTime();

                startDate.setText(ViewNotesController.getOverview(diff," year(s)"," month(s)"," week(s)"," day(s)"," hour(s)"," minute(s)", " second(s)","NOW"," from now"," ago")  );
                endDate.setText(ViewNotesController.getOverview(diff2," year(s)"," month(s)"," week(s)"," day(s)"," hour(s)"," minute(s)", " second(s)","NOW"," from now"," ago")  );

            }
        }));

        hourTimer.setCycleCount(Timeline.INDEFINITE);
        hourTimer.play();

*/



        graphScrollPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            double val = newValue.doubleValue();
            studyChart.setPrefHeight(val);
        });




    }

    public void setupGraph(Date first, Date second, long time){

        //Topic root = (model.isRootSubject() ? null : model.filterTopicByCurrentSubject());

        Topic root = model.getRoot();

        if( ((time + second.getTime()-first.getTime())/time) > (150) ){
            Label label = new Label("TOO MANY ENTRIES TO LOAD GRAPH");
            label.setFont(Font.font("Inter", 25));
            label.setTextFill(Color.WHITE);
            graphPane.setCenter(label);
            return;
        }

        graphPane.setCenter(graphScrollPane);

        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(first.getTime());
        xAxis.setUpperBound(second.getTime());
        xAxis.setTickUnit(time);

        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                Date date =  new Date(object.longValue());
                DateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                return dateFormat.format(date);
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });

        yAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {

                String overview = ViewNotesController.getDateOverviewName(object.longValue(),yAxisTimeToSuffix,2,".");
                overview = (overview.isEmpty() ? "0" : overview);

                return overview;

            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });

        studyChart.getData().clear();

        XYChart.Series data = new XYChart.Series();


        for(long start = first.getTime(); start<second.getTime()+time; start+=time){
            long tot = 0;
            if(model.isRootSubject()){
                tot = model.getStudyDuration(first.getTime(),start);
            }else{
                List<Idea> ideas = new ArrayList<>();

                if(model.isRootSubject()){
                    ideas.addAll(root.getAllIdeas());
                }else{
                    for(Idea i: root.getAllIdeas()){
                        if(model.isRootSubject() || i.isIdeaApartOfSubject(model.getCurrentSubject())){
                            ideas.add(i);
                        }
                    }
                }


                //tot = model.getStudyDuration(first.getTime(),start,root.getAllIdeas());
                tot = model.getStudyDuration(first.getTime(),start,ideas);
            }

            data.getData().add(new XYChart.Data( start, tot));
        }


        studyChart.setPrefWidth(data.getData().size()*230);


        studyChart.getData().add(data);

        studyChart.setLegendVisible(false);



    }

    public void refreshChart(){
        setupGraph(start,end,times[timeInterval.getSelectionModel().getSelectedIndex()]);
    }

    @Override
    public void refreshData() {

        refreshChart();

        long totalTime;

        if(model.isRootSubject()){
            totalTime = model.getStudyDuration(start.getTime(),end.getTime());
        }else{
            List<Idea> ideas = new ArrayList<>();
            for(Idea i: model.getRoot().getAllIdeas()){
                if(model.isRootSubject() || i.isIdeaApartOfSubject(model.getCurrentSubject())){
                    ideas.add(i);
                }
            }
            totalTime = model.getStudyDuration(start.getTime(),end.getTime(),ideas);
        }

        String overview = ViewNotesController.getDateOverviewName(totalTime,timeToSuffix,2,". ");
        overview = (overview.isEmpty() ? "0 mins." : overview);

        this.totalTime.setText(overview);

    }

    @Override
    public void refreshIdeas() {
        refreshData();
    }

    @Override
    public void refreshSubjects() {
        subjectLine.setText(model.getCurrentSubject().getName());
        refreshData();
    }
}
