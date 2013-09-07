package jaje.android.lib.collections;

import java.util.ArrayList;
import java.util.Collection;

public class ObjectCollection<T> {

	private ArrayList<T> list;
	private int count;
	private int increment;
	private Class<?> token;

	public ObjectCollection(Class<?> classToken, int initialSize,
			int incrementIfMissing) throws InstantiationException,
			IllegalAccessException {
		list = new ArrayList<T>(initialSize);
		token = classToken;
		insertNewObject(initialSize);
		count = initialSize;
		increment = incrementIfMissing;
	}

	public ObjectCollection(Class<?> classToken, ArrayList<T> list,
			int incrementIfMissing) {
		this.list = list;
		token = classToken;
		count = list.size();
		increment = incrementIfMissing;
	}

	@SuppressWarnings("unchecked")
	protected void insertNewObject(int count) throws InstantiationException,
			IllegalAccessException {
		for (int i = 0; i < count; ++i) {
			list.add((T) token.newInstance());
		}
	}

	protected void refill() throws InstantiationException,
			IllegalAccessException {
		insertNewObject(increment);
	}

	public ArrayList<T> getList() {
		return list;
	}

	public T get() throws InstantiationException, IllegalAccessException {
		if (count == 0) {
			refill();
			count = increment;
		}
		return list.remove(--count);
	}

	public void add(T obj) {
		list.add(obj);
		++count;
	}

	public void addAll(Collection<? extends T> col) {
		list.addAll(col);
		count += col.size();
	}

}
