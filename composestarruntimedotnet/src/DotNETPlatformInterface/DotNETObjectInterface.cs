using System;

namespace Composestar.RuntimeDotNET.Interface
{
	/// <summary>
	/// Summary description for DotNetObjectInterface.
	/// </summary>
	public class DotNETObjectInterface : Composestar.RuntimeCore.Utils.ObjectInterface
	{
		public DotNETObjectInterface()
		{
			instance = this;
		}

		public override object getFieldValue(object passedObject, String fieldName)
		{
			if((passedObject is String) && INTERNAL_STING_VALUE.Equals(fieldName))
			{
				return passedObject;
			}
			if(fieldName == null || "".Equals(fieldName))
			{
				return null;
			}
			return GetFieldValue(passedObject,fieldName);
		}

		public override String[] getFields(object passedObject)
		{
			System.Reflection.FieldInfo[] fields = GetFields(passedObject);
			int size = fields.Length;
			int i =0;

			String[] fieldNames = new String[size];
			if(passedObject is String) //Strings arn't real objects in some vm's like IBM and .NET
			{
				fieldNames = new String[size+1];
				fieldNames[i] = INTERNAL_STING_VALUE;
				i++;
			}
			for(int j = 0; j < fields.Length;j++)
			{
				fieldNames[i] = fields[j].Name;
				i++;
			}
			return fieldNames;
		}

		private static System.Reflection.FieldInfo[] GetFields(object PassedObject)
		{
			if (PassedObject == null)
				return new System.Reflection.FieldInfo[0];

			Type ObjectType = PassedObject.GetType();
			return ObjectType.GetFields(System.Reflection.BindingFlags.Instance
				| System.Reflection.BindingFlags.NonPublic
				| System.Reflection.BindingFlags.Public
				| System.Reflection.BindingFlags.IgnoreCase
				| System.Reflection.BindingFlags.FlattenHierarchy);
		} 

		public object GetFieldValue(object PassedObject, string
			FieldName)
		{

			object Field=null;

			if (PassedObject == null)
				throw new ArgumentNullException("PassedObject"
					,"PassedObject must be an instantiated object.");

			if (FieldName == null || FieldName.Trim() == "")
				throw new ArgumentOutOfRangeException("FieldName"
					,"Fieldname must be a non empty string.");

			Type ObjectType = PassedObject.GetType();
			System.Reflection.FieldInfo PrivateField =
				ObjectType.GetField(FieldName
				,System.Reflection.BindingFlags.Instance
				| System.Reflection.BindingFlags.NonPublic
				| System.Reflection.BindingFlags.Public
				| System.Reflection.BindingFlags.IgnoreCase);

			if (PrivateField == null)
				throw new ArgumentOutOfRangeException("FieldName"
					, ObjectType.FullName + " does not have a field : " + FieldName +
					".");

			Field = PrivateField.GetValue(PassedObject);
			return Field;
		} 
	}
}
