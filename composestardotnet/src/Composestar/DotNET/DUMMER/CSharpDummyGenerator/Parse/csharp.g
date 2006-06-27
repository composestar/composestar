// C# Antlr grammar
// Robin Debreuil - Debreuil Digital Works ©2004

// =======================================================
// == PARSER =============================================
// =======================================================

header 
{
	using CommonAST					= antlr.CommonAST; //DDW.CSharp.Parse.LineNumberAST;
	using System.Collections;
}

options 
{	
	language = "CSharp";
	namespace = "DDW.CSharp.Parse";
}

class CSharpParser extends Parser;

options 
{
	k = 2;                           	// two token lookahead
	defaultErrorHandler = true;     	// Don't generate parser error handlers
	buildAST = true;   
 	exportVocab=CSharp;
}
tokens 
{
		
// ===== Tokens =====

	CompileUnit				<AST=DDW.CSharp.Parse.CompileUnit>;
	UsingNode				<AST=DDW.CSharp.Parse.UsingNode>;

// ======== DECLARATIONS =========================
			
	NamespaceNode			<AST=DDW.CSharp.Parse.NamespaceNode>;
	ClassNode				<AST=DDW.CSharp.Parse.ClassNode>;				
	InterfaceNode			<AST=DDW.CSharp.Parse.InterfaceNode>;
	StructNode				<AST=DDW.CSharp.Parse.StructNode>;				
	EnumNode				<AST=DDW.CSharp.Parse.EnumNode>;
	DelegateNode			<AST=DDW.CSharp.Parse.DelegateNode>;			
	BaseTypes				<AST=DDW.CSharp.Parse.BaseTypes>;		
	
	BooleanLiteral			<AST=DDW.CSharp.Parse.BooleanLiteral>;
	IntegerLiteral			<AST=DDW.CSharp.Parse.IntegerLiteral>; 	
	RealLiteral 			<AST=DDW.CSharp.Parse.RealLiteral>;	
	CharLiteral 			<AST=DDW.CSharp.Parse.CharLiteral>;	
	StringLiteral 			<AST=DDW.CSharp.Parse.StringLiteral>;	
	NullLiteral 			<AST=DDW.CSharp.Parse.NullLiteral>;	

// ======== MEMBERS ==============================

	Types					<AST=DDW.CSharp.Parse.Types>;
	Members					<AST=DDW.CSharp.Parse.Members>;
//	TypeMemberNode			<AST=DDW.CSharp.Parse.TypeMemberNode>;
	MethodNode				<AST=DDW.CSharp.Parse.MethodNode>;					
	FieldNode				<AST=DDW.CSharp.Parse.FieldNode>;					  
	PropertyNode			<AST=DDW.CSharp.Parse.PropertyNode>;				  
	EventNode				<AST=DDW.CSharp.Parse.EventNode>;					  
	ConstantNode			<AST=DDW.CSharp.Parse.ConstantNode>;				  
	IndexerNode				<AST=DDW.CSharp.Parse.IndexerNode>;				  
	OperatorNode			<AST=DDW.CSharp.Parse.OperatorNode>;				  
	ConstructorNode			<AST=DDW.CSharp.Parse.ConstructorNode>;			  
	DestructorNode			<AST=DDW.CSharp.Parse.DestructorNode>;			  
	AccessorNode			<AST=DDW.CSharp.Parse.AccessorNode>;	
	EnumMemberNode			<AST=DDW.CSharp.Parse.EnumMemberNode>;			  
																		  
// ======== REFERENCES ===========================						  

	Ident 					<AST=DDW.CSharp.Parse.Ident>;						
	QualIdent 				<AST=DDW.CSharp.Parse.QualIdent>;				
	TypeRef 				<AST=DDW.CSharp.Parse.TypeRef>;				  
	BuiltInType 			<AST=DDW.CSharp.Parse.BuiltInType>;				  
	Args 					<AST=DDW.CSharp.Parse.Args>;					
	Arg 					<AST=DDW.CSharp.Parse.Arg>;					
	DeclArgs 				<AST=DDW.CSharp.Parse.DeclArgs>;					
	DeclArg 				<AST=DDW.CSharp.Parse.DeclArg>;					
	ArgDirection			<AST=DDW.CSharp.Parse.ArgDirection>;				
																	
// ======== STATEMENTS ===========================					
																	
	Statements 				<AST=DDW.CSharp.Parse.Statements>;				
//	Statement 				<AST=DDW.CSharp.Parse.Statement>;					
	ExprStmt 				<AST=DDW.CSharp.Parse.ExprStmt>;
	CommentNode 			<AST=DDW.CSharp.Parse.CommentNode>;
	TryCatchFinallyStmt		<AST=DDW.CSharp.Parse.TryCatchFinallyStmt>;
	 TryStmt				<AST=DDW.CSharp.Parse.TryStmt>;
	 CatchClause			<AST=DDW.CSharp.Parse.CatchClause>;
	 FinallyStmt			<AST=DDW.CSharp.Parse.FinallyStmt>;
	IfStmt					<AST=DDW.CSharp.Parse.IfStmt>;		
	SwitchStmt				<AST=DDW.CSharp.Parse.SwitchStmt>;			
	 SwitchSection			<AST=DDW.CSharp.Parse.SwitchSection>;
	IterationStmt			<AST=DDW.CSharp.Parse.IterationStmt>;
	 InitStmt 				<AST=DDW.CSharp.Parse.InitStmt>;
	 IncStmt 				<AST=DDW.CSharp.Parse.IncStmt>;
	ForEachStmt				<AST=DDW.CSharp.Parse.ForEachStmt>;
	 
	GotoStmt				<AST=DDW.CSharp.Parse.GotoStmt>;				
	ReturnStmt				<AST=DDW.CSharp.Parse.ReturnStmt>;
	BreakStmt 				<AST=DDW.CSharp.Parse.BreakStmt>;
	ContinueStmt 			<AST=DDW.CSharp.Parse.ContinueStmt>;
	ThrowStmt				<AST=DDW.CSharp.Parse.ThrowStmt>;	
	
	CheckedStmt 			<AST=DDW.CSharp.Parse.CheckedStmt>;
	UncheckedStmt 			<AST=DDW.CSharp.Parse.UncheckedStmt>;
	LockStmt 				<AST=DDW.CSharp.Parse.LockStmt>;
	UsingStmt 				<AST=DDW.CSharp.Parse.UsingStmt>;
	
	LabeledStmt				<AST=DDW.CSharp.Parse.LabeledStmt>;			
	VariableDeclStmt		<AST=DDW.CSharp.Parse.VariableDeclStmt>;				
	ConstantDeclStmt		<AST=DDW.CSharp.Parse.ConstantDeclStmt>;				

// ======== EXPRESSIONS ==========================

	Expressions				<AST=DDW.CSharp.Parse.Expressions>;		
	Expression				<AST=DDW.CSharp.Parse.Expression>;		
	PrimaryExpression		<AST=DDW.CSharp.Parse.PrimaryExpression>;		
	SubExpr					<AST=DDW.CSharp.Parse.SubExpr>;		
	
//	TypeRefExpr;			
	PrimitiveExpr			<AST=DDW.CSharp.Parse.PrimitiveExpr>;	
	CastExpr				<AST=DDW.CSharp.Parse.CastExpr>;
	ThisRefExpr				<AST=DDW.CSharp.Parse.ThisRefExpr>;			
	BaseRefExpr				<AST=DDW.CSharp.Parse.BaseRefExpr>;
	MemberAccessExpr		<AST=DDW.CSharp.Parse.MemberAccessExpr>;
	
	AssignExpr				<AST=DDW.CSharp.Parse.AssignExpr>;				
	UnaryExpr				<AST=DDW.CSharp.Parse.UnaryExpr>;
	BinaryExpr				<AST=DDW.CSharp.Parse.BinaryExpr>;
	TernaryExpr				<AST=DDW.CSharp.Parse.TernaryExpr>;					
	
	ArrayCreateExpr			<AST=DDW.CSharp.Parse.ArrayCreateExpr>;
	ObjectCreateExpr		<AST=DDW.CSharp.Parse.ObjectCreateExpr>;		
	TypeOfExpr				<AST=DDW.CSharp.Parse.TypeOfExpr>;	
	PostfixExpr				<AST=DDW.CSharp.Parse.PostfixExpr>;
	CheckedExpr				<AST=DDW.CSharp.Parse.CheckedExpr>;
	UncheckedExpr			<AST=DDW.CSharp.Parse.UncheckedExpr>;
	
	InvokeExpr				<AST=DDW.CSharp.Parse.InvokeExpr>;		
	IndexerExpr				<AST=DDW.CSharp.Parse.IndexerExpr>;
	ArrayRankExpr			<AST=DDW.CSharp.Parse.ArrayRankExpr>;		
	ArrayInitExpr			<AST=DDW.CSharp.Parse.ArrayInitExpr>;	

// ======== MISC =================================

	Op						<AST=DDW.CSharp.Parse.Op>;					
	Declarator				<AST=DDW.CSharp.Parse.Declarator>;					
	CustomAttributes		<AST=DDW.CSharp.Parse.CustomAttributes>;	
	CustomAttribute			<AST=DDW.CSharp.Parse.CustomAttribute>;	
	ModifierAttributes		<AST=DDW.CSharp.Parse.ModifierAttributes>;	
}

