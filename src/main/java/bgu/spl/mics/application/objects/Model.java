package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {


    public Status getStatus() {
        return status;
    }

    public enum Status {PreTrained, Training, Trained, Tested}
    public enum Result {None, Good, Bad}

    private String name;
    private Data data;
    private Student student;
    private Status status;
    private Result result;

    public Model(String name, Data data){
        this.name = name;
        this.data = data;
        this.status = Status.PreTrained;
        this.result = Result.None;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Data getData() {
        return data;
    }

    public Result getResult() {
        return result;
    }

    public String getName() {
        return name;
    }

    public Student getStudent() {
        return student;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}