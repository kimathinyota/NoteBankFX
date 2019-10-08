package Code.View.components.MindMap;

import Code.View.ObservableObject;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.util.Pair;

import java.util.*;

class MapGroupNode{
    double angle;
    double radius;



    public MindMapRecursive getMindMap() {
        return mindMap;
    }

    MindMapRecursive mindMap;

    Pair<Double,Double> size;

    public Pair<Double,Double> getSize(){
        return size;
    }

    public void setAngle(double angle){
        this.angle = angle;
    }

    public double getAngle() {
        return angle;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public String toString(){
        return mindMap.toString();
    }

/*
    public Pair<Pair<Double,Double>,Pair<Double,Double>> getRectangle(double cX, double cY){

        List<Pair<Double,Double>> points = new ArrayList<>();

        Pair<Double,Double> offset = mindMap.getCenterDifference();


        points.add(new Pair<>());


    }
*/


    public MapGroupNode(MindMapRecursive mindMap){
        this.mindMap = mindMap;
        this.size = new Pair<>(mindMap.getWidth(),mindMap.getHeight());
    }

    public MapGroupNode(MindMapRecursive mindMap, Pair<Double,Double> centerSize){
        this.mindMap = mindMap;
        this.size = centerSize;
    }
}

class MapGroupList extends ArrayList<MapGroupNode> {
    double radius;
    boolean isFull;

    Pair<Double,Double>largestNode;

    public void setRadius(double radius){
        this.radius = radius;
    }

    public double getRadius(){
        return radius;
    }

    Pair<Double, Double> averageSize;

    double averageLength;


    public boolean withinAverageSize(MapGroupNode mapGroupNode){
        return (!lessThan(mapGroupNode) && !greaterThan(mapGroupNode));
    }

    public boolean lessThan(MapGroupNode mapGroupNode){

        return mapGroupNode.getMindMap().getCumalitiveSize() < 0.8 * averageLength;

        /*

        return ((Double) mapGroupNode.getSize().getKey()) < 0.8 * averageSize.getKey().doubleValue() &&
                ((Double) mapGroupNode.getSize().getValue()).doubleValue() < 0.8 * averageSize.getValue().doubleValue();
                */

    }

    public boolean greaterThan(MapGroupNode mapGroupNode){

        return mapGroupNode.getMindMap().getCumalitiveSize() > 1.2 * averageLength;

        /*
        return ((Double) mapGroupNode.getSize().getKey()) > 1.2 * averageSize.getKey().doubleValue() &&
                ((Double) mapGroupNode.getSize().getValue()).doubleValue() > 1.2 * averageSize.getValue().doubleValue();

    */
    }

    public double length(Pair<Double,Double> size){
        return Math.sqrt(size.getKey()*size.getKey() + size.getValue()*size.getValue());
    }

    @Override
    public boolean add(MapGroupNode mapGroupNode) {

        if (!withinAverageSize(mapGroupNode)) {
            return false;
        }

        /*
        double averageWidth = ((((Double) mapGroupNode.getSize().getKey()).doubleValue() + averageSize.getKey().doubleValue() * this.size()) / (this.size() + 1));
        double averageHeight = ((((Double) mapGroupNode.getSize().getValue()).doubleValue() + averageSize.getValue().doubleValue() * this.size()) / (this.size() + 1));


        if(length(mapGroupNode.size)>length(largestNode)){
            largestNode = mapGroupNode.size;
        }

        averageSize = new Pair<Double, Double>(averageWidth, averageHeight);
        */

        boolean ret =  super.add(mapGroupNode);

        recalculateAverageSize();

        return ret;

    }





    /*
    private void recalculateAverageSize(){
        double width = 0;
        double height = 0;
        for(MapGroupNode n: this){
            width += n.getSize().getKey();
            height += n.getSize().getValue();
        }
        this.averageSize = new Pair<>(width/this.size(),height/this.size()/2);
    }
    */

    private void recalculateAverageSize(){
        double length = 0;
        for(MapGroupNode n: this){
            length += n.getMindMap().getCumalitiveSize();
        }
        averageLength =  length/this.size();
    }

    @Override
    public MapGroupNode remove(int index) {
        MapGroupNode node =  super.remove(index);
        recalculateAverageSize();
        return node;
    }

    @Override
    public boolean remove(Object o) {
       boolean b = super.remove(o);
        recalculateAverageSize();
        return b;
    }

