package Code.View.components.TitleNode;

import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.util.Map;

public class TitleNodeListFactory implements Callback<ListView<Map.Entry<Node, Node>>, ListCell<Map.Entry<Node, Node>>> {

    @Override
    public ListCell<Map.Entry<Node, Node>> call(ListView<Map.Entry<Node, Node>> param) {
        return new TitleNodeListCell();
    }

}
