package io.github.bitonator.oriental;

import java.util.HashMap;
import java.util.Iterator;

import io.github.bitonator.oriental.EdgeEntity;
import io.github.bitonator.oriental.PaginatedEdgeEntityIterable;
import io.github.bitonator.oriental.PaginatedVertexEntityIterable;
import io.github.bitonator.oriental.VertexEntity;

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