    public int findPositionInQueue(MapGroupNode node){
        for(int i=0; i<size(); i++){
            if( this.get(i).getMindMap().getCumalitiveSize() > node.getMindMap().getCumalitiveSize()){
                return i;
            }
        }
        return size();
    }

    public MapGroupList(double radius, MapGroupNode node){
        this.radius = radius;
        this.averageSize = node.getSize();
        this.averageLength = node.getMindMap().getCumalitiveSize();
        this.largestNode = node.getSize();
        this.isFull = false;

        this.add(node);

    }

}

class MapGroupLists{

    List<MapGroupList> lists;
    private double cX, cY;
    MapGroupNode centerNode;
    private double extraAngle = 40;

    public void setAngle(double angle){
        this.extraAngle = angle;
    }

    public List<MapGroupNode> getAllNodes(){
        List<MapGroupNode> nodes = new ArrayList<>();
        nodes.add(centerNode);
        for(MapGroupList l: lists){
            nodes.addAll(l);
        }
        return nodes;
    }

    public void reset(double cX, double cY){
        this.cX = cX;
        this.cY = cY;
    }

    public List<MapGroupList> getLists(MapGroupNode node){
        List<MapGroupList> ls = new ArrayList<>();
        for(MapGroupList m: lists){
            if(m.withinAverageSize(node)){
                ls.add(m);
            }
        }
        return ls;
    }

    public List<MapGroupNode> add(Collection<MapGroupNode> nodes){
        List<MapGroupNode> list = new ArrayList<>();
        for(MapGroupNode n: nodes){
            list.addAll(add(n));
        }
        return list;
    }

    public void setCenterNode(MapGroupNode centerNode){
        this.centerNode = centerNode;
        centerNode.setAngle(0);
        centerNode.setRadius(0);
    }




