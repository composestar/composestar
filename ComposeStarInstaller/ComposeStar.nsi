; Pascal 

;--------------------------------
;Include Modern UI

  !include "MUI.nsh"

;--------------------------------

; Variables
;--------------------------------
  Var JAVA_HOME
	Var REAL_JAVA_HOME
  Var JAVA_VER
	Var REAL_JAVA_VER
  Var JAVA_INSTALLATION_MSG
	Var NET_SDK_PATH
	Var NET_RUN_PATH
	Var NET_VER
	Var INI_FILE
	Var UNIX_DIR
	Var VS_PATH
	Var KEY_FILE

;--------------------------------
;General

  ;Name and file
  Name "Compose* beta"
	Icon cstar.ico
  OutFile "ComposeStar_0.1b.exe"
	
	XPStyle "on"
	ShowInstDetails show

  ;Default installation folder
  InstallDir "$PROGRAMFILES\ComposeStar"
  
  ;Get installation folder from registry if available
  InstallDirRegKey HKCU "Software\ComposeStar" ""

;--------------------------------
;Interface Configuration

  !define MUI_HEADERIMAGE 
  !define MUI_HEADERIMAGE_BITMAP "cstar.bmp"
  !define MUI_ABORTWARNING

;--------------------------------
;Pages
	!insertmacro MUI_PAGE_WELCOME
  !insertmacro MUI_PAGE_LICENSE "ABOUT.txt"
  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY
  !insertmacro MUI_PAGE_INSTFILES
	;!insertmacro MUI_PAGE_FINISH
  
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  
;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section "Compose* beta" SecDummy

	Call LocateJVM
	
	Call IsDotNETInstalled
	
  SetOutPath "$INSTDIR"
  
  ;ADD YOUR OWN FILES HERE...
  File ABOUT.txt
	File /r compilers
	File /r binaries
	File /r documentation
	File /r ComposestarVSAddin
	File /nonfatal /r examples
	File ComposestarSyntaxHighlighting.reg
  
  ;Store installation folder
  ReadRegStr $NET_SDK_PATH HKLM "SOFTWARE\Microsoft\.NETFramework" "sdkInstallRootv1.1"
	StrCpy $NET_SDK_PATH "$NET_SDK_PATHBin"
	
	ReadRegStr $NET_RUN_PATH HKLM "SOFTWARE\Microsoft\.NETFramework" "InstallRoot"
	ReadRegStr $NET_VER HKLM "SOFTWARE\Microsoft\.NETFramework\policy\standards\v1.0.0" "v1.1.4322"
	StrCpy $NET_RUN_PATH "$NET_RUN_PATHv1.1.$NET_VER"
	DetailPrint "Found Microsoft .NET Framework: $NET_RUN_PATH"
	DetailPrint "Found Microsoft .NET SDK at: $NET_SDK_PATH"
	
	Call writeComposeStarINIFile
	Call writeRegistryKeys
	Call writeKeyWordFile
	
	
	ExecWait 'regasm /codebase "$INSTDIR\ComposestarVSAddin\ComposestarVSAddin.dll"'
	
	ExecWait 'regedit /s $INSTDIR\CompostarSyntaxHighlighting.reg'
	
	; Call website for the install times!
	;NSISdl::download http://flatliner.student.utwente.nl/composestar_install
	;Pop $R0 ;Get the return value
  ;StrCmp $R0 "success" +2
	;MessageBox MB_OK "Download failed: $R0"
	
  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"
	
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "Compose*" "$INSTDIR\Uninstall.exe"
	WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "Compose*" "$INSTDIR\Uninstall.exe"

SectionEnd

;--------------------------------
;Descriptions

  ;Language strings
  LangString DESC_SecDummy ${LANG_ENGLISH} "The Compose* package."

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${SecDummy} $(DESC_SecDummy)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END
 
;--------------------------------
;Uninstaller Section

Section "Uninstall"

  ExecWait '$NET_RUN_PATH/regasm /unregister "$INSTDIR\ComposestarVSAddin\ComposestarVSAddin.dll"'
	
	DeleteRegKey HKCU "SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect"
	DeleteRegKey HKLM "SOFTWARE\Microsoft\VisualStudio\7.1\Languages\File Extensions\.cps"
	
	;ADD YOUR OWN FILES HERE...
  RMDir /r "$INSTDIR"

;  ;DeleteRegKey /ifempty HKCU "Software\Software\ComposeStar"

SectionEnd

