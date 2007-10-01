package ComposestarEclipsePlugin.Core.BuildConfiguration;

/**
 * Class representing a type source. Used by BuildConfigurationManager to build
 * configuration-file.
 */
@Deprecated
public class TypeSource
{

	private String name = "";

	private String filename = "";

	public TypeSource(String Name, String fileName)
	{
		this.name = Name;
		this.filename = fileName;
	}

	public String getName()
	{
		return name;
	}

	public String getFileName()
	{
		return filename;
	}
}
