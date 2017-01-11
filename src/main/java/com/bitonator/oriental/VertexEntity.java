package com.bitonator.oriental;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orientechnologies.orient.core.id.ORecordId;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexQuery;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import com.orientechnologies.orient.core.id.ORecordId;


public class VertexEntity extends ElementEntity implements Vertex {
	protected Vertex v;
	
	public static <T extends VertexEntity> OrientVertexType
			createVertexType(OrientGraphFactory factory, String type) {
		OrientGraphNoTx graph = factory.getNoTx();

		OrientVertexType dbtype=graph.getVertexType(type) ;
		try {
			if(dbtype==null) {
				System.out.println("Creating vertex type: "+type);
				return graph.createVertexType(type) ;
			}
			return dbtype ;
		} 
		finally {
			graph.shutdown();
		}
	}
	
	public boolean equals(VertexEntity ve) {
		return ve.getVertex().getId().equals(v.getId());
	}
	
	@JsonIgnore
	public Vertex getVertex() {
		return v;
	}
	
	public boolean isBound() {
		return v!=null;
	}
	
	public void bind(Vertex v) {
		this.v=v;
	}
	
	// Create, find vertices
	public void fresh() {		
		v=this.getGraph().addVertex("class:"+this.getClass().getSimpleName());
	}
	
	public void load(Object id) throws EntityNotFoundException {
		v=this.getGraph().getVertex(id);
		if(v==null)
			throw new EntityNotFoundException(this.getClass().getSimpleName()+" not found");
	}
	
	public Object gremlinQuery(String query, HashMap<String, Object> params) {		
		String final_query=query;
		if(!final_query.startsWith("g.")) {
			final_query="g.v(vertex)."+query;
		}
		params.put("vertex", v.getId().toString());
		return super.query(final_query, params);
	}
	
	public <T extends VertexEntity> Iterable<T> gremlinVertexEntityList(String query, HashMap<String, Object> params, Class<T> klass) {		
		String final_query=query;
		if(!final_query.startsWith("g.")) {
			final_query="g.v(vertex)."+query;
		}		params.put("vertex", v.getId().toString());
		Object result=super.query(final_query, params);
		List<Vertex> list=new ArrayList<Vertex>();
		
		if(result instanceof Vertex) {
			list.add((Vertex)result);
			// return new VertexEntityIterable<T>(this.getTsg(), iterable, klass);
		}
		if(result instanceof Iterable) {
			list=(List<Vertex>) result;
		}
		Iterable<Vertex> iterable=list;
		return new VertexEntityIterable<T>(this.getTsg(), iterable, klass);
	}
	
	public <T extends EdgeEntity> Iterable<T> gremlinEdgeEntityList(String query, HashMap<String, Object> params, Class<T> klass) {		
		String final_query=query;
		if(!final_query.startsWith("g.")) {
			final_query="g.v(vertex)."+query;
		}
		params.put("vertex", v.getId().toString());
		Object result=super.query(final_query, params);
		List<Edge> list=new ArrayList<Edge>();

		if(result instanceof Edge) {
			list.add((Edge)result);
		}
		if(result instanceof Iterable) {
			list=(List<Edge>) result;
		}
		Iterable<Edge> iterable=list;
		return new EdgeEntityIterable<T>(this.getTsg(), iterable, klass);
	}
	
	public <T extends VertexEntity> PaginatedVertexEntityIterable<T>
	gremlinPaginatedVertexEntityList(
			String firstPageQuery, String nextPageQuery,
			HashMap<String, Object> map,
			Object start, int limit, Class<T> klass) throws EntityCreationException {
		String query=firstPageQuery;
		if(start!=null) {
			query=nextPageQuery;
			map.put("start", start);
		}
		map.put("limit", limit-1);
		String pageQuery=query+"[0..limit]";
		Iterable<T> iter=this.gremlinVertexEntityList(pageQuery, map, klass);
		PaginatedVertexEntityIterable<T> result=
				new PaginatedVertexEntityIterable<T>(this.getTsg(), iter);
		String countQuery=pageQuery+".count()";
		long count=(long) this.gremlinQuery(countQuery, map);
		if(count==limit) {
			String lastEntityQuery=query+"[limit]";
			T entity=this.gremlinVertexEntity(lastEntityQuery, map, klass);
			result.setLastPage(false);
			result.setLastEntity(entity);
		} else {
			result.setLastPage(true);
		}
		
		return result;
	}
	
