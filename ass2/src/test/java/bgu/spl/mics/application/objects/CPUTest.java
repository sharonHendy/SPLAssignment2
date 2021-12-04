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
        CPU.getDataBatches();
        assertTrue(CPU.getData().size() != 0);
    }

    @Test
    public void startProcessing() {
        CPU.startProcessing();
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
        int size= CPU.getData().size();
        CPU.sendDataBatch();
        assertEquals(size-1,CPU.getData().size());
    }

    @Test
    public void doneProcessing() {

    }


}