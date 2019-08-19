package Code.View;

import javafx.scene.control.ListView;

public class RemoveNormalListCell<T extends ObservableObject> extends RemoveListCell<T>{
    public RemoveNormalListCell(ListView<T> t,ListCellCode userCode) {
        super(t, "/Code/View/RemoveListTag.fxml",userCode);
    }
}