    public List<MapGroupNode> add(MapGroupNode mapGroupNode){

        // Check if lists is empty: If so then create a new list and the node here

        //System.out.println("Adding " + mapGroupNode +  " to " + centerNode);

        //System.out.println( ( !lists.isEmpty() ? lists.get(0).averageLength : null) + " compared to " + mapGroupNode.getMindMap().getCumalitiveSize());

        //System.out.println("ALL LISTS: " + lists);

        if(lists.isEmpty()){

            //System.out.println("Empty");

            //Radius will be the smallest radius required to surround center node without touching it

            double newRadius = MindMapRecursive.getSmallestRadialDistance(0,centerNode.getSize().getKey(),centerNode.getSize().getValue());

            newRadius = MindMapRecursive.getSmallestRadialDistance(newRadius,2*mapGroupNode.getSize().getKey(),2*mapGroupNode.getSize().getValue());

            //System.out.println("Creating new MapGroupList of radius " + newRadius + " to lists at an angle of 0");
            lists.add(new MapGroupList(newRadius,mapGroupNode));

            mapGroupNode.setRadius(newRadius);
            mapGroupNode.setAngle(0);


            //System.out.println("After lists: " + lists);
            //System.out.println("\n\n\n\n");

            return Collections.singletonList(mapGroupNode);

        }

        if(lists.get(0).lessThan(mapGroupNode)){
            //We need to restart with this node as the first node

            //System.out.println("This is the smallest node so let's readd all bigger nodes");

            List<MapGroupNode> nodes = new ArrayList<>();
            nodes.add(mapGroupNode);
            for(MapGroupList n: lists){
                nodes.addAll(n);
            }

            lists.clear();

            //System.out.println("\n\n\n\n");
            return add(nodes);

        }


        // Need to find most appropriate lists to add node to
        //       Node needs to be within the list size range for the list to be included


        List<MapGroupNode> displacedNodes = new ArrayList<>();
        List<MapGroupList> ls = getLists(mapGroupNode);
        //System.out.println("Found lists for " + mapGroupNode + ": " + ls);

        for(MapGroupList list: ls){

            //System.out.println("Trying " + list);

            if(list!=null){
                // List exists
                // Let's find the position this node should be in this list
                int correctNodePosition = list.findPositionInQueue(mapGroupNode);

                //We need to find and remove displaced nodes - they will have to be added again
                displacedNodes = new ArrayList<>();
                for(int i=correctNodePosition; i<list.size(); i++){
                    displacedNodes.add(list.get(i));
                }

                //System.out.println("Removed and eventually readding: " + displacedNodes);
                list.removeAll(displacedNodes);

                //We need to add node to the list

                if(list.isEmpty()){
                    //well list is no longer needed since it's empty so let's remove it



                    //do i need to readjust all above lists or should i add this list as the first item (need to experiment with this)
                    //System.out.println("Upon removal current list is empty so lets re-add all nodes");

                    List<MapGroupNode> allNodes = new ArrayList<>();

                    allNodes.add(mapGroupNode);
                    allNodes.addAll(displacedNodes);

                    lists.remove(list);



                    //System.out.println("\n\n\n\n");
                    return add(allNodes);
                }



                MapGroupNode lastNode = list.get(correctNodePosition-1);
                MapGroupNode firstNode = list.get(0);

                //System.out.println("Previous node " + lastNode + " Position of current node: " + correctNodePosition);


                List<Pair<Pair<Double,Double>,Pair<Double,Double>>> nodes = new ArrayList<>();
                for(MapGroupNode n: getAllNodes()){
                    nodes.add(new Pair<>(n.getSize(),MindMapRecursive.getCenterPoint(cX,cY,n.getRadius(),n.getAngle())));
                }
                //Now we need to add the current node as close to this last node as possible while still maintaining same radius

                //Pair<Double,Double> lastNodeCP  = MindMapRecursive.getCenterPoint(cX,cY,lastNode.getRadius(),lastNode.getAngle());
                //Pair<Double,Double> firstNodeCP = MindMapRecursive.getCenterPoint(cX,cY,firstNode.getRadius(),firstNode.getAngle());

                Double newAngle = MindMapRecursive.smallestAngleFromObjectAToObjectBWithoutTouching(list.getRadius(),lastNode.getAngle(),cX,cY,nodes,mapGroupNode.getSize());

                //System.out.println("Found Angle: " + newAngle + " Radius: " + list.getRadius());

                if(newAngle!=null){
                    //The angle is not null so we can now add the node to this list under this angle


                    mapGroupNode.setAngle(newAngle);
                    mapGroupNode.setRadius(list.getRadius());

                    //System.out.println("Adding node to this list");

                    list.add(mapGroupNode);

                    List<MapGroupNode> node = new ArrayList<>();
                    node.add(mapGroupNode);
                    if(!displacedNodes.isEmpty()){
                        node.addAll(add(displacedNodes));
                    }

                    //System.out.println("\n\n\n\n");
                    return node;
                }

            }

        }





        if(!ls.isEmpty()){

            //System.out.println("All lists are full ");

            MapGroupList list = ls.get(ls.size()-1);

            //This means that this has to be added to a new list because all of the lists are full

            List<Pair<Pair<Double,Double>,Pair<Double,Double>>> listNodes = new ArrayList<>();
            MapGroupNode lastNode = list.get(list.size()-1);

            for(MapGroupNode node: getAllNodes()){
                listNodes.add(new Pair<>(node.getSize(),MindMapRecursive.getCenterPoint(cX,cY,node.getRadius(),node.getAngle())));
            }

            double angle = list.get(list.size()-1).getAngle() + extraAngle;
            double newRadius = MindMapRecursive.findClosestRadius(lastNode.getRadius(),angle,cX,cX,listNodes,mapGroupNode.getSize());

            mapGroupNode.setAngle(angle);
            mapGroupNode.setRadius(newRadius);

            //We need to re-add any nodes higher than this point

            List<MapGroupNode> ns = new ArrayList<>();


            for(int i=lists.indexOf(list)+1; i<lists.size(); i+=1) {
                ns.addAll(lists.get(i));
                lists.remove(i);
            }


            //System.out.println( "Angle: " + angle + " Radius: " +newRadius );

            lists.add(new MapGroupList(newRadius,mapGroupNode));

            List<MapGroupNode> nodes = new ArrayList<>();

            nodes.add(mapGroupNode);

            if(!displacedNodes.isEmpty()){
                //System.out.println("\n\n\n\n");
                //System.out.println("Readding all displaced nodes: " + displacedNodes);
                nodes.addAll(add(displacedNodes));
            }

            if(!ns.isEmpty()){
                //System.out.println("\n\n\n\n");
                //System.out.println("Readding all nodes higher nodes: " + ns);
                nodes.addAll(add(ns));
            }


            //System.out.println("\n\n\n\n");
            return nodes;

        }


        //System.out.println("Lists cant't be found");

        //if(lists.get(lists.size()-1).greaterThan(mapGroupNode)){
            //We need to add a new list for this mapGroupNode
            MapGroupList lastList = lists.get(lists.size()-1);
            //double radius = MindMapRecursive.getSmallestRadialDistance(lastList.getRadius(),lastList.getAverageSize().getKey(),lastList.getAverageSize().getValue());
            List<Pair<Pair<Double,Double>,Pair<Double,Double>>> listNodes = new ArrayList<>();
            for(MapGroupNode node: getAllNodes()){
                listNodes.add(new Pair<>(node.getSize(), MindMapRecursive.getCenterPoint(cX,cY,node.getRadius(),node.getAngle()) ));
            }

            double angle = lastList.get(lastList.size()-1).getAngle() + extraAngle;
            double radius = MindMapRecursive.findClosestRadius(lastList.radius,angle,cX,cY,listNodes,mapGroupNode.getSize());

            this.lists.add(new MapGroupList(radius,mapGroupNode));
            mapGroupNode.setRadius(radius);
            mapGroupNode.setAngle(angle);

            //System.out.println("Angle: " + angle + " Radius: " + radius);

            //System.out.println("\n\n\n\n");
            return Collections.singletonList(mapGroupNode);

        //}







    }



