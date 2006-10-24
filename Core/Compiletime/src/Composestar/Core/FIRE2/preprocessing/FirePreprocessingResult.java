/*
 * Created on 28-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.preprocessing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.FlowModel;
import Composestar.Core.RepositoryImplementation.RepositoryEntity;

/**
 * 
 *
 * @author Arjan de Roo
 */
public class FirePreprocessingResult extends RepositoryEntity {
    private FlowModel flowModelInputFilters;
    private ExecutionModel executionModelInputFilters;
    private FlowModel flowModelOutputFilters;
    private ExecutionModel executionModelOutputFilters;
    
    public FirePreprocessingResult( 
            FlowModel flowModelInputFilters, ExecutionModel executionModelInputFilters,
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
    public ExecutionModel getExecutionModelInputFilters(){
        return executionModelInputFilters;
    }

    /**
     * @return the executionModelOutputFilters
     */
    public ExecutionModel getExecutionModelOutputFilters(){
        return executionModelOutputFilters;
    }

    /**
     * @return the flowModelInputFilters
     */
    public FlowModel getFlowModelInputFilters(){
        return flowModelInputFilters;
    }

    /**
     * @return the flowModelOutputFilters
     */
    public FlowModel getFlowModelOutputFilters(){
        return flowModelOutputFilters;
    }
    
    /**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		
	}

	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		
	}
}
