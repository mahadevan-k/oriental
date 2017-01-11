package com.bitonator.oriental;

import java.util.Iterator;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

public class EdgeEntityIterable<T extends EdgeEntity> implements Iterable<T> {
	private Iterable<Edge> iter;
	private Class<T> klass;
	private ThreadSafeGraph graph;
	
	public EdgeEntityIterable(ThreadSafeGraph graph, Iterable<Edge> iter, Class<T> klass) {
		this.graph=graph;
		this.iter=iter;
		this.klass=klass;
	}
	
	@Override
	public Iterator<T> iterator() {
		return new EdgeEntityIterator<T>(graph, iter.iterator(), klass);
	}

}
