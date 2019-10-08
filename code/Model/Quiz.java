package Code.Model;

import javafx.collections.FXCollections;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Quiz {

    long dateInMilliseconds;

    int totalCompletionTime;

    public List<IdeaQuiz> getIdeaQuizzes() {
        return ideaQuizzes;
    }

    List<IdeaQuiz> ideaQuizzes;

    public long getDate(){
        return dateInMilliseconds;
    }

    public Quiz(long date){
        this.dateInMilliseconds = date;
        totalCompletionTime = 0;
        this.ideaQuizzes = new ArrayList<>();
    }

    public void add(IdeaQuiz ideaQuiz){
        totalCompletionTime += ideaQuiz.getTime();
        ideaQuizzes.add(ideaQuiz);
    }

    public void remove(Idea idea){
        List<IdeaQuiz> iqs = new ArrayList<>();
        for(IdeaQuiz i: ideaQuizzes){
            if(i.isIdea(idea)){
                iqs.add(i);
                totalCompletionTime -= i.getTime();
            }
        }
        ideaQuizzes.removeAll(iqs);
    }

    public static Quiz fromXML(Element quiz) throws IOException {
        String time = ((Element) quiz.getElementsByTagName("Date").item(0)).getTextContent() ;
        long date  = Long.valueOf(time);
        NodeList nodes = quiz.getElementsByTagName("IdeaQuiz");
        Quiz q = new Quiz(date);
        for(int i=0; i<nodes.getLength(); i++){
            q.add(IdeaQuiz.fromXML((Element) nodes.item(i)));
        }
        return q;
    }

    public long getTotalCompletionTime(){
        return totalCompletionTime;
    }

    public long getAverageCompletionTime(){
        if(ideaQuizzes.size()==0){
            return 0;
        }
        return totalCompletionTime/ideaQuizzes.size();
    }

    public static Comparator<IdeaQuiz> getSortByTimeComparator(){
        return new Comparator<IdeaQuiz>() {
            @Override
            public int compare(IdeaQuiz o1, IdeaQuiz o2) {
                return ((Long) o1.getTime()).compareTo(o2.getTime());
            }
        };
    }

    public static Comparator<IdeaQuiz> getSortByReadinessComparator(){
        return new Comparator<IdeaQuiz>() {
            @Override
            public int compare(IdeaQuiz o1, IdeaQuiz o2) {
                return ((Double) o1.getReadiness()).compareTo(o2.getReadiness());
            }
        };
    }

    public List<IdeaQuiz> getSortedIdeaQuizByTime(){
        List<IdeaQuiz> copy = FXCollections.observableArrayList(ideaQuizzes);
        copy.sort(getSortByTimeComparator());
        return copy;
    }

    public List<IdeaQuiz> getSortedIdeaQuizByReadiness(){
        List<IdeaQuiz> copy = FXCollections.observableArrayList(ideaQuizzes);
        copy.sort(getSortByReadinessComparator());
        return copy;
    }

    public boolean contains(Idea idea){
        for(IdeaQuiz i: ideaQuizzes){
            if(i.isIdea(idea)){
                return true;
            }
        }
        return false;
    }



    public IdeaQuiz find(Idea idea){
        for(IdeaQuiz i: ideaQuizzes){
            if(i.isIdea(idea)){
                return i;
            }
        }
        return null;
    }

    public IdeaQuiz findIdea(Idea idea, long startingDate, long endingDate){
        if(!(dateInMilliseconds >= (startingDate) && dateInMilliseconds <= endingDate)){
            return null;
        }
        IdeaQuiz ideaQuiz = find(idea);
        if(ideaQuiz!=null){
            return ideaQuiz;
        }
        return null;
    }

    public double getAverageReadiness(){
        double tot = 0;
        for(IdeaQuiz q: ideaQuizzes){
            tot += q.getReadiness();
        }
        return tot/ideaQuizzes.size();
    }

    public int getAverageConfidence(){

        if(ideaQuizzes.isEmpty()){
            return 1;
        }

        int tot = 0;
        for(IdeaQuiz q: ideaQuizzes){
            tot += q.getConfidence();
        }
        return tot/ideaQuizzes.size();
    }

    public long getTimeStudiedInMS(long startingDate, long endingDate){
        if(dateInMilliseconds >= (startingDate) && dateInMilliseconds <= endingDate){
            return totalCompletionTime;
        }
        return 0;
    }

    public List<IdeaQuiz> getAllIdeaQuiz(List<Idea> ideas){
        List<IdeaQuiz> iqs = new ArrayList<>();
        for(Idea i: ideas){
            IdeaQuiz iq = find(i);
            if(iq!=null){
                iqs.add(iq);
            }
        }
        return iqs;
    }




    public long getTimeStudiedInMS(long startingDate, long endingDate, List<Idea>ideas){
        if(dateInMilliseconds >= (startingDate) && dateInMilliseconds <= endingDate){
            List<IdeaQuiz> allIdeas = getAllIdeaQuiz(ideas);
            return Quizzes.totalTime(allIdeas);
        }
        return 0;
    }

    public int getNumberOfTimes(Idea idea, long startingDate, long endingDate){
        if(dateInMilliseconds >= (startingDate) && dateInMilliseconds <= endingDate && contains(idea)){
            return 1;
        }
        return 0;
    }

    public String toXML(){
        String xml = "<Quiz>" + System.lineSeparator() +
                        "<Date>" + dateInMilliseconds + "</Date>" + System.lineSeparator();
        for(IdeaQuiz i: ideaQuizzes){
            xml += i.toXML() + System.lineSeparator();
        }
        xml += "</Quiz>";
        return xml;
    }

}