package Code.View.NoteView;

import Code.Model.Note;
import Code.Controller.home.notes.NoteTextCellFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

import java.io.IOException;

public class NoteTextCell extends ListCell<Note> {

    @FXML protected Label name;
    @FXML protected Label type;



    public NoteTextCell(NoteTextCellFactory t) {
        super();
        loadFXML();
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Code/View/NoteView/note_cell_text.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }





    @Override
    protected void updateItem(Note item, boolean empty) {
        super.updateItem(item, empty);
        if(empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
        else {
            name.setText(item.getName());
            this.type.setText(item.getType());
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }
}
