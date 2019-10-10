package Code.Model;

import Code.Controller.Dialogs.ViewNotes.ViewNotesController;
import javafx.collections.FXCollections;
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
import java.util.*;

public class Quizzes {

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

    List<Quiz> quizzes;

    public static long s = (1000L);
    public static long m = (60L * (1000L));
    public static long h = (60L * (60L * (1000L)));
    public static long d = (24L * (60L * (60L * (1000L))));
    public static long w = (7L * (24L * (60L * (60L * (1000L)))));
    public static long mth = (30L * (24L * (60L * (60L * (1000L)))));
    public static long y = (365L * (24L * (60L * (60L * (1000L)))));

    double readinessScore = 10;
    double averageConfidnce = 3;
    double timeFreq[] = {3,5,9,18,32,65,100};
    double scoreIncrement = 0.25;

    private static List<Quiz> fromXML(Element quizzes) throws IOException{
        NodeList nodes = quizzes.getElementsByTagName("Quiz");
        List<Quiz> quizList = new ArrayList<>();
        for(int i=0; i<nodes.getLength(); i++) {
            if(nodes.item(i) instanceof Element) {
                Element elem = (Element) nodes.item(i);
                quizList.add(Quiz.fromXML(elem));
            }
        }
        return quizList;
    }

    public static Quizzes fromXML(String loc) throws ParserConfigurationException, FileNotFoundException, SAXException, IOException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document document = builder.parse( new FileInputStream(loc) );
        Element rootElement = document.getDocumentElement();
        return new Quizzes(fromXML(rootElement));
    }

    public String toXML(){
        String xml = "<Quizzes>" + System.lineSeparator();
        for(Quiz q: quizzes){
            xml += q.toXML() + System.lineSeparator();
        }
        xml += "</Quizzes>";
        return xml;
    }






    public List<IdeaQuiz> getIdeaQuizzesBetweenDates(Idea idea, long startingDate, long endingDate ){
        List<IdeaQuiz> is = new ArrayList<>();
        for(Quiz q: quizzes){
            IdeaQuiz i = q.findIdea(idea,startingDate,endingDate);
            if(i!=null){
                is.add(i);
            }
        }
        return is;
    }

    public List<IdeaQuiz> getIdeaQuizzesInLastX(Idea idea, long startingDate, long timeInMs ){
        return getIdeaQuizzesBetweenDates(idea,startingDate-timeInMs,startingDate);
    }

    public int getNumberOfTimesInLastX(Idea idea, long startingDate, long timeInMs){

        return getIdeaQuizzesInLastX(idea,startingDate,timeInMs).size();

    }

    public int getNumberOfTimes(Idea idea, long startingDate, long endingDate){

       return getIdeaQuizzesBetweenDates(idea,startingDate,endingDate).size();

    }








    public List<IdeaQuiz> getAllIdeaQuizzes(Idea idea){
        List<IdeaQuiz> list = new ArrayList<>();
        for(Quiz q: quizzes){
            IdeaQuiz i = q.find(idea);
            if(i!=null){
                list.add(i);
            }
        }
        return list;
    }

    public long getTimeStudiedInMS(long startingDate, long endingDate){
        long time = 0;
        for(Quiz q: quizzes){
            time += q.getTimeStudiedInMS(startingDate,endingDate);
        }
        return time;
    }

    public long getTimeStudiedInMS(long startingDate, long endingDate, List<Idea> ideas){
        long time = 0;
        for(Quiz q: quizzes){
            time += q.getTimeStudiedInMS(startingDate,endingDate,ideas);
        }
        return time;
    }

    public HashMap<String,List<IdeaQuiz>> getFrequencyPerTime(Idea idea, long startingTime){


        HashMap<String,List<IdeaQuiz>> map = new HashMap<>();

        map.put("hour",getIdeaQuizzesInLastX(idea,startingTime,h) );
        map.put("day",getIdeaQuizzesInLastX(idea,startingTime,d));
        map.put("week",getIdeaQuizzesInLastX(idea,startingTime,w));
        map.put("fortnight",getIdeaQuizzesInLastX(idea,startingTime,2*w));
        map.put("month",getIdeaQuizzesInLastX(idea,startingTime,mth));
        map.put("quarter",getIdeaQuizzesInLastX(idea,startingTime,3*m));
        map.put("half-year",getIdeaQuizzesInLastX(idea,startingTime,6*m));
        map.put("year",getIdeaQuizzesInLastX(idea,startingTime,y));
        map.put("all",getAllIdeaQuizzes(idea));

        return map;
    }

    public static double averageConfidence(List<IdeaQuiz>list){
        double total = 0;
        for(IdeaQuiz i: list){
            total += i.getConfidence();
        }
        return total/list.size();
    }


    public double averageReadiness(List<Idea> ideas){
        double readiness = 0;
        for(Idea i: ideas){
            readiness += getReadiness(i,System.currentTimeMillis(),null);
        }
        return readiness/ideas.size();
    }

    public static double totalConfidence(List<IdeaQuiz>list){
        double total = 0;
        for(IdeaQuiz i: list){
            total += i.getConfidence();
        }
        return total;
    }

    public double minimumReadinessScore(List<Idea> ideas){
        Double max = new Double(1000000);
        Double readiness = max;
        for(Idea i: ideas){
            double r = getReadiness(i,System.currentTimeMillis(),null);
            if(r < readiness){
                readiness = r;
            }
        }

        if(readiness.equals(max)){
            return 0;
        }

        return readiness;
    }

    public static long totalTime(List<IdeaQuiz>list){
        long total = 0;
        for(IdeaQuiz i: list){
            total += i.getTime();
        }
        return total;
    }

    public List<IdeaQuiz> getAllIdeaQuizzes(List<Idea>ideas){
        List<IdeaQuiz> ideaQuizzes = new ArrayList<>();
        for(Idea i: ideas){
            ideaQuizzes.addAll(getAllIdeaQuizzes(i));
        }
        return ideaQuizzes;
    }

    public static String totalTime(List<IdeaQuiz>list, String years, String months, String weeks, String days, String hours, String minutes, String seconds){
        return ViewNotesController.getOverview(totalTime(list),years,months,weeks,days,hours,minutes,seconds,"0","","");
    }


    public double getScoreIncrement(){
        return scoreIncrement;
    }

    public double getReadinessScore(){
        return readinessScore;
    }

    public int getReadinessType(double readinessScore){
        double score = this.readinessScore;
        if(readinessScore>=score){
            return 5;
        }
        if(readinessScore>=3*scoreIncrement*score){
            return 4;
        }
        if(readinessScore>=2*scoreIncrement*score){
            return 3;
        }

        if(readinessScore>=scoreIncrement*score){
            return 2;
        }

        return 1;

    }


    public int getReadinessType(Idea idea, long currentTime){
        return getReadinessType(getReadiness(idea,currentTime,null));
    }

    public static List<IdeaQuiz> subtract(List<IdeaQuiz>a, List<IdeaQuiz>b){
        List<IdeaQuiz> c = new ArrayList<>();
        c.addAll(a);
        c.removeAll(b);
        return c;
    }

    public double getReadiness(Idea idea, long currentTime, Integer confidence){

        HashMap<String,List<IdeaQuiz>> map = getFrequencyPerTime(idea,currentTime);


        // Relationship:
        /**
         * More time spent before reviewing, the less ready you are for that particular idea
         * Readiness vs time
         * Each page of information requires: 15 min of review to be ready
         * K = Each idea takes: (forEach N in Idea: N.noPages * 15) minutes of review to be ready
         * An idea is ready if you've either :
         *   -> Spent at least K minutes on that particular Idea
         *   -> A user has studied this idea and given an average confidence >= 3:
         *           -> 3 times in last hour (f = 9/(3*3))
         *           -> 5 times in last day or (f = 9/(5*3))
         *           -> 9 times in last week or (f = 9/(9*3))
         *           -> 18 times in last month or (f = 9/(18*3))
         *           -> 32 times in last quarter or (f = 9/(32*3))
         *           -> 65 times in last half year or (f = 9/(65*3))
         *           -> 100 time in last year (f = 9/(100*3))
         *
         * Formula:
         * totConf(x) = sum of conf for Idea in last hour
         *
         * ConfidenceScore = totConf(hour)*9/(3*3) + totConf(day)*9/(5*3) + totConf(week)*9/(9*3) + totConf(month)*9/(18*3)
         *  + totConf(quarter)*9/(32*3) + totConf(half-year)*9/(65*3) + totConf(year)*9/(100*3)
         *
         */

        HashMap<String,List<IdeaQuiz>> quizPerTime = getFrequencyPerTime(idea,currentTime);


        double confidenceScore = (totalConfidence(quizPerTime.get("hour"))*readinessScore)/(averageConfidnce*frequency(h)) +
                                 (totalConfidence(subtract(quizPerTime.get("day"),quizPerTime.get("hour")))*readinessScore)/(averageConfidnce*frequency(d)) +
                                 (totalConfidence(subtract(quizPerTime.get("week"),quizPerTime.get("day")))*readinessScore)/(averageConfidnce*frequency(w)) +
                                 (totalConfidence(subtract(quizPerTime.get("month"),quizPerTime.get("week")))*readinessScore)/(averageConfidnce*frequency(mth)) +
                                 (totalConfidence(subtract(quizPerTime.get("quarter"),quizPerTime.get("month")))*readinessScore)/(averageConfidnce*frequency(3*mth)) +
                                 (totalConfidence(subtract(quizPerTime.get("half-year"),quizPerTime.get("quarter")))*readinessScore)/(averageConfidnce*frequency(6*mth)) +
                                 (totalConfidence(subtract(quizPerTime.get("year"),quizPerTime.get("half-year")))*readinessScore)/(averageConfidnce*frequency(y));

        if(confidence==null){
            IdeaQuiz lastIdea = getLastIdeaQuiz(idea);
            if(lastIdea!=null){
                confidence = lastIdea.getConfidence();
            }else{
                confidence = 0;
            }
        }

        return (3*confidence + 7*confidenceScore)/10;
    }



    public double getScoreIncrement(long timeLeft){
        List<Long> regions = FXCollections.observableArrayList(y,6*mth,3*mth,mth,w,d,h);

        long reg = y;

        for(int i=0; i<regions.size()-1; i++){
            long r = regions.get(i);
            if(timeLeft < r){
                reg = regions.get(i+1);
            }
        }

        return 2.5*readinessScore/(averageConfidnce*frequency(reg));

    }



    public double estimateNumberOfIncrementsForIdeaToReachScore(Idea idea, double goalScore, long timeRange){

        double currentScore = getReadiness(idea,System.currentTimeMillis(),null);
        //System.out.println("Score: " + currentScore + " Goal: " + goalScore);

        if(currentScore>=goalScore){
            return 0;
        }

        double increment = getScoreIncrement(timeRange);

       // System.out.println("Increment: " + increment);

        return (goalScore-currentScore)/increment;
    }





    private Integer frequency(long region){
        Integer f = this.regionToFrequency.get(region);
        return (f!=null ? f : 1);
    }

    public void  add(Quiz quiz){
        this.quizzes.add(quiz);
    }

    public void remove(Quiz quiz){
        this.quizzes.remove(quiz);
    }

    public IdeaQuiz getLastIdeaQuiz(Idea idea){
        quizzes.sort(new Comparator<Quiz>() {
            @Override
            public int compare(Quiz o1, Quiz o2) {
                return new Long(o1.getDate()).compareTo(o2.getDate());
            }
        });
        List<IdeaQuiz> qs = getAllIdeaQuizzes(idea);
        if(qs.isEmpty()){
            return null;
        }

        return qs.get(qs.size()-1);
    }

    public void remove(Idea idea){
        for(Quiz q: quizzes){
            q.remove(idea);
        }
    }

    Map<Long,Integer> regionToFrequency;

    public void setRegionToFrequency(Map<Long,Integer> regionToFrequency){
        this.regionToFrequency = regionToFrequency;
    }

    public Map<Long,Integer> getRegionToFrequency(){
        return regionToFrequency;
    }

    private void setDefault(){

        regionToFrequency = new HashMap<>();
        regionToFrequency.put(Quizzes.h,1);
        regionToFrequency.put(Quizzes.d,3);
        regionToFrequency.put(Quizzes.w,7);
        regionToFrequency.put(2*Quizzes.w,10);
        regionToFrequency.put(Quizzes.mth,17);
        regionToFrequency.put(3*Quizzes.mth,27);
        regionToFrequency.put(6*Quizzes.mth,50);
        regionToFrequency.put(Quizzes.y,80);

    }

    public Quizzes(String loc) throws Exception{
        this.quizzes = new ArrayList<>();
        this.quizzes.addAll(fromXML(loc).getQuizzes());
        setDefault();

    }

    public Quizzes(){
        this.quizzes = new ArrayList<>();
        setDefault();
    }

    public Quizzes(List<Quiz> quizzes){
        this.quizzes = quizzes;
        setDefault();
    }

}
