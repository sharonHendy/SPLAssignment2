package bgu.spl.mics.application.objects;

import java.util.ArrayList;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    private ArrayList<Model> models;

    public Student(String name, String department, Degree status,ArrayList<Model> models){
        this.name = name;
        this.department = department;
        this.status = status;
        publications = 0;
        papersRead = 0;
        this.models= models;
    }

    public synchronized void handelPublishConference(ArrayList<Model> models){ //todo synchronized needed????????
        for(Model m:models){
            if(models.contains(m)){
                publications++;
            }
            else{
                papersRead++;
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public Degree getStatus() {
        return status;
    }

    public int getPublications() {
        return publications;
    }

    public int getPapersRead() {
        return papersRead;
    }

    public ArrayList<Model> getModels() {
        return models;
    }

    public void setPublications() {
        publications++;
    }

    public void setPapersRead() {
        papersRead++;
    }
}
