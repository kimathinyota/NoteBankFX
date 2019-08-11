package Code.Controller;

import Code.Model.Model;
import javafx.beans.binding.When;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.StringConverter;

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
    @FXML protected Button studyOverview;
    @FXML protected Button studyFocus;
    @FXML protected Button studyIdeas;

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
    @FXML protected Button newNote;
    @FXML protected Button newSubject;

    @FXML protected ToggleButton inPageToggle;
    @FXML protected ToggleButton includeImagesToggle;
    @FXML protected ToggleButton includeTextsToggle;
    @FXML protected ToggleButton includeBooksToggle;
    @FXML protected ToggleButton includeIdeasToggle;
    @FXML protected ToggleButton includeStudyPlansToggle;
    @FXML protected ListView<String> subjectList;

    Pane homeFeaturedPanel, homeNotesPanel;
    Pane ideasIdeaPanel, ideasMindMapPanel, ideasOverviewPanel;
    Pane studyOverviewPanel, studyFocusPanel, studyIdeasPanel;

    Model model;

    AdvancedSearchSettings advancedSearchSettings;

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
            model.initialise(notesDirectory, notesDirectory, notesDirectory);
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

                        System.out.println("\n\n  Just finished editing " + oldName + " " + newName  + "\n\n ");
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
                        System.out.println("\n\n  Just started editing " + currentlyEditingSubject + " " + lastEditingIndex  + "\n\n ");

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
        createIdea.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=oldValue)
                handleOuterDialogVisibilityChange(oldValue,newValue);
        });

        createNote.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=oldValue)
                handleOuterDialogVisibilityChange(oldValue,newValue);
        });

        advancedSearch.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=oldValue)
                handleOuterDialogVisibilityChange(oldValue,newValue);
        });


        this.numberOfSubjects = this.subjectList.getItems().size();
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
            homeFeaturedPanel = new FXMLLoader().load(getClass().getResource("../View/menus/home/featured.fxml"));
            homeNotesPanel = new FXMLLoader().load(getClass().getResource("../View/menus/home/notes.fxml"));
            ideasIdeaPanel = new FXMLLoader().load(getClass().getResource("../View/menus/ideas/idea.fxml"));
            ideasMindMapPanel = new FXMLLoader().load(getClass().getResource("../View/menus/ideas/mindmap.fxml"));
            ideasOverviewPanel = new FXMLLoader().load(getClass().getResource("../View/menus/ideas/overview.fxml"));
            studyOverviewPanel = new FXMLLoader().load(getClass().getResource("../View/menus/study/overview.fxml"));
            studyFocusPanel = new FXMLLoader().load(getClass().getResource("../View/menus/study/focus.fxml"));
            studyIdeasPanel = new FXMLLoader().load(getClass().getResource("../View/menus/study/ideas.fxml"));

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
    private void setVisibleStudyMenu(boolean focus, boolean ideas, boolean overview){
        studyIdeasPane.setVisible(ideas);
        studyFocusPane.setVisible(focus);
        studyOverviewPane.setVisible(overview);
        studyIdeas.setDisable(ideas);
        studyFocus.setDisable(focus);
        studyOverview.setDisable(overview);
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

    /**
     * Called on Home menu button click
     * @param e
     */
    @FXML protected void handleHomeClick(ActionEvent e){
        setPage(Page.HomeFeatured);
    }
    @FXML void handleIdeasClick(ActionEvent e){
        setPage(Page.IdeasOverview);

    }
    @FXML void handleStudyClick(ActionEvent e){
        setPage(Page.StudyFocus);

    }
    @FXML protected void handleHomeFeaturedClick(ActionEvent e){
        setPage(Page.HomeFeatured);
    }
    @FXML protected void handleHomeNotesClick(ActionEvent e){
        setPage(Page.HomeNotes);
    }
    @FXML protected void handleIdeasOverviewClick(ActionEvent e){
        setPage(Page.IdeasOverview);
    }
    @FXML protected void handleIdeasMindMapClick(ActionEvent e){
        setPage(Page.IdeasMindMap);
    }
    @FXML protected void handleIdeasIdeaClick(ActionEvent e){
        setPage(Page.IdeasIdea);
    }
    @FXML protected void handleStudyOverviewClick(ActionEvent e){
        setPage(Page.StudyOverview);
    }
    @FXML protected void handleStudyFocusClick(ActionEvent e){
        setPage(Page.StudyFocus);
    }
    @FXML protected void handleStudyIdeasClick(ActionEvent e){
        setPage(Page.StudyIdeas);
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
                setVisibleHomeMenu(false,true);
                setMenuSelection(true,false,false);
                break;
            case HomeFeatured:
                mainWindow.setCenter(homeFeaturedPanel);
                setVisibleHomeMenu(true,false);
                setMenuSelection(true,false,false);
                break;
            case IdeasIdea:
                mainWindow.setCenter(ideasIdeaPanel);
                setVisibleIdeasMenu(true,false,false);
                setMenuSelection(false,true,false);
                break;
            case IdeasMindMap:
                mainWindow.setCenter(ideasMindMapPanel);
                setVisibleIdeasMenu(false,true,false);
                setMenuSelection(false,true,false);
                break;
            case IdeasOverview:
                mainWindow.setCenter(ideasOverviewPanel);
                setVisibleIdeasMenu(false,false,true);
                setMenuSelection(false,true,false);
                break;
            case StudyFocus:
                mainWindow.setCenter(studyFocusPanel);
                setVisibleStudyMenu(true,false,false);
                setMenuSelection(false,false,true);
                break;
            case StudyIdeas:
                mainWindow.setCenter(studyIdeasPanel);
                setVisibleStudyMenu(false,true,false);
                setMenuSelection(false,false,true);
                break;
            case StudyOverview:
                mainWindow.setCenter(studyOverviewPanel);
                setVisibleStudyMenu(false,false,true);
                setMenuSelection(false,false,true);
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
        createNote.setVisible(true);
    }

    /**
     * Called when New Idea button is clicked
     * Will show a new idea pane
     * @param e
     */
    @FXML protected void handleNewIdeaAction(ActionEvent e){
        closeOuterWindows();
        createIdea.setVisible(true);
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
