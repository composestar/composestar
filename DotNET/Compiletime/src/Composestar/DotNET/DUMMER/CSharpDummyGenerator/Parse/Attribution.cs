using System;
using System.Collections;
using Dom = DDW.CSharp.Dom;
using DDW.CSharp.SymbolTable;

namespace DDW.CSharp.Parse
{
	public class Attribution
	{
		private Attribution(){}

		public static void AttributeExpressions(ExpressionRootCollection erc)
		{
			Attribution atrib = new Attribution();
			foreach(ExpressionRoot er in erc)
			{
				atrib.ResolveType(er.Expression);
			}
		}
		#region CoverType(IDefinition, IDefinition)
		/// <summary>
		/// Returns the lowest common type of the two arguments, based on C#'s type conversion tables.
		/// </summary>
		/// <param name="type1">First type to compare.</param>
		/// <param name="type2">Second type to compare.</param>
		/// <returns>The common type.</returns>
		public static bool CoverType(IDefinition type1, IDefinition type2)
		{
			// todo: reutrn the IDef that solves the two. For testing just return true (eg had match)
			return true;
		}
		#endregion 
		#region ConvertDistance(IDefinition, IDefinition)
		/// <summary>
		/// Returns true if the origin type can auto convert to the target type.
		/// </summary>
		/// <param name="type1">The type to try to convert.</param>
		/// <param name="type2">The type we are trying to convert to.</param>
		/// <returns>The common type.</returns>
		public static int ConvertDistance(IDefinition origin, IDefinition target)
		{
			// todo: deal with null
			// built in has no declaration, so .Type will be null (for now)
			if(origin is BuiltInDefinition && target is BuiltInDefinition)
			{
				BuiltInDefinition obit = (BuiltInDefinition)origin;
				BuiltInDefinition tbit = (BuiltInDefinition)target;
				int bit = obit.ConvertDistance(tbit.LiteralType);
				return bit;
			}
			else if(origin.Type == target.Type)
			{
				return 0; // match
			}
			else if(origin.Type is Dom.TypeDecl && target.Type is Dom.TypeDecl)
			{
				Dom.TypeDecl otd = (Dom.TypeDecl)origin.Type;
				Dom.TypeDecl ttd = (Dom.TypeDecl)target.Type;
				int dtt =  otd.DistanceToType(ttd.Definition, 0);
				return dtt;
				//otd.
			}
			return Int32.MaxValue;
		}
		#endregion 
		
		#region ResolveOverload
		/// <summary>
		/// Picks the correct overload for a constructor, method, indexer, or operator. Returns null in the case of no defined constructor (use default).
		/// </summary>
		/// <param name="constructors"></param>
		/// <param name="parameters"></param>
		/// <returns></returns>
		private IDefinition ResolveOverload
			(OverloadableDefinition constructors, Dom.ParamCollection parameters)
		{			
			// TODO: maybe return a pre-defined 'default constructor' ref
			if(constructors == null) return null;

			IDefinition matchDef = null;
			int ovCount = constructors.Definitions.Count;
			int paramCount = parameters.Count;
			ArrayList matches = new ArrayList();
			ArrayList argLists = new ArrayList();
			foreach(IDefinition cdef in constructors.Definitions)
			{
				if(cdef.SourceGraph is Dom.IOverloadable)
				{
					Dom.ParamDeclCollection pdc = 
						((Dom.IOverloadable)cdef.SourceGraph).Parameters;
					// TODO: account for  params
					if(paramCount == pdc.Count)
					{
						matches.Add(cdef);
						argLists.Add(pdc);
					}
				}
			}
			// no overloads
			int mCount = matches.Count;
			if(mCount == 0) 
			{
				; // throw error
			}
			else if(mCount == 1) 
			{
				// easy case (because we assume code is correct)
				// - only one match for arg numbers
				matchDef = (IDefinition)matches[0];
			}
			else
			{
				int[] bestMatches = new int[paramCount];
				int invalid = Int32.MaxValue;
				// set to all invalid
				for(int i = 0; i < paramCount; i++) bestMatches[i] = invalid;
				int[] curMatches = new int[paramCount];
				// resolve overload by sig
				// The remaining methods will have the same number of args
				// so more points for a non cast match
				for(int i = 0; i < mCount; i++)
				{
					Dom.ParamDeclCollection curParamDecls = 
						(Dom.ParamDeclCollection)argLists[i];
					// check for params special case
					bool isParams = false;
					if(paramCount > 0)
					{
						isParams = 
							curParamDecls[paramCount - 1].IsParams;
					}
					bool isBetterMatch = true;
					for(int j = 0; j < paramCount; j++)
					{
						Dom.ParamDecl pd = (Dom.ParamDecl)curParamDecls[j];
						Dom.Param p = (Dom.Param)parameters[j];
						if(pd.Direction != p.Direction)
						{
							isBetterMatch = false;
							break;
						}
						else
						{
							// must resolve param expressions...
							IDefinition argDef = ResolveType(p.Value);
							IDefinition declDef = pd.Definition.Type.Definition;
							curMatches[j] = ConvertDistance(argDef, declDef);
							if(	curMatches[j] == Int32.MaxValue || 
								curMatches[j] > bestMatches[j]) 
							{
								isBetterMatch = false;
								break;
							}
						}
					}
					if(isBetterMatch)
					{
						// ignore check for ambig etc now, as we assume valid code
						bestMatches = curMatches;
						matchDef = (IDefinition)matches[i];
					}
				}
			}
			return matchDef;
		}
		#endregion

