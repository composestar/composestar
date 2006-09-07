; Pascal 

;--------------------------------
;Include Modern UI

  !include "MUI.nsh"
  !include "SetEnvVar.nsh"

;--------------------------------

; Variables
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
	Var RESULT
	Var StartMenuGroup
	Var Version

;--------------------------------
;General

  StrCpy $Version "0.6"

  ;Name and file
  Name "Compose* Java Edition"
	Icon cstar.ico
  OutFile "ComposeStar.Java_$Version.exe"
	
  XPStyle "on"
  ShowInstDetails show
  CRCCheck on

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
  !insertmacro MUI_PAGE_LICENSE "AboutComposeStar.Java.txt"
  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY
  !insertmacro MUI_PAGE_STARTMENU Application $StartMenuGroup
  !insertmacro MUI_PAGE_INSTFILES
 ;!insertmacro MUI_PAGE_FINISH
  
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  
;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Function .onInit
  SetOutPath $TEMP
  File /oname=spltmp.bmp "RembrandtNightwatch.bmp"

	advsplash::show 3000 600 400 -1 $TEMP\spltmp

  Pop $0

  Delete $TEMP\spltmp.bmp
FunctionEnd

Section "Checks" Checks
	
	Call LocateJVM
	
  	SetOutPath "$INSTDIR"
	
	DetailPrint "Found Java Virtual Machine $REAL_JAVA_VER in: $REAL_JAVA_HOME."
	
SectionEnd

Section "Compose* beta" Compose

	SetOutPath "$INSTDIR"
  
  	;ADD YOUR OWN FILES HERE...
  	File ABOUT.txt
	File filterdesc.xml
	File INCREconfig.xml
	File INCRE.css
	File /nonfatal secret.css
	File /nonfatal /r /x CVS compilers
	File cstar.ico
	File /nonfatal /r binaries
	File /nonfatal /r /x CVS documentation
	;File /nonfatal /r ComposestarEclipsePlugin
	File /nonfatal /r /x CVS examplesJava
  
  	;Call website for the install times!
	;NSISdl::download http://flatliner.student.utwente.nl/composestar_install
	;Pop $R0 ;Get the return value
  	;StrCmp $R0 "success" +2
	;MessageBox MB_OK "Download failed: $R0"
	
  	;Create uninstaller
  	WriteUninstaller "$INSTDIR\Uninstall.exe"

SectionEnd

Section "Settings" Settings
	
	Call writeComposeStarINIFile
	Call writeRegistryKeys
	
	; Set environment variables, for Java, .NET and .NET sdk!
	Push "PATH"
  	Push "%PATH%;$REAL_JAVA_HOME\bin"
  	Call WriteEnvStr

SectionEnd

;--------------------------------
;Descriptions

  	;Language strings
  	LangString DESC_Checks ${LANG_ENGLISH} "The checks for the Compose*/Java package, this will check your current system to see if it meets some of the requirements."
	LangString DESC_Settings ${LANG_ENGLISH} "The Compose*/Java package."
	LangString DESC_Compose ${LANG_ENGLISH} "The settings for the Compose*/Java package, it can must be used for the first install and can be used for rescue purposes."

  	;Assign language strings to sections
  	!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    	!insertmacro MUI_DESCRIPTION_TEXT ${Checks} $(DESC_Checks)
	!insertmacro MUI_DESCRIPTION_TEXT ${Compose} $(DESC_Settings)
	!insertmacro MUI_DESCRIPTION_TEXT ${Settings} $(DESC_Compose)
  	!insertmacro MUI_FUNCTION_DESCRIPTION_END
 
;--------------------------------
;Uninstaller Section

Section "Uninstall"

	;ADD YOUR OWN FILES HERE...
  	RMDir /r "$INSTDIR"
	
OK:
	;DeleteRegKey /ifempty HKCU "Software\Software\ComposeStar"

SectionEnd

;--------------------------------
Function writeComposeStarINIFile
	
	FileOpen  $INI_FILE "$INSTDIR\Composestar.ini" w
	FileWrite $INI_FILE '################################################################################$\n'
	FileWrite $INI_FILE '#Installer generated stuff:$\n$\n'
	FileWrite $INI_FILE '[Global Composestar configuration]$\n'
	FileWrite $INI_FILE 'ComposestarPath=$INSTDIR\$\n$\n'
	FileWrite $INI_FILE 'ClassPath=$INSTDIR\binaries\ComposestarCORE.jar;$INSTDIR\binaries\ComposestarJava.jar;$INSTDIR\binaries\antlr.jar;$INSTDIR\binaries\prolog\prolog.jar$\n$\n'
	;FileWrite $INI_FILE 'JDKPath=$JAVADK_PATH$\n$\n'
	FileWrite $INI_FILE 'SECRETMode=2$\n$\n'
	FileWrite $INI_FILE 'MainClass=Composestar.Java.MASTER.JavaMaster$\n$\n'
	FileWrite $INI_FILE 'EmbeddedSourcesFolder=embedded\$\n$\n'
	FileWrite $INI_FILE 'RequiredJars=ComposestarCORE.jar,ComposestarJava.jar,ComposestarRuntimeJava.jar,ComposestarRuntimeCore.jar$\n$\n'
	FileWrite $INI_FILE '[Common]$\n'
	FileWrite $INI_FILE 'RunDebugLevel=1$\n'
	FileWrite $INI_FILE 'BuildDebugLevel=1$\n'
	FileWrite $INI_FILE 'VerifyAssemblies=true$\n'
	FileWrite $INI_FILE '################################################################################$\n'
	FileClose $INI_FILE
