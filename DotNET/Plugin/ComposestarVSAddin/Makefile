ROOTDIR=../../

src:
	del error.log
	("devenv.exe" ComposestarVSAddin.sln /rebuild Release /out error.log || (@type error.log && _fail 2>NUL))

clean:
	del error.log
