package com.bitonator.oriental;

import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

public class PaginatedVertexEntityIterable<T extends VertexEntity> implements Iterable<T> {
	private Iterable<T> iter;
	private boolean lastPage;
	private T lastEntity;
	private Object next;
	
	public PaginatedVertexEntityIterable(ThreadSafeGraph graph,
			Iterable<T> iter) {
		this.iter=iter;
	}
	
	public void setIter(Iterable<T> iter) {
		this.iter = iter;
	}

	public void setLastPage(boolean lastPage) {
		this.lastPage = lastPage;
	}

	public void setLastEntity(T lastEntity) {
		this.lastEntity = lastEntity;
	}

	public Iterable<T> getObjects() {
		return iter;
	}

	public boolean isLastPage() {
		return lastPage;
	}
	
	public T getLastEntity() {
		if(next==null)
			return lastEntity;
		else
			return null;
	}

	public Object getNext() {
		return next;
	}
	
	public void setNext(Object next) {
		this.next=next;
	}

	@Override
	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return iter.iterator();
	}




}