;--------------------------------
Function writeComposeStarINIFile
	
	Push "$INSTDIR\binaries\ComposeStarRuntimeInterpreter.dll"
  Push "\" ;needs to be replaced
  Push "/" ;will replace wrong characters
  Call StrReplace
  Pop $UNIX_DIR
	
	FileOpen  $INI_FILE "$INSTDIR\Composestar.ini" w
	FileWrite $INI_FILE '################################################################################$\n'
	FileWrite $INI_FILE '#Installer generated stuff:$\n$\n'
	FileWrite $INI_FILE 'ComposestarPath=$INSTDIR\$\n$\n'
	FileWrite $INI_FILE 'VerifyAssemblies=true$\n$\n'
	FileWrite $INI_FILE 'ClassPath=$INSTDIR\binaries\Composestar.jar;$INSTDIR\binaries\antlr.jar$\n$\n'
	FileWrite $INI_FILE 'DebugLevel=1$\n$\n'
	FileWrite $INI_FILE '.NETPath=$NET_RUN_PATH$\n$\n'
	FileWrite $INI_FILE '.NETSDKPath=$NET_SDK_PATH$\n$\n'
	FileWrite $INI_FILE 'MainClass=Composestar.CTCommon.Master.Master$\n$\n'
	FileWrite $INI_FILE 'RequiredDlls=ComposeStarRepository.dll,ComposeStarRuntimeInterpreter.dll,ComposeStarUtilities.dll$\n$\n'
	FileWrite $INI_FILE 'JSCompiler=$INSTDIR\compilers\msjsharp$\n'
	FileWrite $INI_FILE 'JSCompilerOptions=/debug /nologo /r:\"$UNIX_DIR\"$\n$\n'
	FileWrite $INI_FILE 'VBCompiler=$INSTDIR\compilers\msvbnet$\n'
	FileWrite $INI_FILE 'VBCompilerOptions=/debug /nologo /r:\"$UNIX_DIR\"$\n$\n'
	FileWrite $INI_FILE 'CSCompiler=$INSTDIR\compilers\mscsharp$\n'
	FileWrite $INI_FILE 'CSCompilerOptions=/debug /nologo /r:\"$UNIX_DIR\"$\n'
	FileWrite $INI_FILE '################################################################################$\n'
	FileClose $INI_FILE
FunctionEnd

Function writeRegistryKeys
	WriteRegStr HKCU "SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect" "CommandLineSafe" "0"
	WriteRegStr HKCU "SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect" "CommandPreload" "0"
	WriteRegStr HKCU "SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect" "Description" "Compose* VS AddIn"
	WriteRegStr HKCU "SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect" "FriendlyName" "Compose*"
	;WriteRegStr HKCU "SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect" "LoadBehavior" "0"
	WriteRegDWORD HKCU "SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect" "LoadBehavior" 3
	WriteRegStr HKCU "SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect" "ComposestarPath" "$INSTDIR\"
	
	;Set the cps file stuff!
	;WriteRegStr HKLM "SOFTWARE\Microsoft\VisualStudio\7.1\Languages\File Extensions\.cps" "@" "{B2F072B0-ABC1-11D0-9D62-00C04FD9DFD9}"
FunctionEnd

Function writeKeyWordFile
	ReadRegStr $VS_PATH HKLM "SOFTWARE\Microsoft\VisualStudio\7.1" "InstallDir"
	StrCpy $VS_PATH "$VS_PATH\usertype.dat"
	FileOpen  $KEY_FILE "$VS_PATH" a
	FileWrite $KEY_FILE 'concern$\n'
	FileWrite $KEY_FILE 'filtermodule$\n'
	FileWrite $KEY_FILE 'inputfilters$\n'
	FileWrite $KEY_FILE 'outputfilters$\n'
	FileWrite $KEY_FILE 'internals$\n'
	FileWrite $KEY_FILE 'externals$\n'
	FileWrite $KEY_FILE 'conditions$\n'
	FileWrite $KEY_FILE 'superimposition$\n'
	FileWrite $KEY_FILE 'selectors$\n'
	FileWrite $KEY_FILE 'methods$\n'
	FileWrite $KEY_FILE 'filtermodules$\n'
	FileWrite $KEY_FILE 'implementation$\n'
	FileWrite $KEY_FILE 'by$\n'
	FileWrite $KEY_FILE 'as$\n'
	FileWrite $KEY_FILE 'in$\n'
	FileWrite $KEY_FILE 'begin$\n'
	FileWrite $KEY_FILE 'end$\n'
	FileWrite $KEY_FILE 'True$\n'
	FileWrite $KEY_FILE 'False$\n'
	FileWrite $KEY_FILE 'Dispatch$\n'
	FileWrite $KEY_FILE 'Error$\n'
	FileWrite $KEY_FILE 'Meta$\n'
	FileWrite $KEY_FILE 'inner$\n'
	FileClose $KEY_FILE
FunctionEnd

