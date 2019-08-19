package Code.Controller;

import Code.Model.*;

import Code.View.ListCellCode;
import Code.View.SelectWithOptionsLists;
import Code.View.SelectNormalLists;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.scene.image.Image;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;






public class ViewNotesController implements RefreshSubjectsController, RefreshIdeasController, ListCellCode {

    private String padding = "                  ";
    @FXML ImageView imageView;
    @FXML WebView webView;
    @FXML Label noteTitle;
    @FXML Button done;

    @FXML Button nextNote;
    @FXML Button previousNote;
    @FXML Button zoomIn;
    @FXML Button zoomOut;
    @FXML Button previousPage;
    @FXML Button nextPage;

    @FXML TextField currentPage;
    @FXML TextField totalPages;

    @FXML Label createdFull;
    @FXML Label createdOverview;
    @FXML Label modifiedFull;
    @FXML Label modifiedOverview;


    @FXML Label ideaNumber;
    @FXML Label ideaTotal;

    @FXML Label subjectNumber;
    @FXML Label subjectTotal;

    @FXML TabPane tabPane;
    @FXML Tab viewTab;
    @FXML Tab detailsTab;
    @FXML Tab ideasTab;
    @FXML Tab subjectTab;
    @FXML Tab selectPagesTab;
    @FXML Tab clearSelectTab;

    @FXML BorderPane selectPagesTabPane;
    @FXML BorderPane clearSelectTabPane;


    @FXML BorderPane subjectPane;
    @FXML BorderPane ideaPane;

    @FXML StackPane imageHolder;
    @FXML ScrollPane imageScroll;

    @FXML Pane TextPane;

    @FXML Label noteName;
    @FXML Label noteSize;
    @FXML Label noteType;
    @FXML Label noteTypeInfo;

    @FXML Pane viewNotes;

    @FXML Button clear;
    @FXML Button selectAll;

    @FXML TextField selectedPages;

    @FXML ToggleButton selectedToggle;

    @FXML Label title;


    SelectNormalLists<Subject> subjectLists;
    SelectWithOptionsLists<Idea,String> ideasLists;

    Model model;
    Controller controller;

    private List<Note> notes;
    private int currentNoteIndex;
    private int currentPageIndex;

    private Image loadingGIF;

    ViewMode mode;

    ListView<Note> externalSelectedNotesList;

    HashMap<Note,List<java.lang.Integer>> notesSelected;

    private void setTotal(Label label, int total){
        label.setText("of " + total);
    }

    private void setNumberTotal(Label number, Label total, int n, int t){
        setTotal(total,t);
        number.setText(n + "");
    }

    private int getNumberOfPageIndexes(Note n){
        if( !(n instanceof Book)){
            return 1;
        }
        return ((Book) n).getPages().size();
    }

    @FXML protected void handleNextPageAction(ActionEvent e){
        nextPage();
    }

    @FXML protected void handlePreviousPageAction(ActionEvent e){
        previousPage();
    }

    @FXML protected void handleNextNoteAction(ActionEvent e){
        //save();
        nextNote();
    }

    @FXML protected void handlePreviousNoteAction(ActionEvent e){
        //save();
        previousNote();
    }

    @FXML protected void handleZoomInAction(ActionEvent e){
        this.imageView.setFitHeight(imageView.getFitHeight()*1.1);
        this.imageView.setFitWidth(imageView.getFitWidth()*1.1);
        this.webView.setZoom(1.1);
    }

    @FXML protected void handleZoomOutAction(ActionEvent e){
        this.imageView.setFitHeight(imageView.getFitHeight()*0.90);
        this.imageView.setFitWidth(imageView.getFitWidth()*0.90);
        this.webView.setZoom(0.90);
    }

    @FXML protected void handleDoneAction(ActionEvent e){
        finish();
    }

    @FXML protected void handleSelectedToggleAction(ActionEvent e){
        if(selectedToggle.isSelected()){
            selectPage(currentPageIndex,notes.get(currentNoteIndex));
        }else{
            deselectPage(currentPageIndex,notes.get(currentNoteIndex));
        }
    }

