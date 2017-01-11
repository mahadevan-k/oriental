package com.bitonator.oriental;

import java.util.HashMap;
import java.util.Iterator;

import com.bitonator.oriental.PaginatedVertexEntityIterable;
import com.bitonator.oriental.VertexEntity;

public class VertexViewIterable<S extends VertexEntity, T extends VertexView> implements Iterable<T> {
	private Class<T> view;
	private HashMap<String, Object> properties;
	private Iterable<S> iter;
	
	public VertexViewIterable(PaginatedVertexEntityIterable<S> iter, HashMap<String, Object> properties, Class<T> view) {
		this.view=view;
		this.iter=iter;
		this.properties=properties;
	}
	
	@Override
	public Iterator<T> iterator() {
		return new VertexViewIterator<S,T>(iter, properties, view);
	}

}
