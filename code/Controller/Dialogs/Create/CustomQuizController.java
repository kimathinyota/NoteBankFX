package Code.Controller.Dialogs.Create;

import Code.Controller.Controller;
import Code.Model.*;
import Code.View.*;
import Code.View.components.SelectFromListToList.*;
import Code.View.View;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import org.controlsfx.control.RangeSlider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomQuizController{
    @FXML protected BorderPane pickIdeaPane, window;
    @FXML protected ToggleButton includeKeyWords,includeQuestions,includeStatements,shuffleIdeaOrder;
    @FXML protected BorderPane ideaBoundsPane;
    @FXML protected Slider ideaLimit;
    @FXML protected Button cancel, create;

    Model model;
    Controller controller;

    protected RangeSlider rangeSlider;

    protected SpecialSelectLists<ObservableObject> ideasLists;

    public void clear(){
        this.ideasLists.getSecondListView().getItems().clear();
        this.includeKeyWords.setSelected(true);
        this.includeQuestions.setSelected(true);
        this.includeStatements.setSelected(true);
        this.shuffleIdeaOrder.setSelected(true);
        rangeSlider.setLowValue(1);
        rangeSlider.setHighValue(5);

    }

    public void initialize(){
        model = Model.getInstance();
        controller = Controller.getInstance();
        controller.setCustomQuizController(this);

        this.ideasLists = new SpecialSelectLists<ObservableObject>();
        this.rangeSlider = new RangeSlider();
        pickIdeaPane.setCenter(ideasLists);
        ideaBoundsPane.setCenter(rangeSlider);
        rangeSlider.setMin(1);
        rangeSlider.setMax(5);
        rangeSlider.setShowTickLabels(true);
        rangeSlider.setMajorTickUnit(1);
        rangeSlider.setMinorTickCount(1);
        rangeSlider.setLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
               if(object.intValue() <= 1){
                   return "Poor";
               }
               if(object.intValue() == 2){
                   return "Mediocre";
               }

                if(object.intValue() == 3){
                    return "Pass";
                }

                if(object.intValue() == 4){
                    return "Almost";
                }

                return "Ready";

            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });
        rangeSlider.getStylesheets().add("/Code/View/css/quiz.css");
        clear();

    }

    @FXML protected void handleCancelAction(ActionEvent e){
        window.setVisible(false);
    }

    @FXML protected void handleCreateAction(ActionEvent e){
        List<Idea> ideas = new ArrayList<>();

        for(ObservableObject o: ideasLists.getSecondList()){
            if(o instanceof Idea && !ideas.contains((Idea) o) ){
                ideas.add((Idea) o);
            }else if(o instanceof Topic ){
                for(Idea i: ((Topic) o).getAllIdeas()){
                    if(!ideas.contains(i)){
                        ideas.add(i);
                    }
                }
            }else if( o instanceof Subject){
                for(Idea i: model.getRoot().getAllIdeas()){
                    if(!ideas.contains(i) && i.isIdeaApartOfSubject((Subject) o)){
                        ideas.add(i);
                    }
                }
            }
        }

        boolean removeKeyWord = !includeKeyWords.isSelected();
        boolean removeStatement = !includeStatements.isSelected();
        boolean removeQuestion = !includeQuestions.isSelected();

        List<Idea> remove = new ArrayList<>();
        for(Idea i: ideas){
            int readyType = model.getReadinessType(i);

            boolean shouldRemove = (removeKeyWord && i.getPromptType().equals(PromptType.KeyWords)) ||
                                   (removeQuestion && i.getPromptType().equals(PromptType.Question)) ||
                                   (removeStatement && i.getPromptType().equals(PromptType.Statement)) ||
                                   !(readyType >= rangeSlider.getLowValue() &&  readyType <= rangeSlider.getHighValue());
            if(shouldRemove)
                remove.add(i);
        }


        ideas.removeAll(remove);
        if(shuffleIdeaOrder.isSelected()){
            Collections.shuffle(ideas);
        }

        int max = ((int) ideaLimit.getValue()) < ideas.size() ? (int) ideaLimit.getValue() : ideas.size();

        ideas = ideas.subList(0,max);

        if(ideas.isEmpty()){
            View.displayPopUpForTime("No Ideas Selected", "Can't create list after applying filters ",3, create, 250);
            return;
        }

        window.setVisible(false);
        controller.startQuiz(ideas);

    }

    public void show(){
        clear();
        this.window.setVisible(true);
        this.ideasLists.getSecondListView().getItems().clear();
        List<ObservableObject> list = new ArrayList<>();
        list.addAll(model.getAllIdeas());
        list.addAll(model.getAllTopics());
        list.addAll(model.getAllSubjects());

        /*

        List<Subject> subjects = model.getAllSubjects();
        for(Subject s: subjects){
            if(!s.getName().equals("All")){
                Topic topic = model.filterTopicBySubject(s);

                Topic t = new Topic(s.getName());
                for(Topic p: topic.getSubTopics()){
                    t.add(p);
                }
                for(Idea i: topic.getIdeas()){
                    t.add(i);
                }

                list.add(t);
            }
        }

        */

        this.ideasLists.setFirstList(list);
        this.ideaLimit.setMax(list.size());
        this.ideaLimit.setValue(list.size());
    }

}



class SpecialAddListCell<T extends ObservableObject> extends AddListCell<T> {
    public SpecialAddListCell(ListView<T> listone, ListView<T> listtwo) {
        super(listone, listtwo);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
        else {
            this.item = item;
            label.setText(item.toString());
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }


}


class SpecialAddListCellFactory<T extends ObservableObject> extends AddListCellFactory<T> {

    public SpecialAddListCellFactory(ListView<T>listOne, ListView<T>listTwo){

        super(listOne,listTwo);
    }

    @Override
    public ListCell<T> call(ListView<T> param) {
        AddListCell cell =  new SpecialAddListCell(listOne,listTwo);
        return cell;
    }

}

class SpecialRemoveListCell<T extends ObservableObject> extends RemoveNormalListCell<T> {

    public SpecialRemoveListCell(ListView<T> t) {
        super(t);
    }

    @FXML protected void handleRemoveAction(ActionEvent e){
        for(int i = 0; i< this.view.getItems().size(); i++){
            if(this.view.getItems().get(i).toString().equals(label.getText())){
                this.view.getItems().remove(i);
            }
        }
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if(empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
        else {
            label.setText(item.toString());
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }
}


class SpecialRemoveListCellFactory<T extends ObservableObject> extends RemoveListCellFactory<T> {

    @Override
    public ListCell<T> call(ListView<T> param) {
        return new SpecialRemoveListCell(param);
    }
}



class SpecialSelectLists<T extends ObservableObject> extends SelectObjectsLists<T> {

    protected void refresh(){
        ArrayList<T> firstList = new ArrayList<>() ;
        for(T o: listOne){
            if(o.toString().contains(search.getText())){
                firstList.add(o);
            }
        }
        this.firstList.setItems(FXCollections.observableArrayList(firstList));
    }

    public SpecialSelectLists(){
        this.setFirstListFactory(new SpecialAddListCellFactory(firstList,secondList));
        this.setSecondListFactory(new SpecialRemoveListCellFactory());
    }
}

