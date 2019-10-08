package Code.View.components.SelectFromListToList;


import Code.View.ObservableObject;

public class SelectNormalLists<T extends ObservableObject> extends SelectObjectsLists<T> {



    public SelectNormalLists(){
        this.setFirstListFactory(new AddListCellFactory(this.getFirstListView(),this.getSecondListView()));
        this.setSecondListFactory(new RemoveListCellFactory());
    }

    public SelectNormalLists(AddListCellFactory<T> factory1, RemoveListCellFactory<T> factory2){
        this.setFirstListFactory(factory1);
        this.setSecondListFactory(factory2);
    }


    public SelectNormalLists(AddListCellFactory<T> factory1){
        this.setFirstListFactory(factory1);
        this.setSecondListFactory(new RemoveListCellFactory());
    }



}
