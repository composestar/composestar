package Composestar.Repository;

public class RepositoryAccess {
	private String _databaseFileName;
	private ConfigurationAccess _configuration;
	private LanguageModelAccess _languageModel;
	
	public RepositoryAccess() 
	{
		_configuration = new ConfigurationAccess();
		_languageModel = new LanguageModelAccess();
	}
	
	public void setDatabaseFileName(String fileName)
	{
		_databaseFileName = fileName;
		DataStoreContainer.setYapFileName(_databaseFileName);
	}
	
	public ConfigurationAccess Configuration() { return _configuration; }
	
	public LanguageModelAccess LanguageModel() { return _languageModel; }
}
