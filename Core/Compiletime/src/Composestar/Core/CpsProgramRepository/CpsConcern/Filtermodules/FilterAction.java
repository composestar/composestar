package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import Composestar.Core.RepositoryImplementation.RepositoryEntity;

public class FilterAction extends RepositoryEntity
{
	private static final long serialVersionUID = 1318091104465614214L;

	public static final int FLOW_CONTINUE = 1;

	public static final int FLOW_EXIT = 2;

	public static final int FLOW_RETURN = 3;

	public static final int MESSAGE_ORIGINAL = 1;

	public static final int MESSAGE_SUBSTITUTED = 2;

	public static final int MESSAGE_ANY = 3;

	private int flowBehaviour;

	private int messageChangeBehaviour;

	private String name;

	private String fullName;

	private String resourceOperations;

	/**
	 * @return the flowBehaviour
	 */
	public int getFlowBehaviour()
	{
		return flowBehaviour;
	}

	/**
	 * @param value the flowBehaviour to set
	 */
	public void setFlowBehaviour(int value)
	{
		flowBehaviour = value;
	}

	/**
	 * @return the messageChangeBehaviour
	 */
	public int getMessageChangeBehaviour()
	{
		return messageChangeBehaviour;
	}

	/**
	 * @param value the messageChangeBehaviour to set
	 */
	public void setMessageChangeBehaviour(int value)
	{
		messageChangeBehaviour = value;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param value the name to set
	 */
	public void setName(String value)
	{
		name = value;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName()
	{
		return fullName;
	}

	/**
	 * @param value the fullName to set
	 */
	public void setFullName(String value)
	{
		fullName = value;
	}

	/**
	 * @return
	 */
	public String getResourceOperations()
	{
		return resourceOperations;
	}

	/**
	 * @param
	 */
	public void setResourceOperations(String resop)
	{
		resourceOperations = resop;
	}
}
