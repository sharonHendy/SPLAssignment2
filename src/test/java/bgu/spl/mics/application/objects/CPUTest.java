package bgu.spl.mics.application.objects;

import junit.framework.TestCase;
import org.junit.Before;

public class CPUTest extends TestCase {

    CPU CPU;
    @Before
    public void setUp(){
        CPU = new CPU(3,new Cluster());
        CPU.getData().add(new DataBatch());
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
        CPU.startProcessing();

        assertNotNull(CPU.getCurrDataBatch());

        switch (CPU.getCurrDataBatch().getDataType()){ //TODO if
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
        int size = CPU.getData().size();
        CPU.sendDataBatch();
        assertEquals(CPU.getData().size(), size - 1);
    }

    public void testDoneProcessing() {
        CPU.getData().clear();
        CPU.getData().add(new DataBatch());
        CPU.getData().add(new DataBatch());

        CPU.startProcessing();
        DataBatch curr = CPU.getCurrDataBatch();
        for(int i = 0; i < CPU.getTicksUntilDone(); i++){
            CPU.updateTick();
            assertTrue(CPU.getCurrDataBatch() == curr);
        }
        CPU.updateTick(); //calls doneProcessing
        assertTrue(CPU.getCurrDataBatch() != curr); //checks that it started processing a new batch
        assertTrue(CPU.getData().size() == 1); //checks that it remove the already processed batch
    }
}