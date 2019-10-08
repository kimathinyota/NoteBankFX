package Code.View.components.SelectFromListToList;

import Code.View.ObservableObject;
import Code.View.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddListCell<T extends ObservableObject> extends ListCell<T> {

    @FXML protected Label label;
    @FXML protected Button add;

    @FXML protected Button labelButton;

    @FXML protected void handleLabelButtonClick(ActionEvent e){

    }


    public AddListCell(ListView<T> listone, ListView<T> listtwo) {
        super();
        loadFXML();
        this.listOne = listone;
        this.listTwo = listtwo;
    }

    protected ListView<T> listOne,listTwo;

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Code/View/components/SelectFromListToList/AddListTag.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean contains(ListView<T> items){
        for(T item: items.getItems()){
            if(item.contains(item)){
                return true;
            }
        }
        return false;
    }

    protected T find(ListView<T> items){
        for(T item: items.getItems()){
            if(item.contains(this.item)){
                return item;
            }
        }
        return null;
    }







    @FXML
    protected void handleAddAction(ActionEvent e){

        T foundItem = find(listTwo);

        if(foundItem!=null){
            View.displayPopUpForTime("Information", " Item has already been added to the list and is contained in " + foundItem,3,this, 250);
            return;
        }

        if(listTwo.getItems().contains(item)){
            View.displayPopUpForTime("Information", " Item has already been added to the list ", 3, this, 250);
            return;
        }

        List<T> remove = new ArrayList<>();

        for(T i: listTwo.getItems()){
            if(item.contains(i)){
                remove.add(i);
            }
        }

        listTwo.getItems().removeAll(remove);

        listTwo.getItems().add(item);

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