    @FXML protected void handleSelectAllAction(ActionEvent e){
        this.selectAll(notes.get(currentNoteIndex));
    }

    @FXML protected void handleClearAllAction(ActionEvent e){
        this.clearAll(notes.get(currentNoteIndex));
    }

    public List<java.lang.Integer> getAllPageIndexes(Note note){
        List<java.lang.Integer> list = new ArrayList<>();
        for(int i=0; i< getNumberOfPageIndexes(note); i++){
            list.add(i);
        }
        return list;
    }

    public List<java.lang.Integer> getAllPages(Note note){

        if(!(note instanceof Book))
            return FXCollections.observableArrayList(1);


        return ((Book) note).getPages();
    }

    public void selectAll(Note note){

        this.selectedToggle.setSelected(true);

        this.notesSelected.put(note,getAllPageIndexes(note));

        this.selectedPages.setText( Book.displayName(getAllPages(note)) );
        this.selectedPages.selectEnd();
        this.selectedPages.setDisable(false);

    }

    public void clearAll(Note note){

        this.selectedToggle.setSelected(false);

        this.notesSelected.put(note,new ArrayList<>());

        this.selectedPages.setText("NONE");
        this.selectedPages.selectEnd();

        this.selectedPages.setDisable(false);
    }

    public void showSelectFunctionality(boolean visible){
        selectedToggle.setVisible(visible);
        clearSelectTabPane.setVisible(visible);

        if(visible){
            this.title.setText(padding + "Select Notes");
        }else{
            this.title.setText(padding + "View Notes");
        }
    }



