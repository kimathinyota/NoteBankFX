package Code.Controller;

import Code.Model.Model;
import Code.Model.Note;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.util.Comparator;
import java.util.List;

public class UnderusedNotesListTask extends Task<ObservableList<Note>> {

   ObservableList<Note> allNotes;
   Model model;
   FilterSettings filterSettings;

    @Override
    protected ObservableList<Note> call() throws Exception {
        ObservableList<Note> allNotesCopy = FXCollections.observableArrayList(allNotes.subList(0,allNotes.size()));
        return FXCollections.observableArrayList(model.getUnderutilisedNotes(filterSettings,allNotesCopy));
    }



    public UnderusedNotesListTask(FilterSettings filterSettings, ObservableList<Note> allNotes){
        this.allNotes = allNotes;
        model = Model.getInstance();
        this.filterSettings = filterSettings;


    }


}
