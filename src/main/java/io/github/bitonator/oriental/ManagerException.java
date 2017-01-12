package io.github.bitonator.oriental;

public class ManagerException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -918396759810864054L;

	//Parameterless Constructor
	public ManagerException() {}

	//Constructor that accepts a message
	public ManagerException(String message)
	{
		super(message);
	}
}