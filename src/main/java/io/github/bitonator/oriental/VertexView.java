package io.github.bitonator.oriental;

import java.util.HashMap;

import io.github.bitonator.oriental.VertexEntity;

public abstract class VertexView {
	public abstract void setup(HashMap<String, Object> properties);
	public abstract <T extends VertexEntity> void bind(T entity);
}
