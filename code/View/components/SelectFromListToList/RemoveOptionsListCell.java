package Code.View.components.SelectFromListToList;

import Code.View.ObservableObject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.util.Pair;

import java.io.IOException;

public class RemoveOptionsListCell<T extends ObservableObject> extends ListCell<T> {

    RemoveListCellOptionsFactory factory;

    @FXML protected Button labelButton;
    @FXML protected ToggleButton firstToggle;
    @FXML protected ToggleButton secondToggle;

    @FXML protected Label firstSelectedLabel;
    @FXML protected Label firstNonSelectedLabel;
    @FXML protected Label secondSelectedLabel;
    @FXML protected Label secondNonSelectedLabel;

    @FXML protected Label label;

    private ListView<T> view;

    private T item;


    @FXML protected void handleLabelButtonClick(ActionEvent e){

    }

    @FXML protected void handleSecondToggleAction(ActionEvent e){
        factory.update(item,firstToggle.isSelected(),secondToggle.isSelected());
    }


    @FXML protected void handleFirstToggleAction(ActionEvent e){
        factory.update(item,firstToggle.isSelected(),secondToggle.isSelected());

    }


    public RemoveOptionsListCell(ListView<T> t, RemoveListCellOptionsFactory factory) {
        super();
        loadFXML("/Code/View/components/SelectFromListToList/RemoveOptionsListTag.fxml");
        this.view = t;
        this.factory = factory;

        init();

    }

    public void init(){

        this.firstSelectedLabel.setText(factory.getFirstSelected());
        this.firstNonSelectedLabel.setText(factory.getFirstNonSelected());
        this.secondSelectedLabel.setText(factory.getSecondSelected());
        this.secondNonSelectedLabel.setText(factory.getSecondNonSelected());

    }



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


    protected T removedItem;


    @FXML protected void handleRemoveAction(ActionEvent e){
        this.removedItem = item;
        this.view.getItems().remove(removedItem);
        factory.remove(removedItem);
    }


    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if(empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
        else {
            this.item = item;

            Pair<Boolean,Boolean> i = factory.getPair(item);

            if(i!=null){

                firstToggle.setSelected(i.getKey());
                secondToggle.setSelected(i.getValue());

            }else {

                firstToggle.setSelected(false);
                secondToggle.setSelected(false);

            }

            label.setText(item.getDisplayName());
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        }

    }




}
