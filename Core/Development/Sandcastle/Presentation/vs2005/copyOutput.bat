if not exist Output mkdir Output
if not exist Output\html mkdir Output\html
if not exist Output\icons mkdir Output\icons
if not exist Output\scripts mkdir Output\scripts
if not exist Output\styles mkdir Output\styles
copy ..\..\Presentation\vs2005\icons\* Output\icons
copy ..\..\Presentation\vs2005\scripts\* Output\scripts
copy ..\..\Presentation\vs2005\styles\* Output\styles
if not exist Intellisense mkdir Intellisense