;--------------------------------
Function LocateJVM
    ;Check for Java version and location
    Push $0
    Push $1
    
		ReadRegStr $JAVA_VER HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
		ReadRegStr $REAL_JAVA_VER HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
		;MessageBox MB_OK "Found Java: $JAVA_VER"
    StrCmp "$JAVA_VER" "" JavaNotPresent CheckJavaVer

    JavaNotPresent:
        StrCpy $JAVA_INSTALLATION_MSG "Java Runtime Environment is not installed on your computer. You need version 1.4 or newer to run this program."
				MessageBox MB_OK $JAVA_INSTALLATION_MSG
				DetailPrint $JAVA_INSTALLATION_MSG
				DetailPrint "Installation aborted!"
				Abort
				Quit

    CheckJavaVer:
        ReadRegStr $0 HKEY_LOCAL_MACHINE "SOFTWARE\JavaSoft\Java Runtime Environment\$JAVA_VER" "JavaHome"
        ReadRegStr $REAL_JAVA_HOME HKEY_LOCAL_MACHINE "SOFTWARE\JavaSoft\Java Runtime Environment\$JAVA_VER" "JavaHome"
        GetFullPathName /SHORT $JAVA_HOME "$0"
				StrCpy $0 $JAVA_VER 1 0
        StrCpy $1 $JAVA_VER 1 2
        StrCpy $JAVA_VER "$0$1"
				;MessageBox MB_OK "Found Java: $JAVA_VER"
        IntCmp 14 $JAVA_VER FoundCorrectJavaVer FoundCorrectJavaVer JavaVerNotCorrect
        
    FoundCorrectJavaVer:
        IfFileExists "$JAVA_HOME\bin\javaw.exe" 0 JavaNotPresent
				DetailPrint "Found Java Virtual Machine $REAL_JAVA_VER in $REAL_JAVA_HOME."
        ;MessageBox MB_OK "Found Java: $JAVA_VER at $JAVA_HOME"
        Goto Done
        
    JavaVerNotCorrect:
        StrCpy $JAVA_INSTALLATION_MSG "The version of Java Runtime Environment installed on your computer is $REAL_JAVA_VER. Version 1.4 or newer is required to run this program."
				MessageBox MB_OK $JAVA_INSTALLATION_MSG
				DetailPrint "The version of Java Runtime Environment installed on your computer is $REAL_JAVA_VER."
				DetailPrint "Version 1.4 or newer is required to run this program."
				DetailPrint "Installation aborted!"
				Abort
				Quit
        
    Done:
				Pop $1
        Pop $0
FunctionEnd

;--------------------------------
Function IsDotNETInstalled
   Push $0
   Push $1
   Push $2
   Push $3
   Push $4

   ReadRegStr $4 HKEY_LOCAL_MACHINE "Software\Microsoft\.NETFramework" "InstallRoot"
   # remove trailing back slash
   Push $4
   Exch $EXEDIR
   Exch $EXEDIR
   Pop $4
   # if the root directory doesn't exist .NET is not installed
   IfFileExists $4 0 noDotNET

   StrCpy $0 0

   EnumStart:

     EnumRegKey $2 HKEY_LOCAL_MACHINE "Software\Microsoft\.NETFramework\Policy"  $0
     IntOp $0 $0 + 1
     StrCmp $2 "" noDotNET

     StrCpy $1 0

     EnumPolicy:

       EnumRegValue $3 HKEY_LOCAL_MACHINE "Software\Microsoft\.NETFramework\Policy\$2" $1
       IntOp $1 $1 + 1
        StrCmp $3 "" EnumStart
         IfFileExists "$4\$2.$3" foundDotNET EnumPolicy

   noDotNET:
     StrCpy $0 0
		 StrCpy $JAVA_INSTALLATION_MSG "The Microsoft .NET Framework is not installed on your computer. You need version 1.1 or newer to run this program."
		 MessageBox MB_OK $JAVA_INSTALLATION_MSG
		 DetailPrint "The Microsoft .NET Framework is not installed on your computer."
		 DetailPrint "You need version 1.1 or newer to run this program."
		 DetailPrint "Installation aborted!"
		 Abort
		 Quit
     Goto done

   foundDotNET:
     StrCpy $0 1

   done:
     Pop $4
     Pop $3
     Pop $2
     Pop $1
     Exch $0
FunctionEnd

Function StrReplace
  Exch $0 ;this will replace wrong characters
  Exch
  Exch $1 ;needs to be replaced
  Exch
  Exch 2
  Exch $2 ;the orginal string
  Push $3 ;counter
  Push $4 ;temp character
  Push $5 ;temp string
  Push $6 ;length of string that need to be replaced
  Push $7 ;length of string that will replace
  Push $R0 ;tempstring
  Push $R1 ;tempstring
  Push $R2 ;tempstring
  StrCpy $3 "-1"
  StrCpy $5 ""
  StrLen $6 $1
  StrLen $7 $0
  Loop:
  IntOp $3 $3 + 1
  StrCpy $4 $2 $6 $3
  StrCmp $4 "" ExitLoop
  StrCmp $4 $1 Replace
  Goto Loop
  Replace:
  StrCpy $R0 $2 $3
  IntOp $R2 $3 + $6
  StrCpy $R1 $2 "" $R2
  StrCpy $2 $R0$0$R1
  IntOp $3 $3 + $7
  Goto Loop
  ExitLoop:
  StrCpy $0 $2
  Pop $R2
  Pop $R1
  Pop $R0
  Pop $7
  Pop $6
  Pop $5
  Pop $4
  Pop $3
  Pop $2
  Pop $1
  Exch $0
FunctionEnd
