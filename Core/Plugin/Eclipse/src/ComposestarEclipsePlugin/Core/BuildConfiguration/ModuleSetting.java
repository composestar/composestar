package ComposestarEclipsePlugin.Core.BuildConfiguration;

import java.util.HashMap;

/**
 * Class representing a compose*-module setting. Used by
 * BuildConfigurationManager to build configuration-file.
 */
@Deprecated
public class ModuleSetting
{

	public String name;

	public HashMap settings;

	public ModuleSetting()
	{
		settings = new HashMap();
	}

	public void addSetting(String key, String value)
	{
		settings.put(key, value);
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
