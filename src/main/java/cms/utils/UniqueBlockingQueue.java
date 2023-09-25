package cms.utils;

/**
 * 唯一阻塞队列
 * 来自https://github.com/Alluxio/alluxio/blob/main/dora/core/common/src/main/java/alluxio/util/executor/UniqueBlockingQueue.java
 *
 */
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * 基于 LinkedBlockingQueue 实现的只包含唯一元素的阻塞队列。
 *
 * 我们将插入的元素序列化到队列中，否则队列中可能会出现重复的元素。
 *
 * @param <T> 元素类型
 */
public class UniqueBlockingQueue<T> extends AbstractQueue<T> implements BlockingQueue<T> {
	private ConcurrentHashSet<T> mElementSet = new ConcurrentHashSet<T>();//唯一标记
	private BlockingQueue<T> mBlockingQueue;
	
	/**
	 * UniqueBlockingQueue 的构造函数
	 *
	 * @param 阻塞队列的容量
	 */
	public UniqueBlockingQueue(int capacity) {
	    mBlockingQueue = new LinkedBlockingQueue<>(capacity);
	}
	
	@Override
	public synchronized void put(T e) throws InterruptedException {
	    if (!mElementSet.contains(e)) {
		    mBlockingQueue.put(e);
		    mElementSet.add(e);
	    }
	}
	
	@Override
	public synchronized boolean offer(T e) {
		//接口描述表明，offer 只能因容量原因而失败，但我们是因唯一性原因而失败。
	    if (mElementSet.contains(e)) {
	    	return false;
	    }
	    
	    
	    if (mBlockingQueue.offer(e)) {
	    	mElementSet.add(e);
	    	return true;
	    }
	    return false;
	}
	
	@Override
	public synchronized boolean offer(T e, long timeout, TimeUnit unit) throws InterruptedException {

	    // 接口描述表明，offer 只能因容量原因而失败，但我们是因唯一性原因而失败。
	    // 由于唯一性原因而失败。
	    if (mElementSet.contains(e)) {
	    	return false;
	    }
	    if (mBlockingQueue.offer(e, timeout, unit)) {
	    	mElementSet.add(e);
	    	return true;
	    }
	    return false;
	}
	
	@Override
	public T take() throws InterruptedException {
		
	    T e = mBlockingQueue.take();
	    mElementSet.remove(e);
	    return e;
	}
	
	@Override
	public int remainingCapacity() {
	    return mBlockingQueue.remainingCapacity();
	}
	
	@Override
	public int drainTo(Collection<? super T> c) {
	    return drainTo(c, Integer.MAX_VALUE);
	}
	
	@Override
	public int drainTo(Collection<? super T> c, int maxElements) {
	    int numberOfElements = mBlockingQueue.drainTo(c, maxElements);
	    if (numberOfElements > 0) {
	    	mElementSet.removeAll(c);
	    }
	    return numberOfElements;
	}
	/**
	 * 从指定的队列中移除所有可用的元素，并将它们添加到给定的集合中。 不删除唯一标记
	 * @param c
	 * @param maxElements
	 * @return
	 */
	public int drainTo2(Collection<? super T> c, int maxElements) {
	    int numberOfElements = mBlockingQueue.drainTo(c, maxElements);
	    return numberOfElements;
	}
	
	@Override
	public Iterator<T> iterator() {
	    Iterator<T> iter = mBlockingQueue.iterator();
	    Iterator<T> it = new Iterator<T>() {
	    	private T mLastElem = null;
	    	@Override
	    	public boolean hasNext() {
	    		return iter.hasNext();
	    	}
	
	    	@Override
	    	public T next() {
	    		mLastElem = iter.next();
	    		return mLastElem;
	    	}
	
	    	@Override
	    	public void remove() {
	    		iter.remove();
	    		if (mLastElem != null) {
	    			mElementSet.remove(mLastElem);
	    		}
	    		mLastElem = null;
	    	}
	    };
	    return it;
	}
	
	@Override
	public int size() {
	    return mBlockingQueue.size();
	}
	
	@Override
	public T poll() {
	    T e = mBlockingQueue.poll();
	    if (e != null) {
	    	mElementSet.remove(e);
	    }
	    return e;
	}
	
	@Override
	public T poll(long timeout, TimeUnit unit) throws InterruptedException {
	    T e = mBlockingQueue.poll(timeout, unit);
	    if (e != null) {
	    	mElementSet.remove(e);
	    }
	    return e;
	}
	
	/**
	 * 将首个元素从队列中弹出,如果队列是空的，就返回null。不删除唯一标记
	 */
	public T poll2(long timeout, TimeUnit unit) throws InterruptedException {
	    T e = mBlockingQueue.poll(timeout, unit);
	    return e;
	}
	
	/**
	 * 删除唯一标记
	 */
	public void deleteUnique(T e){
		mElementSet.remove(e);
	}
	
	@Override
	public T peek() {
	    return mBlockingQueue.peek();
	}
}