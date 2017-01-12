package io.github.bitonator.oriental;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.graph.gremlin.OCommandGremlin;
import com.orientechnologies.orient.graph.gremlin.OGremlinHelper;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

public abstract class ElementEntity {	
	private ThreadSafeGraph tsg;
	
	public ElementEntity() {
	}
	
	@JsonIgnore
	public OrientGraph getGraph() {
		return tsg.getGraph();
	}
	
	@JsonIgnore
	public ThreadSafeGraph getTsg() {
		return tsg;
	}
	
	public void setTsg(ThreadSafeGraph graph) {
		this.tsg=graph;
	}
	
	public String getEdgeTag(Vertex vIn, Vertex vOut, Class<?> klass) {
		return vIn.getId().toString()+"->"+
				klass.getSimpleName()+"->"+
				vOut.getId().toString();
	}
	
	public <T> T query(String query, HashMap<String, Object> params) {
		OGremlinHelper.global().create();
		OCommandGremlin command=new OCommandGremlin(query);
		T result=this.getGraph().getRawGraph().command(command).execute(params);
		OGremlinHelper.global().destroy();
		return result;
	}	
	
	public String toJson() throws JsonProcessingException {
		ObjectMapper mapper=new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		return mapper.writeValueAsString(this);
	}
	
	protected Object getIdFromQueryString(String id) {
		String[] parts=id.split("_");
		ORecordId rid=new ORecordId();
		rid.fromString("#"+parts[0]+":"+parts[1]);
		return rid;
	}
	
	@JsonIgnore
	public abstract boolean isBound();
	
	public abstract String getRid();
}
