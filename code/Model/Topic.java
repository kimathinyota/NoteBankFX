package Code.Model;

import Code.View.ObservableObject;
import com.sun.tools.corba.se.idl.constExpr.Not;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class Topic implements ObservableObject {
	private List<Topic> subTopics;
	private List<Idea> ideas;
	private String name;

	public void setName(String name){
		this.name = name;
	}

	public String getID() {
		return uniqueID;
	}

	private String uniqueID;

	public Topic findTopic(String topicID){
		for(Topic n: this.getTopics()){
			if(n.getID().equals(topicID)){
				return n;
			}
		}
		return null;
	}

	public Topic findParent(Topic node){
		for(Topic n: this.subTopics){
			if(n.getID().equals(node.getID())){
				return this;
			}
			return n.findParent(node);
		}
		return null;
	}

	public Topic findParent(Idea node){
		for(Idea n: this.getIdeas()){
			if(n.getID().equals(node.getID())){
				return this;
			}
		}
		for(Topic n: this.subTopics){
			return n.findParent(node);
		}
		return null;
	}

	public Topic findTopic(Topic topic){
		return findTopic(topic.getID());
	}

	public Idea findIdea(String ideaID){
		for(Idea i: this.getAllIdeas()){
			if(i.getID().equals(ideaID)){
				return i;
			}
		}
		return null;
	}

	public Idea findIdea(Idea idea){
		return findIdea(idea.getID());
	}

	public String getName() {
		return this.name;
	}
	
	public List<Topic> getSubTopics(){
		return this.subTopics;
	}
	
	public List<Idea> getIdeas(){
		return this.ideas;
	}
	
	public List<Idea> getAllIdeas(){
		List<Idea> allIdeas = new ArrayList<Idea>();
		allIdeas.addAll(this.getIdeas());
		for(Topic a: subTopics) {
			allIdeas.addAll(a.getAllIdeas());
		}
		return allIdeas;
	}
	
	public void add(Topic topic) {
		subTopics.add(topic);
	}
	
	public void add(List<Idea> ideas) {
		this.ideas.addAll(ideas);
	}
	
	public void addAll(List<Topic> topics) {
		this.subTopics.addAll(topics);
	}
	
	public void add(Idea a) {
		this.ideas.add(a);
	}
	
	public void add(Object a) {
		if( a instanceof Idea)
			this.ideas.add( (Idea) a);
		if( a instanceof Topic)
			this.subTopics.add( (Topic) a  );
	}

	public int getNumberOfConnections(Note n){
		int j = 0;
		for(Idea i: ideas){
			if(i.contains(n)){
				j+=1;
			}
		}

		for(Topic t: subTopics){
			j += t.getNumberOfConnections(n);
		}

		return j;

	}
	
	public Topic(String name) {
		initialise(name,null,null,null);
	}

	private void initialise(String name, List<Idea>ideas, List<Topic>topics, String uniqueID){
		if(subTopics==null)
			subTopics = new ArrayList<Topic>();
		else
			subTopics = topics;
		if(ideas==null)
			this.ideas = new ArrayList<Idea>();
		else
			this.ideas = ideas;
		this.name = name;
		if(uniqueID==null)
			this.uniqueID = UUID.randomUUID().toString();
		else
			this.uniqueID = uniqueID;
	}

	public void initialise(String name, List<Idea>ideas, List<Topic>topics){
		if(ideas==null){
			ideas = this.ideas;
		}
		if(topics==null){
			topics = this.subTopics;
		}

		initialise(name,ideas,topics,uniqueID);
	}

	public Topic(String name, String uniqueID) {
		initialise(name,null,null,uniqueID);
	}

	public Topic(String name, List<Idea>ideas, List<Topic>topics, String uniqueID){
		initialise(name,ideas,topics,uniqueID);
	}

	public Topic(String name, List<Idea>ideas, List<Topic>topics){
		initialise(name,ideas,topics,null);
	}

	private static Topic fromXML(Element topic) throws IOException {
		String name = ((Element) topic.getElementsByTagName("Name").item(0)).getTextContent() ;
		String id = ((Element) topic.getElementsByTagName("ID").item(0)).getTextContent() ;
		Topic top = new Topic(name);
		NodeList nodes = topic.getElementsByTagName("Idea");
		for(int i=0; i<nodes.getLength(); i++) {
			if(nodes.item(i) instanceof Element) {
				Element elem = (Element) nodes.item(i);
				if(elem.getParentNode().equals(topic)) {
					top.add( Idea.fromXML(elem) );
				}	
			}	
		}
		nodes = topic.getElementsByTagName("Topic");
		
		for(int i=0; i<nodes.getLength(); i++) {
			if(nodes.item(i) instanceof Element) {
				Element elem = (Element) nodes.item(i);
				if(elem.getParentNode().equals(topic)) {
					top.add( Topic.fromXML(elem));
				}	
			}	
		}
		return top;
	}
	
	public static Topic fromXML(String loc) throws ParserConfigurationException, FileNotFoundException, SAXException, IOException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document document = builder.parse( new FileInputStream(loc) );
		Element rootElement = document.getDocumentElement();
		return Topic.fromXML(rootElement);
	}

	public List<Note> getAllNotes( ) {
		List<Note> notes = new ArrayList<Note>();
		for(Idea a: this.ideas ) {
			for(Note n: a.getNotes()) {
				if(n!=null) notes.add(n);
			}
		}
		
		for(Topic top: this.subTopics) {
			notes.addAll(  top.getAllNotes() );
		}
		
		return notes;
	}
	
	public Boolean contains(Note n) {
		return this.getAllNotes().contains(n);
	}
	
	public Boolean containsAny(Collection<Note> ns) {
		for(Note n: ns) {
			if(this.contains(n)) {
				return true;
			}
		}
		return false;
	}
	
	public List<Topic> getTopics(){
		List<Topic> topics = new ArrayList<Topic>();
		topics.add(this);
		for(Topic t: this.subTopics ) {
			topics.addAll(  t.getTopics()  );
		}
		return topics;
	}

	public Object delete(String uniqueID){
		for(Idea a: ideas){
			if(a.equalsID(uniqueID)){
				Idea temp = a;
				ideas.remove(a);
				return temp;
			}
		}
		for(Topic t: subTopics){
			if(t.getID().equals(uniqueID)){
				Topic temp = t;
				subTopics.remove(t);
				return temp;
			}
			return t.delete(uniqueID);
		}
		return null;
	}

	public Topic delete(Topic a) {

		Topic returnTopic = (getTopics().contains(a) ? a : null);

		this.subTopics.remove(a);
		for(Topic top: subTopics){
			top.delete(a);
		}

		return returnTopic;
	}
	
	public Idea delete(Idea a) {

		Idea returnIdea = (getIdeas().contains(a) ? a : null);


		this.ideas.remove(a);
		for(Topic top: subTopics){
			top.delete(a);
		}

		return returnIdea;
	}
	
	public void delete(Object a) {
		if( a instanceof Idea)
			this.ideas.remove( (Idea) a);
		if( a instanceof Topic)
			this.subTopics.remove( (Topic) a );
	}
	
	public void removeNote(Note note) {
		for(Idea a: this.ideas ) {
			a.removeNote(note);
		}
		
		for(Topic top: this.subTopics) {
			top.removeNote(note);
		}
	}

	public String toXML() {
		String xml = "<Topic>" + System.lineSeparator()
					+ "<Name>" + this.name + "</Name>" + System.lineSeparator()
					+ "<ID>"+this.uniqueID+"</ID>"+ System.lineSeparator();
			
			
		for(Idea a: this.ideas) {
			xml += a.toXML() + System.lineSeparator();
		}
				
		for(Topic a: this.subTopics)
			xml+=a.toXML() + System.lineSeparator();

		
		xml += "</Topic>";
		
		return xml;
	}

	public List<SearchInformation> search(String search){
		SearchInformation topicSearch = new SearchInformation(name,search,this,"Title",30);
		List<SearchInformation> completTopicSearch = new ArrayList<>();
		completTopicSearch.add(topicSearch);
		for(Idea idea: this.getIdeas()){
			List<SearchInformation> ideasSearch = idea.search(search);
			topicSearch.absorb(ideasSearch.get(0),2);
			completTopicSearch.addAll(ideasSearch);

		}

		for(Topic topic: this.subTopics){
			List<SearchInformation> topicsSearch = topic.search(search);
			topicSearch.absorb(topicsSearch.get(0),2);
			completTopicSearch.addAll(topicsSearch);
		}

		return completTopicSearch;

	}


	public List<Idea> getIdeas(Note note){
		List<Idea> ideas = new ArrayList<>();
		for(Idea i: this.getAllIdeas()){
			if(i.contains(note)){
				ideas.add(i);
			}
		}
		return ideas;
	}





	
	public String toString() {
		return ("Topic: " + name);
	}

	public boolean equals(Object object){
		return (object instanceof  Topic) && ( uniqueID.equals(((Idea) object).getID()) );
	}


	@Override
	public String getDisplayName() {
		return toString();
	}

	@Override
	public boolean contains(Object object) {
		return (object instanceof Note) && (contains((Note) object));
	}
}
