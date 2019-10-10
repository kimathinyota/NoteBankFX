package Code.Controller.ideas;


import Code.Controller.Controller;
import Code.Controller.RefreshInterfaces.RefreshIdeasController;
import Code.Controller.RefreshInterfaces.RefreshSubjectsController;
import Code.Model.*;
import Code.View.ObservableObject;
import Code.View.View;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import java.util.ArrayList;
import java.util.List;


public class IdeaOverviewController implements RefreshSubjectsController, RefreshIdeasController {

    TreeView<ObservableObject> treeView;
    @FXML
    BorderPane window;

    Model model;
    Controller controller;

    public TreeItem<ObservableObject> getRoot(Topic topic){
        TreeItem<ObservableObject> t = new TreeItem<>(topic);
        for(Idea i: topic.getIdeas()){
            t.getChildren().add(new TreeItem<>(i));
        }
        for(Topic top: topic.getSubTopics()){
            t.getChildren().add(getRoot(top));
        }
        return t;
    }

    public void initialize(){

        this.model = Model.getInstance();
        model.addRefreshSubjectsController(this);
        model.addRefreshIdeasController(this);
        this.controller = Controller.getInstance();

        treeView = new TreeView<>();
        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        treeView.setCellFactory(new Callback<TreeView<ObservableObject>, TreeCell<ObservableObject>>() {
            @Override
            public TreeCell<ObservableObject> call(TreeView<ObservableObject> param) {
                TreeCell<ObservableObject> cell = new TreeCell<ObservableObject>(){

                    @Override
                    protected void updateItem(ObservableObject item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty){
                            setText(null);
                            setGraphic(null);
                        }else{
                            setText(item.getDisplayName());
                        }
                    }
                };
                cell.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if(event.getClickCount()==2) {
                            if(cell.getTreeItem().getValue() instanceof Idea) {
                                controller.displayIdea((Idea) cell.getTreeItem().getValue());
                            }
                        }
                    }
                });
                return cell;
                }
        });

        treeView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.isPopupTrigger()){
                    if(menu!=null) menu.hide();
                    menu = new TreeRightClickMenu(treeView.getSelectionModel().getSelectedItems(),treeView.getRoot());
                    window.getStylesheets().add("/Code/View/css/context-menu.css");
                    menu.show(window,event.getScreenX(),event.getScreenY());
                }else if(menu!=null){
                    menu.hide();
                }
            }
        });

        treeView.getStyleClass().add("overviewTree");
        treeView.getStylesheets().add("/Code/View/css/ideaoverview.css");

        window.setCenter(treeView);

        refreshSubjects();

    }

    TreeRightClickMenu menu;

    @Override
    public void refreshSubjects() {
        Topic root = model.filterTopicByCurrentSubject().copy();
        root.setName(model.getCurrentSubject().getName());
        treeView.setRoot(getRoot(root));
        treeView.refresh();
    }

    public void refreshIdeas(){
        refreshSubjects();
    }

}


class TreeRightClickMenu extends ContextMenu{

    Menu menu;

    class TopicMenu extends MenuItem{
        Topic topic;
        List<Object> selected;

