package Code.Controller.study;

import Code.Controller.Controller;
import Code.Controller.Dialogs.ViewNotes.ViewNotesController;
import Code.Controller.IntegerValue;
import Code.Model.*;
import Code.View.components.MindMap.MindMapCell;
import Code.View.components.TitleNode.TitleChoiceBox;
import Code.View.components.TitleNode.TitleNodeListFactory;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.util.Pair;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import Code.View.View;

public class LastQuizController {

    @FXML protected ChoiceBox<Idea> ideaSelect;

    @FXML protected ListView<Map.Entry<Node, Node>> quizFacts;
    @FXML protected ListView<Map.Entry<Node, Node>> ideaFacts;

    Quiz quiz;

    List<Pair<String,String>> namesToColor;

    List<ImageView> confidenceEmojis;

    Model model;
    Controller controller;


    @FXML protected Button viewIdea;

    private ImageView toImageView(Image image,double width, double height){
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        return imageView;
    }

    private ImageView toImageView(String nameOfFileInIcon,double width, double height){
        return toImageView(new Image(getClass().getResourceAsStream("/Code/View/Icons/" + nameOfFileInIcon)),width,height);
    }

    private ImageView confidenceImage(int index){
        if(!(index>=1 && index<=5)){
            return null;
        }
        return toImageView("confidence"+index+".png",50,50);
    }

    public void initialize(){
        this.model = Model.getInstance();
        this.controller = Controller.getInstance();
        this.controller.setLastQuizController(this);

        quizFacts.setCellFactory(new TitleNodeListFactory());
        quizFacts.setOrientation(Orientation.HORIZONTAL);
        ideaFacts.setCellFactory(new TitleNodeListFactory());
        ideaFacts.setOrientation(Orientation.HORIZONTAL);
        namesToColor = FXCollections.observableArrayList(new Pair<String, String>("Fail","Red"),new Pair<>("Mediocre","Orange"), new Pair<>("Pass","Yellow"),
                new Pair<>("Almost","#c4f502"), new Pair<>("Ready","Green"));

        confidenceEmojis = FXCollections.observableArrayList(confidenceImage(1),confidenceImage(2),confidenceImage(3),confidenceImage(4),confidenceImage(5));

        View.setUpListForArrowManipulation(quizFacts,quizIndex);
        View.setUpListForArrowManipulation(ideaFacts,ideaIndex);


    }

    IntegerValue quizIndex = new IntegerValue(-1);
    IntegerValue ideaIndex = new IntegerValue(-1);

    @FXML protected void handleRightClick(ActionEvent e){
        View.handleRightClick(quizFacts,quizIndex);
    }

    @FXML protected void handleLeftClick(ActionEvent e){
       View.handleLeftClick(quizFacts,quizIndex);
    }

    @FXML protected void handleRight2Click(ActionEvent e){
        View.handleRightClick(ideaFacts,ideaIndex);
    }

    @FXML protected void handleLeft2Click(ActionEvent e){
        View.handleLeftClick(ideaFacts,ideaIndex);
    }

    private String getTimes(int number){
        String n = number + " time";
        if(number>1){
            n += "s";
        }
        return n;
    }