		private IDefinition ResolveType(Dom.Expression expr)
		{
			IDefinition retval = null;
			if(expr is Dom.PrimitiveExpr)
			{
				return ((Dom.PrimitiveExpr)expr).ResultType;
			}
			switch(expr.GraphType)
			{
				case Dom.GraphTypes.UnknownReference :
				{
					Dom.UnknownReference ur = (Dom.UnknownReference)expr;
					IDefinition declDef = ur.Definition;

					IDefinition tpDef = declDef.Type.Definition;
					// built ins have no declaration, so use self
					if(tpDef == null)
						tpDef = ur.Definition;

					if(tpDef.Id == 0)
					{
						IDefinition defLookup = tpDef.Scope.EnclosingScope.Lookup(tpDef.Name); 
						ur.Definition.Type = defLookup.SourceGraph;
					}
					ur.DeclaredType = ur.Definition.Type;
					retval = declDef.Type.Definition;
					break;
				}
				case Dom.GraphTypes.AssignExpr :
				{
					Dom.AssignExpr ae = (Dom.AssignExpr)expr;
					// ae.Left -- ignore Left for now
					ae.ResultType = ResolveType(ae.Right);
					retval = ae.ResultType;
					break;
				}
				case Dom.GraphTypes.ObjectCreateExpr :
				{
					Dom.ObjectCreateExpr oce = (Dom.ObjectCreateExpr)expr;
					oce.ResultType = oce.CreateType.Definition;
					Dom.IDeclaration decl = oce.ResultType.SourceGraph;
					
					if(decl is Dom.ClassDecl)
					{
						Dom.ClassDecl cd = (Dom.ClassDecl)decl;
						OverloadableDefinition constructors = cd.Constructors;
						oce.ConstructorDefinition = 
							ResolveOverload(constructors, oce.Parameters);
					}					
					else if(decl is Dom.StructDecl)
					{
						Dom.StructDecl sd = (Dom.StructDecl)decl;
						OverloadableDefinition constructors = sd.Constructors;
						oce.ConstructorDefinition = 
							ResolveOverload(constructors, oce.Parameters);
					}
					retval =  oce.ResultType;
					break;
				}
				case Dom.GraphTypes.MethodInvokeExpr :
				{
					Dom.MethodInvokeExpr mie = (Dom.MethodInvokeExpr)expr;
					IDefinition targ = GetTargetDef(mie.Target);
					OverloadableDefinition overloads = (OverloadableDefinition)targ;
					if(overloads != null)
					{
						mie.Target.Definition = 
							ResolveOverload(overloads, mie.Parameters);
					}
					retval = ResolveType(mie.Target);
					break;
				}
				case Dom.GraphTypes.MethodRef :
				{
					Dom.MethodRef mr = (Dom.MethodRef)expr;
					IDefinition targ = null; 
					if(mr.Definition != null) 
					{
						retval = mr.Definition;
					}
					else if(mr.Target != null)
					{
						targ = ResolveType(mr.Target);
						// swallow unknown refs
						if(mr.Target is Dom.UnknownReference) mr.Target = null;
						if(mr.MethodName != "")
						{
							retval = targ.Scope.Lookup(mr.MethodName);
							mr.Definition = retval;
						}
						else
						{
							retval = targ;
							mr.Definition = retval;
						}
					}
					break;
				}
				case Dom.GraphTypes.MemberAccess :
				{
					Dom.MemberAccess ma = (Dom.MemberAccess)expr;
					IDefinition targ = null; 
					if(ma.Definition != null) 
					{
						retval = ma.Definition;
					}
					else if(ma.Target != null)
					{
						targ = ResolveType(ma.Target);
						//if(ma.Target is Dom.UnknownReference) ma.Target = null;
						if(ma.MemberName != "")
						{
							retval = targ.Scope.Lookup(ma.MemberName);
						}
						else
						{
							retval = targ;
						}
					}
					break;
				}
			}
			return retval;
		}
		private IDefinition GetTargetDef(Dom.Expression expr)
		{
			IDefinition retval = null;
			switch (expr.GraphType)
			{
				case Dom.GraphTypes.MethodRef :
				{
					Dom.MethodRef mrf = ((Dom.MethodRef)expr);
					if(mrf.Definition == null)
					{
						mrf.Definition = GetTargetDef(mrf.Target);
					}
					if(mrf.Target is Dom.UnknownReference) mrf.Target = null;
					retval = mrf.Definition;
					break;
				}
				case Dom.GraphTypes.UnknownReference :
				{
					Dom.UnknownReference ur =((Dom.UnknownReference)expr);
					retval = ur.Definition.Scope.EnclosingScope.Lookup(ur.Definition.Name);

					break;
				}
				case Dom.GraphTypes.MemberAccess :
				{
					Dom.MemberAccess ma =((Dom.MemberAccess)expr);
					IDefinition ttype = ResolveType(ma.Target);
					//if(ma.Target is Dom.UnknownReference) ma.Target = null;
					retval = ttype.SourceGraph.Definition.Scope.Lookup(ma.MemberName);
					break;
				}
			}
			return retval;
		}
	}
}
