package Code.Controller;

import Code.Model.Model;
import Code.Model.Note;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import java.util.List;

public class AllNotesListTask extends Task<ObservableList<Note>> {

    Model model;
    FilterSettings filterSettings;

    @Override
    protected ObservableList<Note> call() throws Exception {
        List<Note> notes = filterSettings.applyFilters(model.getNotes(model.getCurrentSubject().getName() ));
        return FXCollections.observableArrayList(notes);
    }




    public AllNotesListTask(FilterSettings filterSettings){
        model = Model.getInstance();
        this.filterSettings = filterSettings;
    }


}
