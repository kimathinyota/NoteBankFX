package Code.Controller;

import Code.Model.Book;
import Code.Model.Note;
import Code.View.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

class AddNoteListCell extends AddListCell<Note>{


    @Override
    protected void handleLabelButtonClick(ActionEvent e) {
        Controller.getInstance().displayNotes(FXCollections.singletonObservableList(item),ViewMode.ViewLimited);
    }

    public AddNoteListCell(ListView<Note> listone, ListView<Note> listtwo, ListCellCode userCode) {
        super(listone, listtwo, userCode);
    }

    @Override
    protected void handleAddAction(ActionEvent e) {
        super.handleAddAction(e);
        if(item instanceof Book){
            this.listTwo.getItems().remove(item);
            Controller.getInstance().displayAndSelectNotes(FXCollections.singletonObservableList(item),listTwo);
        }
    }
}
class AddNoteListCellFactory extends AddListCellFactory<Note> {

    public AddNoteListCellFactory(ListView<Note> listOne, ListView<Note> listTwo, ListCellCode userCode) {
        super(listOne, listTwo, userCode);
    }

    @Override
    public ListCell<Note> call(ListView<Note> param) {
        AddNoteListCell cell = new AddNoteListCell(listOne,listTwo,userCode);
        return cell;
    }


}

class RemoveNoteOptionListCell extends RemoveOptionsListCell<Note>{

    @Override
    protected void handleLabelButtonClick(ActionEvent e) {
        Controller.getInstance().displayNotes(FXCollections.singletonObservableList(getItem()),ViewMode.ViewLimited);
    }

    public RemoveNoteOptionListCell(ListView<Note> t, RemoveListCellOptionsFactory factory, ListCellCode userCode) {
        super(t, factory, userCode);
    }
}



class RemoveNoteListCellOptionsFactory extends RemoveListCellOptionsFactory<Note, String>{

    @Override
    public ListCell<Note> call(ListView<Note> param) {
        return new RemoveNoteOptionListCell(param,this,userCode);
    }

    public RemoveNoteListCellOptionsFactory(HashMap<Note, String> map, Collection<String> options, ListCellCode userCode) {
        super(map, options, userCode);
    }
}

public class SelectNoteLists extends SelectWithOptionsLists<Note, String> {

    public SelectNoteLists(HashMap<Note, String> map, List<String> options, ListCellCode userCode) {
        super(map, options, userCode);
    }

    public SelectNoteLists(List<String> options, ListCellCode userCode) {
        super(options, userCode);
    }


    @Override
    protected void init(HashMap<Note, String> map){
        AddListCellFactory<Note> factory1 = (new AddNoteListCellFactory(this.getFirstListView(),this.getSecondListView(),userCode));
        RemoveListCellOptionsFactory<Note, String> factory2 = new RemoveNoteListCellOptionsFactory(map,options,userCode);
        init(map,factory1,factory2);
    }


}
