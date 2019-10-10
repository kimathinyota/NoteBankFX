package Code.View;

import Code.Controller.IntegerValue;
import Code.Model.Note;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

public class View {


    public static void setUpListForArrowManipulation(ListView list, IntegerValue index){
        index.setInteger(-1);
        addSelectedItemListener(list,index);
        setLeftRightKeyPressed(list,index);
    }


    public static void addSelectedItemListener(ListView notes, IntegerValue index){
        notes.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> index.setInteger(newValue.intValue()));
    }

    public static void handleLeftClick(ListView allNotes, IntegerValue index){

        if(allNotes.getItems().isEmpty()){
            return;
        }

        if(index.intValue()==-1){
            index.setInteger(1);
        }

        index.setInteger((index.intValue()-1)%allNotes.getItems().size());
        index.setInteger( (index.intValue()<0 ? index.intValue() + allNotes.getItems().size() : index.intValue())  );
        index.setInteger (index.intValue()<0 ? index.intValue() + allNotes.getItems().size() : index.intValue());

        allNotes.scrollTo( index.intValue() );
        allNotes.getSelectionModel().select(index.intValue());


    }
    public static void handleRightClick(ListView allNotes, IntegerValue index){

        if(allNotes.getItems().isEmpty()){
            return;
        }

        index.setInteger((index.intValue()+1)%allNotes.getItems().size());
        allNotes.scrollTo( index.intValue() );
        allNotes.getSelectionModel().select(index.intValue());

    }




    public static void setLeftRightKeyPressed(ListView<Note>list, IntegerValue index){
        list.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.LEFT){
               handleLeftClick(list,index);
            }else if(event.getCode() == KeyCode.RIGHT){
               handleRightClick(list,index);
            }
        });
    }


    public static PopOver getInformationPane(String titleString, String informationString, double width){
        BorderPane pane = new BorderPane();

        pane.setPrefWidth(width);
        Label title = new Label("Information");
        title.setFont(Font.font("Inter-Semi-Bold",22));
        title.setStyle("-fx-text-fill: black");
        title.setAlignment(Pos.CENTER);
        title.setWrapText(true);
        title.setText(titleString);

        HBox top = new HBox();
        Pane p3 = new Pane();
        p3.setPrefWidth(10);
        p3.setPrefHeight(0);
        top.getChildren().add(p3);
        top.getChildren().add(title);
        pane.setTop(top);

        Label information = new Label();
        information.setFont(Font.font("Inter-Medium",15));
        information.setStyle("-fx-text-fill: black");
        information.setAlignment(Pos.CENTER);

        information.setWrapText(true);
        information.setText(informationString);
        pane.setCenter(information);


        Pane p = new Pane();
        p.setPrefWidth(10);
        p.setPrefHeight(0);

        Pane p2 = new Pane();
        p2.setPrefWidth(10);
        p2.setPrefHeight(0);

        pane.setLeft(p);
        pane.setRight(p2);


        return new PopOver(pane);

    }

    public static void displayPopUpForTime(PopOver pop, int time, Node parent){
        PauseTransition delay = new PauseTransition(Duration.seconds(time));
        delay.setOnFinished(e -> pop.hide());

        pop.show(parent);
        delay.play();
    }

    public static void displayPopUpForTime(String titleString, String informationString, int time, Node parent, double width){

        displayPopUpForTime(getInformationPane(titleString, informationString, width),time,parent);

    }

    public static Label getLabel(String title, String family, String color, double size){
        Label label = new Label(title);
        label.setFont(Font.font(family,size));
        label.setTextFill(Color.valueOf(color));
        //label.setStyle("-fx-text-fill: " + color);
        label.setAlignment(Pos.CENTER);
        label.setContentDisplay(ContentDisplay.CENTER);
        label.setWrapText(true);
        return label;
    }

    public static Label getLabel(String title, double size){
        return getLabel(title,"Inter-Semi-Bold","white",size);
    }

    public static Label getLabel(String title, String color, double size){
        return getLabel(title,"Inter-Semi-Bold",color,size);
    }



}
