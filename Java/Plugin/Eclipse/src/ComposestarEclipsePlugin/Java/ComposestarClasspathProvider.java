package ComposestarEclipsePlugin.Java;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.StandardClasspathProvider;

import ComposestarEclipsePlugin.Core.ComposestarEclipsePluginPlugin;
import ComposestarEclipsePlugin.Core.Utils.FileUtils;


public class ComposestarClasspathProvider extends StandardClasspathProvider
{
	public IRuntimeClasspathEntry[] computeUnresolvedClasspath(ILaunchConfiguration configuration) throws CoreException {
		boolean useDefault = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, true);
		if (useDefault) {
			IJavaProject proj = JavaRuntime.getJavaProject(configuration);
			IRuntimeClasspathEntry jreEntry = JavaRuntime.computeJREEntry(configuration);
			
			final IPath composestarCorePath = new Path(FileUtils.fixFilename(ComposestarEclipsePluginPlugin.getAbsolutePath("/binaries/ComposestarCORE.jar")));
			IRuntimeClasspathEntry cstarCore = JavaRuntime.newArchiveRuntimeClasspathEntry(composestarCorePath);
			
			final IPath composestarJavaPath = new Path(FileUtils.fixFilename(ComposestarEclipsePluginPlugin.getAbsolutePath("/binaries/ComposestarJava.jar")));
			IRuntimeClasspathEntry cstarJava = JavaRuntime.newArchiveRuntimeClasspathEntry(composestarJavaPath);
			
			final IPath prologPath = new Path(FileUtils.fixFilename(ComposestarEclipsePluginPlugin.getAbsolutePath("/binaries/prolog/prolog.jar")));
			IRuntimeClasspathEntry prolog = JavaRuntime.newArchiveRuntimeClasspathEntry(prologPath);
			
			if (proj == null) {
				//no project - use default libraries
				if (jreEntry == null) {
					return new IRuntimeClasspathEntry[]{cstarCore,cstarJava,prolog};
				}
				return new IRuntimeClasspathEntry[]{jreEntry,cstarCore,cstarJava,prolog};				
			}
			IRuntimeClasspathEntry[] entries = JavaRuntime.computeUnresolvedRuntimeClasspath(proj);
			// replace project JRE with config's JRE
			IRuntimeClasspathEntry projEntry = JavaRuntime.computeJREEntry(proj);
			if (jreEntry != null && projEntry != null) {
				if (!jreEntry.equals(projEntry)) {
					for (int i = 0; i < entries.length; i++) {
						IRuntimeClasspathEntry entry = entries[i];
						if (entry.equals(projEntry)) {
							entries[i] = jreEntry;
							return entries;
						}
					}
				}
			}
			
			//add compose* runtime libraries
			IRuntimeClasspathEntry[] updatedEntries = new IRuntimeClasspathEntry[entries.length + 3];
			for (int i = 0; i < entries.length; i++) {
				IRuntimeClasspathEntry entry = entries[i];
				updatedEntries[i] = entry;
			}
			updatedEntries[entries.length] = cstarCore;
			updatedEntries[entries.length + 1] = cstarJava;
			updatedEntries[entries.length + 2] = prolog;
			
			return updatedEntries;
		}
		// recover persisted classpath
		return recoverRuntimePath(configuration, IJavaLaunchConfigurationConstants.ATTR_CLASSPATH);
	}
}