	public <T extends EdgeEntity> PaginatedEdgeEntityIterable<T>
	gremlinPaginatedEdgeEntityList(
			String firstPageQuery, String nextPageQuery, 
			HashMap<String, Object> map,
			Object start, int limit, Class<T> klass) throws EntityCreationException {
		String query=firstPageQuery;
		if(start!=null) {
			query=nextPageQuery;
			map.put("start", start);
		}
		map.put("limit", limit-1);
		String pageQuery=query+"[0..limit]";
		Iterable<T> iter=this.gremlinEdgeEntityList(pageQuery, map, klass);
		PaginatedEdgeEntityIterable<T> result=
				new PaginatedEdgeEntityIterable<T>(this.getTsg(), iter);
		String countQuery=pageQuery+".count()";
		long count=(long) this.gremlinQuery(countQuery, map);
		if(count==limit) {
			String lastEntityQuery=query+"[limit]";
			T entity=this.gremlinEdgeEntity(lastEntityQuery, map, klass);
			result.setLastPage(false);
			result.setLastEntity(entity);
		} else {
			result.setLastPage(true);
		}
		
		return result;
	}
	
	public <T extends VertexEntity> T gremlinVertexEntity(String query, HashMap<String, Object> params, Class<T> klass) throws EntityCreationException  {		
		String final_query=query;
		if(!final_query.startsWith("g.")) {
			final_query="g.v(vertex)."+query;
		}
		params.put("vertex", v.getId().toString());
		Object result=super.query(final_query, params);
		if(result instanceof Vertex) {
			T entity;
			try {
				entity = klass.newInstance();
				entity.setTsg(this.getTsg());
				entity.bind((Vertex)result);
				return entity;
			} catch (InstantiationException | IllegalAccessException e) {
				throw new EntityCreationException(e.getMessage());
			}
			
		}
		return null;
	}
	
	public <T extends EdgeEntity> T gremlinEdgeEntity(String query, HashMap<String, Object> params, Class<T> klass) throws EntityCreationException  {		
		String final_query=query;
		if(!final_query.startsWith("g.")) {
			final_query="g.v(vertex)."+query;
		}
		params.put("vertex", v.getId().toString());
		Object result=super.query(final_query, params);
		if(result instanceof Edge) {
			T entity;
			try {
				entity = klass.newInstance();
				entity.setTsg(this.getTsg());
				entity.bind((Edge)result);
				return entity;
			} catch (InstantiationException | IllegalAccessException e) {
				throw new EntityCreationException(e.getMessage());
			}
			
		}
		return null;
	}
	
	// Vertex methods
	@Override
	@JsonIgnore
	public Object getId() {
		return v.getId();
	}
	
	public String getRid() {
		ORecordId rid=(ORecordId)v.getId();
		return rid.clusterId+"_"+rid.clusterPosition;
	}

	@Override
	@JsonIgnore
	public <T> T getProperty(String arg0) {
		return v.getProperty(arg0);
	}

	@Override
	@JsonIgnore
	public Set<String> getPropertyKeys() {
		return v.getPropertyKeys();
	}

	@Override
	public void remove() {
		v.remove();
	}

	@Override
	public <T> T removeProperty(String arg0) {
		return v.removeProperty(arg0);
	}

	@Override
	public void setProperty(String arg0, Object arg1) {
		v.setProperty(arg0, arg1);
	}

	@Override
	public Edge addEdge(String arg0, Vertex arg1) {
		Edge e=v.addEdge(arg0, arg1);
		return e;
	}
	
	public <T extends EdgeEntity> T addEdge(VertexEntity arg1, Class<T> klass) throws EntityCreationException {
		Edge e=v.addEdge(klass.getSimpleName(), arg1.getVertex());
		e.setProperty("tag", this.getEdgeTag(v, arg1.getVertex(), klass));
		T entity;
		try {
			entity = klass.newInstance();
			entity.setTsg(this.getTsg());
			entity.bind(e);
			return entity;
		} catch (InstantiationException | IllegalAccessException e1) {
			throw new EntityCreationException(e1.getMessage());
		}
		
	}
	
	@Override
	@JsonIgnore
	public Iterable<Edge> getEdges(Direction arg0, String... arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@JsonIgnore
	public Iterable<Vertex> getVertices(Direction arg0, String... arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@JsonIgnore
	public <T extends EdgeEntity> Iterable<T> getEdges(Direction arg0, Class<T> klass, String... arg1) {
		return new EdgeEntityIterable<T>(this.getTsg(), v.getEdges(arg0, arg1), klass);
	}

	@JsonIgnore
	public <T extends VertexEntity> Iterable<T> getVertices(Direction arg0, Class<T> klass, String... arg1) {
		return new VertexEntityIterable<T>(this.getTsg(), v.getVertices(arg0, arg1), klass);
	}
	

	@Override
	public VertexQuery query() {
		return v.query();
	}

	public String toString() {
		if(this.isBound()) {
			return this.getClass().getSimpleName()+
					"("+this.getId().toString()+"):";
		} else {
			return "Unbound "+this.getClass().getSimpleName();
		}
	}

}
