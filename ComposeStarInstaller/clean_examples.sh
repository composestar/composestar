#!/bin/sh

cd examples
# First get all project files:
for i in `find . -name "*proj"` 
do
	echo "Processing: $i"
	sed -e "s/HintPath = \".*ComposeStarRuntimeInterpreter.dll\"/HintPath = \"..\/..\/binaries\/ComposeStarRuntimeInterpreter.dll\"/g" < $i > $i.tmp
	sed -e "s/HintPath = \".*ComposeStarReasoning.dll\"/HintPath = \"..\/..\/binaries\/ComposeStarReasoning.dll\"/g" < $i.tmp > $i
	rm -f $i.tmp
done

cd ..
