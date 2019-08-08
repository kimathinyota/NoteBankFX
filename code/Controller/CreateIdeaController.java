package Code.Controller;

import Code.Model.Model;
import Code.Model.PromptType;
import Code.View.menus.TagCellFactory;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;


public class CreateIdeaController {
    @FXML ListView<String> listView;
    @FXML Pane window;
    @FXML TextField prompt;
    @FXML ChoiceBox<String> ideaType;
    @FXML Button create;
    @FXML Button cancel;
    @FXML TextField keyWord;

    Model model;
    public void initialize(){
        this.model = Model.getInstance();
        this.listView.setCellFactory(new TagCellFactory(this.listView));
        this.listView.setOrientation(Orientation.HORIZONTAL);

        ideaType.getItems().add("Question");
        ideaType.getItems().add("Key Words");
        ideaType.getItems().add("Statement");
        ideaType.getSelectionModel().select("Question");
        window.visibleProperty().addListener((observable, oldValue, newValue) -> clear());

        this.prompt.textProperty().addListener((obs, oldText, newText) ->{
            this.prompt.setText(newText.replaceAll("[^a-zA-Z0-9_. ]",""));
        });
    }

    public void onKeyPressed(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            addKeyWord(keyWord.getText());
        }
    }




    private void clear(){
        this.prompt.setText("");
        //this.listView.setItems(FXCollections.emptyObservableList());

        this.listView.getItems().clear();
        this.keyWord.setText("");
    }

    public boolean isValidName(){
        return this.prompt.getText().length() > 2;
    }

    @FXML void handleCreateAction(ActionEvent e){
        if(!isValidName()){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Invalid Input");
            errorAlert.setContentText("The length of the prompt must be greater than 2");
            errorAlert.showAndWait();
            return;
        }

        PromptType type = PromptType.valueOf(ideaType.getSelectionModel().getSelectedItem().replaceAll("\\s+",""));
        model.addIdea(prompt.getText(),this.listView.getItems(),type);

        window.setVisible(false);



    }

    public void addKeyWord(String keyWord){
        if(keyWord.trim().equals("")){
            return;
        }

        System.out.println(this.listView.getItems().size());

        this.listView.getItems().removeAll(keyWord);
        this.listView.getItems().add(keyWord);
        this.listView.scrollTo(keyWord);
        this.keyWord.setText("");
    }

    @FXML void handleAddAction(ActionEvent e){
        addKeyWord(keyWord.getText());
    }

    public List<String> getKeyWords(){
        return this.listView.getItems();
    }



    public void removeKeyWord(String keyWord){
        this.listView.getItems().removeAll(keyWord);
    }

    public void setKeyWords(List<String>keyWords){
        this.listView.getItems().clear();
        for(String s: keyWords){
            addKeyWord(s);
        }
    }

    @FXML void handleCancelAction(ActionEvent e){
        clear();
        window.setVisible(false);
    }

}
