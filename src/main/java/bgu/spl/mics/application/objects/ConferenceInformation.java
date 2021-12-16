package bgu.spl.mics.application.objects;

import java.util.ArrayList;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConferenceInformation {

    private String name;
    private int date;
    private ArrayList<Model> models;

    public ConferenceInformation(String name, int date){
        this.name= name;
        this.date= date;
        models= new ArrayList<>();
    }

    public void addModel(Model model){ //synchronized?????????
        if(model.getResult()== Model.Result.Good){
            models.add(model);
        }
    }

    public ArrayList<Model> getModels() {
        return models;
    }

    public String getName() {
        return name;
    }

    public int getDate() {
        return date;
    }
}
