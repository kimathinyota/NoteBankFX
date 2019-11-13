package Code.Controller.quiz;

import Code.Controller.Controller;
import Code.Controller.Dialogs.ViewNotes.ViewMode;
import Code.Controller.Page;
import Code.Controller.study_session.StudySet;
import Code.Model.*;
import Code.View.View;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.util.Pair;
import org.controlsfx.control.PopOver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class QuizPageController {

    enum  QuizPageType{
        Response,
        EvaluateResponse;
    }

    enum QuizSimulationType{
        Quiz,
        Session
    }

    @FXML protected Button quit;

    @FXML protected Button next;

    @FXML protected Button promptNotes;

    @FXML protected Label prompt, responseTitle;

    @FXML protected HTMLEditor responseEditor;

    @FXML protected Pane setToAnswerPane;

    @FXML protected BorderPane responsePane, evaluatePane;

    @FXML protected Button setToAnswer;

    @FXML protected Pane confidencePane;

    @FXML protected ToggleButton star1,star2,star3,star4,star5;

    @FXML protected WebView missingKeyWord;

    private Button answer,all, evaluate;

    private List<ToggleButton> stars;

    private int numberOfStarsSelected;

    private ToolBar responseEditorToolBarTop,responseEditorToolBarBottom;

    private Model model;

    private Controller controller;

    private long startingTime;

    private IdeaQuiz lastIdeaQuiz;

    private List<Idea> ideas;

    private Idea currentIdea;

    private QuizPageType type;

    private Quiz quiz;

    private QuizSimulationType simulationType;

    private ProgressBar quizProgress;

    private Label quizIdeaLabel, quizIdeaFraction;

    private PopOver popOver;

    private void selectAllStarsUpToIndex(int index){
        for(int i=0; i<stars.size(); i++){
            if(i<=index){
                stars.get(i).setSelected(true);
            }else{
                stars.get(i).setSelected(false);
            }
        }
    }

    private void quit(){

        this.quizIdeaLabel.setVisible(false);

        if(ideas.isEmpty() || quiz.getIdeaQuizzes().isEmpty()){

            controller.switchToPage(Page.HomeFeatured);


            if(lastIdeaQuiz!=null){
                if(lastIdeaQuiz.getTime()==null){
                    lastIdeaQuiz.setTime(System.currentTimeMillis() - startingTime);
                }

                if(lastIdeaQuiz.getConfidence()==null){
                    lastIdeaQuiz.setConfidence(numberOfStarsSelected + 1);
                }

                if(lastIdeaQuiz.getReadiness()==null){
                    lastIdeaQuiz.setReadiness(model.calculateReadiness(model.getIdea(lastIdeaQuiz.getId()),lastIdeaQuiz.getConfidence()));
                }
            }



            reset();
            refreshData();

            return;
        }

        controller.showLastQuiz(quiz);

        reset();

    }

    public void initialize(){
        model = Model.getInstance();
        controller = Controller.getInstance();
        controller.setQuizPageController(this);
        this.quizIdeaLabel = controller.getQuizIdeaLabel();
        this.quizIdeaFraction = controller.getQuizFraction();
        this.quizProgress = controller.getQuizProgressBar();

        this.stars = FXCollections.observableArrayList(star1,star2,star3,star4,star5);
        star1.setSelected(true);
        numberOfStarsSelected = 0;
        for(ToggleButton star: stars){
            star.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    int index = stars.indexOf(star);
                    selectAllStarsUpToIndex(index);
                    numberOfStarsSelected = index;
                }
            });
            star.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    int index = stars.indexOf(star);
                    selectAllStarsUpToIndex(index);
                }
            });

            star.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    selectAllStarsUpToIndex(numberOfStarsSelected);
                }
            });
        }

        evaluate = new Button("↻");
        evaluate.setTooltip(new Tooltip("Highlight missing and show found keywords"));
        answer = new Button("ANSWER");
        answer.setTooltip(new Tooltip("View Final Note"));
        all = new Button("ALL");
        all.setTooltip(new Tooltip("View all notes"));
        answer.getStyleClass().add("answer");
        all.getStyleClass().add("all");
        evaluate.getStyleClass().add("all");
        evaluate.setFont(Font.font("Inter",30));
        evaluate.setMinSize(30,evaluate.getMaxWidth());

        responseEditorToolBarTop = (ToolBar) responseEditor.lookup(".top-toolbar");
        responseEditorToolBarBottom = (ToolBar) responseEditor.lookup(".bottom-toolbar");

        evaluate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Idea idea = model.getIdea(currentIdea.getID());
                if(idea!=null){
                    evaluate(idea);
                }
            }
        });

        answer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                Idea idea = model.getIdea(currentIdea.getID());
                if(idea!=null){
                    if(idea.getFinalNote()!=null){
                        controller.displayNotes(FXCollections.observableArrayList(idea.getFinalNote()), ViewMode.ViewLimited);
                    }else{
                        View.displayPopUpForTime("Information","No final note has been set ",2,answer,200);
                    }
                }
            }
        });

        all.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Idea idea = model.getIdea(currentIdea.getID());
                if(idea!=null){
                    if(!idea.getNotes().isEmpty()){
                        controller.displayNotes(idea.getNotes(),ViewMode.ViewLimited);
                    }else{
                        View.displayPopUpForTime("Information","No notes have been found ",2,all,200);
                    }
                }
            }
        });

        promptNotes.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Idea idea = model.getIdea(currentIdea.getID());
                if(idea!=null){
                    if(!idea.getPromptNotes().isEmpty()){

                        controller.displayNotes(idea.getPromptNotes(),ViewMode.ViewLimited);
                    }else{
                        View.displayPopUpForTime("Information","No prompt notes have been found ",2,promptNotes,200);
                    }
                }
            }
        });

        next.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                next();
            }
        });


        this.prompt.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                popOver = View.getInformationPane("Prompt",prompt.getText(),240);
                popOver.show(prompt);
            }
        });

        this.prompt.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                popOver.hide();
            }
        });


        this.quit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                quit();
            }
        });


        this.setToAnswer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(currentIdea==null)
                    return;
                model.setToFinalNote(responseEditor.getHtmlText(),currentIdea);

            }
        });

        setupEvaluate();

        //quit.setOnAction();

    }

    private void setupResponse(){
        responsePane.getChildren().remove(setToAnswerPane);
        confidencePane.setVisible(false);
        responseTitle.setText("Your Response");
        next.setText("NEXT");
        responseEditorToolBarBottom.getItems().remove(evaluate);
        responseEditorToolBarTop.getItems().remove(answer);
        responseEditorToolBarTop.getItems().remove(all);
        this.type = QuizPageType.Response;
        this.responseEditor.setHtmlText("<html><head></head><body contenteditable=\"true\"></body></html>");

        this.quizIdeaLabel.setText("RESPONSE");
    }

    private void setupEvaluate(){
        if(responsePane.getBottom()==null){
            responsePane.setBottom(setToAnswerPane);
        }
        confidencePane.setVisible(true);
        responseTitle.setText("  Evaluated Response");
        next.setText("NEXT");
        responseEditorToolBarBottom.getItems().remove(evaluate);
        responseEditorToolBarTop.getItems().remove(answer);
        responseEditorToolBarTop.getItems().remove(all);

        responseEditorToolBarBottom.getItems().add(0,evaluate);

        responseEditorToolBarTop.getItems().add(0,answer);
        responseEditorToolBarTop.getItems().add(1,all);
        this.type = QuizPageType.EvaluateResponse;

        this.quizIdeaLabel.setText("EVALUATE RESPONSE");

    }

    private String highlightAndBold(String htmlText, String keywords){
        if(!htmlText.contains(keywords))
            return htmlText;

        List<Pair<String,Boolean>> list = new ArrayList<>();
        String text, tag;
        text = "";
        tag = "";
        boolean isTag = false;
        for(int i=0; i<htmlText.length(); i++){
            if(htmlText.charAt(i) == '<'){
                isTag = true;
                tag = "<";
                if(!text.isEmpty()){
                    list.add(new Pair<>(text,false));
                }
            }else if(htmlText.charAt(i) == '>'){
                isTag = false;
                tag += ">";
                text = "";
                list.add(new Pair<>(tag,true));
            }else{
                if(isTag){
                    tag += htmlText.charAt(i);
                }else{
                    text += htmlText.charAt(i);
                }
            }
        }

        String content = "";
        for(Pair<String,Boolean> p: list){
            if(p.getValue().booleanValue()==true){
                content += p.getKey();
            }else{
                content += p.getKey().replaceAll(keywords,"<font color=\"#17fc26\"><b>"+keywords+"</b></font>");
            }
        }

        return content;


    }

    private void highlightFoundKeywords(List<String> keywords){
        String content = this.responseEditor.getHtmlText();

        for(String keyword: keywords){
            content = highlightAndBold(content,keyword);
        }

        setMissingKeywords(keywords);
        this.responseEditor.setHtmlText(content);
    }

    private void highlightFoundKeywords(Idea idea){
        highlightFoundKeywords(idea.getKeyWords());
    }

    private void setMissingKeywords(List<String> keywords){
        String temp = "";
        String content = this.responseEditor.getHtmlText();
        for(String keyword: keywords){
            content = highlightAndBold(content,keyword);
            if(!responseEditor.getHtmlText().contains(keyword)){
                temp += ("<strike>" + keyword + "</strike>") + "," ;
            }
        }
        temp = (!temp.isEmpty() ? temp.substring(0,temp.length()-1) : temp);
        String keywordHTML = "<html dir=\"ltr\"><head></head><body contenteditable=\"false\"><p><font face=\"Inter\" color=\"#fd1717\" size=\"6\"><b>Missing: </b>" + temp + "</font></p></body></html>";
        this.missingKeyWord.getEngine().loadContent(keywordHTML);
    }

    private void setMissingKeywords(Idea idea){
        setMissingKeywords(idea.getKeyWords());
    }

    private void evaluate(Idea idea){
        setMissingKeywords(idea);
        highlightFoundKeywords(idea);
    }

    private void setUp(Idea i, QuizPageType type){
        display(i);
        if(type.equals(QuizPageType.Response)){
            if(lastIdeaQuiz!=null){
                //if not null, page has been switched from EvaluateResponse to Response
                // so we need to add previous Idea to the quiz
                lastIdeaQuiz.setConfidence(numberOfStarsSelected +1);
                lastIdeaQuiz.setReadiness(model.calculateReadiness(i,lastIdeaQuiz.getConfidence()));
                this.numberOfStarsSelected = 0;
                selectAllStarsUpToIndex(numberOfStarsSelected);
            }

            setupResponse();

            startingTime = System.currentTimeMillis(); // start timer to see how long it takes for user to answer q

        }else{
            // assumes page has been switched from Response to EvaluateResponse so timer needs to be stopped
            lastIdeaQuiz = new IdeaQuiz(i); // set up new IdeaQuiz to store user input information for this idea
            lastIdeaQuiz.setTime(System.currentTimeMillis() - startingTime); // add elapsed time
            quiz.add(lastIdeaQuiz);
            lastIdeaQuiz.setReadiness(model.calculateReadiness(i));
            if(simulationType.equals(QuizSimulationType.Session)){
                controller.getSessionsScheduler().studied(studySet, Collections.singletonList(i));
            }


            setupEvaluate();
            evaluate(i);
        }
    }

    private void reset(){

        this.quiz = null;
        this.lastIdeaQuiz = null;
        type = null;
        currentIdea = null;
        this.numberOfStarsSelected = 0;
        selectAllStarsUpToIndex(numberOfStarsSelected);
    }

    private void refreshData(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                model.refreshData();
            }
        });
    }

    private boolean next(){
        int index = ideas.indexOf(currentIdea); //current position of quiz, -1 if null or doesn't exist
        if ((ideas.size() - 1) == (index) && type == QuizPageType.EvaluateResponse) {
            //implies quiz has been completed since the last idea has been evaluated
            lastIdeaQuiz.setConfidence(numberOfStarsSelected +1);
            lastIdeaQuiz.setReadiness(model.calculateReadiness(currentIdea,lastIdeaQuiz.getConfidence()));

            model.saveQuiz();
            quit();
            refreshData();
            return false;
        }

        if (currentIdea!=null && type!=null && type.equals(QuizPageType.Response)) {
            //need to switch to evaluation page
            double progress = ((double) 2*(index) + 2 )/(2*ideas.size());
            this.quizProgress.setProgress( progress );
            String fractions = (index +1) + "B/" + ideas.size();
            this.quizIdeaFraction.setText(fractions);
            setUp(currentIdea, QuizPageType.EvaluateResponse);
            return true;
        }

        //need to switch to response page

        currentIdea = ideas.get(index + 1);


        double progress = ((double) 2*(index+1) + 1  )/(2*ideas.size());
        this.quizProgress.setProgress( progress );
            String fractions = (index+2) + "A/" + ideas.size();
        this.quizIdeaFraction.setText(fractions);

        setUp(currentIdea,QuizPageType.Response);
        refreshData();
        return true;

    }

    private void display(Idea i){
        this.currentIdea = i;
        this.prompt.setText(i.getPromptType().toString().toUpperCase() + ": " + i.getPrompt());
        //this.quizIdeaLabel.setText(i.toString());
    }

    private void simulate(List<Idea> ideas, QuizSimulationType simulationType){
        if(ideas.isEmpty())
            return;
        this.simulationType = simulationType;
        this.ideas = ideas;
        quiz = new Quiz(System.currentTimeMillis());
        model.addQuiz(quiz);
        this.quizIdeaLabel.setVisible(true);
        next();
    }

    StudySet studySet;

    public void startQuiz(List<Idea> ideas){
        simulate(ideas,QuizSimulationType.Quiz);
    }

    public void startSession(List<Idea> ideas, StudySet set){
        this.studySet = set;
        simulate(ideas,QuizSimulationType.Session);
    }



}


