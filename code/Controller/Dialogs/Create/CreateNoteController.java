package Code.Controller.Dialogs.Create;
import Code.Controller.Controller;
import Code.Model.Book;
import Code.Model.Model;
import Code.Model.Note;
import Code.Model.Text;
import Code.View.View;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.util.Collections;


/**
 * Controller handling CreateNote window
 */
public class CreateNoteController{
    @FXML Button create;
    @FXML Button cancel;
    @FXML Button upload;
    @FXML HTMLEditor Editor;
    @FXML TextField Name;
    @FXML Label fileName, title;
    @FXML Pane window;
    @FXML TabPane tabPane;

    @FXML Tab textTab, otherTab;

    Model model;

    File loadedFile;

    /**
     * Will clear the whole window:
     * Clears: editor, name field, filename label
     */
    public void clear(){
        Editor.setHtmlText("<html><head></head><body contenteditable=\"true\"></body></html>");
        Name.clear();
        fileName.setText("No file selected");
        this.tabPane.getSelectionModel().select(0   );
    }

    /**
     * Will open a file chooser dialog to let user choose file
     * @param e
     */
    @FXML void handleUploadAction(ActionEvent e){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(imageFilter);
        fileChooser.getExtensionFilters().add(bookFilter);
        fileChooser.setTitle("Upload note");
        this.loadedFile = fileChooser.showOpenDialog(window.getScene().getWindow());
        this.fileName.setText(this.loadedFile.getName());
    }

    /**
     * Will clear and hide window on cancel button press
     * @param e
     */
    @FXML void handleCancelAction(ActionEvent e){
        clear();
        window.setVisible(false);
    }


    boolean isEditing;

    public void create(){
        isEditing = false;
        this.Name.setDisable(false);
        window.setVisible(true);
        this.title.setText("Create Note");
        this.create.setText("CREATE");
        this.tabPane.getTabs().clear();
        this.tabPane.getTabs().add(textTab);
        this.tabPane.getTabs().add(otherTab);
        this.tabPane.getSelectionModel().select(0   );


    }

    Note note;

    public void edit(Note note){
        if(!(note instanceof Text)){
            return;
        }

        window.setVisible(true);

        isEditing = true;
        Name.setText(note.getName());
        Editor.setHtmlText(  ((Text) note).loadContent()    );


        this.Name.setDisable(true);
        this.title.setText("Edit Note");
        this.create.setText("UPDATE");
        this.note = note;
        this.tabPane.getTabs().clear();
        this.tabPane.getTabs().add(textTab);
        this.tabPane.getSelectionModel().select(0   );


    }


    /**
     * Called on Create button click
     * Will use input information and model to create and add note
     * @param e
     */
    @FXML void handleCreateAction(ActionEvent e){

        if(!isEditing){
            if(!isValidName()){
                View.displayPopUpForTime("Invalid Name","No symbols are allowed and name length must be greater than 2",3,fileName,250);
                return;
            }

            if(model.doesNoteNameExist(Name.getText(),"txt")){
                View.displayPopUpForTime("Invalid Name","This note already exists and can't be overwritten. Hint: You can edit notes",3,fileName,250);
                return;
            }
        }


        String htmlContent = Editor.getHtmlText();


        if(isEditing){
            window.setVisible(false);
            model.modify(note,htmlContent);
            clear();
            return;
        }



        if(textTabSelected()){
            window.setVisible(false);
            model.addText(Name.getText(),htmlContent);
            clear();
            return;
        }

        if(this.loadedFile==null){
            View.displayPopUpForTime("Invalid File","No file has been uploaded",3,fileName,250);
            return;
        }

        if(model.doesNoteNameExist(loadedFile.getName())){
            View.displayPopUpForTime("Invalid Name","This note already exists and can't be overwritten. Hint: You can edit notes",3,fileName,250);
            return;
        }

        window.setVisible(false);

        if(loadedFile.getPath().contains("pdf") ){
            Book book = model.addBook(Name.getText(),loadedFile.getPath());
            Task<String> task = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    if(book!=null){
                        book.setupForViewing();
                        model.addNumberOfPages(book,book.getMaximumNumberOfPages());
                    }
                    return null;
                }
            };
            Controller.executeTask(task);
        }else{
            model.addImage(Name.getText(),loadedFile.getPath());
        }
        clear();

    }

    private boolean isValidName(){
        return StringUtils.isAlphanumericSpace(Name.getText()) && Name.getText().length()>2;
    }

    private boolean textTabSelected(){
        return tabPane.getSelectionModel().getSelectedItem().getText().contains("Text");
    }

    public static FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png","*.gif","*.jpeg");
    public static FileChooser.ExtensionFilter bookFilter = new FileChooser.ExtensionFilter("PDF Files", "*.pdf");

    Controller controller;

    public void initialize() {
        model = Model.getInstance();
        controller = Controller.getInstance();
        controller.setCreateNoteController(this);
        loadedFile = null;
    }

}
