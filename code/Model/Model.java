package Code.Model;

import Code.Controller.RefreshInterfaces.*;
import Code.Controller.home.notes.filters.FilterSettings;

import javafx.application.Platform;
import javafx.collections.FXCollections;

import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;


public class Model {

    private List<RefreshNotesController> refreshNotesControllers;
    private List<RefreshIdeasController> refreshIdeasControllers;
    private List<RefreshSubjectsController> refreshSubjectsControllers;
    private List<RefreshDataController> refreshDataControllers;
    private List<RefreshStudyController> refreshStudyControllers;

    private Subject currentSubject;
    private String quizDirectory;
    private Study study;
    private List<Subject> subjects;
    private String notesDirectory,rootDirectory,subjectDirectory;

    HashMap<String,Idea> ideas;
    HashMap<String,Topic> topics;

    public void addRefreshNotesController(RefreshNotesController controller){
        refreshNotesControllers.add(controller);
    }

    public void addRefreshIdeasController(RefreshIdeasController controller){
        refreshIdeasControllers.add(controller);
    }

    public void addRefreshSubjectsController(RefreshSubjectsController controller){
        refreshSubjectsControllers.add(controller);
    }

    public void addRefreshDataController(RefreshDataController controller){
        refreshDataControllers.add(controller);
    }

    public void addRefreshStudyController(RefreshStudyController controller){
        refreshStudyControllers.add(controller);
    }

    public void refreshNotes(){
        for(RefreshNotesController c: refreshNotesControllers){
            c.refreshNotes();
        }
    }

    public void refreshData(){
        for(RefreshDataController c: refreshDataControllers){
            c.refreshData();
        }
    }

    public void refreshIdeas(){
        for(RefreshIdeasController c: refreshIdeasControllers){
            c.refreshIdeas();
        }
        saveIdea();
    }

    public void refreshSubjects(){
        for(RefreshSubjectsController c: refreshSubjectsControllers){
            c.refreshSubjects();

        }
        saveSubject();
    }

