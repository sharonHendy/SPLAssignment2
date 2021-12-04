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
    public void startProcessing() {
    }

    @Test
    public void sendDataBatch() {
    }

    @Test
    public void doneProcessing() {
    }
}