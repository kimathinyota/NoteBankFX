package Code.View.menus;

import Code.Model.Note;
import Code.View.NoteView.NoteTextCell;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class NoteTextCellFactory implements Callback<ListView<Note>, ListCell<Note>> {
    @Override
    public ListCell<Note> call(ListView<Note> param) {
        return new NoteTextCell(this);
    }

    ListView<Note>list;

    public ListView<Note> getList(){
        return this.list;
    }

    public NoteTextCellFactory(ListView<Note>list){
        this.list = list;
    }
}
