package bgu.spl.mics.application.objects;


import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	private Collection<GPU> GPUS;
	private Collection<CPU> CPUS;
	private HashMap<DataBatch, GPU> findGPU;
	private HashMap<GPU, LinkedBlockingQueue<DataBatch>> unprocessed;
	private HashMap<GPU, LinkedBlockingQueue<DataBatch>> processed;
	//statistics:
	private ArrayList<String> ModelsNames;
	private int numOfProcessedByCPU;
	private int CPUTimeUnitsUsed;
	private int GPUTimeUnitsUsed;

	public boolean hasDataBatches(CPU cpu) {
	}


	private static class SingletonHolder{
		private static Cluster instance= new Cluster();
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		return Cluster.SingletonHolder.instance;
	}

	private Cluster(){
		numOfProcessedByCPU=0;
		CPUTimeUnitsUsed=0;
		GPUTimeUnitsUsed=0;
		ModelsNames= new ArrayList<>();
	}

	public void receiveDataBatchFromGPU(DataBatch dataBatch, GPU gpu){
		if(unprocessed.putIfAbsent(gpu, new LinkedBlockingQueue<DataBatch>())==null){
			String name= gpu.getModel().getName();
			ModelsNames.add(name);
		}
		unprocessed.get(gpu).add(dataBatch);
		findGPU.put(dataBatch, gpu);
	}

	public void sendDataBatchToGPU(GPU gpu){
		if(!processed.get(gpu).isEmpty()) {
			DataBatch send= processed.get(gpu).poll();
			gpu.receiveDataBatchFromCluster(send);
			findGPU.remove(send);
		}
	}

	public void receiveDataBatchFromCPU(DataBatch dataBatch){
		GPU g= findGPU.get(dataBatch);
		processed.get(g).add(dataBatch);
		numOfProcessedByCPU++;
	}
	public Void sendDataBatchToCPU(){


	}

	public ArrayList<String> getModelsNames() {
		return ModelsNames;
	}

	public int getNumOfProcessedByCPU() {
		return numOfProcessedByCPU;
	}

	public int getCPUTimeUnitsUsed() {
		return CPUTimeUnitsUsed;
	}

	public int getGPUTimeUnitsUsed() {
		return GPUTimeUnitsUsed;
	}

	public void setCPUTimeUnitsUsed() {
		for( CPU c: CPUS){
			CPUTimeUnitsUsed= CPUTimeUnitsUsed+ c.getTotalTimeUnitsUsed();
		}
	}

	public void setGPUTimeUnitsUsed() {
		for( GPU g: GPUS)
		GPUTimeUnitsUsed = GPUTimeUnitsUsed+ g.getTotalTimeUnitsUsed();
	}
}
