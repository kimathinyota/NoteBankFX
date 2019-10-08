package Code.Model;

import javafx.util.Pair;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

public class Study {

    List<StudyPlan> studyPlans;

    List<StudySession> studySessions;

    public StudyPlan findPlan(String id){
        for(StudyPlan p: studyPlans){
            if(p.getID().equals(id)){
                return p;
            }
        }
        return null;
    }

    public List<StudyPlan> getStudyPlans(){
        return studyPlans;
    }

    public void remove(StudyPlan session){
        studyPlans.remove(session);
    }

    public void remove(StudySession session){
        studySessions.remove(session);
    }

    private Pair<Long,Long> upperLowerPair(Map<Long,Integer> regionToFrequency, long timeLeft){
        List<Long> regions = new ArrayList<>(regionToFrequency.keySet());
        Collections.sort(regions);


        //System.out.println( timeLeft );
        //System.out.println( regions );

        //System.out.println( "TimeLeft: " + ViewNotesController.getOverview(timeLeft,"years","months","weeks","days", " hours"," mins"," secs", "0","",""));


        long upper = -1;
        for(Long region: regions){
            if(region.longValue()> timeLeft){
                upper = region.longValue();
                break;
            }
        }
        //System.out.println("U: " + upper);

        Collections.reverse(regions);
        long lower = -1;
        for(Long region: regions){
            if(region.longValue() < timeLeft){
                lower = region.longValue();
                break;
            }
        }

        //System.out.println( "L: " + lower );



        //System.out.println( "Lower: " + ViewNotesController.getOverview(lower,"years","months","weeks","days", " hours"," mins"," secs", "0","",""));



        //System.out.println( "Upper: " + ViewNotesController.getOverview(upper,"years","months","weeks","days", " hours"," mins"," secs", "0","",""));






        return new Pair<>(upper,lower);
    }

    private int determineNumberOfIdeas(Map<Long,Integer> regionToFrequency,int numberOfIdeas, long timeLeft){

        if(regionToFrequency.containsKey(timeLeft)){
            //System.out.println("Found region: " + regionToFrequency.get(timeLeft));
            return regionToFrequency.get(timeLeft);
        }else{
            Pair<Long,Long> upperLower = upperLowerPair(regionToFrequency,timeLeft);
            //System.out.println("UpperLower: " + upperLower);


            //System.out.println("Freqs: " + regionToFrequency.get(upperLower.first) + " " + regionToFrequency.get(upperLower.second));


            double frequency =  regionToFrequency.get(upperLower.getValue()) +  (((double) timeLeft/upperLower.getKey()) * (regionToFrequency.get(upperLower.getKey())));

            //System.out.println(   ( (double) timeLeft/upperLower.first) + " " +  regionToFrequency.get(upperLower.first) + " " + regionToFrequency.get(upperLower.second));

            //System.out.println("Frequency: " + frequency + " Number of ideas: " + numberOfIdeas);
            double tot = frequency*numberOfIdeas;
            //System.out.println( "Total: " + tot);
            return (int) Math.round(tot);
        }
    }

    public static List<String> fromIdeas(List<Idea> ideas) {
        List<String> ids = new ArrayList<>();
        ideas.forEach((idea -> ids.add(idea.getID())));
        return ids;
    }

    public List<StudySession> getStudySessions(){
        return new ArrayList<>(studySessions);
    }

    public void add(StudySession session){
        this.studySessions.add(session);
    }

    public void add(StudyPlan plan){
        this.studyPlans.add(plan);
    }

    public static List<Idea> toIdeas(List<String> ids) {
        List<Idea> ideas = new ArrayList<>();
        Model model = Model.getInstance();
        for(String s: ids){
            Idea idea = model.getIdea(s);
            if(idea!=null){
                ideas.add(idea);
            }
        }
        return ideas;
    }

    public static List<Idea> sortByReadiness(List<Idea> ideas){
        Model model = Model.getInstance();
        ideas.sort(new Comparator<Idea>() {
            @Override
            public int compare(Idea o1, Idea o2) {
                return new Double(model.calculateReadiness(o1)).compareTo(model.calculateReadiness(o2));
            }
        });
        return ideas;
    }


    public static List<Idea> getIdeasForSet(StudySession sp,int totalNumberOfIdeas){
        //Get ideas meant for this set
        List<Idea> ideas = sortByReadiness(toIdeas(sp.getIdeas()));
        Collections.reverse(ideas);


        ideas = ideas.subList(0,( totalNumberOfIdeas > ideas.size() ? ideas.size() : totalNumberOfIdeas));

        List<Idea> extraIdeas = new ArrayList<>();
        int currentIndex = 0;

        while (totalNumberOfIdeas > (extraIdeas.size() + ideas.size())){
            extraIdeas.add( ideas.get(currentIndex%ideas.size()) );
            currentIndex +=1;
        }
        ideas.addAll(extraIdeas);
        return ideas;
    }