identifier!
	:	id:IDENTIFIER 
		{ #identifier = #( identifier, ([Ident, id_AST.getText()]) );}
	;
	
// ***** A.1.3 Comments *****
comment!
	:	slc:SINGLE_LINE_COMMENT
		{ #comment = #( [CommentNode, slc_AST.getText()]); }
	|	dc:DELIMITED_COMMENT
		{ #comment = #( [CommentNode, dc_AST.getText()]); }
	;
	

// ***** A.1.8 Literals *****

literal
	:	boolean_literal				{#literal = #([BooleanLiteral, 	literal_AST.getText()]);} 
	|	INTEGER_LITERAL				{#literal = #([IntegerLiteral, 	literal_AST.getText()]);}
	|	HEXADECIMAL_INTEGER_LITERAL	{#literal = #([IntegerLiteral, 	literal_AST.getText()]);}
	|	REAL_LITERAL				{#literal = #([RealLiteral, 	literal_AST.getText()]);}
	|	CHARACTER_LITERAL			{#literal = #([CharLiteral, 	literal_AST.getText()]);}
	|	STRING_LITERAL				{#literal = #([StringLiteral, 	literal_AST.getText()]);}
	|	null_literal				{#literal = #([NullLiteral, 	"null"]);}
	;
	
boolean_literal
	:	TRUE
	|	FALSE
	;
	
null_literal
	:	NULL
	;
	

// ***** A.2.1 Basic concepts *****
namespace_name
	:	namespace_or_type_name
	;
type_name
	:	namespace_or_type_name
	;
namespace_or_type_name
	:	simple_name (DOT! simple_name)*
	;
	

// ***** A.2.2 Types *****

// moved around reftype stuff due to a.b.c[] x = null; error
type
	:	value_type
	|	(array_type	)=>array_type
	|	type_name
	|!	obj:OBJECT	{#type = #([BuiltInType, obj_AST.getText()]);}
	|!	str:STRING	{#type = #([BuiltInType, str_AST.getText()]);}
	;

value_type!
	:	st:struct_type
	{	#value_type =	 
		#(value_type,[BuiltInType, st_AST.getText()] );
	}
//	|	enum_type	// enum can only be a type_name
	;

struct_type
	:	simple_type
	;

simple_type
	:	numeric_type
	|	BOOL
	;

numeric_type
	:	integral_type
	|	floating_point_type
	|	DECIMAL
	;

integral_type
	:	SBYTE 
	|	BYTE 
	|	SHORT 
	|	USHORT 
	|	INT 
	|	UINT 
	|	LONG 
	|	ULONG 
	|	CHAR
	;

floating_point_type
	:	FLOAT 
	|	DOUBLE
	;

	
reference_type
	:	(class_type)=>class_type
	|	(array_type)=>array_type
//	|	interface_type	// interface_type can only be a type_name
//	|	delegate_type	// delegate_type  can only be a type_name
	;

class_type
	:	type_name
	|!	obj:OBJECT	{#class_type = #([BuiltInType, obj_AST.getText()]);}
	|!	str:STRING 	{#class_type = #([BuiltInType, str_AST.getText()]);}
	;	
	
array_type
	:	non_array_type rank_specifiers
	;
	
interface_type
	:	type_name
	;
	
delegate_type
	:	type_name
	;
	
non_array_type
	:	class_type
	|	value_type
//	|	type_name
//	|	interface_type // determine interface and delegates later
//	|	delegate_type
	;
	
rank_specifiers
	:	(options{greedy=true;}:array_rank)+
	;
array_rank!
{int cnt=1;}
	:	LBRACK (COMMA {cnt++;} )* RBRACK
		{ #array_rank =	 
			#(array_rank,[ArrayRankExpr, cnt.ToString()] );
		}
	;
	
// ***** A.2.3 Variables *****

variable_reference
	:	expression
	;
	
	
// ***** A.2.4 Expressions *****

argument_list
	:	argument (COMMA! argument)*
		{#argument_list = #( #[Args], #argument_list);}
	;
		
argument!
	:	ex:expression
	{ #argument = 
		#(	([Arg]),
			([Expression], ex)
		); 
	}		
	|	dir:parameter_direction vr:variable_reference
	{ #argument = 
		#(	([Arg]),
			dir,
			([Expression], vr)
		); 
	}	
	;

simple_name
	:	identifier
//		{#simple_name = #([QualIdent], id);}
	;
parenthesized_expression!
	:	LPAREN ex:expression RPAREN // just ignore, already sets order
	{ #parenthesized_expression = 
		#(	([SubExpr]),
			ex
		); 
	}	
	;
primary_start
	:	literal	
	|	simple_name	
	|	parenthesized_expression
	|	this_access	
	|	base_access	
	|	(array_creation_expression)=>array_creation_expression
	|	object_creation_expression
	|	typeof_expression
	|	sizeof_expression
	|	checked_expression
	|	unchecked_expression
	|	predefined_type_access
	;
primary_expression
	:ps:primary_start 
		(	options {greedy=true;}:	
//			unchecked_expression
//		|	checked_expression			
//		|	sizeof_expression
//		|	typeof_expression
//		|	object_creation_expression	
			postfix_expression			
//		|	base_access	
//		|	this_access	
		|	element_access				
		|	(invocation_expression)=>invocation_expression
		|	member_access 
//		|	parenthesized_expression
//		|	simple_name			
//		|	literal			
		)*
	;		

member_access!
	:	DOT id:identifier 
		{#member_access = #([MemberAccessExpr,id_AST.getText()]);}
	;
predefined_type_access!
	:	apt:accessible_predefined_types
		DOT  id:identifier
		{#predefined_type_access = #(	#predefined_type_access,
										[BuiltInType, apt_AST.getText()], 
										[MemberAccessExpr,id_AST.getText()     ]);}
	;
accessible_predefined_types
	:(	BOOL	|	BYTE	|	CHAR	|	DECIMAL	|	DOUBLE	
	|	FLOAT	|	INT		|	LONG	|	OBJECT	|	SBYTE	
	|	SHORT	|	STRING	|	UINT	|	ULONG	|	USHORT)
	;
invocation_expression!
	:	LPAREN (args:argument_list)? RPAREN
		{#invocation_expression = #([InvokeExpr], args);}
	;	
element_access!
	:	LBRACK el:expression_list RBRACK
		{#element_access = #([IndexerExpr], el);}
	;
expression_list
	:	expression (COMMA! expression)*
	;
this_access!
	:	THIS
		{#this_access = #([ThisRefExpr]);}
	;
base_access!
	:	BASE DOT id:identifier
		{#base_access = #([BaseRefExpr],id);}	
	|	BASE ea:element_access										
		{#base_access = #([BaseRefExpr], ea);}								
	;																		
postfix_expression!															
	:	po:postfix_op													
		{#postfix_expression = #([PostfixExpr, po_AST.getText()]);}
	;	
postfix_op
	:	INC | DEC	
	;
object_creation_expression!
	:	NEW tp:type LPAREN (al:argument_list)? RPAREN
		{ #object_creation_expression = 
			#(	 [ObjectCreateExpr],
				([TypeRef],tp),
				al				
			);
		}	
	;
typeof_expression!
	:	TYPEOF LPAREN
		tt:typeof_types
		RPAREN
		{#typeof_expression = #([TypeOfExpr], ([TypeRef],tt));}
	;	
typeof_types
	:	(	type 
		|! 	vd:VOID {#typeof_types = #([BuiltInType, vd_AST.getText()]);}
		)
	;
checked_expression!
	:	CHECKED LPAREN! ex:expression RPAREN!
		{#checked_expression = #([CheckedExpr], ex);}
	;

unchecked_expression!
	:	UNCHECKED LPAREN! ex:expression RPAREN!
		{#unchecked_expression = #([UncheckedExpr], ex);}
	;
array_creation_expression!
	:	(NEW array_type array_initializer)=>
		 NEW 
		 tp:array_type 
		 ini:array_initializer
		 
		{ #array_creation_expression = 
			#(	 [ArrayCreateExpr],
				([TypeRef],tp),
				ini				
			);
		}	
	|	NEW 
		tp2:non_array_type 
		LBRACK! 
		el2:expression_list 
		RBRACK! 
		(options{greedy=true;}:rnk2:rank_specifiers )? 
		(ini2:array_initializer)?
		
		{ #array_creation_expression = 
			#(	 [ArrayCreateExpr],
				([TypeRef],tp2),
				([IndexerExpr],el2),
				rnk2,
				ini2				
			);
		}	
		
	;
// until types are discovered, delegate and object creation are the same
//delegate_creation_expression
//	:	NEW delegate_type LPAREN expression RPAREN 
//	;


// ============================

unary_expression
	:!	(cast_expression unary_expression) => 
		ce:cast_expression cue:unary_expression 
		{#unary_expression = #([CastExpr], ([TypeRef],ce), cue);}
		
	|	pe:primary_expression 
	
	|!	op:uanry_op ue:unary_expression	
		{#unary_expression = #(unary_expression, [UnaryExpr, op_AST.getText()], ue);}
	;
uanry_op
	: PLUS | MINUS | LNOT | BNOT | STAR | INC | DEC
	;
	
pre_increment_expression!
	:	op:INC ue:unary_expression
	{#pre_increment_expression = 
		#(pre_increment_expression, [UnaryExpr, op_AST.getText()], ue);}
	;

pre_decrement_expression!
	:	op:DEC ue:unary_expression
	{#pre_decrement_expression = 
		#(pre_decrement_expression, [UnaryExpr, op_AST.getText()], ue);}
	;

cast_expression
	:	LPAREN! type RPAREN! 
//	{#cast_expression = #(cast_expression, #([CastExpr], tp));}
	;

// ============================


multiplicative_expression
	:	unary_expression 
	(! op:multiplicative_op ex:unary_expression
		{#multiplicative_expression = 
			#([BinaryExpr, op_AST.getText()],  
		 ([Expression, "left"], multiplicative_expression), 
		 ([Expression, "right"], ex) )
		 ;} 
	)*
	;
multiplicative_op
	:	STAR | DIV | MOD
	;

additive_expression
	:	multiplicative_expression
	(!	op:additive_op ex:multiplicative_expression
		{#additive_expression = 
			#([BinaryExpr, op_AST.getText()],  
		 ([Expression, "left"], additive_expression), 
		 ([Expression, "right"], ex) )
		 ;} 
	)*
	;
additive_op
	:	PLUS | MINUS
	;
shift_expression
	:	additive_expression
	(!	op:shift_op ex:additive_expression
		{#shift_expression = 
			#([BinaryExpr, op_AST.getText()], 
		 ([Expression, "left"], shift_expression), 
		 ([Expression, "right"], ex) )
		 ;} 
	)*
	;
shift_op
	:	SL | SR
	;

relational_expression
	:	shift_expression
	(!	op1:relational_op ex:shift_expression
		{#relational_expression = 
			#([BinaryExpr, op1_AST.getText()],
		 ([Expression, "left"], relational_expression), 
		 ([Expression, "right"], ex) )
		 ;} 
			
	|!	op2:type_comp_op tp:type
		{#relational_expression = 
			#([BinaryExpr, op2_AST.getText()],
		 ([Expression, "left"], relational_expression), 
		 ([TypeRef, "right"], tp) )
		 ;} 
	)*
	;
relational_op
	:	LTHAN | GTHAN | LE | GE
	;
type_comp_op
	:	IS | AS
	;

equality_expression
	:	relational_expression
	(!	op:equality_op  ex:relational_expression
		{#equality_expression = 
			#([BinaryExpr, op_AST.getText()],
		 ([Expression, "left"], equality_expression), 
		 ([Expression, "right"], ex) )
		 ;} 
	)*
	;
equality_op
	:	EQUAL | NOT_EQUAL
	;

and_expression
	:	equality_expression
	(!	op:BAND ex:equality_expression
	{#and_expression = 
		#([BinaryExpr, op_AST.getText()],
		 ([Expression, "left"], and_expression), 
		 ([Expression, "right"], ex) )
		 ;} 
	)*
	;

exclusive_or_expression
	:	and_expression
	(!	op:BXOR ex:and_expression
	{#exclusive_or_expression = 
		#([BinaryExpr, op_AST.getText()],
		 ([Expression, "left"], exclusive_or_expression), 
		 ([Expression, "right"], ex) )
		 ;} 
	)*
	;

inclusive_or_expression
	:	exclusive_or_expression
	(!	op:BOR ex:exclusive_or_expression
	{#inclusive_or_expression = 
		#([BinaryExpr, op_AST.getText()],
		 ([Expression, "left"], inclusive_or_expression), 
		 ([Expression, "right"], ex) )
		 ;} 
	)*
	;
	
conditional_and_expression
	:	inclusive_or_expression
	(!	op:LAND ex:inclusive_or_expression
	{#conditional_and_expression = 
		#([BinaryExpr, op_AST.getText()], 
		 ([Expression, "left"], conditional_and_expression), 
		 ([Expression, "right"], ex) )
		 ;} 
	)*
	;

conditional_or_expression
	:	conditional_and_expression
	(!	op:LOR ex:conditional_and_expression
		{#conditional_or_expression = 
		#([BinaryExpr, op_AST.getText()], 
		 ([Expression, "left"], conditional_or_expression), 
		 ([Expression, "right"], ex) )
		 ;}
	)*
	;

conditional_expression
	:	conditional_or_expression
	(!	op:QUESTION ex1:expression COLON! ex2:expression
	{#conditional_expression = 
		#(	[TernaryExpr, op_AST.getText()], 
			([Expression, "test"],conditional_expression), 
			([Expression, "true"],ex1), 
			([Expression, "false"],ex2)
		)
	;}
	)?
	;
// ============================

assignment!
	:	ue:unary_expression ao:assignment_operator ex:expression
	{ #assignment = 
		#(	[AssignExpr, ao_AST.getText()],
			([Expression, "left"], ue),
			([Expression, "right"], ex)				
		);
	}	
	;

assignment_operator
	:	ASSIGN	|	PLUS_ASN	|	MINUS_ASN	|	STAR_ASN	|	DIV_ASN
	|	MOD_ASN	|	BAND_ASN	|	BOR_ASN		|	BXOR_ASN	|	SL_ASN	|	SR_ASN
	;

expression
	:	(conditional_expression) => conditional_expression
	|	assignment
	;

constant_expression
	:	expression
	;

boolean_expression
	:	expression
	;



// ***** A.2.5 Statements *****

statement
	:	labeled_statement
	|	(declaration_statement) => declaration_statement
	|	embedded_statement
	;

embedded_statement
	:	block
	|	empty_statement
	|	expression_statement
	|	selection_statement
	|	iteration_statement
	|	jump_statement
	|	try_statement
	|	checked_statement
	|	unchecked_statement
	|	lock_statement
	|	using_statement
	;

block
	:	LBRACE! statement_list RBRACE!
	;
	
statement_list
	:	(statement)*
	;

empty_statement
	:	SEMI!
	; 

labeled_statement!
	:	id:identifier COLON st:statement
	
		{ #labeled_statement = 
			#(	([LabeledStmt]),
			 	id,
				st
			);
		}	
	;
	
declaration_statement
	:	local_variable_declaration SEMI!
	|	local_constant_declaration SEMI! 
	;

local_variable_declaration!
	:	tp:type lvd:local_variable_declarators
	
		{ #local_variable_declaration = 
			#(	([VariableDeclStmt]),
			 	([TypeRef], tp),
				lvd
			);
		}	
	;

local_variable_declarators
	:	local_variable_declarator (COMMA! local_variable_declarator)*
	;

local_variable_declarator!
	:	id:identifier (ASSIGN lvi:local_variable_initializer)?
	
		{ #local_variable_declarator = 
			#(	[Declarator],	
				id,
			 	([Expression], lvi)				
			);
		}	
	;

local_variable_initializer
	:	expression
	|	array_initializer
	;

local_constant_declaration!
	:	CONST tp:type cd:constant_declarators
	
		{ #local_constant_declaration = 
			#(	([ConstantDeclStmt]),
			 	([TypeRef], tp),
				cd
			);
		}	
	;
	
constant_declarators
	:	constant_declarator (COMMA! constant_declarator)*
	;

constant_declarator!
	:	id:identifier ASSIGN ce:constant_expression
	
		{ #constant_declarator = 
			#(	[Declarator],
			 	id, 
			 	([Expression], ce)				
			);
		}	
	;
	
expression_statement!
	:	se:statement_expression SEMI!

		{ #expression_statement = 
			#(	[ExprStmt], se
			);
		}	
	;
	
statement_expression
	:	(assignment)=> assignment	// TODO: this pe can only be	
	|	primary_expression			// a) invocation_expression 
	|	pre_increment_expression	// b) object_creation_expression
	|	pre_decrement_expression	// c) postfix_expression
	;																
	
selection_statement
	:	if_statement
	|	switch_statement
	;
	
if_statement!
	:	IF LPAREN cond:boolean_expression RPAREN tr:embedded_statement
		(	options {warnWhenFollowAmbig = false;} // dangling else
		:	ELSE fl:embedded_statement
		)?
	
		{ #if_statement = 
			#(	([IfStmt]),
			 	([Expression], cond),
				([Statements, "true"], tr),				
				([Statements, "false"], fl)
			);
		}	
	;

switch_statement!
	:	SWITCH LPAREN cond:expression RPAREN sb:switch_block
	
		{ #switch_statement = 
			#(	([SwitchStmt]),
			 	([Expression], cond),
			 	sb
			);
		}	
	;
	
switch_block
	:	LBRACE! ( (switch_section)+ )? RBRACE!
	;
	
switch_section!
	:	sc:switch_cases
		st: statement_list
		
		{ #switch_section = 
			#(	([SwitchSection]),
			 	sc,
				([Statements], st)
			);
		}	
	;
	
switch_cases
	:(	options {warnWhenFollowAmbig=false;} 
		:	switch_label COLON!
	 )+ 
	;
	
switch_label!
	:	CASE ce:constant_expression
		{ #switch_label = 
			#(	([Expression, "case"]), ce
			);
		}	
	|	DEFAULT	
		{ #switch_label = 
			#(	switch_label,
				([Expression, "default"])
			);
		}	
	;
	
