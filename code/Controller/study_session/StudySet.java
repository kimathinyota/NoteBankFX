package Code.Controller.study_session;

import Code.Model.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StudySet {

    public StudySet(StudySession session) {

        this.session = session;
    }

    public int getNumberOfIdeasStudied(){
        return getTotalNumberOfIdeas() - session.getActivation().getRemainingIdeasList().size();
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


    public static List<Idea> getIdeas(StudySession session){

        //System.out.println("Ideas: " +  session.getName() );


        List<Idea> ideas = Study.toIdeas(session.getIdeas());
        Model model = Model.getInstance();

        //System.out.println( ideas);



        for(String subject: session.getSubjects()){


            Subject sub = model.getSubject(subject);

            //System.out.println( sub);

            if(sub!=null ){


                for(Idea i: model.getRoot().getAllIdeas()){
                    if(i.isIdeaApartOfSubject(sub)){
                        ideas.add(i);
                    }
                }


                /*
                Topic topic = model.filterTopicBySubject(sub);
                if(topic!=null){
                    ideas.addAll(topic.getAllIdeas());
                }
                */
            }
        }

        for(String topic: session.getTopics()){
            Topic top = model.getTopic(topic);
            if(top!=null){
                ideas.addAll(top.getAllIdeas());
            }

        }


        return ideas;
    }


    public List<Idea> getIdeas(){
        return Study.toIdeas(session.getActivation().getRemainingIdeasList());
    }









}
