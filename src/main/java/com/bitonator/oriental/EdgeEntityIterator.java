package com.bitonator.oriental;

import java.util.Iterator;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

public class EdgeEntityIterator<T extends EdgeEntity> implements Iterator<T> {
	private Iterator<Edge> iter;
	private Class<T> klass;
	private ThreadSafeGraph graph;
	
	public EdgeEntityIterator(ThreadSafeGraph graph, Iterator<Edge> it, Class<T> klass) {
		this.graph=graph;
		this.iter=it;
		this.klass=klass;
	}
	
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return iter.hasNext();
	}

	@Override
	public T next() {		
		try {
			T entity;
			entity = this.klass.newInstance();
			entity.setTsg(graph);
			entity.bind(iter.next());
			return entity;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

}