package Composestar.DotNET.ASTRA2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.CpsProgramRepository.PlatformRepresentation;
import Composestar.Core.CpsProgramRepository.Signature;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.PathSettings;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.DotNET.LAMA.DotNETMethodInfo;
import Composestar.DotNET.LAMA.DotNETType;
import Composestar.DotNET.MASTER.StarLightMaster;
import Composestar.Utils.CommandLineExecutor;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;
import Composestar.Utils.StringUtils;

import composestar.dotNET.tym.entities.ArrayOfAssemblyConfig;
import composestar.dotNET.tym.entities.ArrayOfExpandedType;
import composestar.dotNET.tym.entities.ArrayOfParameterElement;
import composestar.dotNET.tym.entities.AssemblyConfig;
import composestar.dotNET.tym.entities.ConfigurationContainer;
import composestar.dotNET.tym.entities.ExpandedType;
import composestar.dotNET.tym.entities.MethodElement;
import composestar.dotNET.tym.entities.ParameterElement;
import composestar.dotNET.tym.entities.SignaturesDocument;

public class ASTRA2 implements CTCommonModule
{
	public static final String MODULE_NAME = "ASTRA2";

	private DataStore dataStore;
	
	public ASTRA2()
	{
		dataStore = DataStore.instance();
	}

	public void run(CommonResources resources) throws ModuleException
	{
		if (!resources.getBoolean("signaturesmodified"))
		{
			Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, "No need to transform assemblies");
			return;
		}
				
		PathSettings ps = Configuration.instance().getPathSettings();		
		File baseDir = new File(ps.getPath("Base"), "StarLight");
		File signatures = new File(baseDir, "signatures.xml.gzip");
		
		storeSignaturesDocument(signatures);

		// invoke SigExpander
		List cmd = new ArrayList();
		cmd.add(getExecutable());
		cmd.add(signatures.getAbsolutePath());
		cmd.addAll(getDummyAssemblies()); // usually just one
		
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Command line: " + StringUtils.join(cmd));
		
		CommandLineExecutor cle = new CommandLineExecutor();
		int result = cle.exec(cmd);
		
		if (result != 0)
		{
			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Output from SigExpander: " + cle.outputNormal());
			throw new ModuleException("SigExpander failed with error " + result, MODULE_NAME);
		}
	}
	
	private List getDummyAssemblies()
	{
		List result = new ArrayList();
		ConfigurationContainer config = StarLightMaster.getConfigContainer();
		ArrayOfAssemblyConfig assemblies = config.getAssemblies();
		for (int i = 0; i < assemblies.sizeOfAssemblyConfigArray(); i++)
		{
			AssemblyConfig ac = assemblies.getAssemblyConfigArray(i);
			if (ac.getIsDummy())
			{
				result.add(ac.getFilename());
			}
		}
		return result;
	}
	
	private String getExecutable() throws ModuleException
	{
	//	PathSettings ps = Configuration.instance().getPathSettings();		
	//	File baseDir = new File(ps.getPath("Composestar"), "CoreElements");
	//	File cps = new File("C:\\Program Files\\MSBuild\\StarLight\\0.2");
		File cps = new File("C:\\CPS\\StarLight\\Source\\SigExpander\\bin\\Debug");
		File exe = new File(cps, "Composestar.StarLight.SigExpander.exe");
		
		if (! exe.exists())
			throw new ModuleException("Executable does not exist: " + exe, MODULE_NAME);
		
		return exe.getAbsolutePath();
	}
	
	private void storeSignaturesDocument(File file) throws ModuleException
	{
		SignaturesDocument doc = createSignaturesDocument();
		
		OutputStream os = null;
		try
		{
			os = new GZIPOutputStream(new FileOutputStream(file));
			doc.save(os);
		}
		catch (IOException e)
		{
			throw new ModuleException(
					"IO error while writing signatures to '" + file + "'" +
					": " + e.getMessage(), MODULE_NAME);
		}
		finally
		{
			FileUtils.close(os);		
		}
	}
	
	private SignaturesDocument createSignaturesDocument() throws ModuleException
	{
		SignaturesDocument doc = SignaturesDocument.Factory.newInstance();
		ArrayOfExpandedType ets = doc.addNewSignatures().addNewExpandedTypes();
		
		Iterator concernIt = dataStore.getAllInstancesOf(Concern.class);
		while (concernIt.hasNext())
		{
			Concern concern = (Concern) concernIt.next();
			PlatformRepresentation pr = concern.getPlatformRepresentation();

			if (pr == null || !(pr instanceof DotNETType))
				continue;
			
			DotNETType dnt = (DotNETType) pr;
			Signature sig = concern.getSignature();
			List methods = sig.getMethods(MethodWrapper.ADDED);
			
			if (methods.size() > 0)
			{
				ExpandedType et = ets.addNewType();
				et.setName(dnt.fullName());

				addMethods(et, methods);
			}
		}
		
		return doc;
	}
	
	private void addMethods(ExpandedType et, List methods)
	{
		Iterator methIt = methods.iterator();
		while (methIt.hasNext())
		{
			DotNETMethodInfo mi = (DotNETMethodInfo) methIt.next();					
			
			MethodElement method = et.addNewExtraMethods().addNewMethod();
			method.setName(mi.name());
			method.setReturnType(mi.returnTypeName());
			
			ArrayOfParameterElement pars = method.addNewParameters();
			Iterator it = mi.getParameters().iterator();
			while (it.hasNext())
			{
				ParameterInfo pi = (ParameterInfo)it.next();
				ParameterElement pe = pars.addNewParameter();
				pe.setName(pi.name());
				pe.setType(pi.getParameterTypeString());
			}
		}
	}
}
