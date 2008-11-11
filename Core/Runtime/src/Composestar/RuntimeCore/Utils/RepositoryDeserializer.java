package Composestar.RuntimeCore.Utils;

import java.io.Serializable;

import Composestar.Core.CpsRepository2.Repository;

public abstract class RepositoryDeserializer implements Serializable
{
	private static final long serialVersionUID = 4886132369677137429L;

	public abstract Repository deserialize(String file);
}
