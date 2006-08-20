/*
 * Created on 12-jun-2006
 *
 */
package Composestar.Core.FIRE2.util.regex;

import Composestar.Core.FIRE2.model.ExecutionTransition;

/**
 * 
 *
 * @author Arjan de Roo
 */
public interface Labeler {
    public LabelSequence getLabels( ExecutionTransition transition );
}
