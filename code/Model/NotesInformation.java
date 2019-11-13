package Code.Model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class SingleNoteInformation{

    String path;
    int totalNumberOfPages;

    public String toXML(){
        String xml = "<NoteInformation>" + System.lineSeparator();
        xml += "<Path>" + path + "</Path>";
        xml += "<TotalNumberOfPages>" + totalNumberOfPages + "</TotalNumberOfPages>" + System.lineSeparator();
        xml += "</NoteInformation>";
        return xml;
    }

    public boolean isPath(String path){
        return this.path.equals(path);
    }



    public static SingleNoteInformation fromXML(Element element){
        int totalNumberOfPages = Integer.valueOf((element.getElementsByTagName("TotalNumberOfPages").item(0)).getTextContent().trim());
        String path = ((Element) element.getElementsByTagName("Path").item(0)).getTextContent() ;

        SingleNoteInformation information = new SingleNoteInformation(path,totalNumberOfPages);

        return information;
    }

    public SingleNoteInformation(String path, int totalNumberOfPages){
        this.path = path;
        this.totalNumberOfPages = totalNumberOfPages;
    }


    public int getTotalNumberOfPages() {
        return totalNumberOfPages;
    }

    public void setTotalNumberOfPages(int totalNumberOfPages) {
        this.totalNumberOfPages = totalNumberOfPages;
    }
}

public class NotesInformation {

    List<SingleNoteInformation> information;

    public void addNumberOfPages(String path, int numberOfPages){
        for(SingleNoteInformation i: information){
            if(i.isPath(path)){
                i.setTotalNumberOfPages(numberOfPages);
                return;
            }
        }
        information.add(new SingleNoteInformation(path,numberOfPages));
    }

    public Integer getNumberOfPages(String path){
        for(SingleNoteInformation i: information){
            if(i.isPath(path)){
                return i.getTotalNumberOfPages();
            }
        }
        return null;
    }

    public void removeNote(String path){
        List<SingleNoteInformation> remove = new ArrayList<>();
        for(SingleNoteInformation i: information){
            if(i.isPath(path)){
                remove.add(i);
            }
        }
        information.removeAll(remove);
    }

    public static NotesInformation fromXML(String loc) throws ParserConfigurationException, FileNotFoundException, SAXException, IOException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document document = builder.parse( new FileInputStream(loc) );
        Element rootElement = document.getDocumentElement();
        return fromXML(rootElement);
    }

    public static NotesInformation fromXML(Element element){

        List<SingleNoteInformation> information = new ArrayList<>();

        NodeList nodes = element.getElementsByTagName("NoteInformation");

        for(int i=0; i<nodes.getLength(); i++){
            information.add(SingleNoteInformation.fromXML((Element) nodes.item(i)));
        }

        return new NotesInformation(information);
    }

    public String toXML(){
        String xml = "<NotesInformation>" + System.lineSeparator();
        for(SingleNoteInformation i: information){
            xml += i.toXML() + System.lineSeparator();
        }
        xml += "</NotesInformation>";
        return xml;
    }




    public NotesInformation(){
        information = new ArrayList<>();
    }

    public NotesInformation(List<SingleNoteInformation> information){
        this.information = information;
    }



}
