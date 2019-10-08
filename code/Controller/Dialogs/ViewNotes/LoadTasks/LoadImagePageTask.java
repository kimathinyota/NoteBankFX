package Code.Controller.Dialogs.ViewNotes.LoadTasks;

import javafx.concurrent.Task;
import javafx.scene.image.Image;

public class LoadImagePageTask extends Task<Image> {

    private Code.Model.Image image;

    @Override
    protected Image call() throws Exception {
        return new Image("file:"+image.getPath().toString());
    }

    public LoadImagePageTask(Code.Model.Image image){
        this.image = image;
    }
}
