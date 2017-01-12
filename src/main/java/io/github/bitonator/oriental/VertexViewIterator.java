package io.github.bitonator.oriental;

import java.util.HashMap;
import java.util.Iterator;

import io.github.bitonator.oriental.PaginatedVertexEntityIterable;
import io.github.bitonator.oriental.VertexEntity;
import io.github.bitonator.oriental.VertexEntityIterator;

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