        public TopicMenu(Topic topic, List<Object> selected){
            super(topic.getTreeName());
            this.topic =topic;
            this.selected = selected;
            this.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Model.getInstance().move(selected,topic);
                }
            });
        }
    }

    public void refreshIdeas(List<TreeItem<ObservableObject>> items) {
        if(menu==null) return;
        menu.getItems().clear();
        Topic topic = Model.getInstance().filterTopicByCurrentSubject().copy();
        topic.setName(Model.getInstance().getCurrentSubject().getName());
        List<Object> selectedIdeasOrTopics = getObjects(items);

        List<Topic> movableTopics = Model.getInstance().findMovableTopics(selectedIdeasOrTopics);
        if(!items.isEmpty() && items.get(0).getParent()!=null){
            movableTopics.remove(items.get(0).getParent().getValue());
        }
        for(Topic t: movableTopics){
            TopicMenu m  =  new TopicMenu(t,selectedIdeasOrTopics);
            menu.getItems().add(m);
        }
        if(menu.getItems().isEmpty()){
            menu.setDisable(true);
        }
    }

    private List<Object> getObjects(List<TreeItem<ObservableObject>> items){
        List<Object> objects = new ArrayList<>();
        for(TreeItem<ObservableObject> i: items){
            objects.add(i.getValue());
        }
        return objects;
    }

    private boolean shareSameParent(List<TreeItem<ObservableObject>> items){
        if(items.isEmpty())
            return false;

        if(items.size()==1)
            return true;

        TreeItem<ObservableObject> first = items.get(0);

        TreeItem<ObservableObject> parent = first.getParent();

        for(int i=1; i<items.size(); i++){
            if(parent==null || !parent.equals(items.get(i).getParent())){
                return false;
            }
        }

        return true;
    }

    public TreeRightClickMenu(List<TreeItem<ObservableObject>> items, TreeItem<ObservableObject> root){

        Model model = Model.getInstance();

        this.getStyleClass().add("amenu");
        Menu newItem = new Menu("New");
        newItem.getStyleClass().add("amenu");
        MenuItem newIdea = new MenuItem("Idea");
        newIdea.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(items.size()==1 && items.get(0).getValue() instanceof Topic ){
                    Controller.getInstance().createIdea((Topic) items.get(0).getValue());
                }else{
                    Controller.getInstance().createIdea();
                }
            }
        });
        newItem.getItems().add(newIdea);
        Menu topic = new Menu("Topic");
        topic.getStyleClass().add("amenu");

        BorderPane pane = new BorderPane();
        TextField topicName = new TextField();
        topicName.setStyle("-fx-background-color: grey; -fx-background-radius: 5px; -fx-border-color: transparent; -fx-text-fill: black");
        Button addTopic = new Button("+");
        addTopic.setPrefWidth(25);
        addTopic.getStylesheets().add("/Code/View/css/viewnote.css");
        addTopic.getStyleClass().add("add");
        addTopic.setStyle("-fx-background-color: transparent; -fx-background-radius: 20px; -fx-border-color: transparent; -fx-font-size: 20; -fx-padding: 0px; -fx-text-fill: white");
        addTopic.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                addTopic.setStyle("-fx-background-color: #95b67c; -fx-background-radius: 20px; -fx-border-color: transparent; -fx-font-size: 20; -fx-padding: 0px; -fx-text-fill: white");
            }
        });

        addTopic.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                addTopic.setStyle("-fx-background-color: transparent; -fx-background-radius: 20px; -fx-border-color: transparent; -fx-font-size: 20; -fx-padding: 0px; -fx-text-fill: white");
            }
        });
        addTopic.setDisable(true);
        topicName.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean containsName = Model.getInstance().containsTopicOfName(newValue);
            addTopic.setDisable(newValue.isEmpty() || containsName);
            if(newValue.isEmpty() || containsName){
                topicName.setStyle("-fx-background-color: #af7070; -fx-background-radius: 5px; -fx-border-color: transparent; -fx-text-fill: black");
            }else{
                topicName.setStyle("-fx-background-color: #82af68; -fx-background-radius: 5px; -fx-border-color: transparent; -fx-text-fill: black");
            }

        });

        addTopic.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                String topic = topicName.getText();

                if(topic.isEmpty()){
                    return;
                }

                Topic top = Model.getInstance().addTopic(topic);

                TreeItem<ObservableObject> topicItem = new TreeItem<>(top);

                if(items.size()==1 && items.get(0).getValue() instanceof Topic){

                    Model.getInstance().move(top, (Topic) items.get(0).getValue());

                    refreshIdeas(items);

                    return;
                }

                if(!items.isEmpty() && shareSameParent(items)){
                    Model.getInstance().move(top,(Topic) items.get(0).getParent().getValue());
                    //p.getChildren().add(topicItem);
                    for(TreeItem<ObservableObject> t: items){
                        if(t.getValue() instanceof Idea || t.getValue() instanceof Topic){
                            Model.getInstance().move(t.getValue(),top);
                        }
                    }

                    refreshIdeas(items);
                    return;

                }

                refreshIdeas(items);
                root.getChildren().add(topicItem);


            }
        });


        Pane block = new Pane();
        block.setPrefWidth(5);

        BorderPane bp = new BorderPane();
        bp.setCenter(topicName);
        bp.setRight(block);

        pane.setCenter(bp);
        pane.setRight(addTopic);
        CustomMenuItem text = new CustomMenuItem(pane);
        topic.getItems().add(text);

        newItem.getItems().add(topic);

        this.getItems().add(newItem);


        menu = new Menu("Move");
        menu.getStyleClass().add("amenu");
        this.getItems().add(menu);

        if(shareSameParent(items)){
            refreshIdeas(items);
        }else{
            menu.setDisable(true);
        }


        MenuItem disband = new MenuItem("Disband");
        this.getItems().add(disband);
        disband.setDisable(!(items.size()==1 && !items.get(0).equals(root) && items.get(0).getValue() instanceof Topic));
        disband.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Model.getInstance().disband((Topic) items.get(0).getValue());
            }
        });


        Menu subjects = new Menu("Subjects");

        if(items.size()==1 && items.get(0).getValue() instanceof Idea){

        }else{
            subjects.setDisable(true);
        }





        for(Subject s: model.getAllSubjects()){

            if(!s.getName().equals("All")){
                CheckMenuItem subject = new CheckMenuItem(s.getName());
                subject.setSelected(s.contains(items.get(0).getValue()));
                subject.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        Idea idea = (Idea) items.get(0).getValue();

                        if(subject.isSelected()){
                            //need to add note to subject
                            if(!s.contains(idea)){
                                model.addIdeaToSubject(idea,s);
                            }
                        }else{
                            //remove note from subject

                            List<Note> notes = model.listOfNotesPresentInBothIdeaAndSubject(idea,s);

                            boolean canFullyRemove = notes.isEmpty();

                            if(!canFullyRemove){
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                                        "To remove the idea " +
                                                idea.getPrompt() +
                                                " from subject " +  s.getName()
                                                + System.lineSeparator() +
                                                 notes + " will have to be removed from idea: " + System.lineSeparator() +
                                            "Do you still wish to continue ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

                                alert.showAndWait();

                                if (alert.getResult() == ButtonType.YES) {
                                    model.removeIdeaFromSubject(idea,s);
                                }

                                return;
                            }

                            model.removeIdeaFromSubject(idea,s);

                        }

                    }
                });

                subjects.getItems().add(subject);

            }


        }

        this.getItems().add(subjects);




        MenuItem removeItem = new MenuItem("Remove");
        removeItem.setDisable( !(items.size()==1 && !items.contains(root)) );
        this.getItems().add(removeItem);
        removeItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(items.size()==1 && !items.get(0).getValue().equals(root)){
                    Model.getInstance().remove(items.get(0).getValue());
                }
            }
        });





    }

}
