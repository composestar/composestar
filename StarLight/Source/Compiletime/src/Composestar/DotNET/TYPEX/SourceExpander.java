package Composestar.DotNET.TYPEX;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.PathSettings;
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;

import composestar.dotNET.tym.entities.ExpandedType;
import composestar.dotNET.tym.entities.MethodElement;

class SourceExpander
{
	private static final CPSLogger logger = CPSLogger.getCPSLogger(TYPEX.MODULE_NAME);
	
	private File destDir;

	public SourceExpander()
	{
		destDir = createDestDir();
	}

	public List<File> process(Collection<ExpandedSource> sources) throws ModuleException
	{
		try
		{
			List<File> es = new ArrayList<File>();
			for (ExpandedSource source : sources)
			{
				File destFile = expand(source);
				es.add(destFile);
			}
			
			return es;
		}
		catch (IOException e)
		{
			throw new ModuleException(
					"IOException: " + e.getMessage(), TYPEX.MODULE_NAME);
		}
	}
	
	private File expand(ExpandedSource source) throws IOException
	{
		String sourceFilename = source.getFilename();
		File destFile = getDestFile(source);

		String ext = FileUtils.getExtension(sourceFilename);
		MethodEmitter me = getMethodEmitter(ext); 

		logger.debug("Expanding '" + sourceFilename + "'...");

		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			reader = new BufferedReader(new FileReader(sourceFilename));
			writer = new BufferedWriter(new FileWriter(destFile));
			int curPos = 0;
			
			for (ExpandedType et : source.sortedTypes())
			{
				int skip = et.getEndPos() - curPos;
				curPos += skip;
				
				FileUtils.copy(reader, writer, skip);
				
				for (MethodElement method : et.getExtraMethods().getMethodList())
					me.emit(method, writer);
			}
			
			FileUtils.copy(reader, writer);

			return destFile;
		}
		finally {
			FileUtils.close(reader);
			FileUtils.close(writer);
		}
	}
	
	private File createDestDir()
	{
		PathSettings ps = Configuration.instance().getPathSettings();
		File starlightFolder = new File(ps.getPath("Base"), "Starlight");
		File destDir = new File(starlightFolder, "Expanded");
		destDir.mkdirs();
		
		return destDir;
	}

	private File getDestFile(ExpandedSource source)
	{
		File input = new File(source.getFilename());		
		return new File(destDir, input.getName());
	}

	private MethodEmitter getMethodEmitter(String ext)
	{
		if ("java".equals(ext) || "jsl".equals(ext))
			return new JSharpMethodEmitter();
		
		if ("cs".equals(ext))
			return new CSharpMethodEmitter();

		throw new IllegalArgumentException(
				"Unknown extension '" + ext + "'");
	}
}