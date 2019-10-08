package Code.Model;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class StudySession {

    String name;
    List<String> ideas;
    String studyPlan;

    long startDate, endDate;
    int lastHour, lastMinute;

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

        ActivationInformation activationInformation;
        NodeList activation = element.getElementsByTagName("ActivationInformation");
        if(activation.getLength()==0){
            activationInformation = null;
        }else{
            activationInformation = ActivationInformation.fromXML((Element) element.getElementsByTagName("ActivationInformation").item(0));
        }
        return new StudySession(name,studyPlan,startDate,endDate, lastHour, lastMinute,indexes,activationInformation);

    }

    public StudySession(String name, String studyPlanID, long startDate, long endDate, int lastHour, int lastMinute, List<String>ideasID, ActivationInformation activation){
        this.name = name;
        this.studyPlan = studyPlanID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.lastHour = lastHour;
        this.lastMinute = lastMinute;
        this.ideas = ideasID;
        this.activation = activation;
    }

    public void reactivate(ActivationInformation activation){
        this.activation = activation;
    }


}
