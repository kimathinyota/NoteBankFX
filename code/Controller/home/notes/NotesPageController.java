package Code.Controller.home.notes;

import Code.Controller.*;
import Code.Controller.Dialogs.ViewNotes.ViewMode;
import Code.Controller.RefreshInterfaces.RefreshNotesController;
import Code.Controller.RefreshInterfaces.RefreshSubjectsController;
import Code.Controller.home.notes.filters.FilterSettings;
import Code.Controller.home.notes.filters.Order;
import Code.Controller.home.notes.filters.SortBy;
import Code.Controller.home.notes.filters.View;
import Code.Model.Model;
import Code.Model.Note;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

/**
 * NotesPageController
 *
 */
public class NotesPageController implements RefreshNotesController, RefreshSubjectsController {
    @FXML protected Button openFilter;
    @FXML protected Pane filter;
    @FXML protected Pane filterNoSearch;

    @FXML protected ListView<Note> underusedNotes;
    @FXML protected ListView<Note> utilisedNotes;
    @FXML protected ListView<Note> allNotes;

    @FXML protected ChoiceBox<String> sortBy;
    @FXML protected ChoiceBox<String> order;
    @FXML protected ChoiceBox<String> view;

    @FXML protected Label subjectLine;
    @FXML protected ToggleButton includeBooks;
    @FXML protected ToggleButton includeImages;
    @FXML protected ToggleButton includeTexts;

    @FXML protected TextField search;

    @FXML protected Button rareNotesDisplay;
    @FXML protected Button usedNotesDisplay;
    @FXML protected Button allNotesDisplay;

    private Model model;
    private Controller controller;

    private FilterSettings filterSettings;

    private IntegerValue allIndex = new IntegerValue(-1);
    private IntegerValue underusedIndex = new IntegerValue(-1);
    private IntegerValue utilisedIndex = new IntegerValue(-1);

    public void setFilterInclude(boolean includeBooks, boolean includeImages, boolean includeTexts){
        this.includeBooks.setSelected(includeBooks);
        this.includeImages.setSelected(includeImages);
        this.includeTexts.setSelected(includeTexts);
    }

    public void setFilterChoices(SortBy sortBy, Order order, View view){
        this.sortBy.getSelectionModel().select(sortBy.name());
        this.order.getSelectionModel().select(order.name());
        this.view.getSelectionModel().select(view.name());
    }

    @FXML protected void handleSearchAction(ActionEvent e){
        this.filterSettings.setSearch(search.getText());
        refreshSubjects();
        this.search.setText("");
        this.search.requestFocus();
    }


    Label label;

