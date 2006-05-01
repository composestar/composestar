ROOTDIR=./

include ${ROOTDIR}common.mk

WEAVER=src$(PATHSEP)Composestar$(PATHSEP)DotNET$(PATHSEP)ILICIT$(PATHSEP)Weaver
CSHARPDUMMYGEN=src$(PATHSEP)Composestar$(PATHSEP)DotNET$(PATHSEP)DUMMER$(PATHSEP)CSharpDummyGenerator

all: build 

.PHONY: src \
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

build: jar install

jar: src
	-${RMDIR} jarsource ${NULL}
	${MKDIR} jarsource
	${COPY} "src\*.class" "jarsource\"
	jar -cfmv "ComposestarDotNET.jar" "src\MANIFEST.MF" -C jarsource .
	-${RMDIR} jarsource

src:
	cd src && ${MAKE} && cd ..	

plugin:
	cd plugin\ComposestarVSAddin && ${MAKE} && cd ..\..

clean:
	${DEL} *.exe ${NULL}
	${DEL} *.dll ${NULL}
	${DEL} *~ ${NULL}
	${DEL} ComposestarDotNET.jar ${NULL}
	cd src && ${MAKE} clean && cd ..

install:
	@$(INSTALLFINDER)

installit:
	@echo **************************************************************
	@echo  Installing to ${INSTALLDIR};                                 
	@echo ************************************************************** 
	@$(fileCopy) "ComposestarDotNET.jar" "$(INSTALLDIR)$(PATHSEP)binaries"
	@$(fileCopy) "AttributeHarvester.exe" "$(INSTALLDIR)$(PATHSEP)binaries"
	@$(fileCopy) "TypeHarvester.exe" "$(INSTALLDIR)$(PATHSEP)binaries"
	@$(fileCopy) "$(WEAVER)$(PATHSEP)bin$(PATHSEP)Release$(PATHSEP)WeaveLibrary.dll" "$(INSTALLDIR)$(PATHSEP)binaries"
	@$(fileCopy) "$(WEAVER)$(PATHSEP)bin$(PATHSEP)Release$(PATHSEP)IlWeaver.exe" "$(INSTALLDIR)$(PATHSEP)binaries"
	@$(fileCopy) "$(WEAVER)$(PATHSEP)bin$(PATHSEP)Release$(PATHSEP)PeWeaver.exe" "$(INSTALLDIR)$(PATHSEP)binaries"
	@$(fileCopy) "$(CSHARPDUMMYGEN)$(PATHSEP)CSharpDummyGenerator.exe" "$(INSTALLDIR)$PATHSEPbinaries"
	@$(fileCopy) "$(CSHARPDUMMYGEN)$(PATHSEP)antlr.runtime.dll" "$(INSTALLDIR)$PATHSEPbinaries"