iteration_statement
	:	while_statement
	|	do_statement
	|	for_statement
	|	foreach_statement
	;

while_statement!
	:	WHILE LPAREN tst:boolean_expression RPAREN st:embedded_statement
		{ #while_statement = 
			#(	([IterationStmt]),
			 	([Expression, "while"], tst),
			 	([Statements], st)
			);
		}	
	;

do_statement!
	:	DO st:embedded_statement WHILE LPAREN tst:boolean_expression RPAREN SEMI 
		{ #do_statement = 
			#(	([IterationStmt]),
			 	([Expression, "do"], tst),
			 	([Statements], st)
			);
		}	
	;
for_statement!
	:	FOR 					LPAREN 
		(ini: for_initializer)? SEMI 
		(tst: for_condition)? 	SEMI 
		(inc: for_iterator)? 	RPAREN 
		 stm: embedded_statement
		
		{ #for_statement = 
			#(	([IterationStmt]),
			 	([InitStmt], ini),
			 	([Expression, "for"], tst),
			 	([IncStmt],  inc),
			 	([Statements], stm)
			);
		}	
	;

for_initializer
	:	(local_variable_declaration) => local_variable_declaration
	|	statement_expression_list
	;

for_condition
	:	boolean_expression
	;

for_iterator
	:	statement_expression_list
	;

