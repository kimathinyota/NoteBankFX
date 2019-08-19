package Code.Controller;

import Code.Model.Note;
import Code.Model.NoteType;
import Code.Model.SearchInformation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * Designed to store Filter information on notes, and can be used to apply a set of filters to notes
 */
public class FilterSettings {

    SortBy sortBy;
    View view;
    Order order;
    String search;
    boolean includeImages;
    boolean includeBooks;
    boolean includeTexts;


    /**
     * Uses SortBy and Order information to build a comparator
     * @return
     */
    public Comparator<Note> getComparator(){
        return new Comparator<Note>(){

            @Override
            public int compare(Note o1, Note o2) {

                Path file1 = new File(o1.getPath().toString()).toPath();
                Path file2 = new File(o2.getPath().toString()).toPath();

                try{
                    BasicFileAttributes attr1 = Files.readAttributes(file1,BasicFileAttributes.class);
                    BasicFileAttributes attr2 = Files.readAttributes(file2,BasicFileAttributes.class);


                    if(sortBy.equals(SortBy.Date) && order.equals(Order.Desc)){

                        return attr1.creationTime().compareTo(attr2.creationTime());
                    }

                    if(sortBy.equals(SortBy.Date) && order.equals(Order.Asc)){

                        return attr2.creationTime().compareTo(attr1.creationTime());
                    }

                }catch (IOException e){

                }

                if(sortBy.equals(SortBy.Name) && order.equals(Order.Desc)){
                    return o2.getName().compareTo(o1.getName());
                }

                return o1.getName().compareTo(o2.getName());

            }
        };
    }

    /**
     * Will use includeBooks, includeImages, and includeTexts to filter input list of notes
     * @param notes
     * @return
     */
    private List<Note> filter(List<Note>notes){
        List<Note> after = new ArrayList<>();
        for(Note n: notes){
            if( (search.equals("") || n.getName().toLowerCase().contains(search.toLowerCase()))  &&  ((n.getNoteType().equals(NoteType.Image) && includeImages) ||
                 (n.getNoteType().equals(NoteType.Book) && includeBooks) ||
                 (n.getNoteType().equals(NoteType.Text) && includeTexts) )){
                after.add(n);
            }
        }
        return after;
    }


    /**
     * Will filter and sort notes based on current information
     * @param notes
     * @return
     */
    public List<Note> applyFilters(List<Note> notes){
        List<Note> f = filter(notes);
        f.sort(getComparator());
        return f;
    }


    /**
     * Getters and setters
     * @return
     */
    public SortBy getSortBy() {
        return sortBy;
    }

    public void setSortBy(SortBy sortBy) {
        this.sortBy = sortBy;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public boolean isIncludeImages() {
        return includeImages;
    }

    public void setIncludeImages(boolean includeImages) {
        this.includeImages = includeImages;
    }

    public boolean isIncludeBooks() {
        return includeBooks;
    }

    public void setIncludeBooks(boolean includeBooks) {
        this.includeBooks = includeBooks;
    }

    public boolean isIncludeTexts() {
        return includeTexts;
    }

    public void setIncludeTexts(boolean includeTexts) {
        this.includeTexts = includeTexts;
    }

    public void setSearch(String search){
        this.search = search;
    }

    public String getSearch(){
        return this.search;
    }

    /**
     * Set defaultOptions
     */
    public void defaultOptions(){
        sortBy = SortBy.Name;
        view = View.Icons;
        order = Order.Asc;
        includeImages = true;
        includeBooks = true;
        includeTexts = true;
        search = "";

    }

    public FilterSettings(){
        defaultOptions();
    }










}
