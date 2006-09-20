/*
 * Created on 23-aug-2006
 *
 */
package Composestar.Core.INLINE.lowlevel;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.LAMA.MethodInfo;

public interface LowLevelInlineStrategy {
    public void startInline( FilterModule[] filterSet, MethodInfo method, String[] argReferences );
    public void endInline();
    
    public void startFilter( Filter filter, int jumpLabel );
    public void endFilter();
    
    public void evalCondExpr( ConditionExpression condition );
    public void beginTrueBranch();
    public void endTrueBranch();
    
    public void beginFalseBranch();
    public void endFalseBranch();
    
    public void jump( int jumpLabel );
    
    public void generateAction( ExecutionState state );
}
