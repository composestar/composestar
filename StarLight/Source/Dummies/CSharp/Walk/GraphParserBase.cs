using System;
using System.Reflection;
using System.Collections;

using DDW.CSharp.Dom;

namespace DDW.CSharp.Walk
{		
	public abstract class GraphParserBase
	{
		public virtual void Parse(IGraph ig)
		{
			if(ig == null) return;
			BeginParse(ig);
			switch(ig.GraphType)
			{ 
				case GraphTypes.CompileUnit :
				{
					ParseElement((CompileUnit)ig);
					break;
				}
				case GraphTypes.Import :
				{
					ParseElement((Import)ig);
					break;
				}
				case GraphTypes.NamespaceDecl :
				{
					ParseElement((NamespaceDecl)ig);
					break;
				}
				case GraphTypes.MethodDecl :
				{
					ParseElement((MethodDecl)ig);
					break;
				}
				case GraphTypes.FieldDecl :
				{
					ParseElement((FieldDecl)ig);
					break;
				}
				case GraphTypes.PropertyDecl :
				{
					ParseElement((PropertyDecl)ig);
					break;
				}
				case GraphTypes.EventDecl :
				{
					ParseElement((EventDecl)ig);
					break;
				}
				case GraphTypes.ConstantDecl :
				{
					ParseElement((ConstantDecl)ig);
					break;
				}
				case GraphTypes.IndexerDecl :
				{
					ParseElement((IndexerDecl)ig);
					break;
				}
				case GraphTypes.OperatorDecl :
				{
					ParseElement((OperatorDecl)ig);
					break;
				}
				case GraphTypes.ConstructorDecl :
				{
					ParseElement((ConstructorDecl)ig);
					break;
				}
				case GraphTypes.DestructorDecl :
				{
					ParseElement((DestructorDecl)ig);
					break;
				}
				case GraphTypes.AccessorDecl :
				{
					ParseElement((AccessorDecl)ig);
					break;
				}
				case GraphTypes.EnumMemberDecl :
				{
					ParseElement((EnumMemberDecl)ig);
					break;
				}
				case GraphTypes.ClassDecl :
				{
					ParseElement((ClassDecl)ig);
					break;
				}
				case GraphTypes.InterfaceDecl :
				{
					ParseElement((InterfaceDecl)ig);
					break;
				}
				case GraphTypes.StructDecl :
				{
					ParseElement((StructDecl)ig);
					break;
				}
				case GraphTypes.EnumDecl :
				{
					ParseElement((EnumDecl)ig);
					break;
				}
				case GraphTypes.DelegateDecl :
				{
					ParseElement((DelegateDecl)ig);
					break;
				}
				case GraphTypes.ExprStmt :
				{
					ParseElement((ExprStmt)ig);
					break;
				}
				case GraphTypes.CommentStmt :
				{
					ParseElement((CommentStmt)ig);
					break;
				}
				case GraphTypes.VariableDeclStmt :
				{
					ParseElement((VariableDeclStmt)ig);
					break;
				}
				case GraphTypes.ConstantDeclStmt :
				{
					ParseElement((ConstantDeclStmt)ig);
					break;
				}
				case GraphTypes.IfStmt :
				{
					ParseElement((IfStmt)ig);
					break;
				}
				case GraphTypes.SwitchStmt :
				{
					ParseElement((SwitchStmt)ig);
					break;
				}
				case GraphTypes.Case :
				{
					ParseElement((Case)ig);
					break;
				}
				case GraphTypes.IterationStmt :
				{
					ParseElement((IterationStmt)ig);
					break;
				}
				case GraphTypes.ForEachStmt :
				{
					ParseElement((ForEachStmt)ig);
					break;
				}
				case GraphTypes.GotoStmt :
				{
					ParseElement((GotoStmt)ig);
					break;
				}
				case GraphTypes.LabeledStmt :
				{
					ParseElement((LabeledStmt)ig);
					break;
				}
				case GraphTypes.ReturnStmt :
				{
					ParseElement((ReturnStmt)ig);
					break;
				}
				case GraphTypes.BreakStmt :
				{
					ParseElement((BreakStmt)ig);
					break;
				}
				case GraphTypes.ContinueStmt :
				{
					ParseElement((ContinueStmt)ig);
					break;
				}
				case GraphTypes.CheckedStmt :
				{
					ParseElement((CheckedStmt)ig);
					break;
				}
				case GraphTypes.UncheckedStmt :
				{
					ParseElement((UncheckedStmt)ig);
					break;
				}
				case GraphTypes.LockStmt :
				{
					ParseElement((LockStmt)ig);
					break;
				}
				case GraphTypes.UsingStmt :
				{
					ParseElement((UsingStmt)ig);
					break;
				}
				case GraphTypes.ThrowStmt :
				{
					ParseElement((ThrowStmt)ig);
					break;
				}
				case GraphTypes.TryCatchFinallyStmt :
				{
					ParseElement((TryCatchFinallyStmt)ig);
					break;
				}
				case GraphTypes.Catch :
				{
					ParseElement((Catch)ig);
					break;
				}
				case GraphTypes.AttachDelegateStmt :
				{
					ParseElement((AttachDelegateStmt)ig);
					break;
				}
				case GraphTypes.RemoveDelegateStmt :
				{
					ParseElement((RemoveDelegateStmt)ig);
					break;
				}
				case GraphTypes.AssignExpr :
				{
					ParseElement((AssignExpr)ig);
					break;
				}
				case GraphTypes.UnaryExpr :
				{
					ParseElement((UnaryExpr)ig);
					break;
				}
				case GraphTypes.BinaryExpr :
				{
					ParseElement((BinaryExpr)ig);
					break;
				}
				case GraphTypes.TernaryExpr :
				{
					ParseElement((TernaryExpr)ig);
					break;
				}
				case GraphTypes.CastExpr :
				{
					ParseElement((CastExpr)ig);
					break;
				}
				case GraphTypes.SubExpr :
				{
					ParseElement((SubExpr)ig);
					break;
				}
				case GraphTypes.UnknownReference :
				{
					ParseElement((UnknownReference)ig);
					break;
				}
				case GraphTypes.ThisRef :
				{
					ParseElement((ThisRef)ig);
					break;
				}
				case GraphTypes.BaseRef :
				{
					ParseElement((BaseRef)ig);
					break;
				}
				case GraphTypes.PropertySetValueRef :
				{
					ParseElement((PropertySetValueRef)ig);
					break;
				}
				case GraphTypes.ArgumentRef :
				{
					ParseElement((ArgumentRef)ig);
					break;
				}
				case GraphTypes.LocalRef :
				{
					ParseElement((LocalRef)ig);
					break;
				}
				case GraphTypes.TypeOfExpr :
				{
					ParseElement((TypeOfExpr)ig);
					break;
				}
				case GraphTypes.FieldRef :
				{
					ParseElement((FieldRef)ig);
					break;
				}
				case GraphTypes.ArrayElementRef :
				{
					ParseElement((ArrayElementRef)ig);
					break;
				}
				case GraphTypes.MethodRef :
				{
					ParseElement((MethodRef)ig);
					break;
				}
				case GraphTypes.PropertyRef :
				{
					ParseElement((PropertyRef)ig);
					break;
				}
				case GraphTypes.EventRef :
				{
					ParseElement((EventRef)ig);
					break;
				}
				case GraphTypes.MethodInvokeExpr :
				{
					ParseElement((MethodInvokeExpr)ig);
					break;
				}
				case GraphTypes.PostfixExpr :
				{
					ParseElement((PostfixExpr)ig);
					break;
				}
				case GraphTypes.DelegateInvokeExpr :
				{
					ParseElement((DelegateInvokeExpr)ig);
					break;
				}
				case GraphTypes.IndexerRef :
				{
					ParseElement((IndexerRef)ig);
					break;
				}
				case GraphTypes.MemberAccess :
				{
					ParseElement((MemberAccess)ig);
					break;
				}
				case GraphTypes.ArrayCreateExpr :
				{
					ParseElement((ArrayCreateExpr)ig);
					break;
				}
				case GraphTypes.ObjectCreateExpr :
				{
					ParseElement((ObjectCreateExpr)ig);
					break;
				}
				case GraphTypes.CreateDelegateExpr :
				{
					ParseElement((CreateDelegateExpr)ig);
					break;
				}
				case GraphTypes.BooleanLiteral :
				{
					ParseElement((BooleanLiteral)ig);
					break;
				}
				case GraphTypes.CharLiteral :
				{
					ParseElement((CharLiteral)ig);
					break;
				}
				case GraphTypes.IntegerLiteral :
				{
					ParseElement((IntegerLiteral)ig);
					break;
				}
				case GraphTypes.NullLiteral :
				{
					ParseElement((NullLiteral)ig);
					break;
				}
				case GraphTypes.RealLiteral :
				{
					ParseElement((RealLiteral)ig);
					break;
				}
				case GraphTypes.StringLiteral :
				{
					ParseElement((StringLiteral)ig);
					break;
				}
				case GraphTypes.ArrayInitializer :
				{
					ParseElement((ArrayInitializer)ig);
					break;
				}
				case GraphTypes.CustomAttribute :
				{
					ParseElement((CustomAttribute)ig);
					break;
				}
				case GraphTypes.TypeRef :
				{
					ParseElement((TypeRef)ig);
					break;
				}
				case GraphTypes.BuiltInType :
				{
					ParseElement((BuiltInType)ig);
					break;
				}
				case GraphTypes.ParamDecl :
				{
					ParseElement((ParamDecl)ig);
					break;
				}
				case GraphTypes.Param :
				{
					ParseElement((Param)ig);
					break;
				}
				case GraphTypes.VariableDecl :
				{
					ParseElement((VariableDecl)ig);
					break;
				}
				case GraphTypes.LinePragma :
				{
					ParseElement((LinePragma)ig);
					break;
				}
				case GraphTypes.Comment :
				{
					ParseElement((Comment)ig);
					break;
				}
				case GraphTypes.AssemblyReference :
				{
					ParseElement((AssemblyReference)ig);
					break;
				}
				case GraphTypes.RankSpecifier :
				{
					ParseElement((RankSpecifier)ig);
					break;
				}				
				case GraphTypes.Declarator :
				{
					ParseElement((Declarator)ig);
					break;
				}
				case GraphTypes.CompileUnitCollection :
				{
					ParseElement((CompileUnitCollection)ig);
					break;
				}
				case GraphTypes.ImportCollection :
				{
					ParseElement((ImportCollection)ig);
					break;
				}
				case GraphTypes.NamespaceDeclCollection :
				{
					ParseElement((NamespaceDeclCollection)ig);
					break;
				}
				case GraphTypes.TypeDeclCollection :
				{
					ParseElement((TypeDeclCollection)ig);
					break;
				}
				case GraphTypes.MemberDeclCollection :
				{
					ParseElement((MemberDeclCollection)ig);
					break;
				}
				case GraphTypes.TypeRefCollection :
				{
					ParseElement((TypeRefCollection)ig);
					break;
				}
				case GraphTypes.ParamCollection :
				{
					ParseElement((ParamCollection)ig);
					break;
				}
				case GraphTypes.ParamDeclCollection :
				{
					ParseElement((ParamDeclCollection)ig);
					break;
				}
				case GraphTypes.StatementCollection :
				{
					ParseElement((StatementCollection)ig);
					break;
				}
				case GraphTypes.CommentStmtCollection :
				{
					ParseElement((CommentStmtCollection)ig);
					break;
				}
				case GraphTypes.CatchCollection :
				{
					ParseElement((CatchCollection)ig);
					break;
				}
				case GraphTypes.ExpressionCollection :
				{
					ParseElement((ExpressionCollection)ig);
					break;
				}
				case GraphTypes.CustomAttributeCollection :
				{
					ParseElement((CustomAttributeCollection)ig);
					break;
				}
				case GraphTypes.CaseCollection :
				{
					ParseElement((CaseCollection)ig);
					break;
				}
				case GraphTypes.AssemblyReferenceCollection :
				{
					ParseElement((AssemblyReferenceCollection)ig);
					break;
				}				
				case GraphTypes.RankSpecifierCollection :
				{
					ParseElement((RankSpecifierCollection)ig);
					break;
				}				
				case GraphTypes.DeclaratorCollection :
				{
					ParseElement((DeclaratorCollection)ig);
					break;
				}
				default :
				{
					throw new Exception("Not valid GraphType:" + ig.GraphType);
				}
			}			
			EndParse(ig);
		}


