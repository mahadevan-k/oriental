package io.github.bitonator.oriental;

import java.util.HashMap;
import java.util.Iterator;

import io.github.bitonator.oriental.PaginatedVertexEntityIterable;
import io.github.bitonator.oriental.ThreadSafeGraph;
import io.github.bitonator.oriental.VertexEntity;

public class PaginatedVertexViewIterable<S extends VertexEntity, T extends VertexView>
		implements Iterable<T> {
	private Class<T> view;
	private HashMap<String, Object> properties;
	private PaginatedVertexEntityIterable<S> iter;

	public PaginatedVertexViewIterable(PaginatedVertexEntityIterable<S> iter,
			HashMap<String, Object> properties, Class<T> view) {
		this.view = view;
		this.iter = iter;
		this.properties = properties;
	}

	public Iterable<T> getObjects() {
		return new VertexViewIterable<S, T>(iter, properties, view);
	}

	public boolean isLastPage() {
		return iter.isLastPage();
	}

	public Object getNext() {
		return iter.getNext();
	}

	@Override
	public Iterator<T> iterator() {
		return new VertexViewIterator<S, T>(iter, properties, view);
	}
}
