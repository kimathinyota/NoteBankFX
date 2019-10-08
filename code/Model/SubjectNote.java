package Code.Model;

import org.w3c.dom.Element;

import java.io.IOException;

public class SubjectNote extends Note {

    String subject;

    @Override
    public String toXML() {
        String xml = "<Note>"
                    + "<Subject>" +
                        "<Name>" + subject + "</Name>"
                    + "</Subject>" +
                    "</Note>" ;
        return xml;
    }

    public static SubjectNote fromXML(Element subject) throws IOException {
        String name = ((Element) subject.getElementsByTagName("Name").item(0)).getTextContent() ;

        return new SubjectNote(name);
    }

    /**
     * Override hash code to match equals
     */
    @Override
    public int hashCode() {
        return subject.hashCode();
    }

    @Override
    public SearchInformation search(String searchInput) {
        return null;
    }

    @Override
    public SearchInformation search(String searchInput, int percentageSegment) {
        return null;
    }

    public boolean memberOfSubject(String subject){
        return this.subject.equals(subject);
    }

    public boolean equals(Object note){
        return (note instanceof SubjectNote) && ((SubjectNote) note).getName().equals(subject);
    }


    public SubjectNote(String subject){
        super(NoteType.Subject);
        this.subject = subject;
    }


}
