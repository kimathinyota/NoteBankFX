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

public class Subject {

    String name;
    List<String> paths;
    String currentSubject;


    public void setName(String name){
        this.name = name;
    }

    public void setPaths(List<String>paths){
        this.paths = paths;
    }

    public List<String> getNotePaths(){
        return this.paths;
    }

    public String getName(){
        return name;
    }

    public Subject(String name){
        this.paths = new ArrayList<>();
        this.name = name;
    }

    public void add(String path){
        this.paths.add(path);
    }

    private static Subject toSubject(Element subject) throws IOException {
        String name = ((Element) subject.getElementsByTagName("Name").item(0)).getTextContent() ;
        NodeList nodes = subject.getElementsByTagName("Note");
        Subject sub = new Subject(name);
        for(int i=0; i<nodes.getLength(); i++) {
            if(nodes.item(i) instanceof Element) {
                Element elem = (Element) nodes.item(i);
                if(elem.getParentNode().equals(subject)) {
                    String path = elem.getElementsByTagName("Path").item(0).getTextContent();
                    sub.add(path);
                }
            }
        }
        return sub;
    }

    public static List<Subject> fromXML(Element subjects) throws IOException{
        NodeList nodes = subjects.getElementsByTagName("Subject");
        List<Subject> subjectList = new ArrayList<>();
        for(int i=0; i<nodes.getLength(); i++) {
            if(nodes.item(i) instanceof Element) {
                Element elem = (Element) nodes.item(i);
                if(elem.getParentNode().equals(subjects)) {
                    Subject subject = toSubject(elem);
                    subjectList.add(subject);
                }
            }
        }
        return subjectList;
    }

    public static List<Subject> fromXML(String loc) throws ParserConfigurationException, FileNotFoundException, SAXException, IOException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document document = builder.parse( new FileInputStream(loc) );
        Element rootElement = document.getDocumentElement();

        return Subject.fromXML(rootElement);
    }


    public static String toXML(List<Subject> subjects){
        String xml = "<Subjects>" + System.lineSeparator();
        for(Subject s: subjects){
            xml += s.toXML();
        }
        xml += "</Subjects>";
        return xml;
    }

    public boolean memberOf(Note note){
        return memberOf(note.getPath().toString());
    }

    public boolean memberOf(String path){
        return this.paths.contains(path);
    }



    public String toXML(){
        String xml = "";
        xml += "<Subject>" + System.lineSeparator();
        xml += "<Name>" + name + "</Name>" + System.lineSeparator();
        for(String path: paths){
            xml += "<Note><Path>" + path + "</Path></Note>" + System.lineSeparator();
        }
        xml += "</Subject>" + System.lineSeparator();
        return xml;
    }



}
