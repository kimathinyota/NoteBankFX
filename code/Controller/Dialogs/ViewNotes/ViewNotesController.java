package Code.Controller.Dialogs.ViewNotes;

import Code.Controller.Controller;
import Code.Controller.Dialogs.ViewNotes.LoadTasks.LoadBookPageTask;
import Code.Controller.Dialogs.ViewNotes.LoadTasks.LoadImagePageTask;
import Code.Controller.Dialogs.ViewNotes.LoadTasks.LoadTextPageTask;
import Code.Controller.RefreshInterfaces.RefreshIdeasController;
import Code.Controller.RefreshInterfaces.RefreshSubjectsController;
import Code.Controller.components.lists.SelectIdeaLists;
import Code.Controller.components.lists.SelectSubjectLists;
import Code.Model.*;


import com.sun.tools.corba.se.idl.constExpr.Not;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.scene.image.Image;
import javafx.util.Duration;
import javafx.util.Pair;
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






public class ViewNotesController implements RefreshSubjectsController, RefreshIdeasController {

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
    @FXML Label noteTypeInfo;

    @FXML Pane viewNotes;

    @FXML Button clear;
    @FXML Button selectAll;

    @FXML TextField selectedPages;

    @FXML ToggleButton selectedToggle;

    @FXML Label title;


    SelectSubjectLists subjectLists;
    SelectIdeaLists ideasLists;

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
        //KIMATHI NYOTA
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

        //KIMATHI NYOTA
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
                        //KIMATHI NYOTA
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

        this.displayIdea = null;
        viewNotes.setVisible(true);
        this.tabPane.getTabs().clear();
        this.tabPane.getTabs().addAll(   (mode.equals(ViewMode.ViewFull) ?  viewFullConfiguration : viewLimitedConfiguration) );



