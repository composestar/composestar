package Composestar.Repository;

public class RepositoryAccess {
	private String _databaseFileName;
	private ConfigurationAccess _configuration;
	
	public RepositoryAccess() 
	{
		_configuration = new ConfigurationAccess();
	}
	
	public void setDatabaseFileName(String fileName)
	{
		_databaseFileName = fileName;
		DataStoreContainer.setYapFileName(_databaseFileName);
	}
	
	public ConfigurationAccess Configuration() { return _configuration; }
	
	
}
