package composestarEclipsePlugin.dialogs;

import java.util.HashSet;

import composestarEclipsePlugin.Debug;
import composestarEclipsePlugin.actions.*;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;

public class BuildDialog extends Dialog{

	private GridData gd;
	private IProject[] selection;
	private String debuggerString="";
	private String secretString=""; 
	private String incrementalString="";
	private String verifyAssembliesString="";
	private String filterModuleOrderString="";
	private Text mainText;
	private Button browseButton;
	private String mainString="";
	private String builddlString=""; 
	private String rundlString="";
	private String language="";
	private String customFilterString="";
	private Text customFilterText;
	private Text basePath;
	private Text buildPath;
	private Text outputPath;
	private String baseString="";
	private String buildString="";
	private String outputString="";
	private IProjectDescription projectFile=null;
	private IProject select= null;
	private Sources source=null;
	
	/**
	 * Constructor
	 * @param shell
	 * @param selection
	 */
	public BuildDialog(Shell shell, IProject[] selection){
		super( shell );
		this.selection = selection;
		    
		if(selection[0].getFile("BuildConfiguration.xml").exists()){
			getStandardSettings(selection);
		}
		else Debug.instance().Log("No buildConfiguration file found");
		
	}
	
	/**
	 * Configures the given shell in preparation for opening this window in it.
	 * - Sets the title of the dialog.
	 * @param newShell
	 */
	protected void configureShell(Shell newShell) {
	      super.configureShell(newShell);
	      newShell.setText("Build with Compose*");
	}
	
	protected void buttonPressed(int buttonID){
		if(buttonID==this.OK){
			if(mainString.equals("")||language.equals("")){
				Debug.instance().Log("Not everything filled in");
			}
			else super.okPressed();
		}
		if(buttonID==this.CANCEL){super.cancelPressed();} 
	}
	
	/**
	 * Creates and returns the contents of the upper part of this dialog (above the button bar).
	 * @param parent
	 */
	protected Control createDialogArea(Composite parent) {
		//TODO: implement this
		Composite controls =
            new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        controls.setLayout(layout);
        layout.numColumns = 1;
        layout.verticalSpacing = 15;
                     	        
		projectSettings(controls);
		//composestarSettings(controls);
		return controls;
	}
	