FunctionEnd

Function writeRegistryKeys
	WriteRegStr HKCU "SOFTWARE\ComposeStar\Java" "ComposestarPath" "$INSTDIR\"
	
	;Set the cps file stuff!
	;WriteRegStr HKLM "SOFTWARE\Microsoft\VisualStudio\7.1\Languages\File Extensions\.cps" "@" "{B2F072B0-ABC1-11D0-9D62-00C04FD9DFD9}"
	
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\composestar" "DisplayIcon" "$INSTDIR\cstar.ico"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\composestar" "DisplayName" "Compose*"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\composestar" "UninstallString" "$INSTDIR\Uninstall.exe"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\composestar" "Publisher" "University of Twente"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\composestar" "URLUpdateInfo" "http://composestar.sourceforge.net"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\composestar" "HelpLink" "http://composestar.sourceforge.net"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\composestar" "URLInfoAbout" "http://composestar.sourceforge.net"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\composestar" "DisplayVersion" "$Version"
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
    
	ReadRegStr $JAVA_VER HKLM "SOFTWARE\JavaSoft\Java Development Kit" "CurrentVersion"
	ReadRegStr $REAL_JAVA_VER HKLM "SOFTWARE\JavaSoft\Java Development Kit" "CurrentVersion"
	;MessageBox MB_OK "Found Java: $JAVA_VER"
    	StrCmp "$JAVA_VER" "" JavaNotPresent CheckJavaVer

    	JavaNotPresent:
        StrCpy $JAVA_INSTALLATION_MSG "Java Development Kit is not installed on your computer. You need version 1.4 or newer to run this program."
	MessageBox MB_OK $JAVA_INSTALLATION_MSG
	DetailPrint $JAVA_INSTALLATION_MSG
	DetailPrint "Installation aborted!"
	Abort
	Quit

    	CheckJavaVer:
       	ReadRegStr $0 HKEY_LOCAL_MACHINE "SOFTWARE\JavaSoft\Java Development Kit\$JAVA_VER" "JavaHome"
       	ReadRegStr $REAL_JAVA_HOME HKEY_LOCAL_MACHINE "SOFTWARE\JavaSoft\Java Development Kit\$JAVA_VER" "JavaHome"
       	GetFullPathName /SHORT $JAVA_HOME "$0"
	StrCpy $0 $JAVA_VER 1 0
        StrCpy $1 $JAVA_VER 1 2
        StrCpy $JAVA_VER "$0$1"
	;MessageBox MB_OK "Found Java: $JAVA_VER"
        IntCmp 14 $JAVA_VER FoundCorrectJavaVer FoundCorrectJavaVer JavaVerNotCorrect
        
    FoundCorrectJavaVer:
        IfFileExists "$JAVA_HOME\bin\javaw.exe" 0 JavaNotPresent
	;MessageBox MB_OK "Found Java: $JAVA_VER at $JAVA_HOME"
        Goto Done
        
    JavaVerNotCorrect:
        StrCpy $JAVA_INSTALLATION_MSG "The version of Java Development Kit installed on your computer is $REAL_JAVA_VER. Version 1.4 or newer is required to run this program."
	MessageBox MB_OK $JAVA_INSTALLATION_MSG
	DetailPrint "The version of Java Development Kit installed on your computer is $REAL_JAVA_VER."
	DetailPrint "Version 1.4 or newer is required to run this program."
	DetailPrint "Installation aborted!"
	Abort
	Quit
        
    Done:
	Pop $1
        Pop $0
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

; Author: Lilla (lilla@earthlink.net) 2003-06-13
; function IsUserAdmin uses plugin \NSIS\PlusgIns\UserInfo.dll
; This function is based upon code in \NSIS\Contrib\UserInfo\UserInfo.nsi
; This function was tested under NSIS 2 beta 4 (latest CVS as of this writing).
;
; Usage:
;   Call IsUserAdmin
;   Pop $R0   ; at this point $R0 is "true" or "false"
;
Function IsUserAdmin
Push $R0
Push $R1
Push $R2

ClearErrors
UserInfo::GetName
IfErrors Win9x
Pop $R1
UserInfo::GetAccountType
Pop $R2

StrCmp $R2 "Admin" 0 Continue1
;DetailPrint 'User "$R1" is in the Administrators group'
StrCpy $R0 "true"
Goto Check

Continue1:
StrCmp $R2 "Power" 0 Continue
;etailPrint 'User "$R1" is in the Administrators group'
StrCpy $R0 "true"
Goto Check

Continue:
StrCmp $R2 "" Win9x
StrCpy $R0 "false"
;DetailPrint 'User "$R1" is in the "$R2" group'
Goto Check

Win9x:
StrCpy $R0 "true"

Check:
StrCmp $R0 "true" Done Failure

Failure:
	;DetailPrint 'User= "$R1"  AccountType= "$R2"  IsUserAdmin= "$R0"'
	MessageBox MB_OK "You should be a member of the Administrators group!"
	Abort
	Quit

Done:

;DetailPrint 'User= "$R1"  AccountType= "$R2"  IsUserAdmin= "$R0"'

Pop $R2
Pop $R1
Pop $R0

FunctionEnd