    public void initialize(){
        filterSettings = new FilterSettings();
        controller = Controller.getInstance();

        underusedNotes.setCellFactory(new NoteIconCellFactory(underusedNotes));
        underusedNotes.setOrientation(Orientation.HORIZONTAL);

        allNotes.setCellFactory(new NoteIconCellFactory(allNotes));
        allNotes.setOrientation(Orientation.HORIZONTAL);

        utilisedNotes.setCellFactory(new NoteIconCellFactory(utilisedNotes));
        utilisedNotes.setOrientation(Orientation.HORIZONTAL);

        setFilterInclude(true,true,true);
        setFilterChoices(SortBy.Name,Order.Asc,View.Icons);

        filter.setVisible(false);
        sortBy.getItems().setAll(SortBy.Date.name(),SortBy.Name.name());
        sortBy.getSelectionModel().select(SortBy.Name.name());
        order.getItems().setAll(Order.Asc.name(),Order.Desc.name());
        order.getSelectionModel().select(0);
        view.getItems().setAll(View.Icons.name(),View.Text.name());
        view.getSelectionModel().select(View.Icons.name());
        model = Model.getInstance();
        model.addRefreshSubjectsController(this);
        model.addRefreshNotesController(this);



        refreshSubjects();




        Code.View.View.setUpListForArrowManipulation(underusedNotes,underusedIndex);
        Code.View.View.setUpListForArrowManipulation(utilisedNotes,utilisedIndex);
        Code.View.View.setUpListForArrowManipulation(allNotes,allIndex);








        rareNotesDisplay.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.displayNotes(underusedNotes.getItems(), ViewMode.ViewFull);
            }
        });

        usedNotesDisplay.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.displayNotes(utilisedNotes.getItems(), ViewMode.ViewFull);
            }
        });

        allNotesDisplay.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.displayNotes(allNotes.getItems(), ViewMode.ViewFull);
            }
        });


        search.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("") && !newValue.equals(oldValue)){
                filterSettings.setSearch("");
                refreshSubjects();
            }
        });

        label = new Label();
        label.textProperty().bind(search.textProperty());
        label.textProperty().addListener((observable, oldValue, newValue) -> {
            filterSettings.setSearch(label.getText());
            refreshSubjects();
        });


        sortBy.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filterSettings.setSortBy(SortBy.valueOf(newValue));
            refreshSubjects();
        });
        order.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filterSettings.setOrder(Order.valueOf(newValue));
            refreshSubjects();
        });
        view.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filterSettings.setView(View.valueOf(newValue));
            if(!oldValue.equals(newValue)){
                if(newValue.equals(View.Text.name())){
                    changeToTextDisplay(allNotes);
                    changeToTextDisplay(underusedNotes);
                    changeToTextDisplay(utilisedNotes);
                }else{
                    changeToIconDisplay(allNotes);
                    changeToIconDisplay(underusedNotes);
                    changeToIconDisplay(utilisedNotes);
                }
                refreshSubjects();
            }
        });




    }



    @FXML protected void handleSearchEnterPress(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            filterSettings.setSearch(search.getText());
            refreshSubjects();
        }
    }



    public void setIndex(IntegerValue i, int val){
        i.setInteger(val);
    }

    public void changeToIconDisplay(ListView<Note> allNotes){
        allNotes.setCellFactory(new NoteIconCellFactory(allNotes));
        allNotes.setOrientation(Orientation.HORIZONTAL);
    }

    public void changeToTextDisplay(ListView<Note> allNotes){
        allNotes.setCellFactory(new NoteTextCellFactory(allNotes));
        allNotes.setOrientation(Orientation.HORIZONTAL);
    }

    @FXML protected void handleIncludeBooksAction(ActionEvent e){
        filterSettings.setIncludeBooks(includeBooks.isSelected());
        refreshSubjects();
    }

    @FXML protected void handleIncludeImagesAction(ActionEvent e){
        filterSettings.setIncludeImages(includeImages.isSelected());
        refreshSubjects();
    }

    @FXML protected void handleIncludeTextsAction(ActionEvent e){
        filterSettings.setIncludeTexts(includeTexts.isSelected());
        refreshSubjects();
    }

    @FXML protected void handleRightClick(ActionEvent e){
        Code.View.View.handleRightClick(underusedNotes,underusedIndex);
    }

    @FXML protected void handleLeftClick(ActionEvent e){
        Code.View.View.handleLeftClick(underusedNotes,underusedIndex);
    }

    @FXML protected void handleRight2Click(ActionEvent e){
        Code.View.View.handleRightClick(utilisedNotes,utilisedIndex);
    }

    @FXML protected void handleLeft2Click(ActionEvent e){
        Code.View.View.handleLeftClick(utilisedNotes,utilisedIndex);
    }

    @FXML protected void handleRight3Click(ActionEvent e){
        Code.View.View.handleRightClick(allNotes,allIndex);
    }

    @FXML protected void handleLeft3Click(ActionEvent e){
        Code.View.View.handleLeftClick(allNotes,allIndex);
    }



    @FXML protected void handleOpenFilterAction(ActionEvent e){
        filter.setVisible(true);
    }

    @FXML protected void handleCloseFilterAction(ActionEvent e){
        filter.setVisible(false);
    }

    @Override
    public void refreshNotes() {
        refreshSubjects();
    }

    @Override
    public void refreshSubjects() {

        subjectLine.setText(model.getCurrentSubject().getName());

        try{


            /**
             * SO what is going on here:
             * Well i created a set of tasks to load all of the notes since it has the potential to require a large amount of processing (e.g. for 600 notes)
             * JAVAFX is really weird: all GUI objects used within the application can only be accessed by JavaFX application thread making concurrency difficult:
             * You have to use these Task<?> classes, and bind the event of task succeeding to a set of actions allowing for thread-safe concurrency
             * Tasks will then have to be executed and then shutdown by an ExecutorService object
             * Why is it nested ?
             *  -> Task 1 => used to load x to allNotes listview, where x = all notes for the current subject in order (FilterService)
             *  -> Task 2 => used to load y to underusedNotes listview , where y =  the notes in x that are "rarely used " (must follow from Task 1)
             *  -> Task 3 => loads list z to highlyUsed listview, where z = x - y (set minus) (must follow from Task 2 and Task 1)
             */

            AllNotesListTask task = new AllNotesListTask(filterSettings);
            allNotes.setItems(task.getValue());

            task.setOnRunning( (runningAllNotes) -> {
                filterNoSearch.setDisable(true);
                controller.disable(AppFeatures.SubjectList,true);
            });

            task.setOnSucceeded((completedEvent) -> {
                allNotes.setItems(task.getValue());
                filterNoSearch.setDisable(false);
                controller.disable(AppFeatures.SubjectList,false);

                UnderusedNotesListTask task2 = new UnderusedNotesListTask(filterSettings,task.getValue());

                task2.setOnRunning( (runningUnderusedNotes) -> {
                    filterNoSearch.setDisable(true);
                    controller.disable(AppFeatures.SubjectList,true);
                });

                task2.setOnSucceeded(event -> {

                    filterNoSearch.setDisable(false);
                    controller.disable(AppFeatures.SubjectList,false);
                    underusedNotes.setItems(task2.getValue());

                    UtilisedNotesListTask task3 = new UtilisedNotesListTask(filterSettings,task2.getValue(),task.getValue());

                    task3.setOnRunning( (runningUsedNotes) -> {
                        filterNoSearch.setDisable(true);
                        controller.disable(AppFeatures.SubjectList,true);
                    });

                    task3.setOnSucceeded(event1 -> {
                        filterNoSearch.setDisable(false);
                        controller.disable(AppFeatures.SubjectList,false);
                        utilisedNotes.setItems(task3.getValue());
                    });

                    Controller.executeTask(task3);

                });

                Controller.executeTask(task2);

            });

            Controller.executeTask(task);




        }catch (Exception e){

        }


        /**
         * LEGACY CODE: How i accomplished it before
         * There was some latency which is why it has been commented out
         * Keeping it here in case i have to use it again
         *
         *         List<Note> notes = filterSettings.applyFilters(model.getNotes(currentSubject.getName()));
         *
         *         allNotes.getItems().clear();
         *         for(Note n: notes){
         *             allNotes.getItems().add(n);
         *         }
         *
         *
         *         List<Note> allNotes = model.getUnderutilisedNotes(notes);
         *
         *         underusedNotes.getItems().clear();
         *
         *         for(Note n: allNotes){
         *             underusedNotes.getItems().add(n);
         *         }
         *
         *
         *         notes.removeAll(allNotes);
         *
         *         for(Note n: notes){
         *             utilisedNotes.getItems().add(n);
         *         }
         */


    }

}
