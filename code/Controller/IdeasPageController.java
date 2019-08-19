package Code.Controller;

import Code.Model.Idea;
import Code.Model.Model;
import Code.Model.Note;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

import java.time.chrono.IsoChronology;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class IdeasPageController implements RefreshNotesController{

    @FXML BorderPane ideaPane;
    @FXML Label ideaTitle;
    @FXML Label ideaType;


    SelectNoteLists notesList;

    Model model;
    Controller controller;

    @FXML ListView<String> keywords;


    public void displayIdea(Idea idea){
        ideaTitle.setText(idea.getPrompt());
        ideaType.setText(idea.getPromptType().name());
        keywords.setItems(FXCollections.observableArrayList(idea.getKeyWords()));
        notesList.setSecondList(convertPromptTypeHashMap(idea.getNotesMap()));
    }


    private HashMap<Note,String> convertPromptTypeHashMap(HashMap<Note,Integer> a){
        HashMap<Note,String> b = new HashMap<>();
        for(Note n: a.keySet() ){
            b.put( n,  (a.get(b).intValue()==Idea.PROMPT_NOTE ? "Prompt" : "Non-Prompt") ) ;
        }
        return b;
    }


    public void initialize(){

        model = Model.getInstance();
        controller = Controller.getInstance();


        notesList = new SelectNoteLists(FXCollections.observableArrayList("Prompt","Non-Prompt"),Controller.emptyListCellCode());

        ideaPane.setCenter(notesList);

        keywords.setOrientation(Orientation.HORIZONTAL);


        refreshNotes();
    }


    @Override
    public void refreshNotes() {
        notesList.setFirstList(model.getAllNotes());
    }
}
