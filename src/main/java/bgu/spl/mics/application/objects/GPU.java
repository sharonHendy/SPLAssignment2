package bgu.spl.mics.application.objects;

import com.sun.org.apache.xpath.internal.operations.Mod;
import javafx.print.Collation;

import java.util.ArrayList;
import java.util.Collection;

import static bgu.spl.mics.application.objects.GPU.Type.*;

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
    private  Cluster cluster;
    private int MaxNumOfProcessedBatches;

    private boolean isTraining;
    private int ticksUntilDone; //number of ticks it takes to train the current batch
    private int currTick;
    private Collection<DataBatch> unprocessedDBs; //batches before processing
    private Collection<DataBatch> processedDBs; //batches after processing
    int numOfTrainedDBs; //number of data batches trained so far
    int numOfTotalDBs; //total number of data batches
    int totalTimeUnitsUsed; //for statistics
    private DataBatch currDBInTraining; //current DB the GPU trains




    GPU(Type type, Model model, Cluster cluster){
        this.type = type;
        this.model = model;
        this.cluster = cluster;
        unprocessedDBs = new ArrayList<>();
        processedDBs = new ArrayList<>();

        if (this.type == RTX3090){
            MaxNumOfProcessedBatches = 32;
        }else if(this.type == RTX2080){
            MaxNumOfProcessedBatches = 16;
        }else if(this.type == GTX1080){
            MaxNumOfProcessedBatches = 8;
        }

        isTraining = false;
        currTick = 0;
        currDBInTraining = null;
        prepareDataBatches();
    }

    GPU(Model model){
        this.model = model;
    }

    /**
     * tests the model.
     * @pre: model.getResult() == None
     * @post: model.getResult() == Good | Bad
     */
    void testModel(){

    }

    /**
     *the GPUService will call this method when it receives a tickBroadcast from the messageBus.
     *@post: @currTick - @pre:currTick == 1
     */
    void updateTick(){
        currTick = currTick + 1;
        doneTraining();
    }

    /**
     * splits the data to dataBatches.
     * @post: numOfTotalDBs == model.getData().getSize()
     */
    void prepareDataBatches(){
        numOfTotalDBs = model.getData().getSize()/1000;
        //for loop
        sendDataBatchesToCluster();
    }

    /**
     * sends unprocessed data batch to the cluster.
     * @pre: GPU.getUnprocessedDBs().size() > 0
     * @post: GPU.getUnprocessedDBs().size() == @pre:GPU.getUnprocessedDBs().size() -1
     */
    void sendDataBatchesToCluster(){
        //sends one by one, called from doneTraining
    }

    /**
     * receives processed data batch from the cluster.
     * @inv: GPU.getProcessedDBs().size() <= MaxNumOfProcessedBatches
     * @post: GPU.getProcessedDBs().size() == @pre:GPU.getProcessedDBs().size() + 1
     */
    void receiveDataBatchFromCluster(){
        if(!isTraining){
            startTraining();
        }
    }

    /**
     * starts to train a processed data batch.
     * @pre: processedDBs.size() > 0
     * @post: ticksUntilDone != 0
     * @post: currTick == 0
     * @post: isTraining == true
     * @post: currDBInTraining != null
     *
     */
    void startTraining(){

    }

    /**
     * checks if the ticksUntilDone is equal to the currTick, if so starts the training of another batch.
     * @post: if(ticksUntilDone == currTick) {processedDBs.size() == @pre:processedDBs.size() -1;
     *                                        model.getData().getProcessed() == @pre:model.getData().getProcessed() + 1}
     */
    void doneTraining(){
        //processedDBs.remove()
    }

    /**
     * notifies the GPUService that it finished training the model.
     * @pre: model.status == "PreTrained"
     * @pre: model.getData().getProcessed() == model.getData().getSize()
     * @pre: numOfTrainedDBs == numOfTotalDBs
     * @post: model.status == "Trained"
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
    public int getCurrTick() {
        return currTick;
    }

    public DataBatch getCurrDBInTraining() {
        return currDBInTraining;
    }

    public void setCurrTick(int currTick) {
        this.currTick = currTick;
    }

    public void setCurrDBInTraining(DataBatch currDBInTraining) {
        this.currDBInTraining = currDBInTraining;
    }
}
