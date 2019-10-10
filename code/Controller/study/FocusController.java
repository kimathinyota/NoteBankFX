package Code.Controller.study;

import Code.Controller.Controller;
import Code.Controller.IntegerValue;
import Code.Controller.RefreshInterfaces.RefreshStudyController;
import Code.Controller.RefreshInterfaces.RefreshSubjectsController;
import Code.Model.Model;
import Code.Model.StudyPlan;
import Code.View.components.TitleNode.CustomSizeTitleNodeListFactory;
import Code.View.View;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.AbstractMap;
import java.util.Map;



public class FocusController implements RefreshSubjectsController, RefreshStudyController {

    @FXML protected Button newStudyPlan;
    @FXML protected ListView<Map.Entry<Node, Node>> studyPlans;
    @FXML protected Label subjectLine;

    Model model;
    Controller controller;

    IntegerValue plansIndex = new IntegerValue(-1);

    public void initialize(){
        model = Model.getInstance();
        controller = Controller.getInstance();
        model.addRefreshSubjectsController(this);
        model.addRefreshStudyController(this);

        studyPlans.setOrientation(Orientation.HORIZONTAL);

        studyPlans.setCellFactory(new CustomSizeTitleNodeListFactory(310,150));

        setupPlans();

        newStudyPlan.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.createStudyPlan();

            }
        });

        View.setUpListForArrowManipulation(studyPlans,plansIndex);

    }

    @FXML protected void handleRightClick(ActionEvent e){
        View.handleRightClick(studyPlans,plansIndex);
    }

    @FXML protected void handleLeftClick(ActionEvent e){
        View.handleLeftClick(studyPlans,plansIndex);
    }


    StudyPlanRightClickMenu menu;


    public void setupPlans(){



        this.studyPlans.getItems().clear();

        for(StudyPlan plan: model.getStudyPlans()){
            Text text = new Text();
            text.setText(plan.getDescription());
            text.setFont(Font.font("Inter Semi Bold", 17));
            text.setStyle("-fx-background-color: transparent");
            text.setFill(Color.WHITE);
            text.setWrappingWidth(270);
            text.setTextAlignment(TextAlignment.CENTER);

            ScrollPane pane = new ScrollPane(text);
            pane.getStyleClass().add("mylistview");
            pane.getStylesheets().add("/Code/View/css/viewnote.css");
            pane.applyCss();

            pane.setStyle("-fx-background-color: transparent; -fx-background: transparent");

            Label label = View.getLabel(plan.getName(),"Inter Semi Bold","white", 20);

            EventHandler<MouseEvent> handler = new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(menu!=null)
                        menu.hide();

                    if(event.getButton() == MouseButton.SECONDARY){
                        pane.getStylesheets().add("/Code/View/css/context-menu.css");
                        menu = new StudyPlanRightClickMenu(plan,studyPlans);
                        menu.show(pane,event.getScreenX(),event.getScreenY());
                        return;
                    }

                    Controller.getInstance().editStudyPlan(plan);



                }
            };

            label.setOnMouseClicked(handler);
            pane.setOnMouseClicked(handler);

            pane.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    label.setUnderline(true);
                    text.setFill(Color.LIGHTGRAY);
                    text.setUnderline(true);
                    label.setTextFill(Color.LIGHTGRAY);

                }
            });

            pane.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    text.setFill(Color.WHITE);
                    text.setUnderline(false);
                    label.setUnderline(false);
                    label.setTextFill(Color.WHITE);

                }
            });


            label.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    label.setUnderline(true);
                    label.setTextFill(Color.LIGHTGRAY);
                    text.setFill(Color.LIGHTGRAY);
                    text.setUnderline(true);
                }
            });

            label.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    text.setFill(Color.WHITE);
                    label.setUnderline(false);
                    label.setTextFill(Color.WHITE);
                    text.setUnderline(false);
                }
            });





            this.studyPlans.getItems().add(
                    new AbstractMap.SimpleEntry<Node, Node>(
                            label,
                            pane
                    )
            );


        }


    }


    @Override
    public void refreshSubjects() {
        subjectLine.setText(model.getCurrentSubject().getName());
    }

    @Override
    public void refreshStudy() {
        setupPlans();
    }
}


class StudyPlanRightClickMenu extends ContextMenu{

    public StudyPlanRightClickMenu(StudyPlan plan, ListView view) {

        this.getStyleClass().add("amenu");
        MenuItem simulate = new MenuItem("Simulate");
        simulate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Controller.getInstance().createStudySession(plan);
            }
        });
        MenuItem edit = new MenuItem("Edit");
        edit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Controller.getInstance().editStudyPlan(plan);
            }
        });
        MenuItem remove = new MenuItem("Remove");
        remove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(plan.getID().equals("lower") || plan.getID().equals("medium") || plan.getID().equals("higher")){
                    View.displayPopUpForTime("Important Study Plan", "Can't remove " + plan.getName(),3,view,250);
                    return;
                }
                Model.getInstance().remove(plan);
            }
        });

        this.getItems().add(simulate);
        this.getItems().add(edit);
        this.getItems().add(remove);
    }
}