    public MapGroupLists(double cX, double cY){
        this.lists = new ArrayList<>();
        this.cX = cX;
        this.cY = cY;
    }

}

public class MindMapRecursive<T extends ObservableObject> extends Pane {

    protected T center;
    protected double width;
    protected double height;
    protected HashMap<MindMapRecursive, Shape> nodeToLine;
    protected double maxRadialDistance;
    protected MindMapFactory factory;
    protected MindMapCell<T> mainNode;
    protected MapGroupLists mapGroup;
    private Line pad;

    public MindMapCell getMainNode(){
        return mainNode;
    }

    public Pair<Double,Double> getCenterOfCenterNode(){
        double x = getLayoutX() + mainNode.getLayoutX();
        double y = getLayoutY() +mainNode.getLayoutY();

        x = x + (mainNode.getCalculatedWidth()/2);
        y = y + (mainNode.getCalculatedHeight()/2);

        return new Pair<>(x,y);
    }

    public String toString(){
        return center.toString();
    }

    private List<MindMapRecursive> getAllMindMapCells(){
        List<MindMapRecursive> cells = new ArrayList<>();
        for(Node n: this.getChildren()){
            if(n instanceof MindMapRecursive){
                cells.add((MindMapRecursive) n);
            }
        }
        return cells;
    }

    public void reset(T center){
        this.getStylesheets().add("/Code/View/css/MindMap.css");
        nodeToLine = new HashMap<>();
        this.factory = new MindMapFactory() {
            @Override
            public Line call(Line shape, ObservableObject observableObject) {
                shape.getStyleClass().add("mind-map-line");
                return shape;
            }
            @Override
            public MindMapCell call(ObservableObject item) {
                MindMapCell cell = new MindMapCell<>(item);
                double padding = 10;
                MindMapRecursive.setCell(cell, Font.font("Inter-Medium", 20),new Insets(padding,padding,padding,padding),200,100);
                cell.getStyleClass().add("mind-map-center");
                return cell;
            }
        };

        this.center = center;
        setFactory(factory);

    }

    public void reset(T center, MindMapFactory<T> factory){
        this.getStylesheets().add("/Code/View/css/MindMap.css");
        nodeToLine = new HashMap<>();
        this.factory = factory;
        this.center = center;
        setFactory(factory);
    }

    public T getCenter(){
        return center;
    }

    public void updateFactory(MindMapFactory factory){
        this.factory = factory;
    }

    public void addOuterItem(MindMapRecursive cell, double lineLength, double angleDeg){
        double sinAngle = Math.sin(Math.toRadians(angleDeg));
        double cosAngle = Math.cos(Math.toRadians(angleDeg));


        double cX = this.getCenterOfCenterNode().getKey();
        double cY = this.getCenterOfCenterNode().getValue();

        double endX = cX + (lineLength*sinAngle);
        double endY = cY - (lineLength*cosAngle);


        if(this.getChildren().contains(cell)){
            Shape line = nodeToLine.get(cell);
            this.getChildren().remove(cell);
            this.getChildren().remove(line);
            this.nodeToLine.remove(line);
        }

        this.getChildren().add(cell);


        double midX = (cell.getWidth())/2;
        double midY = (cell.getHeight())/2;

        double x = (endX - midX) /*+ (midX)*sinAngle*/;
        double y =  (endY - midY) /*- (midY)*cosAngle*/;

        cell.setLayoutX(x);
        cell.setLayoutY(y);

        Pair<Double,Double> c = cell.getCenterOfCenterNode();

        Line sectorLine = factory.call(new Line(getCenterOfCenterNode().getKey(),getCenterOfCenterNode().getValue(), c.getKey(), c.getValue()), cell.getCenter());

        this.getChildren().add(sectorLine);

        nodeToLine.put(cell,sectorLine);

        trim();

    }

