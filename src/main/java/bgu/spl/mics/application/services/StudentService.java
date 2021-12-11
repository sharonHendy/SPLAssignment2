package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import com.sun.xml.internal.ws.api.message.Message;

import java.util.LinkedList;

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
    private MessageBus messageBus;
    private Student student;
    private LinkedList<Future<Model>> modelsToTrain;
    private LinkedList<Future<Model>> modelsToTest;

    public StudentService(String name, Student student) {
        super(name);
        messageBus= MessageBusImpl.getInstance();
        this.student= student;
    }

    @Override
    protected void initialize() {
        messageBus.register(this);
        //subscribeBroadcast(PublishResultsEvent.class);
        for(Model m: student.getModels()){
            TrainModelEvent trainModel= new TrainModelEvent(m);
            Future<Model> future= sendEvent(trainModel);
            modelsToTrain.add(future);
        }




    }
}
