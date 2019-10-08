package Code.Controller.ideas;

import Code.Controller.Controller;
import Code.Controller.Dialogs.ViewNotes.ViewMode;
import Code.Controller.DisplayString;
import Code.Controller.RefreshInterfaces.RefreshIdeasController;
import Code.Controller.RefreshInterfaces.RefreshSubjectsController;
import Code.Model.Idea;
import Code.Model.Model;
import Code.Model.Note;
import Code.Model.Topic;
import Code.View.*;
import Code.View.components.MindMap.MindMapCell;
import Code.View.components.MindMap.MindMapFactory;
import Code.View.components.MindMap.MindMapRecursive;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.util.Pair;

import java.util.*;


public class MindMapController implements RefreshIdeasController, RefreshSubjectsController {
    @FXML
    BorderPane window;
    Model model;
    Controller controller;

    MindMapRecursive map;
    ScrollPane scrollPane;

    @FXML protected ImageView loadingGIF;

    private static MindMapRecursive fromString(String name, String lineClass, String centerClass){
        MindMapRecursive recursive = new MindMapRecursive(new DisplayString(name),new MindMapFactory() {

            @Override
            public Line call(Line shape,ObservableObject string) {
                shape.getStyleClass().add(lineClass);
                return shape;
            }


            @Override
            public MindMapCell call(ObservableObject item) {
                MindMapCell cell = new MindMapCell<>(item);
                double padding = 10;
                MindMapRecursive.setCell(cell, Font.font("Inter-Medium", 20),new Insets(padding,padding,padding,padding),200,100);
                cell.getStyleClass().add(centerClass);
                return cell;
            }

        });
        return recursive;
    }

    private static MindMapRecursive fromString(String name, MindMapFactory factory){

        MindMapRecursive recursive = new MindMapRecursive(new DisplayString(name), factory);
        return recursive;
    }

