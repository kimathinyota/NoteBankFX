package Code.Controller;

import Code.Model.Model;
import Code.Model.Note;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class UtilisedNotesListTask extends Task<ObservableList<Note>> {


    private Model model;
    private FilterSettings filterSettings;
    private ObservableList<Note> allNotes;
    private ObservableList<Note> underusedNotes;

    @Override
    protected ObservableList<Note> call() throws Exception {
        ObservableList<Note> underusedNotesCopy = FXCollections.observableArrayList(underusedNotes.subList(0,underusedNotes.size()));
        ObservableList<Note> allNotesCopy = FXCollections.observableArrayList(allNotes.subList(0,allNotes.size()));
        return FXCollections.observableArrayList(model.getHighlyUsedNotes(filterSettings,underusedNotesCopy,allNotesCopy));
    }


    public UtilisedNotesListTask(FilterSettings filterSettings, ObservableList<Note> underusedNotes, ObservableList<Note> allNotes){
        this.underusedNotes = underusedNotes;
        model = Model.getInstance();
        this.filterSettings = filterSettings;
        this.allNotes = allNotes;
    }


}