statement_expression_list
	:	annotated_statement_expression (COMMA! annotated_statement_expression)*
	;
annotated_statement_expression!
	:	se:statement_expression
	{	#annotated_statement_expression = #( ([ExprStmt]),([Expression],se) );	}
	;

foreach_statement!
	:	 FOREACH 			LPAREN 
		 tp: type 
		 id: identifier 	IN 
		 ex: expression 	RPAREN 
		 st: embedded_statement
		
		{ #foreach_statement = 
			#(	([ForEachStmt]),
			 	([TypeRef],    tp),
			 	 id,
			 	([Expression, "foreach"], ex),
			 	([Statements], st)
			);
		}	
	;
jump_statement
	:	break_statement
	|	continue_statement
	|	goto_statement
	|	return_statement
	|	throw_statement
	;

break_statement!	
	:	BREAK SEMI		
	{ #break_statement = #(	 [BreakStmt]				);}	
	;
continue_statement!
	:	CONTINUE SEMI
	{ #continue_statement = #([ContinueStmt]			);}	
	;
goto_statement!
	:	GOTO id:identifier SEMI
	{ #goto_statement = #(	([GotoStmt]), 
							 id 			);}
	
	|	GOTO mod:CASE ce:constant_expression SEMI
	{ #goto_statement = #(	([GotoStmt]), 
							([ModifierAttributes], mod),
							([Expression], ce) 			);}
	|	GOTO mod2:DEFAULT SEMI
	{ #goto_statement = #(	([GotoStmt]), 
							([ModifierAttributes], mod2));}
	;
return_statement!
	:	RETURN (ex:expression)? SEMI 
	{ #return_statement = #(([ReturnStmt]), 
							([Expression], ex) 			);}
	; 	
throw_statement!
	:	THROW (ex:expression)? SEMI 
	{ #throw_statement = #( ([ThrowStmt]), 
							([Expression], ex) 			);}
	;	
	
checked_statement!
	:	CHECKED st:block
	{ #checked_statement = #([CheckedStmt],
							([Statements], st)			);}	
	;
unchecked_statement!
	:	UNCHECKED st:block
	{ #unchecked_statement = #([UncheckedStmt],
							  ([Statements], st)		);}	
	;
lock_statement!
	:	LOCK LPAREN ex:expression RPAREN st:embedded_statement
	{ #lock_statement = #(	([LockStmt]), 
							([Expression], ex),
							([Statements], st)			);}
	;
using_statement!
	:	USING LPAREN ra:resource_acquisition RPAREN st:embedded_statement
	{ #using_statement = #(	([UsingStmt]), 
							([Expression], ra),
							([Statements], st)			);}
	;
resource_acquisition
	:	(local_variable_declaration) => local_variable_declaration
	|	expression
	;
try_statement!
	:	TRY ts:block cc:catch_clauses (fc:finally_clause)?
	{ #try_statement = #(	([TryCatchFinallyStmt]), 
							([TryStmt], ([Statements], ts)),
							cc, fc						);}
	;
catch_clauses
	:	(catch_clause)*
	;
catch_clause!
	:	CATCH (LPAREN tp:class_type (id:identifier)? RPAREN)? st:block
	{ #catch_clause = #(	([CatchClause]),
							([TypeRef], tp),
							 id,
							([Statements], st)			);}
	;
finally_clause!
	:	FINALLY st:block
	{ #finally_clause = #(	([FinallyStmt]),
							([Statements], st)			);}
	;



// ***** A.2.5b Namespaces *****	

compilation_unit //{getASTFactory().setASTNodeType("DDW.CSharp.Parse.LineNumberAST");}
	:	(using_directive)*  (global_attributes)*  (namespace_member_declaration)* 
		
		{ #compilation_unit = 
			#([CompileUnit], compilation_unit);
		}
	;

namespace_declaration!
	:	NAMESPACE   
		qi:qualified_identifier
		LBRACE!
		ud:using_directives
		bd:namespace_body
		RBRACE!
		(SEMI!)?
		
		{ #namespace_declaration = 
			#([NamespaceNode],
			 ([QualIdent], qi),
			 ud,
			 ([Types], bd) ); 
		}
	;
	
using_directives
    : (using_directive)*
    ;

qualified_identifier
	:	identifier (DOT! identifier)*
	;
namespace_body
	:	(namespace_member_declaration)*
	;
	
using_directive!
	:	USING   
		(	id:identifier   ASSIGN   nm:namespace_or_type_name   SEMI
			{ #using_directive = 
				#([UsingNode],
				 ([Ident, "alias"], id),
				 ([QualIdent, "import"], nm) );
			}
		|	nm2:namespace_name	SEMI
			{ #using_directive = 
				#([UsingNode],
				 ([QualIdent, "import"], nm2) );
			}
		)
		
	;
namespace_member_declaration
	:	namespace_declaration
	|	type_declaration
	;

type_declaration
	:	(	(class_declaration) 		=> class_declaration
		|	(struct_declaration) 		=> struct_declaration 
		|	(interface_declaration) 	=> interface_declaration
		|	(enum_declaration)			=> enum_declaration
		|	(delegate_declaration SEMI!)=> delegate_declaration SEMI!
		) 
		(SEMI!)?
	;
	
	
	
// ***** A.2.6 Classes *****

class_declaration!
	:	(atr:attributes)?	
		mod: class_modifiers 
		CLASS 
		 id: identifier 
		(bs: class_base)? 
		 bd: class_body
		{ #class_declaration = 
			#(	([ClassNode]),
				  id, atr,
				([ModifierAttributes], mod),
				([BaseTypes], bs),
				([Members], bd)
			); 
		}
	;
	
	
class_modifiers
	:	(NEW 	|	PUBLIC	 |	PROTECTED  |	INTERNAL 
	|	PRIVATE |	ABSTRACT |	SEALED)*
	;

class_base
	:	COLON! 
		(	class_type (COMMA class_type)*
//		|	interface_type_list
//		|	class_type COMMA! interface_type_list
		)
	;
//interface_type_list
//	:	interface_type (COMMA interface_type)*
//	;
class_body
	:	LBRACE! (class_member_declaration)* RBRACE!
	;

class_member_declaration
	:	(constant_declaration) 		=> constant_declaration
	|	(field_declaration) 		=> field_declaration 
	|	(method_declaration) 		=> method_declaration 
	|	(property_declaration) 		=> property_declaration
	|	(event_declaration) 		=> event_declaration
	|	(indexer_declaration) 		=> indexer_declaration
	|	(operator_declaration) 		=> operator_declaration
	|	(constructor_declaration) 	=> constructor_declaration 
	|	(destructor_declaration) 	=> destructor_declaration
	|	(static_constructor_declaration) => static_constructor_declaration
	|	type_declaration
	;
	
	
constant_declaration!
	:	(atr:attributes)? 
		mod: constant_modifiers 
		CONST 
		tp: type 
		cd: constant_declarators // consts can declare more than one field
		SEMI
	
		{ #constant_declaration = 
			#(	([ConstantNode]),
				atr,
				([ModifierAttributes], mod),
				([TypeRef], tp),
				cd	
			); 
		}		//([Members], cd)
	;
constant_modifiers
	:	(NEW
	|	PUBLIC
	|	PROTECTED 
	|	INTERNAL
	|	PRIVATE)*
	;
field_declaration!
	:	(atr:attributes)? 
		mod: field_modifiers 
		tp: type 
		vd: variable_declarators
		SEMI
		
		{ #field_declaration = 
			#(	([FieldNode]),
				 atr,
				([ModifierAttributes], mod),
				([TypeRef], tp),
				vd
			); 
		}		//([Members], vd)	
	;
field_modifiers
	 : (NEW | PUBLIC | PROTECTED | INTERNAL | PRIVATE | STATIC | READONLY | VOLATILE)*
	;
variable_declarators
	:	variable_declarator (COMMA! variable_declarator)* 
	;
variable_declarator!
	:	id:identifier
		{ #variable_declarator = 
			#(	[Declarator],
				id,
				([Expression])				
			);
		}	
	|	id2:identifier ASSIGN vi:variable_initializer
		{ #variable_declarator = 
			#(	[Declarator],
				id2,
				([Expression], vi)				
			);
		}	
	;
variable_initializer
	:	ex:expression 	
	|	ai:array_initializer 	//{#variable_initializer = #([ArrayInitExpr], ai);}
	;
	
method_declaration!
	:	mh: method_header 
		mb: method_body
		
		{ #method_declaration = 
			#(	([MethodNode]),
				mh,
				([Statements], mb)
			); 
		}
	;
method_header!
	:	(atr:attributes)? 
		mod: method_modifiers 
		ret: return_type 
		nm:  member_name 
		LPAREN! (pms: formal_parameter_list)? RPAREN!
		
		{ #method_header = 
			#(	method_header,
				atr,
				([ModifierAttributes], mod),
				([TypeRef], ret),
				([QualIdent], nm),
				pms
			); 
			
		}
	;
method_modifiers
	:	(NEW	|	PUBLIC	|	PROTECTED	|	INTERNAL
	|	PRIVATE |	STATIC	|	VIRTUAL		|	SEALED	
	|	OVERRIDE|	ABSTRACT|	EXTERN)*
	;
