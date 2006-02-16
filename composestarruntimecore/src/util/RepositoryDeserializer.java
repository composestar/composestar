package Composestar.RuntimeCore.Utils;

import Composestar.Core.RepositoryImplementation.DataStore;

import java.io.Serializable;

public abstract class RepositoryDeserializer implements Serializable
{
	public abstract DataStore deserialize(String file);
}