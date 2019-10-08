package Code.View.components.Date.DateRangePicker;


import Code.View.components.Date.PickDate.SelectDate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.Date;





public class SelectFromToDates extends HBox {

    @FXML protected BorderPane left, right;



    protected void handleLowerDateSelection(){
    }

    protected void handleHigherDateSelection(){
    }

    protected void handleLowerDateTimerChange(){
    }

    protected void handleHigherDateTimerChange(){
    }


    class SelectLowerDate extends SelectDate {

        SelectDate higher;

        SelectFromToDates d;


        public SelectLowerDate(Date start, SelectDate higher, SelectFromToDates d) {
            super(start);
            this.higher = higher;
            this.d = d;
        }

        @Override
        public void handleDateSelection(ActionEvent e) {
            super.handleDateSelection(e);
            higher.setMinimumDate(this.getDate());
            d.handleLowerDateSelection();
        }

        @Override
        protected void handleTimerChange(ActionEvent e) {
            super.handleTimerChange(e);
            d.handleLowerDateTimerChange();
        }

    }

    class SelectHigherDate extends SelectDate{

        public SelectHigherDate(Date start) {
            super(start);

        }

        @Override
        public void selectDate(Date d) {
            super.selectDate(d);
        }


        @Override
        public void handleDateSelection(ActionEvent e) {
            super.handleDateSelection(e);
            handleHigherDateSelection();
            System.out.println("Higher Date Select 1");
        }

        @Override
        protected void handleTimerChange(ActionEvent e) {
            super.handleTimerChange(e);
            handleHigherDateTimerChange();
            System.out.println("Higher Date Time 1");
        }


    }


    public void setDate(Date firstDate, Date secondDate, Date minimumDate, Date maximumDate){
        this.first.setDate(firstDate);
        this.second.setDate(secondDate);
        this.first.setMinimumDate(minimumDate);
        this.first.setMaximumDate(maximumDate);
        this.second.setMinimumDate(firstDate);
        this.second.setMaximumDate(maximumDate);
    }


    public Date getFirstDate(){
        return first.getDate();
    }

    public Date getSecondDate(){
        return second.getDate();
    }


    public SelectDate getSecond() {
        return second;
    }

    public SelectDate getFirst() {
        return first;
    }

    //DateSelector first, second;

    protected SelectDate second;
    protected SelectDate first;

    private void setUp(Date firstDate, Date secondDate, Date minimumDate, Date maximumDate){
        loadFXML();
        this.second = new SelectHigherDate(secondDate);
        this.second.setMinimumDate(firstDate);
        this.second.setMaximumDate(maximumDate);

        this.first = new SelectLowerDate(firstDate,this.second,this);
        this.first.setMinimumDate(minimumDate);
        this.first.setMaximumDate(maximumDate);


        left.setCenter(first);
        right.setCenter(second);
    }

    public SelectFromToDates(Date firstDate, Date secondDate, Date minimumDate, Date maximumDate){
        setUp(firstDate,secondDate,minimumDate,maximumDate);

    }


    public void close(){
        this.first.close();
        this.second.close();
    }


    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Code/View/components/LeftNodeRightNode/LeftToRight.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
