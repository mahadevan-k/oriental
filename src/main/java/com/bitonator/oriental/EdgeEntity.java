package com.bitonator.oriental;

import java.util.Iterator;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orientechnologies.orient.core.id.ORecordId;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import com.tinkerpop.gremlin.Tokens.T;

public class EdgeEntity extends ElementEntity implements Edge {
	private Edge e;
	
	public static <T extends EdgeEntity> OrientEdgeType createEdgeType(
			OrientGraphFactory factory, String type) {
		OrientGraphNoTx graph = factory.getNoTx();

		OrientEdgeType dbtype = graph.getEdgeType(type);
		try {
			if (dbtype == null) {
				System.out.println("Creating edge type: " + type);
				return graph.createEdgeType(type);
			}
			return dbtype;
		} finally {
			graph.shutdown();
		}
	}
	
	@JsonIgnore
	public Edge getEdge() {
		return e;
	}
	
	public boolean isBound() {
		return e!=null;
	}
	
	public void bind(Edge e) {
		this.e=e;
	}
	
	// creation methods
	
	
	public void fresh(VertexEntity vIn, VertexEntity vOut) {		
		e=this.getGraph().addEdge("class:"+this.getClass().getSimpleName(), 
				vIn.getVertex(), vOut.getVertex(), null);
		e.setProperty("tag", this.getEdgeTag(vIn, vOut, this.getClass()));
	}
	
	public void load(Object id) throws EntityNotFoundException {
		e=this.getGraph().getEdge(id);
		if(e==null)
			throw new EntityNotFoundException(this.getClass().getSimpleName()+" not found");
	}
	
	public void load(VertexEntity vIn, VertexEntity vOut) throws EntityNotFoundException {
		String tag=this.getEdgeTag(vIn, vOut, this.getClass());
		Iterable<Edge> edges=this.getGraph().getEdges("tag",
				tag);
		if(edges!=null) {
			Iterator<Edge> iter=edges.iterator();
			if(iter.hasNext())
				e=iter.next();
			else
				throw new EntityNotFoundException(this.getClass().getSimpleName()+" not found");
		} else {
			throw new EntityNotFoundException(this.getClass().getSimpleName()+" not found");
		}

	}
	
	@JsonIgnore
	public String getTag() {
		return e.getProperty("tag");
	}

	// Edge methods
	@Override
	@JsonIgnore
	public Object getId() {
		return e.getId();
	}
	
	public String getRid() {
		ORecordId rid=(ORecordId)e.getId();
		return rid.clusterId+"_"+rid.clusterPosition;
	}


	@Override
	@JsonIgnore
	public <T> T getProperty(String arg0) {
		return e.getProperty(arg0);
	}

	@Override
	@JsonIgnore
	public Set<String> getPropertyKeys() {
		return e.getPropertyKeys();
	}

	@Override
	public void remove() {
		e.remove();
	}

	@Override
	public <T> T removeProperty(String arg0) {
		return e.removeProperty(arg0);
	}

	@Override
	public void setProperty(String arg0, Object arg1) {
		e.setProperty(arg0, arg1);
	}

	@Override
	@JsonIgnore
	public String getLabel() {
		return e.getLabel();
	}

	@Override
	@JsonIgnore
	public Vertex getVertex(Direction arg0) throws IllegalArgumentException {
		return e.getVertex(arg0);
	}
	
	@JsonIgnore
	public <VE extends VertexEntity> VE getInVertex(Class<VE> klass) throws EntityCreationException {
		VE entity;
		try {
			entity = klass.newInstance();
			entity.setTsg(this.getTsg());
			entity.bind(e.getVertex(Direction.OUT)); 
			return entity;
		} catch (InstantiationException | IllegalAccessException e1) {
			throw new EntityCreationException(e1.getMessage());
		}
		
	}
	
	@JsonIgnore
	public <VE extends VertexEntity> VE getOutVertex(Class<VE> klass) throws EntityCreationException {
		VE entity;
		try {
			entity = klass.newInstance();
			entity.setTsg(this.getTsg());
			entity.bind(e.getVertex(Direction.IN));
			return entity;
		} catch (InstantiationException | IllegalAccessException e1) {
			throw new EntityCreationException(e1.getMessage());
		}
		
	}
	
	public String toString() {
		if(this.isBound()) {
			return this.getTag();
		} else {
			return "Unbound "+this.getClass().getSimpleName();
		}
	}
}
