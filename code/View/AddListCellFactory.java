package Code.View;

import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class AddListCellFactory<T extends ObservableObject> implements Callback<ListView<T>, ListCell<T>> {

    protected ListView<T> listTwo, listOne;
    protected ListCellCode userCode;

    public AddListCellFactory(ListView<T>listOne, ListView<T>listTwo, ListCellCode userCode){
        this.listTwo = listTwo;
        this.listOne = listOne;
        this.userCode=userCode;
    }

    @Override
    public ListCell<T> call(ListView<T> param) {
        AddListCell cell =  new AddListCell(listOne,listTwo,userCode);
        return cell;
    }
}