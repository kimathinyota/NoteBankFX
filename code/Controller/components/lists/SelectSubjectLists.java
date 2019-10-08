package Code.Controller.components.lists;

import Code.Model.Model;
import Code.Model.Note;
import Code.Model.Subject;
import Code.View.components.SelectFromListToList.*;
import javafx.event.ActionEvent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.util.List;


class AddSubjectListCell extends AddListCell<Subject> {

    Note note;

    public AddSubjectListCell(ListView<Subject> listone, ListView<Subject> listtwo, Note note) {
        super(listone, listtwo);
        this.note = note;
    }

    @Override
    protected void handleAddAction(ActionEvent e) {
        super.handleAddAction(e);
        Model.getInstance().addToSubject(item.getName(),note);
    }
}


class AddSubjectListFactory extends AddListCellFactory<Subject> {

    Note note;

    public AddSubjectListFactory(ListView<Subject> listOne, ListView<Subject> listTwo, Note note) {
        super(listOne, listTwo);
        this.note = note;
    }

    @Override
    public ListCell<Subject> call(ListView<Subject> param) {
        return new AddSubjectListCell(listOne,listTwo,note);
    }
}


class RemoveSubjectListCell extends RemoveNormalListCell<Subject> {

    Note note;

    public RemoveSubjectListCell(ListView<Subject> t, Note note) {
        super(t);
        this.note = note;
    }


    @Override
    protected void handleRemoveAction(ActionEvent e) {
        super.handleRemoveAction(e);
        Model.getInstance().removeFromSubject(getItem().getName(),note);
    }
}

class RemoveSubjectListFactory extends RemoveListCellFactory<Subject> {

    Note note;
    @Override
    public ListCell<Subject> call(ListView<Subject> param) {
        return new RemoveSubjectListCell(param,note);
    }

    public RemoveSubjectListFactory(Note note) {
        super();
        this.note = note;
    }
}


public class SelectSubjectLists extends SelectNormalLists<Subject> {

    public SelectSubjectLists(Note note, List<Subject> subjects) {
        setFirstListFactory(new AddSubjectListFactory(firstList,secondList,note));
        setSecondListFactory(new RemoveSubjectListFactory(note));
        setSecondList(subjects);
    }

}
