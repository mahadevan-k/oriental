package com.bitonator.oriental;

public class EntityNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5900614362280266797L;

	//Parameterless Constructor
	public EntityNotFoundException() {}

	//Constructor that accepts a message
	public EntityNotFoundException(String message)
	{
		super(message);
	}
}
