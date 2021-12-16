package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.GPU.GPUStatus;
import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import bgu.spl.mics.application.objects.GPU;
/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {

    private boolean isBusy;
    boolean hasCompleted;
    private Model currModel;
    private GPU GPU;
    private TrainModelEvent currEvent;
    private LinkedList<Event<Model>> eventsInLine;

    public GPUService(String name,GPU gpu) {
        super(name);
        GPU= gpu;
        GPU.setService(this);
        isBusy=false;
        eventsInLine= new LinkedList<>();
        hasCompleted= false;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, c -> terminate());
        subscribeBroadcast(TickBroadcast.class, c ->{
            GPU.updateTick();
            if(hasCompleted){
                currModel= GPU.getModel();
                complete(currEvent, currModel);
                sendEvent(new FinishedTrainEvent(currModel));
                if(!eventsInLine.isEmpty()){
                    startProcessingEvent();
                }
                else{
                    isBusy=false;
                }
            }

        } );
        subscribeEvent(TrainModelEvent.class, c -> {
            if(!isBusy){
                isBusy=true;
                GPU.trainModel(c.getModel());
                currModel=c.getModel();
                currEvent= c;
            }
            else{
                eventsInLine.add(c);
            }
        });

        subscribeEvent(TestModelEvent.class, c -> {
            if(!isBusy){
                isBusy=true;
                GPU.testModel(c.getModel());
                complete(c, c.getModel());
                sendEvent(new FinishedTestEvent(c.getModel()));
                isBusy=false;
            }
            else{
                eventsInLine.addFirst(c);
            }
        });

    }

    private void startProcessingEvent() {
        isBusy=true;
        Event<Model> e= eventsInLine.poll();
        if(e.getClass()==TrainModelEvent.class){
            currEvent= (TrainModelEvent) e;
            currModel= currEvent.getModel();
            GPU.trainModel(currModel);
        }
        else if(e.getClass()==TestModelEvent.class){
            currModel= ((TestModelEvent) e).getModel();
            GPU.testModel(currModel);
            complete(e, ((TestModelEvent) e).getModel());
        }
    }

    public boolean HasCompleted() {
        return hasCompleted;
    }

    public void setHasCompleted(boolean hasCompleted) {
        this.hasCompleted = hasCompleted;
        notifyAll();
    }
}
