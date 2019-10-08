package Code.Controller;

import Code.View.ObservableObject;

public class DisplayString  implements ObservableObject {
    @Override
    public String getDisplayName() {
        return string;
    }

    @Override
    public String getMindMapName() {
        return string;
    }

    @Override
    public String getTreeName() {
        return string;
    }

    @Override
    public boolean contains(Object object) {
        return (object instanceof CharSequence) && contains((CharSequence) object);
    }

    String string;

    public DisplayString(String string){
        this.string = string;
    }


}
