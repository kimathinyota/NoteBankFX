package Code.Model;

import com.sun.applet2.AppletParameters;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.w3c.dom.Element;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * Book
 * Class used to represent notes as PDF files 
 */
public class Book extends Note {
	
	private Book globalBook;

	/**
	 * gives total number of pages in PDF file
	 */
	private Integer numberOfPages = null;


	/**
	 * Set specific pages of PDF file e.g. pages 1,3,5,7 
	 */
	private String specifyPages;
	


	/**
	 * loadPage: This returns page as rendered image. Non-zero indexed.
	 * @param pageNumber
	 * @return Page as image of type BufferedImage
	 * @throws IOException 
	 */


	/*volatile*/ LinkedHashMap<Integer,BufferedImage> pageToImage;


	private boolean isBetween(int n, int startIndex, int endIndex){

		boolean a =  (startIndex < endIndex && ( n >= startIndex && n <= endIndex)) ||
				(startIndex > endIndex &&  (n >= startIndex || n <= endIndex) ) ||
				(startIndex == endIndex && n==startIndex);

		return a;

	}


	public void loadImages(int startIndex, int endIndex) throws IOException{

		for(int i=0; i<getPages().size(); i++){
			if(isBetween(i,startIndex,endIndex)){
				loadPage(getPages().get(i)-1);
			}
		}
	}


	PDDocument document;
	PDFRenderer pdfRenderer;

	public synchronized BufferedImage loadPage(int pageNumber) throws IOException {

		List<Integer> keySet = new ArrayList<>(pageToImage.keySet());
		if(pageToImage.containsKey(pageNumber)){
			return pageToImage.get(pageNumber);
		}

		if(document==null)
			document =  PDDocument.load(new File(this.getPath().toString()), MemoryUsageSetting.setupTempFileOnly());

		if(pdfRenderer==null){
			pdfRenderer = new PDFRenderer(document);
			pdfRenderer.setSubsamplingAllowed(true);
		}

		BufferedImage bim = pdfRenderer.renderImageWithDPI(pageNumber, 300, ImageType.RGB);
		//document.close();


		return bim;

	}


	private int getNumberOfPages() throws IOException{

		if(document==null)
			document =  PDDocument.load(new File(this.getPath().toString()), MemoryUsageSetting.setupTempFileOnly());

		//PDDocument document = PDDocument.load(new File(this.getPath().toString()));
		this.numberOfPages = document.getNumberOfPages();
		//document.close();
		return numberOfPages;
	}


	public Integer getMaximumNumberOfPages(){
		return numberOfPages;
	}

	public static String getSpecifyPages(int numberOfPages){
		String pages = "";
		for(int i=1; i<numberOfPages+1; i++){
			pages += i + ",";
		}
		pages = (pages.length()>0 ? pages.substring(0,pages.length()-1) : "");
		return pages;
	}

	String pathToNotesDirectory;

	public Book(String name, String originalFilePath, String pathToNotesDirectory) throws IOException{
		super(name,originalFilePath,pathToNotesDirectory,NoteType.Book);
		this.numberOfPages = getNumberOfPages();
		this.specifyPages = getSpecifyPages(numberOfPages);
		pageToImage = new LinkedHashMap<>();
	}

	public void stopViewing(){
		pageToImage.clear();

		try{
			if(document==null)
				document.close();
			document=null;

		}catch (IOException e){
			e.printStackTrace();
		}
	}

	public /*synchronized*/ boolean isLoaded(int page){
		return pageToImage.containsKey(page);
	}

	public void onlyKeepPages(int startIndex, int endIndex){
		List<Integer> pages = getPages();
		for(int i=0; i<pages.size(); i++ ){
			if( !(isBetween(i,startIndex,endIndex)) ) {
				pageToImage.remove( Integer.valueOf(pages.get(i)-1));
			}
		}
	}

	public Book(String path, String specifyPages) throws IOException{
		super(path,NoteType.Book );
		if(!new File(path).exists()) {
			throw new IOException();
		}
		this.specifyPages = (specifyPages.contains("null") ? null : specifyPages);
		pageToImage = new LinkedHashMap<>();
	}

	public Book(String path) throws IOException{
		super(path,NoteType.Book );

		if(!new File(path).exists()) {
			throw new IOException();
		}
		this.specifyPages = null;
		pageToImage = new LinkedHashMap<>();
	}

	public boolean isSetUpForViewing(){
		return specifyPages!=null;
	}



	public void setSpecifyPages(int numberOfPages){
		this.numberOfPages = numberOfPages;
		if(specifyPages==null){
			this.specifyPages = getSpecifyPages(numberOfPages);
		}
	}


	public void setupForViewing(){
		try {

			if(specifyPages==null){

				this.numberOfPages = getNumberOfPages();
				this.specifyPages = getSpecifyPages(numberOfPages);

			}
		}catch (IOException e){

		}
	}



	public List<Integer> getPages(){
		setupForViewing();
		return getIntegers( specifyPages  );
	}


