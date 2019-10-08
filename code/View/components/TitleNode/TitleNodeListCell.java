package Code.View.components.TitleNode;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Map;

public class TitleNodeListCell extends ListCell<Map.Entry<Node, Node>> {


    @FXML protected BorderPane itemPane, titlePane;


    public TitleNodeListCell(){
        loadFXML();
    }


    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Code/View/components/TitleNode/TitleNodeListCell.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




    @Override
    protected void updateItem(Map.Entry<Node, Node> item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
        else {
            titlePane.setCenter(item.getKey());
            this.itemPane.setCenter(item.getValue());
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }

    }


}