		#region CompileUnit
		public abstract void ParseElement(CompileUnit gr);
		#endregion
		#region Import
		public abstract void ParseElement(Import gr);
		#endregion
		#region NamespaceDecl
		public abstract void ParseElement(NamespaceDecl gr);
		#endregion
		#region MethodDecl
		public abstract void ParseElement(MethodDecl gr);
		#endregion
		#region FieldDecl
		public abstract void ParseElement(FieldDecl gr);
		#endregion
		#region PropertyDecl
		public abstract void ParseElement(PropertyDecl gr);
		#endregion
		#region EventDecl
		public abstract void ParseElement(EventDecl gr);
		#endregion
		#region ConstantDecl
		public abstract void ParseElement(ConstantDecl gr);
		#endregion
		#region IndexerDecl
		public abstract void ParseElement(IndexerDecl gr);
		#endregion
		#region OperatorDecl
		public abstract void ParseElement(OperatorDecl gr);
		#endregion
		#region ConstructorDecl
		public abstract void ParseElement(ConstructorDecl gr);
		#endregion
		#region DestructorDecl
		public abstract void ParseElement(DestructorDecl gr);
		#endregion
		#region AccessorDecl
		public abstract void ParseElement(AccessorDecl gr);
		#endregion
		#region EnumMemberDecl
		public abstract void ParseElement(EnumMemberDecl gr);
		#endregion
		#region ClassDecl
		public abstract void ParseElement(ClassDecl gr);
		#endregion
		#region InterfaceDecl
		public abstract void ParseElement(InterfaceDecl gr);
		#endregion
		#region StructDecl
		public abstract void ParseElement(StructDecl gr);
		#endregion
		#region EnumDecl
		public abstract void ParseElement(EnumDecl gr);
		#endregion
		#region DelegateDecl
		public abstract void ParseElement(DelegateDecl gr);
		#endregion