    public List<Note> getSelectedNotesList(){
        List<Note> copy = new ArrayList<>();
        for(Note note: this.notesSelected.keySet() ){
            if(!this.notesSelected.get(note).isEmpty()){
                if(note instanceof Book){
                    try{
                        Book book = new Book( note.getPath().toString(), Book.getPageRanges(getPages(note,this.notesSelected.get(note))));
                        copy.add(book);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }else {
                    copy.add(note);
                }
            }

        }
        return copy;
    }


    public void displayNotes(List<Note>notes, ViewMode mode){
        if(!(mode.equals(ViewMode.ViewFull) || mode.equals(ViewMode.ViewLimited  ))){
            return;
        }

        this.tabPane.getTabs().clear();
        this.tabPane.getTabs().addAll(   (model.equals(ViewMode.ViewFull) ?  viewFullConfiguration : viewLimitedConfiguration) );
        show(notes,mode);
    }




    public void displayAndSelectNotes(List<Note>notes, ListView<Note> externalSelectedNotesList){
        this.externalSelectedNotesList = externalSelectedNotesList;
        this.tabPane.getTabs().clear();
        this.tabPane.getTabs().addAll(selectLimitedConfiguration);
        show(notes,ViewMode.SelectWithListView);
    }


    private ObservableList<Tab> fullConfiguration;
    private ObservableList<Tab> viewFullConfiguration;
    private ObservableList<Tab> viewLimitedConfiguration;
    private ObservableList<Tab> selectLimitedConfiguration;



    public void displayAndSelectNotes(List<Note>notes){
        this.tabPane.getTabs().clear();
        this.tabPane.getTabs().addAll(selectLimitedConfiguration);
        show(notes,ViewMode.Select);
    }




    public List<Note> finish(){

        viewNotes.setVisible(false);


        if(mode.equals(ViewMode.Select)){
            return this.getSelectedNotesList();
        }

        if(mode.equals(ViewMode.SelectWithListView)){

            List<Note> notes = getSelectedNotesList();
            for(Note n: notes){

                externalSelectedNotesList.getItems().add(n);
            }
            return notes;
        }

        return new ArrayList<>();
    }








    private void show(List<Note>notes, ViewMode mode){

        showSelectFunctionality(  (mode.equals(ViewMode.Select) || mode.equals(ViewMode.SelectWithListView)));

        this.mode = mode;

        tabPane.getSelectionModel().select(viewTab);



        if(notes==null || notes.isEmpty() ){
            return;
        }

        this.notesSelected = new HashMap<>();
        refreshIdeas();
        refreshSubjects();


        for(Note note: notes){
            this.notesSelected.put(note,new ArrayList<>());
        }

        this.notes = notes;


        currentNoteIndex = -1;
        nextNote();

        viewNotes.setVisible(true);

    }





    private String getOverview(long date){

        long diff = new Date().getTime() - new Date(date).getTime();

        long s = (1000L);
        long m = (60L * s);
        long h = (60L * m);
        long d = (24L * h);
        long w = (7L * d);
        long mth = (30L * d);
        long y = (365L * d);

        double year = ((double) diff/y);

        if(year>=1){
            return ( ( (int) Math.floor(year)) + " years ago");
        }

        double month = ((double) diff/mth);

        if(month>=1){
            return ( ( (int) Math.floor(month)) + " months ago");
        }

        double week = ((double) diff/w);

        if(week>=1){
            return ( ( (int) Math.floor(week)) + " weeks ago");
        }

        double day = ((double) diff/d);

        if(day>=1){
            return (  ( (int) Math.floor(day)) + " days ago");
        }

        double hour = ((double) diff/h);

        if(hour>=1){
            return ( ( (int)  Math.floor(hour)) + " hours ago");
        }

        double minute = ((double) diff/m);

        if(minute>=1){
            return (  ( (int) Math.floor(minute)) + " minutes ago");
        }

        double seconds = ((double) diff/s);

        return (  ( (int) Math.floor(seconds)) + " seconds ago");

    }

    public void setTime(Label overview, Label time, long date ){
        overview.setText(getOverview(date) + "");

        DateFormat dateFormat = new SimpleDateFormat("EEEE dd/MM/yyyy HH:mm");
        time.setText(dateFormat.format(new Date(date)));

    }

    private int getTotalPages(Note note){
        if(!(note instanceof Book)){
            return 1;
        }
        return ((Book) note).getMaximumNumberOfPages();
    }

    public void setDetails(Note note){
        Path file  = new File(note.getPath().toString()).toPath();
        try {
            BasicFileAttributes attr = Files.readAttributes(file,BasicFileAttributes.class);
            this.noteName.setText(note.getName());
            this.noteSize.setText((attr.size()/(Math.pow(10,3))) + "Kbs");
            this.noteType.setText(note.getType());
            this.noteTypeInfo.setText("Pages: "+ Book.displayName(getAllPages(note)) + "  |  Total Pages: " + getTotalPages(note) );

            setTime(this.createdOverview,this.createdFull,attr.creationTime().toMillis());
            setTime(this.modifiedOverview,this.modifiedFull,attr.lastModifiedTime().toMillis());

            setNumberTotal(this.ideaNumber,this.ideaTotal,model.getIdeas(note).size(), model.getAllIdeas().size());
            setNumberTotal(this.subjectNumber,this.subjectTotal,model.getSubjects(note).size(), model.getAllSubjects().size()-1);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void setupNote(Note note){
        this.noteTitle.setText( note.getType() + ": " + note.getName());
        this.subjectLists.setSecondList(model.getSubjects(note));
        this.ideasLists.setSecondList(model.getIdeasMap(note));
        setDetails(note);
        this.totalPages.setText(getNumberOfPageIndexes(note) + "");
        this.totalPages.setDisable(true);
    }

    public void nextNote(){
        if(notes==null) return;
        currentNoteIndex = (currentNoteIndex + 1) % this.notes.size();
        currentPageIndex = -1;
        nextPage();
        setupNote(notes.get(currentNoteIndex));
    }

    public void previousNote(){
        if(notes==null) return;

        currentNoteIndex = (currentNoteIndex==-1 ? 0 : (currentNoteIndex - 1) % this.notes.size());
        currentNoteIndex = (currentNoteIndex < 0 ? (currentNoteIndex + this.notes.size() ) : currentNoteIndex   );

        this.currentPage.setText((currentPageIndex + 1) + "");
        currentPageIndex = -1;
        previousPage();

        setupNote(notes.get(currentNoteIndex));

    }

    private void selectPage(int pageIndex, Note note){


        List<java.lang.Integer> pages = this.notesSelected.get(note);
        if(!pages.contains(pageIndex)){
            pages.add(pageIndex);
        }
        this.selectedToggle.setSelected(true);
        this.selectedPages.setDisable(false);


        this.selectedPages.setText((!pages.isEmpty() ? Book.displayName(getPages(note,pages)) : "NONE"));

    }

    private List<java.lang.Integer> getDisplayList(List<java.lang.Integer>list, int number){
        List<java.lang.Integer> newList = new ArrayList<>();
        for(java.lang.Integer i: list){
            newList.add(i.intValue()+number);
        }
        return newList;
    }

    public void refreshPage(){
        setPage(currentPageIndex);
    }

    private void deselectPage(int pageIndex, Note note){
        List<java.lang.Integer> pages = this.notesSelected.get(note);
        this.notesSelected.get(note).remove(new java.lang.Integer(pageIndex));
        this.selectedToggle.setSelected(false);
        this.selectedPages.setText((!this.notesSelected.get(note).isEmpty() ? Book.displayName(getPages(note,this.notesSelected.get(note))) : "NONE") );
        this.selectedPages.selectEnd();
        this.selectedPages.setDisable(false);
    }

    private void setSelectPage(int pageIndex, Note note){
        List<java.lang.Integer> pages = this.notesSelected.get(note);
        this.selectedToggle.setSelected(pages.contains(pageIndex));

        if(mode.equals(ViewMode.ViewLimited) || mode.equals(ViewMode.ViewFull)){
            this.selectedPages.setText(Book.displayName(getAllPages(note)));
        }else{
            this.selectedPages.setText( (!pages.isEmpty() ? Book.displayName(getPages(note,pages)) : "NONE") );
        }

        this.selectedPages.selectEnd();
        this.selectedPages.setDisable(false);
    }

    private void setPage(int pageIndex){
        if(notes==null || notes.isEmpty()){
            return;
        }
        Note currentNote = notes.get(currentNoteIndex);

        int total = getNumberOfPageIndexes(currentNote);
        this.currentPageIndex = pageIndex % total;
        this.currentPageIndex = (currentPageIndex<0 ? currentPageIndex + total : currentPageIndex );

        this.currentPage.setText(java.lang.Integer.toString(currentPageIndex+1));
        this.setSelectPage(currentPageIndex,currentNote);
        try{
            display(currentNote,currentPageIndex);
        }catch (IOException e){
            e.printStackTrace();
            //show error loading image
        }
    }

    public void nextPage(){
        if(notes==null) return;
        Note currentNote = notes.get(currentNoteIndex);
        setPage((currentPageIndex + 1));
    }

    public void previousPage(){
        if(notes==null) return;
        System.out.println(currentNoteIndex);
        Note currentNote = notes.get(currentNoteIndex);
        setPage((currentPageIndex==-1 ? 0 : (currentPageIndex - 1) ));
    }

    public void initialize(){
        model = Model.getInstance();
        model.addRefreshSubjectsController(this);
        model.addRefreshIdeasController(this);

        controller = Controller.getInstance();
        controller.setViewNotesController(this);



        this.loadingGIF = new Image(getClass().getResourceAsStream("../View/Icons/loading.gif"));


        subjectLists = new SelectNormalLists(this);
        ideasLists = new SelectWithOptionsLists(FXCollections.observableArrayList("Prompt","Non-Prompt"),this);



        imageHolder.minWidthProperty().bind(Bindings.createDoubleBinding(() ->
                imageScroll.getViewportBounds().getWidth(), imageScroll.viewportBoundsProperty()));



        subjectPane.setCenter(subjectLists);
        ideaPane.setCenter(ideasLists);


        notes = null;


        fullConfiguration = FXCollections.observableArrayList(detailsTab,subjectTab,ideasTab,viewTab,selectPagesTab,clearSelectTab);
        viewFullConfiguration = FXCollections.observableArrayList(detailsTab,subjectTab,ideasTab,viewTab, selectPagesTab);
        viewLimitedConfiguration = FXCollections.observableArrayList(detailsTab,viewTab,selectPagesTab);
        selectLimitedConfiguration = FXCollections.observableArrayList(detailsTab,viewTab,selectPagesTab,clearSelectTab);



        this.viewNotes.heightProperty().addListener((observable, oldValue, newValue) -> {
            if(currentNoteIndex>=0)
                setPage(this.currentPageIndex);
        });


        currentPage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue.booleanValue()==true && newValue.booleanValue()==false && StringUtils.isNumeric(currentPage.getText())){
                setPage(java.lang.Integer.parseInt(currentPage.getText()) - 1);
            }else if(oldValue.booleanValue()==true && newValue.booleanValue()==false && !StringUtils.isNumeric(currentPage.getText())){
                currentPage.setText((currentPageIndex+1) + "");
            }
        });

    }

