ROOTDIR=./

include ${ROOTDIR}common.mk

all: build 

.PHONY: src \
	test \
	clean

help:
	@echo **************************
	@echo  Compose* Building system
	@echo **************************
	@echo Usage: Make command options
	@echo Commands:
	@echo       all (Builds the system, installs it and tests it)
	@echo       clean (Removes all builded files)
	@echo       help (Displays this text)
	@echo       build (Builds the system and installs it)
	@echo       rebuild (Rebuilds the system and installs it)
	@echo       jar (Builds the system and generates a binary jar file)
	@echo       src (Just builds the system)
	@echo       install (Installs the system)
	@echo	      runtime (Builds the runtime)
	@echo       test (RBuilds and runs the unit tests)
	@echo       build_test (Builds the unit tests)
	@echo       run_test (runs the unit tests)

rebuild: clean build

build: src 

test: build_test run_test
	
jar:	
	-${RMDIR} jarsource ${NULL}
	${MKDIR} jarsource
	${COPY} "src\*.class" "jarsource\"
	jar -cfmv "ComposestarCORE.jar" "src\MANIFEST.MF" -C jarsource .
	-${RMDIR} jarsource

src:
	cd src && ${MAKE} && cd ..	

clean:
	${DEL} *.exe ${NULL}
	${DEL} *.dll ${NULL}
	${DEL} *~ ${NULL}
	${DEL} ComposestarCORE.jar ${NULL}
	cd src && ${MAKE} clean && cd ..
	cd test && ${MAKE} clean && cd ..

install:
	@$(INSTALLFINDER)

installit:
	@echo **************************************************************
	@echo  Installing to ${INSTALLDIR};                                 
	@echo ************************************************************** 
	@$(COPY) Composestar.jar "$(INSTALLDIR)binaries"
	@$(fileCopy) src\Composestar\CTCommon\INCRE\INCRE.css "$(INSTALLDIR)"
	@$(fileCopy) src\Composestar\CTCommon\INCRE\INCREconfig.xml "$(INSTALLDIR)"
