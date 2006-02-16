compile: recurse_classes $(CLASSES) $(ASSEMBLIES)

recurse_classes:
	${RECURSE_MAKE_RULE}

%.class: %.java
	${JAVAC_RULE}

ICONSWITCH=
ifneq ($(strip $(ICON)),)
ICONSWITCH = ${CSHARP_ICON_SWITCH}
endif

%.exe:
	$(CSHARP_EXE_RULE)

%.dll:
	$(CSHARP_DLL_RULE)

clean:
	${RECURSE_CLEAN_RULE}
	${DEL} *.class ${NULL}
	${DEL} *.bak ${NULL}
	${DEL} *~ ${NULL}
	${EXTRA_CLEAN}
	
# change directory to the root first. Some JUnit tests rely on this.
run_test:
	cd ${ROOTDIR} && java -classpath .${SEP}src${SEP}binaries/junit/junit.jar${SEP}binaries/antlr/antlr.jar \
	junit.textui.TestRunner test.Composestar.Suite

run_jdb:
	cd ${ROOTDIR} && jdb -classpath .${SEP}src${SEP}binaries/junit/junit.jar${SEP}binaries/antlr/antlr.jar \
	junit.textui.TestRunner test.Composestar.Suite

