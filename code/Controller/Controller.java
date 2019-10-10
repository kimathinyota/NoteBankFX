package Code.Controller;

import Code.Controller.Dialogs.Create.*;
import Code.Controller.Dialogs.ViewNotes.ViewMode;
import Code.Controller.Dialogs.ViewNotes.ViewNotesController;
import Code.Controller.ideas.IdeasPageController;
import Code.Controller.quiz.QuizPageController;
import Code.Controller.study.LastQuizController;
import Code.Controller.study_session.ScheduleSessions;
import Code.Controller.study_session.StudySet;
import Code.Model.*;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Designed to handle inter-controller communication
 */
public class Controller {

    MainWindowController controller;

    ViewNotesController viewNotesController;

    IdeasPageController ideasPageController;

    LastQuizController lastQuizController;



    public void setQuizPageController(QuizPageController quizPageController) {
        this.quizPageController = quizPageController;
    }

    QuizPageController quizPageController;

    public void setCreateNoteController(CreateNoteController createNoteController) {
        this.createNoteController = createNoteController;
    }

    CreateNoteController createNoteController;


    public void createStudySession(){
        createStudySessionController.create();
    }

    public void createStudySession(StudyPlan plan){
        createStudySessionController.create(plan);
    }

    public void restartStudySession(StudySession session){
        createStudySessionController.edit(session);
    }

    public void createStudyPlan(){
        createStudyPlanController.create();
    }

    public void editStudyPlan(StudyPlan studyPlan){
        createStudyPlanController.edit(studyPlan);
    }


    public void createNote(){
        createNoteController.create();
    }

    public void editNote(Note note){
        createNoteController.edit(note);
    }


    public void setCustomQuizController(CustomQuizController customQuizController) {
        this.customQuizController = customQuizController;
    }

    CustomQuizController customQuizController;

    CreateStudySessionController createStudySessionController;

    CreateStudyPlanController createStudyPlanController;

    public void setCreateStudySessionController(CreateStudySessionController createStudySessionController){
        this.createStudySessionController = createStudySessionController;
    }

    public void setCreateStudyPlanController(CreateStudyPlanController createStudyPlanController){
        this.createStudyPlanController = createStudyPlanController;
    }


    public void setLastQuizController(LastQuizController lastQuizController){
        this.lastQuizController = lastQuizController;
    }

    public ProgressBar getQuizProgressBar(){
        return controller.getQuizProgressBar();
    }

    public Label getQuizIdeaLabel(){
        return controller.getQuizIdeaLabel();
    }


    public Label getQuizFraction(){
        return controller.getQuizFraction();
    }


    public void setCreateIdeaController(CreateIdeaController createIdeaController) {
        this.createIdeaController = createIdeaController;
    }

    CreateIdeaController createIdeaController;


    public void setIdeasPageController(IdeasPageController controller){
        this.ideasPageController = controller;
    }

    public void displayIdea(Idea idea){
        ideasPageController.displayIdea(idea);
        switchToPage(Page.IdeasIdea);
    }

    public void switchToPage(Page page){
        controller.setPage(page);
    }

    public void disable(AppFeatures features, boolean disable){
        switch (features){
            case SubjectList:
                controller.disableSubjectList(disable);
                break;
        }
    }


    public void displayNotes(List<Note>notes, ViewMode mode){
        this.viewNotesController.displayNotes(notes, mode);
    }

    public void displayAndSelectNotes(List<Note>notes, ListView<Note> listForAddingSelectedNotes, Idea idea){
        this.viewNotesController.displayAndSelectNotes(notes,listForAddingSelectedNotes, idea);
    }

    public static void executeTask(Task task){
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(task);
        executorService.shutdown();
    }


    public void createIdea(){
        createIdeaController.createIdea();
    }

    public void createIdea(Topic topic){
        createIdeaController.createIdea(topic);
    }

    public void editIdea(Idea idea){
        createIdeaController.editIdea(idea);
    }

    public void displayAndSelectNotes(List<Note>notes){
        this.viewNotesController.displayAndSelectNotes(notes);
    }

    public List<Note> finish(){
        return viewNotesController.finish();
    }


    public void setViewNotesController(ViewNotesController viewNotesController){
        this.viewNotesController = viewNotesController;
    }

    public void setMainWindowController(MainWindowController controller){
        this.controller = controller;
    }

    private final static Controller instance = new Controller();


    public static Controller getInstance(){
        return instance;
    }


    public void setupQuiz(){
        customQuizController.show();
    }

    public void showLastQuiz(Quiz quiz){
        switchToPage(Page.StudyLastQuiz);
        this.lastQuizController.showQuiz(quiz);
    }

    public Label getIdeaLabel(Idea newIdea, double size){
        Label label = new Label(newIdea.getPrompt());
        label.setAlignment(Pos.CENTER);
        label.setContentDisplay(ContentDisplay.CENTER);
        label.setWrapText(true);
        label.getStyleClass().add("idea-label");
        label.getStylesheets().add("/Code/View/css/quiz.css");
        label.setFont(Font.font("Inter-Semi-Bold",size));
        label.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                displayIdea(newIdea);
            }
        });
        return label;
    }


    public void startQuiz(List<Idea>ideas){
        if(ideas.isEmpty()){
            return;
        }
        switchToPage(Page.QuizPage);
        this.quizPageController.startQuiz(ideas);
    }

    public void startSession(List<Idea>ideas, StudySet set){
        if(ideas.isEmpty()){
            return;
        }
        switchToPage(Page.QuizPage);
        this.quizPageController.startSession(ideas,set);
    }



    ScheduleSessions scheduleSessions;

    public void setScheduleSessions(ScheduleSessions scheduleSessions){
        this.scheduleSessions = scheduleSessions;
    }

    public ScheduleSessions getSessionsScheduler(){
        return scheduleSessions;
    }

}
