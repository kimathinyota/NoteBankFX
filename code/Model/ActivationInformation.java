package Code.Model;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;


public class ActivationInformation {

    public long getTimeStart() {
        return timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public int getNumberOfSessions() {
        return numberOfSessions;
    }

    public long getTimePerSession() {
        return timePerSession;
    }

    public int getCurrentSesionIndex() {
        return currentSesionIndex;
    }

    public List<String> getRemainingIdeasList() {
        return remainingIdeasList;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public void setNumberOfSessions(int numberOfSessions) {
        this.numberOfSessions = numberOfSessions;
    }

    public void setTimePerSession(long timePerSession) {
        this.timePerSession = timePerSession;
    }

    public void setCurrentSesionIndex(int currentSesionIndex) {
        this.currentSesionIndex = currentSesionIndex;
    }

    public void setRemainingIdeasList(List<String> remainingIdeasList) {
        this.remainingIdeasList = remainingIdeasList;
    }

    private long timeStart;
    private long timeEnd;
    private int numberOfSessions;
    private long timePerSession;
    private int currentSesionIndex;
    private List<String> remainingIdeasList;

    public void setStartingIdeasNumber(int startingIdeasNumber) {
        this.startingIdeasNumber = startingIdeasNumber;
    }

    int startingIdeasNumber;

    public int getStartingIdeasNumber(){
        return startingIdeasNumber;
    }

    public int getNumbersOfIdeasPerSet() {
        return numbersOfIdeasPerSet;
    }

    private int numbersOfIdeasPerSet;






    public String toXML(){
        String xml = "<ActivationInformation>" + System.lineSeparator();
        xml += "<TimeStart>" + timeStart + "</TimeStart>";
        xml += "<TimeEnd>" + timeEnd + "</TimeEnd>";
        xml += "<NumberOfSessions>" + numberOfSessions + "</NumberOfSessions>";
        xml += "<TimePerSession>" + timePerSession + "</TimePerSession>";
        xml += "<CurrentSession>" + currentSesionIndex + "</CurrentSession>" + System.lineSeparator();
        xml += "<IdeasPerSet>" + numbersOfIdeasPerSet + "</IdeasPerSet>" + System.lineSeparator();
        xml += "<Ideas>";
        xml += "<IdeasNumber>" + startingIdeasNumber + "</IdeasNumber>";
        for(String index: remainingIdeasList){
            xml += "<Idea>" + index + "</Idea>";
        }
        xml += "</Ideas>" + System.lineSeparator();
        xml += "</ActivationInformation>";
        return xml;
    }

    public static ActivationInformation fromXML(Element element){
        long timeStart =  Long.valueOf((element.getElementsByTagName("TimeStart").item(0)).getTextContent().trim());
        long timeEnd =  Long.valueOf((element.getElementsByTagName("TimeEnd").item(0)).getTextContent().trim());
        int numberOfSessions = Integer.valueOf((element.getElementsByTagName("NumberOfSessions").item(0)).getTextContent().trim());
        long timePerSession =  Long.valueOf((element.getElementsByTagName("TimePerSession").item(0)).getTextContent().trim());
        int currentSession = Integer.valueOf((element.getElementsByTagName("CurrentSession").item(0)).getTextContent().trim());
        int numbersOfIdeasPerSet = Integer.valueOf((element.getElementsByTagName("IdeasPerSet").item(0)).getTextContent().trim());
        Element ideas = (org.w3c.dom.Element) element.getElementsByTagName("Ideas").item(0);
        int ideasNumber = Integer.valueOf(ideas.getElementsByTagName("IdeasNumber").item(0).getTextContent().trim());
        NodeList nodes = ideas.getElementsByTagName("Idea");
        List<String> indexes = new ArrayList<>();
        for(int i=0; i<nodes.getLength(); i++) {
            if(nodes.item(i) instanceof Element ) {
                Element idea = (org.w3c.dom.Element) nodes.item(i);
                String id = idea.getTextContent();
                indexes.add(id);
            }
        }
        return new ActivationInformation(timeStart,timeEnd,numberOfSessions,timePerSession,currentSession,numbersOfIdeasPerSet,ideasNumber,indexes);
    }

    public ActivationInformation(long timeStart,long timeEnd,int numberOfSessions,long timePerSession,int currentSesionIndex, int numbersOfIdeasPerSet,
            int ideasNumber, List<String> remainingIdeasList){
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.numberOfSessions = numberOfSessions;
        this.timePerSession = timePerSession;
        this.currentSesionIndex = currentSesionIndex;
        this.remainingIdeasList = remainingIdeasList;
        this.numbersOfIdeasPerSet = numbersOfIdeasPerSet;
        this.startingIdeasNumber = ideasNumber;
    }









}
