package Composestar.Ant.taskdefs;

import java.util.Vector;
import java.util.Iterator;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.taskdefs.XSLTProcess;
import org.apache.tools.ant.taskdefs.XSLTProcess.Param;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.PatternSet.NameEntry;
import java.io.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;

/**
 * Compiles a devenv solution with Compose*
 * 
 * @author Michiel Hendriks
 */
public class cstarVsp extends Task {
	protected static final String BUILD_CONFIGURATION_XML = "BuildConfiguration.xml";
	
	protected Vector fileSets = new Vector();
	
	protected boolean failOnError = true;
	protected boolean failOnFirstError = false;
	
	protected String conversionXslt;
	protected String composestarBase;
	protected String master;
	
	protected FileSet cstarJars;
	
	// internals	
	protected int cntTotal;
	protected int cntSuccess;
	protected int cntFail;
	
	public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }
	
	public void setFailOnFirstError(boolean failOnFirstError) {
        this.failOnFirstError = failOnFirstError;
    }
	
	public void setConversionXslt(String conversionXslt) {
        this.conversionXslt = conversionXslt;
    }
	
	public void setMaster(String master) {
        this.master = master;
    }
	
	public void setComposestarBase(String composestarBase) {
        this.composestarBase = composestarBase;
    }
	
	public void addFileset(FileSet set) {
		fileSets.add(set);
    }
	
	public void execute() throws BuildException {	
		cntTotal = 0;
		cntSuccess = 0;
		cntFail = 0;
		
		cstarJars = new FileSet();
		cstarJars.setDir(new File(composestarBase+File.separator+"Binaries"));
		NameEntry inc = cstarJars.createInclude();
		inc.setName("*.jar");
		
		for (Iterator it = fileSets.iterator(); it.hasNext(); /*nop*/ ) {
			FileSet fileSet = (FileSet) it.next();
			DirectoryScanner ds = fileSet.getDirectoryScanner(this.getProject());
			String[] files = ds.getIncludedFiles();
			for (int i = 0; i < files.length; i++) {
				compileProject(ds.getBasedir().getPath()+File.separator+files[i]);
			}
		}
		
		getProject().log(this, "Compiled "+cntTotal+" project(s); success: "+cntSuccess+"; failed: "+cntFail+"; ratio: "+(cntSuccess*100/cntTotal)+"%", Project.MSG_INFO);
		if (failOnError && (cntFail > 0)) {
			throw new BuildException("Compilation of "+cntFail+" project(s) failed.");
		}
	}
		
	protected void compileProject(String project) throws BuildException {
		cntTotal++;
		getProject().log(this, "Building Compose* project: "+project, Project.MSG_INFO);
		
		File projectFile = new File(project);
		File buildXML = new File(projectFile.getParent()+File.separator+BUILD_CONFIGURATION_XML);
		
		try {		
			XSLTProcess xslt = (XSLTProcess) getProject().createTask("xslt");
			xslt.init();
			xslt.setIn(projectFile);
			xslt.setOut(buildXML);
			xslt.setStyle(conversionXslt);
			Param param = xslt.createParam();
			param.setName("basepath");
			param.setExpression(projectFile.getParent()+File.separator);
			param = xslt.createParam();
			param.setName("composestarpath");
			param.setExpression(composestarBase+"/");
			xslt.execute();
			
			if (!buildXML.exists()) throw new Exception(buildXML.getName()+" does not exist.");
			
			Java java = (Java) getProject().createTask("java");
			java.init();
			java.setDir(projectFile.getParentFile());
			java.setClassname(master);
			Argument arg = java.createArg();
			arg.setValue(buildXML.toString());
			Path cpath = java.createClasspath();
			cpath.addFileset(cstarJars);
			java.setFork(true);
			java.setOutput(new File(projectFile.getParent()+File.separator+"buildlog.txt"));
			
			int err = java.executeJava();
			if (err != 0) {
                throw new Exception("Exit code is not zero");
            }
			
			cntSuccess++;
		}
		catch (Exception e) {
			cntFail++;
			if (failOnFirstError) {
				throw new BuildException("Compilation of project "+project+" failed; "+e.getMessage());
			}
			else {
				getProject().log(this, "Compilation of project "+project+" failed; "+e.getMessage(), Project.MSG_ERR);
			}
		}
	}
}