    public void remove(List<MapGroupNode> nodes){
        for(Node item: FXCollections.observableArrayList(nodeToLine.keySet())){
            if( item instanceof MindMapRecursive){
                MindMapRecursive r = ((MindMapRecursive) item);
                for(MapGroupNode n: nodes){
                    if(r.equals(n.getMindMap())){
                        this.getChildren().remove(item);
                        this.getChildren().remove(nodeToLine.get(r));
                        nodeToLine.remove(r);
                    }
                }
            }
        }
    }

    public void addItem(MindMapRecursive cell){

        MapGroupNode  node = new MapGroupNode(cell);

        List<MapGroupNode> nodes = this.mapGroup.add(node);

        for(MapGroupNode n: nodes){

            this.addOuterItem(n.getMindMap(),n.getRadius(),n.getAngle());
        }

        reorganiseLines();

    }

    public void reorganiseLines(){
        List<Shape> shapes = new ArrayList<>();
        shapes.addAll(nodeToLine.values());

        shapes.sort(new Comparator<Shape>() {
            @Override
            public int compare(Shape o1, Shape o2) {
                Double length1 = Math.sqrt(o1.getLayoutBounds().getWidth()*o1.getLayoutBounds().getWidth() + o1.getLayoutBounds().getHeight()*o1.getLayoutBounds().getHeight());
                Double length2 = Math.sqrt(o2.getLayoutBounds().getWidth()*o2.getLayoutBounds().getWidth() + o2.getLayoutBounds().getHeight()*o2.getLayoutBounds().getHeight());
                return length2.compareTo(length1);
            }
        });

        List<MindMapRecursive> cells = this.getAllMindMapCells();
        this.getChildren().removeAll(cells);
        this.getChildren().removeAll(shapes);
        this.getChildren().addAll(shapes);
        this.getChildren().addAll(cells);

        this.getChildren().remove(mainNode);
        this.getChildren().add(mainNode);
    }

    private Pair<Pair<Double,Double>,Pair<Double,Double>> getBounds(){
        double left = width;
        left = (mainNode.getLayoutX()<left ? mainNode.getLayoutX() : left);
        double right = 0;
        double x1 = mainNode.getLayoutX()+mainNode.getCalculatedWidth();
        right = (x1>right ? x1 : right);
        double top = height;
        top = (mainNode.getLayoutY()<top ? mainNode.getLayoutY() : top);
        double bottom = 0;
        double y1 = mainNode.getLayoutY()+mainNode.getCalculatedHeight();
        bottom = (y1>bottom ? y1 : bottom);

        for(Node n: getChildren()){
            if(n instanceof MindMapRecursive){
                left = (n.getLayoutX()<left ? n.getLayoutX() : left);
                double x = n.getLayoutX()+((MindMapRecursive) n).getWidth();
                right = (x>right ? x : right);
                top = (n.getLayoutY()<top ? n.getLayoutY() : top);
                double y = n.getLayoutY()+((MindMapRecursive) n).getHeight();
                bottom = (y>bottom ? y : bottom);
            }
        }

        return new Pair<>(new Pair<>(left,right),new Pair<>(top,bottom));
    }

