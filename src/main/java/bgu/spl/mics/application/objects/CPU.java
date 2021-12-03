package bgu.spl.mics.application.objects;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    private int cores;
    private Collection<DataBatch> data;
    private Cluster cluster;

    private int currTick;
    private DataBatch currDataBatch;
    private int ticksUntilDone;

    CPU(int cores, Cluster cluster){
        this.cores = cores;
        data = new ArrayList<>();
        this.cluster = cluster;
        currTick = 0;
    }

    /**
     *the CPUService will call this method when it receives e tickBroadcast from the messageBus.
     *@post: @currTick - @pre currTick == 1
     */
    void updateTick(){
        currTick = currTick + 1;
        doneProcessing();
    }

    /**
     * gets dataBatches from the cluster.
     * @post: data.size() > 0
     */
    void getDataBatches(){

    }

    /**
     * sets the current dataBatch the CPU is working on, and the number of ticks it takes to process it.
     * @post: currDataBatch != null
     */
    void startProcessing(){
        if(data.size() == 0){
            getDataBatches();
        }
        currDataBatch = ((ArrayList<DataBatch>)data).get(0); //TODO how should it know if it receaved data batches?
        ((ArrayList<DataBatch>)data).remove(0);
        currTick = 0;

        switch (currDataBatch.getDataType()){
            case Images -> {
                ticksUntilDone = (32/cores) * 4;
            }
            case Text -> {
                ticksUntilDone = (32/cores)*2;
            }
            case Tabular -> {
                ticksUntilDone = (32/cores);
            }
            default -> ticksUntilDone = (32/cores) * 4;
        }
    }

    /**
     * send the processed data batch to the cluster.
     */
    void sendDataBatch(){
        cluster.receiveDataBatchFromCPU(currDataBatch);
    }

    /**
     * checks if the number of ticks it takes to process the data batch have passed,
     * if so starts the processing of another batch.
     */
    void doneProcessing(){
        if(currTick >= ticksUntilDone){
            sendDataBatch();
            startProcessing();
        }
    }

    public int getCurrTick() {
        return currTick;
    }

    public int getTicksUntilDone() {
        return ticksUntilDone;
    }

    public int getCores() {
        return cores;
    }

    public Collection<DataBatch> getData() {
        return data;
    }

    public DataBatch getCurrDataBatch() {
        return currDataBatch;
    }
}
