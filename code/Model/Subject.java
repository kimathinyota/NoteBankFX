package Code.Model;

import Code.View.ObservableObject;
import com.sun.jmx.mbeanserver.NamedObject;
import com.sun.tools.corba.se.idl.constExpr.Not;
import org.bouncycastle.math.raw.Mod;
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

public class Subject implements ObservableObject {

    String name;
    List<String> paths;
    String currentSubject;

    public boolean equals(Object object){
        return (object instanceof Subject) && ((Subject) object).getName().equals(name);
    }

    public int hashCode(){
        return name.hashCode();
    }

    public void remove(Note note){
        remove(note.getPath().toString());
    }

    public void remove(String path){
        paths.remove(path);
    }


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


    public SubjectNote getNote(){
        return new SubjectNote(name);
    }


    public Subject(String name){
        this.paths = new ArrayList<>();
        this.name = name;
    }

    public void add(String path){
        if(!paths.contains(path))
            this.paths.add(path);
    }

    public void add(Note note){
        add(note.getPath().toString());
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


    public static List<Subject> getSubjects(List<Subject>subjects, Note note){
        List<Subject> subs = new ArrayList<>();
        for(Subject s: subjects){
            if(s.memberOf(note)){
                subs.add(s);
            }
        }
        return subs;
    }



    public String toString(){
        return "Subject: " + getName();
    }


    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public String getMindMapName() {
        return getDisplayName();
    }

    @Override
    public String getTreeName() {
        return getDisplayName();
    }

    @Override
    public boolean contains(Object object) {

        if(object instanceof Note){
            return getNotePaths().contains( ( (Note) object ).getPath());
        }

        Model model = Model.getInstance();
        if(object instanceof Idea){
            return ((Idea) object).isIdeaApartOfSubject(this);
            //return model.filterTopicBySubject(this).getAllIdeas().contains(object);
        }

        return false;

    }





}
