package Code.Controller.ideas;

import Code.Controller.Controller;
import Code.Controller.RefreshInterfaces.RefreshIdeasController;
import Code.Controller.RefreshInterfaces.RefreshNotesController;
import Code.Controller.components.lists.SelectNoteForIdeaLists;
import Code.Model.Idea;
import Code.Model.Model;
import Code.Model.Note;

import Code.View.View;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Pair;
import org.controlsfx.control.PopOver;

import java.util.HashMap;



public class IdeasPageController implements RefreshNotesController, RefreshIdeasController {

    @FXML BorderPane ideaPane;
    @FXML Label ideaTitle;
    @FXML Label ideaType;


    SelectNoteForIdeaLists notesList;

    Model model;
    Controller controller;

    @FXML ListView<String> keywords;

    @FXML
    ImageView loadingGIF;

    Idea lastIdea;

    public void displayDetails(Idea idea){
        this.lastIdea = idea;
        ideaTitle.setText(idea.getPrompt());

        ideaType.setText(idea.getPromptType().name());
        keywords.setItems(FXCollections.observableArrayList(idea.getKeyWords()));
    }


    public void displayIdea(Idea idea){


        displayDetails(idea);
        System.out.println("Idea: " + idea);

        Task<SelectNoteForIdeaLists> listsTask = new Task<SelectNoteForIdeaLists>() {
            @Override
            protected SelectNoteForIdeaLists call() throws Exception {
                SelectNoteForIdeaLists l = new SelectNoteForIdeaLists(convertPromptTypeHashMap(idea.getNotesMap(), idea.getFinalNote()),idea);
                l.setFirstList(model.getAllNotes());
                return l;
            }
        };

        listsTask.setOnSucceeded((createdList) -> {
            notesList = listsTask.getValue();
            ideaPane.setCenter(notesList);
        });

        listsTask.setOnRunning((event2) -> {
            ideaPane.setCenter(loadingGIF);
        });

        Controller.executeTask(listsTask);


    }


    public static HashMap<Note, Pair<Boolean,Boolean>> convertPromptTypeHashMap(HashMap<Note,Boolean> a, Note finalNote){
        HashMap<Note,Pair<Boolean,Boolean>> b = new HashMap<>();
        for(Note n: a.keySet() ){
            if(n!=null)
                b.put( n,  new Pair( n.equals(finalNote), a.get(n) )) ;
        }
        return b;
    }

    @FXML protected void handleEditAction(ActionEvent e){
        controller.editIdea(lastIdea);
    }


    PopOver popOver;

    public void initialize(){

        model = Model.getInstance();
        model.addRefreshNotesController(this);
        model.addRefreshIdeasController(this);
        controller = Controller.getInstance();
        controller.setIdeasPageController(this);


        this.ideaTitle.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                popOver = View.getInformationPane("Full Prompt",lastIdea.getPrompt(),250);
                popOver.show(ideaTitle);
            }
        });

        this.ideaTitle.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                popOver.hide();
            }
        });


        keywords.setOrientation(Orientation.HORIZONTAL);




        refreshNotes();
    }


    @Override
    public void refreshNotes() {
        if(notesList==null) return;
        notesList.setFirstList(model.getAllNotes());
    }



    @Override
    public void refreshIdeas() {

        //new Task<Integer>()
        if(lastIdea!=null){
            this.lastIdea = model.getIdea(lastIdea.getID());
            System.out.println("New Idea: " + lastIdea );
            displayDetails(lastIdea);
        }

        //this.lastIdea = model.getIdea(lastIdea.getID());
        //displayIdea(lastIdea);

    }




}
