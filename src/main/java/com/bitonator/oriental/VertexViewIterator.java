package com.bitonator.oriental;

import java.util.HashMap;
import java.util.Iterator;

import com.bitonator.oriental.PaginatedVertexEntityIterable;
import com.bitonator.oriental.VertexEntity;
import com.bitonator.oriental.VertexEntityIterator;

public class VertexViewIterator<S extends VertexEntity, T extends VertexView> implements Iterator<T>  {
	private Class<T> view;
	private HashMap<String, Object> properties;
	private Iterator<S> iter;
	
	public VertexViewIterator(Iterable<S> iter, HashMap<String, Object> properties, Class<T> view) {
		this.view=view;
		this.iter=iter.iterator();
		this.properties=properties;
	}
	
	@Override
	public boolean hasNext() {
		return iter.hasNext();
	}

	@Override
	public T next() {
		try {
			T entity;
			entity = view.newInstance();
			entity.setup(properties);
			entity.bind(iter.next());
			return entity;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

}
