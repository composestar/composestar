; Pascal 

;--------------------------------
;Include Modern UI

  !include "MUI.nsh"

;--------------------------------

; Variables
;--------------------------------
  Var JAVA_INSTALLATION_MSG
	Var RESULT

;--------------------------------
;General

  ;Name and file
  Name "Compose* Add In Fixer"
	OutFile "ComposeStarAddIn.exe"
	
	XPStyle "on"
	ShowInstDetails show

  ;Default installation folder
  InstallDir "$PROGRAMFILES\ComposeStar"

;--------------------------------
;Interface Configuration

  !define MUI_HEADERIMAGE 
  !define MUI_HEADERIMAGE_BITMAP "cstar.bmp"
  !define MUI_ABORTWARNING

;--------------------------------
;Pages
  !insertmacro MUI_PAGE_WELCOME
  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY
  !insertmacro MUI_PAGE_INSTFILES
	!insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section "Compose* AddIn Fixer" Fix

	SetOutPath "$INSTDIR"
  
	Call writeRegistryKeys
	
	ExecWait 'regasm /codebase "$INSTDIR\ComposestarVSAddin\ComposestarVSAddin.dll"' $RESULT
	IntCmp 0 $RESULT OK
	StrCpy $JAVA_INSTALLATION_MSG "Could not register the Compose* Visual Studio AddIn!"
	MessageBox MB_OK $JAVA_INSTALLATION_MSG
	OK:

SectionEnd

;--------------------------------
;Descriptions

  ;Language strings
  LangString DESC_FIX ${LANG_ENGLISH} "Sets the registry back to the original position and registers the plugin again."
	
  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
  	!insertmacro MUI_DESCRIPTION_TEXT ${Fix} $(DESC_FIX)
	!insertmacro MUI_FUNCTION_DESCRIPTION_END

Function writeRegistryKeys
	WriteRegStr   HKCU "SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect" "CommandLineSafe" "0"
	WriteRegStr   HKCU "SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect" "CommandPreload" "0"
	WriteRegStr   HKCU "SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect" "Description" "Compose* VS AddIn"
	WriteRegStr   HKCU "SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect" "FriendlyName" "Compose*"
	WriteRegDWORD HKCU "SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect" "LoadBehavior" 3
	WriteRegStr   HKCU "SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect" "ComposestarPath" "$INSTDIR\"
	
	;Set the cps file stuff!
	;WriteRegStr HKLM "SOFTWARE\Microsoft\VisualStudio\7.1\Languages\File Extensions\.cps" "@" "{B2F072B0-ABC1-11D0-9D62-00C04FD9DFD9}"
FunctionEnd

