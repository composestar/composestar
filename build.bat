cd Core
	Cd compiletime
		make clean
		make
	cd ..

	cd runtime
		make clean
		make
	cd ..
cd..

cd DotNet
	cd compiletime
rem		make clean
		make
	cd ..
	
	cd runtime
		make clean
		make
	cd ..

        cd plugin
                cd ComposestarVSAddin
                        make
                cd ..
        cd ..
cd ..


