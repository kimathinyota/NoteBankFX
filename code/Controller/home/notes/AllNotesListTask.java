package Code.Controller.home.notes;

import Code.Controller.home.notes.filters.FilterSettings;
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

        try {
            List<Note> notes = filterSettings.applyFilters(model.getNotes(model.getCurrentSubject().getName() ));
            ObservableList<Note> ret =  FXCollections.observableArrayList(notes);
            return ret;
        }catch (Exception e){
            e.printStackTrace();
        }


        return FXCollections.emptyObservableList();
    }




    public AllNotesListTask(FilterSettings filterSettings){
        model = Model.getInstance();
        this.filterSettings = filterSettings;
    }


}