	private Composite projectSettings(Composite controls){
		Group group = new Group(controls,SWT.NULL);
	    GridLayout layout =new GridLayout();
	    source= new Sources(selection);
		group.setLayout(layout);
	    layout.numColumns = 3;
	    layout.verticalSpacing = 15;
	    group.setText("Project Settings");
		group.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL |
	         GridData.HORIZONTAL_ALIGN_FILL));
		
		Label label = new Label(group, SWT.NULL);
        label.setText("MainClass");
        
        mainText = new Text(group,
                SWT.BORDER | SWT.SINGLE);
        if(!mainString.equals("")) mainText.setText(mainString);
        mainText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				mainString = mainText.getText();
			}
		});
         browseButton = new Button(
        		 group,
	                SWT.PUSH | SWT.RIGHT);
	     browseButton.setText("Browse..");
	     browseButton.addSelectionListener(
     		new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e){
				Shell shell = new Shell();
				shell.setText("Select application startup object");
				FileDialog fd = new FileDialog(shell,SWT.OPEN);
				fd.open();
				mainString=fd.getFileName();
				try {
					mainString=source.getRelativePathtoWorkSpace(mainString);
				} catch (CoreException e1) {
					Debug.instance().Log("Not in workspace",Debug.MSG_ERROR);
					e1.printStackTrace();
				}
				mainText.setText(mainString);
			}
		});
        gd = new GridData(
	            GridData.FILL_HORIZONTAL);
        mainText.setLayoutData(gd);
	              
        //New code
        label = new Label(group, SWT.NULL);
        label.setText("basePath");
        		        
        basePath = new Text(
        		group,
                SWT.BORDER | SWT.SINGLE);
        if(!baseString.equals("")) basePath.setText(baseString);
        basePath.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				baseString = basePath.getText();
			}
		});
         Button browseBPButton = new Button(
        		 group,
	                SWT.PUSH | SWT.RIGHT);
	     browseBPButton.setText("Browse..");
	     browseBPButton.addSelectionListener(
     		new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {
				Debug.instance().Log("Default Selected");
			}
			public void widgetSelected(SelectionEvent e){
				Shell shell = new Shell();
				shell.setText("Select application startup object");
				DirectoryDialog dd = new DirectoryDialog(shell,SWT.OPEN);
				baseString=dd.open();
				basePath.setText(baseString);
			}
		});
        gd = new GridData(
	            GridData.FILL_HORIZONTAL);
	    basePath.setLayoutData(gd);
	    label = new Label(group, SWT.NULL);
        label.setText("buildPath");
        		        
        buildPath = new Text(
        		group,
                SWT.BORDER | SWT.SINGLE);
       if(!buildString.equals("")) buildPath.setText(buildString);
        buildPath.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				buildString = buildPath.getText();
			}
		});
         Button browseBuildButton = new Button(
        		 group,
	                SWT.PUSH | SWT.RIGHT);
	     browseBuildButton.setText("Browse..");
	     browseBuildButton.addSelectionListener(
	     		new SelectionListener(){
				public void widgetDefaultSelected(SelectionEvent e) {
					Debug.instance().Log("Default Selected");
				}
				public void widgetSelected(SelectionEvent e){
					Shell shell = new Shell();
					shell.setText("Select application startup object");
					DirectoryDialog dd = new DirectoryDialog(shell,SWT.OPEN);
					buildString=dd.open();
					buildPath.setText(buildString);
				}
		});
        gd = new GridData(
	            GridData.FILL_HORIZONTAL);
        buildPath.setLayoutData(gd);
        label = new Label(group, SWT.NULL);
        label.setText("outputPath");
        		        
        outputPath = new Text(
        		group,
                SWT.BORDER | SWT.SINGLE);
        if(!outputString.equals("")) outputPath.setText(outputString);
        outputPath.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				outputString = outputPath.getText();
			}
		});
         Button browseOutputButton = new Button(
        		 	group,
	                SWT.PUSH | SWT.RIGHT);
	     browseOutputButton.setText("Browse..");
	     browseOutputButton.addSelectionListener(
     		new SelectionListener(){
				public void widgetDefaultSelected(SelectionEvent e) {
					Debug.instance().Log("Default Selected");
				}
				public void widgetSelected(SelectionEvent e){
					Shell shell = new Shell();
					shell.setText("Select application startup object");
					DirectoryDialog dd = new DirectoryDialog(shell,SWT.OPEN);
					outputString=dd.open();
					outputPath.setText(outputString);
				}
			});
        gd = new GridData(
	            GridData.FILL_HORIZONTAL);
        outputPath.setLayoutData(gd);

        if(language.equals("")){
	    	 	try {
	    	 		select=selection[0];
	    	 		projectFile = select.getDescription();
				} catch (CoreException e1) {
					Debug.instance().Log("No eclipse project", Debug.MSG_ERROR);
					e1.printStackTrace();
				}
				if(projectFile!=null){
		    	 for(int i=0; i<projectFile.getNatureIds().length; i++){
						if((projectFile.getNatureIds())[i].split("jdt").length>1)language = "JAVA";
						else if((projectFile.getNatureIds())[i].split("cdt").length>1)language = "C";
		    	 }
				}
	     }
        label = new Label(group, SWT.NULL);
        label.setText("CustomFilters");
        
        customFilterText = new Text(
        		group,
                SWT.MULTI | SWT.WRAP );
        if(!customFilterString.equals("")) customFilterText.setText(customFilterString.replaceAll(java.io.File.pathSeparator,java.io.File.pathSeparator+"\n"));
        customFilterText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if(customFilterText.getText().length()==1){	
					customFilterString = customFilterText.getText();
				}
			}
		});
        
        Button browseCFButton = new Button(
       		 		group,
	                SWT.PUSH | SWT.RIGHT);
	     browseCFButton.setText("Browse..");
	     browseCFButton.addSelectionListener(
    		new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e){
				Shell shell = new Shell();
				shell.setText("Select Custom Filter library");
				FileDialog fd = new FileDialog(shell,SWT.OPEN);
				fd.open();
				customFilterString +=fd.getFilterPath()+java.io.File.separatorChar+fd.getFileName()+java.io.File.pathSeparator+"\n";
				customFilterText.setText(customFilterString);
			}
		});	 
	
					        
		/** removed from the interface is already select from the project
		languageSelect = new List(
	                controls,
	                SWT.SINGLE);
	     languageSelect.add("C");
	     languageSelect.add("JAVA");
	     if(!language.equals("")){
	    	 for(int i =0; i<languageSelect.getItems().length; i++){
		     		if((languageSelect.getItems()[i]).equals(language)){
		     			languageSelect.select(i);
		     		}
		     	}
	     }
	     		    	
	     languageSelect.addSelectionListener(
	     		new SelectionListener(){
				public void widgetDefaultSelected(SelectionEvent e) {
					language=languageSelect.getSelection()[0];
				}
				public void widgetSelected(SelectionEvent e){
					language=languageSelect.getSelection()[0];
					
				}
	     });
	     gd = new GridData(
	            GridData.FILL_HORIZONTAL);
	      languageSelect.setLayoutData(gd);	    
	      **/
		
		return controls;
	}
		
    private void getStandardSettings(IProject[] selection){
    	StandardSettings ss;
    	String projectLocation=selection[0].getProject().getLocation().toString();
    	ss = new StandardSettings(projectLocation);
		ss.run();
		customFilterString=ss.getCustomFilterString();
		mainString=ss.getMainString();
		builddlString=ss.getBuilddlString(); 
		rundlString=ss.getRundlString();
		language=ss.getLanguage();
		baseString=ss.getBasePath();
		buildString=ss.getBuildPath();
		outputString=ss.getOutputPath();	
		debuggerString=ss.getDebuggerString();
		secretString=ss.getSecretString(); 
		incrementalString=ss.getIncrementalString();
		verifyAssembliesString=ss.getVerifyAssembliesString();
		filterModuleOrderString=ss.getFilterModuleOrderString();
		
		if(baseString==null||baseString.equals("")){
			baseString=projectLocation;
        	Debug.instance().Log("BasePath was not set, Standard value applied",Debug.MSG_WARNING);
        }
		if(outputString==null||outputString.equals("")){
			outputString=projectLocation;
        	Debug.instance().Log("OutputPath was not set, Standard value applied",Debug.MSG_WARNING);
        }
		if(mainString==null){
			mainString="";
        	Debug.instance().Log("MainClass was not set, Standard value applied",Debug.MSG_WARNING);
        }
		if(builddlString==null||builddlString.equals("")){
			builddlString="5";
        	Debug.instance().Log("buildDebugLevel was not set, Standard value applied",Debug.MSG_WARNING);
        }
		if(rundlString==null||rundlString.equals("")){
			rundlString="5";
        	Debug.instance().Log("runDebugLevel was not set, Standard value applied",Debug.MSG_WARNING);
        }
		if(buildString==null||buildString.equals("")){
			buildString=projectLocation;
        	Debug.instance().Log("buildPath was not set, Standard value applied",Debug.MSG_WARNING);
        }
		if(language==null){
			language="C";
        	Debug.instance().Log("Language was not set, Standard value applied",Debug.MSG_WARNING);
        }
	}
    
    public String getRundl() {
        return rundlString;
    }
    public String getBuilddl() {
        return builddlString;
    }
    
    public String getLanguage(){
    	return language;
    }
    
    public String getMain() {
    	return mainString;
    }
    public String getBuildPath(){
    	if(buildString.endsWith(""+java.io.File.separatorChar))		
    			return buildString;
    	else return buildString+java.io.File.separatorChar;
    }
    public String getOutputPath(){
    	if(outputString.endsWith(""+java.io.File.separatorChar))		
			return outputString;
    	else return outputString+java.io.File.separatorChar;
    }
    public String getBasePath(){
    	if(baseString.endsWith(""+java.io.File.separatorChar))		
			return baseString;
    	else return baseString+java.io.File.separatorChar;
    }
    public String getCustomFilterString(){
    	return customFilterString;
    }
    
    public String getDebuggerType(){
    	return debuggerString;
    }
    
    public void setDebuggerType(String debuggerString){
    	this.debuggerString=debuggerString;
    }

	public String getFilterModuleOrderString() {
		return filterModuleOrderString;
	}

	public void setFilterModuleOrderString(String filterModuleOrderString) {
		this.filterModuleOrderString = filterModuleOrderString;
	}

	public String getIncrementalString() {
		return incrementalString;
	}

	public void setIncrementalString(String incrementalString) {
		this.incrementalString = incrementalString;
	}

	public String getSecretString() {
		return secretString;
	}

	public void setSecretString(String secretString) {
		this.secretString = secretString;
	}

	public String getVerifyAssembliesString() {
		return verifyAssembliesString;
	}

	public void setVerifyAssembliesString(String verifyAssembliesString) {
		this.verifyAssembliesString = verifyAssembliesString;
	}
	
}
