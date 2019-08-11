package Code.Model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public abstract class Note {

	private StringBuilder path;
	private NoteType type;


	/**
	 * Getter for name
	 * @return 
	 */

	public String getName(){
		return getName(path.toString());
	}

	public static String getName(String path) {

		String name =  new File(path.toString()).getName();

		name = name.substring(0,name.lastIndexOf('.'));

		return name.replaceAll("-"," ");

	}



	private String getFileExtension(String path) {

		String p = new File(path.toString()).getName();
		return p.substring(p.lastIndexOf('.') + 1);

	}

	/**
	 * Getter for type
	 * @return
	 */
	public String getType() {
		return type.toString();
	}

	public void moveFile(String newLocation, String name){
		this.path = new StringBuilder(newLocation);
		new File(path.toString()).renameTo(new File(newLocation + File.separator + name));
	}


	public static String getPath(String name, String pathToNotesDirectory, String fileExtension){
		return pathToNotesDirectory + File.separator + name.trim().replaceAll(" ","-") + "." + fileExtension;
	}

	public static String getPath(String fileNameExtension, String pathToNotesDirectory){
		return pathToNotesDirectory + File.separator + fileNameExtension.trim().replaceAll(" ","-");
	}

	/**
	 *
	 * @param name
	 * @param pathToNotesDirectory
	 * @param type
	 */
	public Note(String name, String originalFilePath, String pathToNotesDirectory, NoteType type) throws IOException {
		if(!new File(originalFilePath).exists())
			throw new IOException();

		String fileExtension = getFileExtension(originalFilePath);
		this.path = new StringBuilder(getPath(name,pathToNotesDirectory,fileExtension));

		this.type = type;


		File directory = new File(pathToNotesDirectory);
		if(!directory.exists())
			directory.mkdir();

		File dest = new File(path.toString() );

	    Files.copy(new File(originalFilePath).toPath(),dest.toPath() , StandardCopyOption.REPLACE_EXISTING);
	}

	public Note(String path, NoteType type) throws IOException{
		this.path = new StringBuilder(path);
		this.type = type;
	}


	public NoteType getNoteType(){
		return this.type;
	}


	public Note(NoteType type){
		this.path = null;
		this.type = type;
	}







	/**
	 * Deletes note: Existing file will be removed
	 */
	public void delete() {
		new File(path.toString()).delete();
	}

	/**
	 * Converts note to XML
	 * @return 
	 */
	public abstract String toXML();



	public StringBuilder getPath(){
		return this.path;
	}


	
	/**
	 * Object is equal if they share the same file path
	 */
	@Override
	public boolean equals(Object n) {
		return (n instanceof Note) && this.path.equals( ((Note) n).getPath() );
	}

	/**
	 * Override hash code to match equals
	 */
	@Override
	public int hashCode() {
		return path.hashCode();
	}


	public abstract SearchInformation search(String searchInput);
	public abstract SearchInformation search(String searchInput, int percentageSegment);

}
