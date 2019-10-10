package Code.Controller.study;

import Code.Controller.Controller;
import Code.Controller.IntegerValue;
import Code.Controller.RefreshInterfaces.RefreshDataController;
import Code.Controller.RefreshInterfaces.RefreshIdeasController;
import Code.Controller.RefreshInterfaces.RefreshSubjectsController;
import Code.Controller.Dialogs.ViewNotes.ViewNotesController;
import Code.Model.*;
import Code.View.components.MindMap.MindMapCell;
import Code.View.components.TitleNode.TitleChoiceBox;
import Code.View.components.TitleNode.TitleNodeListFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.Pair;
import org.controlsfx.control.PopOver;
import Code.View.View;

import java.util.*;

class ReadinessCell extends TableCell<String,String>{

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if(!empty){

            setAlignment(Pos.CENTER);

            String space = "  ";
            if(item.toLowerCase().contains("ready")){
                setText(item + " ["+5+"]");
                setTextFill(Color.GREEN);
            }else if(item.toLowerCase().contains("almost")){
                setText(item + " ["+4+"]" );
                setTextFill(Color.rgb(196,245,2));
            }else if(item.toLowerCase().contains("fail")){
                setText(item + " ["+1+"]");
                setTextFill(Color.RED);
            }else if(item.toLowerCase().contains("pass")){
                setText(item + " ["+3+"]" );
                setTextFill(Color.YELLOW);
            }else if(item.toLowerCase().contains("mediocre")){
                setText(item + " ["+2+"]" );
                setTextFill(Color.ORANGE);
            }else{
                setText(item);
            }
            setFont(Font.font("Arial",14.5));
        }
    }
}

class TimeCell extends TableCell<String,String>{

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if(!empty){
            //String space = "  ";
            setAlignment(Pos.CENTER);
            //setText(space + item + extra);

            String time = "0";
            if(Long.valueOf(item)>0){
                time = ViewNotesController.getOverview(Long.valueOf(item)," years"," months", " weeks",
                        " days", " hours", " mins", " secs","0","","");
            }

            setText(time);

            setTextFill(Color.rgb(191,191,191));
            setFont(Font.font("Arial",14.5));

        }
    }
}



class NumberCell extends TableCell<String,String>{

    String extra;

    public NumberCell(String extra){
        this.extra = extra;
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if(!empty){
            //String space = "  ";
            setAlignment(Pos.CENTER);
            //setText(space + item + extra);
            setText(item + extra);
            setTextFill(Color.rgb(191,191,191));
            setFont(Font.font("Arial",14.5));

        }
    }
}

class PromptCell extends TableCell<String,String>{


    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if(!empty){

            setWrapText(true);
            setPrefHeight(MindMapCell.getHeight(item,Font.font("Arial",14.5),this.getMaxWidth()));
            String space = "  ";
            setAlignment(Pos.CENTER);
            setText(item);
            setTextFill(Color.rgb(168,208,141));
            setFont(Font.font("Arial",14.5));
        }
    }

}


public class IdeaFactsController implements RefreshIdeasController, RefreshSubjectsController, RefreshDataController {

    @FXML protected ListView<Map.Entry<Node, Node>> quizFacts;
    @FXML protected Label subjectLine;
    @FXML protected TableView ideaTable;
    Model model;
    Controller controller;

    List<String> timeRegions = FXCollections.observableArrayList("all","hour","day","week","fortnight","month",
            "half-year","year");


    PopOver lengthPopOver, frequencyPopOver;
    ChoiceBox<String> lengthChoiceBox, frequencyChoiceBox;



    private BorderPane getSelectTimePane(ChoiceBox<String> choiceBox){
        BorderPane pane = new BorderPane();
        Label title = new Label("  Change Time Region  ");
        title.setStyle("-fx-font-family: Inter-Semi-Bold; -fx-font-size: 18px; -fx-text-fill: black");

        title.setAlignment(Pos.CENTER);
        pane.setTop(title);

        choiceBox.getItems().add("All");
        choiceBox.getItems().add("Last Hour");
        choiceBox.getItems().add("Last Day");
        choiceBox.getItems().add("Last Week");
        choiceBox.getItems().add("Last Fortnight");
        choiceBox.getItems().add("Last Month");
        choiceBox.getItems().add("Last Half-Year");
        choiceBox.getItems().add("Last Year");
        choiceBox.getStylesheets().add("/Code/View/css/table-view.css");
        choiceBox.getStyleClass().clear();
        choiceBox.getStyleClass().add("select-time");
        choiceBox.getSelectionModel().selectFirst();



        pane.setCenter(choiceBox);
        return pane;
    }

