package Composestar.RuntimeCore.FLIRT;

import java.io.Serializable;

import Composestar.RuntimeCore.Utils.RepositoryDeserializer;

public abstract class PlatformProvider implements Serializable
{
	public abstract void instantiatePlatform();

	public abstract RepositoryDeserializer getRepositoryDeserializer();
}
