package bgu.spl.mics.application.objects;

import bgu.spl.mics.Event;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.messages.TestModelEvent;
import com.sun.org.apache.xpath.internal.operations.Mod;


import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

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
    private MessageBusImpl messageBus;
    private boolean isTraining;
    private int ticksUntilDone; //number of ticks it takes to train the current batch
    private int currTick;
    private LinkedBlockingQueue<DataBatch> unprocessedDBs; //batches before processing
    private LinkedBlockingQueue<DataBatch> processedDBs; //batches after processing
    int numOfTrainedDBs; //number of data batches trained so far
    int numOfTotalDBs; //total number of data batches
    int totalTimeUnitsUsed; //for statistics
    private DataBatch currDBInTraining; //current DB the GPU trains




    GPU(Type type, Model model, Cluster cluster){
        messageBus= MessageBusImpl.getInstance();
        this.type = type;
        this.model = model;
        this.cluster = cluster;
        unprocessedDBs = new LinkedBlockingQueue<>();
        processedDBs = new LinkedBlockingQueue<>();

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
    public void testModel(){
        double num= Math.random();
        Model.Result result;
        if(model.getStudent().getStatus()==Student.Degree.PhD){
            if(num<=0.8){
                result= Model.Result.Good;
            }
            else{
                result= Model.Result.Bad;
            }
        }
        else{
            if(num<=0.6){
                result= Model.Result.Good;
            }
            else{
                result= Model.Result.Bad;
            }
        }
        model.setResult(result);
        model.setStatus(Model.Status.Tested);
        messageBus.complete(TestModelEvent, result);
    }

    /**
     *the GPUService will call this method when it receives a tickBroadcast from the messageBus.
     *@post: @currTick - @pre:currTick == 1
     */
    public void updateTick(){
        currTick = currTick + 1;
        doneTraining();
    }

    /**
     * splits the data to dataBatches.
     * @post: numOfTotalDBs == model.getData().getSize()/1000
     */
    public void prepareDataBatches(){
        for(int i=0; i<model.getData().getSize();i=i+1000){
            unprocessedDBs.add(new DataBatch(model.getData(),i));
        }
        numOfTotalDBs = unprocessedDBs.size(); //numOfTotalDBs =model.getData().getSize()/1000
    }

    /**
     * sends unprocessed data batch to the cluster.
     * @pre: GPU.getUnprocessedDBs().size() > 0
     * @post: GPU.getUnprocessedDBs().size() == @pre:GPU.getUnprocessedDBs().size() -1
     */
    public void sendDataBatchesToCluster(){
        //sends one by one, called from doneTraining
        if(!unprocessedDBs.isEmpty() && processedDBs.size()<MaxNumOfProcessedBatches){
            cluster.receiveDataBatchFromGPU(unprocessedDBs.poll());
        }
    }

    /**
     * receives processed data batch from the cluster.
     * @inv: GPU.getProcessedDBs().size() <= MaxNumOfProcessedBatches
     * @post: GPU.getProcessedDBs().size() == @pre:GPU.getProcessedDBs().size() + 1
     */
    public void receiveDataBatchFromCluster(DataBatch dataBatch){
        /*if(!isTraining){
            startTraining();
        }*/
        if(processedDBs.size()<MaxNumOfProcessedBatches){
            processedDBs.add(dataBatch);
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
    public void startTraining(){
        sendDataBatchesToCluster();


    }

    /**
     * checks if the ticksUntilDone is equal to the currTick, if so starts the training of another batch.
     * @post: if(ticksUntilDone == currTick) {processedDBs.size() == @pre:processedDBs.size() -1;
     *                                        model.getData().getProcessed() == @pre:model.getData().getProcessed() + 1}
     */
    public void doneTraining(){
        //processedDBs.remove()
    }

    /**
     * notifies the GPUService that it finished training the model.
     * @pre: model.status == "PreTrained"
     * @pre: model.getData().getProcessed() == model.getData().getSize()
     * @pre: numOfTrainedDBs == numOfTotalDBs
     * @post: model.status == "Trained"
     */
    public void complete(){

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