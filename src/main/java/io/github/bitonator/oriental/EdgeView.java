package io.github.bitonator.oriental;

import java.util.HashMap;

import io.github.bitonator.oriental.EdgeEntity;
import io.github.bitonator.oriental.VertexEntity;

public abstract class EdgeView {
	public abstract void setup(HashMap<String, Object> properties);
	public abstract <T extends EdgeEntity> void bind(T entity);
}