        show(notes,mode);


    }



    Idea displayIdea;



    public void displayAndSelectNotes(List<Note>notes, ListView<Note> externalSelectedNotesList, Idea idea){
        viewNotes.setVisible(true);
        this.externalSelectedNotesList = externalSelectedNotesList;
        this.tabPane.getTabs().clear();
        this.tabPane.getTabs().addAll(selectLimitedConfiguration);

        this.displayIdea = idea;
        show(notes,ViewMode.SelectWithListView);
    }

    private ObservableList<Tab> fullConfiguration;
    private ObservableList<Tab> viewFullConfiguration;
    private ObservableList<Tab> viewLimitedConfiguration;
    private ObservableList<Tab> selectLimitedConfiguration;

    public void displayAndSelectNotes(List<Note>notes){
        viewNotes.setVisible(true);
        this.tabPane.getTabs().clear();
        this.tabPane.getTabs().addAll(selectLimitedConfiguration);
        show(notes,ViewMode.Select);
    }

    public List<Note> finish(){

        if(loadPage!=null)  loadPage.cancel();
        if(loadAllPages!=null) loadAllPages.cancel();

        displayVerbatim(loadingGIF);

        notes.forEach( note -> {if(note instanceof Book) ((Book) note).stopViewing();});

        viewNotes.setVisible(false);


        if(mode.equals(ViewMode.Select)){
            return this.getSelectedNotesList();
        }

        if(mode.equals(ViewMode.SelectWithListView)){

            List<Note> notes = getSelectedNotesList();
            for(Note n: notes){

                externalSelectedNotesList.getItems().add(n);

                if(displayIdea!=null){
                    model.addToIdea(displayIdea,n,false,false);
                }

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



        this.notes = notes;


        currentNoteIndex = -1;


        nextNote();


    }

    public static Pair<Long,Pair<Long,Long>> regionToQuotientRemainder(long date,Collection<Long>timeRegions){
        List<Long> regions = new ArrayList<>(timeRegions);
        regions.sort(Comparator.reverseOrder());
        for(Long div: regions){
            long quotient = Math.floorDiv(date,div);
            if(quotient>=1){
                long remainder = date - quotient*div;
                Pair<Long,Long> quotientRemainder = new Pair<>(quotient,remainder);
                return new Pair<>(div,quotientRemainder);
            }
        }

        return null; //this means date is less than all time regions given

    }

    public static HashMap<Long,Integer> getRegions(long date,Collection<Long>timeRegions){
        HashMap<Long,Integer> regions = new HashMap<>();
        long nextDate = date;
        for(int order=0; order<timeRegions.size(); order+=1){
            Pair<Long,Pair<Long,Long>> result = regionToQuotientRemainder(nextDate,timeRegions);
            if(result==null){
                break;
            }
            regions.put(result.getKey(),result.getValue().getKey().intValue());
            nextDate = result.getValue().getValue();
        }

        timeRegions.forEach(aLong -> {if(regions.get(aLong)==null) regions.put(aLong,0); } );

        return regions;

    }

    public static String getDateOverviewName(long date,HashMap<Long,String> regionsToSuffix, int numberOfOrders, String betweenOrdersString){

        String current = "";
        long nextDate = date;
        for(int order=0; order<numberOfOrders; order+=1){
            Pair<Long, Pair<Long,Long>> result = regionToQuotientRemainder(nextDate,regionsToSuffix.keySet());
            if(result==null){
                return current;
            }
            if(order>0){
                current += betweenOrdersString;
            }

            current += result.getValue().getKey().intValue() + regionsToSuffix.get(result.getKey());

            nextDate = result.getValue().getValue();

        }
        return current;

    }

    public static int getDay(Date date){
        return ((int) (date.getTime()/Quizzes.d));
    }

    public static int getHour(Date date){
        return ((int) (date.getTime()/Quizzes.h));
    }

    public static int getMinute(Date date){
        return ((int) (date.getTime()/Quizzes.m));
    }

    public static int getSeconds(Date date){
        return ((int) (date.getTime()/Quizzes.s));
    }

    public static String getOverview(long date,
                               String yearText,String monthText,
                               String weekText, String dayText,
                               String hourText, String minuteText, String secondText, String zeroText, String pastSuffix, String futureSuffix){


        String suffix = (date < 0 ? pastSuffix : futureSuffix);

        long diff = Math.abs(date);

        long s = (1000L);
        long m = (60L * s);
        long h = (60L * m);
        long d = (24L * h);
        long w = (7L * d);
        long mth = (30L * d);
        long y = (365L * d);

        double year = ((double) diff/y);

        if(year>=1){
            return ( ( (int) Math.floor(year)) + yearText + suffix);
        }

        double month = ((double) diff/mth);

        if(month>=1){
            return ( ( (int) Math.floor(month)) + monthText + suffix);
        }

        double week = ((double) diff/w);

        if(week>=1){
            return ( ( (int) Math.floor(week)) + weekText + suffix);
        }

        double day = ((double) diff/d);

        if(day>=1){
            return (  ( (int) Math.floor(day)) + dayText + suffix);
        }

        double hour = ((double) diff/h);

        if(hour>=1){
            return ( ( (int)  Math.floor(hour)) + hourText + suffix);
        }

        double minute = ((double) diff/m);

        if(minute>=1){
            return (  ( (int) Math.floor(minute)) + minuteText + suffix);
        }

        double seconds = ((double) diff/s);


        int scs = ( (int) Math.floor(seconds));

        if (scs > 0){
            return (  scs + secondText + suffix);
        }

        return zeroText;

    }




    public static String getOverview(long date){

        return getOverview(new Date().getTime() - new Date(date).getTime()," years"," months", " weeks", " days", " hours", " minutes", " seconds","NOW"," from now"," ago");

    }

    public void setTime(Label overview, Label time, long date ){
        overview.setText(getOverview(date) + "");

        DateFormat dateFormat = new SimpleDateFormat("EEEE dd/MM/yyyy HH:mm");
        time.setText(dateFormat.format(new Date(date)));

    }

    private Integer getTotalPages(Note note){
        if(!(note instanceof Book)){
            return 1;
        }
        return ((Book) note).getMaximumNumberOfPages();
    }

    public void setDetails(Note note) {
        Path file = new File(note.getPath().toString()).toPath();
        try {
            BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
            this.noteName.setText(note.getName());
            this.noteSize.setText((attr.size() / (Math.pow(10, 3))) + "Kbs");


            Integer totalNumberOfPages = getTotalPages(note);
            this.noteTypeInfo.setText(note.getType() + "  Pages: " + Book.displayName(getAllPages(note)) +  (totalNumberOfPages!=null ? "  Total Pages: " + totalNumberOfPages : "")     );

            setTime(this.createdOverview, this.createdFull, attr.creationTime().toMillis());
            setTime(this.modifiedOverview, this.modifiedFull, attr.lastModifiedTime().toMillis());

            setNumberTotal(this.ideaNumber, this.ideaTotal, model.getIdeas(note).size(), model.getAllIdeas().size());
            setNumberTotal(this.subjectNumber, this.subjectTotal, model.getSubjects(note).size(), model.getAllSubjects().size() - 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<Idea, Pair<Boolean,Boolean>> convertToIdeaMap(Note note){
        HashMap<Idea,Pair<Boolean,Boolean>> map = new HashMap<>();
        List<Idea> ideas = new ArrayList<>();
        for(Idea i: model.getIdeas(note)){
            map.put(i,new Pair<>(i.getFinalNote()!=null && i.getFinalNote().equals(note),i.getNotesMap().get(note)));
        }
        return map;
    }

    private void setupNote(Note note){



            subjectLists = new SelectSubjectLists(note,model.getSubjects(note));
            ideasLists = new SelectIdeaLists(note, convertToIdeaMap(note));
            refreshIdeas();
            refreshSubjects();
            subjectPane.setCenter(subjectLists);
            ideaPane.setCenter(ideasLists);
            setDetails(note);
            totalPages.setText(getNumberOfPageIndexes(note) + "");
            totalPages.setDisable(true);







    }

    public void nextNote(){
        if(notes==null) return;

        currentNoteIndex = (currentNoteIndex + 1) % notes.size();
        currentPageIndex = -1;

        Note note = notes.get(currentNoteIndex);

        organiseNote(note);

    }


    Book lastBook;


    private void organiseNote(Note note){
        this.noteTitle.setText( note.getType() + ": " + note.getName());

        disableNavButtons(true);

        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                if(note instanceof Book ){

                    try{
                        Book book = ((Book) note);

                        boolean isSetUp = book.isSetUpForViewing();

                        if(!isSetUp){
                            Integer numberOfPages = model.getNumberOfPages(book);
                            if(numberOfPages!=null){
                                book.setSpecifyPages(numberOfPages);
                                // System.out.println("BOOOBOOIES");
                            }else{
                                //System.out.println("setup");
                                book.setupForViewing();
                                model.addNumberOfPages(book,book.getMaximumNumberOfPages());


                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
                return null;
            }
        };


        task.setOnSucceeded( event -> {
            nextPage();
            setupNote(note);
            disableTabs(false,false,false,false);
        });

        task.setOnRunning( event -> {
            displayVerbatim(loadingGIF);
            disableNavButtons(true);
            disableTabs(false,true,true,true);
        });

        executeTask(task);
    }


    private void disableTabs(boolean view, boolean ideas, boolean subject,  boolean details){
        this.ideasTab.setDisable(ideas);
        this.subjectTab.setDisable(subject);
        this.detailsTab.setDisable(details);
        this.viewTab.setDisable(view);
    }



    public void previousNote(){
        if(notes==null) return;

        currentNoteIndex = (currentNoteIndex==-1 ? 0 : (currentNoteIndex - 1) % this.notes.size());
        currentNoteIndex = (currentNoteIndex < 0 ? (currentNoteIndex + this.notes.size() ) : currentNoteIndex   );

        ///this.currentPage.setText((currentPageIndex + 1) + "");
        currentPageIndex = -1;


        Note note = notes.get(currentNoteIndex);
        organiseNote(note);

    }

    private void selectPage(int pageIndex, Note note){


        List<java.lang.Integer> pages = this.notesSelected.get(note);
        if(pages==null){
            pages = new ArrayList<>();
            this.notesSelected.put(note,pages);
        }
        if(!pages.contains(pageIndex)){
            pages.add(pageIndex);
        }
        this.selectedToggle.setSelected(true);
        this.selectedPages.setDisable(false);


        //KIMATHI NYOTA
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
        //KIMATHI NYOTA
        this.selectedPages.setText((!this.notesSelected.get(note).isEmpty() ? Book.displayName(getPages(note,this.notesSelected.get(note))) : "NONE") );
        this.selectedPages.selectEnd();
        this.selectedPages.setDisable(false);
    }

    private void setSelectPage(int pageIndex, Note note){
        List<java.lang.Integer> pages = this.notesSelected.get(note);

        this.selectedToggle.setSelected(pages!=null && pages.contains(pageIndex));

        //KIMATHI NYOTA
        if(mode.equals(ViewMode.ViewLimited) || mode.equals(ViewMode.ViewFull)){
            this.selectedPages.setText(Book.displayName(getAllPages(note)));
        }else{
            this.selectedPages.setText( (pages!=null && !pages.isEmpty() ? Book.displayName(getPages(note,pages)) : "NONE") );
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
        //System.out.println(currentNoteIndex);
        Note currentNote = notes.get(currentNoteIndex);
        setPage((currentPageIndex==-1 ? 0 : (currentPageIndex - 1) ));
    }

    public void initialize(){
        model = Model.getInstance();
        model.addRefreshSubjectsController(this);
        model.addRefreshIdeasController(this);

        controller = Controller.getInstance();
        controller.setViewNotesController(this);


        this.loadingGIF = new Image(getClass().getResourceAsStream("/Code/View/Icons/loading.gif"));



        imageHolder.minWidthProperty().bind(Bindings.createDoubleBinding(() ->
                imageScroll.getViewportBounds().getWidth(), imageScroll.viewportBoundsProperty()));


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


        /*
        currentPage.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null && !newValue.isEmpty() && StringUtils.isNumeric(currentPage.getText())){
                setPage(java.lang.Integer.parseInt(currentPage.getText()) - 1);
            }else if(newValue!=null){
                currentPage.setText((currentPageIndex+1) + "");
            }
        });
        */



        viewNotes.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null && newValue.equals(false) && !newValue.equals(oldValue)){
                //loadAllPages.cancel();
                //loadPage.cancel();
            }
        });



    }


    /*
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

    */


    private List<java.lang.Integer> getPages(Note n, List<java.lang.Integer>pages){
        if(!(n instanceof Book)){
            return pages;
        }
        //KIMATHI NYOTA
        List<java.lang.Integer> lst = new ArrayList<>();
        for(java.lang.Integer i: pages){
            //KIMATHI NYOTA
            lst.add( ((Book) n).getPages().get(i) );
        }
        return lst;
    }


    Task<Image>  loadPage;

    Task<String> loadAllPages;


    private void loadPages(Book book, int index){

        if(loadAllPages!=null){
            loadAllPages.cancel();
        }

        int start1 = (index - 1) % book.getPages().size();
        int start = (start1 < 0 ? start1 + book.getPages().size() : start1);
        int end = (index + 1) % book.getPages().size();

        System.out.println("Loading pages: " +  start + " - " + end);

        loadAllPages = new Task<String>() {
            @Override
            protected String call() throws Exception {
                try{
                    book.loadImages(start,end);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };

        loadAllPages.setOnSucceeded(event -> {
            book.onlyKeepPages(start,end);
        });


        loadAllPages.setOnCancelled(event -> {
            book.onlyKeepPages(start,end);

        });


        Controller.executeTask(loadAllPages);


    }


    private void display(Note note, java.lang.Integer bookIndex) throws IOException {


        if(note instanceof Book){

            List<Integer> pages = ((Book) note).getPages();
            int page = pages.get(bookIndex.intValue()) - 1;
            Book book = (Book) note;
/*

            loadPages((Book) note,bookIndex.intValue());



            if(loadPage!=null){
                loadPage.cancel();
            }

            loadPage = new Task<Image>() {
                @Override
                protected Image call() throws Exception {
                    //will wait until page is loaded in memory before returning the page
                    try{
                        while(!book.isLoaded(page));
                        return SwingFXUtils.toFXImage(((Book) book).loadPage(page), null);
                    }catch (Exception e){
                        e.printStackTrace();;
                    }

                    return loadingGIF;

                }
            };

            loadPage.setOnRunning(event -> {
                if(bookIndex.intValue()==currentPageIndex){
                    displayVerbatim(loadingGIF);
                }
            });

            loadPage.setOnSucceeded(event -> {
                if(bookIndex.intValue()==currentPageIndex){
                    displayImage(loadPage.getValue(),true);
                }
            });

            Controller.executeTask(loadPage);
*/


            //loadPages((Book) note,bookIndex.intValue());


            LoadBookPageTask pageTask = new LoadBookPageTask(book,page);

            pageTask.setOnRunning(event -> {
                displayVerbatim(loadingGIF);
                disableNavButtons(true);
            });

            pageTask.setOnSucceeded( event -> {
                displayImage(pageTask.getValue(),true);
                disableNavButtons(false);
            });


            Controller.executeTask(pageTask);


            return;


        }else if(note instanceof Code.Model.Image){

            LoadImagePageTask task = new LoadImagePageTask((Code.Model.Image) note);

            task.setOnRunning( (running) -> {
                //displayImage(loadingGIF,false);
                displayVerbatim(loadingGIF);
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
                //displayImage(loadingGIF,false);
                displayVerbatim(loadingGIF);
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

    public static void executeTask(Task task){
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

    private void displayVerbatim(Image image){
        displayImage(image,false);
        imageView.setFitHeight(300);
        imageView.setFitWidth(300);

    }

    @Override
    public void refreshIdeas() {
        if(ideasLists==null) return;
        ideasLists.setFirstList(model.getAllIdeas());
    }

    @Override
    public void refreshSubjects() {

        if(subjectLists==null) return;
        List<Subject> subjects = FXCollections.observableArrayList(model.getAllSubjects());
        subjects.remove(model.getSubject("All"));
        subjectLists.setFirstList(subjects);

    }




}
