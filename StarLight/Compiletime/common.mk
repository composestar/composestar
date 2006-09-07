ifeq ($(OS), Darwin)
	SEP=:
	MAKE=make
	RECURSE_MAKE_RULE=if [ "$(SUBDIRS)" != "" ]; then for a in $(SUBDIRS); do cd $$a && $(MAKE) && cd ..; done fi
	RECURSE_CLEAN_RULE=if [ "$(SUBDIRS)" != "" ]; then for a in $(SUBDIRS); do cd $$a && $(MAKE) clean && cd ..; done fi
	DEL=rm -f
	MKDIR=mkdir
	COPY=cp -r
	filecopy=cp
	RMDIR=rm -rf
	NULL=
	INSTALLDIR=/var/bin/
	INSTALLFINDER=$(MAKE) installit
	PATHSEP=/
else 
ifeq ($(OS), linux)
	SEP=:
	MAKE=make
	RECURSE_MAKE_RULE=if [ "$(SUBDIRS)" != "" ]; then for a in $(SUBDIRS); do cd $$a && $(MAKE) && cd ..; done fi
	RECURSE_CLEAN_RULE=if [ "$(SUBDIRS)" != "" ]; then for a in $(SUBDIRS); do cd $$a && $(MAKE) clean && cd ..; done fi
	DEL=rm -f
	MKDIR=mkdir
	COPY=cp -r
	filecopy=cp
	RMDIR=rm -rf
	NULL=
	INSTALLDIR=/var/bin/
	INSTALLFINDER=$(MAKE) installit
	PATHSEP=/
else
	SEP=;
	MAKE=make.exe
	RECURSE_MAKE_RULE=if not "$(SUBDIRS)" == "" for %%a in ( $(SUBDIRS) ) do cd %%a && $(MAKE) && cd..
	RECURSE_CLEAN_RULE=if not "$(SUBDIRS)" == "" for %%a in ( $(SUBDIRS) ) do cd %%a && $(MAKE) clean && cd..
	DEL=del
	MKDIR=md
	COPY=xcopy /s /y
	fileCopy=copy /Y
	RMDIR=rmdir /s /q
	NULL=2>NUL
	INSTALLFINDER=installIt.bat $(MAKE)
	PATHSEP=\\
endif
endif


CLASSPATH=.$(SEP)../$(ROOTDIR)composestarcore/src$(SEP)$(ROOTDIR)binaries/junit/junit.jar$(SEP)$(ROOTDIR)binaries/prolog/prolog.jar$(SEP)../$(ROOTDIR)composestarcore/binaries/antlr/antlr.jar
JAVAC_RULE=javac -target 1.4 -source 1.4 -sourcepath $(ROOTDIR)src$(SEP)$(ROOTDIR) -classpath $(CLASSPATH)  $<

ANTLR_RULE=java -classpath $(CLASSPATH) antlr.Tool 
RUN=java -classpath $(CLASSPATH)$(SEP)../../../

CSHARP_ICON_SWITCH=/win32icon:$(ICON)
CSHARP_EXE_RULE=csc /t:exe /nologo $(ICONSWITCH) /out:$(ROOTDIR)$@ /recurse:*.cs
CSHARP_DLL_RULE=csc /t:library /nologo $(ICONSWITCH) /out:$(ROOTDIR)$@ /recurse:*.cs
