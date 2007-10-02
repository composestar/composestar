:: Extract registry attributes as a WiX include file
@echo off

set VSSDK=C:\Program Files\Visual Studio 2005 SDK\2007.02\VisualStudioIntegration\Tools\Bin

"%VSSDK%\regpkg.exe" /codebase /root:Software\Microsoft\VisualStudio\8.0 /wixfile:.\StarLight.LanguageService.generated.wxi ..\..\Source\VSIntegration\VisualStudio.Project\bin\Release\Composestar.StarLight.VisualStudio.LanguageServices.dll
"%VSSDK%\regpkg.exe" /codebase /root:Software\Microsoft\VisualStudio\8.0 /wixfile:.\StarLightProject.generated.wxi ..\..\Source\VSIntegration\VisualStudio.Project\bin\Release\Composestar.StarLight.VisualStudio.Project.dll

