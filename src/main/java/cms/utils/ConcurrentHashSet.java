package cms.utils;


import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 来自https://github.com/Alluxio/alluxio/blob/main/dora/core/common/src/main/java/alluxio/collections/ConcurrentHashSet.java
 * 
 * 由cms.utils.UniqueBlockingQueue.java调用
 * 
 * 并发哈希集。 这是由 {@link ConcurrentHashMap} 和 {@link Set} 支持的
 * 操作被转换为 {@link ConcurrentHashMap} 操作。
 *
 * @param <T> 设置对象的类型
 */
public final class ConcurrentHashSet<T> extends AbstractSet<T> {
	  private final ConcurrentHashMap<T, Boolean> mMap;
	
	  /**
	   * Creates a new {@link ConcurrentHashSet}.
	   */
	  public ConcurrentHashSet() {
		  this(2, 0.95f, 1);
	  }
	
	  /**
	   * 创建一个新的 {@link ConcurrentHashSet}。
	   *
	   * @param initialCapacity 初始容量
	   * @param loadFactor 负载系数阈值，用于控制大小调整
	   * @param concurrencyLevel 估计并发更新线程数
	   */
	  public ConcurrentHashSet(int initialCapacity, float loadFactor, int concurrencyLevel) {
		  mMap = new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel);
	  }
	
	  @Override
	  public Iterator<T> iterator() {
		  return mMap.keySet().iterator();
	  }
	
	  @Override
	  public int size() {
		  return mMap.size();
	  }
	
	  @Override
	  public boolean add(T element) {
		  return mMap.put(element, Boolean.TRUE) == null;
	  }
	
	  /**
	   * 将元素添加到集合中，前提是该元素尚未加入集合
	   *
	   * @param 要添加到集合中的元素
	   * @return 如果该集合尚未包含指定元素，则返回 true
	   */
	  public boolean addIfAbsent(T element) {
		  return mMap.putIfAbsent(element, Boolean.TRUE) == null;
	  }
	
	  @Override
	  public void clear() {
		  mMap.clear();
	  }
	
	  @Override
	  public boolean contains(Object o) {
		  return mMap.containsKey(o);
	  }
	
	  @Override
	  public boolean containsAll(Collection<?> c) {
		  return mMap.keySet().containsAll(c);
	  }
	
	  @Override
	  public boolean isEmpty() {
		  return mMap.isEmpty();
	  }
	
	  @Override
	  public boolean remove(Object o) {
		  return mMap.remove(o) != null;
	  }
	
	  @Override
	  public boolean removeAll(Collection<?> c) {
		  return mMap.keySet().removeAll(c);
	  }
	
	  @Override
	  public boolean retainAll(Collection<?> c) {
		  return mMap.keySet().retainAll(c);
	  }
	
	  @Override
	  public Object[] toArray() {
		  return mMap.keySet().toArray();
	  }
	
	  @Override
	  public <E> E[] toArray(E[] a) {
		  return mMap.keySet().toArray(a);
	  }
	
	  @Override
	  public String toString() {
		  return mMap.keySet().toString();
	  }
}