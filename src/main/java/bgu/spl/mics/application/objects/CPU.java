package bgu.spl.mics.application.objects;

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
    private boolean isProcessing; //true if the CPU is currently processing a batch or just created

    CPU(int cores, Cluster cluster){
        this.cores = cores;
        data = new ArrayList<>();
        this.cluster = cluster;
        currTick = 0;
        isProcessing = true;
    }

    /**
     *the CPUService will call this method when it receives a tickBroadcast from the messageBus.
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
     * @pre: data.size() != 0
     * @post: currDataBatch != null
     */
    void startProcessing(){
        currDataBatch = ((ArrayList<DataBatch>)data).get(0);

        switch (currDataBatch.getDataType()){ //TODO if
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

        currTick = 0;
        isProcessing = true; //ready for ticks
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
        if(isProcessing & currTick >= ticksUntilDone){
            isProcessing = false; //ignores ticks until it started processing a new batch
            sendDataBatch();
            ((ArrayList<DataBatch>)data).remove(0);

            if(data.size() == 0){ //waits for the cluster to put batches in its queue
                Thread t = new Thread(() -> {
                    while(!cluster.hasDataBatches(this)){
                        try {
                            cluster.wait(); //when the cluster puts batches in the CPU queue it does notifyAll
                        } catch (InterruptedException e) {
                        }
                    }
                    getDataBatches();
                    startProcessing();
                });
                t.start();
            }else{
                startProcessing();
            }
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

    public Cluster getCluster() {
        return cluster;
    }
}
