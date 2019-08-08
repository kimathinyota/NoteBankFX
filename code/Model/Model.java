package Code.Model;

import Code.Controller.RefreshIdeasController;
import Code.Controller.RefreshNotesController;
import Code.Controller.RefreshSubjectsController;
import javafx.collections.ObservableList;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Model {



    private List<RefreshNotesController> refreshNotesControllers;
    private List<RefreshIdeasController> refreshIdeasControllers;
    private List<RefreshSubjectsController> refreshSubjectsControllers;

    Subject currentSubject;


    public void addRefreshNotesController(RefreshNotesController controller){
        refreshNotesControllers.add(controller);
    }

    public void addRefreshIdeasController(RefreshIdeasController controller){
        refreshIdeasControllers.add(controller);
    }

    public void addRefreshSubjectsController(RefreshSubjectsController controller){
        refreshSubjectsControllers.add(controller);
    }

    public void refreshNotes(){
        for(RefreshNotesController c: refreshNotesControllers){
            c.refreshNotes();
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

        System.out.println("Path: " + path);
        File file = new File(path);

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
                notes.add(fromPath(path));
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
            System.out.println();
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
            new Book(name,originalFilePath,notesDirectory);
            refreshNotes();
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
            new Image(name,originalPath,notesDirectory);
            refreshNotes();
        }catch (IOException e){

        }
    }

    /**
     * Adds new Text to notesDirectory
     * @param name
     * @param textContent
     */
    public void addText(String name, String textContent){
        try{
            new Text(name,textContent,notesDirectory);
            refreshNotes();
        }catch (IOException e){

        }
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
    public Idea updateIdea(String ideaID,HashMap<Note,Integer>notes, List<String>keyWords, String prompt, PromptType promptType, Note finalNote) {

        Idea idea = root.findIdea(ideaID);

        System.out.println("Found idea: " + idea);

        if(idea==null){
            idea = new Idea(ideaID,notes,keyWords,prompt,promptType,finalNote);
            this.root.add(idea);

        }else{
            idea.initialise(notes,keyWords,prompt,promptType,finalNote); ;
        }

        refreshIdeas();

        return idea;
    }

    private Idea addIdea(String ideaID,HashMap<Note,Integer>notes, List<String>keyWords, String prompt, PromptType promptType, Note finalNote) {
        Idea idea = new Idea(ideaID,notes,keyWords,prompt,promptType,finalNote);
        this.root.add(idea);
        refreshIdeas();
        return idea;
    }

    public Idea addIdea(String prompt,HashMap<Note,Integer>notes, List<String>keyWords, PromptType promptType, Note finalNote) {
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
        refreshIdeas();
        return topic;
    }

    public Topic updateTopic(String topicID,String name,List<Idea> ideas, List<Topic>topics){
        Topic topic = root.findTopic(topicID);
        if(topic==null)
            return null;
        topic.initialise(name,ideas,topics);
        refreshIdeas();
        return topic;
    }

    public Topic updateTopic(String topicID, String name, List<Idea> ideas){
        Topic topic = root.findTopic(topicID);
        if(topic==null)
            return null;
        topic.initialise(name,ideas,null);
        refreshIdeas();
        return topic;
    }

    public Topic updateTopic(String topicID, List<Topic>topics, String name){
        Topic topic = root.findTopic(topicID);
        if(topic==null)
            return null;
        topic.initialise(name,null,topics);
        refreshIdeas();
        return topic;
    }

    public Topic addTopic(String name,List<Idea> ideas, List<Topic>topics){
        Topic topic = new Topic(name);
        topic.initialise(name,ideas,topics);
        this.root.add(topic);
        refreshIdeas();
        return topic;
    }

    public Topic addTopic(String name){
        Topic topic = new Topic(name);
        this.root.add(topic);
        refreshIdeas();
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
        refreshIdeas();
        return o;
    }


    public Subject remove(Subject subject){
        this.subjects.remove(subject);
        return subject;
    }



    /**
     * Removes idea from system
     * @param idea
     * @return
     */
    public Idea remove(Idea idea){
        Idea i = root.delete(idea);
        refreshIdeas();
        return i;
    }

    /**
     * Removes topic from system
     * @param topic
     * @return
     */
    public Topic remove(Topic topic){
        Topic t = root.delete(topic);
        refreshIdeas();
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

        if(i==null || loc==null){
            return;
        }
        root.delete(i);
        loc.add(i);
        refreshIdeas();
    }

    public void move(Topic node, Topic newLocation){
        Topic t = root.findTopic(node);
        Topic loc = root.findTopic(newLocation);

        if(node==null || loc==null){
            return;
        }

        root.delete(t);
        loc.add(t);
        refreshIdeas();
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
            saveXML(Subject.toXML(subjects),subjectDirectory,"Subjects");
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
        refreshSubjects();
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
        refreshSubjects();
        return true;
    }

    public Subject updateSubject(String currentName, String newName, List<Note>notes ){
        for(Subject s: subjects){
            if(s.getName().equals(currentName)){
                if(newName!=null)
                    s.setName(newName);
                if(notes!=null)
                    s.setPaths(Model.getPaths(notes));
                refreshSubjects();

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
        refreshSubjects();
        return true;
    }

    /**
     * Gets all subjects a note is a member of
     * @param note
     * @return
     */
    public List<Subject> getSubjects(Note note){
        List<Subject> subs = new ArrayList<>();
        for(Subject s: subjects){
            if(s.memberOf(note)){
                subs.add(s);
            }
        }
        return subs;
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


    private List<Subject> subjects;

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
    public void initialise(String notesDirectory, String rootDirectory, String subjectDirectory) throws ParserConfigurationException, FileNotFoundException, SAXException, IOException{

        this.notesDirectory = notesDirectory;
        this.rootDirectory = rootDirectory;
        this.subjectDirectory = subjectDirectory;

        try{
            this.subjects = getSubjects();
        }catch (IOException e){
            Subject allSubject = new Subject("All");
            allSubject.setPaths(Model.getPaths(this.getAllNotes(notesDirectory)));
            this.subjects = new ArrayList<>();
            this.subjects.add(allSubject);
            this.saveSubject();
        }

        this.setCurrentSubject("All");

        this.root = getRoot(rootDirectory);



    }


    public Subject getCurrentSubject(){
        return this.currentSubject;
    }




    public void setCurrentSubject(String subject){
        Subject sub = this.getSubject(subject);
        if(sub==null)
            return;
        this.currentSubject = sub;
    }

    String notesDirectory;
    String rootDirectory;
    String subjectDirectory;



    public Model(){
        refreshNotesControllers = new ArrayList<>();
        refreshIdeasControllers = new ArrayList<>();
        refreshSubjectsControllers = new ArrayList<>();
    }

}
