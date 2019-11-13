package Code.Controller.study_session;

import Code.Controller.RefreshInterfaces.RefreshIdeasController;
import Code.Controller.RefreshInterfaces.RefreshSubjectsController;
import Code.Model.*;
import javafx.application.Platform;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ScheduleSessions implements RefreshSubjectsController, RefreshIdeasController {

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

        double score = model.minimumReadinessScore(StudySet.getIdeas(session));

        double percentage = plan.getScorePercentage();

        double readinessScore = percentage*model.getReadinessScore();

        return (score >= readinessScore);

    }

    private boolean removeIfDone(StudySet set){
        //check if set has been completed (and so should be removed)
        if(set.isBonus() && areAllIdeasGreaterThanReadinessScore(set.getSession())){

            set.getSession().getActivation().setRemainingIdeasList(new ArrayList<>());
            set.getSession().getActivation().setStartingIdeasNumber(0);

            listView.getItems().remove(set);
            listView.refresh();


            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    //this is a time costly method that can be run at any point
                    model.saveStudy();
                }
            });

            return true;
        }
        return false;

    }


    public void generateNextStudySet(StudySet set){
        long currentTime = System.currentTimeMillis();
        //Called everytime a user has studied every single idea in the set (completed quiz)  so we need to check if activation has expired
        if(currentTime > set.getSession().getActivation().getTimeEnd()){
            //session is done and needs to be reactivated
            //lets remove current set
            listView.getItems().remove(set);
            addToList(set.getSession());
            listView.refresh();
            return;
        }
        //set can now be regenerated

        //increment session index to represent additFion of new set
        set.getSession().getActivation().setCurrentSesionIndex(set.getSession().getActivation().getCurrentSesionIndex() + 1);


        removeIfDone(set);


        List<Idea> remainingIdeasLeftToStudy = set.getIdeas();

        int numberOfIdeasStudiedSoFar  = set.getSetNumber()*set.getSession().getActivation().getNumbersOfIdeasPerSet();
        //only accurate when !set.isBonus
        int numberOfIdeasLeftToStudy = set.getSession().getActivation().getStartingIdeasNumber() - numberOfIdeasStudiedSoFar;

        //since totalNumberOfSets is always an overestimate numberOfIdeasLeftToStudy will always be less than numberOfIdeasPerSet

        if(!set.isBonus()){
            //we can actually use numberOfIdeasLeftToStudy
            int total = (set.getSetNumber() == set.getSetTotal() - 1 ? numberOfIdeasLeftToStudy : set.getNumberOfIdeasPerSet()  );
            //it shouldn;t ever be zero but im not 100% sure
            if(total==0){
                total = 1;
                //generateNextStudySet(set);
            }

            remainingIdeasLeftToStudy.addAll(Study.getIdeasForSet(set.getSession(), total , set.getSession().getActivation().getTimeEnd() - System.currentTimeMillis()));
        }else{


            int extraIdeas = Study.determineNumberOfIdeas(set.getSession(),set.getSession().getActivation().getTimeEnd() - System.currentTimeMillis());
            System.out.println(set.getSession().getName() + " " + extraIdeas);

            if(extraIdeas>0){
                remainingIdeasLeftToStudy.addAll(Study.getIdeasForSet(set.getSession(), extraIdeas, set.getSession().getActivation().getTimeEnd() - System.currentTimeMillis()));
            }


        }


        List<String> ideas = Study.fromIdeas(remainingIdeasLeftToStudy);

        set.getSession().getActivation().setRemainingIdeasList(ideas);
        set.getSession().getActivation().setStartingIdeasNumber(ideas.size());



    }




    /*
    public void generateNextStudySet(StudySet set){

        ////System.out.println( "Generate next study set");

        StudySession session = set.getSession();
        ActivationInformation information = session.getActivation();

        //Check StudySession activation has expired
        long currentTime = System.currentTimeMillis();

        if (currentTime < information.getTimeStart() || currentTime > information.getTimeEnd() ){
            //study session activation has expired
            //we need to remove this set and replace it with a new one by reactivating the session

            StudySet nextSet = reactivate(session);
            model.saveStudy();

            studySets.remove(set);

            if(nextSet!=null) add(nextSet);

            return;

        }

        //From this point we are actually generating/refreshing the Study set


        information.setCurrentSesionIndex( set.getSetNumber() + 1);

        boolean isDone = areAllIdeasGreaterThanReadinessScore(session);
        ////System.out.println(session.getName() + " Is done: " + isDone);

        if(set.isBonus() && isDone ){
            ////System.out.println("Let's remove set " + session.getName());
            listView.getItems().remove(set);
            listView.refresh();
            model.saveStudy();
            return;
        }

        //Get missed ideas
        List<Idea> ideasNotStudied = set.getIdeas();

        List<String> ids = Study.fromIdeas(ideasNotStudied);


        int numberOfIdeasStudied = set.getSetNumber()*information.getNumbersOfIdeasPerSet();

        ////System.out.println( "Alright: " +  information.getCalculatedNumberOfIdeas() + " " +  numberOfIdeasStudied );
        
        int totalIdeasLeft = information.getCalculatedNumberOfIdeas() - numberOfIdeasStudied;
        
        List<Idea> ideas = new ArrayList<>();

        //means that we require to study less (or exact number of) ideas than the allotted number of ideas for each set
        if(totalIdeasLeft<=information.getNumbersOfIdeasPerSet()) {
            //this means we need to set get totalIdeasLeft number of Ideas
            if (information.getCurrentSesionIndex() == information.getNumberOfSessions() - 1) {
                //on final set - inline with expectations
                ideas = Study.getIdeasForSet(session, totalIdeasLeft, information.getTimeEnd() - System.currentTimeMillis());

            } else if ((information.getCurrentSesionIndex() <= information.getNumberOfSessions() - 1)) {
                //implies that user has finished earlier than anticipated - need to remove surplus sets after this one
                information.setNumberOfSessions(information.getCurrentSesionIndex() + 1);
                ideas = Study.getIdeasForSet(session, totalIdeasLeft, information.getTimeEnd() - System.currentTimeMillis());
            }
        }else{
            ideas = Study.getIdeasForSet(session,information.getNumbersOfIdeasPerSet(), information.getTimeEnd() - System.currentTimeMillis() );
        }

        if(set.isBonus()){

            int extraIdeas = Study.determineNumberOfIdeas(session,information.getTimeEnd() - System.currentTimeMillis());

            ideas = Study.getIdeasForSet(session, extraIdeas, information.getTimeEnd() - System.currentTimeMillis());

        }


        ids.addAll(Study.fromIdeas(ideas));

        information.setRemainingIdeasList( ids );

        information.setStartingIdeasNumber( ids.size() );

        model.saveStudy();


    }

*/

    private StudySet reactivate(StudySession session){

        long currentTime = System.currentTimeMillis();
        //check if session has already expired
        if(currentTime > session.getEndDate()){
            //study session has expired so needs to be removed
            model.remove(session);
            //cant generate set now
            return null;
        }

        if(currentTime < session.getStartDate()){
            //study session hasn;t started yet so we cant start it
            return null;
        }


        if(session.getActivation()==null){
            //need to activate session

            //Calculate Date from the session's final working time
            Date endWorkingDate = Study.getDate(session.getLastTime(), new Date() );

            //Need to add 24hrs to endWorkingDate if current time is ahead of it
            if(currentTime > endWorkingDate.getTime()){
                endWorkingDate = new Date(endWorkingDate.getTime() + Quizzes.d);
            }

            //if <= 24 hours before end of session, session will be activated from now until then, else just use end of working date
            Date finalDate = ( ( (session.getEndDate() - currentTime) <= Quizzes.d ) ? new Date(session.getEndDate()) : endWorkingDate);

            ActivationInformation activation = model.getActivation(session,finalDate);
            if(activation==null){
                return null;
            }
            session.reactivate(activation);

        }

        if(currentTime > session.getActivation().getTimeEnd()){
            //study session activation has expired so lets reset it
            session.reactivate(null);
            return null;
        }

        //by this point session is activated and within the activation date windows

        // we can now create a set
        StudySet set = new StudySet(session);

        if(StudySet.getIdeas(session).isEmpty()){
            //no ideas have been added yet so we cant activate this set yet

            listView.getItems().remove(set);
            return null;
        }




        boolean shouldRemove = removeIfDone(set);

        if(shouldRemove){
            return null;
        }


        if(set.getIdeas().isEmpty() && set.isBonus()){
            generateNextStudySet(set);
        }

        return set ;

    }




