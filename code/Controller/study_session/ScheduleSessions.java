package Code.Controller.study_session;

import Code.Model.*;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ScheduleSessions {

    Model model;


    public List<Idea> getQuizIdeas(StudySet studySet){
        List<Idea> ideas = studySet.getIdeas();
        List<Idea> allIdeas = new ArrayList<>();
        ideas = Study.sortByReadiness(ideas);
        ideas.forEach(idea -> {if(!allIdeas.contains(idea) ) allIdeas.add(idea);});
        return allIdeas;
    }


    private void add(StudySet set){
        studySets.add(set);
        studySets.sort(new Comparator<StudySet>() {
            @Override
            public int compare(StudySet o1, StudySet o2) {
                return new Long(o1.getTimeToKill()).compareTo(o2.getTimeToKill());
            }
        });
    }


    private boolean areAllIdeasGreaterThanReadinessScore(StudySession session){

        StudyPlan plan = model.findPlan(session.getStudyPlan());

        double score = model.minimumReadinessScore(Study.toIdeas(session.getIdeas()));

        double percentage = plan.getScorePercentage();

        double readinessScore = percentage*model.getReadinessScore();

        return (score >= readinessScore);
    }

    public void generateNextStudySet(StudySet set){

        //System.out.println( "Generate next study set");

        StudySession session = set.getSession();
        ActivationInformation information = session.getActivation();

        //Check StudySession activation has expired
        long currentTime = System.currentTimeMillis();

        if (currentTime < information.getTimeStart() || currentTime > information.getTimeEnd() || (set.getIdeas().isEmpty() && set.isBonus())){
            //study session activation has expired
            //we need to remove this set and replace it with a new one by reactivating the session

            StudySet nextSet = reactivate(session);

            studySets.remove(set);

            if(nextSet!=null) add(nextSet);

            return;


        }

        //From this point we are actually generating/refreshing the Study set

        //Get missed ideas
        List<Idea> ideasNotStudied = set.getIdeas();
        List<String> ids = Study.fromIdeas(ideasNotStudied);


        List<Idea> ideas = Study.getIdeasForSet(session,information.getNumbersOfIdeasPerSet());

        ids.addAll(Study.fromIdeas(ideas));

        information.setRemainingIdeasList(  ids );
        information.setCurrentSesionIndex( set.getSetNumber() + 1);
        information.setStartingIdeasNumber( ids.size() );



        listView.refresh();

    }




    private StudySet reactivate(StudySession session){

        ActivationInformation activation = session.getActivation();

        long currentTime = System.currentTimeMillis();



        if(currentTime < session.getStartDate() || currentTime > session.getEndDate()){
            //cant activate or resume since study session has expired or hasn't started yet

            if(currentTime > session.getEndDate()){
                //need to remove StudySession from model only if session has ended
                model.remove(session);
            }
            return null;
        }


        if(activation!=null && (currentTime < activation.getTimeStart() || currentTime > activation.getTimeEnd()) ){
            //activation has expired so it needs to be reactivated
            session.reactivate(null);
        }

        activation = session.getActivation();

        if(activation==null){
            //need to activate study session

            //Need to schedule session up until next working date or endDate
            Date endWorkingDate = Study.getDate(session.getLastTime(),new Date());
            if(currentTime > endWorkingDate.getTime()){
                endWorkingDate = new Date(endWorkingDate.getTime() + Quizzes.d);
            }
            /*
            if <24 hours until endDate, then finalDate is endate (ingore endWorkingDate)
            else finalDate = smallerDate(endWorkingDate,endDate)
             */
            Date finalDate;
            if( (session.getEndDate() - currentTime) <= Quizzes.d ){
                finalDate = new Date(session.getEndDate());
            }else{
                finalDate = (session.getEndDate() < endWorkingDate.getTime() ? new Date(session.getEndDate()) : new Date(endWorkingDate.getTime()));
            }


            activation = model.getActivation(session,finalDate);

            session.reactivate(activation);

        }



        return new StudySet(session);

    }


    List<StudySet> studySets;


    private void addToList(StudySession session) {
        StudySet set = reactivate(session);
        if (set != null) {
            add(set);
        }
    }

    public void addSession(StudySession session){
        addToList(session);
        model.add(session);
    }


    public void removeSession(StudySession session){
        model.remove(session);
        List<StudySet> setsToRemove = new ArrayList<>();
        studySets.forEach(studySet -> {if(studySet.getSession().equals(session)) setsToRemove.add(studySet);});
        studySets.removeAll(setsToRemove);
    }


    public void studied(StudySet set, List<Idea> ideas){
        //called when quiz has been run on idea
        set.removeIdeas(ideas);
        if(set.getIdeas().isEmpty()){
            generateNextStudySet(set);
        }
        listView.refresh();
        model.saveStudy();
    }



    public List<StudySet> getAllSets(){
        return studySets;
    }



    public void initialise(){
        model = Model.getInstance();
        List<StudySession> sessions = model.getSessions();
        for(StudySession session: sessions){
            addToList(session);
        }
    }

    ListView<StudySet> listView;

    public ScheduleSessions(ListView<StudySet> listView){
        this.listView = listView;
        studySets = listView.getItems();
        initialise();

    }



}
