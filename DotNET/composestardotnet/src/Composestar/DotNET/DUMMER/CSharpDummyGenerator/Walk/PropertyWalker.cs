using System;
using System.Reflection;
using System.Collections;
using DDW.CSharp.Dom;

namespace DDW.CSharp.Walk
{		
	public interface IGraphVisitor
	{
		void VisitCallback(	);
		int			NestingLevel	{ get; set; }
		IGraph		BaseElement		{ get; set; }
		bool		IsProperty		{ get; }
		MemberInfo  PropertyInfo	{ get; set; }
		object		PropertyValue	{ get; set; }
		bool		IsCollection	{ get; set; }
		bool		IgnoreEmpty		{ get; set; }
	}

	public class PropertyWalker
	{
		public static void Walk(IGraph element, IGraphVisitor gv)
		{

			Type type = element.GetType();
			bool isCol = element is ICollection;
			gv.PropertyInfo = null;
			gv.PropertyValue = null;
			gv.BaseElement = element;
			gv.IsCollection = isCol;
			if(IsNotEmpty(gv)) gv.VisitCallback();

			gv.NestingLevel++;

			MemberInfo[] props = type.FindMembers(MemberTypes.Property,
				BindingFlags.Public | BindingFlags.Instance,
				new MemberFilter(MemberFilter),
				null);
			for(int i = 0; i < props.Length; i++)
			{
				MemberInfo prop = props[i];
				object result = type.InvokeMember(
					prop.Name, 
					BindingFlags.Public  | 
					BindingFlags.GetProperty | 
					BindingFlags.Instance, 
					null, 
					element, 
					new object[]{} );

				bool pIsCol = result is ICollection;

				gv.PropertyInfo = prop;
				gv.PropertyValue = result;
				gv.BaseElement = element;
				gv.IsCollection = pIsCol;
				if(IsNotEmpty(gv)) gv.VisitCallback();
				if(pIsCol)
				{	
					ICollection col = (ICollection)result;
					int cnt = col.Count;
					if(cnt == 0)
					{
					}
					gv.NestingLevel++;
					foreach(object gr in col)
					{
						if(gr is IGraph)
						{
							PropertyWalker.Walk((IGraph)gr, gv);
						}
					}
					gv.NestingLevel--;
				}
				else if(result is IGraph)
				{
					gv.NestingLevel++;
					PropertyWalker.Walk((IGraph)result, gv);
					gv.NestingLevel--;
				}
			}
			gv.NestingLevel--;

//				if(result is Enum)
//				{
//					string enumName = Enum.GetName(result.GetType(), result);
//					if(enumName == "Empty")
//					{
//					}
//				}
		}
		private static bool IsNotEmpty(IGraphVisitor gv)
		{
			if(!gv.IgnoreEmpty) return true;
			bool hasValue = true;

			if(gv.IsProperty)
			{
				if(gv.PropertyValue == null)
				{
					hasValue = false;
				}
				else if( !(gv.PropertyValue is IGraph) )
				{
					if(gv.PropertyValue is Enum)
					{
						string enumName = 
							Enum.GetName(gv.PropertyValue.GetType(), gv.PropertyValue);
						if(enumName == "Empty" || enumName == null)
							hasValue = false;
					}
				}
			}
			else if(gv.BaseElement == null)
			{
				hasValue = false;
			}
			if(gv.IsCollection && ((ICollection)gv.PropertyValue).Count == 0) 
				hasValue = false;
			return hasValue;
		}

		public static bool MemberFilter(MemberInfo objMemberInfo, Object objSearch)
		{			
			if(objMemberInfo.Name == "LinePragma") return false;
			object[] oa = objMemberInfo.GetCustomAttributes(
				Type.GetType("DDW.CSharp.Dom.CodeElementAttribute"), true);
			if(oa.Length > 0)
			{
				return true;
			}
			return false;
		}
	}
}