    public void initialize(){

        model = Model.getInstance();
        controller = Controller.getInstance();

        window.getStylesheets().add("/Code/View/css/MindMap.css");
        window.getStyleClass().add("scroll-pane");

        model.addRefreshIdeasController(this);
        model.addRefreshSubjectsController(this);

        window.widthProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.equals(oldValue)){
                adjustMindMap();
            }
        });

        window.heightProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.equals(oldValue)){
               // map.centerNode(window.getWidth(),window.getHeight());
                adjustMindMap();
            }
        });

    }

    public void centerNodeInScrollPane(ScrollPane scrollPane, Node node) {
        double h = scrollPane.getContent().getBoundsInLocal().getHeight();
        double y = (node.getBoundsInParent().getMaxY() +
                node.getBoundsInParent().getMinY()) / 2.0;
        double v = scrollPane.getViewportBounds().getHeight();

        double w = scrollPane.getContent().getBoundsInLocal().getWidth();
        double x = (node.getBoundsInParent().getMaxX() +
                node.getBoundsInParent().getMinX()) / 2.0;
        double hv = scrollPane.getViewportBounds().getWidth();
        scrollPane.setVvalue(scrollPane.getVmax() * ((y - 0.5 * v) / (h - v)));
        scrollPane.setHvalue(scrollPane.getHmax() * ((x - 0.5 * hv) / (w - hv)));
    }

    private void adjustMindMap(){

        if(map.getHeight() < window.getHeight() || map.getWidth() < window.getWidth()){

            double factor = ((window.getHeight()/map.getHeight()) > (window.getWidth()/map.getWidth()) ? (window.getHeight()/map.getHeight()) : (window.getWidth()/map.getWidth()));


            double y = map.getHeight() * factor;

            double x = map.getWidth() * factor;



            double hp = (y - map.getHeight())/2;
            double xp = (x - map.getWidth())/2;


            map.pad(xp,xp,hp,hp);


        }


        Pair<Pair<Double,Double>,Pair<Double,Double>> r = map.getEdgeDistances();



        if(r.getKey().getValue()>r.getKey().getKey()){
            map.pad(r.getKey().getValue() - r.getKey().getKey(),0,0,0);
        }else if(r.getKey().getKey()>r.getKey().getValue()){
            map.pad(0,r.getKey().getKey()-r.getKey().getValue(),0,0);
        }


        if(r.getValue().getValue()>r.getValue().getKey()){
            map.pad(0,0,r.getValue().getValue()-r.getValue().getKey(),0);
        }else if(r.getValue().getKey()>r.getValue().getValue()){
            map.pad(0,0,0,r.getValue().getKey()-r.getValue().getValue());
        }

        centerNodeInScrollPane(scrollPane,map.getMainNode());
    }

    private  MindMapFactory getTopicFactory(String topicClass, String ideaClass, String ideaLineClass, String topicLineClass, String parentClass, String parentLineClass, Topic parent){
        return new MindMapFactory() {
            @Override
            public Line call(Line shape, ObservableObject object) {
                if(parent!=null && parent.equals(object)){
                    shape.getStyleClass().add(parentLineClass);
                    return shape;
                }
                if(object instanceof Topic){
                    shape.getStyleClass().add(topicLineClass);
                    return shape;
                }
                shape.getStyleClass().add(ideaLineClass);
                return shape;
            }

            @Override
            public MindMapCell call(ObservableObject item) {
                MindMapCell cell = new MindMapCell<>(item);
                MindMapRecursive.setCell(cell,Font.font("Inter-Medium", 20), new Insets(10,10,10,10),200,100);
                if(item instanceof Topic){
                    if(item.equals(parent)){
                        cell.getStyleClass().add(parentClass);
                    }else{
                        cell.getStyleClass().add(topicClass);
                    }
                }else if(item instanceof Idea ){
                    cell.getStyleClass().add(ideaClass);
                }

                cell.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Task<MindMapRecursive> r = new Task<MindMapRecursive>(){

                            @Override
                            protected MindMapRecursive call() throws Exception {
                                try{
                                    if(cell.getItem() instanceof Topic){
                                        Topic parent = Model.getInstance().parent((Topic) cell.getItem());
                                        if(parent!=null){
                                            parent = parent.copy();
                                        }
                                        MindMapRecursive recursive = fromTopic((Topic) cell.getItem(),parent);
                                        return recursive;
                                    }else if(cell.getItem() instanceof Idea){
                                        Topic parent = Model.getInstance().parent((Idea) cell.getItem());
                                        MindMapRecursive recursive = fromIdea((Idea) cell.getItem(),parent);
                                        return recursive;
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                return null;
                            }

                        };

                        r.setOnSucceeded(event1 -> {
                            if(r.getValue()!=null){
                                setMindMap(r.getValue());
                            }
                        });


                        r.setOnRunning((running) -> {
                            window.setCenter(loadingGIF);
                        });

                        Controller.executeTask(r);

                    }
                });
               return cell;
            }
        };
    }

    @Override
    public void refreshIdeas() {
        refreshSubjects();
    }

    private static MindMapFactory getKeyWordsFactory(String lineClass, String keywordClass){
        return new MindMapFactory() {
            @Override
            public Line call(Line shape, ObservableObject object) {
                shape.getStyleClass().add(lineClass);
                return shape;
            }
            @Override
            public MindMapCell call(ObservableObject item) {
                MindMapCell cell = new MindMapCell<>(item);
                MindMapRecursive.setCell(cell,Font.font("Inter-Medium", 20),new Insets(10,10,10,10),200,200);
                cell.getStyleClass().add(keywordClass);
                cell.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                    }
                });
                return cell;
            }
        };
    }

    private MindMapFactory getNotesFactory(String lineClass, String notesClass){
        return new MindMapFactory() {

            @Override
            public Line call(Line shape, ObservableObject object) {
                shape.getStyleClass().add(lineClass);
                return shape;
            }

            @Override
            public MindMapCell call(ObservableObject item) {
                MindMapCell cell = new MindMapCell<>(item);
                MindMapRecursive.setCell(cell,Font.font("Inter-Medium", 20),new Insets(10,10,10,10),200,100);
                cell.getStyleClass().add(notesClass);
                cell.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        controller.displayNotes(FXCollections.singletonObservableList((Note) cell.getItem()), ViewMode.ViewLimited);
                    }
                });
                return cell;
            }
        };
    }

    private MindMapFactory getIdeaFactory(String topicClass, String ideaClass, String ideaLineClass, String topicLineClass, String parentClass, String parentLineClass, Topic parent){
        return new MindMapFactory() {
            @Override
            public Line call(Line shape, ObservableObject object) {
                if(parent!=null && parent.equals(object)){
                    shape.getStyleClass().add(parentLineClass);
                    return shape;
                }
                if(object instanceof Topic){
                    shape.getStyleClass().add(topicLineClass);
                    return shape;
                }
                shape.getStyleClass().add(ideaLineClass);
                return shape;
            }
            @Override
            public MindMapCell call(ObservableObject item) {
                MindMapCell cell = new MindMapCell<>(item);
                MindMapRecursive.setCell(cell,Font.font("Inter-Medium", 20), new Insets(10,10,10,10),200,100);
                if(item instanceof Topic){
                    if(item.equals(parent)){
                        cell.getStyleClass().add(parentClass);
                    }else{
                        cell.getStyleClass().add(topicClass);
                    }
                }else if(item instanceof Idea ){
                    cell.getStyleClass().add(ideaClass);
                }

                cell.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        if(cell.getItem() instanceof Topic){
                            Task<MindMapRecursive> r = new Task<MindMapRecursive>(){

                                @Override
                                protected MindMapRecursive call() throws Exception {
                                    try{
                                        Topic parent = Model.getInstance().parent((Topic) cell.getItem());
                                        if(parent!=null){
                                            parent = parent.copy();
                                        }
                                        MindMapRecursive recursive = fromTopic((Topic) cell.getItem(),parent);
                                        return recursive;

                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                    return null;
                                }

                            };

                            r.setOnSucceeded(event1 -> {
                                if(r.getValue()!=null){
                                    setMindMap(r.getValue());
                                }
                            });

                            r.setOnRunning((running) -> {
                                window.setCenter(loadingGIF);
                            });

                            Controller.executeTask(r);

                        }else if(cell.getItem() instanceof Idea){
                            Controller.getInstance().displayIdea((Idea) cell.getItem());
                        }
                    }
                });
                return cell;
            }
        };
    }

    public MindMapRecursive fromIdea(Idea idea, Topic parent){

        System.out.println("\n\n\n\n Let's start with idea: " + idea);
        MindMapRecursive map = new MindMapRecursive(idea,getIdeaFactory("mind-map-topic","mind-map-idea","mind-map-idea-line", "mind-map-topic-line","mind-map-parent","mind-map-line-parent",parent));
        map.setAngle(60);

        //Add topic:
        if(parent!=null){
            //System.out.println(parent);
            //map.updateFactory(getTopicFactory("mind-map-topic","mind-map-idea","mind-map-line-parent"));
            //map.updateFactory(getTopicFactory("mind-map-topic","mind-map-idea","mind-map-line-parent"));
            map.addItem(new MindMapRecursive(parent, getTopicFactory("mind-map-topic","mind-map-idea","mind-map-idea-line", "mind-map-topic-line","mind-map-parent","mind-map-line-parent",parent)));
            //map.updateFactory(getTopicFactory("mind-map-topic","mind-map-idea","mind-map-line"));
        }

        //Add keywords:
        idea.getKeyWords().sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return new Integer(o1.length()).compareTo(o2.length());
            }
        });


        if(idea.getKeyWords().size()>0){
            MindMapRecursive keyWords = fromString("Key Words","mind-map-keyword-line","mind-map-keyword-center");
            for(String s: idea.getKeyWords()){
                if(s!=null){
                    System.out.println("S: " + s);
                    keyWords.addItem(fromString(s,getKeyWordsFactory("mind-map-line","mind-map-keyword")));
                }
            }
            map.addItem(keyWords);
        }


        if(idea.getNotes().size()>0){

            MindMapRecursive notes = fromString("Notes","mind-map-notes-line","mind-map-notes-center");


            idea.getNotes().sort(new Comparator<Note>() {
                @Override
                public int compare(Note o1, Note o2) {
                    return o1.getDisplayName().compareTo(o2.getDisplayName());
                }
            });

            for(Note n: idea.getNotes()){
                if(n!=null){
                    if(idea.getNotesMap().get(n)==true){
                        MindMapRecursive r = new MindMapRecursive(n,getNotesFactory("mind-map-line","mind-map-note"));
                        notes.addItem(r);
                    }else{
                        MindMapRecursive r = new MindMapRecursive(n,getNotesFactory("mind-map-line","mind-map-prompt-note"));
                        notes.addItem(r);
                    }
                }
            }




            map.addItem(notes);
        }

        return map;
    }

    public  MindMapRecursive fromTopic(Topic topic ){

        return fromTopic(topic,null);
    }

    public  MindMapRecursive fromTopic(Topic topic, Topic parent ){

        MindMapRecursive recursive = new MindMapRecursive(topic,getTopicFactory("mind-map-topic","mind-map-idea","mind-map-idea-line", "mind-map-topic-line","mind-map-parent","mind-map-line-parent",parent));

        if(parent!=null){
            recursive.addItem(new MindMapRecursive(parent,getTopicFactory("mind-map-topic","mind-map-idea","mind-map-idea-line", "mind-map-topic-line","mind-map-parent","mind-map-line-parent",parent)));
        }

        List<ObservableObject> ideasTopics = new ArrayList<>();
        ideasTopics.addAll(topic.getIdeas());
        ideasTopics.addAll(topic.getSubTopics());

        ideasTopics.sort(new Comparator<ObservableObject>() {
            @Override
            public int compare(ObservableObject o1, ObservableObject o2) {
                return ((Integer) o1.getMindMapName().length()).compareTo(o2.getMindMapName().length());
            }
        });


        for(ObservableObject o: ideasTopics){
            recursive.addItem(new MindMapRecursive(o,getTopicFactory("mind-map-topic","mind-map-idea","mind-map-idea-line", "mind-map-topic-line","mind-map-parent","mind-map-line-parent",parent)));
        }

        return recursive;
    }

    @Override
    public void refreshSubjects() {

        Task<MindMapRecursive> task = new Task<MindMapRecursive>() {

            @Override
            protected MindMapRecursive call() throws Exception {

                try{
                    Topic root = Model.getInstance().filterTopicByCurrentSubject();
                    root.setName(model.getCurrentSubject().getName());
                    MindMapRecursive map = fromTopic(root);
                    return map;
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };

        task.setOnSucceeded((event -> {
            setMindMap(task.getValue());
        }));

        task.setOnRunning(event -> {
            window.setCenter(loadingGIF);
        });

        Controller.executeTask(task);

    }

    private void setMindMap(MindMapRecursive mindMap){
        this.map = mindMap;

        ScrollPane scroll = new ScrollPane();
        scroll.setContent(map);
        this.scrollPane = scroll;

        window.setCenter(scroll);

        adjustMindMap();

    }
}
