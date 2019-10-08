package Code.Controller.Dialogs.Create;

import Code.Controller.Controller;
import Code.Model.Model;
import Code.Model.StudyPlan;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;

public class CreateStudyPlanController{

    @FXML protected TextField name;
    @FXML protected TextArea description;
    @FXML protected Slider studyLength, ideasPerSet, completionScore;
    @FXML protected Label title;
    @FXML protected Button create;

    @FXML protected BorderPane window;

    Model model;
    Controller controller;

    @FXML protected void handleCreateAction(ActionEvent e){

        if(isEditing){

            model.update(plan,name.getText(),description.getText(),ideasPerSet.getValue(),studyLength.getValue(),completionScore.getValue());

        }else{
            StudyPlan plan = new StudyPlan(name.getText(),description.getText(),ideasPerSet.getValue(),studyLength.getValue(),completionScore.getValue());
            model.add(plan);
        }

        window.setVisible(false);

    }

    @FXML protected void handleCancelAction(ActionEvent e){
        window.setVisible(false);
    }

    public void create(){
        title.setText("Study Plan");
        create.setText("CREATE");
        clear();
        setupCompletionSlider();
        window.setVisible(true);
        this.isEditing = false;

    }

    boolean isEditing;

    StudyPlan plan;

    public void edit(StudyPlan plan){
        title.setText("Edit Study Plan");
        create.setText("UPDATE");

        setupCompletionSlider();
        this.name.setText(plan.getName());
        this.description.setText(plan.getDescription());

        this.studyLength.setValue(plan.getMSL());
        this.ideasPerSet.setValue(plan.getIPS());
        this.completionScore.setValue(plan.getScorePercentage());

        window.setVisible(true);
        this.isEditing = true;
        this.plan = plan;
    }

    private void clear(){
        this.name.setText("");
        this.description.setText("");
        this.studyLength.setValue(50);
        this.ideasPerSet.setValue(50);
    }

    private void setupCompletionSlider(){

        double tick = model.getScoreIncrement();

        completionScore.setMin(0);
        completionScore.setMax(1.5);
        completionScore.setMajorTickUnit(tick);
        completionScore.setShowTickMarks(true);
        completionScore.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                double percentage = object.doubleValue();
                double scoreIncrement = model.getScoreIncrement();
                if(percentage==1){
                    return "Ready (" + ((int) (percentage*100) ) + "%)";
                }
                if(percentage==3*scoreIncrement){
                    return "Almost (" + ((int) (percentage*100) ) + "%)";
                }
                if(percentage==2*scoreIncrement){
                    return "Pass (" + ((int) (percentage*100) ) + "%)";
                }

                if(percentage==scoreIncrement){
                    return "Mediocre (" + ((int) (percentage*100) ) + "%)";
                }

                if(percentage == 0){
                    return "Fail (" + ((int) (percentage*100) ) + "%)";
                }

                return ""+((int) (percentage*100) )+ "%";
            }

            @Override
            public Double fromString(String string) {
                return null;
            }
        });

        this.completionScore.setValue(1);

    }

    public void initialize(){

        model = Model.getInstance();
        controller = Controller.getInstance();
        controller.setCreateStudyPlanController(this);

        studyLength.setMin(0.1);
        studyLength.setMax(1);
        studyLength.setMajorTickUnit(0.2);
        studyLength.setShowTickMarks(true);

        ideasPerSet.setMin(0.1);
        ideasPerSet.setMax(1);
        ideasPerSet.setShowTickMarks(true);

        ideasPerSet.setMajorTickUnit(0.2);

        StringConverter<Double> converter = new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                double percentage = object.doubleValue();
                return ""+((int) (percentage*100) )+ "%";
            }

            @Override
            public Double fromString(String string) {
                return null;
            }
        };

        ideasPerSet.setLabelFormatter(converter);
        studyLength.setLabelFormatter(converter);


    }


}
