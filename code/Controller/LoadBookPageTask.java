package Code.Controller;

import Code.Model.Book;

import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class LoadBookPageTask extends Task<Image> {

    private int page;
    private Book book;

    @Override
    protected Image call() throws Exception {
        return SwingFXUtils.toFXImage(((Book) book).loadPage(page), null);
    }

    public LoadBookPageTask(Book book, int page){
        this.page = page;
        this.book = book;
    }
}
