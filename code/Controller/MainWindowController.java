package Code.Controller;

import Code.Controller.RefreshInterfaces.RefreshSubjectsController;
import Code.Controller.search.AdvancedSearchSettings;
import Code.Model.Model;
import Code.Model.Quizzes;
import Code.Model.StudyPlan;
import Code.Model.Subject;
import Code.View.View;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.bouncycastle.math.raw.Mod;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Controller for MainWindow
 */
public class MainWindowController implements RefreshSubjectsController {

    @FXML protected Button home;
    @FXML protected Button ideas;
    @FXML protected Button study;

    @FXML protected GridPane homeMenu;
    @FXML protected GridPane ideasMenu;
    @FXML protected GridPane studyMenu;

    @FXML protected Pane homePane;
    @FXML protected Pane homeFeaturedPane;
    @FXML protected Pane homeNotesPane;
    @FXML protected Button homeFeatured;
    @FXML protected Button homeNotes;

    @FXML protected Pane ideasPane;
    @FXML protected Pane ideasOverviewPane;
    @FXML protected Pane ideasMindMapPane;
    @FXML protected Pane ideasIdeaPane;
    @FXML protected Button ideasOverview;
    @FXML protected Button ideasMindMap;
    @FXML protected Button ideasIdea;

    @FXML protected Pane studyPane;
    @FXML protected Pane studyOverviewPane;
    @FXML protected Pane studyFocusPane;
    @FXML protected Pane studyIdeasPane;
    @FXML protected Pane studyLastQuizPane;
    @FXML protected Button studyOverview;
    @FXML protected Button studyFocus;
    @FXML protected Button studyIdeas;
    @FXML protected Button studyLastQuiz;

    @FXML protected BorderPane mainWindow;
    @FXML protected Text mainWindowHeader;
    @FXML protected Button settings;
    @FXML protected AnchorPane advancedSearch;

    @FXML protected Pane app;
    @FXML protected Pane clickpane;

    @FXML protected TextField search;
    @FXML protected ListView searchlist;
    @FXML protected Pane searchlistpane;
    @FXML protected Pane createNote;
    @FXML protected Pane createIdea;
    @FXML protected Pane customQuiz, createstudysession, createstudyplan;
    @FXML protected Button newNote;
    @FXML protected Button newSubject;


    @FXML protected ToggleButton inPageToggle;
    @FXML protected ToggleButton includeImagesToggle;
    @FXML protected ToggleButton includeTextsToggle;
    @FXML protected ToggleButton includeBooksToggle;
    @FXML protected ToggleButton includeIdeasToggle;
    @FXML protected ToggleButton includeStudyPlansToggle;
    @FXML protected ListView<String> subjectList;

    @FXML protected BorderPane quizProgress;
    @FXML protected ProgressBar quizProgressBar;
    @FXML protected Label quizIdeaLabel;
    @FXML protected Label quizFraction;


    @FXML protected Pane viewNote;

    @FXML protected Pane lastQuizMenu;

    @FXML protected Pane leftNavigation;

    Pane homeFeaturedPanel, homeNotesPanel;
    Pane ideasIdeaPanel, ideasMindMapPanel, ideasOverviewPanel;
    Pane studyOverviewPanel, studyFocusPanel, studyIdeasPanel, studyLastQuizPanel;
    Pane quizPanel;

    Model model;



    AdvancedSearchSettings advancedSearchSettings;


    public ProgressBar getQuizProgressBar(){
        return quizProgressBar;
    }

    public Label getQuizIdeaLabel(){
        return quizIdeaLabel;
    }


    public Label getQuizFraction(){
        return quizFraction;
    }


    /**
     * Set default advance search settings
     */
    public void setDefaultAdvanceSearchSettings(){
        inPageToggle.setSelected(false);
        includeBooksToggle.setSelected(true);
        includeIdeasToggle.setSelected(true);
        includeImagesToggle.setSelected(true);
        includeStudyPlansToggle.setSelected(true);
        includeTextsToggle.setSelected(true);
    }


