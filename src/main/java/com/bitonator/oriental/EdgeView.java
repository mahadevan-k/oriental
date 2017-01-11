package com.bitonator.oriental;

import java.util.HashMap;

import com.bitonator.oriental.EdgeEntity;
import com.bitonator.oriental.VertexEntity;

public abstract class EdgeView {
	public abstract void setup(HashMap<String, Object> properties);
	public abstract <T extends EdgeEntity> void bind(T entity);
}
