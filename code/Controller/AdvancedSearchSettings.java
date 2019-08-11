package Code.Controller;

/**
 * Class designed to handle advanced searching
 */
public class AdvancedSearchSettings {

    public boolean isSearchInPage() {
        return searchInPage;
    }

    public void setSearchInPage(boolean searchInPage) {
        this.searchInPage = searchInPage;
    }

    public boolean isIncludeImages() {
        return includeImages;
    }

    public void setIncludeImages(boolean includeImages) {
        this.includeImages = includeImages;
    }

    public boolean isIncludeTexts() {
        return includeTexts;
    }

    public void setIncludeTexts(boolean includeTexts) {
        this.includeTexts = includeTexts;
    }

    public boolean isIncludeBooks() {
        return includeBooks;
    }

    public void setIncludeBooks(boolean includeBooks) {
        this.includeBooks = includeBooks;
    }

    public boolean isIncludeIdeas() {
        return includeIdeas;
    }

    public void setIncludeIdeas(boolean includeIdeas) {
        this.includeIdeas = includeIdeas;
    }

    public boolean isIncludeStudyPlans() {
        return includeStudyPlans;
    }

    public void setIncludeStudyPlans(boolean includeStudyPlans) {
        this.includeStudyPlans = includeStudyPlans;
    }

    boolean searchInPage;
    boolean includeImages;
    boolean includeTexts;
    boolean includeBooks;
    boolean includeIdeas;
    boolean includeStudyPlans;

    public void setDefault(){
        this.searchInPage = false;
        this.includeBooks = true;
        this.includeImages = true;
        this.includeTexts = true;
        this.includeIdeas = true;
        this.includeStudyPlans = true;
    }

    public AdvancedSearchSettings(){
        setDefault();
    }
}
