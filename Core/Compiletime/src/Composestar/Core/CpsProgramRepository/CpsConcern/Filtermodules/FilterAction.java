package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import Composestar.Core.RepositoryImplementation.RepositoryEntity;

public class FilterAction extends RepositoryEntity
{
	public final static int FLOW_CONTINUE = 1;

	public final static int FLOW_EXIT = 2;

	public final static int FLOW_RETURN = 3;

	public final static int MESSAGE_ORIGINAL = 1;

	public final static int MESSAGE_SUBSTITUTED = 2;

	public final static int MESSAGE_ANY = 3;

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
	 * @param flowBehaviour the flowBehaviour to set
	 */
	public void setFlowBehaviour(int flowBehaviour)
	{
		this.flowBehaviour = flowBehaviour;
	}

	/**
	 * @return the messageChangeBehaviour
	 */
	public int getMessageChangeBehaviour()
	{
		return messageChangeBehaviour;
	}

	/**
	 * @param messageChangeBehaviour the messageChangeBehaviour to set
	 */
	public void setMessageChangeBehaviour(int messageChangeBehaviour)
	{
		this.messageChangeBehaviour = messageChangeBehaviour;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName()
	{
		return fullName;
	}

	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName)
	{
		this.fullName = fullName;
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
