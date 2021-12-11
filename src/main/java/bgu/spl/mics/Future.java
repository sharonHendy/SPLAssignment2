package bgu.spl.mics;

import bgu.spl.mics.application.objects.Model;

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
	private boolean isResolved;

	/**
	 * This should be the only public constructor in this class.
	 */
	public Future() {
		isResolved= false;
		result=null;
		//TODO: implement this
	}

	/**
	 * retrieves the result the Future object holds if it has been resolved.
	 * This is a blocking method! It waits for the computation in case it has
	 * not been completed.
	 * <p>
	 * @return return the result of type T if it is available, if not wait until it is available.
	 * @post: this.isDone() == true
	 * @post: @return == result
	 */
	public T get() throws InterruptedException {
		//TODO: implement this.
		synchronized (this){ //after we checked !isResolved before we get to wait no one can resolve it and get us deadlock
			while(!isResolved){
				this.wait();
			}
		}
		return result;
	}

	/**
	 * Resolves the result of this Future object.
	 * @pre: this.isDone() == false
	 * @post: this.isDone() == true
	 * @post: future.get() == result
	 */
	public void resolve (T result) {
		synchronized (this){
			isResolved=true;
			this.result= result;
			this.notifyAll();
		}
	}

	/**
	 * @return true if this object has been resolved, false otherwise
	 */
	public boolean isDone() {
		return isResolved;
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
	 * @pre: timeout>=0
	 * @post: this.isDone() == true
	 * @post: @return:T == result || @return:T == null
	 */
	public T get(long timeout, TimeUnit unit) {
		//TODO: implement this.
		return null;
	}

}