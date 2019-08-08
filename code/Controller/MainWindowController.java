package Code.Controller;

import Code.Model.Model;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

    public void setDefaultAdvanceSearchSettings(){
        inPageToggle.setSelected(false);
        includeBooksToggle.setSelected(true);
        includeIdeasToggle.setSelected(true);
        includeImagesToggle.setSelected(true);
        includeStudyPlansToggle.setSelected(true);
        includeTextsToggle.setSelected(true);
    }


    @FXML protected void handleSearchInPageAction(ActionEvent e){
        advancedSearchSettings.setSearchInPage(inPageToggle.isSelected());
    }

    @FXML protected void handleIncludeImagesAction(ActionEvent e){
        advancedSearchSettings.setIncludeImages(includeImagesToggle.isSelected());
    }

    @FXML protected void handleIncludeTextsAction(ActionEvent e){
        advancedSearchSettings.setIncludeTexts(includeTextsToggle.isSelected());
    }

    @FXML protected void handleIncludeBooksAction(ActionEvent e){
        advancedSearchSettings.setIncludeBooks(includeBooksToggle.isSelected());
    }

    @FXML protected void handleIncludeIdeasAction(ActionEvent e){
        advancedSearchSettings.setIncludeIdeas(includeIdeasToggle.isSelected());
    }

    @FXML protected void handleIncludeStudyPlansAction(ActionEvent e){
        advancedSearchSettings.setIncludeStudyPlans(includeStudyPlansToggle.isSelected());
    }


    public void initialize(){
        model = Model.getInstance();

        String notesDirectory = "/Users/faithnyota1/NoteBankFX/src/notes";
        try {
            model.initialise(notesDirectory, notesDirectory, notesDirectory);
        } catch (Exception e) {
        }
        model.refreshSubjects();
        advancedSearchSettings = new AdvancedSearchSettings();
        setDefaultAdvanceSearchSettings();



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

                cell.editingProperty().addListener((observable, oldValue, newValue) -> {

                    if(oldValue==true && newValue==false){

                        if(!model.subjectExists(currentlyEditingSubject)){
                            model.addSubject(subjectList.getItems().get(lastEditingIndex), new ArrayList<>());
                        }else{
                            model.updateSubject(currentlyEditingSubject,subjectList.getItems().get(lastEditingIndex),null);
                        }

                        newSubject.setDisable(false);

                    }else if(oldValue==false && newValue==true){
                        currentlyEditingSubject = subjectList.getItems().get(subjectList.getEditingIndex());
                        lastEditingIndex = subjectList.getEditingIndex();
                        newSubject.setDisable(true);

                    }
                });

                return cell;
            }
        });

        this.subjectList.setEditable(true);

        model.addRefreshSubjectsController(this);

        model.refreshSubjects();
        this.subjectList.getSelectionModel().selectFirst();


        this.subjectList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            model.setCurrentSubject(this.subjectList.getSelectionModel().getSelectedItem());
            model.refreshSubjects();
        });



        loadPanes();

        search.textProperty().addListener((obs, oldText, newText) ->{
            //Search new text amongst all data in program
            //Display in listview in searchlist
            searchlistpane.setVisible(newText.length()>0);
        });

        closeOuterWindows();

        createIdea.visibleProperty().addListener((observable, oldValue, newValue) -> {
            handleOuterDialogVisibilityChange(oldValue,newValue);
        });

        createNote.visibleProperty().addListener((observable, oldValue, newValue) -> {
            handleOuterDialogVisibilityChange(oldValue,newValue);
        });

        advancedSearch.visibleProperty().addListener((observable, oldValue, newValue) -> {
            handleOuterDialogVisibilityChange(oldValue,newValue);
        });


        this.numberOfSubjects = this.subjectList.getItems().size();
    }

    String currentlyEditingSubject = null;
    int lastEditingIndex = -1;

    private void handleOuterDialogVisibilityChange(boolean oldValue, boolean newValue){
        if(oldValue==true && newValue==false){
            app.setDisable(false);
        }else if(oldValue==false && newValue==true){
            app.setDisable(true);
        }
    }

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
        }
    }

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

    int numberOfSubjects;

    @FXML protected void handleNewSubjectAction(ActionEvent e){

        this.subjectList.getItems().add("Subject " + (numberOfSubjects+1));
        this.subjectList.scrollTo(this.subjectList.getItems().size()-1);
        this.subjectList.layout();
        this.subjectList.edit(this.subjectList.getItems().size()-1);
        this.numberOfSubjects+=1;
    }


    void setToggleOnGraphic(Button button){
        Image toggle = new Image(getClass().getResourceAsStream("../View/Icons/toggleOn.png"));
        button.setGraphic(new ImageView(toggle));
    }

    void setToggleOffGraphic(Button button){
        Image toggle = new Image(getClass().getResourceAsStream("../View/Icons/toggleOff.png"));
        button.setGraphic(new ImageView(toggle));
    }

    @FXML protected void handleSettingsEnter(MouseEvent e){
        Image setting = new Image(getClass().getResourceAsStream("../View/Icons/hoversettings.png"));
        settings.setGraphic(new ImageView(setting));
    }

    @FXML protected void handleSettingsExit(MouseEvent e){
        Image setting = new Image(getClass().getResourceAsStream("../View/Icons/settings.png"));
        settings.setGraphic(new ImageView(setting));
    }

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

    @FXML protected void handleSettingsClick(ActionEvent e){
        advancedSearch.setVisible(true);
        app.setDisable(true);
    }

    public void closeOuterWindows(){
        createNote.setVisible(false);
        createIdea.setVisible(false);
        advancedSearch.setVisible(false);
        searchlistpane.setVisible(false);
    }

    @FXML void handleAdvancedSearchCloseAction(ActionEvent e){
        advancedSearch.setVisible(false);
    }

    @FXML protected void handleAppMouseClick(MouseEvent e){
        closeOuterWindows();
    }

    @FXML protected void handleSearchListExit(MouseEvent e){
        search.setText("");
        searchlistpane.setVisible(false);
    }

    @FXML protected void onNewNoteAction(ActionEvent e){
        closeOuterWindows();
        createNote.setVisible(true);
    }

    @FXML protected void handleNewIdeaAction(ActionEvent e){
        closeOuterWindows();
        createIdea.setVisible(true);
    }

    @Override
    public void refreshSubjects() {
        subjectList.setItems(FXCollections.observableArrayList(model.getSubjectStrings()));
        this.subjectList.getSelectionModel().select(model.getCurrentSubject().getName());

    }
}
