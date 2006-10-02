:: Build the binaries then extract their registry attributes as a WiX include file

MSBuild ..\..\Source\VSIntegration\VisualStudio.Project\StarLight.VisualStudio.Project.csproj /p:Configuration=Release
"D:\Development\Visual Studio 2005 SDK\2006.09\VisualStudioIntegration\Tools\Bin\regpkg.exe" /codebase /root:Software\Microsoft\VisualStudio\8.0 /wixfile:.\StarLight.LanguageService.generated.wxi ..\..\Source\VSIntegration\VisualStudio.Project\bin\Release\Composestar.StarLight.VisualStudio.LanguageServices.dll
"D:\Development\Visual Studio 2005 SDK\2006.09\VisualStudioIntegration\Tools\Bin\regpkg.exe" /codebase /root:Software\Microsoft\VisualStudio\8.0 /wixfile:.\StarLightProject.generated.wxi ..\..\Source\VSIntegration\VisualStudio.Project\bin\Release\Composestar.StarLight.VisualStudio.Project.dll