return_type
	:	type
	|!	vd:VOID  	{#return_type = #([BuiltInType, vd_AST.getText()]);}
	;
member_name
	:	type_name	// identifier
					// interface_type DOT identifier 
	;
method_body
	:	block
	|	SEMI!
	;
	
// TODO: insure later that PARAMS is last one and single dimension
formal_parameter_list
	:	method_parameter
		(COMMA! method_parameter)*
		{#formal_parameter_list = #( #[DeclArgs], #formal_parameter_list);}
	;
method_parameter!
		// fixed_parameter
	:	((attributes)? (parameter_direction)? type identifier) =>
		 (atr: attributes)? 
		 (dir:parameter_direction)? 
		  tp:  type 
		  id:  identifier
		 { #method_parameter = 
			#(	([DeclArg]),
				atr,
				dir,
				([TypeRef], tp),
				 id
			); 
		}	
		//  parameter_array
	|	((attributes)? PARAMS array_type identifier) =>
		 (patr: attributes)? 
		  pmod: PARAMS 
		  ptp:  array_type 
		  pid:  identifier
		 { #method_parameter = 
			#(	([DeclArg]),
				patr,
				([ModifierAttributes], pmod),
				([TypeRef], ptp),
				 pid
			);
		}
	;
parameter_direction!
	:	REF {#parameter_direction = #([ArgDirection, "ref"]);}
	| 	OUT	{#parameter_direction = #([ArgDirection, "out"]);}
	;
	
property_declaration!
	:	(atr:attributes)? 
		 mod:property_modifiers 
		 tp: type 
		 nm: type_name // member_name 
		LBRACE 
		 ad: accessor_declarations 
		RBRACE
		{ #property_declaration = 
			#(	([PropertyNode]),
				atr,
				([ModifierAttributes], mod),
				([TypeRef], tp),
				([QualIdent], nm),
				ad
			);
		}
	;
property_modifiers
 : 	(	NEW		|	PUBLIC	|	PROTECTED	|	INTERNAL	|	PRIVATE	|	STATIC	
	|	VIRTUAL	|	SEALED	|	OVERRIDE	|	ABSTRACT	|	EXTERN
 	)*
	;
// TODO: can't be two gets etc.
accessor_declarations
	:	 accessor_declaration (accessor_declaration)?
	;
accessor_declaration!
	:	(atr:attributes)? mod:accessor_modifier st:accessor_body
		{ #accessor_declaration = 
			#(	([AccessorNode, mod_AST.getText()]),
				atr,
				([Statements], st)
			);
		}
	;
accessor_modifier
	:	{ LT(1).getText()=="get" || LT(1).getText()=="set"}? 
		identifier		
	;
accessor_body
	:	block
	|	SEMI!
	;
	
event_declaration!
	:	(atr: attributes)? 
		 mod: event_modifiers 
		EVENT 
		 tp:  type 
		(	(vd: variable_declarators)+ SEMI
			{ #event_declaration = 
				#(	([EventNode]),
					atr,
					([ModifierAttributes], mod),
					([TypeRef], tp),
					vd	
				);
			}				
		|	nm:member_name LBRACE ead:event_accessor_declarations RBRACE
			{ #event_declaration = 
				#(	([EventNode]),
					atr,
					([ModifierAttributes], mod),
					([TypeRef], tp),
					([QualIdent], nm),
					ead
				);
			}		
		)
	;
event_modifiers
	:	(NEW 	 |	PUBLIC 	 |	PROTECTED |	INTERNAL 
	|	PRIVATE	 |	STATIC 	 |	VIRTUAL   |	SEALED 
	|	OVERRIDE |	ABSTRACT |	EXTERN)*
	;
// TODO: insure not two PLUS_ASN's etc
event_accessor_declarations
	:	 event_accessor_declaration	(event_accessor_declaration)?
	;
event_accessor_declaration!
	:	(atr:attributes)? mod:event_accessor_modifiers st:event_accessor_block
		{ #event_accessor_declaration = 
			#(	([AccessorNode, mod_AST.getText()]),
				atr,
				([Statements], st)
			);
		}	
	;
event_accessor_modifiers
	:	{ LT(1).getText()=="add" || LT(1).getText()=="remove"}? 
		identifier		
	;
event_accessor_block
	:	block
	|	SEMI!
	;
indexer_declaration!
	:	(atr: attributes)? 
		 mod: indexer_modifiers 
		 idl: indexer_declarator 
		LBRACE ad:accessor_declarations RBRACE 
		
		{ #indexer_declaration = 
			#(	([IndexerNode]),
				atr,
				([ModifierAttributes], mod),
				idl,
				ad
			);
		}	
	;
indexer_modifiers
	:	(NEW 	 |	PUBLIC  |	PROTECTED |	INTERNAL  
	|	PRIVATE  |	VIRTUAL |	SEALED    |	OVERRIDE  
	|	ABSTRACT |	EXTERN)*
	;
indexer_declarator!
	:	 tp: type 
		(id:simple_name DOT)?
		THIS 			 
		LBRACK (pms:formal_parameter_list)? RBRACK
				
		{ #indexer_declarator = 
			#(	indexer_declarator,
				([TypeRef], tp),
				 id,
				pms
			);
		}
	;
operator_declaration!
	:	(atr:attributes)? mod:operator_modifiers odl:operator_declarator st:operator_body 
	
		{ #operator_declaration = 
			#(	([OperatorNode]),
				atr,
				([ModifierAttributes], mod),
				odl,
				([Statements], st)
			);
		}	
	;
operator_modifiers
	:	(PUBLIC 	|	STATIC 	|	EXTERN )*
	;
operator_declarator
	:	(unary_operator_declarator) 	 => unary_operator_declarator
	|	(binary_operator_declarator) 	 => binary_operator_declarator
	|	(conversion_operator_declarator) => conversion_operator_declarator
	;
unary_operator_declarator!
	:	tp1:type 
		OPERATOR 
		uop:overloadable_unary_operator 
		LPAREN 
		tp2:type 
		id:identifier 
		RPAREN 
					
		{ #unary_operator_declarator = 
			#(	unary_operator_declarator,
				([TypeRef], tp1),
				([Op, "unary"], uop),
				([DeclArgs], 
					([DeclArg],
					([TypeRef], tp2),
					 id 
					)
				)
			);
		}
	;	
overloadable_unary_operator
	:	PLUS	|	MINUS	|	LNOT	|	BNOT	|	STAR 
	|	INC		|	DEC		|	TRUE	|	FALSE
	;	
binary_operator_declarator!
	:	tp: type 
		OPERATOR 
		obo: overloadable_binary_operator 
		LPAREN 
		tp1: type 
		id1: identifier 
		COMMA 
		tp2: type 
		id2: identifier 
		RPAREN
							
		{ #binary_operator_declarator = 
			#(	binary_operator_declarator,
				([TypeRef], tp),
				([Op, "binary"], obo),
				([DeclArgs], 
					([DeclArg],
					([TypeRef], tp1),
					 id1 
					),
					([DeclArg],
					([TypeRef], tp2),
					 id2 
					)
				)
			);
		}
	;
	
overloadable_binary_operator
	:	PLUS	|	MINUS	|	STAR	|	DIV	|	MOD 
	|	BAND	|	BOR		|	BXOR	|	SL	|	SR
	|	EQUAL	|	NOT_EQUAL			
	|	GTHAN	|	LTHAN
	|	GE		|	LE
	;
conversion_operator_declarator!
	:	cls: conversion_classification 
		OPERATOR 
		tp:  type 
		LPAREN 
		atp: type 		
		id:	 identifier 
		RPAREN
					
		{ #conversion_operator_declarator = 
			#(	conversion_operator_declarator,
				([TypeRef], tp),
				([Op, "conversion"], cls),
				([DeclArgs], 
					([DeclArg],
					([TypeRef], atp),
					 id 
					)
				)
			);
		}
	;
conversion_classification
	:	IMPLICIT | EXPLICIT
	;
operator_body
	:	block
	|	SEMI!
	; 
	
constructor_declaration!
	:	(atr: attributes)? 
		 mod: constructor_modifiers 
		 cdl: constructor_declarator 
		 st:  constructor_body
	
		{ #constructor_declaration = 
			#(	([ConstructorNode]),
				atr,
				([ModifierAttributes], mod),
				cdl,
				([Statements], st)
			);
		}	
	;
		
constructor_modifiers
	:	(PUBLIC  |	PROTECTED |	INTERNAL |	PRIVATE  |	EXTERN)*
	;
constructor_declarator!
	:	 id: identifier 
		LPAREN 
		(pms: formal_parameter_list)? 
		RPAREN 
		(ini: constructor_initializer)?
					
		{ #constructor_declarator = 
			#(	constructor_declarator,
				 id,
				pms,
				ini				
			);
		}
	;
constructor_initializer!
	:	COLON cim:constructor_init_modifiers LPAREN (al:argument_list)? RPAREN 
	
		{ #constructor_initializer = 
			#(	cim,
				 al				
			);
		}
	;
constructor_init_modifiers!
	:	BASE 	{ #constructor_init_modifiers = #([BaseRefExpr]);}
	|	THIS	{ #constructor_init_modifiers = #([ThisRefExpr]);}
	;
constructor_body
	:	block
	|	SEMI!
	; 
// no init, no args
static_constructor_declaration!
	:	(atr: attributes)? 
		 mod: static_constructor_modifiers 
		 id:  identifier LPAREN RPAREN 
		 st:  static_constructor_body
	
		{ #static_constructor_declaration = 
			#(	([ConstructorNode]),
				atr,
				([ModifierAttributes], mod),
				 id,
				([Statements], st)
			);
		}	
	;
// TODO: insure not two externs etc...
static_constructor_modifiers
	:	(EXTERN | STATIC)
		(STATIC | EXTERN)?
	;

static_constructor_body
	:	block
	|	SEMI!
	;

destructor_declaration!
	:	(atr: attributes)? 
		(mod: EXTERN)? 
		BNOT 
		 id:  identifier LPAREN RPAREN  
		 st:  destructor_body
	
		{ #destructor_declaration = 
			#(	([DestructorNode]),
				atr,
				([ModifierAttributes], mod),
				 id,
				([Statements], st)
			);
		}	
	;