    public void trim(){
        Pair<Pair<Double,Double>,Pair<Double,Double>> bounds = getBounds();
        double left = bounds.getKey().getKey();
        double right = bounds.getKey().getValue();
        double top = bounds.getValue().getKey();
        double bottom = bounds.getValue().getValue();

        this.width = right - left;

        this.height = bottom - top;


        for(Node n: getChildren()){
            if(n instanceof Line){
                ((Line) n).setStartX(((Line) n).getStartX()-left);
                ((Line) n).setStartY(((Line) n).getStartY()-top);

                ((Line) n).setEndX(((Line) n).getEndX()-left);
                ((Line) n).setEndY(((Line) n).getEndY()-top);
            }else if(n instanceof Rectangle){
                ((Rectangle) n).setX(((Rectangle) n).getX()-left);
                ((Rectangle) n).setY(((Rectangle) n).getY()-top);
            }else{
                n.setLayoutX(n.getLayoutX()-left);
                n.setLayoutY(n.getLayoutY()-top);
            }
        }


        this.setHeight(height);
        this.setWidth(width);
        mapGroup.reset(getCenterOfCenterNode().getKey(),getCenterOfCenterNode().getValue());

    }


    public void setAngle(double extraAngle){
        this.mapGroup.setAngle(extraAngle);
    }


    public Pair<Pair<Double,Double>,Pair<Double,Double>> getEdgeDistances(){
        double left = getCenterOfCenterNode().getKey() - getLayoutX();
        double right = getWidth() - getCenterOfCenterNode().getKey() - getLayoutX();
        double top = getCenterOfCenterNode().getValue() - getLayoutY();
        double bottom = getHeight() - getCenterOfCenterNode().getValue() - getLayoutY();
        return new Pair<>(new Pair<>(left,right), new Pair<>(top,bottom));
    }





    public void pad(double left, double right, double top, double bottom){

        if(pad!=null){
            this.getChildren().remove(pad);
        }
        this.width = width + left + right ;
        this.height = height + top + bottom ;

        this.setWidth(width);
        this.setHeight(height);


        for(Node n: getChildren()){
            if(n instanceof Line){
                ((Line) n).setStartX(((Line) n).getStartX()+left);
                ((Line) n).setStartY(((Line) n).getStartY()+top);

                ((Line) n).setEndX(((Line) n).getEndX()+left);
                ((Line) n).setEndY(((Line) n).getEndY()+top);
            }else{
                n.setLayoutX(n.getLayoutX()+left);
                n.setLayoutY(n.getLayoutY()+top);
            }
        }

        pad = new Line();
        pad.setStartX(0); pad.setStartY(0);
        pad.setEndX(width); pad.setEndY(height);
        pad.setStyle("-fx-stroke: transparent");
        pad.setStrokeWidth(0);
        this.getChildren().add(pad);


        this.mapGroup.reset(getCenterOfCenterNode().getKey(),getCenterOfCenterNode().getValue());

    }

    public int getCumalitiveSize(){
        int size = center.getMindMapName().length();
        for(MindMapRecursive o: getAllMindMapCells()){
            size+=o.getCumalitiveSize();
        }
        return size;
    }

    public MindMapRecursive(T center){
        reset(center);
    }

    public MindMapRecursive(T center, MindMapFactory<T> factory){
        reset(center,factory);
    }

    public static void setCell(MindMapCell cell, Font font, Insets padding, double maxWidth, double maxHeight){
        cell.setFont(font);
        cell.setPadding(padding);
        cell.setWrapText(true);
        cell.setMaxSize(maxWidth,maxHeight);
    }

    protected void setFactory(MindMapFactory factory){
        this.factory = factory;
        this.width = factory.call(center).getCalculatedWidth();
        this.height = factory.call(center).getCalculatedHeight();
        this.setWidth(width);
        this.setHeight(height);
        this.maxRadialDistance = MindMapRecursive.getSmallestRadialDistance(0,height,width);
        this.mainNode = factory.call(center);

        this.getChildren().add(mainNode);
        mainNode.setLayoutX(0);
        mainNode.setLayoutY(0);
        this.mapGroup = new MapGroupLists(getCenterOfCenterNode().getKey(),getCenterOfCenterNode().getValue());

        this.mapGroup.setCenterNode(new MapGroupNode(this,new Pair<>(mainNode.getCalculatedWidth(),mainNode.getCalculatedHeight())));

   }

