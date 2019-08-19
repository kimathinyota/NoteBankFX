package Code.View;


import javafx.collections.FXCollections;


import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class SelectWithOptionsLists<T extends ObservableObject,K> extends SelectObjectsLists<T> {

    protected RemoveListCellOptionsFactory<T,K> factory;

    protected List<K> options;

    public HashMap<T, K> getMap(){
        return factory.getMap();
    }


    protected void init(HashMap<T,K> map, AddListCellFactory<T> addFactory, RemoveListCellOptionsFactory<T,K> removeFactory){
        this.setFirstListFactory(addFactory);
        factory = removeFactory;
        this.setSecondListFactory(factory);
        if(map!=null){
            secondList.setItems( FXCollections.observableArrayList(map.keySet()));
        }
    }


    protected void init(HashMap<T,K> map){
        AddListCellFactory<T> factory1 = (new AddListCellFactory(this.getFirstListView(),this.getSecondListView(),userCode));
        RemoveListCellOptionsFactory<T,K> factory2 = new RemoveListCellOptionsFactory(map,options,userCode);
        init(map,factory1,factory2);
    }


    public void setSecondList(Collection<T> list){
        factory.clear();
        this.getSecondListView().getItems().clear();
        for(T t: list){
            factory.update(t,(options.isEmpty() ? null : options.get(0)));
            this.getSecondListView().getItems().add(t);
        }
    }


    public void setSecondList(HashMap<T,K>secondList) {
        factory.setOptions(secondList);
        this.getSecondListView().getItems().clear();
        for(T t: secondList.keySet()){
            this.getSecondListView().getItems().add(t);
        }
    }



    public SelectWithOptionsLists(HashMap<T,K> map, List<K>options, ListCellCode userCode){
        this.options = options;
        this.userCode = userCode;
        init(map);
    }

    public SelectWithOptionsLists(List<K> options, ListCellCode userCode){
        this.options = options;
        this.userCode = userCode;
        init(null);
    }


    public SelectWithOptionsLists(List<K> options, AddListCellFactory<T> addFactory, RemoveListCellOptionsFactory<T,K> removeFactory, ListCellCode userCode){
        this.options = options;
        this.userCode = userCode;
        init(null,addFactory,removeFactory);
    }





    protected ListCellCode userCode;

}
