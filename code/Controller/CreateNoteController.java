package Code.Controller;
import Code.Model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.StringUtils;
import java.io.File;


/**
 * Controller handling CreateNote window
 */
public class CreateNoteController{
    @FXML Button create;
    @FXML Button cancel;
    @FXML Button upload;
    @FXML HTMLEditor Editor;
    @FXML TextField Name;
    @FXML Label fileName;
    @FXML Pane window;
    @FXML TabPane tabPane;

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

    /**
     * Called on Create button click
     * Will use input information and model to create and add note
     * @param e
     */
    @FXML void handleCreateAction(ActionEvent e){
        if(!isValidName()){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Invalid Name");
            errorAlert.setContentText("No symbols are allowed and name length must be greater than 2");
            errorAlert.showAndWait();
            return;
        }

        Alert existsAlert = new Alert(Alert.AlertType.ERROR);
        existsAlert.setHeaderText("Invalid Name");
        existsAlert.setContentText("This note already exists and can't be overwritten. Hint: You can edit notes");



        if(model.doesNoteNameExist(Name.getText(),"txt")){
            existsAlert.showAndWait();
            return;
        }



        if(textTabSelected()){
            String htmlContent = Editor.getHtmlText();
            model.addText(Name.getText(),htmlContent);
            clear();
            window.setVisible(false);
            return;
        }

        if(this.loadedFile==null){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Invalid Input");
            errorAlert.setContentText("No file has been uploaded");
            errorAlert.showAndWait();
            return;
        }

        if(model.doesNoteNameExist(loadedFile.getName())){
            existsAlert.showAndWait();
            return;
        }



        if(loadedFile.getPath().contains("pdf") ){
            model.addBook(Name.getText(),loadedFile.getPath());
        }else{
            model.addImage(Name.getText(),loadedFile.getPath());
        }
        clear();
        window.setVisible(false);
    }

    private boolean isValidName(){
        return StringUtils.isAlphanumericSpace(Name.getText()) && Name.getText().length()>2;
    }

    private boolean textTabSelected(){
        return tabPane.getSelectionModel().getSelectedItem().getText().contains("Text");
    }

    public static FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
    public static FileChooser.ExtensionFilter bookFilter = new FileChooser.ExtensionFilter("PDF Files", "*.pdf");

    public void initialize() {
        model = Model.getInstance();
        window.visibleProperty().addListener((observable, oldValue, newValue) -> clear());
        loadedFile = null;
    }

}
