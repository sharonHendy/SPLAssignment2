package bgu.spl.mics;

import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private HashMap<MicroService, Queue> a;
	private int numOfEventsSent;
	private int numOfBroadcastsSent;
	ArrayList<MicroService> trainModelEventSubscribers;
	ArrayList<MicroService> testModelEventSubscribers;
	ArrayList<MicroService> publishResultsEventSubscribers;

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
		b.getClass();

	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void register(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub

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
		return numOfBroadcastsSent;
	}

	@Override
	public int numOfEventsSent() {
		return numOfEventsSent;
	}

}
