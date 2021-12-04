package bgu.spl.mics.application.objects;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

import java.util.Collection;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    private Model model;
    private Cluster cluster;
    private Collection<DataBatch> unprocessed;
    private Collection<DataBatch> processed;
    private boolean complete;
    private int totalNumOfBatches;
    private int currNumOfBatches;
    private int numOfProcessedBatchesFromCPU;
    private int numOfProcessedBatchesFromGPU;
    private int timeUnitsUsed; //for the statistics in cluster


    public void sendBatch(){

    }

    public void receiveBatch(){

    }

    public void GPUprocessBatch(){

    }

    public void complete(Event e, Model m){

    }
    public void UpdateResults(Model m, Student s){

    }

    public Type getType(){
        return type;
    }

    public Model returnModel(){
        return model;
    }
    public boolean isDone(){
        return complete;
    }


}
