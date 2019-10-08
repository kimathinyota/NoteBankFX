package Code.Controller.components.lists;

import Code.Controller.Controller;
import Code.Controller.Dialogs.ViewNotes.ViewMode;
import Code.Model.Book;
import Code.Model.Idea;
import Code.Model.Model;
import Code.Model.Note;
import Code.View.*;

import Code.View.components.SelectFromListToList.AddListCell;
import Code.View.components.SelectFromListToList.AddListCellFactory;
import Code.View.components.SelectFromListToList.RemoveListCellOptionsFactory;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class AddNoteIdeasListCell extends AddListCell<Note> {

    Idea idea;
    AddNoteIdeasListCellFactory factory;

    public AddNoteIdeasListCell(ListView<Note> listone, ListView<Note> listtwo, AddNoteIdeasListCellFactory factory, Idea idea) {
        super(listone, listtwo);
        this.factory = factory;
        this.idea = idea;
    }

    @Override
    protected void handleAddAction(ActionEvent e) {
        super.handleAddAction(e);
        if(item instanceof Book){

            Model.getInstance().removeFromIdea(idea,item);
            this.listTwo.getItems().remove(item);

            Controller.getInstance().displayAndSelectNotes(FXCollections.singletonObservableList(item),listTwo,idea);
            return;
        }
        Model.getInstance().addToIdea(idea,item,false,false);
    }


    @Override
    protected void handleLabelButtonClick(ActionEvent e) {
        Controller.getInstance().displayNotes(FXCollections.singletonObservableList(item), ViewMode.ViewLimited);
    }

}

class AddNoteIdeasListCellFactory extends AddNoteListCellFactory {

    Idea idea;

    public AddNoteIdeasListCellFactory(ListView<Note> listOne, ListView<Note> listTwo, Idea idea) {
        super(listOne, listTwo);
        this.idea = idea;
    }

    @Override
    public ListCell<Note> call(ListView<Note> param) {
        AddNoteIdeasListCell cell = new AddNoteIdeasListCell(listOne,listTwo,this,idea);
        return cell;
    }

}

class RemoveNoteIdeasOptionListCell extends RemoveNoteOptionListCell{

    Idea idea;
    RemoveListCellOptionsFactory factory;

    public Note item(){
        return super.getItem();
    }

    public void setFirstToggle(boolean toggle){
        this.firstToggle.setSelected(toggle);
    }

    public void setSecondToggle(boolean toggle){
        this.secondToggle.setSelected(toggle);
    }


    public RemoveNoteIdeasOptionListCell(ListView<Note> t, RemoveListCellOptionsFactory factory, Idea idea) {
        super(t, factory);
        this.idea = idea;
        this.factory = factory;
    }

    @Override
    protected void handleSecondToggleAction(ActionEvent e) {
        super.handleSecondToggleAction(e);
        Note note = getItem();
        if(note==null) return;
        Model.getInstance().addToIdea(idea,note,firstToggle.isSelected(),secondToggle.isSelected());
    }

    @Override
    protected void handleFirstToggleAction(ActionEvent e) {
        super.handleFirstToggleAction(e);
        Note note = getItem();
        if(note==null) return;
        if(firstToggle.isSelected()){
            ((RemoveNoteIdeaListCellOptionsFactory) factory).setFinalNote(note);
        }else {
            ((RemoveNoteIdeaListCellOptionsFactory) factory).resetFinalNote();
        }


        Model.getInstance().addToIdea(idea,note,firstToggle.isSelected(),secondToggle.isSelected());

    }

    @Override
    protected void handleRemoveAction(ActionEvent e) {
        super.handleRemoveAction(e);
        Note note = removedItem;
        if(note==null) return;

        ((RemoveNoteIdeaListCellOptionsFactory) factory).remove(note);

        System.out.println(idea + " " + note);
        Model.getInstance().removeFromIdea(idea,note);
    }

}

class RemoveNoteIdeaListCellOptionsFactory extends RemoveNoteListCellOptionsFactory{

    Idea idea;
    ListView<Note> list;

    List<RemoveNoteIdeasOptionListCell> cells;

    public Note getFinalNote() {
        return finalNote;
    }

    Note finalNote;

    public void setFinalNote(Note note){
        for(RemoveNoteIdeasOptionListCell c: cells){
            if(c.item()!=null && c.item().equals(finalNote)){
                c.setFirstToggle(false);
            }
        }
        this.finalNote = note;
    }

    public void resetFinalNote(){
        this.finalNote = null;
    }

    public void remove(Note note){
        if(finalNote!=null && finalNote.equals(note)){
            finalNote = null;
        }
        for(RemoveNoteIdeasOptionListCell c: cells){
            if(c.item()!=null && c.item().equals(note)){
                cells.remove(c);
                return;
            }
        }
    }

    public RemoveNoteIdeaListCellOptionsFactory(HashMap<Note, Pair<Boolean, Boolean>> map, String firstSelected, String firstNonSelected , String secondSelected, String secondNonSelected,  Idea idea) {
        super(map,firstSelected,firstNonSelected,secondSelected,secondNonSelected);
        this.idea = idea;
        cells = new ArrayList<>();
    }

    @Override
    public ListCell<Note> call(ListView<Note> param) {
        this.list = param;
        RemoveNoteIdeasOptionListCell cell =  new RemoveNoteIdeasOptionListCell(param,this, idea);
        cells.add(cell);
        return cell;
    }

}

public class SelectNoteForIdeaLists extends SelectNoteLists  {

    Idea idea;

    public SelectNoteForIdeaLists(HashMap<Note, Pair<Boolean, Boolean>> map, Idea idea) {

        super();
        this.idea = idea;
        init(map,"FINAL NOTE","NORMAL NOTE", "NON-PROMPT","PROMPT");

    }

    public SelectNoteForIdeaLists(HashMap<Note,Pair<Boolean,Boolean>> map ){
        this.idea = idea;
    }

    @Override
    public void init(HashMap<Note, Pair<Boolean,Boolean>> map, String firstSelected, String firstNonSelected , String secondSelected, String secondNonSelected){

        AddListCellFactory<Note> factory1 = new AddNoteIdeasListCellFactory(this.getFirstListView(),this.getSecondListView(),idea);
        RemoveListCellOptionsFactory<Note> factory2 = new RemoveNoteIdeaListCellOptionsFactory(map,firstSelected,firstNonSelected,secondSelected,secondNonSelected,idea);
        setFirstListFactory(factory1);

        Note finalNote = null;
        for(Note n: map.keySet()){
            if(map.get(n).getKey().equals(true)){
                finalNote = n;
                break;
            }
        }

        ((RemoveNoteIdeaListCellOptionsFactory) factory2).setFinalNote(finalNote);
        setSecondListFactory(factory2);
        setSecondList(map);

    }


}