		#region ExprStmt
		public abstract void ParseElement(ExprStmt gr);
		#endregion
		#region CommentStmt
		public abstract void ParseElement(CommentStmt gr);
		#endregion
		#region VariableDeclStmt
		public abstract void ParseElement(VariableDeclStmt gr);
		#endregion
		#region ConstantDeclStmt
		public abstract void ParseElement(ConstantDeclStmt gr);
		#endregion
		#region IfStmt
		public abstract void ParseElement(IfStmt gr);
		#endregion
		#region SwitchStmt
		public abstract void ParseElement(SwitchStmt gr);
		#endregion
		#region Case
		public abstract void ParseElement(Case gr);
		#endregion
		#region IterationStmt
		public abstract void ParseElement(IterationStmt gr);
		#endregion
		#region ForEachStmt
		public abstract void ParseElement(ForEachStmt gr);
		#endregion
		#region GotoStmt
		public abstract void ParseElement(GotoStmt gr);
		#endregion
		#region LabeledStmt
		public abstract void ParseElement(LabeledStmt gr);
		#endregion
		#region ReturnStmt
		public abstract void ParseElement(ReturnStmt gr);
		#endregion
		#region BreakStmt
		public abstract void ParseElement(BreakStmt gr);
		#endregion
		#region ContinueStmt
		public abstract void ParseElement(ContinueStmt gr);
		#endregion
		#region CheckedStmt
		public abstract void ParseElement(CheckedStmt gr);
		#endregion
		#region UncheckedStmt
		public abstract void ParseElement(UncheckedStmt gr);
		#endregion
		#region LockStmt
		public abstract void ParseElement(LockStmt gr);
		#endregion
		#region UsingStmt
		public abstract void ParseElement(UsingStmt gr);
		#endregion
		#region ThrowStmt
		public abstract void ParseElement(ThrowStmt gr);
		#endregion
		#region TryCatchFinallyStmt
		public abstract void ParseElement(TryCatchFinallyStmt gr);
		#endregion
		#region Catch
		public abstract void ParseElement(Catch gr);
		#endregion
		#region AttachDelegateStmt
		public abstract void ParseElement(AttachDelegateStmt gr);
		#endregion
		#region RemoveDelegateStmt
		public abstract void ParseElement(RemoveDelegateStmt gr);
		#endregion

