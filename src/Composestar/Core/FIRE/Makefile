ROOTDIR=../../../../
include ${ROOTDIR}common.mk

SUBDIRS=Reporter \
	jargs

CLASSES=MatchSignature.class \
	MatchTrue.class \
	Action.class \
	MetaFilter.class \
	AntlrStdIn.class \
	Substitute.class \
	RepositoryPtrList.class \
	Column.class \
	Start.class \
	DispatchFilter.class \
	StateTable.class \
	EndOfFilter.class \
	ErrorFilter.class \
	StatusColumn.class \
	Filter.class \
	FilterComponent.class \
	FilterComposite.class \
	DepthFirstIterator.class \
	ChildIterator.class \
	Iterator.class \
	Symbol.class \
	FilterFactory.class \
	SymbolTable.class \
	FilterLeaf.class \
	FilterReasoningEngine.class \
	Tand.class \
	TandNot.class \
	Logic.class \
	Terminated.class \
	Main.class \
	Tnot.class \
	Tor.class \
	Match.class \
	TreeBuilder.class \
	MatchFalse.class \
	QueryState.class \
	ActionNode.class \
	VoidNode.class \
	FilterActionNode.class \
	SignatureActionNode.class \
	SubstituteActionNode.class \
	PathIterator.class \
	Node.class


all: compile

include ${ROOTDIR}default.mk

run:
	${RUN} Composestar.CTCommon.FIRE.Main  tests/DoubleBuffer.cps
#	${RUN} Composestar.CTCommon.FIRE.Main -t ${ROOTDIR}test/Composestar/CTCommon/FIRE/files/meta_signature_rename.antlr


# Extra cleaning rules.
EXTRA_CLEAN= ${DEL} TT_*.* ${NULL}
