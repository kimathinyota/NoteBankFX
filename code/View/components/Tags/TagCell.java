package Code.View.components.Tags;

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


public class TagCell extends ListCell<String> {

    @FXML protected Label tagName;
    @FXML protected Button exit;



    public TagCell(TagCellFactory t) {
        super();
        loadFXML();
        exit.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                t.getList().getItems().removeAll(tagName.getText());
            }
        });
        this.setWrapText(true);
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Code/View/components/Tags/tag_cell.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }





    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
        else {
            tagName.setText(item);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }
}
