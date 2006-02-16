using System;
using System.Xml;
using System.Collections;
using System.Collections.Specialized;
using EnvDTE;
using Ini;
using VSLangProj;

namespace ComposestarVSAddin 
{
	/// <summary>
	/// Summary description for DummyGenerator.
	/// </summary>
	public class AttributeManager : AbstractManager
	{
		private IniFile inifile;
		private ArrayList attributes;
		private XmlTextWriter writer;


		public AttributeManager(IniFile inifile) : base (inifile)
		{
			this.inifile = inifile;
			attributes = new ArrayList();
		}

		private void ProcessProjectFiles(ProjectItems projectitems) 
		{
			foreach (ProjectItem projectitem in projectitems)
			{
				// this is a folder, process the files in it
				if (projectitem.ProjectItems.Count > 0) 
				{
					ProcessProjectFiles(projectitem.ProjectItems);
				}
				else // this is a file
				{
					// fetch the filecodemodel
					FileCodeModel cm = projectitem.FileCodeModel;
					
					if( cm != null ) // check if it is a source file
					{
						recurseElements(cm.CodeElements);
					}
				}
			}
		}

		public override void run(_DTE applicationObject, vsBuildScope scope, vsBuildAction action) 
		{
			String outFolder = inifile.IniReadValue("Common","TempFolder");
			writer = new XmlTextWriter(outFolder + "attributes.xml", null);
			writer.WriteStartDocument();
			writer.Formatting = Formatting.Indented;
			writer.WriteStartElement( "Attributes" );

			foreach( Project project in applicationObject.Solution.Projects ) 
			{
				if( project != null  && project.Properties != null)
				{
					// Cast to VSProject to access the references, luckily C#, J# and VB projects are casted
					ProcessProjectFiles(project.ProjectItems);
				}
			}
			
			writer.WriteEndElement();
			writer.Close();
		}
		
		public void writeAttributes(CodeElements attributes, String targetType, String targetName)
		{
			foreach( CodeAttribute attribute in attributes )
			{
				writer.WriteStartElement("Attribute");
				writer.WriteStartAttribute("","type","");
				writer.WriteString(attribute.FullName);
				writer.WriteEndAttribute();
				writer.WriteStartAttribute("","value","");
				writer.WriteString(attribute.Value);
				writer.WriteEndAttribute();
				writer.WriteStartAttribute("","target","");
				writer.WriteString(targetType);
				writer.WriteEndAttribute();
				writer.WriteStartAttribute("","location","");
				writer.WriteString(targetName);
				writer.WriteEndAttribute();
				
				writer.WriteEndElement();
			}
		}

		///
		/// <returns>true if in this element contains the startup object</returns>
		///
		public void recurseElements(CodeElements codeElements) 
		{
			foreach(CodeElement codeElement in codeElements) 
			{
				if (codeElement.Kind == vsCMElement.vsCMElementNamespace)
				{
					CodeNamespace nameSpace = (CodeNamespace)codeElement;
					recurseElements(nameSpace.Members);
				}
				else if( codeElement.Kind == vsCMElement.vsCMElementClass || codeElement.Kind == vsCMElement.vsCMElementModule) 
				{
					CodeClass codeClass = (CodeClass) codeElement;
					if( codeClass.Attributes.Count != 0 )
					{
						writeAttributes(codeClass.Attributes, "Type", codeClass.FullName);
					}
					recurseElements(codeClass.Members);
				}
				else if( codeElement.Kind == vsCMElement.vsCMElementInterface )
				{
					CodeInterface codeInterface = (CodeInterface) codeElement;
					if( codeInterface.Attributes.Count != 0 )
					{
						writeAttributes(codeInterface.Attributes, "Type" , codeInterface.FullName);
					}
					recurseElements(codeInterface.Members);
				}
				else if( codeElement.Kind == vsCMElement.vsCMElementFunction )
				{
					CodeFunction codeFunction = (CodeFunction) codeElement;
					if( codeFunction.Attributes.Count != 0 )
					{
						writeAttributes(codeFunction.Attributes, "Method", codeFunction.FullName);
					}
					recurseElements(codeFunction.Parameters);
				}
				else if( codeElement.Kind == vsCMElement.vsCMElementParameter )
				{
					CodeParameter codeParameter = (CodeParameter) codeElement;
					if( codeParameter.Attributes.Count != 0 )
					{
						writeAttributes(codeParameter.Attributes, "Parameter", codeParameter.FullName);
					}
				}
				else if( codeElement.Kind == vsCMElement.vsCMElementProperty )
				{
					CodeProperty codeProperty = (CodeProperty) codeElement;
					if( codeProperty.Attributes.Count != 0 )
					{
						writeAttributes(codeProperty.Attributes, "Field", codeProperty.FullName);
					}
				}
				else if( codeElement.Kind == vsCMElement.vsCMElementVariable )
				{
					CodeVariable codeVariable = (CodeVariable) codeElement;
					if( codeVariable.Attributes.Count != 0 )
					{
						writeAttributes(codeVariable.Attributes, "Field" , codeVariable.FullName);
					}
				}
				else
				{
					System.Windows.Forms.MessageBox.Show("Found attribute in unknown code element: " + codeElement.GetType().ToString());
				}
			}
		}

	}
}