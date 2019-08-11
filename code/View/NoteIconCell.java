package Code.View;

import Code.Controller.View;
import Code.Model.NoteType;
import Code.View.menus.NoteIconCellFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import java.io.IOException;


import Code.Model.Note;


public class NoteIconCell extends ListCell<Note> {

    @FXML protected Label name;

    View view;


    @FXML protected Button image;
    @FXML protected Button book;
    @FXML protected Button text;


    public void setView(boolean image, boolean book, boolean text){
        this.image.setVisible(image);
        this.book.setVisible(book);
        this.text.setVisible(text);
    }

    public void setImage(NoteType type){
        switch (type){
            case Book:
                setView(false,true,false);
                break;
            case Image:
                setView(true,false,false);
                break;
            case Text:
                setView(false,false,true);
                break;
        }


    }


    public NoteIconCell(NoteIconCellFactory t) {
        super();
        loadFXML();
        this.view = view;
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("note_cell3.fxml"));
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
            setImage(item.getNoteType());
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }
}