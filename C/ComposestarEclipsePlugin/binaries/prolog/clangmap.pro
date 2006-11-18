isFile(Unit) :-
  isClass(Unit).

isFileWithName(Unit, Name) :-
  isClassWithName(Unit, Name).

isFileWithAttribute(Unit, Attr) :-
  isClassWithAttribute(Unit, Attr).

isFileWithNameInList(Unit, [Name|Ns]) :-
  isClassWithNameInList(Unit, [Name|Ns]).

fileHasAnnotationWithName(Unit, AnnotName) :-
  classHasAnnotationWithName(Unit, AnnotName).
  
isDirectory(Unit) :-
  isNamespace(Unit).

isDirectoryWithName(Unit, Name) :-
  isNamespaceWithName(Unit, Name).

isDirectoryWithAttribute(Unit, Attr) :-
  isNamespaceWithAttribute(Unit, Attr).

isDirectoryWithNameInList(Unit, [Name|Ns]) :-
  isNamespaceWithNameInList(Unit, [Name|Ns]).

directoryHasAnnotationWithName(Unit, AnnotName) :-
  namespaceHasAnnotationWithName(Unit, AnnotName).

isFunction(Unit):-
  isMethod(Unit).

isFunctionWithName(Unit, Name):-
  isMethodWithName(Unit, Name).

isFunctionWithAttribute(Unit, Attr):-
  isMethodWithAttribute(Unit, Attr).

isFunctionWithNameInList(Unit, [Name|Ns]):-
  isMethodWithNameInList(Unit, [Name|Ns]).

functionHasAnnotationWithName(Unit, AnnotName):-
  methodHasAnnotationWithName(Unit, AnnotName).

isVariable(Unit) :-
  isField(Unit).

isVariableWithName(Unit, Name):-
	isFieldWithName(Unit, Name).

isVariable(Unit) :-
	isFieldWithAttribute(Unit, Attr) .

isVariableWithNameInList(Unit, [Name|Ns]) :-
	isFieldWithNameInList(Unit, [Name|Ns]).

variableHasAnnotationWithName(Unit, AnnotName):-
	fieldHasAnnotationWithName(Unit, AnnotName).
	
functionHasAnnotation(Unit, AnnotName):-
	methodHasAnnotation(Unit, AnnotName).
	
directoryHasDirectory(SubDir, ParentDir):-	
	namespaceHasNamespace(SubDir, ParentDir).

variableHasAnnotation(Field, Annotation) :-
  fieldHasAnnotation(Field, Annotation).

fileHasFunction(Class, Method):-
	classHasMethod(Class, Method).

fileHasAnnotation(Class, Annotation) :-
  classHasAnnotation(Class, Annotation).

fileHasVariable(Class, Field) :-
  classHasField(Class, Field).

functionHasReturnType(Method, Type) :-
  methodHasReturnType(Method, Type).

directoryHasFile(Namespace, Class) :-
  namespaceHasClass(Namespace, Class).

functionHasFile(Method, Class) :-
  methodHasClass(Method, Class).

variableHasType(Field, Type) :-
  fieldHasType(Field, Type).

functionHasParameter(Method, Parameter) :-
  methodHasParameterBuiltin(Parameter, Method).
  
