package Code.View;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;

public class RemoveOptionsListCell<T extends ObservableObject> extends ListCell<T> {


    RemoveListCellOptionsFactory factory;

    ListCellCode userCode;

    @FXML protected Button labelButton;


    @FXML protected void handleLabelButtonClick(ActionEvent e){

    }

    public RemoveOptionsListCell(ListView<T> t, RemoveListCellOptionsFactory factory, ListCellCode userCode) {
        super();
        this.userCode = userCode;
        loadFXML("/Code/View/RemoveOptionsListTag.fxml");
        this.view = t;
        this.factory = factory;

        init();

    }


    public void init(){

        options.setItems(FXCollections.observableArrayList(factory.getOptions()));
        options.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.equals(oldValue) && item!=null){
                System.out.println("CHAAANGED: " + item + " , " + newValue);
                factory.update(item,newValue);
                if(userCode!=null)
                    userCode.onOptionChange();
            }
        } );
    }




    public String getSelectedOption(){
        return options.getSelectionModel().getSelectedItem();
    }
    @FXML protected ComboBox<String> options;




    @FXML protected Label label;


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


    T item;

    @FXML protected void handleRemoveAction(ActionEvent e){
        this.view.getItems().remove(item);
        factory.remove(item);
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
            this.item = item;
            String i = (String) factory.getMap().get(item);
            if(i!=null){
                options.getSelectionModel().select(i);
            }else {
                options.getSelectionModel().select(0);
                this.userCode.onOptionChange();
            }
            label.setText(item.getDisplayName());
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);


        }
    }

}
