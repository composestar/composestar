*******************************************************************************************************************
*														  *
*														  *
*						Compose* System Test						  *
*														  *
*														  *
*														  *
*******************************************************************************************************************

[Compilation and running without CVS update]

1. Get the latest SystemTest package from CVS (UPDATE SystemTest directory)
	
2. Add devenv to your path: devenv should be an external command
   Possible directory: C:\Program Files\Microsoft Visual Studio .NET 2003\Common7\IDE

3. Add MyMacros.vsmacros to your Visual Studio Macros under the MyMacros directory
   (e.g C:\Documents and Settings\composestar\My Documents\Visual Studio Projects\VSMacros71\MyMacros)
   If you are already using MyMacros then simply add a module called SystemTest to this macro 
   and copy the code from macro.txt	

4. Add the paths of your examples to be compiled to config.ini 
	
	e.g: 	[EXAMPLES]
		Example0=C:\local\dennis\examples\Pacman\Pacman~1.sln
		Example1=C:\local\dennis\examples\VenusFlyTrap\VenusF~1.sln
		Example2=C:\local\dennis\examples\Platypus\Platyp~1.sln

	or e.g.:
		[EXAMPLES]
		ExampleDirectory=C:\local\dennis\examples

	combinations are also posible e.g.
		[EXAMPLES]
		ExampleDirectory=C:\local\dennis\examples
		Example0=C:\local\dennis\examples\quicktest\qtest.sln


5. Add the paths of your runnables to config.ini

	e.g: 	[RUNNABLE_EXAMPLES]
		Example0=C:\local\dennis\examples\Platypus\bin\Main.exe

6. For each runnable example place a correct output file in the bin directory (TIP: Main.exe > correct.txt)
   ! the name of the file must be the same as mentioned in config.ini (correct.txt by default)

7. Configurate the mail settings in config.ini (i don't want your mail:))

8. Start by executing nocvs.bat

9. Look at the console or your mail to see the results
   The build output is stored in the solution directory of each project
   The output of the execution is stored in the bin directory of each project 		

[Compilation and running without CVS update]

coming...
		  


	