		#region AssignExpr
		public abstract void ParseElement(AssignExpr gr);
		#endregion
		#region UnaryExpr
		public abstract void ParseElement(UnaryExpr gr);
		#endregion
		#region BinaryExpr
		public abstract void ParseElement(BinaryExpr gr);
		#endregion
		#region TernaryExpr
		public abstract void ParseElement(TernaryExpr gr);
		#endregion
		#region CastExpr
		public abstract void ParseElement(CastExpr gr);
		#endregion
		#region SubExpr
		public abstract void ParseElement(SubExpr gr);
		#endregion
		#region UnknownReference
		public abstract void ParseElement(UnknownReference gr);
		#endregion
		#region ThisRef
		public abstract void ParseElement(ThisRef gr);
		#endregion
		#region BaseRef
		public abstract void ParseElement(BaseRef gr);
		#endregion
		#region PropertySetValueRef
		public abstract void ParseElement(PropertySetValueRef gr);
		#endregion
		#region ArgumentRef
		public abstract void ParseElement(ArgumentRef gr);
		#endregion
		#region LocalRef
		public abstract void ParseElement(LocalRef gr);
		#endregion
		#region TypeOfExpr
		public abstract void ParseElement(TypeOfExpr gr);
		#endregion
		#region FieldRef
		public abstract void ParseElement(FieldRef gr);
		#endregion
		#region ArrayElementRef
		public abstract void ParseElement(ArrayElementRef gr);
		#endregion
		#region MethodRef
		public abstract void ParseElement(MethodRef gr);
		#endregion
		#region PropertyRef
		public abstract void ParseElement(PropertyRef gr);
		#endregion
		#region EventRef
		public abstract void ParseElement(EventRef gr);
		#endregion
		#region MethodInvokeExpr
		public abstract void ParseElement(MethodInvokeExpr gr);
		#endregion
		#region PostfixExpr
		public abstract void ParseElement(PostfixExpr gr);
		#endregion
		#region DelegateInvokeExpr
		public abstract void ParseElement(DelegateInvokeExpr gr);
		#endregion
		#region IndexerRef
		public abstract void ParseElement(IndexerRef gr);
		#endregion
		#region MemberAccess
		public abstract void ParseElement(MemberAccess gr);
		#endregion
		#region ArrayCreateExpr
		public abstract void ParseElement(ArrayCreateExpr gr);
		#endregion
		#region ObjectCreateExpr
		public abstract void ParseElement(ObjectCreateExpr gr);
		#endregion
		#region CreateDelegateExpr
		public abstract void ParseElement(CreateDelegateExpr gr);
		#endregion

