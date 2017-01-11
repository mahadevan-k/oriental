package com.bitonator.oriental;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public abstract class EntityManager {
	protected OrientGraphFactory factory;
	private ThreadSafeGraph tsg;
	
	public EntityManager(OrientGraphFactory factory) {
		this.factory=factory;
		this.registerEntityTypes();
	}
	
	public abstract void registerEntityTypes();
	
	public void setTsg(ThreadSafeGraph graph) {
		this.tsg=graph;
	}
	
	public void startTx() {
		tsg.getGraph().begin();
	}
	
	public void commit() {
		tsg.getGraph().commit();
	}
	
	public void rollback() {
		tsg.getGraph().rollback();
	}
	
	public <T extends VertexEntity> T newVertexEntity(Class<T> klass) throws EntityCreationException {
		T entity;
		try {
			entity = klass.newInstance();
			entity.setTsg(tsg);
			entity.fresh();
			return entity;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new EntityCreationException(e.getMessage());
		}
		
	}
	
	public <T extends VertexEntity> T getVertexEntity(Object id, Class<T> klass) throws EntityCreationException, EntityNotFoundException {
		T entity;
		try {
			entity = klass.newInstance();
			entity.setTsg(tsg);
			entity.load(id);
			return entity;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new EntityCreationException(e.getMessage());
		}
	}
	
	public <T extends EdgeEntity> T newEdgeEntity(VertexEntity vIn, VertexEntity vOut, Class<T> klass) throws EntityCreationException {
		T entity;
		try {
			entity = klass.newInstance();
			entity.setTsg(tsg);
			entity.fresh(vIn, vOut);
			return entity;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new EntityCreationException(e.getMessage());
		}
		
	}
	
	public <T extends EdgeEntity> T getEdgeEntity(Object id, Class<T> klass) throws EntityCreationException, EntityNotFoundException {
		T entity;
		try {
			entity = klass.newInstance();
			entity.setTsg(tsg);
			entity.load(id);
			return entity;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new EntityCreationException(e.getMessage());
		}
		
	}
	
	public <T extends EdgeEntity> T getEdgeEntity(VertexEntity vIn, VertexEntity vOut, Class<T> klass) throws EntityCreationException, EntityNotFoundException {
		T entity;
		try {
			entity = klass.newInstance();
			entity.setTsg(tsg);
			entity.load(vIn, vOut);
			return entity;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new EntityCreationException(e.getMessage());
		}
	}
	
	public <T extends EdgeEntity> T findEdgeEntity(VertexEntity vIn, VertexEntity vOut, Class<T> klass) throws EntityCreationException {
		T entity;
		try {
			entity = klass.newInstance();
			entity.setTsg(tsg);
			entity.load(vIn, vOut);
			return entity;
		} catch (EntityNotFoundException e) {
			return null;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new EntityCreationException(e.getMessage());
		}
	}
	
	public <T extends VertexEntity> T findVertexEntity(Class<T> klass, String property, Object value) throws EntityCreationException, EntityNotFoundException {
		Iterable<Vertex> vertices=tsg.getGraph().getVertices(klass.getSimpleName(), new String[]{property}, new Object[]{value});
		// Iterable<Vertex> vertices=tsg.getGraph().getVertices(klass.getSimpleName()+"."+property, value);
		Iterator<Vertex> iter=vertices.iterator();
		if(iter.hasNext()) {
			T entity;
			try {
				entity = klass.newInstance();
				entity.setTsg(tsg);
				entity.bind(iter.next());
				return entity;
			} catch (InstantiationException | IllegalAccessException e) {
				throw new EntityCreationException(e.getMessage());
			}
		} else {
			return null;
		}
	}
	
	public <T extends EdgeEntity> T findEdgeEntity(Class<T> klass, String property, Object value) throws EntityCreationException, EntityNotFoundException {
		Iterable<Edge> edges=tsg.getGraph().getEdges(klass.getSimpleName()+"."+property, value);
		// Iterable<Vertex> vertices=tsg.getGraph().getVertices(klass.getSimpleName()+"."+property, value);
		Iterator<Edge> iter=edges.iterator();
		if(iter.hasNext()) {
			T entity;
			try {
				entity = klass.newInstance();
				entity.setTsg(tsg);
				entity.bind(iter.next());
				return entity;
			} catch (InstantiationException | IllegalAccessException e) {
				throw new EntityCreationException(e.getMessage());
			}
		} else {
			return null;
		}
	}
	
	public <T extends VertexEntity> Iterable<T> findVertexEntities(Class<T> klass, String property, Object value) throws EntityCreationException, EntityNotFoundException {
		Iterable<Vertex> vertices=tsg.getGraph().getVertices(klass.getSimpleName(), new String[]{property}, new Object[]{value});
		// Iterable<Vertex> vertices=tsg.getGraph().getVertices(klass.getSimpleName()+"."+property, value);
		
		List<T> result=new ArrayList<T>();
		for(Vertex v: vertices) {
			try {	
				T entity = klass.newInstance();
				entity.setTsg(tsg);
				entity.bind(v);
				result.add(entity);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new EntityCreationException(e.getMessage());
			}
		}
		return result;
	}
	
	public <T extends EdgeEntity> Iterable<T> findEdgeEntities(Class<T> klass, String property, Object value) throws EntityCreationException, EntityNotFoundException {
		Iterable<Edge> edges=tsg.getGraph().getEdges(klass.getSimpleName()+"."+property, value);
		// Iterable<Vertex> vertices=tsg.getGraph().getVertices(klass.getSimpleName()+"."+property, value);
		
		List<T> result=new ArrayList<T>();
		for(Edge v: edges) {
			try {	
				T entity = klass.newInstance();
				entity.setTsg(tsg);
				entity.bind(v);
				result.add(entity);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new EntityCreationException(e.getMessage());
			}
		}
		return result;
	}
}
