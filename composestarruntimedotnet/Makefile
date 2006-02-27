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
	@echo       src (Just builds the system)
	@echo       install (Installs the system)
	@echo       runtime (Builds the runtime)
	@echo       test (RBuilds and runs the unit tests)
	@echo       build_test (Builds the unit tests)
	@echo       run_test (runs the unit tests)

rebuild: clean build

build: src install

runtime: src
	
src:
	-${DEL} error.log ${NULL}
	"devenv.exe" composestarruntimedotnet.sln /rebuild Release /out error.log
	type error.log	

clean:
	${DEL} error.log ${NULL}

install:
	@$(INSTALLFINDER)

installit:
	@echo **************************************************************
	@echo  Installing to ${INSTALLDIR};                                 
	@echo ************************************************************** 
	@$(fileCopy) "src$(PATHSEP)FLIRT$(PATHSEP)bin$(PATHSEP)Release$(PATHSEP)ComposestarCore.dll" "$(INSTALLDIR)$(PATHSEP)binaries"
	@$(fileCopy) "src$(PATHSEP)FLIRT$(PATHSEP)bin$(PATHSEP)Release$(PATHSEP)ComposestarCoreDotNET.dll" "$(INSTALLDIR)$(PATHSEP)binaries"
	@$(fileCopy) "src$(PATHSEP)FLIRT$(PATHSEP)bin$(PATHSEP)Release$(PATHSEP)ComposeStarUtilities.dll" "$(INSTALLDIR)$(PATHSEP)binaries"
	@$(fileCopy) "src$(PATHSEP)FLIRT$(PATHSEP)bin$(PATHSEP)Release$(PATHSEP)ComposeStarRuntimeInterpreter.dll" "$(INSTALLDIR)$(PATHSEP)binaries"
	@$(fileCopy) "src$(PATHSEP)FLIRT$(PATHSEP)bin$(PATHSEP)Release$(PATHSEP)ComposeStarDotNETRuntimeInterpreter.dll" "$(INSTALLDIR)$(PATHSEP)binaries"
	@$(fileCopy) "src$(PATHSEP)FLIRT$(PATHSEP)bin$(PATHSEP)Release$(PATHSEP)DotNETPlatformInterface.dll" "$(INSTALLDIR)$(PATHSEP)binaries"
	@$(fileCopy) "src$(PATHSEP)FLIRT$(PATHSEP)bin$(PATHSEP)Release$(PATHSEP)ComposeStarDotNETUtilities.dll" "$(INSTALLDIR)$(PATHSEP)binaries"
	@$(fileCopy) "src$(PATHSEP)FLIRT$(PATHSEP)bin$(PATHSEP)Release$(PATHSEP)ComposeStarFilterDebugger.dll" "$(INSTALLDIR)$(PATHSEP)binaries"
	@$(fileCopy) "src$(PATHSEP)FLIRT$(PATHSEP)bin$(PATHSEP)Release$(PATHSEP)AntlrDotNet.dll" "$(INSTALLDIR)$(PATHSEP)binaries"
