package Code.Controller.components.lists;

import Code.Controller.Controller;
import Code.Controller.Dialogs.ViewNotes.ViewMode;
import Code.Model.Book;
import Code.Model.Note;
import Code.View.*;
import Code.View.components.SelectFromListToList.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Pair;

import java.util.HashMap;

class AddNoteListCell extends AddListCell<Note> {


    @Override
    protected void handleLabelButtonClick(ActionEvent e) {
        Controller.getInstance().displayNotes(FXCollections.singletonObservableList(item), ViewMode.ViewLimited);
    }

    public AddNoteListCell(ListView<Note> listone, ListView<Note> listtwo) {
        super(listone, listtwo);
    }

    @Override
    protected void handleAddAction(ActionEvent e) {
        super.handleAddAction(e);
        if(item instanceof Book){
            this.listTwo.getItems().remove(item);
            Controller.getInstance().displayAndSelectNotes(FXCollections.singletonObservableList(item),listTwo,null);
        }
    }

}



class AddNoteListCellFactory extends AddListCellFactory<Note> {

    public AddNoteListCellFactory(ListView<Note> listOne, ListView<Note> listTwo) {
        super(listOne, listTwo);
    }

    @Override
    public ListCell<Note> call(ListView<Note> param) {
        AddNoteListCell cell = new AddNoteListCell(listOne,listTwo);
        return cell;
    }

}

class RemoveNoteOptionListCell extends RemoveOptionsListCell<Note> {

    @Override
    protected void handleLabelButtonClick(ActionEvent e) {
        Controller.getInstance().displayNotes(FXCollections.singletonObservableList(getItem()),ViewMode.ViewLimited);
    }

    public RemoveNoteOptionListCell(ListView<Note> t, RemoveListCellOptionsFactory factory) {
        super(t, factory);
    }
}


class RemoveNoteListCellOptionsFactory extends RemoveListCellOptionsFactory<Note>{

    @Override
    public ListCell<Note> call(ListView<Note> param) {
        return new RemoveNoteOptionListCell(param,this);
    }

    public RemoveNoteListCellOptionsFactory(HashMap<Note, Pair<Boolean,Boolean>> map, String firstSelected, String firstNonSelected , String secondSelected, String secondNonSelected) {
        super(map,firstSelected,firstNonSelected,secondSelected,secondNonSelected);
    }

}



public class SelectNoteLists extends SelectWithOptionsLists<Note> {


    public SelectNoteLists() {
    }


    public SelectNoteLists(HashMap<Note, Pair<Boolean,Boolean>> map){
        init(map,"FINAL NOTE","NORMAL NOTE", "NON-PROMPT","PROMPT");
    }


    @Override
     public void init(HashMap<Note, Pair<Boolean,Boolean>> map, String firstSelected, String firstNonSelected , String secondSelected, String secondNonSelected){
        AddListCellFactory<Note> factory1 = (new AddNoteListCellFactory(firstList,secondList));
        RemoveListCellOptionsFactory<Note> factory2 = new RemoveNoteListCellOptionsFactory(map, firstSelected, firstNonSelected, secondSelected, secondNonSelected);
        setFirstListFactory(factory1);
        setSecondListFactory(factory2);
        setSecondList(map);

    }


}
