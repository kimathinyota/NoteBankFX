package Code.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchInformation implements Comparable<SearchInformation>{

    ArrayList<String> highlightWords;
    HashMap<String,String> segments;
    String reference;

    int score;

    Object searchObject;

    public String getMainSegment(){
        return segments.get(reference);
    }

    public String getReference(){
        return reference;
    }

    public Boolean searchFailed(){
        return (score>0);
    }

    public Map<String,String> getSegments(){
        return this.segments;
    }

    public void initialise(String input, String criteria, Object searchObject, String reference, int percentageSegment) {
        int matches = 0;
        String[] splitCriteria = criteria.split("\\s+");
        highlightWords = new ArrayList<>();
        segments = new HashMap<>();

        for (String word : splitCriteria) {
            if (input.contains(word)) {
                matches += 1;
                highlightWords.add(word);
            }
        }


        int largeSegment = 0;
        String largestSegment = "";

        int segment = 0;
        int noMatches = 0;
        for (int i = 0; i < splitCriteria.length; i++) {
            int j = 0;
            int largestSegmentFound = 0;
            String word = "";
            while (j < (splitCriteria.length - i)) {
                word += splitCriteria[i + j];
                noMatches +=1;
                if (!input.contains(word)) {
                    break;
                }
                segment += 1;
                largestSegmentFound += 1;
                word += " ";
                j += 1;
            }
            if (largestSegmentFound > largeSegment) {
                largeSegment = largestSegmentFound;
                for (int k = i; k < i + largestSegmentFound; k++) {
                    largestSegment += splitCriteria[k] + " ";
                }
                largestSegment = largestSegment.trim();
            }
        }


        int extraWords = Math.round(percentageSegment * input.length() / 100);

        int startSegment = input.indexOf(largestSegment);



        if (startSegment != -1){
            int endSegment = startSegment + extraWords + largestSegment.length();
            endSegment = (endSegment > input.length() ? input.length() : endSegment);
            segments.put(reference, input.substring(startSegment, endSegment));
        }



        int containsWord = (input.contains(criteria) ? 500 : 1) ;
        int matchesWord = (input.equals(criteria) ? 1500 : 1) ;


        float percentage = (segment*noMatches)/(noMatches*splitCriteria.length);

        score = (containsWord + matchesWord)/2;

        score = Math.round(percentage*score);

        this.searchObject = searchObject;
        this.reference = reference;


        //System.out.println("Score for " + searchObject + " is " + score);



    }


    static final int MINIMUM = 50;

    public SearchInformation(String input, String criteria, Object searchObject, String reference, int percentageSegment){
        initialise(input,criteria,searchObject, reference, percentageSegment);
    }

    public SearchInformation(String input, String criteria, Object searchObject, String reference){
        initialise(input,criteria,searchObject, reference, 25);
    }

    public void absorb(SearchInformation child, int distanceRemoved){
        absorb(child,distanceRemoved,false);
    }

    public void absorb(SearchInformation child, int distanceRemoved, boolean dontAddSegments){
        this.score += child.getScore()/(distanceRemoved+1);
        if(!dontAddSegments)
            segments.putAll(child.getSegments());
    }

    public SearchInformation(int score){
        this.score = score;
    }

    public Object getSearchObject(){
        return this.searchObject;
    }

    public void merge(SearchInformation peer){
        absorb(peer,0);
    }

    public int getScore(){
        return score;
    }

    @Override
    public int compareTo(SearchInformation o) {
        return this.getScore() - o.getScore();
    }

}
