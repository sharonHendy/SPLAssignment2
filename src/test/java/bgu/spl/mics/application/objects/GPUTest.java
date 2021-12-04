package bgu.spl.mics.application.objects;

import org.junit.Before;
import org.junit.Test;

import static bgu.spl.mics.application.objects.GPU.Type.RTX3090;
import static org.junit.Assert.*;

public class GPUTest {
    GPU GPU;
    @Before
    public void setUp(){
        GPU = new GPU(RTX3090, new Model("noam",new Data(Data.Type.Images,3000),
                new Student("sharon","cs", Student.Degree.PhD) ),new Cluster());


    }

    @Test
    public void updateTick() {
        int currTick = GPU.getCurrTick();
        GPU.updateTick();
        assertEquals(currTick, currTick + 1);
    }

    @Test
    public void prepareDataBatches() {
        int size = GPU.getModel().getData().getSize();
        assertEquals(GPU.getNumOfTotalDBs() ,size/1000);
        assertEquals(GPU.getUnprocessedDBs().size(),size/1000);
    }

    @Test
    public void sendDataBatchesToCluster(){
        GPU.
    }

    @Test
    public void receiveDataBatchFromCluster() {
    }

    @Test
    public void startTraining() {
    }

    @Test
    public void doneTraining() {
    }

    @Test
    public void complete() {

    }

}