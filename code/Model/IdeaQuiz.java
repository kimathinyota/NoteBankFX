package Code.Model;

import org.w3c.dom.Element;

import java.io.IOException;

public class IdeaQuiz{

    public String getId() {
        return id;
    }

    public Integer getConfidence() {
        return confidence;
    }

    public Double getReadiness() {
        return readiness;
    }

    public Long getTime() {
        return time;
    }

    String id;
    Integer confidence;
    Double readiness;
    Long time;

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public void setReadiness(double readiness) {
        this.readiness = readiness;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isIdea(Idea idea){
        return idea.getID().equals(id);
    }

    public IdeaQuiz(Idea idea){
        this.id = idea.getID();
        this.confidence = 1;
        this.time = null;
        this.readiness = null;
    }



    private boolean isTimeSet(){
        return time>-1;
    }

    public IdeaQuiz(Idea idea, double readiness){
        this.id = idea.getID();
        this.confidence = 1;
        this.readiness = readiness;
        this.time = null;
    }


    public IdeaQuiz(String id, int confidence, double readiness, long time){
        this.id = id;
        this.confidence = confidence;
        this.readiness = readiness;
        this.time = time;
    }

    public static IdeaQuiz fromXML(Element subject) throws IOException {
        String id = ((Element) subject.getElementsByTagName("ID").item(0)).getTextContent();
        String confidenceString = ((Element) subject.getElementsByTagName("Confidence").item(0)).getTextContent() ;
        String readinessString = ((Element) subject.getElementsByTagName("Readiness").item(0)).getTextContent() ;
        String timeString = ((Element) subject.getElementsByTagName("Time").item(0)).getTextContent() ;
        int confidence = Integer.valueOf(confidenceString).intValue();
        double readiness = Double.valueOf(readinessString);
        long time = Long.valueOf(timeString);

        IdeaQuiz ideaQuiz = new IdeaQuiz(id,confidence,readiness,time);

        return ideaQuiz;

    }

    public String toXML(){
        String xml = "<IdeaQuiz>" + System.lineSeparator() +
                "<ID>" + id + "</ID>" + System.lineSeparator() +
                "<Confidence>" + confidence + "</Confidence>" + System.lineSeparator() +
                "<Readiness>" + readiness + "</Readiness>" + System.lineSeparator() +
                "<Time>" + time + "</Time>" + System.lineSeparator() +
                "</IdeaQuiz>";
        return xml;
    }



}
