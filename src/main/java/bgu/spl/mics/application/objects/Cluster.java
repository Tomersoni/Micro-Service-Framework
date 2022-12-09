package bgu.spl.mics.application.objects;


import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	public static Cluster instance=null;
	public Collection<GPU> GPUS;
	public Collection<CPU> CPUS;
	public Statistics stats;
	public BlockingQueue<DataBatch> strongCPUsBatches;
	public BlockingQueue<DataBatch> weakCPUsBatches;
	private ConcurrentHashMap<DataBatch,GPU> batchToGPU;
	private ConcurrentHashMap<GPU,BlockingQueue<DataBatch>> GPUtoQueue;



	public Collection<GPU> getGPUS() {
		return GPUS;
	}

	public ConcurrentHashMap<GPU, BlockingQueue<DataBatch>> getGPUtoQueue() {
		return GPUtoQueue;
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		//TODO: Implement this
		if(instance==null){
			instance=new Cluster();
		}
		return instance;
	}

	public Cluster() {
		this.GPUS = new LinkedList<>();
		this.CPUS = new LinkedList<>();
		this.stats = new Statistics();
		this.strongCPUsBatches = new LinkedBlockingQueue<>();
		this.weakCPUsBatches = new LinkedBlockingQueue<>();
		this.batchToGPU = new ConcurrentHashMap<>();
		this.GPUtoQueue = new ConcurrentHashMap<>();
	}


	public void insertUnprocessedBatchToLists(DataBatch batch, GPU myGPU)
	{


			batchToGPU.put(batch, myGPU);
			Data.Type batchType = batch.getData().getType();

			if (batchType.equals(Data.Type.Images)) {
				strongCPUsBatches.add(batch);
			} else if (batchType.equals(Data.Type.Tabular)) {
				weakCPUsBatches.add(batch);
			}
			else
			{
				if(Math.random()<=0.5)
					strongCPUsBatches.add(batch);
				else
					weakCPUsBatches.add(batch);
			}

	}

	public void returnProcessedBatch(DataBatch batch)
	{
			GPU gpu = batchToGPU.get(batch);
			getGPUtoQueue().get(gpu).add(batch);

	}

}
