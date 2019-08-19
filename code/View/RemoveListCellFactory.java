package Code.View;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;


public class RemoveListCellFactory<T extends ObservableObject> implements Callback<ListView<T>, ListCell<T>> {

    ListCellCode userCode;

    @Override
    public ListCell<T> call(ListView<T> param) {
        RemoveNormalListCell cell =  new RemoveNormalListCell(param,userCode);
        return cell;
    }

    ListView<ObservableObject> view;
    String location;

    public RemoveListCellFactory(ListCellCode userCode){
        this.userCode = userCode;
    }


}