destructor_body
	:	block
	|	SEMI!
	;


// ***** A.2.7 Structs *****

struct_declaration!
	:	(atr:attributes)?	
		mod: struct_modifiers 
		STRUCT 
		 id: identifier 
		(bs: struct_interfaces)? 
		 bd: struct_body		
		 
		{ #struct_declaration = 
			#(	([StructNode]),
				 atr,
				 id,
				([ModifierAttributes], mod),
				([BaseTypes], bs),
				([Members], bd)
			); 
		}
	;
struct_modifiers
	:	(NEW | PUBLIC | PROTECTED | INTERNAL | PRIVATE)*
	;

struct_interfaces
	:	COLON! class_type (COMMA! class_type)*
	;

struct_body
	:	LBRACE! (struct_member_declaration)* RBRACE!
	;
	
struct_member_declaration	
	:	(constant_declaration) 		=> constant_declaration
	|	(field_declaration) 		=> field_declaration
	|	(method_declaration) 		=> method_declaration 
	|	(property_declaration) 		=> property_declaration
	|	(event_declaration) 		=> event_declaration
	|	(indexer_declaration) 		=> indexer_declaration
	|	(operator_declaration) 		=> operator_declaration
	|	(constructor_declaration) 	=> constructor_declaration 
	|	(static_constructor_declaration) => static_constructor_declaration
	|	 type_declaration
	;


// ***** A.2.8 Arrays *****

// array_type
// non_array_type
// rank_specifiers

array_initializer
	:	LBRACE! 
		(variable_initializer (COMMA! variable_initializer )*)?
		RBRACE!
		{ #array_initializer = 
			#( ([ArrayInitExpr]),array_initializer);
		}
	;


// ***** A.2.9 Interfaces *****

interface_declaration!
	:	(atr:attributes)?	
		mod: interface_modifiers 
		INTERFACE 
		 id: identifier 
		(bs: interface_base)? 
		 bd: interface_body
		 
		{ #interface_declaration = 
			#(	([InterfaceNode]),
				 atr, id,
				([ModifierAttributes], mod),
				([BaseTypes], bs),
				([Members], bd)
			); 
		}
	;
	
interface_modifiers
	:	(NEW | PUBLIC | PROTECTED | INTERNAL | PRIVATE)*
	;
interface_base
	:	COLON! class_type (COMMA! class_type)* //interface_type_list
	;
	//TODO!! Fill interface members into types
interface_body
	:	LBRACE! (interface_member_declaration)* RBRACE!
	;
interface_member_declaration
	:	(interface_method_declaration)	=> interface_method_declaration
	|	(interface_property_declaration)=> interface_property_declaration
	|	(interface_event_declaration)	=> interface_event_declaration
	|	(interface_indexer_declaration)	=> interface_indexer_declaration
	;

interface_method_declaration!
	:	(atr: attributes)? 
		(mod: NEW)? 
		ret:  return_type 
		id:   identifier 
		LPAREN (pms:formal_parameter_list)? RPAREN SEMI
		
		{ #interface_method_declaration = 
			#(	([MethodNode]),
				atr,
				([ModifierAttributes], mod),
				([TypeRef], ret),
				id,
				pms
			); 				
		}
	;
interface_property_declaration!
	:	(atr:attributes)? 
		(mod:NEW)? 
		 tp: type 
		 id: identifier 
		LBRACE ia: interface_accessors RBRACE
		{ #interface_property_declaration = 
			#(	([PropertyNode]),
				atr,
				([ModifierAttributes], mod),
				([TypeRef], tp),
				id,
				ia
			);
		}
	;
interface_accessors
	:	 interface_accessor (interface_accessor)?
	;
	
interface_accessor!
	:	(atr:attributes)? mod:accessor_modifier SEMI
		{ #interface_accessor = 
			#(	([AccessorNode, mod_AST.getText()]),
				atr
			);
		}
	;
	
interface_event_declaration! 
	:	(atr:attributes)? (mod:NEW)? EVENT tp:type id:identifier SEMI
		{ #interface_event_declaration = 
			#(	([EventNode]),
				atr,
				([ModifierAttributes], mod),
				([TypeRef], tp),
				id
			);
		}
	;
interface_indexer_declaration! 
	:	(atr:attributes)? (mod:NEW)? tp:type THIS 
		LBRACK (pms:formal_parameter_list)? RBRACK 
		LBRACE ia:interface_accessors RBRACE 
		{ #interface_indexer_declaration = 
			#(	([IndexerNode]),
				atr,
				([ModifierAttributes], mod),
				([TypeRef], tp),
				pms,
				ia
			);
		}
	;
	
// ***** A.2.10 Enums *****

enum_declaration!
	:	(atr:attributes)?	
		mod:enum_modifiers 
		ENUM 
		 id:identifier 
		(bs:enum_base)? 
		 bd:enum_body	
		 
		{ #enum_declaration = 
			#(	([EnumNode]),
				 atr,id,
				([ModifierAttributes], mod),
				([BaseTypes], bs),
				([Members], bd)
			); 
		}
	;
	
enum_base
	:	COLON! integral_type
	;

enum_body
	:	(LBRACE (enum_member_declaration  (COMMA enum_member_declaration)* )? COMMA RBRACE) =>
		 LBRACE! (enum_member_declaration (COMMA! enum_member_declaration)* )? COMMA! RBRACE!
	|	 LBRACE! (enum_member_declaration (COMMA! enum_member_declaration)* )? RBRACE!
	;
	
enum_modifiers
	:	(NEW | PUBLIC | PROTECTED | INTERNAL | PRIVATE)*
	;

enum_member_declaration!
	:	(atr:attributes)? id:identifier (ASSIGN! ex:constant_expression)?  
		 
		{ #enum_member_declaration = 
			#(	([EnumMemberNode]),
				 atr,id,
				([Expression, "constant"], ex)
			); 
		}
	;


// ***** A.2.11 Delegates *****

delegate_declaration!
	:	(atr:attributes)?	
		mod: delegate_modifiers
		DELEGATE 
		tp: return_type  // note: this must include VOID (vs C# spec, 'type')
		id: identifier 
		LPAREN! (pms: formal_parameter_list)?	RPAREN!
		
		{ #delegate_declaration = 
			#(	([DelegateNode]),
				 atr, id,
				([ModifierAttributes], mod),
				([TypeRef], tp),
				pms
			); 
		}
	;
		 
delegate_modifiers
	:	(NEW | PUBLIC | PROTECTED | INTERNAL | PRIVATE)*
	;



// ***** A.2.12 Attributes *****

global_attributes!
	:	LBRACK asm:ASSEMBLY COLON al:attribute_list RBRACK
		{ #global_attributes = #(  [CustomAttributes],
								  ([ModifierAttributes], asm),
								    al); }
	;

attributes
	:	(attribute_section)+
	;
	
attribute_section!
	:	LBRACK (at:attribute_target COLON)? atr:attribute_list RBRACK  
	{	#attribute_section = #( ([CustomAttributes]), 
								([ModifierAttributes], at),								
								  atr  );}
	;

attribute_target
	:	FIELD
	|	EVENT
	|	METHOD
	|	MODULE
	|	PARAM
	|	PROPERTY
	|	RETURN 
	|	TYPE 
	;

attribute_list
	:	(attribute (COMMA attribute)* COMMA) => 
		 attribute (COMMA! attribute)* COMMA!
	|	 attribute (COMMA! attribute)*
	;

attribute!
	:	tn:type_name (aa:attribute_arguments)?
		{ #attribute = #( 	([CustomAttribute]), 
							([TypeRef], tn), 
							aa 
						); 
		}
	;

// args must be positional, then named - not mixed
// (x=5) can be positional, but not x=5 
// TODO: must enforce (2,4,x=5,y=6) order 
attribute_arguments
	:	LPAREN! (attribute_argument_list)? RPAREN!
	;
	
 // an expression can be an expression_list
attribute_argument_list
	:	attribute_argument (COMMA! attribute_argument)* 
		{#attribute_argument_list = #( #[Args], #attribute_argument_list);}
	;
attribute_argument!	
	:	ex:expression
	{ #attribute_argument = 
		#(	([Arg]),
			([Expression], ex)
		); 
	}		
	;
	
/* // unused for now
positional_argument_list
	:	expression (COMMA! expression)* 
		{#positional_argument_list = #( #[Args], #positional_argument_list);}
	;

// unused for now
named_argument_list
	:	identifier ASSIGN expression (COMMA! identifier ASSIGN expression)*
	;
*/	


// ***** A.3 Grammar extensions for unsafe code *****

sizeof_expression
	:	SIZEOF LPAREN type RPAREN // unmanaged_type
	;		
// unsafe part skipped for now...	


	
	
	
	
	
	
	
	
	
	
	
	
	
// =======================================================
// == LEXER ==============================================
// =======================================================
	
class CSharpLexer extends Lexer;