    public void modify(StudySession session, List<String> topics, List<String> subjects, List<String> ideas, Date first, Date end, StudyPlan plan, int hours, int mins ){

        session.setSubjects(subjects);
        session.setTopics(topics);
        session.setIdeas(ideas);
        session.setStartDate(first);
        session.setEndDate(end);
        session.setStudyPlan(plan);
        session.setFinalHours(hours);
        session.setFinalMins(mins);
        session.reactivate(null);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshStudy();
            }
        });


    }

    public void refreshStudy(){

        for(RefreshStudyController c: refreshStudyControllers){
            c.refreshStudy();
        }

        saveStudy();
    }

    /*
        @TODO: Consider why you have chosen to go for a look-up approach (less memory, more CPU) instead of referencing
            Advantages for look-up:
                Less memory usage at a time (almost essential considering i'm using java)
                Implements MVC: Better separation between model and view
            Disadvantages:
                More CPU usage:
            Conclusion: Still not sure which method to use - i will use look-up approach for now and change approach if problems arise after thorough testing of first prototype

        @TODO: Test remove methods
     */

    /**
     * For concurrent access of Model data amongst the different controller threads
     */
    private final static Model instance = new Model();

    /**
     *
     * @return instance of model
     */
    public static Model getInstance(){
        return instance;
    }

    /**
     * Will find all notes in a specific directory and return as a list of notes
     * @param notesDirectory: directory
     * @return
     */
    private List<Note> getAllNotes(String notesDirectory) {
        List<Note> notes = new ArrayList<Note>();
        try {
            File directory = new File(notesDirectory);
            String path ;
            for(File file: directory.listFiles()) {
                path = file.getPath();
                if (!file.isDirectory()) {
                    Note note = fromPath(path);
                    if(note!=null){
                        notes.add(note);
                    }
                }
            }
        }catch (IOException e){
        }
        return notes;
    }

    public List<Note> getAllNotes(){
        return getNotes("All");
    }

    /**
     * Find and returns a list of subjects from the subjectDirectory
     * subjectDirectory: contains Subjects.xml file
     * @return
     */
    private List<Subject> getSubjects() throws ParserConfigurationException, FileNotFoundException, SAXException, IOException {
        return Subject.fromXML(subjectDirectory + File.separator + "Subjects.xml");
    }

    /**
     * Converts a full path into a Note
     * @param path
     * @return
     * @throws IOException
     */
    public static Note fromPath(String path) throws IOException{

        File file = new File(path);
        if(!file.exists()){
            return null;
        }

        if (ImageIO.read(file) != null) {
            return new Image(path);
        } else if (file.getPath().contains(".pdf")) {
            Book book = new Book(path);
            return book;
        } else if (file.getPath().contains(".txt")) {
            return new Text(path);
        }

        return null;

    }

    /**
     * Gets a list of note paths for a given subject
     * @param subject
     * @return
     */
    public List<String> getNotesPaths(String subject){

        for(Subject sub: subjects){
            if(sub.getName().equals(subject))
                return sub.getNotePaths();
        }

        return new ArrayList<>();

    }

    /**
     * Gets a list of note paths for a given subject
     * @param subject
     * @return
     */
    public List<Note> getNotes(String subject){
        List<Note> notes = new ArrayList<>();
        for(String path: getNotesPaths(subject)){
            try{
                Note found = fromPath(path);

                if(found!=null){
                    notes.add(found);
                }


            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return notes;
    }

    String recoveryPath;

    private File addToRecoveryFolder(String pathToFileDirectory, String originalFileName, String newFileName, String fileExtension, String folder){
        File file = new File(recoveryPath);
        if(!file.exists()){
            file.mkdir();
        }

        File found = new File(pathToFileDirectory + File.separator + originalFileName + fileExtension);
        if(found.exists()){

            String dest = recoveryPath + File.separator + folder + File.separator + newFileName + fileExtension;

            File destination = new File(dest);

            try{
                Files.copy(found.toPath(),destination.toPath(),StandardCopyOption.REPLACE_EXISTING);
                return new File(dest);
            }catch (IOException e){
                e.printStackTrace();

            }
        }

        return null;
    }

    List<String> folders;

    String studyDirectory;

    public void backup(){

        String date = new SimpleDateFormat("HH-mm-ss dd-MM-yyyy").format(new Date());


        File folder = new File(recoveryPath + File.separator + date);
        if(!folder.exists()){
            folder.mkdir();
        }

        folders.add(folder.getPath());

        File f = addToRecoveryFolder(rootDirectory,"Topics","Topics", ".xml",date);
        File f2 = addToRecoveryFolder(subjectDirectory,"Subjects","Subjects",".xml", date);
        File f3 = addToRecoveryFolder(quizDirectory,"Quizzes","Quizzes",".xml", date);
        File f4 = addToRecoveryFolder(studyDirectory,"Study","Study",".xml", date);

        trimRecoveryFolder();

    }

    private void trimRecoveryFolder(){
        while (this.folders.size()>maxNumberOfBackupFolders) {
            String path = folders.get(0);
            File file = new File(path);
            delete(file);
            folders.remove(path);
        }
    }

    private void delete(File folder){
        if(!folder.isDirectory()){
            return;
        }
        for(File f: folder.listFiles()){
            f.delete();
        }

        folder.delete();
    }

    /**
     * Gets root Topic in Topics.xml found in input rootDirectory
     * @param rootDirectory
     * @return
     */
    private Topic getRoot(String rootDirectory){
        try{
            return Topic.fromXML(rootDirectory + File.separator + "Topics.xml");
        }catch (Exception e){
            e.printStackTrace();

            if(new File(rootDirectory + File.separator + "Topics.xml").exists()){
                String date = new SimpleDateFormat("HH-mm-dd-MM-yyyy").format(new Date());
                addToRecoveryFolder(rootDirectory,"Topics", "Topics-"+date,".xml", "corrupted");
            }


            Topic allTopics = new Topic("All Topics");
            try{
                saveXML(allTopics.toXML(), rootDirectory , "Topics");
                return allTopics;
            }catch (IOException e2){
            }

            return null;
        }
    }

    /**
     * Adds new book to noteDirectory
     * @param name
     * @param originalFilePath
     */
    public Book addBook(String name, String originalFilePath){
        try{
            Book b = new Book(name,originalFilePath,notesDirectory);
            this.getSubject("All").add(b.getPath().toString());
            if(!isRootSubject()){
                currentSubject.add(b.getPath().toString());
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    refreshNotes();
                }
            });
            return b;
        }catch (IOException e){

        }
        return null;
    }

    /**
     * Adds new Image to notesDirectory
     * @param name
     * @param originalPath
     */
    public void addImage(String name, String originalPath){
        try{
            Image i = new Image(name,originalPath,notesDirectory);
            this.getSubject("All").add(i.getPath().toString());
            if(!isRootSubject()){
                currentSubject.add(i.getPath().toString());
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    refreshNotes();
                }
            });
        }catch (IOException e){

        }
    }

    /**
     * Adds new Text to notesDirectory
     * @param name
     * @param textContent
     */
    public Text addText(String name, String textContent){
        try{
            Text t = new Text(name,textContent,notesDirectory);
            this.getSubject("All").add(t.getPath().toString());
            if(!isRootSubject()){
                currentSubject.add(t.getPath().toString());
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    refreshNotes();
                }
            });
            return t;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public Text setToFinalNote(String evaluatedContent, Idea idea){

        String date = new SimpleDateFormat("HH_mm_ss dd_MM_yyyy").format(new Date());
        Text text = addText("EVALUATED " + idea.getPrompt() + " " + date, evaluatedContent);
        addToIdea(idea,text,true,false);

        return text;

    }

    public boolean doesNoteNameExist(String name, String fileExtension){
        return getNotesPaths("All").contains(Note.getPath(name,notesDirectory,fileExtension));
    }

    public boolean doesNoteNameExist(String fileExtension){
        return getNotesPaths("All").contains(Note.getPath(fileExtension,notesDirectory));
    }

    /**
     * Gets Text note given path
     * @param path
     * @return
     */
    public Text getText(String path){
        try{
            return new Text(path);
        }catch (IOException e){
            return null;
        }
    }


    public double minimumReadinessScore(List<Idea> ideas){
        return quizzes.minimumReadinessScore(ideas);
    }


    public double averageReadiness(List<Idea> ideas){
        return quizzes.averageReadiness(ideas);
    }


    public double estimateNumberOfIncrementsForIdeaToReachScore(Idea idea, double goalScore, long timeRange){
        return quizzes.estimateNumberOfIncrementsForIdeaToReachScore(idea,goalScore,timeRange);
    }


    /**
     * Static method that saves xml string as a file
     * @param xml
     * @param loc
     * @param name
     * @throws IOException
     */
    public static void saveXML(String xml, String loc, String name) throws IOException {
        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + System.lineSeparator();
        xmlContent += xml;
        FileWriter xmlFile = new FileWriter(new File(loc+File.separator+name+".xml"));
        xmlFile.write(xmlContent);
        xmlFile.close();
    }

    /**
     * Saves current root directory to rootDirectory
     */
    public void saveIdea(){
        try{
            Model.saveXML(root.toXML(),rootDirectory,"Topics");
        }catch (IOException e){

        }
    }


    public Idea updateIdea(Idea idea, String prompt, List<String>keyWords, PromptType promptType){
        //Idea idea = root.findIdea(ideaID);

        idea.setKeyWords(keyWords);
        idea.setPromptType(promptType);
        idea.setPrompt(prompt);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
            }
        });

        return idea;
    }


    private Idea addIdea(String ideaID,HashMap<Note,Boolean>notes, List<String>keyWords, String prompt, PromptType promptType, Note finalNote) {
        Idea idea = new Idea(ideaID,notes,keyWords,prompt,promptType,finalNote);
        if(!isRootSubject()){
            idea.addNote(new SubjectNote(getCurrentSubject()));
        }
        this.root.add(idea);
        this.ideas.put(idea.getID(),idea);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
            }
        });
        return idea;
    }

    public Idea addIdea(String prompt,HashMap<Note,Boolean>notes, List<String>keyWords, PromptType promptType, Note finalNote) {
        return this.addIdea(null,notes,keyWords,prompt,promptType,finalNote);
    }

    public Idea addIdea(String prompt,List<String>keyWords,PromptType promptType){
        return addIdea(prompt,null,keyWords,promptType,null);
    }

    public Topic addTopic(String name){
        Topic topic = new Topic(name);
        this.root.add(topic);
        this.topics.put(topic.getID(),topic);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
            }
        });
        return topic;
    }

    public Object remove(String ID){
        Object o =  root.delete(ID);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
            }
        });
        return o;
    }


    public Subject remove(Subject subject){

        this.subjects.remove(subject);
        if(currentSubject.equals(subject)){
            this.setCurrentSubject("All");
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshSubjects();
            }
        });
        return subject;
    }

    public void remove(Object object){
        if(object instanceof Idea){
            remove((Idea) object);
        }else if(object instanceof Topic){
            remove((Topic) object);
        }else if(object instanceof Subject){
            remove((Subject) object);
        }
    }

    /**
     * Removes idea from system
     * @param idea
     * @return
     */
    public Idea remove(Idea idea){
        Idea i = root.delete(idea);
        quizzes.remove(i);
        ideas.remove(idea.getID());

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
                refreshData();
            }
        });
        return i;
    }

    /**
     * Removes topic from system
     * @param topic
     * @return
     */
    public Topic remove(Topic topic){
        List<Idea> ideas = topic.getAllIdeas();
        Topic t = root.delete(topic);
        topics.remove(topic.getID());
        List<Idea> rootIdeas = root.getAllIdeas();
        for(Idea i: ideas){
            if(!rootIdeas.contains(i)){
                quizzes.remove(i);
            }
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
                refreshData();
            }
        });
        return t;
    }

    /**
     * Moves node to a new Topic (if node
     * @param node
     * @param newLocation
     */
    public void move(Idea node, Topic newLocation){

        Topic loc = topics.get(newLocation.getID());

        root.delete(node);

        loc.add(node);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
            }
        });
    }


    public void addAndMoveIdeasToTopic(List<Object> ideasTopics, String newTopic){
        Topic topic = new Topic(newTopic);
        this.root.add(topic);
        this.topics.put(topic.getID(),topic);

        for(Object o: ideasTopics){
            this.root.delete(o);
            topic.add(o);
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
            }
        });

    }


    public void move( Topic topic,List<Object> ideasTopics){
        topic = topics.get(topic.getID());
        if(topic==null)
            return;

        for(Object o: ideasTopics){
            this.root.delete(o);
            topic.add(o);
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
            }
        });

    }


    public void addAndMoveTopicToTopic(String newTopic, Topic topic){
        Topic top = topics.get(topic.getID());
        Topic t = new Topic(newTopic);
        this.topics.put(t.getID(),t);
        top.add(t);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
            }
        });

    }



    public void remove(Note note){
        if(isRootSubject()){
            note.delete();

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    root.removeNote(note);
                    refreshSubjects();
                    refreshNotes();
                    refreshIdeas();
                }
            });
            return;

        }
        currentSubject.remove(note);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshSubjects();
                refreshNotes();
                refreshIdeas();
            }
        });
    }

    public List<Topic> findMovableTopics(List<Object> topicsIdeas){
        List<Topic> topics = root.getTopics();

        List<Topic> remove = new ArrayList<>();

        for(int i=0; i<topics.size(); i+=1){
            Topic t = topics.get(i);
            for(Object o: topicsIdeas){
                if(o instanceof Topic ){
                    Topic p = this.topics.get(((Topic) o).getID());
                    if(p!=null && p.contains(t) ){
                        remove.add(t);
                    }

                }
            }
        }

        topics.removeAll(remove);


        return topics;
    }

    public void move(Topic node, Topic newLocation){
        //Topic t = root.findTopic(node);
        //Topic loc = root.findTopic(newLocation);
/*
        Topic t = node;
        Topic loc = newLocation;

        if(node==null || loc==null){

            return;
        }

        root.delete(t);
        loc.add(t);*/

        root.delete(node);
        //root.findTopic(newLocation).add(node);

        Topic top = topics.get(newLocation.getID());
        top.add(node);
        System.out.println("TT Move: "+ node +" "+top);


        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
            }
        });

    }



    public boolean containsTopicOfName(String topic){
        return root.containsTopicOfName(topic);
    }

    public void disband(Topic topic){

        Topic loc = topics.get(topic.getID());

        if(loc==null)
            return;

        List<Object> children = new ArrayList<>();
        children.addAll(loc.getIdeas());
        children.addAll(loc.getSubTopics());

        Topic parent = root.findParent(loc);
        root.delete(loc);

        move(parent,children);

    }

    public Topic parent(Idea idea){
        //Topic root = filterTopicByCurrentSubject();
        return root.findParent(idea);
    }

    public Topic parent(Topic topic){
        //Topic root = filterTopicByCurrentSubject();
        return root.findParent(topic);
    }

    private Topic root;

    public Topic getRoot(){
        return root;
    }

    /**
     * Use to save current list of subjects to subject directory
     */
    public void saveSubject(){
        try{
            List<Subject> saveList = new ArrayList<>();
            for(Subject s: subjects){
                if(!s.getName().equals("All")){
                    saveList.add(s);
                }
            }
            saveXML(Subject.toXML(saveList),subjectDirectory,"Subjects");
        }catch (IOException e){
        }
    }

    /**
     * Check if subject exists to avoid duplicates
     * @param name
     * @return
     */
    public boolean subjectExists(String name){
        for(Subject s: subjects){
           if(s.getName().equals(name)){
               return true;
           }
        }
        return false;
    }


    /**
     * Adds subject to model. Returns false if subject can't be added
     * @param subject: name of the subject
     * @param notes: notes associated with the subject
     * @return
     */
    public boolean addSubject(String subject, List<Note>notes){
        if(subjectExists(subject)) return false;
        Subject sub = new Subject(subject);
        for(Note n: notes){
            sub.add(n.getPath().toString());
        }
        this.subjects.add(sub);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshSubjects();
            }
        });
        return true;
    }

    public Subject updateSubject(String currentName, String newName, List<Note>notes ){

        for(Subject s: subjects){
            if(s.getName().equals(currentName)){
                if(newName!=null)
                    s.setName(newName);
                if(notes!=null)
                    s.setPaths(Model.getPaths(notes));

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        refreshSubjects();
                    }
                });
                return s;
            }
        }
        return null;
    }

    public Subject getSubject(String name){
        for(Subject s: subjects){
            if(s.getName().equals(name)){
                return s;
            }
        }
        return null;
    }


    /**
     * Gets all subjects a note is a member of
     * @param note
     * @return
     */
    public List<Subject> getSubjects(Note note){
        List<Subject> subs = FXCollections.observableArrayList(Subject.getSubjects(subjects,note));
        subs.remove(getSubject("All"));
        return subs;
    }

    public List<Subject> getAllSubjects(){
        return subjects;
    }

    public List<Idea> getIdeas(Note note){
        return root.getIdeas(note);
    }

    /*
    public Topic filterTopicBySubject(Subject currentSubject){
        if(currentSubject.getName().equals("All")){
            return root;
        }
        Topic rootCopy = root.copy();
        rootCopy.setName(currentSubject.getName());
        List<Note> notes = this.getNotes(currentSubject.getName());
        notes.add(currentSubject.getNote());
        rootCopy.removeAllUnassociatedWithNotes(notes);
        return rootCopy;
    }

    public Topic filterTopicByCurrentSubject(){
        return filterTopicBySubject(currentSubject);
    }
*/
    public void removeFromSubject(String subject, Note note){
        Subject s = getSubject(subject);
        if(s==null) return;
        s.remove(note);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshSubjects();
                refreshNotes();
            }
        });


    }

    public void addToSubject(String subject, Note note){
        Subject s = getSubject(subject);
        if(s==null) return;
        s.add(note);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshSubjects();
                refreshNotes();
            }
        });
    }

    public List<String> getSubjectStrings(){
        List<String> subjects = new ArrayList<String>();
        for(Subject s: this.subjects){
            subjects.add(s.getName());
        }
        return subjects;
    }

    public static List<String> getPaths(List<Note>notes){
        List<String> paths = new ArrayList<>();
        for(Note n: notes){
            paths.add(n.getPath().toString());
        }
        return paths;
    }

    /**
     * Sort By Number of connections comparator
     * @return
     */
    public Comparator<Note> sortByNumberOfConnectionsComparator() {

        return (new Comparator<Note>() {
            @Override
            public int compare(Note o1, Note o2) {
                return root.getNumberOfConnections(o1) - root.getNumberOfConnections(o2);
            }
        });

    }

    public List<Note> getHighlyUsedNotes(FilterSettings filterSettings, List<Note> underutilisedNotes, List<Note>allNotes){

        List<Note> copy = allNotes.subList(0,allNotes.size());
        copy.removeAll(underutilisedNotes);
        return filterSettings.applyFilters(copy);

    }

    public List<Idea> getAllIdeas(){
        //if(root==null) return new ArrayList<>();
        return this.root.getAllIdeas();
    }

    public List<Topic> getAllTopics(){
        return this.root.getTopics();
    }

    public int getReadinessType(Idea idea){
        return quizzes.getReadinessType(idea,System.currentTimeMillis());
    }

    public List<Note> getUnderutilisedNotes(FilterSettings filterSettings, List<Note>allNotes){
        allNotes.sort(sortByNumberOfConnectionsComparator());

        if(allNotes.isEmpty()){
            return allNotes;
        }

        int largest = root.getNumberOfConnections(allNotes.get(allNotes.size()-1));
        int small = root.getNumberOfConnections(allNotes.get(0));


        if(allNotes.isEmpty()){
            return allNotes;
        }

        int tenPercent = (small + largest)/10;


        int finalIndex = 0;

        for(Note n: allNotes){

            if(root.getNumberOfConnections(n) <= tenPercent ){
                finalIndex += 1;
            }else{
                break;
            }

        }

        finalIndex = (finalIndex < (0.4*allNotes.size()) ?  (int) (0.4*allNotes.size()) : finalIndex);


        List<Note> fin = allNotes.subList(0,finalIndex);

        fin = filterSettings.applyFilters(fin);

        return fin;

    }

    public void saveQuiz(){
        try {
            saveXML(quizzes.toXML(),quizDirectory ,"Quizzes");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void saveStudy(){
        try {
            saveXML(study.toXML(),quizDirectory ,"Study");
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public List<IdeaQuiz> getAllIdeaQuizzes(List<Idea> ideas){
        return quizzes.getAllIdeaQuizzes(ideas);
    }

    String corruptedDirectory;

    /**
     * Used to setup the Model key environmental paths
     * @param notesDirectory
     * @param rootDirectory
     * @param subjectDirectory
     * @throws ParserConfigurationException
     * @throws FileNotFoundException
     * @throws SAXException
     * @throws IOException
     */


    int maxNumberOfBackupFolders;


    NotesInformation notesInformation;

    public void saveNotesInformation(){
        try {
            saveXML(notesInformation.toXML(),notesDirectory,"NotesInformation");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addNumberOfPages(Note note, int numberOfPages){
        notesInformation.addNumberOfPages(note.getPath().toString(),numberOfPages);
        saveNotesInformation();
    }

    public Integer getNumberOfPages(Note note){
        Integer yo =  notesInformation.getNumberOfPages(note.getPath().toString());
        return yo;
    }

    public void initialise(String notesDirectory, String settingsDirectory) throws ParserConfigurationException, FileNotFoundException, SAXException, IOException{
        this.notesDirectory = notesDirectory;

        File notesFolder = new File(notesDirectory);
        if(!notesFolder.exists()){
            notesFolder.mkdirs();
        }
        File settingsFolder = new File(settingsDirectory);
        if(!settingsFolder.exists()){
            settingsFolder.mkdirs();
        }

        this.rootDirectory = settingsDirectory;
        this.subjectDirectory = settingsDirectory;
        this.quizDirectory = settingsDirectory;
        this.studyDirectory = settingsDirectory;

        this.topics = new HashMap<>();
        this.ideas = new HashMap<>();


        this.maxNumberOfBackupFolders = 4;

        this.recoveryPath = notesDirectory + File.separator + "recovery";

        this.corruptedDirectory = recoveryPath + File.separator + "corrupted";


        File folder = new File(corruptedDirectory);
        folder.mkdirs();
        this.folders.clear();
        File[] fs = new File(recoveryPath).listFiles();
        List<File> files = Arrays.asList(fs);

        files.sort(new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return new Long(o1.lastModified()).compareTo(o2.lastModified());
            }
        });

        for(File file: files){
            if(!file.getPath().contains("corrupted")){
                this.folders.add(file.getPath());
            }
        }

        trimRecoveryFolder();

        try{

            notesInformation = NotesInformation.fromXML(notesDirectory + File.separator + "NotesInformation.xml");

        }catch (Exception e){

            notesInformation = new NotesInformation();

            saveNotesInformation();

        }

        Subject allSubject = new Subject("All");

        try{
            allSubject.setPaths(Model.getPaths(this.getAllNotes(notesDirectory)));
        }catch (Exception e){

        }

        this.subjects = new ArrayList<>();
        this.subjects.add(allSubject);
        this.setCurrentSubject("All");


        try{
            this.subjects.addAll(getSubjects());
        }catch (IOException e){
            e.printStackTrace();

            if(new File(subjectDirectory + File.separator + "Subjects.xml").exists()){
                String date = new SimpleDateFormat("HH-mm-dd-MM-yyyy").format(new Date());
                addToRecoveryFolder(subjectDirectory,"Subjects","Subjects-" + date,".xml", "corrupted");
            }


            saveSubject();
        }

        this.root = getRoot(rootDirectory);

        this.root.getAllIdeas().forEach(idea -> {ideas.put(idea.getID(),idea);});
        this.root.getTopics().forEach(topic -> {topics.put(topic.getID(),topic);});


        try {
            this.quizzes = new Quizzes(quizDirectory + File.separator + "Quizzes.xml");
        }catch (Exception e){

            e.printStackTrace();

            if(new File(quizDirectory + File.separator + "Quizzes.xml").exists()){
                String date = new SimpleDateFormat("HH-mm-dd-MM-yyyy").format(new Date());
                addToRecoveryFolder(quizDirectory,"Quizzes","Quizzes-"+date,".xml", "corrupted");
            }

            this.quizzes = new Quizzes();
            saveQuiz();

        }


        try {

            this.study = Study.fromXML(studyDirectory + File.separator + "Study.xml");

        }catch (Exception e){

            e.printStackTrace();

            if(new File(studyDirectory + File.separator + "Study.xml").exists()){
                String date = new SimpleDateFormat("HH-mm-dd-MM-yyyy").format(new Date());
                addToRecoveryFolder(studyDirectory,"Study","Study-"+date,".xml", "corrupted");
            }

            this.study = new Study();
            saveStudy();

        }

        setDefaultStudyPlans();

    }

    public void modify(Note note, String textContent){
        if(!(note instanceof Text)){
            return;
        }
        Text text = (Text) note;
        try{
            text.setTextContent(textContent);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void setDefaultStudyPlans(){
        StudyPlan low = this.study.findStudyPlan("lower");
        if(low==null){
            String name = "Learning";
            String description = "A simple plan with plenty of time for deep learning of notes. Best started weeks before an exam.";
            low = new StudyPlan("lower",name,description,0.2,0.2,1);
            this.add(low);
            //System.out.println("Adding learning" );

        }

        StudyPlan med = this.study.findStudyPlan("medium");
        if(med==null){
            String name = "Exam Cure";
            String description = "A kinda intense study plan. Best completed plenty of time before an exam for reviewing (not learning) your notes. ";
            med = new StudyPlan("medium",name,description,0.4,0.4,1);
            this.add(med);
            //System.out.println("Adding Exam Cure" );
        }

        StudyPlan high = this.study.findStudyPlan("higher");
        if(high==null){
            String name = "Kid to Einstein ASAP";
            String description = "An incredibly intense study plan.  Best suited for a level 100 procrastinator who barely has a week of study before the exam.";
            high = new StudyPlan("higher",name,description,0.6,0.6,1);
            this.add(high);
            //System.out.println("Adding Higher" );
        }
    }

    public Subject getCurrentSubject(){
        return this.currentSubject;
    }

    public void removeFromIdea(Idea idea, Note note){

        //Idea i = root.findIdea(idea.getID());
        Idea i = idea;

        if(i==null){
            return;
        }

        i.removeNote(note);

        refreshIdeas();

    }

    public void addToIdea(Idea idea, Note note, boolean isFinalNote, boolean isPrompt){

        if(idea==null || note==null)
            return;

        //Idea i = root.findIdea(idea.getID());

        Idea i = idea;

        if(i==null){
            return;
        }

        i.addNote(note,isPrompt);

        if(isFinalNote)
            i.setFinalNote(note);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
            }
        });

    }

    public void addIdeaToSubject(Idea idea, Subject subject){
        Idea i = getIdea(idea.getID());
        Subject s = getSubject(subject.getName());
        if(i==null || s==null){
            return;
        }
        i.addNote(new SubjectNote(s));
        for(Note n: i.getNotes()){
            s.add(n);
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshSubjects();
                refreshIdeas();
            }
        });
    }

    public void removeIdeaFromSubject(Idea idea, Subject subject){
        Idea i = getIdea(idea.getID());
        Subject s = getSubject(subject.getName());
        if(i==null || s==null){
            return;
        }

        for(Note note: listOfNodesPresentInBothIdeaAndSubject(i,s)){
            i.removeNote(note);
        }

        i.removeNote(new SubjectNote(s));

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
                refreshSubjects();
            }
        });


    }

    private List<Note> listOfNodesPresentInBothIdeaAndSubject(Idea idea, Subject subject){
        List<Note> notes = new ArrayList<>();

        for(Note n: idea.getNotes()){
            if(subject.memberOf(n)){
                notes.add(n);
            }
        }

        return notes;
    }

    public List<Note> listOfNotesPresentInBothIdeaAndSubject(Idea idea, Subject subject){
        Idea i = getIdea(idea.getID());
        Subject s = getSubject(subject.getName());
        if(i==null || s==null){
            return new ArrayList<>();
        }

        return listOfNodesPresentInBothIdeaAndSubject(idea,subject);


    }

    Quizzes quizzes;

    public double calculateReadiness(Idea idea, int confidence){
        return quizzes.getReadiness(idea,System.currentTimeMillis(),confidence);
    }

    public double calculateReadiness(Idea idea){
        return quizzes.getReadiness(idea,System.currentTimeMillis(),null);
    }

    public int getReadinessType(double readinessScore){
        return quizzes.getReadinessType(readinessScore);
    }

    public HashMap<String,List<IdeaQuiz>> getIdeaQuizzesByDate(Idea idea){
        return quizzes.getFrequencyPerTime(idea,System.currentTimeMillis());
    }

    public void addQuiz(Quiz quiz){
        quizzes.add(quiz);
    }

    public Idea getIdea(String uniqueID){
        return ideas.get(uniqueID);
        //return root.findIdea(uniqueID);
    }

    public Topic getTopic(String uniqueID){
        return topics.get(uniqueID);
        //return root.findTopic(uniqueID);
    }

    public void setCurrentSubject(String subject){
        Subject sub = this.getSubject(subject);
        if(sub==null)
            return;
        this.currentSubject = sub;
    }

    public long getStudyDuration(long start, long end){
        return quizzes.getTimeStudiedInMS(start,end);
    }

    public long getStudyDuration(long start, long end, List<Idea> ideas){
        return quizzes.getTimeStudiedInMS(start,end,ideas);
    }

    public void add(StudyPlan studyPlan){
        this.study.add(studyPlan);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshStudy();
            }
        });
    }

    public void update(StudyPlan plan, String name, String description, double ips, double msl, double score){
        plan.reset(name,description,ips,msl,score);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshStudy();
            }
        });
    }

    public boolean isRootSubject(){
        return currentSubject.getName().equals("All");
    }

    public void remove(StudySession session){
        study.remove(session);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshStudy();
            }
        });
    }

    public List<StudySession> getSessions(){
        return study.getStudySessions();
    }

    public void add(StudySession session){
        this.study.add(session);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshStudy();
            }
        });
    }

    public void remove(StudyPlan plan){
        this.study.remove(plan);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshStudy();
            }
        });
    }

    public StudyPlan findPlan(String id){
        return study.findPlan(id);
    }

    public List<StudyPlan> getStudyPlans(){
        return this.study.getStudyPlans();
    }

    public double getScoreIncrement(){
        return quizzes.getScoreIncrement();
    }

    public double getReadinessScore(){
        return quizzes.getReadinessScore();
    }

    public Model(){
        refreshNotesControllers = new ArrayList<>();
        refreshIdeasControllers = new ArrayList<>();
        refreshSubjectsControllers = new ArrayList<>();
        refreshDataControllers = new ArrayList<>();
        refreshStudyControllers = new ArrayList<>();
        this.folders = new ArrayList<>();

    }

    public boolean studySessionNameExists(String name){

        for(StudySession p: study.getStudySessions()){
            if(p.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    public ActivationInformation getActivation(StudySession session, Date finalDate){
        return study.getActivation(quizzes.getRegionToFrequency(),session,finalDate);
    }


}
