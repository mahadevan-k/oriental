package com.bitonator.oriental;

public class EntityCreationException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1617076725649791121L;

	//Parameterless Constructor
	public EntityCreationException() {}

	//Constructor that accepts a message
	public EntityCreationException(String message)
	{
		super(message);
	}
}
