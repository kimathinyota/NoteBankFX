package Code.Controller.study_session;

import Code.Model.Idea;
import Code.Model.Model;
import Code.Model.Study;
import Code.Model.StudySession;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StudySet {

    public StudySet(StudySession session) {

        this.session = session;
    }

    public int getNumberOfIdeasStudied(){
        return session.getActivation().getStartingIdeasNumber() - session.getActivation().getRemainingIdeasList().size();
    }

    public int getTotalNumberOfIdeas(){
        return session.getActivation().getStartingIdeasNumber();
    }

    public int getNumberOfIdeasPerSet(){
        return session.getActivation().getNumbersOfIdeasPerSet();
    }

    public List<String> getRemainingIdeas() {
        return session.getActivation().getRemainingIdeasList();
    }



    public long getTimeToKill() {
        long ttk =  session.getActivation().getTimePerSession();
        //System.out.println("TTK: " + ViewNotesController.getOverview(ttk,"years","months","weeks","days", " hours"," mins"," secs", "0","",""));


        long endTime = session.getActivation().getTimeStart();

        if(getSetNumber() < getSetTotal()){
            endTime += (getSetNumber()+1) * ttk;
        }else{
            endTime = session.getActivation().getTimeEnd();
        }

        return (endTime - System.currentTimeMillis());
    }


    public boolean isBonus() {
        return getSetNumber()>=getSetTotal();
    }

    public int getSetNumber() {
        return session.getActivation().getCurrentSesionIndex();
    }

    public int getSetTotal() {
        return session.getActivation().getNumberOfSessions();
    }

    public String getName(){
        return session.getName();
    }

    public void removeIdeas(List<Idea> ideas){
        List<String> ids = Study.fromIdeas(ideas);

        //Remove only the first occurence of each id in ids list
        ids.forEach( s -> {if(getRemainingIdeas().indexOf(s)>=0) getRemainingIdeas().remove(getRemainingIdeas().indexOf(s)); });

    }





    public StudySession getSession() {
        return session;
    }

    StudySession session;


    public List<Idea> getIdeas(){
        return Study.toIdeas(getRemainingIdeas());
    }









}
