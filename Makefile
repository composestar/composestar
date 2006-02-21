ROOTDIR=./
INSTALLDIR=C:\Program Files\ComposeStar

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
	@echo       test (Builds and runs the unit tests)
	@echo       build_test (Builds the unit tests)
	@echo       run_test (runs the unit tests)

rebuild: clean build

build: src jar install

test: build_test run_test
	
jar: src
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
#	cd test && ${MAKE} clean && cd ..

install:
	@echo **************************************************************
	@echo  Installing to ${INSTALLDIR};                                 
	@echo ************************************************************** 
	@$(fileCopy) "binaries$(PATHSEP)antlr$(PATHSEP)antlr.jar" "$(INSTALLDIR)$(PATHSEP)binaries"
	@$(fileCopy) "binaries$(PATHSEP)prolog$(PATHSEP)prolog.jar" "$(INSTALLDIR)$(PATHSEP)binaries$(PATHSEP)prolog"
	@$(fileCopy) "binaries$(PATHSEP)prolog$(PATHSEP)lib.pro" "$(INSTALLDIR)$(PATHSEP)binaries$(PATHSEP)prolog"
	@$(fileCopy) "binaries$(PATHSEP)prolog$(PATHSEP)connector.pro" "$(INSTALLDIR)$(PATHSEP)binaries$(PATHSEP)prolog"
	@$(fileCopy) "ComposestarCORE.jar" "$(INSTALLDIR)$(PATHSEP)binaries"
	@$(fileCopy) "src$(PATHSEP)Composestar$(PATHSEP)Core$(PATHSEP)INCRE$(PATHSEP)INCRE.css" "$(INSTALLDIR)"
	@$(fileCopy) "src$(PATHSEP)Composestar$(PATHSEP)Core$(PATHSEP)INCRE$(PATHSEP)INCREconfig.xml" "$(INSTALLDIR)"
	@$(fileCopy) "src$(PATHSEP)Composestar$(PATHSEP)Core$(PATHSEP)CKRET$(PATHSEP)SECRET.css" "$(INSTALLDIR)"
	@$(fileCopy) "src$(PATHSEP)Composestar$(PATHSEP)Core$(PATHSEP)CKRET$(PATHSEP)filterdesc.xml" "$(INSTALLDIR)"