    public void initialize(){
        this.model = Model.getInstance();
        model.addRefreshIdeasController(this);
        model.addRefreshSubjectsController(this);
        model.addRefreshDataController(this);
        this.controller = Controller.getInstance();
        quizFacts.setCellFactory(new TitleNodeListFactory());
        quizFacts.setOrientation(Orientation.HORIZONTAL);

        namesToColor = FXCollections.observableArrayList(new Pair<String, String>("Fail","Red"),new Pair<>("Mediocre","Orange"), new Pair<>("Pass","Yellow"),
                new Pair<>("Almost","#c4f502"), new Pair<>("Ready","Green"));

        this.ideaTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        TableColumn<String,String> ideaPrompt = new TableColumn<>("Idea");
        ideaPrompt.setCellValueFactory(new PropertyValueFactory<>("prompt"));
        ideaPrompt.setCellFactory(new Callback<TableColumn<String, String>, TableCell<String, String>>() {
            @Override
            public TableCell<String, String> call(TableColumn<String, String> param) {
                return new PromptCell();
            }
        });

        TableColumn<String,String> lengthTime = new TableColumn<>();
        lengthTime.setPrefWidth(100);
        Label lengthLabel = new Label("Total Time");
        lengthLabel.setUnderline(true);

        lengthChoiceBox = new ChoiceBox<>();
        lengthPopOver = new PopOver(getSelectTimePane(lengthChoiceBox));
        lengthChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null & !newValue.equals(oldValue)){
                for(Object r: ideaTable.getItems()){
                    if(r instanceof IdeaRecord){
                        IdeaRecord record = (IdeaRecord) r;
                        record.setTimeLength(timeRegions.get(newValue.intValue()));
                    }
                }
            }
            ideaTable.refresh();
        });

        lengthPopOver.setStyle("-fx-background-color: #6B6B6B");

        lengthTime.setGraphic(lengthLabel);
        lengthTime.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        lengthTime.setCellFactory(new Callback<TableColumn<String, String>, TableCell<String, String>>() {
            @Override
            public TableCell<String, String> call(TableColumn<String, String> param) {
                return new NumberCell(" times");
            }
        });


        lengthTime.setCellValueFactory(new PropertyValueFactory<>("total"));
        lengthTime.setCellFactory(new Callback<TableColumn<String, String>, TableCell<String, String>>() {
            @Override
            public TableCell<String, String> call(TableColumn<String, String> param) {
                return new TimeCell();
            }
        });

        TableColumn<String,String> frequencyTime = new TableColumn<>(""); //"Frequency"

        frequencyTime.setPrefWidth(100);
        Label frequencyLabel = new Label("Frequency");
        frequencyLabel.setUnderline(true);

        frequencyChoiceBox = new ChoiceBox<>();
        frequencyPopOver = new PopOver(getSelectTimePane(frequencyChoiceBox));
        frequencyChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null & !newValue.equals(oldValue)){
                for(Object r: ideaTable.getItems()){
                    if(r instanceof IdeaRecord){
                        IdeaRecord record = (IdeaRecord) r;
                        record.setTimeFrequency(timeRegions.get(newValue.intValue()));
                    }
                }
            }
            ideaTable.refresh();
        });

        frequencyPopOver.setStyle("-fx-background-color: #6B6B6B");

        frequencyTime.setGraphic(frequencyLabel);
        frequencyTime.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        frequencyTime.setCellFactory(new Callback<TableColumn<String, String>, TableCell<String, String>>() {
            @Override
            public TableCell<String, String> call(TableColumn<String, String> param) {
                return new NumberCell(" times");
            }
        });


        TableColumn<String,String> readiness = new TableColumn<>("Readiness");
        readiness.setCellValueFactory(new PropertyValueFactory<>("readiness"));
        readiness.setCellFactory(new Callback<TableColumn<String, String>, TableCell<String, String>>() {
            @Override
            public TableCell<String, String> call(TableColumn<String, String> param) {
                return new ReadinessCell();
            }
        });


        TableColumn<String,String> score = new TableColumn<>("Score");
        score.setCellValueFactory(new PropertyValueFactory<>("score"));
        score.setCellFactory(new Callback<TableColumn<String, String>, TableCell<String, String>>() {
            @Override
            public TableCell<String, String> call(TableColumn<String, String> param) {
                return new NumberCell("");
            }
        });


        ideaTable.getColumns().add(ideaPrompt);
        ideaTable.getColumns().add(lengthTime);
        ideaTable.getColumns().add(frequencyTime);
        ideaTable.getColumns().add(score);
        ideaTable.getColumns().add(readiness);



        lengthLabel.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                lengthPopOver.show(lengthLabel);
                frequencyPopOver.hide();
                //View.displayPopUpForTime(lengthPopOver,10,lengthLabel);
            }
        });

        frequencyLabel.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                frequencyPopOver.show(frequencyLabel);
                lengthPopOver.hide();
            }
        });




        //refreshIdeas();


        Timeline hourTimer = new Timeline(new KeyFrame(Duration.seconds(60*60), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                refreshIdeas();
            }
        }));
        hourTimer.setCycleCount(Timeline.INDEFINITE);
        hourTimer.play();


        View.setUpListForArrowManipulation(quizFacts,factsIndex);


    }

    IntegerValue factsIndex = new IntegerValue(-1);

    public void refreshIdeas(){
        ideaTable.getItems().clear();
        ideaTable.refresh();

        ObservableList<String> list = FXCollections.observableArrayList("Fail","Mediocre","Pass","Almost","Ready");

        int indexTime = lengthChoiceBox.getSelectionModel().getSelectedIndex();
        int indexFreq = frequencyChoiceBox.getSelectionModel().getSelectedIndex();

        String timeLength = timeRegions.get(indexTime);
        String timeFrequency = timeRegions.get(indexFreq);



        List<Idea> ideas = model.filterTopicByCurrentSubject().getAllIdeas();


        for (Idea idea: ideas){

            double score = model.calculateReadiness(idea);

            IdeaRecord record = new IdeaRecord(idea.getPrompt(),list.get(model.getReadinessType(score) - 1),((int) (100*score)) +"");
            record.setIdeaQuizzesByDate(model.getIdeaQuizzesByDate(idea));

            record.setTimeLength(timeLength);
            record.setTimeFrequency(timeFrequency);

            ideaTable.getItems().add(record);

        }

        updateQuizDetails(ideas);

    }

    List<Pair<String,String>> namesToColor;

    protected void updateQuizDetails(List<Idea> ideas){

        quizFacts.getItems().clear();

        List<IdeaQuiz> ideaQuizzes = model.getAllIdeaQuizzes(ideas);


        int numberOfIdeasStudied = 0;
        for(Idea i: ideas){

            if(Quizzes.totalTime(model.getIdeaQuizzesByDate(i).get("month"))>0){
                numberOfIdeasStudied += 1;
            }

        }


        //Number of Ideas:
        this.quizFacts.getItems().add(
                new AbstractMap.SimpleEntry<Node, Node>(
                        View.getLabel("IDEAS STUDIED (%)",18),
                        View.getLabel(""+numberOfIdeasStudied + " of " + ideas.size() + " (" + (ideas.isEmpty() ? 0 : (100*numberOfIdeasStudied/ideas.size())) + "%)",24))
        );


        Pair<String,String> nameColor = this.namesToColor.get(model.getReadinessType(model.averageReadiness(ideas))-1);
        //Average Readiness:
        this.quizFacts.getItems().add(
                new AbstractMap.SimpleEntry<Node, Node>(
                        View.getLabel("AVG. READINESS",18),
                        View.getLabel(nameColor.getKey(),nameColor.getValue(),24))
        );


        //Total Completion time:
        long cTime = Quizzes.totalTime(ideaQuizzes);
        String completionTime = "0";
        if(cTime>0){
            completionTime = ViewNotesController.getOverview(cTime," years"," months", " weeks",
                    " days", " hours", " mins", " secs","0","","");
        }

        this.quizFacts.getItems().add(
                new AbstractMap.SimpleEntry<Node, Node>(
                        View.getLabel("TOTAL TIME",18),
                        View.getLabel(completionTime,24))
        );


        TitleChoiceBox<String> readyChoices = new TitleChoiceBox<>();
        Label percentagesOfIdeas = readyChoices.getLeftLabel();
        percentagesOfIdeas.setText("IDEAS % ");
        ChoiceBox<String> readinessBox = readyChoices.getRightChoice();
        readinessBox.getItems().add("FAIL");
        readinessBox.getItems().add("MEDIOCRE");
        readinessBox.getItems().add("PASS");
        readinessBox.getItems().add("ALMOST");
        readinessBox.getItems().add("READY");

        Label bottom = View.getLabel("",24);


        List<IntegerValue> numbersOfIdeas = new ArrayList<>();
        numbersOfIdeas.add(new IntegerValue(0)); //Fail
        numbersOfIdeas.add(new IntegerValue(0)); //Mediocre
        numbersOfIdeas.add(new IntegerValue(0)); //Pass
        numbersOfIdeas.add(new IntegerValue(0)); // Almost
        numbersOfIdeas.add(new IntegerValue(0)); // Ready

        for(Idea i: ideas){
            int type = model.getReadinessType(i);
            IntegerValue value = numbersOfIdeas.get(type-1);
            value.setInteger(value.intValue() + 1);
        }

        readinessBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null && !newValue.equals(oldValue)){
                if(!ideas.isEmpty()){
                    bottom.setText("" + (100*numbersOfIdeas.get(newValue.intValue()).intValue()/ideas.size()) + "%");
                }else{
                    bottom.setText("" + (0) + "%");
                }

                Label label = (Label) readinessBox.lookup(".label");
                if(label!=null)
                    readinessBox.setPrefWidth(MindMapCell.getTextWidth(label.getText(),label.getFont())+40);

            }
        });

        readinessBox.getSelectionModel().selectLast();

        this.quizFacts.getItems().add(new AbstractMap.SimpleEntry<Node, Node>(
                readyChoices,
                bottom));





        String averageTime = "0";
        if(!ideaQuizzes.isEmpty()){
            averageTime = ViewNotesController.getOverview(Quizzes.totalTime(ideaQuizzes)/ideaQuizzes.size()," years"," months", " weeks",
                    " days", " hours", " mins", " secs","0","","");

        }


        this.quizFacts.getItems().add(
                new AbstractMap.SimpleEntry<Node, Node>(
                        View.getLabel("AVG. TIME",18),
                        View.getLabel(averageTime,24))
        );


        if(!ideas.isEmpty()){

            Study.sortByReadiness(ideas);

            //Best Idea
            this.quizFacts.getItems().add(
                    new AbstractMap.SimpleEntry<Node, Node>(
                            View.getLabel("BEST IDEA",18),
                            controller.getIdeaLabel(ideas.get(ideas.size()-1),20)
                    )
            );


            //Worst Idea
            this.quizFacts.getItems().add(
                    new AbstractMap.SimpleEntry<Node, Node>(
                            View.getLabel("WORST IDEA",18),
                            controller.getIdeaLabel( ideas.get(0),20)
                    )
            );

        }


    }

    @FXML protected void handleRightClick(ActionEvent e){

        View.handleRightClick(quizFacts,factsIndex);
    }

    @FXML protected void handleLeftClick(ActionEvent e){

        View.handleLeftClick(quizFacts,factsIndex);
    }

    @Override
    public void refreshSubjects() {
        subjectLine.setText(model.getCurrentSubject().getName());
        refreshIdeas();
        this.ideaTable.refresh();
    }

    @Override
    public void refreshData() {
        refreshIdeas();
    }
}