options 
{
	k=4;                       // four characters of lookahead
	charVocabulary='\u0003'..'\u7FFF'; 	// to avoid hanging eof on comments (eof = -1)
	exportVocab=CSharp;
	testLiterals=false;
}

	
// ***** A.1.7 Keywords *****
tokens
{
	ABSTRACT	=	"abstract";			LONG		=	"long";
	AS			=	"as";				NAMESPACE	=	"namespace";
	BASE		=	"base";				NEW			=	"new";
	BOOL		=	"bool";				NULL		=	"null";
	BREAK		=	"break";			OBJECT		=	"object";
	BYTE		=	"byte";				OPERATOR	=	"operator";
	CASE		=	"case";				OUT			=	"out";
	CATCH		=	"catch";			OVERRIDE	=	"override";
	CHAR		=	"char";				PARAMS		=	"params";
	CHECKED		=	"checked";			PRIVATE		=	"private";
	CLASS		=	"class";			PROTECTED	=	"protected";
	CONST		=	"const";			PUBLIC		=	"public";
	CONTINUE	=	"continue";			READONLY	=	"readonly";
	DECIMAL		=	"decimal";			REF			=	"ref";
	DEFAULT		=	"default";			RETURN		=	"return";
	DELEGATE	=	"delegate";			SBYTE		=	"sbyte";
	DO			=	"do";				SEALED		=	"sealed";
	DOUBLE		=	"double";			SHORT		=	"short";
	ELSE		=	"else";				SIZEOF		=	"sizeof";
	ENUM		=	"enum";				STACKALLOC	=	"stackalloc";
	EVENT		=	"event";			STATIC		=	"static";
	EXPLICIT	=	"explicit";			STRING		=	"string";
	EXTERN		=	"extern";			STRUCT		=	"struct";
	FALSE		=	"false";			SWITCH		=	"switch";
	FINALLY		=	"finally";			THIS		=	"this";
	FIXED		=	"fixed";			THROW		=	"throw";
	FLOAT		=	"float";			TRUE		=	"true";
	FOR			=	"for";				TRY			=	"try";
	FOREACH		=	"foreach";			TYPEOF		=	"typeof";
	GOTO		=	"goto";				UINT		=	"uint";
	IF			=	"if";				ULONG		=	"ulong";
	IMPLICIT	=	"implicit";			UNCHECKED	=	"unchecked";
	IN			=	"in";				UNSAFE		=	"unsafe";
	INT			=	"int";				USHORT		=	"ushort";
	INTERFACE	=	"interface";		USING		=	"using";
	INTERNAL	=	"internal";			VIRTUAL		=	"virtual";
	IS			=	"is";				VOID		=	"void";
	LOCK		=	"lock";				WHILE		=	"while";
//	GET			=	"get";				SET			=	"set";
//	ADD			=	"add";				REMOVE		=	"remove";

// Attribute Targets TODO: I don't think these should be reserved words...? The are with this.
	FIELD		=	"field";
	METHOD		=	"method";
	MODULE		=	"module";
	PARAM		=	"param";
	PROPERTY	=	"property";
	TYPE 		=	"type";
	ASSEMBLY	=	"assembly";
//	EVENT		=	"event";	// defined above
//	RETURN 		=	"return";	// defined above

}


// ***** Lexical Grammar *****
/*
Input
	:	(Input_section)*
	;
Input_section
	:	(Input_element)*   New_line
	;
Input_element
	:	Whitespace
	|	Comment
	|	Token
	;
*/
// ***** A.1.1 LINE TERMINATORS *****
protected
NEW_LINE
	:	(	// carriage return character followed by possible line feed character	
			{ LA(2)=='\u000A' }? '\u000D' '\u000A'			
		|	'\u000D'			// line feed character							
		|	'\u000A'			// line feed character							
		|	'\u2028'			// line separator character
		|	'\u2029'			// paragraph separator character
		)
		{newline();}
	;
	
protected
NEW_LINE_CHARACTER
	:	('\u000D' | '\u000A' | '\u2028' | '\u2029')
	;
	
protected
NOT_NEW_LINE
	:	~( '\u000D' | '\u000A' | '\u2028' | '\u2029')
	;
	
	
// ***** A.1.2 WHITESPACE *****
WHITESPACE
	:	(	' '
		|	'\u0009' // horizontal tab character
		|	'\u000B' // vertical tab character
		|	'\u000C' // form feed character 
		|	NEW_LINE 
		)+
		{ _ttype = Token.SKIP; }
	;	
	
	
// ***** A.1.3 COMMENTS *****
SINGLE_LINE_COMMENT
	:	"//" 
		(NOT_NEW_LINE)* 
		(NEW_LINE)? // may be eof
		{_ttype = Token.SKIP;}
	;
	
DELIMITED_COMMENT
	:	"/*"  
		(	{ LA(2)!='/' }? '*'
		|	NEW_LINE		
		|	~('*'|'\u000D'|'\u000A'|'\u2028'|'\u2029')
		)*
		"*/"
		{ _ttype = Token.SKIP; }
	;	

/*	
// ***** A.1.4 TOKENS *****
TOKEN
	:	identifier
	|	KEYWORD
	|	INTEGER_LITERAL
	|	REAL_LITERAL
	|	CHARACTER_LITERAL
	|	STRING_LITERAL
	|	OPERATOR_OR_PUNCTUATOR
	;
*/	
	
// ***** A.1.5 UNICODE character escape sequences *****
UNICODE_ESCAPE_SEQUENCE
	:	('\\' 'u'   HEX_DIGIT   HEX_DIGIT   HEX_DIGIT  HEX_DIGIT)
	|	('\\' 'U'   HEX_DIGIT   HEX_DIGIT   HEX_DIGIT  HEX_DIGIT  
					HEX_DIGIT   HEX_DIGIT   HEX_DIGIT  HEX_DIGIT)
	;

// ***** A.1.6 IDENTIFIERS *****

IDENTIFIER
options { testLiterals=true; }
	:	IDENTIFIER_START_CHARACTER (IDENTIFIER_PART_CHARACTER)*
//	|	'@' (IDENTIFIER_PART_CHARACTER)*
	;
	
protected
IDENTIFIER_START_CHARACTER
	:	('a'..'z'|'A'..'Z'|'_'|'$') //|'@') //TODO: identifier literals can have starting @
	;
	
