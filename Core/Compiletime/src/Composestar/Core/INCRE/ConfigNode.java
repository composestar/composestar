package Composestar.Core.INCRE;

import Composestar.Utils.Logging.CPSLogger;

public class ConfigNode extends Node
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(INCRE.MODULE_NAME);

	private static final long serialVersionUID = 1676465661039216621L;

	public ConfigNode(String objectref)
	{
		super(objectref);
	}

	/**
	 * @return the referenced configuration object
	 * @param Object obj
	 */
	@Override
	public Object visit(Object obj)
	{
		try
		{
			// because INCRE is broken anyway
			// INCRE incre = INCRE.instance();
			String config = ""; // incre.getConfiguration(reference);
			if (config.length() == 0)
			{
				logger.debug("INCRE::ConfigNode EMPTY value for configuration " + reference);
				return "EMPTY_CONFIG";
			}
			else
			{
				return config;
			}

		}
		catch (Exception excep)
		{
			logger.warn("Cannot find value for config node " + reference + " due to " + excep.getMessage());
			return null;
		}
	}

	/**
	 * @return an unique id for a referenced configuration
	 */
	@Override
	public String getUniqueID(Object obj)
	{
		return reference;
	}
}
