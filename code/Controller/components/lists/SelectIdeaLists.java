package Code.Controller.components.lists;

import Code.Model.Idea;

import Code.Model.Model;
import Code.Model.Note;
import Code.View.*;
import Code.View.components.SelectFromListToList.*;
import javafx.event.ActionEvent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Pair;

import java.util.HashMap;

class AddIdeasListCell extends AddListCell<Idea> {

    Note note;

    public AddIdeasListCell(ListView<Idea> listone, ListView<Idea> listtwo, Note note) {
        super(listone, listtwo);
        this.note = note;
    }

    @Override
    protected void handleAddAction(ActionEvent e) {
        super.handleAddAction(e);
        Model.getInstance().addToIdea(item,note,false,false);
    }

}

class AddIdeasListCellFactory extends AddListCellFactory<Idea> {

    Note note;
    public AddIdeasListCellFactory(ListView<Idea> listOne, ListView<Idea> listTwo, Note note) {
        super(listOne, listTwo);
        this.note = note;

    }

    @Override
    public ListCell<Idea> call(ListView<Idea> param) {
        return new AddIdeasListCell(listOne,listTwo, note);
    }

}

class RemoveIdeasOptionListCell extends RemoveOptionsListCell<Idea> {

    Note note;

    public RemoveIdeasOptionListCell(ListView<Idea> t, RemoveListCellOptionsFactory factory, Note note) {
        super(t, factory);
        this.note = note;
    }


    @Override
    protected void handleSecondToggleAction(ActionEvent e) {
        super.handleSecondToggleAction(e);
        Idea idea = getItem();
        if(idea==null) return;
        Model.getInstance().addToIdea(idea,note,firstToggle.isSelected(),secondToggle.isSelected());
    }

    @Override
    protected void handleFirstToggleAction(ActionEvent e) {
        super.handleFirstToggleAction(e);
        Idea idea = getItem();
        if(idea==null) return;
        Model.getInstance().addToIdea(idea,note,firstToggle.isSelected(),secondToggle.isSelected());
    }

    @Override
    protected void handleRemoveAction(ActionEvent e) {
        super.handleRemoveAction(e);
        Model.getInstance().removeFromIdea(getItem(),note);
    }
}

class RemoveIdeaListCellOptionsFactory extends RemoveListCellOptionsFactory<Idea>{

    Note note;

    public RemoveIdeaListCellOptionsFactory(HashMap<Idea, Pair<Boolean, Boolean>> map, String firstSelected, String firstNonSelected , String secondSelected, String secondNonSelected, Note note) {
        super(map, firstSelected,firstNonSelected,secondSelected,secondNonSelected);
        this.note = note;
    }


    @Override
    public ListCell<Idea> call(ListView<Idea> param) {
        return new RemoveIdeasOptionListCell(param,this, note);
    }

}

public class SelectIdeaLists extends SelectWithOptionsLists<Idea> {

    Note note;

    public SelectIdeaLists(Note note) {
        this.note = note;
        init(new HashMap<>(),"FINAL NOTE","NORMAL NOTE", "PROMPT","NON-PROMPT");
    }

    public SelectIdeaLists(Note note, HashMap<Idea, Pair<Boolean,Boolean>> map) {
        this.note = note;
        init(map,"FINAL NOTE","NORMAL NOTE", "PROMPT","NON-PROMPT");
    }


    @Override
    public void init(HashMap<Idea, Pair<Boolean,Boolean>> map, String firstSelected, String firstNonSelected , String secondSelected, String secondNonSelected){
        AddListCellFactory<Idea> factory1 = new AddIdeasListCellFactory(firstList,secondList,note);
        RemoveListCellOptionsFactory<Idea> factory2 = new RemoveIdeaListCellOptionsFactory(map,firstSelected,firstNonSelected,secondSelected,secondNonSelected,note);
        setFirstListFactory(factory1);
        setSecondListFactory(factory2);
        setSecondList(map);
    }

}
