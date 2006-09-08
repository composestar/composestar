package Composestar.RuntimeCore.Utils;

public abstract class PlatformProvider {
	public abstract void instantiatePlatform();
	public abstract RepositoryDeserializer getRepositoryDeserializer();
}
