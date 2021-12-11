package bgu.spl.mics.application.objects;


import bgu.spl.mics.MessageBusImpl;

import java.util.Collection;

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
	private Collection<DataBatch> unprocessed;
	private Collection<DataBatch> processed;
	//private statistics

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

	}

	public void receiveDataBatchFromGPU(DataBatch dataBatch){
		unprocessed.add(dataBatch);
	}

	public void sendDataBatchToGPU(DataBatch dataBatch){
		if(!processed.isEmpty()){
			//needs to know the sender of the data batch
		}
	}

	public void receiveDataBatchFromCPU(DataBatch dataBatch){
		processed.add(dataBatch);
	}

	public boolean hasDataBatches(CPU cpu) {
		return true;
	}
}
