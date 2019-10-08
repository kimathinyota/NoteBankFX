package Code.View.components.MindMap;

import Code.View.ObservableObject;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;


public class MindMapCell<T extends ObservableObject> extends Button {

    T item;

    public static double getTextWidth(String label, Font font){
        FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();

        double nodeWidth = fontLoader.computeStringWidth(label,font);
        return nodeWidth;
    }

    public static double getTextHeight(Font font){
        return font.getSize();
    }


    public Rectangle getRectangle(){
        Rectangle r = new Rectangle();
        double width = getCalculatedWidth();
        double height = getCalculatedHeight();

        r.setWidth(width /*+ this.getPadding().getRight()*/);
        r.setHeight(height /*+ this.getPadding().getBottom()*/);
        r.setX(this.getLayoutX() /*- this.getPadding().getLeft() */);
        r.setY(this.getLayoutY() /*- this.getPadding().getTop()*/);
        r.setStroke(Color.BLACK);
        r.setFill(Color.TRANSPARENT);
        r.setArcHeight(32);
        r.setArcWidth(32);


        return r;
    }










    public void setCell(String fontFamily, int fontSize,String fontColor,  String backgroundColor,
                          double radius, Insets padding, double maxWidth, double maxHeight){


        this.setFont(Font.font(fontFamily,fontSize));

        this.setMaxSize(maxWidth,maxHeight);
        this.setWrapText(true);
        this.setTextAlignment(TextAlignment.CENTER);

        this.setPadding(padding);

        this.setStyle("-fx-background-color: " + backgroundColor +
                "; -fx-background-radius: " + radius + "; -fx-border-radius: " + radius +
                "; -fx-text-fill: " + fontColor + ";  " +
                "-fx-border-color: " + backgroundColor);
    }


    public double getCalculatedWidth(){
        double width = (getTextWidth(this.getText(),this.getFont()) + this.getPadding().getLeft() + this.getPadding().getRight())+3;

        if(width>this.getMaxWidth()){
            return this.getMaxWidth();
        }
        return width;
    }

    public double getCalculatedHeight(){

        /**
         * Height of a button is calculated using formula:
         * width = textWidth(button.getText, button,getFont) = width of text taking into consideration the font
         * height = textHeight(button.getText) = height of the font
         * maxWidth = (maxWidth of button) - button.leftPadding - button.rightPadding - 2
         * heightFactor = width/maxWidth
         * d = 5 = internal padding given to button text
         *
         * heightButton = (heightFactor + 2)d + (heightFactor + 1)*height + button.upPadding + button.downPadding
         */

        double height = (getTextHeight(this.getFont()));
        double width = (getTextWidth(this.getText(),this.getFont()));
        double maxWidth = this.getMaxWidth() - this.getPadding().getRight() - this.getPadding().getLeft() - 5;
        double heightFactor = (width<maxWidth) ? Math.floor((width/maxWidth)) : Math.round((width/maxWidth));

        //double heightFactor = Math.round((width/maxWidth));
        ////System.out.println(width + " " + maxWidth + " " + heightFactor + " " + (width/maxWidth) + " " +   this.getText());


        double d = 5.3;


        double calculatedHeight = (heightFactor + 2)*d + (heightFactor+1)*height + this.getPadding().getTop() + this.getPadding().getBottom();

        if(calculatedHeight>this.getMaxHeight()){
            return this.getMaxHeight();
        }



        return calculatedHeight;
    }


    public static double getHeight(String text, Font font, double maxWidth){
        double height = (getTextHeight(font));
        double width = (getTextWidth(text,font));
        double heightFactor = Math.round((width/maxWidth));
        double d = 5.3;
        double calculatedHeight = (heightFactor + 2)*d + (heightFactor+1)*height;
        return calculatedHeight;

    }


    public boolean holds(T item){
        return this.getText().equals(item);
    }


    public T getItem(){
        return item;
    }



    public void update(T item){
        this.item = item;
        this.setText(item.getMindMapName());
    }



    public MindMapCell(T item){
        update(item);
    }

}
