package bgu.spl.mics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	//private ArrayList<Queue<Event>> queues;
	private HashMap<MicroService,Queue> MSqueues;
	ArrayList<MicroService> trainModelEventSubscribers;
	ArrayList<MicroService> testModelEventSubscribers;
	ArrayList<MicroService> publishResultsEventSubscribers;

	private static MessageBusImpl single_instance=null;

	private MessageBusImpl(){}

	public static MessageBusImpl getInstance(){
		if (single_instance == null){
			single_instance = new MessageBusImpl();
		}
		return single_instance;
	}


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub

	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void register(MicroService m) {
		MSqueues.put(m, new LinkedList<Event>());

	}

	@Override
	public void unregister(MicroService m) {
		MSqueues.remove(m);

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> boolean isMicroServiceSubscribedEvent(MicroService m, Class<? extends Event<T>> type) {
		return false;
	}

	@Override
	public boolean isMicroServiceSubscribedBroadcast(MicroService m, Class<? extends Broadcast> type) {
		return false;
	}

	@Override
	public boolean isMicroServiceRegistered(MicroService m) {
		return false;
	}

	@Override
	public int numOfBroadcastsSent() {
		return 0;
	}

	@Override
	public int numOfEventsSent() {
		return 0;
	}


}
