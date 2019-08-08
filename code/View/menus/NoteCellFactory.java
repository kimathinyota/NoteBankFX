package Code.View.menus;

import Code.Model.Note;
import Code.View.NoteCell;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;



public class NoteCellFactory implements Callback<ListView<Note>, ListCell<Note>> {
    @Override
    public ListCell<Note> call(ListView<Note> param) {
        return new NoteCell(this);
    }

    ListView<Note>list;

    public ListView<Note> getList(){
        return this.list;
    }

    public NoteCellFactory(ListView<Note>list){
        this.list = list;
    }
}
