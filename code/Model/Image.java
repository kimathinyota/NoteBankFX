package Code.Model;

import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Image
 * Class used to represent notes as Model.Image files
 */
public class Image extends Note{

	/**
	 *
	 * @param name
	 * @param originalPath
	 * @param notesDirectory
	 * @throws IOException
	 */
	public Image(String name, String originalPath, String notesDirectory) throws IOException{
		super(name,originalPath,notesDirectory,NoteType.Image);
	}

	public Image(String path) throws IOException{
		super(path,NoteType.Image);
		if(!new File(path).exists()) {
			throw new IOException();
		}
	}

	public static Image fromXML(Element image){

		String path = ((Element) image.getElementsByTagName("Path").item(0)).getTextContent() ;

		try {
			return new Image(path);
		}catch (IOException e){
		}

		return null;
	}


	public BufferedImage loadImage(){
		try {
			return ImageIO.read(new File(this.getPath().toString()));
		}catch (IOException e){

		}
		return null;
	}


	/**
	 * Convert Model.Image to XML
	 * @return xml content as string
	 */
	public String toXML() {
		String xml = "<Note>"
				  		+ "<Image>"
        						+ "<Path>" + getPath().toString() + "</Path>"
						+ "</Image>"  +
					 "</Note>";
		return xml;
	}

	@Override
	public SearchInformation search(String searchInput) {
		return new SearchInformation(this.getName(),searchInput,this,"Title");
	}

	public SearchInformation search(String searchInput, int percentageSegment) {
		return new SearchInformation(this.getName(),searchInput,this,"Title",percentageSegment);
	}


	public String toString(){
		return "Image: "+getName();
	}

}
