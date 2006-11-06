package Composestar.RuntimeCore.Utils;

import java.io.Serializable;

import Composestar.Core.RepositoryImplementation.DataStore;

public abstract class RepositoryDeserializer implements Serializable
{
	public abstract DataStore deserialize(String file);
}
