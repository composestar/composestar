using System;
using System.Text;
using System.Reflection;
using System.Collections;

using DDW.CSharp.Dom;

namespace DDW.CSharp.Walk
{		
	public class PropertyWalkerBase : IGraphVisitor
	{
		public virtual void VisitCallback()
		{
		}

		#region  NestingLevel  
		private int p_nestingLevel = 0;
		public int				NestingLevel				
		{
			get
			{
				return p_nestingLevel;
			}
			set
			{
				p_nestingLevel = value;
			}
		}	
	#endregion	
		#region  BaseElement  
		private IGraph element;
		public IGraph				BaseElement					
		{
			get
			{
				return element;
			}
			set
			{
				element = value;
			}
		}	
	#endregion	

		#region  IsProperty  
		public bool					IsProperty				
		{
			get
			{
				return !(propertyInfo == null);
			}
		}	
	#endregion

		#region  PropertyInfo  
		private MemberInfo propertyInfo;
		public MemberInfo			PropertyInfo					
		{
			get
			{
				return propertyInfo;
			}
			set
			{
				propertyInfo = value;
			}
		}	
	#endregion	
		#region  PropertyValue  
		private object propVal;
		public object				PropertyValue					
		{
			get
			{
				return propVal;
			}
			set
			{
				propVal = value;
			}
		}	
	#endregion	
		#region  IsCollection  
		private bool p_isCollection = false;
		public bool				IsCollection				
		{
			get
			{
				return p_isCollection;
			}
			set
			{
				p_isCollection = value;
			}
		}	
	#endregion
		#region  IgnoreEmpty  
		private bool p_ignoreEmpty = true;
		public bool				IgnoreEmpty				
		{
			get
			{
				return p_ignoreEmpty;
			}
			set
			{
				p_ignoreEmpty = value;
			}
		}	
	#endregion

	}
}