protected
IDENTIFIER_PART_CHARACTER
	:	('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'$') 
	;
	
// ***** A.1.8 LITERALS *****

/* // move to parser - TODO: look into this...

BOOLEAN_LITERAL
	:	TRUE
	|	FALSE
	;
NULL_LITERAL
	:	NULL
	;
*/

NUMERIC_LITERAL

	// real
	:	('.' DECIMAL_DIGIT) =>
		 '.' (DECIMAL_DIGIT)+ (EXPONENT_PART)? (REAL_TYPE_SUFFIX)?
		{$setType(REAL_LITERAL);}
			
	|	((DECIMAL_DIGIT)+ '.' DECIMAL_DIGIT) =>
		 (DECIMAL_DIGIT)+ '.' (DECIMAL_DIGIT)+ (EXPONENT_PART)? (REAL_TYPE_SUFFIX)?
		{$setType(REAL_LITERAL);}
		
	|	((DECIMAL_DIGIT)+ (EXPONENT_PART)) =>
		 (DECIMAL_DIGIT)+ (EXPONENT_PART) (REAL_TYPE_SUFFIX)?
		{$setType(REAL_LITERAL);}
		
	|	((DECIMAL_DIGIT)+ (REAL_TYPE_SUFFIX)) =>
		 (DECIMAL_DIGIT)+ (REAL_TYPE_SUFFIX)		
		{$setType(REAL_LITERAL);}
		 
	// integer
	|	 (DECIMAL_DIGIT)+ (INTEGER_TYPE_SUFFIX)?	
		{$setType(INTEGER_LITERAL);}
	
	// just a dot
	| '.'{$setType(DOT);}
	;

	
HEXADECIMAL_INTEGER_LITERAL
	:	"0x"   (HEX_DIGIT)+   (INTEGER_TYPE_SUFFIX)?
	|	"0X"   (HEX_DIGIT)+   (INTEGER_TYPE_SUFFIX)?
	;

CHARACTER_LITERAL
	:	"'"!   CHARACTER   "'"!
	;	

STRING_LITERAL
	:	REGULAR_STRING_LITERAL
	|	VERBATIM_STRING_LITERAL
	;

	
// ===== literal (protected) helpers ============

// nums
protected
DECIMAL_DIGIT
	: 	'0'	|	'1'	|	'2'	|	'3'	|	'4'	|	'5'	|	'6'	|	'7'	|	'8'	|	'9'
	;
protected	
INTEGER_TYPE_SUFFIX
	:
	(	options {generateAmbigWarnings=false;}
		:	"UL"	| "LU" 	| "ul"	| "lu"
		|	"UL"	| "LU" 	| "uL"	| "lU"
		|	"U"		| "L"	| "u"	| "l"
	)
	;
		
protected
HEX_DIGIT
	:	'0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' | 
		'A' | 'B' | 'C' | 'D' | 'E' | 'F'  |
		'a' | 'b' | 'c' | 'd' | 'e' | 'f'
	;	
	
protected	
EXPONENT_PART
	:	"e"  (SIGN)*  (DECIMAL_DIGIT)+
	|	"E"  (SIGN)*  (DECIMAL_DIGIT)+
	;	
	
protected
SIGN
	:	'+' | '-'
	;
	
protected
REAL_TYPE_SUFFIX
	: 'F' | 'f' | 'D' | 'd' | 'M' | 'm'
	;

// chars
protected
CHARACTER
	:	SINGLE_CHARACTER
	|	SIMPLE_ESCAPE_SEQUENCE
	|	HEXADECIMAL_ESCAPE_SEQUENCE
	|	UNICODE_ESCAPE_SEQUENCE
	;

protected
SINGLE_CHARACTER
	:	 ~( '\'' | '\\' | '\u000D' | '\u000A' | '\u2028' | '\u2029')
	;
	
protected
SIMPLE_ESCAPE_SEQUENCE
	:	"\\'" | "\\\"" | "\\\\" | "\\0" | "\\a"  
	|	"\\b" | "\\f"  | "\\n"  | "\\r" | "\\t" | "\\v"
	;
	
protected	
HEXADECIMAL_ESCAPE_SEQUENCE	
	:	('\\' 'x' HEX_DIGIT)
		( options {warnWhenFollowAmbig = false;}: 
		HEX_DIGIT 
			( options {warnWhenFollowAmbig = false;}:
			HEX_DIGIT 
				( options {warnWhenFollowAmbig = false;}:
				HEX_DIGIT
				)?
			)?
		)? // oh antlr syntax
	;

// strings
protected	
REGULAR_STRING_LITERAL
	:	'\"'!   
		(	rs:REGULAR_STRING_LITERAL_CHARACTER
		)*   
		'\"'!
	;
	
protected	
REGULAR_STRING_LITERAL_CHARACTER
	:	SINGLE_REGULAR_STRING_LITERAL_CHARACTER
	|	SIMPLE_ESCAPE_SEQUENCE
	|	HEXADECIMAL_ESCAPE_SEQUENCE
	|	UNICODE_ESCAPE_SEQUENCE
	;
	
protected	
SINGLE_REGULAR_STRING_LITERAL_CHARACTER
	:	 ~( '\"' | '\\' | '\u000D' | '\u000A' | '\u2028' | '\u2029')
	;
	
protected	
VERBATIM_STRING_LITERAL
{string s="";}
	:	 '@' "\""  	
		(	"\"\""				{s+=("\"");}
		|	"\\"				{s+=("\\\\");}
		|	ch:~('\"' | '\\')	{s+=(ch);}
		)* 
		"\""	
		{$setText(s);}
	;
			
	
// ***** A.1.9 Operators and punctuators *****
/*
Operator_or_punctuator
	:	'{'	|	'}'	|	'['	|	']'	|	'('	|	')'	|			','	|	':'	|	';'
	|	'+'	|	'-'	|	'*'	|	'/'	|	'%'	|	'&'	|	'|'	|	'^'	|	'!'	|	'~'
	|	'='	|	'<'	|	'>'	|	'?'	|	"++"|	"--"|	"&&"|	"||"|	"<<"|	">>"
	|	"=="|	"!="|	"<="|	">="|	"+="|	"-="|	"*="|	"/="|	"%="|	"&="
	|	"|="|	"^="| "<<="	| ">>=" |	"->"
	;
*/
LBRACE		:	'{'		;	RBRACE		:	'}'		;
LBRACK		:	'['		;	RBRACK		:	']'		;
LPAREN		:	'('		;	RPAREN		:	')'		;


PLUS		:	'+'		;	PLUS_ASN	:	"+="	;	
MINUS		:	'-'		;	MINUS_ASN	:	"-="	;	
STAR		:	'*'		;	STAR_ASN	:	"*="	;
DIV			:	'/'		;	DIV_ASN		:	"/="	;
MOD			:	'%'		;	MOD_ASN		:	"%="	;
INC			:	"++"	;	DEC			:	"--"	;

SL			:	"<<"	;	SL_ASN		:	"<<="	;
SR			:	">>"	;	SR_ASN		:	">>="	;
BSR			:	">>>"	;	BSR_ASN		:	">>>="	;

BAND		:	'&'		;	BAND_ASN	:	"&="	;	
BOR			:	'|'		;	BOR_ASN		:	"|="	;	
BXOR		:	'^'		;	BXOR_ASN	:	"^="	;
BNOT		:	'~'		;

ASSIGN		:	'='		;	EQUAL		:	"=="	;
LTHAN		:	'<'		;	LE			:	"<="	;
GTHAN		:	">"		;	GE			:	">="	;
LNOT		:	'!'		;	NOT_EQUAL	:	"!="	;
LOR			:	"||"	;	LAND		:	"&&"	;

COMMA		:	','		;	COLON		:	':'		;	
SEMI		:	';'		;	HASH		:	"#"     ;
QUOTE		:	"\""    ;	QUESTION	:	'?'		;


// ***** A.1.10 Pre_processing directives *****
protected	
PP_NEW_LINE
	:	(SINGLE_LINE_COMMENT | NEW_LINE_CHARACTER)
	;
protected	
PP_WHITESPACE
	:	(	' '
		|	'\u0009' // horizontal tab character
		|	'\u000B' // vertical tab character
		|	'\u000C' // form feed character 
		)+
		{ _ttype = Token.SKIP; }
	;

PP_DIRECTIVE
	:	 HASH (PP_WHITESPACE)?
		(	dc:PP_DECLARATION	{Console.Write("===>decl: "+dc.getText());}
		| 	rg:PP_REGION		{Console.Write("===>regn: "+rg.getText());}
//		|	PP_CONDITIONAL
//		|	PP_LINE
//		|	PP_DIAGNOSTIC
		)
	{ _ttype = Token.SKIP; }
	;
protected	
PP_DECLARATION
	:	(PPT_DEFINE | PPT_UNDEF)(PP_WHITESPACE)? CONDITIONAL_SYMBOL PP_NEW_LINE
	;
protected	
PP_REGION
	:	(PPT_REGION | PPT_END_REGION) PP_MESSAGE	
	;
protected
PP_MESSAGE
	:	(NOT_NEW_LINE)* NEW_LINE
	;
protected
CONDITIONAL_SYMBOL
	:	IDENTIFIER_START_CHARACTER (IDENTIFIER_PART_CHARACTER)*
	;
protected
PPT_DEFINE
	:	"define"
	;
protected
PPT_UNDEF
	:	"undef"
	;
protected
PPT_REGION
	:	"region"
	;
protected
PPT_END_REGION
	:	"endregion"
	;

/*
// skip conditional compilation for now 
protected	
PP_CONDITIONAL
	:	PP_IF_SECTION PP_ENDIF
	;
protected	
PP_IF_SECTION
	:	PPT_IF PP_EXPRESSION PP_NEW_LINE// (CONDITIONAL_SECTION)?
	;
protected	
PP_ENDIF
	:	HASH PPT_ENDIF PP_NEW_LINE
	;
	
protected	
PP_SIGN  
	:	(LOR | LAND | EQUAL | NOT_EQUAL | LNOT)
	;

protected	
PP_EXPRESSION  
	:	PP_PRIMARY_EXPRESSION
		(PP_SIGN PP_PRIMARY_EXPRESSION)?
	;

protected	
PP_OR_EXPRESSION
	:	PP_AND_EXPRESSION (LOR PP_AND_EXPRESSION)?
	;
protected	
PP_AND_EXPRESSION
	:	PP_EQUALITY_EXPRESSION ((PP_WHITESPACE)? LAND  (PP_WHITESPACE)? PP_EQUALITY_EXPRESSION)?
	;
protected	
PP_EQUALITY_EXPRESSION
	:	PP_UNARY_EXPRESSION ((EQUAL | NOT_EQUAL)  PP_UNARY_EXPRESSION)? 
	;
protected	
PP_UNARY_EXPRESSION
	:	PP_PRIMARY_EXPRESSION (LNOT  PP_PRIMARY_EXPRESSION)?
	;

protected	
PP_PRIMARY_EXPRESSION
	:	(PPT_TRUE)=>PPT_TRUE 		{Console.WriteLine("  ===>true ");}
	|	(PPT_FALSE)=>PPT_FALSE 		{Console.WriteLine("  ===>false ");}
	|	LPAREN 
		ex:PP_EXPRESSION 
		RPAREN 						{Console.WriteLine("  ===>expr "+ex.getText());}
	|	((PP_WHITESPACE)? 
		cs:CONDITIONAL_SYMBOL 
		(PP_WHITESPACE)?)  			{Console.WriteLine("  ===>symbol "+cs.getText());}
	;

	
protected
PPT_IF
	:	"if"
	;
protected
PPT_ENDIF
	:	"endif"
	;
protected
PPT_TRUE
	:	"true"
	;
protected
PPT_FALSE
	:	"false"
	;


protected	
PP_ELIF_SECTIONS:
	:	PP_ELIF_SECTION
	|	PP_ELIF_SECTIONS PP_ELIF_SECTION
	;

PP_ELIF_SECTION:
	:	(WHITESPACE)? # (WHITESPACE)? ELIF WHITESPACE PP_EXPRESSION PP_NEW_LINE (CONDITIONAL_SECTION)?
	;
PP_ELSE_SECTION:
	:	(WHITESPACE)? # (WHITESPACE)? ELSE PP_NEW_LINE (CONDITIONAL_SECTION)?
	;
*/


// =======================================================
// == TREE WALKER ========================================
// =======================================================

// TODO: treewalker: move AST to non antlr code dom structure (must be cls compliant).	
//class CSharpTreeWalker extends TreeParser;
//compilation_unit returns [string s]
//{
//	string tabs = "\t\t\t";
//	s = "";
//}	
//	:	((any:.) 
//		{	int tabCount = 3-((int)(any.getText().Length / 8)); 
//			s += "\n"+any.getText()+tabs.Substring(3-tabCount)+any.Type;}
//		)*
//	{return s;}
//	;
	
	
	
	