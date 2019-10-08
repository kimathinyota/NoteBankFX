package Code.Model;


import org.w3c.dom.Element;

import java.util.UUID;

public class StudyPlan {

    String ID;
    StudySession studySession;
    String name;

    public String getDescription() {
        return description;
    }

    String description;
    double ideasPerSetPercentage;
    double minimumSessionPercentage;
    double scorePercentage;

    public void setStudySession(StudySession studySession) {
        this.studySession = studySession;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIdeasPerSetPercentage(double ideasPerSetPercentage) {
        this.ideasPerSetPercentage = ideasPerSetPercentage;
    }

    public void setMinimumSessionPercentage(double minimumSessionPercentage) {
        this.minimumSessionPercentage = minimumSessionPercentage;
    }

    public void setCompletionScore(double completionScore) {
        this.scorePercentage = completionScore;
    }

    public String getName(){
        return name;
    }

    public double getIPS(){
        return ideasPerSetPercentage;
    }

    public double getMSL(){
        return minimumSessionPercentage;
    }



    public String getID(){
        return ID;
    }

    public String toXML(){
        String xml = "<StudyPlan>";
        xml += "<ID>" + ID + "</ID>";
        xml += "<Name>" + name + "</Name>";
        xml += "<Description>" + description + "</Description>";
        xml += "<MSL>" + minimumSessionPercentage + "</MSL>";
        xml += "<IPS>" + ideasPerSetPercentage + "</IPS>";
        xml += "<Score>" + scorePercentage + "</Score>";
        xml += "</StudyPlan>";
        return xml;
    }



    public void reset(String name, String description, double ips, double msl, double score){
        this.name = name;
        this.description = description;

        this.ideasPerSetPercentage = (ips < 0.1 ? 0.1 : ips);
        this.minimumSessionPercentage = (msl < 0.1 ? 0.1 : msl);
        this.scorePercentage = (score < 0.2 ? 0.2 : score);



    }


    public StudyPlan(String name, String description, double ips, double msl, double score){
        this.ID = UUID.randomUUID().toString();
        reset(name,description,ips,msl,score);
    }

    public StudyPlan(String ID, String name, String description, double ips, double msl, double score){
        this.ID = ID;
        reset(name,description,ips,msl,score);
    }


    public static StudyPlan fromXML(Element element){
        String id = ((Element) element.getElementsByTagName("ID").item(0)).getTextContent().trim() ;
        String name = ((org.w3c.dom.Element) element.getElementsByTagName("Name").item(0)).getTextContent().trim() ;
        String description = ((org.w3c.dom.Element) element.getElementsByTagName("Description").item(0)).getTextContent().trim() ;
        double score =  Double.valueOf(((org.w3c.dom.Element) element.getElementsByTagName("Score").item(0)).getTextContent().trim());
        double msl =  Double.valueOf(((org.w3c.dom.Element) element.getElementsByTagName("MSL").item(0)).getTextContent().trim());
        double ips =  Double.valueOf(((org.w3c.dom.Element) element.getElementsByTagName("IPS").item(0)).getTextContent().trim());
        return new StudyPlan(id,name,description,ips,msl,score);
    }


    public double getScorePercentage(){
        return scorePercentage;
    }


    public String toString(){
        return name;
    }

}
