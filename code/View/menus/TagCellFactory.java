package Code.View.menus;

import Code.View.components.Tags.TagCell;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class TagCellFactory implements Callback<ListView<String>, ListCell<String>> {

    @Override
    public ListCell<String> call(ListView<String> param) {
        return new TagCell(this);
    }

    ListView<String>list;

    public ListView<String> getList(){
        return this.list;
    }

    public TagCellFactory(ListView<String>list){
        this.list = list;
    }

}
