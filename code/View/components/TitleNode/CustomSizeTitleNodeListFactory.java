package Code.View.components.TitleNode;

import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.util.Map;

public class CustomSizeTitleNodeListFactory implements Callback<ListView<Map.Entry<Node, Node>>, ListCell<Map.Entry<Node, Node>>>  {



    @Override
    public ListCell<Map.Entry<Node, Node>> call(ListView<Map.Entry<Node, Node>> param) {
        ListCell<Map.Entry<Node, Node>> cell =  new TitleNodeListCell();
        cell.setPrefHeight(height);
        cell.setPrefWidth(width);
        return cell;
    }


    double width, height;



    public CustomSizeTitleNodeListFactory(double width, double height){
        this.width = width;
        this.height = height;
    }

}
