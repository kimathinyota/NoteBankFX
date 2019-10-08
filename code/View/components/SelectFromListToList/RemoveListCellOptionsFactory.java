package Code.View.components.SelectFromListToList;

import Code.View.ObservableObject;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.Pair;

import java.util.HashMap;

public class RemoveListCellOptionsFactory<T extends ObservableObject> implements Callback<ListView<T>, ListCell<T>> {


    String firstSelected,firstNonSelected,secondSelected, secondNonSelected;

    public String getFirstSelected() {
        return firstSelected;
    }

    public String getFirstNonSelected() {
        return firstNonSelected;
    }

    public String getSecondSelected() {
        return secondSelected;
    }

    public String getSecondNonSelected() {
        return secondNonSelected;
    }

    protected HashMap<T, Pair<Boolean,Boolean>> assignedOptions;

    public void update(T item, boolean firstToggle, boolean secondToggle) {
        assignedOptions.put(item, new Pair(firstToggle,secondToggle));
    }

    public Pair<Boolean,Boolean> getPair(T item){
        return assignedOptions.get(item);
    }

    public void setOptions(HashMap<T, Pair<Boolean,Boolean>> assignedOptions) {
        this.assignedOptions = assignedOptions;
    }

    public void remove(T item) {
        assignedOptions.remove(item);
    }


    public void clear() {
        assignedOptions.clear();
    }

    public HashMap<T, Pair<Boolean,Boolean>> getMap() {
        return assignedOptions;
    }

    @Override
    public ListCell<T> call(ListView<T> param) {
        return new RemoveOptionsListCell<>(param, this);
    }

    public RemoveListCellOptionsFactory(HashMap<T, Pair<Boolean,Boolean>> map, String firstSelected, String firstNonSelected,String secondSelected, String secondNonSelected) {
        this.firstNonSelected = firstNonSelected;
        this.firstSelected = firstSelected;
        this.secondNonSelected = secondNonSelected;
        this.secondSelected = secondSelected;

        if (map == null) {
            assignedOptions = new HashMap<>();
            return;
        }

        assignedOptions = map;

    }


}
