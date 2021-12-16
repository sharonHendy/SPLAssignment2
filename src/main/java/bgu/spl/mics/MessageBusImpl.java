package bgu.spl.mics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private ConcurrentHashMap<Event<?>, Future<?>> futureHashMap;
	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> MSqueues;
	private ConcurrentHashMap<Class<? extends Event<?>>, LinkedBlockingQueue<MicroService>> EventsSubscribers;
	private ConcurrentHashMap<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> BroadcastSubscribers;
	private int numOfEventsSent;
	private int numOfBroadcastsSent;

	private static class SingletonHolder{
		private static MessageBusImpl instance= new MessageBusImpl();
	}


	private MessageBusImpl(){
		numOfEventsSent=0;
		numOfBroadcastsSent=0;
		EventsSubscribers= new ConcurrentHashMap<>();
		BroadcastSubscribers= new ConcurrentHashMap<>();
		futureHashMap= new ConcurrentHashMap<>();
		MSqueues= new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance(){
		return SingletonHolder.instance;
	}


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if(isMicroServiceRegistered(m)) {
			EventsSubscribers.putIfAbsent(type, new LinkedBlockingQueue<MicroService>());
			if (!isMicroServiceSubscribedEvent(m, type)) {
				EventsSubscribers.get(type).add(m);
			}
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if(isMicroServiceRegistered(m)) {
			BroadcastSubscribers.putIfAbsent(type, new LinkedBlockingQueue<MicroService>());
			if (!isMicroServiceSubscribedBroadcast(m, type)) {
				BroadcastSubscribers.get(type).add(m);
			}
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future<T> future= (Future<T>) futureHashMap.get(e);
		future.resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		LinkedBlockingQueue<MicroService> ms= BroadcastSubscribers.get(b.getClass());
		for(MicroService m: ms){
			MSqueues.get(m).add(b);
		}
		synchronized(this){
			numOfBroadcastsSent++;
		}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e){
		MicroService ms = EventsSubscribers.get(e.getClass()).poll();
		if(ms==null){
			return null;
		}
		//removes and add to the back of the queue
		EventsSubscribers.get(e.getClass()).add(ms);
		MSqueues.get(ms).add(e);
		Future<T> future= new Future<T>();
		futureHashMap.put(e, future);
		synchronized (this){
			numOfEventsSent++;
		}
		return future;
	}

	@Override
	public void register(MicroService m) {
		MSqueues.put(m, new LinkedBlockingQueue<Message>());
	}

	@Override
	public void unregister(MicroService m) {
		MSqueues.remove(m);
		for(LinkedBlockingQueue<MicroService> ms: EventsSubscribers.values()){
			ms.remove(m);
		}
		for(LinkedBlockingQueue<MicroService> ms: BroadcastSubscribers.values()){
			ms.remove(m);
		}
		// what about the queue m have with the messages
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if(!isMicroServiceRegistered(m)){
			throw new IllegalStateException();
		}
		return MSqueues.get(m).take(); //take- waiting if necessary until an element becomes available and throw interrupted exception
	}

	@Override
	public <T> boolean isMicroServiceSubscribedEvent(MicroService m, Class<? extends Event<T>> type) {
		if(EventsSubscribers.containsKey(type)){
			return EventsSubscribers.get(type).contains(m);
		}
		return false;
	}

	@Override
	public boolean isMicroServiceSubscribedBroadcast(MicroService m, Class<? extends Broadcast> type) {
		if(BroadcastSubscribers.containsKey(type)){
			return BroadcastSubscribers.get(type).contains(m);
		}
		return false;
	}

	@Override
	public boolean isMicroServiceRegistered(MicroService m) {
		return MSqueues.containsKey(m);
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