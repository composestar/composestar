package Composestar.RuntimeCore.FLIRT.Actions;

import Composestar.RuntimeCore.FLIRT.Message.Message;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: ContinueToNextFilterAction.java 361 2006-06-25 19:24:10Z wminnen $
 * 
 * Continues to the next filter.
 * This action does nothing, and allows for the continuing of the filtering of the 
 * message
 */
public class ContinueFilterEvaluationAction extends ComposeStarAction {
	public final static ComposeStarAction NOP = new ContinueFilterEvaluationAction(); 
	
	public void execute(Message nope){
	}
}
