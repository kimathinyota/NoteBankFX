package Code.Model;

import org.jsoup.Jsoup;
import org.w3c.dom.Element;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Text extends Note {

	private void writeToFile(String textContent) throws IOException {
		FileWriter storeFile = new FileWriter(new File(this.getPath().toString()));
		storeFile.write(textContent);
		storeFile.close();
	}

	public Text(String name, String textContent, String pathToSaveDirectory) throws IOException {
		super(pathToSaveDirectory + File.separator + name.replaceAll(" ","-") + ".txt",NoteType.Text);
		this.writeToFile(textContent);
	}

	public String toString(){
		return "Text: " + getName();
	}

	public Text(String path) throws IOException {
		super(path,NoteType.Text);
	}

	public void setTextContent(String newContent) throws IOException {
		this.writeToFile(newContent);
	}

	public String loadContent() {
		try{
			String textContent = "";
			File file = new File(this.getPath().toString().trim());
			Scanner fileScanner = new Scanner(new File(this.getPath().toString()));
			while(fileScanner.hasNextLine())
				textContent += fileScanner.nextLine() + "\n";

			fileScanner.close();
			return textContent;
		}catch (IOException e){
			return "";
		}
	}

	public String getText(){
		return Jsoup.parse(loadContent()).text();
	}

	public static Text fromXML(Element text) throws IOException {
		String path = ((Element) text.getElementsByTagName("Path").item(0)).getTextContent() ;

		return new Text(path);
	}

	public String toXML() {
		String xml = "<Note>"
						+ "<Text>"
							+ "<Path>" + this.getPath() + "</Path>"
						+ "</Text>" +
			        "</Note>" ;
		
		return xml;
	}




	@Override
	public SearchInformation search(String searchInput) {

		SearchInformation title =  (new SearchInformation(this.getName(),searchInput,this,"Title"));
		title.merge(new SearchInformation(getText().replaceAll("\n"," "),searchInput,this,"Text"));

		return title;

	}

	public SearchInformation search(String searchInput, int percentageSegment) {

		SearchInformation title =  (new SearchInformation(this.getName(),searchInput,this,"Title"));
		title.merge(new SearchInformation(getText().replaceAll("\n"," "),searchInput,this,"Text",percentageSegment));

		return title;

	}


}

