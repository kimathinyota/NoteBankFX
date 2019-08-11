package Code.Controller;

import Code.Model.Model;
import Code.Model.Note;
import Code.Model.Subject;
import Code.View.menus.NoteIconCellFactory;

import Code.View.menus.NoteTextCellFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import java.security.Key;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * NotesPageController
 *
 */
public class NotesPageController implements RefreshNotesController, RefreshSubjectsController {
    @FXML protected Button openFilter;
    @FXML protected Pane filter;

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

    private FilterSettings filterSettings;

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
    }



    Model model;
    Controller controller;
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
        currentSubject = model.getCurrentSubject();

        refreshSubjects();

        addSelectedItemListener(underusedNotes,underusedIndex);
        addSelectedItemListener(utilisedNotes, utilisedIndex);
        addSelectedItemListener(allNotes,allIndex);

        search.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("") && !newValue.equals(oldValue)){
                filterSettings.setSearch("");
                refreshSubjects();
            }
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


        setLeftRightKeyPressed(allNotes, allIndex);
        setLeftRightKeyPressed(underusedNotes,underusedIndex);
        setLeftRightKeyPressed(utilisedNotes,utilisedIndex);

    }


    public void setLeftRightKeyPressed(ListView<Note>list, Integer index){
        list.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.LEFT){

                handleLeftClick(null);
            }else if(event.getCode() == KeyCode.RIGHT){
                handleLeftClick(null);
            }
        });
    }



    @FXML protected void handleSearchEnterPress(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            filterSettings.setSearch(search.getText());
            refreshSubjects();
        }
    }


    public void addSelectedItemListener(ListView<Note> notes, Integer notesIndex){
        notes.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> setIndex(notesIndex,newValue.intValue()));
    }


    public void setIndex(Integer i, int val){
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


    private Integer allIndex = new Integer(-1);
    private Integer underusedIndex = new Integer(-1);
    private Integer utilisedIndex = new Integer(-1);


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
        handleRightClick(underusedNotes,underusedIndex);
    }

    @FXML protected void handleLeftClick(ActionEvent e){
        handleLeftClick(underusedNotes,underusedIndex);
    }

    @FXML protected void handleRight2Click(ActionEvent e){
        handleRightClick(utilisedNotes,utilisedIndex);
    }

    @FXML protected void handleLeft2Click(ActionEvent e){
        handleLeftClick(utilisedNotes,utilisedIndex);
    }

    @FXML protected void handleRight3Click(ActionEvent e){
        handleRightClick(allNotes,allIndex);
    }

    @FXML protected void handleLeft3Click(ActionEvent e){
        handleLeftClick(allNotes,allIndex);
    }


    private void handleLeftClick(ListView<Note>allNotes,Integer allIndex){

        if(allNotes.getItems().isEmpty()){
            return;
        }

        if(allIndex.intValue()==-1){
            allIndex.setInteger(1);
        }

        allIndex.setInteger((allIndex.intValue()-1)%allNotes.getItems().size());
        allIndex.setInteger( (allIndex.intValue()<0 ? allIndex.intValue() + allNotes.getItems().size() :allIndex.intValue())  );
        allIndex.setInteger (allIndex.intValue()<0 ? allIndex.intValue() + allNotes.getItems().size() : allIndex.intValue());

        allNotes.scrollTo( allIndex.intValue() );
        allNotes.getSelectionModel().select(allIndex.intValue());


    }

    private void handleRightClick(ListView<Note>allNotes,Integer allIndex){

        if(allNotes.getItems().isEmpty()){
            return;
        }

        allIndex.setInteger((allIndex.intValue()+1)%allNotes.getItems().size());
        allNotes.scrollTo( allIndex.intValue() );
        allNotes.getSelectionModel().select(allIndex.intValue());

    }


    @FXML protected void handleOpenFilterAction(ActionEvent e){
        filter.setVisible(true);
    }

    @FXML protected void handleCloseFilterAction(ActionEvent e){
        filter.setVisible(false);
    }

    Subject currentSubject;

    @Override
    public void refreshNotes() {
        refreshSubjects();
    }



    @Override
    public void refreshSubjects() {

        this.currentSubject = model.getCurrentSubject();
        subjectLine.setText(this.currentSubject.getName());

        try{
            AllNotesListTask task = new AllNotesListTask(filterSettings);
            task.setOnRunning((runningEven) -> {
                // @TODO: Add a progress bar to refreshSubjects

            } );

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

            allNotes.setItems(task.getValue());

            task.setOnRunning( (runningAllNotes) -> {
                filter.setDisable(true);
                controller.disable(AppFeatures.SubjectList,true);
            });

            task.setOnSucceeded((completedEvent) -> {
                allNotes.setItems(task.getValue());
                filter.setDisable(false);
                controller.disable(AppFeatures.SubjectList,false);

                UnderusedNotesListTask task2 = new UnderusedNotesListTask(filterSettings,task.getValue());

                task2.setOnRunning( (runningUnderusedNotes) -> {
                    filter.setDisable(true);
                    controller.disable(AppFeatures.SubjectList,true);
                });

                task2.setOnSucceeded(event -> {

                    filter.setDisable(false);
                    controller.disable(AppFeatures.SubjectList,false);
                    underusedNotes.setItems(task2.getValue());

                    UtilisedNotesListTask task3 = new UtilisedNotesListTask(filterSettings,task2.getValue(),task.getValue());

                    task3.setOnRunning( (runningUsedNotes) -> {
                        filter.setDisable(true);
                        controller.disable(AppFeatures.SubjectList,true);
                    });

                    task3.setOnSucceeded(event1 -> {
                        filter.setDisable(false);
                        controller.disable(AppFeatures.SubjectList,false);
                        utilisedNotes.setItems(task3.getValue());
                    });

                    ExecutorService executorService = Executors.newFixedThreadPool(1);
                    executorService.execute(task3);
                    executorService.shutdown();

                });

                ExecutorService executorService = Executors.newFixedThreadPool(1);
                executorService.execute(task2);
                executorService.shutdown();

            });


            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(task);
            executorService.shutdown();




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
