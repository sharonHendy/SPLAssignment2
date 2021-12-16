package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConferenceInformation;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConferenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {

    private ConferenceInformation conferenceInformation;
    private int numOfTicks;
    private int date;
    private int tickTime;


    public ConferenceService(String name, int date, int TickTime) {
        super(name);
        conferenceInformation =new ConferenceInformation(name,date);
        numOfTicks=0;
        this.date =date;
        this.tickTime= TickTime;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminationBroadcast.class, c -> terminate());
        subscribeBroadcast(TickBroadcast.class, c -> updateTick());
        subscribeEvent(PublishResultsEvent.class, c ->conferenceInformation.addModel(c.getModel())  );

    }

    private void updateTick(){
        numOfTicks++;
        if(numOfTicks*tickTime==date){
            sendBroadcast(new PublishConferenceBroadcast(conferenceInformation.getModels()));
            terminate();
        }
    }
}
