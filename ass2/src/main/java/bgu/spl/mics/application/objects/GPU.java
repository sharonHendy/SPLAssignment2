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
    private int MaxNumOfProcessedBatches;
    private boolean isTraining;
    private int ticksUntilDone;
    private int currTick;
    private Collection<DataBatch> unprocessedDBs;
    private Collection<DataBatch> processedDBs;
    int numOfTrainedDBs; //number of data batches trained so far
    int numOfTotalDBs; //total number of data batches
    int totalTimeUnitsUsed; //for statistics



    GPU(Type type, Model model, Cluster cluster){
        this.type = type;
        this.model = model;
        this.cluster = cluster;
        switch (this.type){//TODO if
            case RTX3090 -> { MaxNumOfProcessedBatches = 32;}
            case RTX2080 -> { MaxNumOfProcessedBatches = 16;}
            case GTX1080 -> { MaxNumOfProcessedBatches = 8;}
        }
        isTraining = false;
        currTick = 0;
        numOfProcessedDBs = 0;
    }

    public GPU(Model m){
        model=m;
    }

    public int getCurrTick() {
        return currTick;
    }

    /**
     *the GPUService will call this method when it receives a tickBroadcast from the messageBus.
     *@post: @currTick - @pre currTick == 1
     */
    void updateTick(){
        currTick = currTick + 1;
        doneTraining();
    }
    /**
     * splits the data to dataBatches.
     *
     */
    void prepareDataBatches(){
        numOfUnprocessedDBs = model.getData().getSize();
        while(numOfProcessedDBs != MaxNumOfProcessedBatches){

        }
    }

    /**
     * sends unprocessed data batches to the cluster.
     * will only send if it has enough room to receive them.
     */
    void sendDataBatchesToCluster(){

    }

    /**
     * receives processed data batches from the cluster.
     * waits for messages.
     */
    void receiveDataBatchFromCluster(){

    }

    /**
     * starts to train a processed data batch.
     */
    void startTraining(){

    }

    /**
     * checks if the ticksUntilDone is equal to the currTick, if so starts the training of another batch.
     */
    void doneTraining(){

    }

    /**
     * notifies the GPUService that it finished training the model.
     */
    void complete(){

    }

    public Model getModel() {
        return model;
    }

    public Type getType() {
        return type;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public int getMaxNumOfProcessedBatches() {
        return MaxNumOfProcessedBatches;
    }

    public boolean isTraining() {
        return isTraining;
    }

    public int getTicksUntilDone() {
        return ticksUntilDone;
    }

    public Collection<DataBatch> getUnprocessedDBs() {
        return unprocessedDBs;
    }

    public Collection<DataBatch> getProcessedDBs() {
        return processedDBs;
    }


    public int getNumOfTrainedDBs() {
        return numOfTrainedDBs;
    }

    public int getNumOfTotalDBs() {
        return numOfTotalDBs;
    }

    public int getTotalTimeUnitsUsed() {
        return totalTimeUnitsUsed;
    }




}