		#region BooleanLiteral
		public abstract void ParseElement(BooleanLiteral gr);
		#endregion
		#region CharLiteral
		public abstract void ParseElement(CharLiteral gr);
		#endregion
		#region IntegerLiteral
		public abstract void ParseElement(IntegerLiteral gr);
		#endregion
		#region NullLiteral
		public abstract void ParseElement(NullLiteral gr);
		#endregion
		#region RealLiteral
		public abstract void ParseElement(RealLiteral gr);
		#endregion
		#region StringLiteral
		public abstract void ParseElement(StringLiteral gr);
		#endregion
		#region ArrayInitializer
		public abstract void ParseElement(ArrayInitializer gr);
		#endregion
		#region CustomAttribute
		public abstract void ParseElement(CustomAttribute gr);
		#endregion
		#region TypeRef
		public abstract void ParseElement(TypeRef gr);
		#endregion
		#region BuiltInType
		public abstract void ParseElement(BuiltInType gr);
		#endregion
		#region ParamDecl
		public abstract void ParseElement(ParamDecl gr);
		#endregion
		#region Param
		public abstract void ParseElement(Param gr);
		#endregion
		#region VariableDecl
		public abstract void ParseElement(VariableDecl gr);
		#endregion
		#region LinePragma
		public abstract void ParseElement(LinePragma gr);
		#endregion
		#region Comment
		public abstract void ParseElement(Comment gr);
		#endregion
		#region AssemblyReference
		public abstract void ParseElement(AssemblyReference gr);
		#endregion
		#region RankSpecifier
		public abstract void ParseElement(RankSpecifier gr);
		#endregion
		#region Declarator
		public abstract void ParseElement(Declarator gr);
		#endregion

