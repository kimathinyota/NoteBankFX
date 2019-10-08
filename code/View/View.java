package Code.View;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

public class View {

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
