package io.github.bitonator.oriental;

import java.util.Iterator;

import com.tinkerpop.blueprints.Vertex;

public class VertexEntityIterable<T extends VertexEntity> implements Iterable<T> {
	private Iterable<Vertex> iter;
	private Class<T> klass;
	private ThreadSafeGraph graph;
	
	public VertexEntityIterable(ThreadSafeGraph graph, Iterable<Vertex> iter, Class<T> klass) {
		this.graph=graph;
		this.iter=iter;
		this.klass=klass;
	}
	
	@Override
	public Iterator<T> iterator() {
		return new VertexEntityIterator<T>(graph, iter.iterator(), klass);
	}

}
