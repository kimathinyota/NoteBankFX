package Code.Controller;

import Code.Model.Model;
import Code.Model.PromptType;
import Code.View.menus.TagCellFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import java.util.List;


/**
 * Controller handling CreateIdea window
 */

public class CreateIdeaController {
    @FXML ListView<String> listView;
    @FXML Pane window;
    @FXML TextField prompt;
    @FXML ChoiceBox<PromptType> ideaType;
    @FXML Button create;
    @FXML Button cancel;
    @FXML TextField keyWord;

    Model model;

    public void initialize(){
        this.model = Model.getInstance();
        this.listView.setCellFactory(new TagCellFactory(this.listView));
        this.listView.setOrientation(Orientation.HORIZONTAL);

        // Initialise idea type choicebox items
        ideaType.getItems().add(PromptType.Question);
        ideaType.getItems().add(PromptType.KeyWords);
        ideaType.getItems().add(PromptType.Statement);
        ideaType.getSelectionModel().select(PromptType.Question);

        // Listener to clear createIdea window when window is closed
        window.visibleProperty().addListener((observable, oldValue, newValue) -> clear());

        //Will prevent user from entering any incorrect symbol
        this.prompt.textProperty().addListener((obs, oldText, newText) ->{
            this.prompt.setText(newText.replaceAll("[^a-zA-Z0-9_. ]",""));
        });
    }

    /**
     * Will add new keyword (tag) to keyword listView on enter press in keyword textfield
     * @param e
     */
    public void onKeyPressed(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            addKeyWord(keyWord.getText());
        }
    }

    /**
     * Will remove all content from current createIdea window
     */
    private void clear(){
        this.prompt.setText("");
        this.listView.getItems().clear();
        this.keyWord.setText("");
    }

    /**
     * Check if name is valid: appropriate name length of 3 or more chars
     * @return
     */
    public boolean isValidName(){
        return this.prompt.getText().length() > 2;
    }

    /**
     * Called on Create button click
     * Will use user input and the model to add to add a valid idea to the system
     * @param e
     */
    @FXML void handleCreateAction(ActionEvent e){
        if(!isValidName()){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Invalid Input");
            errorAlert.setContentText("The length of the prompt must be greater than 2");
            errorAlert.showAndWait();
            return;
        }

        PromptType type = ideaType.getSelectionModel().getSelectedItem();
        model.addIdea(prompt.getText(),this.listView.getItems(),type);

        window.setVisible(false);
    }

    /**
     * Updates the listview with a new keyword
     * @param keyWord
     */
    public void addKeyWord(String keyWord){
        if(keyWord.trim().equals("")){
            return;
        }

        this.listView.getItems().removeAll(keyWord);
        this.listView.getItems().add(keyWord);
        this.listView.scrollTo(keyWord);
        this.keyWord.setText("");
    }

    /**
     * Called on ADD button click (to add keyword)
     * @param e
     */
    @FXML void handleAddAction(ActionEvent e){
        addKeyWord(keyWord.getText());
    }

    /**
     * @return list of keywords in listview
     */
    public List<String> getKeyWords(){
        return this.listView.getItems();
    }

    /**
     * removes keyword
     * @param keyWord
     */
    public void removeKeyWord(String keyWord){
        this.listView.getItems().removeAll(keyWord);
    }

    public void setKeyWords(List<String>keyWords){
        this.listView.getItems().clear();
        for(String s: keyWords){
            addKeyWord(s);
        }
    }


    /**
     * Called on cancel button click
     * Will clear and then hide the window
     * @param e
     */
    @FXML void handleCancelAction(ActionEvent e){
        clear();
        window.setVisible(false);
    }

}
