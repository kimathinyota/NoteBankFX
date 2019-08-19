package Code.View;

import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;

public class AddListCell<T extends ObservableObject> extends ListCell<T> {

    @FXML protected Label label;
    @FXML protected Button add;

    ListCellCode userCode;


    @FXML protected Button labelButton;


    @FXML protected void handleLabelButtonClick(ActionEvent e){

    }





    public AddListCell(ListView<T> listone, ListView<T> listtwo, ListCellCode userCode) {
        super();
        loadFXML();
        this.listOne = listone;
        this.listTwo = listtwo;
        this.userCode = userCode;
    }

    protected ListView<T> listOne,listTwo;

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Code/View/AddListTag.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




    private boolean contains(ListView<T> items){
        for(T item: items.getItems()){
            if(item.contains(item)){
                return true;
            }
        }
        return false;
    }



    @FXML
    protected void handleAddAction(ActionEvent e){

        if(listOne.getItems().contains(item) && !(listTwo.getItems().contains(item) || contains(listTwo))){
            listTwo.getItems().add(item);
        }

        userCode.onAddClick();

    }


    protected T item;

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
        else {
            this.item = item;
            label.setText(item.getDisplayName());
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

}
