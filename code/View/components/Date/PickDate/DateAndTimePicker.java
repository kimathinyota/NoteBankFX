package Code.View.components.Date.PickDate;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.apache.commons.lang3.StringUtils;


import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateAndTimePicker extends BorderPane {

    @FXML protected TextField hours, minutes;
    @FXML protected DatePicker date;

    @FXML protected Button selectButton, now;


    public Button getSelect(){
        return this.selectButton;
    }

    public Date getDate(){

        long s = (1000L);
        long m = (60L * s);
        long h = (60L * m);

        int hour = Integer.valueOf(hours.getText());
        int mins = Integer.valueOf(minutes.getText());

        LocalDateTime timeDate = this.date.getValue().atTime(hour,mins);

        ZonedDateTime zdt = timeDate.atZone(ZoneId.systemDefault());
        Date output = Date.from(zdt.toInstant());

        return output;

    }

    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public void initialize(){

        hours.textProperty().addListener(getListener(23,hours));

        minutes.textProperty().addListener(getListener(59,minutes));

        now.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setDate(new Date());
            }
        });


    }


    private ChangeListener<String> getListener(int max,TextField field){
        return new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue!=null && !newValue.equals(oldValue)){

                    if(newValue.length()>2){
                        newValue = newValue.substring(0,2);
                    }

                    if(newValue.isEmpty() || !StringUtils.isNumeric(newValue)){
                        field.setText("00");
                    }else{
                        int time = Integer.valueOf(newValue);
                        if(time>max || time<0){
                            field.setText("00");
                        }else{
                            field.setText( (time < 10 ? "0" + time : ""+time) );
                        }
                    }
                }
            }
        };
    }


    public void setDate(Date date){
        this.date.setValue(convertToLocalDateViaInstant(date));
        this.hours.setText((date.getHours()<0 ? "0"+date.getHours() : ""+date.getHours()) );
        this.minutes.setText((date.getMinutes()<0 ? "0"+date.getMinutes() : ""+date.getMinutes()) );
    }

    public DateAndTimePicker(Date date){
        loadFXML();
        setDate(date);
    }

    public DateAndTimePicker(){
        loadFXML();
    }

    private void loadFXML() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Code/View/components/Date/PickDate/pickdate.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
