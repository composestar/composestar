package Composestar.RuntimeCore.FLIRT;

import Composestar.RuntimeCore.Utils.RepositoryDeserializer;

import java.io.Serializable;

public abstract class PlatformProvider implements Serializable
{
	public abstract void instantiatePlatform();
	public abstract RepositoryDeserializer getRepositoryDeserializer();
}