/*


    private StudySet reactivate(StudySession session){

        ActivationInformation activation = session.getActivation();

        long currentTime = System.currentTimeMillis();

        if(currentTime < session.getStartDate() || currentTime > session.getEndDate()){
            //cant activate or resume since study session has expired or hasn't started yet

            //System.out.println("Session is done");

            if(currentTime > session.getEndDate()){
                //need to remove StudySession from model only if session has ended
                model.remove(session);
                //System.out.println("Finally done");
            }
            return null;
        }

        if(activation!=null && (currentTime < activation.getTimeStart() || currentTime > activation.getTimeEnd()) ){
            //activation has expired so it needs to be reactivated

            System.out.println("Activation done");

            session.reactivate(null);
            model.saveStudy();

        }

        activation = session.getActivation();

        if(activation==null){
            //need to activate study session

            //System.out.println( "Activation starting" );

            //Need to schedule session up until next working date or endDate.date()
            Date endWorkingDate = Study.getDate(session.getLastTime(), new Date() );

            if(currentTime > endWorkingDate.getTime()){
                endWorkingDate = new Date(endWorkingDate.getTime() + Quizzes.d);
            }

            /*
            if <24 hours until endDate, then finalDate is endate (ingore endWorkingDate)
            else finalDate = smallerDate(endWorkingDate,endDate)

            Date finalDate;
            if( (session.getEndDate() - currentTime) <= Quizzes.d ){
                finalDate = new Date(session.getEndDate());
            }else{
                finalDate = (session.getEndDate() < endWorkingDate.getTime() ? new Date(session.getEndDate()) : new Date(endWorkingDate.getTime()));
            }

            activation = model.getActivation(session,finalDate);



            if(activation==null){
                //activation will only be null if there are no ideas for this study set yet
                return null;
            }

            session.reactivate(activation);
            model.saveStudy();

        }


        StudySet set = new StudySet(session);

        //extra ideas may have been added to topic or subject so we need to consider these:
        //set needs to be ammended: e.g. change number of ideas per each set, or change number of sets

        int numberOfIdeas = Study.determineNumberOfIdeas(session,session.getActivation().getTimeEnd() - System.currentTimeMillis());

        if(numberOfIdeas==-1){
            //need to remove set (since it has no nodes)
            //System.out.println( "-1 Remove" );
            return null;
        }

        int previousNumberOfIdeas = session.getActivation().getCalculatedNumberOfIdeas();

        if(numberOfIdeas > previousNumberOfIdeas){

            //System.out.println( "Adding more sessions " );

            int numberOfSessions = (int) Math.ceil( ((double) numberOfIdeas)/ ((double) activation.getNumbersOfIdeasPerSet()) );

            activation.setNumberOfSessions(numberOfSessions);

            activation.setCalculatedNumberOfIdeas(numberOfIdeas);


            //Let's refresh ideas (choose ideas with a lower readiness)

            int total = activation.getRemainingIdeasList().size();

            List<String> refreshedIdeas = Study.fromIdeas(Study.getIdeasForSet(session,total, activation.getTimeEnd() - System.currentTimeMillis() ));
            activation.setRemainingIdeasList(refreshedIdeas);
            model.saveStudy();

        }



        //check if bonus and finished

        if(  StudySet.getIdeas(session).isEmpty()    ){
            return null;
        }

        boolean isDone = areAllIdeasGreaterThanReadinessScore(session);

        if(set.isBonus() && isDone){
            //System.out.println( "Done man" );
            return null;
        }

        if(set.isBonus()){
            //System.out.println( "Gen new one man" );
            generateNextStudySet(set);
        }

        //System.out.println( "Return set boi" );


        return set;

    }

*/




    List<StudySet> studySets;


    private void addToList(StudySession session) {
        StudySet set = reactivate(session);
        model.saveStudy();
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

    public void removeTemporarilySession(StudySession session){
        List<StudySet> setsToRemove = new ArrayList<>();
        studySets.forEach(studySet -> {if(studySet.getSession().equals(session)) setsToRemove.add(studySet);});
        studySets.removeAll(setsToRemove);
    }


    public void studied(StudySet set, List<Idea> ideas){
        //called when quiz has been run on idea
        set.removeIdeas(ideas);

        model.saveStudy();

        listView.refresh();

        System.out.println(set.getIdeas());

        if(set.getIdeas().isEmpty()){
            generateNextStudySet(set);
        }
    }



    public List<StudySet> getAllSets(){
        return studySets;
    }



    public void initialise(ListView<StudySet> listView){
        listView.getItems().clear();
        this.listView = listView;
        studySets = listView.getItems();
        model = Model.getInstance();
        List<StudySession> sessions = model.getSessions();
        for(StudySession session: sessions){
            addToList(session);
        }
    }

    ListView<StudySet> listView;

    public ScheduleSessions(ListView<StudySet> listView){
        initialise(listView);
    }


    public void refreshIdeas(){
        initialise(listView);
    }

    public void modify(StudySession session, List<String> topics, List<String> subjects, List<String> ideas, Date first, Date end, StudyPlan plan, int hours, int mins ){
        model.modify(session,topics,subjects,ideas,first,end,plan,hours,mins);
        initialise(listView);
    }



        @Override
    public void refreshSubjects() {
       initialise(listView);
    }

}
