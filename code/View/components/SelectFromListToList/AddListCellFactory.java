package Code.View.components.SelectFromListToList;

import Code.View.ObservableObject;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class AddListCellFactory<T extends ObservableObject> implements Callback<ListView<T>, ListCell<T>> {

    protected ListView<T> listTwo, listOne;

    public AddListCellFactory(ListView<T>listOne, ListView<T>listTwo){
        this.listTwo = listTwo;
        this.listOne = listOne;
    }

    @Override
    public ListCell<T> call(ListView<T> param) {
        AddListCell cell =  new AddListCell(listOne,listTwo);
        return cell;
    }
}