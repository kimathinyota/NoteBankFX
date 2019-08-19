package Code.Controller;

import Code.Model.Text;
import javafx.concurrent.Task;

public class LoadTextPageTask extends Task<String> {

    Text text;

    @Override
    protected String call() throws Exception {
        return text.loadContent();
    }

    public LoadTextPageTask(Text text){
        this.text = text;
    }

}