    private AbstractMap.SimpleEntry<Node,Node> getLastDate(Idea idea){

        TitleChoiceBox<String> selectTime = new TitleChoiceBox<>();

        Label left = selectTime.getLeftLabel();
        left.setText("LAST");
        ChoiceBox<String> choiceBox = selectTime.getRightChoice();

        HashMap<String,List<IdeaQuiz>> map = model.getIdeaQuizzesByDate(idea);

        List<String> options = new ArrayList<>();
        for(String s: map.keySet()){
            options.add(s.toUpperCase());
        }
        choiceBox.getItems().addAll(options);

        Label duration = View.getLabel("  "+Quizzes.totalTime(map.get("all")," years"," months", " weeks",
                " days", " hours", " mins", " secs"),22);
        Label times = View.getLabel( ""+getTimes(map.get("all").size()),22);
        left.setPrefHeight(choiceBox.getPrefHeight());


        choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue!=null && newValue!=null && !oldValue.equals(newValue)){
                List<IdeaQuiz> list = map.get(newValue.toLowerCase());
                times.setText(   ""+getTimes(map.get(newValue.toLowerCase()).size() ));
                duration.setText("  "+Quizzes.totalTime(list," years"," months", " weeks",
                        " days", " hours", " mins", " secs"));

                Label label = (Label) choiceBox.lookup(".label");
                choiceBox.setPrefWidth(MindMapCell.getTextWidth(label.getText(),label.getFont())+40);
            }
        });

        choiceBox.getSelectionModel().select("ALL");
        choiceBox.setPrefWidth(MindMapCell.getTextWidth("ALL",Font.font("Inter-Semi-Bold",18))+40);


        HBox box = new HBox();
        box.getChildren().add(times);
        box.getChildren().add(duration);
        box.setAlignment(Pos.CENTER);



        return new AbstractMap.SimpleEntry(selectTime,box);

    }

    private void showIdea(Quiz quiz, Idea idea){
        IdeaQuiz ideaQuiz = quiz.find(idea);
        if(ideaQuiz==null) return;
        this.ideaFacts.getItems().clear();

        this.ideaFacts.getItems().add(
                new AbstractMap.SimpleEntry<Node, Node>(
                        View.getLabel("CONFIDENCE",18),
                        (confidenceImage(ideaQuiz.getConfidence()))
                )
        );

        Pair<String,String> nameColor = this.namesToColor.get(model.getReadinessType(idea)-1);
        //Average Readiness:
        this.ideaFacts.getItems().add(
                new AbstractMap.SimpleEntry<Node, Node>(
                        View.getLabel("READINESS",18),
                        View.getLabel(nameColor.getKey(),nameColor.getValue(),24))
        );

        String completionTime = ViewNotesController.getOverview(ideaQuiz.getTime()," years"," months", " weeks",
                " days", " hours", " mins", " secs","0","","");

        this.ideaFacts.getItems().add(
                new AbstractMap.SimpleEntry<Node, Node>(
                        View.getLabel("TOTAL TIME",18),
                        View.getLabel(completionTime,24))
        );


        this.ideaFacts.getItems().add(
                getLastDate(idea)
        );


        //Average Score
        this.ideaFacts.getItems().add(
                new AbstractMap.SimpleEntry<Node, Node>(
                        View.getLabel("SCORE",18),
                        View.getLabel( (""+((int) (100*ideaQuiz.getReadiness()) )),24)
                )
        );

    }

    private Idea idea;





    public void showQuiz(Quiz quiz){
        this.ideaFacts.getItems().clear();
        this.quizFacts.getItems().clear();
        this.quiz = quiz;

        List<IdeaQuiz> bestOrder = quiz.getSortedIdeaQuizByReadiness();

        List<Idea> ideas = new ArrayList<>();

        for(IdeaQuiz i: bestOrder){
            ideas.add(model.getIdea(i.getId()));
        }

        this.ideaSelect.setItems(FXCollections.observableArrayList(ideas));

        this.ideaSelect.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null && quiz!=null && !newValue.equals(oldValue)){
                showIdea(quiz,newValue);
                this.idea = newValue;
                Label label = (Label) ideaSelect.lookup(".label");
                if(label!=null)
                    ideaSelect.setPrefWidth(MindMapCell.getTextWidth(label.getText(),label.getFont())+40);
            }
        });

        viewIdea.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.displayIdea(idea);
            }
        });

        ideaSelect.getSelectionModel().selectLast();
        ideaSelect.setPrefWidth(MindMapCell.getTextWidth(ideaSelect.getSelectionModel().getSelectedItem().toString(),Font.font("Inter-Semi-Bold",18))+40);





        //DateFormat dateFormat = new SimpleDateFormat("EEEE dd/MM/yyyy HH:mm");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        //time.setText(dateFormat.format(new Date(date)));

        //Date
        this.quizFacts.getItems().add(
                new AbstractMap.SimpleEntry<Node, Node>(
                        View.getLabel("DATE",18),
                        View.getLabel( (dateFormat.format(new Date(quiz.getDate()))),24)
                )
        );

        //Number of Ideas:
        this.quizFacts.getItems().add(
                new AbstractMap.SimpleEntry<Node, Node>(
                        View.getLabel("NUMBER OF IDEAS",18),
                        View.getLabel(""+quiz.getIdeaQuizzes().size(),24))
        );

        Pair<String,String> nameColor = this.namesToColor.get(model.getReadinessType(quiz.getAverageReadiness())-1);
        //Average Readiness:
        this.quizFacts.getItems().add(
                new AbstractMap.SimpleEntry<Node, Node>(
                        View.getLabel("AVG. READINESS",18),
                        View.getLabel(nameColor.getKey(),nameColor.getValue(),24))
        );


        //Total Completion time:
        String completionTime = ViewNotesController.getOverview(quiz.getTotalCompletionTime()," years"," months", " weeks",
                " days", " hours", " mins", " secs","","","");



        this.quizFacts.getItems().add(
                new AbstractMap.SimpleEntry<Node, Node>(
                        View.getLabel("TOTAL TIME",18),
                        View.getLabel(completionTime,24))
        );


        String averageTime = ViewNotesController.getOverview(quiz.getAverageCompletionTime()," years"," months", " weeks",
                " days", " hours", " mins", " secs","0","","");


        this.quizFacts.getItems().add(
                new AbstractMap.SimpleEntry<Node, Node>(
                        View.getLabel("AVG. TIME",18),
                        View.getLabel(averageTime,24))
        );


        //Average Confidence
        this.quizFacts.getItems().add(
                new AbstractMap.SimpleEntry<Node, Node>(
                        View.getLabel("AVG. CONFIDENCE",18),
                        confidenceImage(quiz.getAverageConfidence())
                )
        );

        //Average Score
        this.quizFacts.getItems().add(
                new AbstractMap.SimpleEntry<Node, Node>(
                        View.getLabel("AVG. SCORE",18),
                        View.getLabel( (""+((int) (100*quiz.getAverageReadiness()) )),24)
                )
        );


        //Best Idea
        this.quizFacts.getItems().add(
                new AbstractMap.SimpleEntry<Node, Node>(
                        View.getLabel("BEST IDEA",18),
                        controller.getIdeaLabel(model.getIdea(bestOrder.get(bestOrder.size()-1).getId()),20)
                )
        );




        //Worst Idea
        this.quizFacts.getItems().add(
                new AbstractMap.SimpleEntry<Node, Node>(
                        View.getLabel("WORST IDEA",18),
                        controller.getIdeaLabel( model.getIdea(bestOrder.get(0).getId()),20)
                )
        );


        List<IdeaQuiz> timeOrder = quiz.getSortedIdeaQuizByTime();

        //Quickest Idea
        this.quizFacts.getItems().add(
                new AbstractMap.SimpleEntry<Node, Node>(
                        View.getLabel("QUICKEST IDEA",18),
                        controller.getIdeaLabel( model.getIdea(timeOrder.get(timeOrder.size()-1).getId()),20)
                )
        );


        //Slowest Idea
        this.quizFacts.getItems().add(
                new AbstractMap.SimpleEntry<Node, Node>(
                        View.getLabel("SLOWEST IDEA",18),
                        controller.getIdeaLabel( model.getIdea(timeOrder.get(0).getId()),20)
                )
        );

    }

}
