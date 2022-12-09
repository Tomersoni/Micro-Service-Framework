package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {

    Data data;
    int start_index;
    terminateBatch isTerminateBatch;


    public enum terminateBatch{Yes,No};

    public Data getData() {
        return data;
    }

    public Status getStatus() {
        return status;
    }

    enum Status{Unprocessed,Processed};
    Status status;



    public DataBatch(Data data, int start_index) {
        this.data = data;
        this.start_index = start_index;
        this.status=Status.Unprocessed;
        this.isTerminateBatch=terminateBatch.No;
    }


}
