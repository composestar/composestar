%% Definition of language unit types

isClass(Unit) :-
  check_or_gen1(isUnitTypeBuiltin(Unit, 'Class')).

isClassWithName(Unit, Name) :-
  isUnitNameBuiltin(Unit, Name, 'Class').

isClassWithAttribute(Unit, Attr) :-
  check_or_gen12(hasAttributeBuiltin(Unit, Attr, 'Class')).

isClassWithNameInList(Unit, [Name|Ns]) :-
  or(isClassWithName(Unit, Name), isClassWithNameInList(Unit,Ns)).

classHasAnnotationWithName(Unit, AnnotName) :-
  isAnnotationWithName(A, AnnotName), classHasAnnotation(Unit, A).


isType(Unit) :-
  check_or_gen1(isUnitTypeBuiltin(Unit, 'Type')).

isTypeWithName(Unit, Name) :-
  isUnitNameBuiltin(Unit, Name, 'Type').

isTypeWithAttribute(Unit, Attr) :-
  check_or_gen12(hasAttributeBuiltin(Unit, Attr, 'Type')).

isTypeWithNameInList(Unit, [Name|Ns]) :-
  or(isTypeWithName(Unit, Name), isTypeWithNameInList(Unit,Ns)).

typeHasAnnotationWithName(Unit, AnnotName) :-
  isAnnotationWithName(A, AnnotName), typeHasAnnotation(Unit, A).


isField(Unit) :-
  check_or_gen1(isUnitTypeBuiltin(Unit, 'Field')).

isFieldWithName(Unit, Name) :-
  check_or_gen1(isUnitNameBuiltin(Unit, Name, 'Field')).

isFieldWithAttribute(Unit, Attr) :-
  check_or_gen12(hasAttributeBuiltin(Unit, Attr, 'Field')).

isFieldWithNameInList(Unit, [Name|Ns]) :-
  or(isFieldWithName(Unit, Name), isFieldWithNameInList(Unit,Ns)).

fieldHasAnnotationWithName(Unit, AnnotName) :-
  isAnnotationWithName(A, AnnotName), fieldHasAnnotation(Unit, A).


isNamespace(Unit) :-
  check_or_gen1(isUnitTypeBuiltin(Unit, 'Namespace')).

isNamespaceWithName(Unit, Name) :-
  isUnitNameBuiltin(Unit, Name, 'Namespace').

isNamespaceWithAttribute(Unit, Attr) :-
  check_or_gen12(hasAttributeBuiltin(Unit, Attr, 'Namespace')).

isNamespaceWithNameInList(Unit, [Name|Ns]) :-
  or(isNamespaceWithName(Unit, Name), isNamespaceWithNameInList(Unit,Ns)).

namespaceHasAnnotationWithName(Unit, AnnotName) :-
  isAnnotationWithName(A, AnnotName), namespaceHasAnnotation(Unit, A).


isAnnotation(Unit) :-
  check_or_gen1(isUnitTypeBuiltin(Unit, 'Annotation')).

isAnnotationWithName(Unit, Name) :-
  isUnitNameBuiltin(Unit, Name, 'Annotation').

isAnnotationWithAttribute(Unit, Attr) :-
  check_or_gen12(hasAttributeBuiltin(Unit, Attr, 'Annotation')).

isAnnotationWithNameInList(Unit, [Name|Ns]) :-
  or(isAnnotationWithName(Unit, Name), isAnnotationWithNameInList(Unit,Ns)).

annotationHasAnnotationWithName(Unit, AnnotName) :-
  isAnnotationWithName(A, AnnotName), annotationHasAnnotation(Unit, A).


isMethod(Unit) :-
  check_or_gen1(isUnitTypeBuiltin(Unit, 'Method')).

isMethodWithName(Unit, Name) :-
  isUnitNameBuiltin(Unit, Name, 'Method').

isMethodWithAttribute(Unit, Attr) :-
  check_or_gen12(hasAttributeBuiltin(Unit, Attr, 'Method')).

isMethodWithNameInList(Unit, [Name|Ns]) :-
  or(isMethodWithName(Unit, Name), isMethodWithNameInList(Unit,Ns)).

methodHasAnnotationWithName(Unit, AnnotName) :-
  isAnnotationWithName(A, AnnotName), methodHasAnnotation(Unit, A).


isParameter(Unit) :-
  check_or_gen1(isUnitTypeBuiltin(Unit, 'Parameter')).

isParameterWithName(Unit, Name) :-
  check_or_gen1(isUnitNameBuiltin(Unit, Name, 'Parameter')).

isParameterWithAttribute(Unit, Attr) :-
  check_or_gen12(hasAttributeBuiltin(Unit, Attr, 'Parameter')).

isParameterWithNameInList(Unit, [Name|Ns]) :-
  or(isParameterWithName(Unit, Name), isParameterWithNameInList(Unit,Ns)).

parameterHasAnnotationWithName(Unit, AnnotName) :-
  isAnnotationWithName(A, AnnotName), parameterHasAnnotation(Unit, A).




%% Definition of relations between language units

methodHasAnnotation(Method, Annotation) :-
  methodHasAnnotationBuiltin(Method, Annotation).


namespaceHasNamespace(Namespace, Namespace) :-
  check_or_gen1(namespaceHasNamespaceBuiltin(Namespace, Namespace)).


parameterHasType(Parameter, Type) :-
  parameterHasTypeBuiltin(Parameter, Type).


parameterHasAnnotation(Parameter, Annotation) :-
  parameterHasAnnotationBuiltin(Parameter, Annotation).


fieldHasAnnotation(Field, Annotation) :-
  fieldHasAnnotationBuiltin(Field, Annotation).


classHasMethod(Class, Method) :-
  check_or_gen1(classHasMethodBuiltin(Method, Class)).


classHasAnnotation(Class, Annotation) :-
  check_or_gen1(classHasAnnotationBuiltin(Annotation, Class)).


classHasField(Class, Field) :-
  check_or_gen1(classHasFieldBuiltin(Field, Class)).


methodHasReturnType(Method, Type) :-
  methodHasReturnTypeBuiltin(Method, Type).


namespaceHasClass(Namespace, Class) :-
  check_or_gen1(namespaceHasClassBuiltin(Class, Namespace)).


methodHasClass(Method, Class) :-
  check_or_gen1(methodHasClassBuiltin(Method, Class)).


fieldHasType(Field, Type) :-
  fieldHasTypeBuiltin(Field, Type).


methodHasParameter(Method, Parameter) :-
  check_or_gen1(methodHasParameterBuiltin(Parameter, Method)).


