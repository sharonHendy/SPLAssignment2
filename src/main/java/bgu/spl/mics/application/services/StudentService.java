package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    private Student student;
    private int index;

    public StudentService(String name, Student student) {
        super(name);
        this.student= student;
        index=0;
    }

    @Override
    protected void initialize() {
        if(student.getModels().size()>0){
            sendEvent(new TrainModelEvent(student.getModels().get(0)));
            index++;
        }
        subscribeBroadcast(TerminationBroadcast.class, c -> terminate());
        subscribeBroadcast(PublishConferenceBroadcast.class, c -> student.handelPublishConference(c.getModels()));
        subscribeEvent(FinishedTrainEvent.class,c -> {
            sendEvent(new TestModelEvent(c.getModel()));
            if(index< student.getModels().size()){
                sendEvent(new TrainModelEvent(student.getModels().get(index)));
                index++;
            }
        });
        subscribeEvent(FinishedTestEvent.class, c -> {
            if(c.getModel().getResult()== Model.Result.Good){
                sendEvent(new PublishResultsEvent(c.getModel()));
            }
        });

        /*
        while(index<student.getModels().size()){
            Future<Model> future= sendEvent(new TrainModelEvent(student.getModels().get(index)));
            Future<Model> future2= sendEvent(new TestModelEvent(future.get()));
            if(future2.get().getResult()== Model.Result.Good){
                sendEvent(new PublishResultsEvent(future2.get()));
            }
            index++;
        }*/
    }
}
