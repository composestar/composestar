/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.COPPER;

import antlr.CommonAST;

class ParseResult
{
	public String filename;
	public CommonAST ast;
	public int startPos;
	public String embeddedCode;
}
