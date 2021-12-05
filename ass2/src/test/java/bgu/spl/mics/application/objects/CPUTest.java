package bgu.spl.mics.application.objects;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CPUTest {
    private static CPU CPU;

    @Before
    public void setUp(){
        CPU= new CPU(3, new Cluster());
        CPU.getData().add(new DataBatch());
    }

    @Test
    public void updateTick(){
        int currTick = CPU.getCurrTick();
        CPU.updateTick();
        assertEquals(currTick, currTick + 1);
    }
    @Test
    public void getDataBatches() {
        int before= CPU.getData().size();
        CPU.getDataBatches();
        assertTrue(CPU.getData().size() > before);
    }

    @Test
    public void startProcessing() {
        CPU.startProcessing();
        assertNotNull(CPU.getCurrDataBatch());

    }

    @Test
    public void sendDataBatch() {
        int size= CPU.getData().size();
        CPU.sendDataBatch();
        assertEquals(size-1,CPU.getData().size());
    }

    @Test
    public void doneProcessing() {

    }


}