package Code.View;

import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class RemoveListCellOptionsFactory<T extends ObservableObject,K> implements Callback<ListView<T>, ListCell<T>> {
    protected HashMap<T,K> assignedOptions;

    protected ListCellCode userCode;

    protected ListView<T> lastList;

    public void update(T item, K options){
        assignedOptions.put(item,options);
    }


    public void setOptions(HashMap<T,K> assignedOptions){
        this.assignedOptions = assignedOptions;
    }


    public void remove(T item){
        assignedOptions.remove(item);
    }

    public Collection<K> getOptions(){
        List<K> options = FXCollections.observableArrayList(assignedOptions.values());
        for(K o: this.options){
            if(!options.contains(o)){
                options.add(o);
            }
        }
        return options;
    }


    public void clear(){
        assignedOptions.clear();
    }

    protected Collection<K> options;

    public HashMap<T,K> getMap(){
        return assignedOptions;
    }


    public ListView<T> getLastListView(){
        return this.lastList;
    }

    @Override
    public ListCell<T> call(ListView<T> param) {
        this.lastList = param;
        return new RemoveOptionsListCell<>(param,this,userCode);
    }

    public RemoveListCellOptionsFactory(HashMap<T,K>map, Collection<K>options,ListCellCode userCode){
        if(map==null){
            assignedOptions = new HashMap<>();
            this.options = options;
            this.userCode = userCode;
            return;
        }
        assignedOptions = map;
        this.options = options;
        this.userCode = userCode;
    }

}
