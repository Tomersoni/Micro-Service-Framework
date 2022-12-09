package bgu.spl.mics;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {

	private T result;
	private boolean done;


	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
		//TODO: implement this
		this.result=null;
		this.done=false;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
     * @pre: !future.isDone()
	 * @post: future.isDone()
	 * @post: future.get()==result
     */
	public synchronized T get() {
		//TODO: implement this.
		//NOT SURE
		//can't use wait without adding exception
		while(!isDone()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
     * Resolves the result of this Future object.
	 * @post: future.get()==result
	 * @post: future.isDone()
     */
	public synchronized void resolve (T result) {
		//TODO: implement this.
		this.result=result;
		this.done=true;
		notifyAll(); //to stop waiting in get() method.
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
	 * @pre: future.isDone()==false before activating resolve
	 * @post: future.isDone()==true after activating resolve
     */
	public boolean isDone() {
		//TODO: implement this.
		return done;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timeout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
	 *
	 * @pre: !future.isDone()
	 * @pre: future.get(1000, TimeUnit.MILLISECONDS)==null before activating resolve
	 * @post: future.get(1000,TimeUnit.MILLISECONDS)==result after activating resolve
	 * @post: future.isDone()
     */
	public synchronized T get(long timeout, TimeUnit unit) {
		//TODO: implement this.
		if(unit!=TimeUnit.MILLISECONDS){
			timeout=unit.toMillis(timeout);
		}

		if(isDone())
			return result;
		else
		{
			try {
				wait(timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if(isDone())
				return result;

			return null;
		}

	}

}