    public ActivationInformation getActivation(Map<Long,Integer> regionToFrequency, StudySession sp, Date finishDate){
        long currentTime = System.currentTimeMillis();
        long workingTime = finishDate.getTime() - currentTime;
        int numberOfIdeas = determineNumberOfIdeas(regionToFrequency,sp.getIdeas().size(),workingTime);

        StudyPlan plan = findStudyPlan(sp.getStudyPlan());

        double p = 0.25 + 0.75*plan.getIPS();
        numberOfIdeas = (int) Math.round(p*numberOfIdeas);

        //System.out.print("According to IPS " + plan.getIPS() + ": ");
        long time = Math.round(plan.getIPS() * workingTime);
        double ips = 1 + (plan.getIPS() * (numberOfIdeas - 1));
        //System.out.println(ips + " ideas every " + ViewNotesController.getOverview(time,"years","months","weeks","days", " hours"," mins"," secs", "0","",""));

        System.out.print("According to MSL " + plan.getMSL() + ": ");
        long msl = Math.round(2*Quizzes.m + ( (long) (plan.getMSL()* ( (long) (workingTime - 2*Quizzes.m))) ) );
        double ideaNumber = plan.getMSL() * numberOfIdeas;

        //System.out.println(ideaNumber + " ideas every " + ViewNotesController.getOverview(msl,"years","months","weeks","days", " hours"," mins"," secs", "0","",""));



        double xIdeaNumber = (ideaNumber + ips)/2;
        long xTime = (msl + time)/2;

        //System.out.println("Combined: " + xIdeaNumber + " ideas every " + ViewNotesController.getOverview(xTime,"years","months","weeks","days", " hours"," mins"," secs", "0","",""));


        int totalNumberOfIdeas = (int) Math.round(xIdeaNumber);
        long sessionTime = xTime;

        List<Idea> ideas = getIdeasForSet(sp,totalNumberOfIdeas);

        return new ActivationInformation(currentTime,currentTime+workingTime,numberOfIdeas/totalNumberOfIdeas,sessionTime,0,totalNumberOfIdeas,ideas.size(),fromIdeas(ideas));


    }

    public static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static Date getDate(LocalTime time, Date date){
        Instant instant = time.atDate(convertToLocalDateViaInstant(date)).
                atZone(ZoneId.systemDefault()).toInstant();
        Date d = Date.from(instant);
        return d;
    }

    public String toXML() {
        String xml = "<Study>" + System.lineSeparator();
        for(StudyPlan studyPlan: studyPlans){
            xml += studyPlan.toXML() + System.lineSeparator();
        }
        for(StudySession studySession: studySessions){
            xml += studySession.toXML() + System.lineSeparator();
        }
        xml += "</Study>";
        return xml;
    }

    public Study(List<StudyPlan> studyPlans, List<StudySession> studySessions){
        this.studyPlans = studyPlans;
        this.studySessions = studySessions;
    }

    public static Study fromXML(Element element){
        NodeList studyPlans = element.getElementsByTagName("StudyPlan");
        List<StudyPlan> plans = new ArrayList<>();
        for(int i=0; i<studyPlans.getLength(); i++) {
            plans.add( StudyPlan.fromXML((Element) studyPlans.item(i)) );
        }
        NodeList studySessions = element.getElementsByTagName("StudySession");
        List<StudySession> sessions = new ArrayList<>();
        for(int i=0; i<studySessions.getLength(); i++) {
            sessions.add(  StudySession.fromXML((Element) studySessions.item(i)) );
        }

        return new Study(plans,sessions);
    }

    public static Study fromXML(String loc) throws ParserConfigurationException, FileNotFoundException, SAXException, IOException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document document = builder.parse( new FileInputStream(loc) );
        Element rootElement = document.getDocumentElement();
        return fromXML(rootElement);
    }

    public StudyPlan findStudyPlan(String studyPlanID){
        for(StudyPlan plan: studyPlans){
            if(plan.getID().equals(studyPlanID)){
                return plan;
            }
        }
        return null;
    }


    private StudyPlan findStudyPlanByName(String name){
        for(StudyPlan plan: studyPlans){
            if(plan.getName().equals(name)){
                return plan;
            }
        }
        return null;
    }

    public Study(){
        this.studyPlans = new ArrayList<>();
        this.studySessions = new ArrayList<>();




    }






}
