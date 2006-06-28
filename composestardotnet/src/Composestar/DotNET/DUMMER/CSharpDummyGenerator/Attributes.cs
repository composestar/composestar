using System.Collections;
using System.Xml;
using System.IO;

namespace DDW.CSharp
{
	public class AttributeState
	{
		private static string sNamespace;
		private static string sClass;
		private static string sMethod;
		private static string sField;
		private static TargetType targetType;

		public enum TargetType
		{
			TargetClass = 1,
			TargetMethod = 2,
			TargetField = 3,
			TargetInterface = 4
		}

		public static TargetType Type
		{
			get { return targetType; }
			set { targetType = value; }
		}

		public static string TypeAsString
		{
			get
			{
				switch (targetType)
				{
					case TargetType.TargetClass :
					case TargetType.TargetInterface: 
						return "Type";
					case TargetType.TargetMethod : 
						return "Method";
					case TargetType.TargetField : 
						return "Field";
					default: 
						return "unknown";
				}
			}
		}

		public static string Namespace
		{
			get { return sNamespace; }
			set { sNamespace = value; }
		}

		public static string Class
		{
			get { return sClass; }
			set { sClass = value; }
		}
		
		public static string Method
		{
			get { return sMethod; }
			set { sMethod = value; }
		}
		
		public static string Field
		{
			get { return sField; }
			set { sField = value; }
		}

		public static string Target
		{
			get
			{
				switch (targetType)
				{
					case TargetType.TargetInterface:
					case TargetType.TargetClass:
						return sNamespace + "." + sClass;
					case TargetType.TargetMethod:
						return sNamespace + "." + sClass + "." + sMethod;
					case TargetType.TargetField:
						return sNamespace + "." + sClass + "." + sField;
					default:
						return "<unknown annotation type>";
				}
			}
		}
	}

	public class AttributeRepresentation
	{
		private string typeName;
		private string constructorValues;
		private string targetLocation;
		private string targetType;

		public string TypeName
		{
			get { return typeName; }
			set { typeName = value; }
		}

		public string ConstructorValues
		{
			get { return constructorValues; }
			set { constructorValues = value; }
		}

		public string TargetLocation
		{
			get { return targetLocation; }
			set { targetLocation = value; }
		}

		public string TargetType
		{
			get { return targetType; }
			set { targetType = value; }
		}
	}

	public class AttributeWriter
	{
		private ArrayList listAttributes;
		private static AttributeWriter instance;

		public static AttributeWriter Instance
		{
			get
			{
				if (null == instance)
					instance = new AttributeWriter();
				return instance;
			}
		}

		public AttributeWriter()
		{
			listAttributes = new ArrayList();
		}

		public void addAttribute(AttributeRepresentation ar)
		{
			listAttributes.Add(ar);
		}

		public void writeXML(string outputFilename)
		{
			XmlTextWriter xml = new XmlTextWriter(outputFilename, null);
			xml.WriteStartDocument();
			xml.Formatting = Formatting.Indented;
			xml.WriteStartElement("attributes"); // <attributes>
			foreach (AttributeRepresentation ar in listAttributes)
			{
				xml.WriteStartElement("attribute");          // <attribute

				xml.WriteStartAttribute(null, "type", null); //   type="
				xml.WriteString(ar.TypeName);                //       <thetype>
				xml.WriteEndAttribute();                     //        "

				xml.WriteStartAttribute(null, "value", null);//   value="
				xml.WriteString(ar.ConstructorValues);       //       <theconstructorvalues>
				xml.WriteEndAttribute();                     //        "

				xml.WriteStartAttribute(null, "target", null); //   target="
				xml.WriteString(ar.TargetType);              //       <thetargettype>
				xml.WriteEndAttribute();                     //        "

				xml.WriteStartAttribute(null, "location", null); //   location="
				xml.WriteString(ar.TargetLocation);          //       <theLocation>
				xml.WriteEndAttribute();                     //        "

				xml.WriteEndElement();                       // / >
			}
			xml.WriteEndElement();               // </attributes>
			xml.Close();
		}
	}

}

namespace DDW.CSharp.Gen
{
	public class CapturingLineWriter : LineWriter
	{
		private LineWriter normalWriter;
		private string contents;

		public CapturingLineWriter(LineWriter normalWriter)
		{
			this.normalWriter = normalWriter;
		}

		public override void Write(string s)
		{
			normalWriter.Write(s);
			contents += s;
		}

		public override void WriteLine(string s)
		{
			normalWriter.WriteLine(s);
			contents += s;
		}

		public override void Write(int s)
		{
			normalWriter.Write(s);
			contents += s;
		}

		public override void WriteLine(int s)
		{
			normalWriter.WriteLine(s);
			contents += s;
		}

		public string Text
		{
			get { return contents; }
		}
	}
}