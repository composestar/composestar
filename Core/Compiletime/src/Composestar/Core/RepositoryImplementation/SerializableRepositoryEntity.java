/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.RepositoryImplementation;

import java.io.Serializable;

//
// !! Compose* Runtime Warning !!
//
// This class is referenced in the Compose* Runtime for .NET 1.1
// Do not use Java features added after Java 2.0
//

/**
 * Defines an entity that should be serialized into the repository. Only objects
 * that are needed during runtime should implement this interface. It is not
 * required to implement this interface to add it to the repository at
 * compiletime
 */
public interface SerializableRepositoryEntity extends Serializable
{
}
