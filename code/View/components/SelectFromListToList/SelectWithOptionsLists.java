package Code.View.components.SelectFromListToList;


import Code.View.ObservableObject;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.Pair;

import java.util.Collection;
import java.util.HashMap;

public class SelectWithOptionsLists<T extends ObservableObject> extends SelectObjectsLists<T> {

    protected RemoveListCellOptionsFactory<T> factory;

    String firstSelected,firstNonSelected,secondSelected, secondNonSelected;


    public void setSecondList(Collection<T> list){
        factory.clear();
        this.getSecondListView().getItems().clear();
        for(T t: list){
            factory.update(t,false,false);
            this.getSecondListView().getItems().add(t);
        }
    }

    @Override
    public void setSecondListFactory(Callback<ListView<T>, ListCell<T>> factory) {
        if(!(factory instanceof RemoveListCellOptionsFactory)){
            return;
        }
        this.factory = (RemoveListCellOptionsFactory) factory;
        super.setSecondListFactory(factory);
    }


    public void setSecondList(HashMap<T, Pair<Boolean,Boolean>> secondList) {
        factory.setOptions(secondList);
        this.getSecondListView().getItems().clear();
        for(T t: secondList.keySet()){
            this.getSecondListView().getItems().add(t);
        }
    }


    public void init(HashMap<T,Pair<Boolean,Boolean>> map, String firstSelected, String firstNonSelected , String secondSelected, String secondNonSelected){
        setFirstListFactory((new AddListCellFactory(this.getFirstListView(),this.getSecondListView())));
        setSecondListFactory(new RemoveListCellOptionsFactory(map,firstSelected,firstNonSelected,secondSelected,secondNonSelected));
        setSecondList(map);
    }

    public SelectWithOptionsLists( ){
        super();
    }

}
