package Code.Model;

import Code.Controller.Dialogs.ViewNotes.ViewNotesController;
import Code.Controller.study_session.StudySet;
import javafx.util.Pair;
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

            double frequency;
            if(upperLower.getKey().longValue()==-1){
                frequency = timeLeft/upperLower.getValue() * regionToFrequency.get(upperLower.getValue());
            }else if(upperLower.getValue().longValue()==-1){
                frequency = timeLeft/upperLower.getKey() * regionToFrequency.get(upperLower.getKey());
            }else{
                frequency =  regionToFrequency.get(upperLower.getValue()) +  (((double) timeLeft/upperLower.getKey()) * (regionToFrequency.get(upperLower.getKey())));
            }

            double tot = frequency*numberOfIdeas;

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

    private static boolean containsOnlyZeros(Collection<Integer> collection){
        for(Integer i: collection){
            if(i.intValue()!=0){
                return false;
            }
        }
        return true;
    }

    public static List<Idea> sortIdeasByLength(List<Idea> time){

        Model model = Model.getInstance();

        time.sort(new Comparator<Idea>() {
            @Override
            public int compare(Idea o1, Idea o2) {
                Long time = Quizzes.totalTime(model.getAllIdeaQuizzes(Collections.singletonList(o1)));
                Long time2 = Quizzes.totalTime(model.getAllIdeaQuizzes(Collections.singletonList(o2)));
                return time.compareTo(time2);
            }
        });

        return time;

    }


    public static List<Idea> getIdeasForSet(StudySession sp,int totalNumberOfIdeas, long timeRange){

        Model model = Model.getInstance();
        StudyPlan plan = model.findPlan(sp.getStudyPlan());
        double goalScore = model.getReadinessScore() * plan.getScorePercentage();


        HashMap<Idea,Integer> ideaToNumberNeeded = new HashMap<>();

        List<Idea> sessionIdeas = StudySet.getIdeas(sp);


        for(Idea i: sessionIdeas){
            ideaToNumberNeeded.put(i,(int) Math.ceil(model.estimateNumberOfIncrementsForIdeaToReachScore(i,goalScore,timeRange)));
        }


        List<Idea> ideas = new ArrayList<>();
        ideas.addAll(ideaToNumberNeeded.keySet());


        Comparator<Idea> comparator = new Comparator<Idea>() {
            @Override
            public int compare(Idea o1, Idea o2) {
                return ideaToNumberNeeded.get(o2).compareTo(ideaToNumberNeeded.get(o1));
            }
        };
        ideas.sort(comparator);

        List<Idea> setIdeas = new ArrayList<>();
        int current = 0;

        while(setIdeas.size() < totalNumberOfIdeas && !containsOnlyZeros(ideaToNumberNeeded.values())){

            Idea idea = ideas.get(current%ideas.size());
            if(idea!=null && ideaToNumberNeeded.get(idea)>0){
                setIdeas.add(idea);
                ideaToNumberNeeded.put(idea,ideaToNumberNeeded.get(idea)-1);
            }

            current += 1;

        }

        Collections.shuffle(ideas);

        current = 0;
        while(setIdeas.size() < totalNumberOfIdeas){
            setIdeas.add(ideas.get(current%ideas.size()));
            current += 1;
        }


        return setIdeas;


        /*
        //Get ideas meant for this set
        List<Idea> ideas = sortByReadiness(toIdeas(sp.getIdeas()));

        ideas = ideas.subList(0,( totalNumberOfIdeas > ideas.size() ? ideas.size() : totalNumberOfIdeas));

        List<Idea> extraIdeas = new ArrayList<>();
        int currentIndex = 0;

        while (totalNumberOfIdeas > (extraIdeas.size() + ideas.size())){
            extraIdeas.add( ideas.get(currentIndex%ideas.size()) );
            currentIndex +=1;
        }
        ideas.addAll(extraIdeas);
        return ideas;
        */

    }

    public static int determineNumberOfIdeas(StudySession sp, long timeRange){

        System.out.println("For " + sp.getName());
        Model model = Model.getInstance();
        StudyPlan plan = model.findPlan(sp.getStudyPlan());
        double goalScore = model.getReadinessScore() * plan.getScorePercentage();
        System.out.println("GS: " + goalScore);

        List<Idea> ideas = StudySet.getIdeas(sp);

        System.out.println( "Ideas: " + ideas + " " + ideas.size());

        if(ideas.isEmpty()){
            return -1;
        }

/*
        for(Idea i: ideas){
            double f = model.estimateNumberOfIncrementsForIdeaToReachScore(i,goalScore,timeRange);
            //System.out.println( i + " - " + f );
            freqs.add(f);
        }


        double inc = 2.5;
        List<Double> upper = freqs.subList( (int) (freqs.size() - freqs.size()/inc) , freqs.size() );
        while (upper.isEmpty()){
            inc *= 1.5;
            int low = (int) (freqs.size() - freqs.size()/inc);
            low = (low < 0 ? 0 : low);
            upper = freqs.subList( low , freqs.size() );
        }

        double frequency = upper.stream().mapToDouble(Double::doubleValue).average().getAsDouble();

        frequency = (frequency < 0.5 ? 0.5 : frequency);


        int numberOfIdeas = (int) Math.ceil(frequency*ideas.size());
        numberOfIdeas = (numberOfIdeas<1 ? 1 : numberOfIdeas  );*/

        double numberOfIdeas = 0;

        for(Idea i: ideas){
            double f = model.estimateNumberOfIncrementsForIdeaToReachScore(i,goalScore,timeRange);
            System.out.println("For: " + i + " is " + f);
            //System.out.println( i + " - " + f );
            numberOfIdeas += f;
        }

        //return (numberOfIdeas< 1 ? 1 : numberOfIdeas);

        return (int) Math.ceil(numberOfIdeas);

    }

    public ActivationInformation getActivation(Map<Long,Integer> regionToFrequency, StudySession sp, Date finishDate){
        long currentTime = System.currentTimeMillis();
        long workingTime = finishDate.getTime() - currentTime;
        //int numberOfIdeas = determineNumberOfIdeas(regionToFrequency,sp.getIdeas().size(),workingTime);

        int numberOfIdeas = determineNumberOfIdeas(sp,finishDate.getTime() - System.currentTimeMillis());

        if(numberOfIdeas<=0){
            return null;
        }



        //System.out.println(sp.getIdeas().size() + " <-> " + numberOfIdeas );



        StudyPlan plan = findStudyPlan(sp.getStudyPlan());



        //System.out.print("According to IPS " + plan.getIPS() + ": ");
        long time = Math.round(plan.getIPS() * workingTime);
        double ips = 1 + (plan.getIPS() * (numberOfIdeas - 1));
        //System.out.println(ips + " ideas every " + ViewNotesController.getOverview(time,"years","months","weeks","days", " hours"," mins"," secs", "0","",""));

       // System.out.print("According to MSL " + plan.getMSL() + ": ");
        long msl = Math.round(2*Quizzes.m + ( (long) (plan.getMSL()* ( (long) (workingTime - 2*Quizzes.m))) ) );
        double ideaNumber = plan.getMSL() * numberOfIdeas;

        //System.out.println(ideaNumber + " ideas every " + ViewNotesController.getOverview(msl,"years","months","weeks","days", " hours"," mins"," secs", "0","",""));

        double xIdeaNumber = (ideaNumber + ips)/2;
        long xTime = (msl + time)/2;


        int totalNumberOfIdeas = (int) Math.ceil(xIdeaNumber);
        long sessionTime = xTime;

        List<Idea> ideas = getIdeasForSet(sp,totalNumberOfIdeas, finishDate.getTime() - System.currentTimeMillis()  );

        //System.out.println(numberOfIdeas + " " + totalNumberOfIdeas);

        int numberOfSessions = (int) Math.ceil( ((double) workingTime)/ ((double) sessionTime) );
        //numberOfSessions = numberOfSessions +

        return new ActivationInformation(currentTime,currentTime+workingTime, numberOfSessions ,sessionTime,0,totalNumberOfIdeas,ideas.size(),fromIdeas(ideas), numberOfIdeas);

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
