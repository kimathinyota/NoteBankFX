package Code.View.components.TitleNode;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class TitleChoiceBox<T> extends HBox {

    public ChoiceBox<T> getRightChoice() {
        return rightChoice;
    }

    public Label getLeftLabel() {
        return leftLabel;
    }

    @FXML protected ChoiceBox<T> rightChoice;
    @FXML protected Label leftLabel;


    protected void initialize(){

    }

    public TitleChoiceBox(){
        loadFXML();
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Code/View/components/LeftNodeRightNode/labelchoice.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
