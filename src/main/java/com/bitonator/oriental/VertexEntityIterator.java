package com.bitonator.oriental;

import java.util.Iterator;

import com.tinkerpop.blueprints.Vertex;

public class VertexEntityIterator<T extends VertexEntity> implements Iterator<T> {
	private Iterator<Vertex> iter;
	private Class<T> klass;
	private ThreadSafeGraph graph;
	
	public VertexEntityIterator(ThreadSafeGraph graph, Iterator<Vertex> it, Class<T> klass) {
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
