package Code.Model;

import Code.View.ObservableObject;
import javafx.collections.FXCollections;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.*;
import java.lang.Integer;


/**
 * 
 * Idea: represents a prompt attached to a set of notes
 * A set of key words is also associated with a given prompt in an idea
 * Key word: A word that must be included when giving an answer to the given prompt
 * @author kimathinyota
 *
 */

public class Idea implements ObservableObject {
	public void setNotes(HashMap<Note, Boolean> notes) {
		this.notes = notes;
	}

	public void setKeyWords(List<String> keyWords) {
		this.keyWords.clear();
		this.keyWords.addAll(keyWords);
	}

	public String getUniqueID() {
		return uniqueID;
	}

	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public void setPromptType(PromptType promptType) {
		this.promptType = promptType;
	}

	private HashMap<Note,Boolean> notes;
	private Set<String>keyWords;
	private String uniqueID;
	private String prompt;
	private PromptType promptType;
	private Note finalNote;

	List<SubjectNote> subjectNotes;


	/**
	 * Complete reset of Model.Idea without reinstantiating the object
	 * @param notes
	 * @param keyWords
	 * @param prompt
	 * @param promptType
	 * @param finalNote
	 */
	public void initialise(HashMap<Note,Boolean>notes, List<String>keyWords, String prompt, PromptType promptType, Note finalNote) {
		if(notes!=null)
			this.notes = new HashMap<Note,Boolean>(notes);


		if(keyWords!=null){
			this.keyWords = new HashSet<>(keyWords);
		}

		this.prompt = (prompt==null ? this.prompt : prompt);
		this.promptType = (promptType==null ? this.promptType : promptType);
		if(finalNote!=null){
			this.finalNote = finalNote;
		}

		this.notes.remove(null);
		this.keyWords.remove(null);

	}


	public Idea(HashMap<Note,Boolean>notes, List<String>keyWords, String prompt, PromptType promptType, Note finalNote){
		this.subjectNotes = new ArrayList<>();
		this.notes = new HashMap<Note,Boolean>();
		this.keyWords = new HashSet<>();
		this.prompt = "";
		this.finalNote = null;
		this.promptType = PromptType.Question;
		this.uniqueID = UUID.randomUUID().toString();
		this.initialise(notes,keyWords,prompt,promptType,finalNote);
	}


	public Idea(String uniqueID, HashMap<Note,Boolean>notes, List<String>keyWords, String prompt, PromptType promptType, Note finalNote){
		this.subjectNotes = new ArrayList<>();
		this.notes = new HashMap<Note,Boolean>();
		this.keyWords = new HashSet<>();
		this.prompt = "";
		this.finalNote = null;
		this.promptType = PromptType.Question;
		if(uniqueID!=null){
			this.uniqueID = uniqueID;
		}else
			this.uniqueID = UUID.randomUUID().toString();
		this.initialise(notes,keyWords,prompt,promptType,finalNote);
	}


/*
	public String getNoteType(Note note){
		return (notes.get(note).equals(Idea.PROMPT_NOTE) ? "Prompt" : "Non-Prompt");
	}

*/
	
	/**
	 * 
	 * @return ID for Model.Idea
	 */
	public String getID() {
		return this.uniqueID;
	}
	
	/**
	 * Getter for notes
	 * @return list of Model.Note
	 */
	public List<Note> getNotes(){
		ArrayList<Note> notes = new ArrayList<Note>();
		notes.addAll(this.notes.keySet());
		notes.removeAll(Collections.singleton(null));
		return notes;
	}
	
	public HashMap<Note,Boolean> getNotesMap(){
		return this.notes;
	}
	
	public List<Note> getNotes(boolean isPrompt){
		ArrayList<Note> notes = new ArrayList<Note>();
		for(Note n: this.notes.keySet()) {
			if(this.notes.get(n).booleanValue()==isPrompt)
				notes.add(n);
		}
		notes.removeAll(Collections.singleton(null));
		return notes;
	}

	public List<Note> getPromptNotes(){
		return getNotes(true);
	}

	public List<Note> getNonPromptNotes(){
		return getNotes(false);
	}
	
	/**
	 * Getter for prompt
	 * @return prompt for Model.Idea
	 */
	public String getPrompt() {
		return this.prompt;
	}
	
	/**
	 * Getter for finalNote
	 * @return finalNote
	 */
	public Note getFinalNote() {
		return this.finalNote;
	}
	
	/**
	 * 
	 * @return list of key words
	 */
	public List<String> getKeyWords(){
		List<String> keyWords = new ArrayList<String>();
		for(String kW: this.keyWords) {
			keyWords.add(kW);
		}
		return keyWords;
	}
	
	/**
	 * Setter for final note
	 * @param note
	 */
	public void setFinalNote(Note note) {
		if(notes==null)
			return;

		if(notes.get(note)==null){
			notes.put(note,false);

		}

		this.finalNote = note;
	}

	/*
	 * Add and remove methods for key word(s) and note(s)
	 */
	public void addNote(Note note) {
		if(note==null) return;
		if(note instanceof SubjectNote){
			this.subjectNotes.add( (SubjectNote) note);
			return;
		}
		this.notes.put(note,false);
	}
	
