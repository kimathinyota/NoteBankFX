package Code.Model;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class StudySession {

    String name;


    List<String> ideas;
    String studyPlan;


    long startDate, endDate;
    int lastHour, lastMinute;

    public int getLastHour() {
        return lastHour;
    }

    public int getLastMinute() {
        return lastMinute;
    }

    public String getName() {
        return name;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public LocalTime getLastTime() {
        return LocalTime.of(lastHour,lastMinute);
    }

    public List<String> getIdeas(){
        return ideas;
    }



    public String getStudyPlan(){
        return studyPlan;
    }

    ActivationInformation activation;

    public ActivationInformation getActivation(){
        return activation;
    }


    public List<String> getSubjects() {
        return subjects;
    }

    public List<String> getTopics() {
        return topicsID;
    }

    List<String> subjects;
    List<String> topicsID;



    public void setSubjects(List<String> subjects){
        this.subjects = subjects;
    }

    public void setTopics(List<String> topics){
        this.topicsID = topics;
    }

    public void setIdeas(List<String> ideas){
        this.ideas = ideas;
    }


    public void setStartDate(Date date){
        this.startDate = date.getTime();
    }

    public void setEndDate(Date date){
        this.endDate = date.getTime();
    }

    public void setFinalHours(int hours){
        this.lastHour = hours;
    }

    public void setFinalMins(int mins ){
        this.lastMinute = mins;
    }


    public void setStudyPlan(StudyPlan plan){
        this.studyPlan = plan.getID();
    }



    public String toXML() {
        String xml = "<StudySession>" + System.lineSeparator();
        xml += "<Name>" + name + "</Name>";
        xml += "<StudyPlanID>" + studyPlan + "</StudyPlanID>";
        xml += "<LastTime><Hour>" + lastHour + "</Hour><Minute>" +  lastMinute + "</Minute></LastTime>";
        xml += "<StartDate>" + startDate + "</StartDate>";
        xml += "<EndDate>" + endDate + "</EndDate>" + System.lineSeparator();
        xml += "<Ideas>";
        for(String index: ideas){
            xml += "<Idea>" + index + "</Idea>";
        }
        xml += "</Ideas>" + System.lineSeparator();

        xml += "<Dynamic>" + System.lineSeparator();

        for(String t: topicsID){
            xml += "<Topic>" + t + "</Topic>" + System.lineSeparator();
        }

        for(String s: subjects){
            xml += "<Subject>" + s + "</Subject>" + System.lineSeparator();
        }

        xml += "</Dynamic>";

        if(activation!=null)
            xml += activation.toXML() + System.lineSeparator();
        xml += "</StudySession>";
        return xml;
    }

    public static StudySession fromXML(Element element){
        String name = (element.getElementsByTagName("Name").item(0)).getTextContent().trim() ;
        String studyPlan = (element.getElementsByTagName("StudyPlanID").item(0)).getTextContent().trim();
        long startDate = Long.valueOf((element.getElementsByTagName("StartDate").item(0)).getTextContent().trim());
        long endDate = Long.valueOf((element.getElementsByTagName("EndDate").item(0)).getTextContent().trim());

        Element lastTime = (Element) element.getElementsByTagName("LastTime").item(0);

        int lastHour = Integer.valueOf((lastTime.getElementsByTagName("Hour").item(0)).getTextContent().trim());
        int lastMinute = Integer.valueOf((lastTime.getElementsByTagName("Minute").item(0)).getTextContent().trim());

        Element ideas = (org.w3c.dom.Element) element.getElementsByTagName("Ideas").item(0);
        NodeList nodes = ideas.getElementsByTagName("Idea");
        List<String> indexes = new ArrayList<>();
        for(int i=0; i<nodes.getLength(); i++) {
            if(nodes.item(i) instanceof Element ) {
                Element idea = (org.w3c.dom.Element) nodes.item(i);
                String id = idea.getTextContent();
                indexes.add(id);
            }
        }

        NodeList topics = element.getElementsByTagName("Topic");
        List<String> ids = new ArrayList<>();
        for(int i=0; i<topics.getLength(); i++) {
            if(topics.item(i) instanceof Element ) {
                Element topic = (org.w3c.dom.Element) topics.item(i);
                String id = topic.getTextContent();
                ids.add(id);
            }
        }

        NodeList subjects = element.getElementsByTagName("Subject");
        List<String> subs = new ArrayList<>();

        for(int i=0; i<subjects.getLength(); i++) {
            if(subjects.item(i) instanceof Element ) {
                Element subject = (org.w3c.dom.Element) subjects.item(i);
                String id = subject.getTextContent();

                subs.add(id);
            }
        }





        ActivationInformation activationInformation;
        NodeList activation = element.getElementsByTagName("ActivationInformation");
        if(activation.getLength()==0){
            activationInformation = null;
        }else{
            activationInformation = ActivationInformation.fromXML((Element) element.getElementsByTagName("ActivationInformation").item(0));
        }
        return new StudySession(name,studyPlan,startDate,endDate, lastHour, lastMinute,indexes, ids, subs, activationInformation);

    }

    public StudySession(String name, String studyPlanID, long startDate, long endDate, int lastHour, int lastMinute, List<String>ideasID, List<String>topicsID, List<String> subjectsID, ActivationInformation activation){
        this.name = name;
        this.studyPlan = studyPlanID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.lastHour = lastHour;
        this.lastMinute = lastMinute;
        this.ideas = ideasID;
        this.activation = activation;
        this.subjects = subjectsID;
        this.topicsID = topicsID;
    }

    public void reactivate(ActivationInformation activation){
        this.activation = activation;
    }


}
