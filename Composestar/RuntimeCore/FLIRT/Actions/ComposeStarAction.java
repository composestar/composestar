package Composestar.RuntimeCore.FLIRT.Actions;

import Composestar.RuntimeCore.FLIRT.Message.Message;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: ComposeStarAction.java 361 2006-06-25 19:24:10Z wminnen $
 * 
 * Parent class of all Actions
 * Actions dictate the result of a message passing by a filter.
 * In particular, an action can just be to pass on to the next filter
 */
public abstract class ComposeStarAction {
	public abstract void execute(Message message);
}
