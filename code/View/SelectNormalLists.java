package Code.View;



public class SelectNormalLists<T extends ObservableObject> extends SelectObjectsLists<T> {



    public SelectNormalLists(ListCellCode userCode){
        this.setFirstListFactory(new AddListCellFactory(this.getFirstListView(),this.getSecondListView(),userCode));
        this.setSecondListFactory(new RemoveListCellFactory(userCode));
    }



}