	/**
	 * Converts [1,2,3,5,6,7,8,9] to 1,2,3,5,6,7,8,9
	 * @param input
	 * @return
	 */
	public static String getPageRanges(List<Integer>input){
		String range = "";
		for(Integer page: input ){
			range += page.toString() +",";
		}
		range = (!range.equals("") ? range.substring(0,range.length()-1) : "");
		return range;
	}

	/**
	 * Returns all text from PDF
	 * @return
	 * @throws IOException
	 */
	public String getSource() throws IOException{
		PDFParser parser = new PDFParser(new RandomAccessBufferedFileInputStream(new File(this.getPath().toString())));
		parser.parse();
		try (COSDocument cosDoc = parser.getDocument()) {
			PDFTextStripper pdfStripper = new PDFTextStripper();
			PDDocument pdDoc = new PDDocument(cosDoc);
			pdfStripper.setStartPage(1);
			pdfStripper.setEndPage(getNumberOfPages());
			String parsedText = pdfStripper.getText(pdDoc);

			pdDoc.close();
			return parsedText;
		}catch (IOException e){

		}
		return "";
	}

	public void setPageRange(String range){
		this.specifyPages = range;
	}

	/**
	 * Creates book from xml file
	 * @param book: Found <Book> .. </Book> element
	 * @return Book
	 * @throws IOException
	 */
	public static Book fromXML(Element book) throws IOException {
		String path = ((Element) book.getElementsByTagName("Path").item(0)).getTextContent() ;
		String pages = ((Element) book.getElementsByTagName("Pages").item(0)).getTextContent() ;

		return new Book(path,pages);
	}
	
	/**
	 * Converts a specifyPages string to a list of Integers
	 * e.g. 1,2,3,4 to a list [1,2,3,4]
	 * @param pages
	 * @return List<Integer> list of integers
	 */
	public static List<Integer> getIntegers(String pages){

		if(pages.isEmpty() || pages.contains("null")){
			return new ArrayList<>();
		}
		pages = pages.trim();

		pages = pages.replaceAll("\\(","");
		pages = pages.replaceAll("\\)","");


		List<String> integerList = Arrays.asList(pages.split(","));
		List<Integer> intList = new ArrayList<Integer>();


		for(String a: integerList) {
			intList.add( Integer.valueOf(a) );
		}
		return intList;

	}

	@Override
	public String getMindMapName() {
		return "Book: " + getName();
	}

	@Override
	public String getTreeName() {
		return "Book: " + getName();
	}

	/**
	 * Convert a book into xml
	 */
	public String toXML() {

		String xml = "<Note>"
  						+ "<Book>"
  							+ "<Path>" + this.getPath() + "</Path>"
  							+ "<Pages>" + this.specifyPages + "</Pages>"
  						+ "</Book>" +
  					"</Note>";
		return xml;
	}

	public SearchInformation search(String searchInput) {

		try{
			SearchInformation title =  (new SearchInformation(this.getName(),searchInput,this,"Title"));
			title.merge(new SearchInformation(getSource(),searchInput,this,"Text"));

			return (title.getScore()>0 ? title : null);
		}catch (IOException e){

		}
		return null;

	}

	public SearchInformation search(String searchInput, int segmentPercentage) {

		try{
			SearchInformation title =  (new SearchInformation(this.getName(),searchInput,this,"Title"));
			title.merge(new SearchInformation(getSource().replaceAll("\n"," "),searchInput,this,"Text", segmentPercentage));

			return (title.getScore()>0 ? title : null);
		}catch (IOException e){

		}
		return null;
	}

	public int getNumberOfImages() {
		return this.numberOfPages;
	}

	/**
	 * Converts [1,2,3,5,7,8,9,12,13,14,18,19,20] to 1-3,5,7-9,12-14,18-20
	 * @param specifyPages
	 * @return
	 */
	public static String displayName(String specifyPages){

		if(specifyPages==null || specifyPages.equals(""))
			return "";
		List<Integer> sorted = getIntegers(specifyPages);

		return displayName(sorted);
	}


	public static String displayName(List<Integer> list){

		if(list.isEmpty()){
			return null;
		}

		List<Integer> sorted = list;
		Collections.sort(sorted);

		String temp = (sorted.size()>0 ? sorted.get(0).toString() : "");

		int k = 0;

		for(int i=0; i<sorted.size(); i++){
			if( (sorted.size()-1)==i || !sorted.get(i).equals(sorted.get(i+1)-1)    ){
				temp += (k!=0 ? "-"+sorted.get(i) + "," : ",")  +  ((sorted.size()-1)==i ? "" : sorted.get(i+1) );
				k = 0;
			}else{
				k+=1;
			}
		}

		temp = temp.substring(0,temp.length()-1);

		return temp;
	}


	public String toString() {
		String ext = "";
		if(this.specifyPages!=null){
			ext = displayName(this.specifyPages);
			ext = " (" +  (ext) + ")";
		}
		return ("Book: " + this.getName().replaceAll("-"," ") + ext);
	}


}
