package Code.Controller.home.notes;

import Code.Model.Note;
import Code.View.NoteView.NoteTextCell;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class NoteTextCellFactory implements Callback<ListView<Note>, ListCell<Note>> {
    @Override
    public ListCell<Note> call(ListView<Note> param) {
        ListCell<Note> cell  = new NoteTextCell(this);
        cell.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(menu!=null)
                    menu.hide();

                if(event.getButton() == MouseButton.SECONDARY){
                    cell.getStylesheets().add("/Code/View/css/context-menu.css");
                    menu = new NotesRightClickMenu(cell.getItem(),cell);
                    menu.show(cell,event.getScreenX(),event.getScreenY());
                }
            }
        });
        return cell;
    }

    ContextMenu menu;

    ListView<Note>list;

    public ListView<Note> getList(){
        return this.list;
    }

    public NoteTextCellFactory(ListView<Note>list){
        this.list = list;
    }
}
