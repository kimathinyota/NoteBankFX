package Code.View.components.SelectFromListToList;

import Code.View.ObservableObject;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;


public class RemoveListCellFactory<T extends ObservableObject> implements Callback<ListView<T>, ListCell<T>> {


    @Override
    public ListCell<T> call(ListView<T> param) {
        RemoveNormalListCell cell =  new RemoveNormalListCell(param);
        return cell;
    }

    ListView<ObservableObject> view;
    String location;

    public RemoveListCellFactory(){
    }


}
