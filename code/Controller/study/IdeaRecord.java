package Code.Controller.study;

import Code.Model.Idea;
import Code.Model.IdeaQuiz;
import Code.Model.Quizzes;

import java.util.HashMap;
import java.util.List;

public class IdeaRecord{
    private String id;

    public String getPrompt() {
        return prompt;
    }

    private String prompt;
    private String frequency, total;
    private String readiness;
    private String score;

    private HashMap<String, List<IdeaQuiz>> ideaQuizzes;

    public void setIdeaQuizzesByDate(HashMap<String, List<IdeaQuiz>> ideaQuizzes){
        this.ideaQuizzes = ideaQuizzes;
    }


    public void setTimeFrequency(String time){
        this.frequency = ""+(ideaQuizzes.get(time)).size();
    }

    public void setTimeLength(String time){
        this.total = ""+Quizzes.totalTime(ideaQuizzes.get(time));
    }


    public IdeaRecord(String prompt, String readiness, String score){

        this.prompt = prompt;
        this.readiness = readiness;
        this.score = score;

    }


    public boolean equalsID(Idea idea){
        return id.equals(idea.getID());
    }


    public void setReadiness(String readiness){
        this.readiness = readiness;
    }

    public String getReadiness(){
        return readiness;
    }

    public String getFrequency(){
        return frequency;
    }

    public String getTotal() {
        return total;
    }

    public String getScore(){
        return score;
    }


}