	public void addNote(Note note, boolean isPromptNote) {
		if(note==null) return;
		if(note instanceof SubjectNote){
			this.subjectNotes.add( (SubjectNote) note);
			return;
		}
		this.notes.put(note, isPromptNote);
	}

	public Note getNote(String path){
		for(Note note: this.notes.keySet()){
			if(note.getPath().toString().equals(note)){
				return note;
			}
		}
		return null;
	}
	
	public void removeNote(Note note) {
		if(note==null) return;
		if(this.finalNote!=null && this.finalNote.equals(note))
			this.finalNote = null;
		notes.remove(note);
		if(note instanceof SubjectNote){
			subjectNotes.remove(note);
		}
	}

	public void addNotes(Collection<Note> notes) {
		for(Note n: notes) {
			if(n!=null) this.addNote(n);
		}
	}

	public void addNotes(HashMap<Note, Boolean> notes) {
		for(Note n: notes.keySet()) {
			this.notes.put(n, notes.get(n));
		}
	}

	public void addKeyWord(String word) {
		this.keyWords.add(word);
	}
	
	public void addKeyWord(Collection<String>words) {
		this.keyWords.addAll(words);
	}
	
	public void removeKeyWord(String word) {
		this.keyWords.remove(word);
	}
	

	
	/**
	 * Checks if any of the notes within the collection are contained in this idea
	 * @param ns - collection of notes
	 * @return 
	 */
	public Boolean containsAny(Collection<Note> ns) {
		for(Note n: ns) {
			if(this.contains(n)) {
				return true;
			}
		}
		return false;
	}


	

	public PromptType getPromptType() {
		return promptType;
	}


	/**
	 * Checks if this.ID is equal to input ID
	 * @param ID
	 * @return 
	 */
	public boolean equalsID(String ID) {
		return ID.equals(this.uniqueID);
	}
	
	/**
	 * Creates Idea from an XML file
	 * @param idea: Found <Idea> ... </Idea> element
	 * @return Idea
	 * @throws IOException
	 */
	public static Idea fromXML(Element idea) throws IOException {
		String prompt = ((Element) idea.getElementsByTagName("Prompt").item(0)).getTextContent().trim() ;
		String promptType = ((Element) idea.getElementsByTagName("PromptType").item(0)).getTextContent().trim() ;
		String keyWords = ((Element) idea.getElementsByTagName("KeyWords").item(0)).getTextContent().trim() ;
		String id = ((Element) idea.getElementsByTagName("ID").item(0)).getTextContent().trim() ;
		Idea createdIdea = new Idea(prompt, (PromptType.valueOf(promptType)), id );
		createdIdea.addKeyWordsFromCSV(keyWords);
		
		NodeList nodes = idea.getElementsByTagName("Note");
		for(int i=0; i<nodes.getLength(); i++) {
			if(nodes.item(i) instanceof Element &&
					!((Element) nodes.item(i).getParentNode()).getTagName().equals("FinalNote") ) {
					//ignores note under Final Model.Note tag to avoid repeat notes
				boolean type = ((Element) nodes.item(i).getParentNode()).getTagName().equals("PromptNotes") ;
				
				if( ((Element) nodes.item(i)).getElementsByTagName("Text").getLength() > 0) {
					Element elem = (Element) ((Element) nodes.item(i)).getElementsByTagName("Text").item(0);
					Text text = Text.fromXML(elem);
					if(text!=null)
						createdIdea.addNote(text,type );
				}else if( ((Element) nodes.item(i)).getElementsByTagName("Image").getLength() > 0 ) {
					Element elem = (Element) ((Element) nodes.item(i)).getElementsByTagName("Image").item(0);
					Image image = Image.fromXML(elem);
					if(image!=null)
						createdIdea.addNote(image,type );
				}else if( ((Element) nodes.item(i)).getElementsByTagName("Book").getLength() > 0 ) {
					Element elem = (Element) ((Element) nodes.item(i)).getElementsByTagName("Book").item(0);
					Book book = Book.fromXML(elem);
					if (book != null)
						createdIdea.addNote(book,type );
				}else if(((Element) nodes.item(i)).getElementsByTagName("Subject").getLength() > 0 ){
					Element elem = (Element) ((Element) nodes.item(i)).getElementsByTagName("Subject").item(0);
					SubjectNote subject = SubjectNote.fromXML(elem);
					if(subject!=null){
						createdIdea.addNote(subject);
					}
				}
			}
		}
		
		Element finalNote = (Element) idea.getElementsByTagName("FinalNote").item(0);
		String path = (finalNote.getElementsByTagName("Path").getLength()>0 ? finalNote.getElementsByTagName("Path").item(0).getTextContent() : null);

		Note fN = createdIdea.getNote(path) ;
		createdIdea.setFinalNote(fN);
		
		return createdIdea;
	}
	
	/**
	 * Adds all key words given input in csv form
	 * @param commaSeperatedKeyWords e.g. Yellow,Round,Tennis
	 */
	private void addKeyWordsFromCSV(String commaSeperatedKeyWords) {
		for(String kW: commaSeperatedKeyWords.trim().split(",")) {
			if(kW.length()>0){
				this.addKeyWord(kW);
			}
		}
	}

