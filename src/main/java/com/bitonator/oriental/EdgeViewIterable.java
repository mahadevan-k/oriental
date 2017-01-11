package com.bitonator.oriental;

import java.util.HashMap;
import java.util.Iterator;

import com.bitonator.oriental.EdgeEntity;
import com.bitonator.oriental.PaginatedEdgeEntityIterable;
import com.bitonator.oriental.PaginatedVertexEntityIterable;
import com.bitonator.oriental.VertexEntity;

public class EdgeViewIterable<S extends EdgeEntity, T extends EdgeView> implements Iterable<T> {
	private Class<T> view;
	private HashMap<String, Object> properties;
	private Iterable<S> iter;
	
	public EdgeViewIterable(PaginatedEdgeEntityIterable<S> iter, HashMap<String, Object> properties, Class<T> view) {
		this.view=view;
		this.iter=iter;
		this.properties=properties;
	}
	
	@Override
	public Iterator<T> iterator() {
		return new EdgeViewIterator<S,T>(iter, properties, view);
	}


}
