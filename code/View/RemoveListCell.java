package Code.View;

import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;

public abstract class RemoveListCell<T extends ObservableObject> extends ListCell<T> {

    @FXML protected Label label;
    @FXML protected Button remove;

    ListCellCode userCode;

    public RemoveListCell(ListView<T> t, String location, ListCellCode userCode) {
        super();
        loadFXML(location);
        this.view = t;
        this.userCode = userCode;
    }

    private ListView<T> view;

    private void loadFXML(String location) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML protected Button labelButton;


    @FXML protected void handleLabelButtonClick(ActionEvent e){

    }



    @FXML protected void handleRemoveAction(ActionEvent e){
        for(int i = 0; i< this.view.getItems().size(); i++){
            if(this.view.getItems().get(i).getDisplayName().equals(label.getText())){
                this.view.getItems().remove(i);
            }
        }
        userCode.onRemoveClick();
    }




    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if(empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
        else {
            label.setText(item.getDisplayName());
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

}
