package com.bitonator.oriental;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class ThreadSafeGraph {
	private OrientGraph graph;
	
	public ThreadSafeGraph(OrientGraph graph) {
		this.graph=graph;
	}
	
	public OrientGraph getGraph() {
		ODatabaseRecordThreadLocal.INSTANCE.set(graph.getRawGraph());
		return this.graph;
	}
	
	public void shutdown() {
		graph.shutdown();
	}
}