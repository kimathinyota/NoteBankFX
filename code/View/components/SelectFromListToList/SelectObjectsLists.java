package Code.View.components.SelectFromListToList;

import Code.View.ObservableObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public abstract class SelectObjectsLists<T extends ObservableObject> extends GridPane {

    protected ObservableList<T> listOne;
    @FXML protected ListView<T> firstList;
    @FXML protected ListView<T> secondList;
    @FXML protected TextField search;

    public void setFirstList(Collection<T>list) {
        listOne = FXCollections.observableArrayList(list);
        refresh();
    }

    public void setSecondList(Collection<T> list) {
        secondList.setItems( FXCollections.observableArrayList(list));
    }

    public List<T> getSecondList(){
        return this.secondList.getItems();
    }

    protected void refresh(){
        ArrayList<T> firstList = new ArrayList<>() ;
        for(T o: listOne){
            if(o.getDisplayName().contains(search.getText())){
                firstList.add(o);
            }
        }
        this.firstList.setItems(FXCollections.observableArrayList(firstList));
    }

    public ListView getSecondListView(){
        return secondList;
    }

    public ListView getFirstListView(){
        return firstList;
    }

    public void setFirstListFactory(Callback<ListView<T>, ListCell<T>> factory){
        firstList.setCellFactory(factory);
    }

    public void setSecondListFactory(Callback<ListView<T>, ListCell<T>> factory){
        secondList.setCellFactory(factory);
    }

    @FXML protected void handleSearchEnterPress(KeyEvent e){
        refresh();
    }

    @FXML protected void handleSearchAction(ActionEvent e){
        refresh();
    }

    public void load(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Code/View/components/SelectFromListToList/SelectObjectLists.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SelectObjectsLists(){
        load();
        search.textProperty().addListener((observable, oldValue, newValue) -> {if(!newValue.equals(oldValue)){
            refresh();
        }
        });
    }

}