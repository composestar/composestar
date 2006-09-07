#!/bin/sh

cd examples
# First get all project files:
for i in `find . -name "*proj"` 
do
	echo "Processing: $i"
	sed -e "s/HintPath = \".*AntlrDotNet.dll\"/HintPath = \"..\/..\/binaries\/AntlrDotNet.dll\"/g" < $i > $i.tmp
	sed -e "s/HintPath = \".*ComposestarCore.dll\"/HintPath = \"..\/..\/binaries\/ComposestarCore.dll\"/g" < $i.tmp > $i
	sed -e "s/HintPath = \".*ComposestarCoreDotNET.dll\"/HintPath = \"..\/..\/binaries\/ComposestarCoreDotNET.dll\"/g" < $i > $i.tmp
	sed -e "s/HintPath = \".*ComposeStarDotNETRuntimeInterpreter.dll\"/HintPath = \"..\/..\/binaries\/ComposeStarDotNETRuntimeInterpreter.dll\"/g" < $i.tmp > $i
	sed -e "s/HintPath = \".*ComposeStarDotNETUtilities.dll\"/HintPath = \"..\/..\/binaries\/ComposeStarDotNETUtilities.dll\"/g" < $i > $i.tmp
	sed -e "s/HintPath = \".*ComposeStarFilterDebugger.dll\"/HintPath = \"..\/..\/binaries\/ComposeStarFilterDebugger.dll\"/g" < $i.tmp > $i
	sed -e "s/HintPath = \".*ComposeStarRuntimeInterpreter.dll\"/HintPath = \"..\/..\/binaries\/ComposeStarRuntimeInterpreter.dll\"/g" < $i > $i.tmp
	sed -e "s/HintPath = \".*ComposeStarUtilities.dll\"/HintPath = \"..\/..\/binaries\/ComposeStarUtilities.dll\"/g" < $i.tmp > $i
	sed -e "s/HintPath = \".*DotNETPlatformInterface.dll\"/HintPath = \"..\/..\/DotNETPlatformInterface.dll\"/g" < $i > $i.tmp
	sed -e "s/HintPath = \".*ComposeStarReasoning.dll\"/HintPath = \"..\/..\/binaries\/ComposeStarReasoning.dll\"/g" < $i.tmp > $i
	rm -f $i.tmp
done

cd ..