    /**
     * Called on SearchInPage toggle button click
     * Will update stored AdvancedSearchSettings object
     * @param e
     */
    @FXML protected void handleSearchInPageAction(ActionEvent e){
        advancedSearchSettings.setSearchInPage(inPageToggle.isSelected());
    }

    /**
     * Called on IncludeImages toggle button click
     * Will update stored AdvancedSearchSettings object
     * @param e
     */
    @FXML protected void handleIncludeImagesAction(ActionEvent e){
        advancedSearchSettings.setIncludeImages(includeImagesToggle.isSelected());
    }

    /**
     * Called on IncludeTexts toggle button click
     * Will update stored AdvancedSearchSettings object
     * @param e
     */
    @FXML protected void handleIncludeTextsAction(ActionEvent e){
        advancedSearchSettings.setIncludeTexts(includeTextsToggle.isSelected());
    }

    /**
     * Called on IncludeBooks toggle button click
     * Will update stored AdvancedSearchSettings object
     * @param e
     */
    @FXML protected void handleIncludeBooksAction(ActionEvent e){
        advancedSearchSettings.setIncludeBooks(includeBooksToggle.isSelected());
    }

    /**
     * Called on IncludeIdeas toggle button click
     * Will update stored AdvancedSearchSettings object
     * @param e
     */
    @FXML protected void handleIncludeIdeasAction(ActionEvent e){
        advancedSearchSettings.setIncludeIdeas(includeIdeasToggle.isSelected());
    }

    /**
     * Called on IncludeStudyPlans toggle button click
     * Will update stored AdvancedSearchSettings object
     * @param e
     */
    @FXML protected void handleIncludeStudyPlansAction(ActionEvent e){
        advancedSearchSettings.setIncludeStudyPlans(includeStudyPlansToggle.isSelected());
    }

    Controller controller;

