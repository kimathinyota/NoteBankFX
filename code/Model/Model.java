package Code.Model;

import Code.Controller.RefreshInterfaces.*;
import Code.Controller.home.notes.filters.FilterSettings;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.util.Pair;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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
            return new Book(path);
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
                    notes.add(fromPath(path));
                }


            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return notes;
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
    public void addBook(String name, String originalFilePath){
        try{
            Book b = new Book(name,originalFilePath,notesDirectory);
            this.getSubject("All").add(b.getPath().toString());
            if(!currentSubject.getName().equals("All")){
                currentSubject.add(b.getPath().toString());
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
     * Adds new Image to notesDirectory
     * @param name
     * @param originalPath
     */
    public void addImage(String name, String originalPath){
        try{
            Image i = new Image(name,originalPath,notesDirectory);
            this.getSubject("All").add(i.getPath().toString());
            if(!currentSubject.getName().equals("All")){
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
            if(!currentSubject.getName().equals("All")){
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

    /**
     * Gets book note given path
     * @param path
     * @return
     */
    public Book getBook(String path){
        try{
            return new Book(path);
        }catch (IOException e){
            return null;
        }
    }

    /**
     * Gets book note given path and number of pages
     * @param path
     * @param specifyPage
     * @return
     */
    public Book getBook(String path, String specifyPage){
        try{
            return new Book(path,specifyPage);
        }catch (IOException e){
            return null;
        }
    }

    /**
     * Get image note given path
     * @param path
     * @return
     */
    public Image getImage(String path){
        try{
            return new Image(path);
        }catch (IOException e){
            return null;
        }
    }


    public double minimumReadinessScore(List<Idea> ideas){
        return quizzes.minimumReadinessScore(ideas);
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

    /**
     * Allows user to update an Idea (or create one)
     * @param ideaID
     * @param notes
     * @param keyWords
     * @param prompt
     * @param promptType
     * @param finalNote
     * @return
     */
    public Idea updateIdea(String ideaID,HashMap<Note,Boolean>notes, List<String>keyWords, String prompt, PromptType promptType, Note finalNote) {

        Idea idea = root.findIdea(ideaID);


        if(idea==null){
            idea = new Idea(ideaID,notes,keyWords,prompt,promptType,finalNote);
            this.root.add(idea);

        }else{
            idea.initialise(notes,keyWords,prompt,promptType,finalNote); ;
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
            }
        });

        return idea;
    }

    public Idea updateIdea(String ideaID, String prompt, List<String>keyWords, PromptType promptType){
        Idea idea = root.findIdea(ideaID);

        if(idea==null){
            idea = new Idea(ideaID,new HashMap<>(),keyWords,prompt,promptType,null);
            this.root.add(idea);

        }else{
            idea.setKeyWords(keyWords);
            idea.setPromptType(promptType);
            idea.setPrompt(prompt);
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
            }
        });

        return idea;
    }

    public void addNoteToIdea(Idea i, Note note){
        Idea idea = root.findIdea(i.getID());
        if(idea==null){
            return;
        }
        idea.addNote(note);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
            }
        });
    }

    private Idea addIdea(String ideaID,HashMap<Note,Boolean>notes, List<String>keyWords, String prompt, PromptType promptType, Note finalNote) {
        Idea idea = new Idea(ideaID,notes,keyWords,prompt,promptType,finalNote);
        this.root.add(idea);
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

    /**
     * Allows user to update a Topic (or create one)
     * @param topicID
     * @param name
     * @return
     */
    public Topic updateTopic(String topicID,String name){
        Topic topic = root.findTopic(topicID);
        if(topic==null)
            return null;
        topic.setName(name);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
            }
        });
        return topic;
    }

    public Topic updateTopic(String topicID,String name,List<Idea> ideas, List<Topic>topics){
        Topic topic = root.findTopic(topicID);
        if(topic==null)
            return null;
        topic.initialise(name,ideas,topics);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
            }
        });
        return topic;
    }

    public Topic updateTopic(String topicID, String name, List<Idea> ideas){
        Topic topic = root.findTopic(topicID);
        if(topic==null)
            return null;
        topic.initialise(name,ideas,null);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
            }
        });
        return topic;
    }

    public Topic updateTopic(String topicID, List<Topic>topics, String name){
        Topic topic = root.findTopic(topicID);
        if(topic==null)
            return null;
        topic.initialise(name,null,topics);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
            }
        });
        return topic;
    }

    public Topic addTopic(String name,List<Idea> ideas, List<Topic>topics){
        Topic topic = new Topic(name);
        topic.initialise(name,ideas,topics);
        this.root.add(topic);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
            }
        });
        return topic;
    }

    public Topic addTopic(String name){
        Topic topic = new Topic(name);
        this.root.add(topic);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
            }
        });
        return topic;
    }

    public Topic addTopic(String name, List<Idea> ideas){
        return addTopic(name,ideas,null);
    }

    public Topic addTopic(List<Topic>topics, String name){
       return addTopic(name,null,topics);
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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
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
        Topic t = root.delete(topic);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
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
        Idea i = root.findIdea(node);
        Topic loc = root.findTopic(newLocation);

        //System.out.println("Move " + i + " to " + newLocation);

        if(i==null || loc==null){
            return;
        }
        root.delete(i);
        loc.add(i);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
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
                    Topic p = root.findTopic((Topic) o);
                    if(p.contains(t)){
                        remove.add(t);
                    }

                }
            }
        }

        topics.removeAll(remove);


        return topics;
    }

    public void move(Topic node, Topic newLocation){
        Topic t = root.findTopic(node);
        Topic loc = root.findTopic(newLocation);

        if(node==null || loc==null){

            return;
        }

        root.delete(t);
        loc.add(t);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
            }
        });
    }

    public void move(Object object, Topic newLocation){
        if(object instanceof Idea){
            move((Idea) object, newLocation);
        }else if(object instanceof Topic){
            move((Topic) object, newLocation);
        }
    }

    public boolean containsTopicOfName(String topic){
        return root.containsTopicOfName(topic);
    }

    public void move(List<Object>ideasOrTopics, Topic newLocation){

        Topic loc = root.findTopic(newLocation);
        if(loc==null)
            return;

        for(Object o: ideasOrTopics){
            if(o instanceof Topic || o instanceof Idea){
                root.delete(o);
                loc.add(o);
            }
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
            }
        });
    }

    public void disband(Topic topic){
        Topic loc = root.findTopic(topic);
        if(loc==null)
            return;

        List<Object> children = new ArrayList<>();
        children.addAll(loc.getIdeas());
        children.addAll(loc.getSubTopics());

        Topic parent = root.findParent(loc);
        root.delete(loc);

        move(children,parent);

    }

    public Topic parent(Idea idea){
        return root.findParent(idea);
    }

    public Topic parent(Topic topic){
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
     * @return
     */
    public boolean addSubject(String subject){
        if(subjectExists(subject)) return false;
        this.subjects.add(new Subject(subject));
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshSubjects();
            }
        });
        return true;
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

    public void addNoteToSubject(String subjectName, Note note){
        Subject subject = getSubject(subjectName);
        if(!subject.memberOf(note)){
            getSubject(subjectName).add(note.getPath().toString());
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshSubjects();
            }
        });

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
     * Adds subject to model. Returns false if subject can't be added
     * @param notePaths: notes paths associated with the subject
     * @param subject: name of the subject
     * @return
     */
    public boolean addSubject(List<String>notePaths,String subject){
        if(subjectExists(subject)) return false;
        Subject sub = new Subject(subject);
        for(String p: notePaths){
            sub.add(p);
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

    public void limitNoteToTheseSubjects(Note note, List<Subject>subjects){
        for(Subject s: this.getAllSubjects()){
            if(!s.getName().equals("All")){
                if(subjects.contains(s)){
                    //note should be added to subject
                    s.add(note);
                }else{
                    s.remove(note);
                }
            }
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshSubjects();
            }
        });
    }

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

    public void limitNoteToTheseIdeas(Note note, HashMap<Idea, Pair<String,Boolean>>map){
        for(Idea i: this.getAllIdeas()){
            if(map.containsKey(i)){
                i.setFinalNote(null);
                //note should be added to idea
                Idea idea = root.findIdea(i);
                Pair<String,Boolean> p = map.get(i);
                idea.addNote(note,p.equals("Prompt"));
                if(p.getValue()){
                    idea.setFinalNote(note);
                }
            }else {
                root.findIdea(i).removeNote(note);
            }
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
            }
        });
    }

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

        }
    }


    public void saveStudy(){
        try {
            System.out.println("Saving study");
            saveXML(study.toXML(),quizDirectory ,"Study");
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public List<IdeaQuiz> getAllIdeaQuizzes(List<Idea> ideas){
        return quizzes.getAllIdeaQuizzes(ideas);
    }

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
    public void initialise(String notesDirectory, String rootDirectory, String subjectDirectory, String quizDirectory) throws ParserConfigurationException, FileNotFoundException, SAXException, IOException{

        this.notesDirectory = notesDirectory;
        this.rootDirectory = rootDirectory;
        this.subjectDirectory = subjectDirectory;
        this.quizDirectory = quizDirectory;

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

        }

        this.root = getRoot(rootDirectory);


        try {
            this.quizzes = new Quizzes(quizDirectory + File.separator + "Quizzes.xml");
        }catch (Exception e){
            this.quizzes = new Quizzes();
            saveQuiz();
        }


        try {

            this.study = Study.fromXML(quizDirectory + File.separator + "Study.xml");

            StudyPlan low = study.findStudyPlan("lower");
            if(low==null){
                String name = "Learning";
                String description = "A simple plan with plenty of time for deep learning of notes. Best started weeks before an exam.";
                low = new StudyPlan("lower",name,description,0.2,0.2,1);
                this.add(low);
                System.out.println("Adding learning" );

            }

            StudyPlan med = study.findStudyPlan("medium");
            if(med==null){
                String name = "Exam Cure";
                String description = "A kinda intense study plan. Best completed plenty of time before an exam for reviewing (not learning) your notes. ";
                med = new StudyPlan("medium",name,description,0.4,0.4,1);
                this.add(med);
                System.out.println("Adding Exam Cure" );
            }

            StudyPlan high = study.findStudyPlan("higher");
            if(high==null){
                String name = "Kid to Einstein ASAP";
                String description = "An incredibly intense study plan.  Best suited for a level 100 procrastinator who barely has a week of study before the exam.";
                high = new StudyPlan("higher",name,description,0.6,0.6,1);
                this.add(high);
                System.out.println("Adding Higher" );
            }



        }catch (Exception e){

            e.printStackTrace();
            this.study = new Study();
            saveStudy();

        }




/*
        for(int i=0; i<this.root.getAllIdeas().size(); i++){
            if(i<4){
                Topic topic = this.addTopic("The first topic i've created");
                this.move(this.getAllIdeas().get(i),topic);
            }else if(i<8){
                Topic topic = this.addTopic("The second topic i've created");
                this.move(this.getAllIdeas().get(i),topic);
            }else if(i<12){
                Topic topic = this.addTopic("The third topic i've created");
                this.move(this.getAllIdeas().get(i),topic);
            }
        }
        */

    }

    public Subject getCurrentSubject(){
        return this.currentSubject;
    }

    public void removeFromIdea(Idea idea, Note note){
        //System.out.println("Remove " + note + " from " + idea);
        Idea i = root.findIdea(idea.getID());

        if(i==null){
            return;
        }

        i.removeNote(note);

        refreshIdeas();
        //refreshNotes();


    }

    public void addToIdea(Idea idea, Note note, boolean isFinalNote, boolean isPrompt){
        ////System.out.println("Add " + note + " to " + idea);
        if(idea==null || note==null)
            return;
        Idea i = root.findIdea(idea.getID());

        if(i==null){
            return;
        }


        i.addNote(note,isPrompt);

        //System.out.println("Added " + note + " to " + idea + " of (isFinalNote,isPrompt) = (" + isFinalNote + "," + isPrompt + ")" );


        if(isFinalNote)
            i.setFinalNote(note);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
            }
        });
    }

    public Topic addToTopic(Object object, Topic topic){
        Topic t = root.findTopic(topic);
        if(t==null || !(object instanceof Idea || object instanceof Topic )){
            return null;
        }

        Object o = (object instanceof Idea ? root.findIdea((Idea) object) : root.findTopic((Topic) object));
        t.add(o);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                refreshIdeas();
            }
        });
        return t;
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
        return root.findIdea(uniqueID);
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
        return currentSubject.equals("All");
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