    public static MindMapRecursive mindMap(ObservableObject center, List<ObservableObject> outerNodes){
        MindMapRecursive map = new MindMapRecursive(center);
        outerNodes.sort(new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        for(ObservableObject item: outerNodes){
            map.addItem(new MindMapRecursive(item));
        }
        return map;
    }

    public static double getSmallestRadialDistance(double radialDistanceToA, double aWidth, double aHeight ){
        double n = Math.sqrt( aWidth*aWidth/4 + aHeight*aHeight/4);
        //double n = (aWidth < aHeight ? aHeight : aWidth);
        return (radialDistanceToA + n) ;
    }

    private static boolean IfPointInsersectsWithRectangle( double pointX, double pointY, double rWidth, double rHeight, double rCenterX, double rCenterY){
        return (  ( (pointX ) <= (rCenterX + rWidth/2)  &&  (pointX ) >= (rCenterX - rWidth/2) ) &&
                (   (pointY ) <= (rCenterY + rHeight/2)  &&  (pointY ) >= (rCenterY - rHeight/2) ) );
    }

    private static boolean IfRectanglesIntersect(double aWidth, double aHeight, double aCenterX, double aCenterY,
                                                 double bWidth, double bHeight, double bCenterX, double bCenterY){

        List<  Pair<Double,Double>  > points = FXCollections.observableArrayList( new Pair<>(aCenterX+aWidth/2,aCenterY+aHeight/2), new Pair<>(aCenterX+aWidth/2,aCenterY-aHeight/2), new Pair<>(aCenterX-aWidth/2,aCenterY-aHeight/2), new Pair<>(aCenterX-aWidth/2,aCenterY+aHeight/2));

        for(Pair<Double,Double> p: points){
            if(IfPointInsersectsWithRectangle(p.getKey().doubleValue(),p.getValue().doubleValue(),bWidth,bHeight,bCenterX,bCenterY)){
                return true;
            }
        }
        return false;
    }

    private static boolean IfRectanglesIntersect(Pair<Pair<Double,Double>,Pair<Double,Double>> a,
                                                 Pair<Pair<Double,Double>,Pair<Double,Double>> b) {

        return IfRectanglesIntersect(b.getKey().getKey(),b.getKey().getValue(),b.getValue().getKey(),b.getValue().getValue(),
                a.getKey().getKey(),a.getKey().getValue(),a.getValue().getKey(),a.getValue().getValue()) ||
                IfRectanglesIntersect(a.getKey().getKey(),a.getKey().getValue(),a.getValue().getKey(),a.getValue().getValue(),
                        b.getKey().getKey(),b.getKey().getValue(),b.getValue().getKey(),b.getValue().getValue());
    }


    public static Pair<Double,Double> getCenterPoint(double cX, double cY, double lineLength, double angleDeg ){
        double sinAngle = Math.sin(Math.toRadians(angleDeg));
        double cosAngle = Math.cos(Math.toRadians(angleDeg));

        double endX = cX + (lineLength*sinAngle);
        double endY = cY - (lineLength*cosAngle);

        return new Pair<>(endX,endY);

    }


    public static Double smallestAngleFromObjectAToObjectBWithoutTouching(double radius, double angleOfA,double cX, double cY, List<Pair<Pair<Double,Double>,Pair<Double,Double>>> allNodes, Pair<Double,Double>bSize){
        double increment = 1;

        for(double i=angleOfA; i<720; i+=increment){

            double centerX =cX + (radius*Math.sin(Math.toRadians(i)));
            double centerY = cY -  (radius*Math.cos(Math.toRadians(i)));
            Pair<Pair<Double,Double>,Pair<Double,Double>> b = new Pair<>(bSize,new Pair<>(centerX,centerY));
            boolean doesIntersect = false;
            for(Pair<Pair<Double,Double>,Pair<Double,Double>> rectangle: allNodes){
                if(IfRectanglesIntersect(rectangle,b)){
                    doesIntersect = true;
                }
            }
            if(!doesIntersect){
                return i;
            }
        }
        return null;
    }


    public static Double findClosestRadius(double radius, double angle,double cX, double cY, List<Pair<Pair<Double,Double>,Pair<Double,Double>>> allNodes, Pair<Double,Double>bSize
    ){
        double increment = 1;
        double newRadius = radius;

        while ( true ){
            double centerX = cX + (newRadius*Math.sin(Math.toRadians(angle)));
            double centerY = cY -  (newRadius*Math.cos(Math.toRadians(angle)));
            Pair<Pair<Double,Double>,Pair<Double,Double>> b = new Pair<>(bSize,new Pair<>(centerX,centerY));
            boolean doesIntersect = false;
            for(Pair<Pair<Double,Double>,Pair<Double,Double>> rectangle: allNodes){
                if(IfRectanglesIntersect(rectangle,b)){
                    doesIntersect = true;
                }
            }
            if(!doesIntersect){
                return newRadius;
            }
            newRadius+=increment;
        }
    }







}
