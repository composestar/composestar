% Really lowlevel stuff that you really should not mess with.

% There are 3 predicates for generating values from a Source.
%
% Use check_or_gen1(func(Var[,...])) to generate or check values for func(Var[,...]). There can be more arguments,
% but they will not be touched/changed by the generator.
%
% Use check_or_gen2(pred(Var1, Var2[,...])) to generate values for either Var1 or Var2 - the other must be
% bound or be assigned a single return value. There can be other arguments which will not be changed.
%
% Use check_or_gen12(func(Var1, Var2,[...])) when both Var1 and Var2 might return a Source.
%
% The more powerful the predicate, the slower execution will be. So use check_or_gen1 whenever possible (!!)
% 

% TODO: actually check whether a Source is returned, ignore other return values (such as JavaObjects!!!) XXX

% Input: a term with at least 1 argument, such as 'isUnit(Uid)'.
% Output: True if Arg1 of Func1 was specified and Func1(Arg1) holds true, fails if not.
%         All possible values for Arg1 generated using Gen1 if Arg1 was not specified.
% The term can have multiple arguments, but only results generated in the 1st arg will be returned!

check_or_gen1(Func1) :- 
  argn(1, Func1, Arg1),  % Arg1=1st argument of Func1
  if (type_of(Arg1,var), % XXXX ARGH!!!! const), % Is the argument bound?
      generate1(Func1),  % No, so generate the list of possible values.
      Func1).           % Yes, so just execute the check

generate1(Func1) :-
  copy_term(Func1,GenFunc1),
  GenFunc1, % Generate value source 'Args'
  argn(1, GenFunc1, Args),
  argn(1, Func1, Arg1),
  if (type_of(Args, const),
      eq(Args,Arg1),
      element_of(Args, Arg1)). % bind Arg1 to all possible values in Args.

% Input: a term with at least 2 arguments, one of which should be bound.
% The other one will become bound to possible values for that term, or fail if none exist (also see check_or_gen1).
check_or_gen2(Func2) :- argn(2, Func2, Arg2), % Arg2=2nd argument of Func2
                        if (type_of(Arg2,var),  % Is the 2nd argument bound?
                            generate2(Func2),     % No, so we have to generate a list of possibilities.
                            check_or_gen1(Func2)).% Yes, so check_or_gen1 can handle it!

% In this case we assume that the 1st arg is bound (the builtin function should return an
%  error otherwise so it will never match)
generate2(Func2) :- 
  copy_term(Func2, GenFunc2),
  GenFunc2, % Generate values for Arg2
  argn(2, GenFunc2, Args),
  argn(2, Func2, Arg2),
  if (type_of(Args,const),
      eq(Args,Arg2),
      element_of(Args, Arg2)). % Bind 2nd arg to all possible values in Args.

% Input: a term with at least 2 arguments. Neither has to be bound (but can be).
check_or_gen12(Func2) :- 
  argn(2, Func2, Arg2), % Arg2=2nd argument of Func2
  if (type_of(Arg2,var), % Is the 2nd argument bound?
      generate12(Func2),    % No, so we have to generate a list of possibilities.
      check_or_gen1(Func2)).% Yes, so check_or_gen1 can handle it!

generate12(Func2) :- 
  copy_term(Func2, GenFunc2),
  GenFunc2, % Generate values for Arg2
  argn(2, GenFunc2, Args2),
  argn(2, Func2, Arg2),
  if (type_of(Args2,const),
      eq(Args2,Arg2),
      element_of(Args2, Arg2)), % Bind 2nd arg to all possible values in Args2.
  argn(1, Func2, Arg1),
  if (type_of(Arg1, var),
    generate1(Func2), % Now that we have bound Arg2 we can use generate1 to get values for Arg1
    true). % If Arg1 was bound already, we don't have to change anything




inherits(Parent, Child) :-
  isSuperClass(Parent, Child).
inherits(Parent, Child) :-
  if (type_of(Child,const),
      inheritsKnownChild(Parent, Child),
      inheritsKnownParent(Parent,Child)).
inheritsKnownParent(Parent, Child) :-
  isSuperClass(Parent, Between),
  inherits(Between, Child).
inheritsKnownChild(Parent, Child) :-
  isSuperClass(Between, Child),
  inherits(Parent, Between).
  
inheritsOrSelf(Self, Self).
inheritsOrSelf(Parent, Child) :- inherits(Parent, Child).