package Code.View.components.SelectFromListToList;

import Code.View.ObservableObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;

public abstract class RemoveListCell<T extends ObservableObject> extends ListCell<T> {

    @FXML protected Label label;
    @FXML protected Button remove;


    public RemoveListCell(ListView<T> t, String location) {
        super();
        loadFXML(location);
        this.view = t;
    }

    protected ListView<T> view;

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
