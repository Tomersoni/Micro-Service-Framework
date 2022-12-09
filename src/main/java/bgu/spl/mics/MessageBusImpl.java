package bgu.spl.mics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {


	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> microToQueue=new ConcurrentHashMap<>();;
	private ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedDeque<MicroService>> typeToMicro=new ConcurrentHashMap<>();;
	public static MessageBusImpl instance=null;
	public Object key1= new Object();
	public Object key2= new Object();
	public Object key3= new Object();

	public MessageBusImpl() {
		//microToQueue=new ConcurrentHashMap<>();
		//typeToMicro=new ConcurrentHashMap<>();
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// TODO Auto-generated method stub
		synchronized (key2) {
			if (typeToMicro.get(type) == null) {
				ConcurrentLinkedDeque<MicroService> deque = new ConcurrentLinkedDeque<>();
				deque.addLast(m);
				typeToMicro.put(type, deque);
				m.messageTypes.addLast(type);
			} else {
				typeToMicro.get(type).add(m);
				m.messageTypes.addLast(type);
			}
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub
		synchronized (key1) {
			if (typeToMicro.get(type) == null) {
				ConcurrentLinkedDeque<MicroService> deque = new ConcurrentLinkedDeque<>();
				deque.addLast(m);
				typeToMicro.put(type, deque);
				m.messageTypes.addLast(type);
			} else {
				typeToMicro.get(type).add(m);
				m.messageTypes.addLast(type);
			}
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub
		e.getFuture().resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub
		Class type=b.getClass();
		ConcurrentLinkedDeque<MicroService> list=typeToMicro.get(type);
		if(list!=null){
			for(MicroService m :list){
				microToQueue.get(m).add(b);
			}
		}
	}

	
	@Override
	public  <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub

		Future<T> future=new Future<>();
		Class type=e.getClass();
		ConcurrentLinkedDeque<MicroService> list=typeToMicro.get(type);
		if(!list.isEmpty()){
			synchronized (key3) {
				microToQueue.get(list.getFirst()).add(e);
				list.addLast(list.removeFirst());
			}
		}
		return future;
	}

	@Override
	public void register(MicroService m) {
		// TODO Auto-generated method stub
		LinkedBlockingQueue<Message> q=new LinkedBlockingQueue<>();
		microToQueue.put(m,q);
	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method
		synchronized (microToQueue) {
			synchronized (typeToMicro) {
				for (Class<? extends Message> type : m.messageTypes) {
					typeToMicro.get(type).remove(m);
				}
			}
			if (microToQueue.get(m) != null) {
				microToQueue.remove(m);
			}
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
			// TODO Auto-generated method stub
			if(microToQueue.containsKey(m)) {
				return microToQueue.get(m).take();
			}
			else
			{
				throw new IllegalStateException("MicroService not registered");
			}
	}

	@Override
	public boolean hasQueue(MicroService m) {
		return microToQueue.get(m)!=null;
	}

	@Override
	public <T> boolean hasEvent(MicroService m, Event<T> event) {
		LinkedBlockingQueue<Message> q=microToQueue.get(m);
		if(q!=null){
			for(Message msg : q){
				if(msg.equals(event)) //Do we need to implement equals?
					return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasBroadcast(MicroService m, Broadcast broad) {
		LinkedBlockingQueue<Message> q=microToQueue.get(m);
		if(q!=null){
			for(Message msg : q){
				if(msg.equals(broad)) //Do we need to implement equals?
					return true;
			}
		}
		return false;
	}

	public static MessageBusImpl getInstance(){
		if(instance==null){
			instance=new MessageBusImpl();
		}
		return instance;
	}


	public ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> getMicroToQueue() {
		return microToQueue;
	}

	public ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedDeque<MicroService>> getTypeToMicro() {
		return typeToMicro;
	}
}
