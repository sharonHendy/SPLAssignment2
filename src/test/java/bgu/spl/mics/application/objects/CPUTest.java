package bgu.spl.mics.application.objects;

import junit.framework.TestCase;

public class CPUTest extends TestCase {

    CPU CPU;
    public void setUp() throws Exception {
        super.setUp();
        CPU = new CPU(3,new Cluster());
    }

    public void testUpdateTick() {
        int currTick = CPU.getCurrTick();
        CPU.updateTick();
        assertEquals(currTick, currTick + 1);
    }

    public void testGetDataBatches() {
        CPU.getDataBatches();
        assertTrue(CPU.getData().size() != 0);
    }

    public void testStartProcessing() {
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

    public void testSendDataBatch() {
    }

    public void testDoneProcessing() {
    }
}