    public void initialize(){

        model = Model.getInstance();
        controller = Controller.getInstance();
        controller.setMainWindowController(this);

        String notesDirectory = "/Users/faithnyota1/NoteBankFX/src/notes";
        try {

            /* Assumes Subject.xml, Topics.xml and Notes are all stored in the same directory */
            // @TODO: Need to create a workspace dialog - that let's user change/input their workspace
            model.initialise(notesDirectory, notesDirectory);
        } catch (Exception e) {
            // Can't be bothered to list the 3+ exceptions
        }

        model.refreshSubjects();

        /* Instantiate AdvancedSearchSettings and set up UI to match default settings */
        advancedSearchSettings = new AdvancedSearchSettings();
        setDefaultAdvanceSearchSettings();

        /**
         * SubjectList refers to the list of subjects on the left-hand navigation panel
         * When you add a new subject, you need to be able to edit the default subject name
         * Below adds a cell factor that will add TextField type ListCells to the ListView
         */
        this.subjectList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                TextFieldListCell<String> cell = new TextFieldListCell<>(
                        new StringConverter<String>() {
                            @Override
                            public String toString(String object) {
                                return object;
                            }

                            @Override
                            public String fromString(String string) {
                                return string;
                            }
                        }
                );

                /* When a user finishes the edit, the code below will be able to detect that and will update the Subject
                   within the model.
                   @TODO: Problem with editing All that requires fixing: Need to make all exempt from editng
                 */

                cell.editingProperty().addListener((observable, oldValue, newValue) -> {

                    if(oldValue==true && newValue==false){ //implies user has just finished editing this TextFieldCel

                        String newName = subjectList.getItems().get(lastEditingIndex);
                        String oldName = currentlyEditingSubject;

                        //System.out.println("\n\n  Just finished editing " + oldName + " " + newName  + "\n\n ");
                        if(!model.subjectExists(oldName)){
                            model.addSubject(newName, new ArrayList<>());
                        }else{
                            model.updateSubject(oldName,newName,null);
                        }

                        newSubject.setDisable(false);


                    }else if(oldValue==false && newValue==true){ //implies user has just started editing this TextFieldCell

                        currentlyEditingSubject = subjectList.getItems().get(subjectList.getEditingIndex());
                        lastEditingIndex = subjectList.getEditingIndex();
                        newSubject.setDisable(true);
                        //System.out.println("\n\n  Just started editing " + currentlyEditingSubject + " " + lastEditingIndex  + "\n\n ");

                    }
                });

                cell.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if(menu!=null)
                            menu.hide();

                        if(event.getButton() == MouseButton.SECONDARY){
                            cell.getStylesheets().add("/Code/View/css/context-menu.css");
                            menu = new SubjectRightClickMenu(cell.getItem(),cell);
                            menu.show(cell,event.getScreenX(),event.getScreenY());
                        }
                    }
                });

                return cell;
            }
        });

        loadPanes(); // Will instantiate all required Pages

        this.subjectList.setEditable(true);

        model.addRefreshSubjectsController(this);

        refreshSubjects();
        model.refreshSubjects();
        this.subjectList.getSelectionModel().selectFirst(); // First should always be All



        /*
        Will update model based on new Subject selection in subjectList
         */
        this.subjectList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {

            if(!newValue.equals(oldValue)){
                model.setCurrentSubject(this.subjectList.getSelectionModel().getSelectedItem());
                model.refreshSubjects();
            }

        });


        search.textProperty().addListener((obs, oldText, newText) ->{
            //Search new text amongst all data in program
            //Display in listview in searchlist
            if(!newText.equals(oldText))
                searchlistpane.setVisible(newText.length()>0);
        });

        closeOuterWindows(); // e.g. CreateIdea, CreateNote

        /**
         * Monitor visibility property for outer windows to disable/enable mainwindow when required
         */

        enableAndShowMainWindowOnClosingNode(createIdea);
        enableAndShowMainWindowOnClosingNode(createNote);
        enableAndShowMainWindowOnClosingNode(advancedSearch);
        enableAndShowMainWindowOnClosingNode(viewNote);


        lastQuizMenu.setVisible(false);


        this.numberOfSubjects = this.subjectList.getItems().size();

        setPage(Page.HomeFeatured);


        Timeline timer = new Timeline(new KeyFrame(Duration.seconds(240), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                model.backup();
            }
        }));

        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();


    }

    ContextMenu menu;


    private void enableAndShowMainWindowOnClosingNode(Node node){
        node.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=oldValue)
                handleOuterDialogVisibilityChange(oldValue,newValue);
        });
    }

    private String currentlyEditingSubject = null;
    private int lastEditingIndex = -1;


    /**
     * Designed for visibility property: oldValue, newValue
     * Will disable the mainwindow when visibility property is no longer false
     * Will enable the mainWindow when visibility property is no longer true
     * @param oldValue
     * @param newValue
     */
    private void handleOuterDialogVisibilityChange(boolean oldValue, boolean newValue){
        if(oldValue==true && newValue==false){
            app.setDisable(false);
        }else if(oldValue==false && newValue==true){
            app.setDisable(true);
        }
    }

    /**
     * Load/Instantiate all pages required
     */
    public void loadPanes(){
        try{
            homeFeaturedPanel = new FXMLLoader().load(getClass().getResource("/Code/View/menus/home/featured.fxml"));
            homeNotesPanel = new FXMLLoader().load(getClass().getResource("/Code/View/menus/home/notes.fxml"));
            ideasIdeaPanel = new FXMLLoader().load(getClass().getResource("/Code/View/menus/ideas/idea.fxml"));
            ideasMindMapPanel = new FXMLLoader().load(getClass().getResource("/Code/View/menus/ideas/mindmap.fxml"));
            ideasOverviewPanel = new FXMLLoader().load(getClass().getResource("/Code/View/menus/ideas/overview.fxml"));
            studyOverviewPanel = new FXMLLoader().load(getClass().getResource("/Code/View/menus/study/overview.fxml"));
            studyFocusPanel = new FXMLLoader().load(getClass().getResource("/Code/View/menus/study/focus.fxml"));
            studyIdeasPanel = new FXMLLoader().load(getClass().getResource("/Code/View/menus/study/ideas.fxml"));
            studyLastQuizPanel = new FXMLLoader().load(getClass().getResource("/Code/View/menus/study/lastQuiz.fxml"));
            quizPanel = new FXMLLoader().load(getClass().getResource("/Code/View/menus/study/quiz.fxml"));

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /*********************************************
     *                                           *
     *  CONTROLLING NAVIGATION PANEL COMPONENTS  *
     *                                           *
     *********************************************/

    private void setVisibleHomeMenu(boolean featured, boolean notes){
        homeFeaturedPane.setVisible(featured);
        homeNotesPane.setVisible(notes);
        homeFeatured.setDisable(featured);
        homeNotes.setDisable(notes);
    }
    private void setVisibleIdeasMenu(boolean idea, boolean mindmap, boolean overview){
        ideasIdeaPane.setVisible(idea);
        ideasMindMapPane.setVisible(mindmap);
        ideasOverviewPane.setVisible(overview);
        ideasIdea.setDisable(idea);
        ideasMindMap.setDisable(mindmap);
        ideasOverview.setDisable(overview);
    }
    private void setVisibleStudyMenu(boolean focus, boolean ideas, boolean overview, boolean lastQuiz){
        studyIdeasPane.setVisible(ideas);
        studyFocusPane.setVisible(focus);
        studyOverviewPane.setVisible(overview);
        studyLastQuizPane.setVisible(lastQuiz);

        studyIdeas.setDisable(ideas);
        studyFocus.setDisable(focus);
        studyOverview.setDisable(overview);
        studyLastQuiz.setDisable(lastQuiz);

    }
    private void setMenuSelection(boolean home, boolean ideas, boolean study){
        homePane.setVisible(home);
        this.home.setDisable(home);
        this.ideas.setDisable(ideas);
        this.study.setDisable(study);

        ideasPane.setVisible(ideas);
        studyPane.setVisible(study);

        homeMenu.setVisible(home);
        ideasMenu.setVisible(ideas);
        studyMenu.setVisible(study);

        if(study) mainWindowHeader.setText("Study");
        if(ideas) mainWindowHeader.setText("Ideas");
        if(home) mainWindowHeader.setText("Home");


    }

    private void disableAllMenus(boolean disable){
        leftNavigation.setDisable(disable);
    }


    /**
     * Called on home menu button click
     * @param e
     */
    @FXML protected void handleHomeClick(ActionEvent e){
        setPage(Page.HomeFeatured);
        ideasIdea.setDisable(true);
    }
    @FXML void handleIdeasClick(ActionEvent e){
        setPage(Page.IdeasOverview);
        ideasIdea.setDisable(true);

    }
    @FXML void handleStudyClick(ActionEvent e){
        setPage(Page.StudyFocus);
        ideasIdea.setDisable(true);
    }
    @FXML protected void handleHomeFeaturedClick(ActionEvent e){
        setPage(Page.HomeFeatured);
        ideasIdea.setDisable(true);
    }
    @FXML protected void handleHomeNotesClick(ActionEvent e){
        setPage(Page.HomeNotes);
        ideasIdea.setDisable(true);
    }
    @FXML protected void handleIdeasOverviewClick(ActionEvent e){
        setPage(Page.IdeasOverview);
        ideasIdea.setDisable(true);
    }
    @FXML protected void handleIdeasMindMapClick(ActionEvent e){
        setPage(Page.IdeasMindMap);
        ideasIdea.setDisable(true);
    }
    @FXML protected void handleIdeasIdeaClick(ActionEvent e){
        setPage(Page.IdeasIdea);
        ideasIdea.setDisable(true);
    }
    @FXML protected void handleStudyOverviewClick(ActionEvent e){
        setPage(Page.StudyOverview);
        ideasIdea.setDisable(true);
    }
    @FXML protected void handleStudyFocusClick(ActionEvent e){
        setPage(Page.StudyFocus);
        ideasIdea.setDisable(true);
    }
    @FXML protected void handleStudyIdeasClick(ActionEvent e){
        setPage(Page.StudyIdeas);
        ideasIdea.setDisable(true);
    }
    @FXML protected void handleStudyLastQuizClick(ActionEvent e){
        setPage(Page.StudyLastQuiz);
        ideasIdea.setDisable(true);
    }

    /********** Finish Navigation Panels *********/

    /**
     * Set current page to input and handles navigation panel accordingly
     * @param page
     */
    public void setPage(Page page){
        switch (page){
            case HomeNotes:
                mainWindow.setCenter(homeNotesPanel);
                quizProgress.setVisible(false);
                disableAllMenus(false);
                setVisibleHomeMenu(false,true);
                setMenuSelection(true,false,false);
                break;
            case HomeFeatured:
                homeFeaturedPanel.setVisible(true);
                mainWindow.setCenter(homeFeaturedPanel);
                quizProgress.setVisible(false);
                disableAllMenus(false);
                setVisibleHomeMenu(true,false);
                setMenuSelection(true,false,false);
                break;
            case IdeasIdea:
                mainWindow.setCenter(ideasIdeaPanel);
                quizProgress.setVisible(false);
                disableAllMenus(false);
                setVisibleIdeasMenu(true,false,false);
                setMenuSelection(false,true,false);
                break;
            case IdeasMindMap:
                mainWindow.setCenter(ideasMindMapPanel);
                quizProgress.setVisible(false);
                disableAllMenus(false);
                setVisibleIdeasMenu(false,true,false);
                setMenuSelection(false,true,false);
                break;
            case IdeasOverview:
                mainWindow.setCenter(ideasOverviewPanel);
                quizProgress.setVisible(false);
                disableAllMenus(false);
                setVisibleIdeasMenu(false,false,true);
                setMenuSelection(false,true,false);
                break;
            case StudyFocus:
                mainWindow.setCenter(studyFocusPanel);
                quizProgress.setVisible(false);
                disableAllMenus(false);
                setVisibleStudyMenu(true,false,false,false);
                setMenuSelection(false,false,true);
                break;
            case StudyIdeas:
                mainWindow.setCenter(studyIdeasPanel);
                quizProgress.setVisible(false);
                disableAllMenus(false);
                setVisibleStudyMenu(false,true,false,false);
                setMenuSelection(false,false,true);
                break;
            case StudyOverview:
                mainWindow.setCenter(studyOverviewPanel);
                //mainWindow.setCenter(quizPanel);
                quizProgress.setVisible(false);
                disableAllMenus(false);
                setVisibleStudyMenu(false,false,true,false);
                setMenuSelection(false,false,true);
                break;
            case StudyLastQuiz:
                lastQuizMenu.setVisible(true);
                mainWindow.setCenter(studyLastQuizPanel);
                quizProgress.setVisible(false);
                disableAllMenus(false);
                setVisibleStudyMenu(false,false,false,true);
                setMenuSelection(false,false,true);
                break;
            case QuizPage:
                mainWindow.setCenter(quizPanel);
                this.mainWindowHeader.setText("Quiz");
                homeMenu.setVisible(false);
                ideasMenu.setVisible(false);
                studyMenu.setVisible(false);
                disableAllMenus(true);
                quizProgress.setVisible(true);
                setVisibleStudyMenu(false,false,false,false);
                setMenuSelection(false,false,false);
                break;
        }
    }

    private int numberOfSubjects;

    /**
     * Called on New Subject button click
     * Will add default name (Subject x) to the listview and will start edit
     * @param e
     */
    @FXML protected void handleNewSubjectAction(ActionEvent e){
        this.subjectList.getItems().add("Subject " + (numberOfSubjects+1));
        this.subjectList.scrollTo(this.subjectList.getItems().size()-1);
        this.subjectList.layout();
        this.subjectList.edit(this.subjectList.getItems().size()-1);
        this.numberOfSubjects+=1;
    }

    /**
     * Set hover icon for Settings
     * @TODO: Replace handleSettingsEnter code with CSS
     * @param e
     */
    @FXML protected void handleSettingsEnter(MouseEvent e){
        Image setting = new Image(getClass().getResourceAsStream("../View/Icons/hoversettings.png"));
        settings.setGraphic(new ImageView(setting));
    }

    /**
     * Set hover icon for Settings
     * @TODO: Replace handleSettingsExit code with CSS
     * @param e
     */
    @FXML protected void handleSettingsExit(MouseEvent e){
        Image setting = new Image(getClass().getResourceAsStream("../View/Icons/settings.png"));
        settings.setGraphic(new ImageView(setting));
    }

    /**
     * Called on Settings click
     * Shows advancedSearch pane
     * @param e
     */
    @FXML protected void handleSettingsClick(ActionEvent e){
        advancedSearch.setVisible(true);
        app.setDisable(true);
    }

    /**
     * Closes all outer windows e.g. createNote, createIdea, ...
     */
    public void closeOuterWindows(){
        createNote.setVisible(false);
        createIdea.setVisible(false);
        advancedSearch.setVisible(false);
        searchlistpane.setVisible(false);
        viewNote.setVisible(false);
        customQuiz.setVisible(false);
        createstudysession.setVisible(false);
        createstudyplan.setVisible(false);

    }

    /**
     * Called on AdvancedSearch close button click
     * Will hide advancedSearch menu
     * @param e
     */
    @FXML void handleAdvancedSearchCloseAction(ActionEvent e){
        advancedSearch.setVisible(false);
    }

    /**
     * Called when main window is clicked
     * Close all outer windows
     * @param e
     */
    @FXML protected void handleAppMouseClick(MouseEvent e){
        closeOuterWindows();
    }

    /**
     * Called when mouse leaves searchList
     * Exits search list
     * @param e
     */
    @FXML protected void handleSearchListExit(MouseEvent e){
        search.setText("");
        searchlistpane.setVisible(false);
    }

    /**
     * Called when New Note button is clicked
     * Will show a create note pane
     * @param e
     */
    @FXML protected void onNewNoteAction(ActionEvent e){
        closeOuterWindows();
        controller.createNote();
    }

    /**
     * Called when New Idea button is clicked
     * Will show a new idea pane
     * @param e
     */
    @FXML protected void handleNewIdeaAction(ActionEvent e){
        closeOuterWindows();
        controller.createIdea();
    }


    @FXML protected void handleCustomQuizAction(ActionEvent e){
        closeOuterWindows();
        controller.setupQuiz();
    }

    @FXML protected void handleStudySession(ActionEvent e){
        closeOuterWindows();
        controller.createStudySession();
    }

    public void disableSubjectList(boolean disable){
        this.subjectList.setDisable(disable);
    }

    /**
     *  Will refresh subject list and select current subject
     */
    @Override
    public void refreshSubjects() {
        subjectList.setItems(FXCollections.observableArrayList(model.getSubjectStrings()));
        this.subjectList.getSelectionModel().select(model.getCurrentSubject().getName());
    }




}


class SubjectRightClickMenu extends ContextMenu{

    public SubjectRightClickMenu(String subject, Node node) {

        this.getStyleClass().add("amenu");
        MenuItem remove = new MenuItem("Remove");
        remove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Model model = Model.getInstance();
                Subject sub =model.getSubject(subject);
                if(sub.getName().equals("All")){
                    View.displayPopUpForTime("Deletion Disabled","Cant delete the this subject",2,node,250);
                    return;
                }
                model.remove(sub);
            }
        });

        this.getItems().add(remove);
    }
}
