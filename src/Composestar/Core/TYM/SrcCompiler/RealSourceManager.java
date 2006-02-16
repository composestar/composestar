/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: RealSourceManager.java,v 1.1 2006/02/13 11:16:57 pascal Exp $
 */

package Composestar.Core.TYM.SrcCompiler;

import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Exception.*;


/**
 * Takes care of compiling the real user sources. Links with the dummies and takes 
 * care not to destroy them during compilation.
 */
public interface RealSourceManager extends CTCommonModule {
    public abstract void compileSource(String sourceFile, String buildPath, String targetFile, String compilerOptions, String compilerPath, Compiler comp) throws ModuleException;
     
}
