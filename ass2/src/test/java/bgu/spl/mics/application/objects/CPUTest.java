package bgu.spl.mics.application.objects;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CPUTest {
    private static CPU CPU;

    @Before
    public void setUp(){
        CPU= new CPU(3, new Cluster());
    }

    @Test
    public void updateTick(){
        int currTick = CPU.getCurrTick();
        CPU.updateTick();
        assertEquals(currTick, currTick + 1);
    }
    @Test
    public void getDataBatches() {
        CPU.getDataBatches();
        assertTrue(CPU.getData().size() != 0);
    }

    @Test
    public void startProcessing() {
        int size = CPU.getData().size();
        CPU.startProcessing();

        if(size != 0 ){ //checks it took one of the data batches for processing
            assertEquals(CPU.getData().size(), size - 1);
        }
        assertNotNull(CPU.getCurrDataBatch());

        switch (CPU.getCurrDataBatch().getDataType()){
            case Images -> {
                assertEquals(CPU.getTicksUntilDone(),(32/CPU.getCores()) * 4);
            }
            case Text -> {
                assertEquals(CPU.getTicksUntilDone(),(32/CPU.getCores()) * 2);
            }
            case Tabular -> {
                assertEquals(CPU.getTicksUntilDone(),32/CPU.getCores());
            }
            default -> assertEquals(CPU.getTicksUntilDone(),32/CPU.getCores());
        }
    }

    @Test
    public void sendDataBatch() {
    }

    @Test
    public void doneProcessing() {
    }


}