    public void updateIdeas(){
        model.limitNoteToTheseIdeas(notes.get(currentNoteIndex),ideasLists.getMap());
    }

    public void updateSubject(){
        model.limitNoteToTheseSubjects(notes.get(currentNoteIndex),subjectLists.getSecondList());
    }

    public void save(){
        updateSubject();
        updateIdeas();
    }




    private List<java.lang.Integer> getPages(Note n, List<java.lang.Integer>pages){
        if(!(n instanceof Book)){
            return pages;
        }
        List<java.lang.Integer> lst = new ArrayList<>();
        for(java.lang.Integer i: pages){
            lst.add( ((Book) n).getPages().get(i) );
        }
        return lst;
    }

    private void display(Note note, java.lang.Integer bookIndex) throws IOException {

        if(note instanceof Book){
            List<java.lang.Integer> pages = ((Book) note).getPages();
            if(!(bookIndex.intValue()<pages.size())){
                return;
            }

            int page = pages.get(bookIndex.intValue())-1;


            LoadBookPageTask task = new LoadBookPageTask((Book) note, page);

            task.setOnRunning( (running) -> {
                displayImage(loadingGIF,false);
                disableNavButtons(true);
            });

            task.setOnSucceeded( (succeeded) -> {
                displayImage(task.getValue(),true);
                disableNavButtons(false);
            });

            executeTask(task);


        }else if(note instanceof Code.Model.Image){

            LoadImagePageTask task = new LoadImagePageTask((Code.Model.Image) note);

            task.setOnRunning( (running) -> {
                displayImage(loadingGIF,false);
                disableNavButtons(true);
            });

            task.setOnSucceeded( (succeeded) -> {
                displayImage(task.getValue(),true);
                disableNavButtons(false);
            });

            executeTask(task);

        }else if(note instanceof Text){

            LoadTextPageTask task = new LoadTextPageTask((Code.Model.Text) note);

            task.setOnRunning( (running) -> {
                displayImage(loadingGIF,false);
                disableNavButtons(true);

            });

            task.setOnSucceeded( (succeeded) -> {
                webView.getEngine().loadContent(task.getValue());
                this.imageScroll.setVisible(false);
                this.TextPane.setVisible(true);
                disableNavButtons(false);
            });

            executeTask(task);
        }
    }

    private void disableNavButtons(boolean disable){
        this.nextPage.setDisable(disable);
        this.nextNote.setDisable(disable);
        this.previousPage.setDisable(disable);
        this.previousNote.setDisable(disable);
    }

    private void executeTask(Task task){
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(task);
        executorService.shutdown();
    }

    private void displayImage(Image image, boolean fitImage){
        this.imageView.setImage(image);
        if(fitImage){
            double height = 0.98 * imageScroll.getHeight();
            double ratio = height/image.getHeight();
            double width = ratio * image.getWidth();
            this.imageView.setFitHeight(height);
            this.imageView.setFitWidth(width);
        }
        this.imageScroll.setVisible(true);
        this.TextPane.setVisible(false);
    }

    @Override
    public void refreshIdeas() {
        ideasLists.setFirstList(model.getAllIdeas());
    }

    @Override
    public void refreshSubjects() {
        List<Subject> subjects = FXCollections.observableArrayList(model.getAllSubjects());
        subjects.remove(model.getSubject("All"));
        subjectLists.setFirstList(subjects);
    }

    @Override
    public void onAddClick() {
        save();
    }

    @Override
    public void onRemoveClick() {
        save();
    }

    @Override
    public void onOptionChange() {
        save();
    }


}