	/**
	 * Convert Idea to XML
	 * @return xml content as string
	 */
	public String toXML() {
		String promptNotesXML = "";
		String nonPromptNotesXML = "";
		String subjectNotesXML = "";
		for(Note n: notes.keySet()) {
			if(n!=null && notes.get(n).booleanValue()==true)
				promptNotesXML += n.toXML() + System.lineSeparator();
			else if(n!=null)
				nonPromptNotesXML += n.toXML() + System.lineSeparator();
		}

		for(SubjectNote s: subjectNotes){
			subjectNotesXML += s.toXML() + System.lineSeparator();
		}



			
		String keyWordsXML = "";
		for(String k: keyWords) {
			if(k!=null)
				keyWordsXML += k + ",";
		}
		if(keyWords.size()>0)
			keyWordsXML = keyWordsXML.substring(0,keyWordsXML.length()-1);
		
		String finalNoteXML = "" ;
		
		if(this.finalNote!=null)
			finalNoteXML = finalNote.toXML();


		String xml = "<Idea>" + System.lineSeparator()
						+ "<Prompt>" + this.prompt + "</Prompt>" + System.lineSeparator()
						+ "<ID>"+this.uniqueID+"</ID>"+ System.lineSeparator()
						+ "<PromptType>" + this.promptType + "</PromptType>" + System.lineSeparator()
						+ "<KeyWords>" + keyWordsXML + "</KeyWords>" + System.lineSeparator()
						+ "<PromptNotes>" + System.lineSeparator() + promptNotesXML + "</PromptNotes>"  + System.lineSeparator()
						+ "<NonPromptNotes>" + System.lineSeparator() + nonPromptNotesXML + "</NonPromptNotes>"  + System.lineSeparator()
						+ "<SubjectNotes>" + System.lineSeparator() + subjectNotesXML + "</SubjectNotes>" + System.lineSeparator()
						+ "<FinalNote>"
						+ finalNoteXML
						+ "</FinalNote>" + System.lineSeparator() +
					"</Idea>";

		return xml;
		 
	}
		
	public String toString() {
		return ("Idea: " + this.prompt);
	}
    
	/**
	 * Constructor for Idea
	 * Key info: Generates a uniqueID given a null input 
	 * @param prompt
	 * @param promptType
	 * @param uniqueID
	 */
	public Idea(String prompt, PromptType promptType, String uniqueID) {
		this.subjectNotes = new ArrayList<>();
		notes = new HashMap<Note,Boolean>();
		this.promptType = promptType;
		this.finalNote = null;
		this.keyWords = new HashSet<String>();
		this.prompt = prompt;
		this.uniqueID = uniqueID;
		if(uniqueID==null)
			this.uniqueID = UUID.randomUUID().toString();
	}

	public List<SearchInformation> search(String search, int percentageSegment){
		SearchInformation ideaSearch = new SearchInformation(this.prompt,search,this,"Title",percentageSegment);
		SearchInformation keywordSearch = null;
		for(String keyword: this.getKeyWords()){
			if(keywordSearch==null)
				keywordSearch = new SearchInformation(keyword,search,null,"Key word",percentageSegment);
			keywordSearch.merge(new SearchInformation(keyword,search,null,"Key word", percentageSegment));
		}
		if(keywordSearch!=null)
			ideaSearch.merge(keywordSearch);

		ArrayList<SearchInformation> results = new ArrayList<>();
		results.add(ideaSearch);

		SearchInformation notesSearch = null;
		for(Note n: this.notes.keySet()){
			SearchInformation temp = (n instanceof Image ? n.search(search) : n.search(search,percentageSegment));
			if(notesSearch==null) {
				notesSearch = new SearchInformation(temp.score);
			}else {
				notesSearch.absorb(temp,0,true);
			}
			results.add(temp);
		}

		ideaSearch.absorb(notesSearch,this.notes.keySet().size(),true);

		return results;
	}

	public List<SearchInformation> search(String search){
		return search(search,10);

	}

	public boolean equals(Object object){
		return (object instanceof Idea) && ( (Idea) object).equalsID(this.uniqueID);
	}
	@Override
	public String getDisplayName() {
		return getPrompt();
	}

	@Override
	public String getMindMapName() {
		return getPrompt();
	}

	@Override
	public String getTreeName() {
		return getPrompt();
	}

	@Override
	public boolean contains(Object object) {
		return (object instanceof Note) && (this.getNotes().contains((Note) object) || this.subjectNotes.contains(object) );
	}


	private boolean containsPath(String path){

		for(Note n: notes.keySet()){
			if(n!=null && n.getPath().toString().equals(path)){
				return true;
			}
		}
		return false;
	}

	public boolean containsAny(List<String> notes){

		for(String n: notes){
			if(containsPath(n)){
				return true;
			}
		}

		return false;
	}



	public boolean isIdeaApartOfSubject(Subject subject){

		return subjectNotes.contains(new SubjectNote(subject)) || containsAny(subject.getNotePaths());

	}




}
