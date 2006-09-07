/*
 * Created on 28-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.preprocessing;

import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.FlowModel;
import Composestar.Core.RepositoryImplementation.RepositoryEntity;

/**
 * 
 *
 * @author Arjan de Roo
 */
public class FirePreprocessingResult extends RepositoryEntity {
    private FlowModel flowModel;
    private ExecutionModel executionModel;
    
    public FirePreprocessingResult( 
            FlowModel flowModel, ExecutionModel executionModel )
    {
        super();
        
        this.flowModel = flowModel;
        this.executionModel = executionModel;
    }
    
    
    /**
     * @return Returns the executionModel.
     */
    public ExecutionModel getExecutionModel() {
        return executionModel;
    }
    /**
     * @return Returns the flowModel.
     */
    public FlowModel getFlowModel() {
        return flowModel;
    }
}
