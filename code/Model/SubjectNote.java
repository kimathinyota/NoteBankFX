package Code.Model;

public class SubjectNote extends Note {

    String subject;

    @Override
    public String toXML() {
        return "";
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
        return this.subject.split("\\s+")[1].equals(subject);
    }

    public SubjectNote(String subject){
        super(NoteType.Text);
    }

}
