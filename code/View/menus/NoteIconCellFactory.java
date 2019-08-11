package Code.View.menus;

import Code.Model.Note;
import Code.View.NoteIconCell;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;



public class NoteIconCellFactory implements Callback<ListView<Note>, ListCell<Note>> {
    @Override
    public ListCell<Note> call(ListView<Note> param) {
        return new NoteIconCell(this);
    }

    ListView<Note>list;

    public ListView<Note> getList(){
        return this.list;
    }

    public NoteIconCellFactory(ListView<Note>list){
        this.list = list;
    }
}
