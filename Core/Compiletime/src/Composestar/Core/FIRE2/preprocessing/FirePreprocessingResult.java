/*
 * Created on 28-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.preprocessing;

import java.io.Serializable;

import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.FlowModel;

/**
 * @author Arjan de Roo
 */

public class FirePreprocessingResult implements Serializable
{
	private static final long serialVersionUID = 4193686682386435353L;

	private FlowModel flowModelInputFilters;

	private ExecutionModel executionModelInputFilters;

	private FlowModel flowModelOutputFilters;

	private ExecutionModel executionModelOutputFilters;

	public FirePreprocessingResult(FlowModel flowModelInputFilters, ExecutionModel executionModelInputFilters,
			FlowModel flowModelOutputFilters, ExecutionModel executionModelOutputFilters)
	{
		super();

		this.flowModelInputFilters = flowModelInputFilters;
		this.executionModelInputFilters = executionModelInputFilters;

		this.flowModelOutputFilters = flowModelOutputFilters;
		this.executionModelOutputFilters = executionModelOutputFilters;
	}

	/**
	 * @return the executionModelInputFilters
	 */
	public ExecutionModel getExecutionModelInputFilters()
	{
		return executionModelInputFilters;
	}

	/**
	 * @return the executionModelOutputFilters
	 */
	public ExecutionModel getExecutionModelOutputFilters()
	{
		return executionModelOutputFilters;
	}

	/**
	 * @return the flowModelInputFilters
	 */
	public FlowModel getFlowModelInputFilters()
	{
		return flowModelInputFilters;
	}

	/**
	 * @return the flowModelOutputFilters
	 */
	public FlowModel getFlowModelOutputFilters()
	{
		return flowModelOutputFilters;
	}
}