		#region CompileUnitCollection
		public abstract void ParseElement(CompileUnitCollection gr);
		#endregion
		#region ImportCollection
		public abstract void ParseElement(ImportCollection gr);
		#endregion
		#region NamespaceDeclCollection
		public abstract void ParseElement(NamespaceDeclCollection gr);
		#endregion
		#region TypeDeclCollection
		public abstract void ParseElement(TypeDeclCollection gr);
		#endregion
		#region MemberDeclCollection
		public abstract void ParseElement(MemberDeclCollection gr);
		#endregion
		#region TypeRefCollection
		public abstract void ParseElement(TypeRefCollection gr);
		#endregion
		#region ParamCollection
		public abstract void ParseElement(ParamCollection gr);
		#endregion
		#region ParamDeclCollection
		public abstract void ParseElement(ParamDeclCollection gr);
		#endregion
		#region StatementCollection
		public abstract void ParseElement(StatementCollection gr);
		#endregion
		#region CommentStmtCollection
		public abstract void ParseElement(CommentStmtCollection gr);
		#endregion
		#region CatchCollection
		public abstract void ParseElement(CatchCollection gr);
		#endregion
		#region ExpressionCollection
		public abstract void ParseElement(ExpressionCollection gr);
		#endregion
		#region CustomAttributeCollection
		public abstract void ParseElement(CustomAttributeCollection gr);
		#endregion
		#region CaseCollection
		public abstract void ParseElement(CaseCollection gr);
		#endregion
		#region AssemblyReferenceCollection
		public abstract void ParseElement(AssemblyReferenceCollection gr);
		#endregion
		#region RankSpecifierCollection
		public abstract void ParseElement(RankSpecifierCollection gr);
		#endregion
		#region DeclaratorCollection
		public abstract void ParseElement(DeclaratorCollection gr);
		#endregion
		//enums
		#region Modifiers
		public abstract void ParseElement(Modifiers gr);
		#endregion
		#region TypeModifiers
		public abstract void ParseElement(TypeModifiers gr);
		#endregion
		#region AccessorModifiers
		public abstract void ParseElement(AccessorModifiers gr);
		#endregion
		#region AttributeTarget
		public abstract void ParseElement(AttributeTarget gr);
		#endregion
		#region OverloadableOperator
		public abstract void ParseElement(OverloadableOperator gr);
		#endregion
		#region BinaryOperator
		public abstract void ParseElement(BinaryOperator gr);
		#endregion
		#region UnaryOperator
		public abstract void ParseElement(UnaryOperator gr);
		#endregion
		#region PostfixOperator
		public abstract void ParseElement(PostfixOperator gr);
		#endregion
		#region AssignOperator
		public abstract void ParseElement(AssignOperator gr);
		#endregion
		#region IterationType
		public abstract void ParseElement(IterationType gr);
		#endregion
		#region ParamDirection
		public abstract void ParseElement(ParamDirection gr);
		#endregion
		protected virtual void BeginParse(IGraph gr)
		{
		}
		protected virtual void EndParse(IGraph gr)
		{
		}
	}
}
