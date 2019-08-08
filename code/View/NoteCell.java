package Code.View;

import Code.Model.NoteType;
import Code.View.menus.NoteCellFactory;
import Code.View.menus.TagCellFactory;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import java.io.IOException;


import Code.Model.Note;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;


public class NoteCell extends ListCell<Note> {

    @FXML protected Label name;

    /*
    @FXML protected ImageView image;
    @FXML protected ImageView book;
    @FXML protected ImageView text;

*/

    public void setView(boolean image, boolean book, boolean text){
        /*
        this.image.setVisible(image);
        this.book.setVisible(book);
        this.text.setVisible(text);
        */
    }

    public void setImage(NoteType type){

        System.out.println("Type: " + type);
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


    public NoteCell(NoteCellFactory t) {
        super();
        loadFXML();
        System.out.println("Reinstantiated");
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("note_cell2.fxml"));
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

        System.out.println("Baby girl good evening: " + empty + " " + item);
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