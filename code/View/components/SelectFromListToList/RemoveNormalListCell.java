package Code.View.components.SelectFromListToList;

import Code.View.ObservableObject;
import javafx.scene.control.ListView;

public class RemoveNormalListCell<T extends ObservableObject> extends RemoveListCell<T>{
    public RemoveNormalListCell(ListView<T> t) {
        super(t, "/Code/View/components/SelectFromListToList/RemoveListTag.fxml");
    }
}

