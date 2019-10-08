package Code.Controller.home.notes;

import Code.Controller.home.notes.filters.FilterSettings;
import Code.Model.Model;
import Code.Model.Note;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class UnderusedNotesListTask extends Task<ObservableList<Note>> {

   ObservableList<Note> allNotes;
   Model model;
   FilterSettings filterSettings;

    @Override
    protected ObservableList<Note> call() throws Exception {
        try{
            ObservableList<Note> allNotesCopy = FXCollections.observableArrayList(allNotes.subList(0,allNotes.size()));
            ObservableList<Note> ret =  FXCollections.observableArrayList(model.getUnderutilisedNotes(filterSettings,allNotesCopy));
            return ret;
        }catch (Exception e){
            e.printStackTrace();
        }
        return FXCollections.emptyObservableList();

    }



    public UnderusedNotesListTask(FilterSettings filterSettings, ObservableList<Note> allNotes){
        this.allNotes = allNotes;
        model = Model.getInstance();
        this.filterSettings = filterSettings;


    }


}
