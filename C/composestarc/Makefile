ROOTDIR=./
INSTALLDIR=C:\local\Johan\Composestar

include ${ROOTDIR}common.mk

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
	jar -cfmv "ComposestarC.jar" "src\MANIFEST.MF" -C jarsource .
	-${RMDIR} jarsource

src:
	cd src && ${MAKE} && cd ..	

plugin:

clean:
	${DEL} *.exe ${NULL}
	${DEL} *.dll ${NULL}
	${DEL} *~ ${NULL}
	${DEL} ComposestarC.jar ${NULL}
	cd src && ${MAKE} clean && cd ..

install:
	@echo **************************************************************
	@echo  Installing to ${INSTALLDIR};                                 
	@echo ************************************************************** 
	@$(fileCopy) "ComposestarC.jar" "$(INSTALLDIR)$(PATHSEP)binaries"
	
	