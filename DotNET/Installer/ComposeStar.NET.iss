; $Id$
; Needs Inno Setup QuickStart Pack 5

; these will be set on the commandline by ant
#ifndef VERSION
  #define VERSION "0.0.0"
#endif
#ifndef BUILD
  #define BUILD "0"
#endif

; image to use for the splash screen
#define SPLASH_IMAGE "EschersRelativity.bmp"

#define NAME "Compose*.NET"
#define SAFE_NAME "ComposeStar.NET"

; if defined use the slow fade-in
#define SPLASH_FADEIN

[Setup]
AppName={#NAME}
AppVerName={#NAME} {#VERSION}
AppPublisher=University of Twente
AppPublisherURL=http://composestar.sourceforge.net
AppSupportURL=http://composestar.sourceforge.net
AppUpdatesURL=http://composestar.sourceforge.net
DefaultDirName={pf}\ComposeStar
DefaultGroupName=ComposeStar
AllowNoIcons=true
InfoBeforeFile=AboutComposeStar.NET.txt
OutputDir=dist
OutputBaseFilename=ComposeStar.NET_{#VERSION}
SetupIconFile=src\cstar.ico
Compression=lzma
SolidCompression=true
VersionInfoVersion={#VERSION}.{#BUILD}
VersionInfoCompany=University of Twente
VersionInfoDescription={#NAME} Setup
VersionInfoTextVersion={#VERSION}.{#BUILD}
PrivilegesRequired=poweruser
ShowLanguageDialog=auto
AppVersion={#VERSION}.{#BUILD}
UninstallDisplayIcon={app}\cstar.ico
MinVersion=4.1.2222,5.0.2195
AppID={{7ED194F6-701D-4A30-88FE-A87B3EF156B3}
WizardSmallImageFile=logo_small.bmp

[Languages]
Name: english; MessagesFile: compiler:Default.isl

[Components]
Name: core; Description: {#NAME}; Flags: fixed; Types: full compact custom
Name: core\addin; Description: VisualStudio AddIn; Flags: dontinheritcheck; Types: full compact
Name: examples; Description: Examples; Types: full

[Files]
Source: src\*; DestDir: {app}; Flags: ignoreversion recursesubdirs createallsubdirs; Excludes: .svn; Components: core
Source: build\*; DestDir: {app}; Flags: ignoreversion regserver regtypelib noregerror; Excludes: .svn; Components: core
Source: build\binaries\*; DestDir: {app}\binaries; Flags: ignoreversion recursesubdirs createallsubdirs regserver regtypelib noregerror; Excludes: .svn; Components: core
Source: build\ComposestarVSAddin\*; DestDir: {app}\ComposestarVSAddin; Flags: ignoreversion regserver regtypelib noregerror; Excludes: .svn; Components: core\addin
Source: build\examplesDotNET\*; DestDir: {app}\examplesDotNET; Flags: ignoreversion recursesubdirs createallsubdirs; Excludes: .svn,build.xml; Components: examples

Source: vjssupuilib.exe; DestDir: {app}; Flags: ignoreversion deleteafterinstall
Source: {#SPLASH_IMAGE}; DestDir: {tmp}; DestName: splash.bmp; Flags: ignoreversion dontcopy noencryption

[INI]
Filename: {app}\{#SAFE_NAME} Homepage.url; Section: InternetShortcut; Key: URL; String: http://composestar.sourceforge.net

[Icons]
Name: {group}\{cm:ProgramOnTheWeb,{#SAFE_NAME}}; Filename: {app}\{#SAFE_NAME} Homepage.url
Name: {group}\Documentation; Filename: {app}\documentation\MessageAPI\index.html; WorkingDir: {app}\documentation\
Name: {group}\{cm:UninstallProgram,{#SAFE_NAME}}; Filename: {uninstallexe}

[Run]
Filename: {app}\vjssupuilib.exe; parameters: "/C:""installer /qb"""; Components: core; StatusMsg: Installing J# UI supplemental; Flags: waituntilterminated

[UninstallDelete]
Type: files; Name: {app}\{#SAFE_NAME} Homepage.url

[Registry]
Root: HKCU; Subkey: SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect; ValueType: string; ValueName: CommandLineSafe; ValueData: 0; Flags: uninsdeletevalue uninsdeletekeyifempty
Root: HKCU; Subkey: SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect; ValueType: string; ValueName: CommandPreload; ValueData: 0; Flags: uninsdeletevalue uninsdeletekeyifempty
Root: HKCU; Subkey: SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect; ValueType: string; ValueName: Description; ValueData: Compose* VS AddIn; Flags: uninsdeletevalue uninsdeletekeyifempty
Root: HKCU; Subkey: SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect; ValueType: string; ValueName: FriendlyName; ValueData: Compose*; Flags: uninsdeletevalue uninsdeletekeyifempty
Root: HKCU; Subkey: SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect; ValueType: dword; ValueName: LoadBehavior; ValueData: 3; Flags: uninsdeletevalue uninsdeletekeyifempty
Root: HKCU; Subkey: SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect; ValueType: string; ValueName: ComposestarPath; ValueData: {app}; Flags: uninsdeletevalue uninsdeletekeyifempty
Root: HKCU; Subkey: SOFTWARE\Microsoft\VisualStudio\7.1\Languages\File Extensions\.cps; ValueData: {{B2F072B0-ABC1-11D0-9D62-00C04FD9DFD9}}; ValueType: string; Flags: uninsdeletevalue uninsdeletekeyifempty

[Code]
function detectedDotNet(): Boolean;
begin
  Result := RegKeyExists(HKLM, 'SOFTWARE\Microsoft\.NETFramework\policy\v1.1');
  if Result = false then begin
    SuppressibleMsgBox('This setup requires the .NET Framework v1.1. Please download and install the .NET Framework and run this setup again.', mbError, MB_OK, IDOK);
  end;
end;

function detectedJava(): Boolean;
var
    version: String;
begin
  Result := RegQueryStringValue(HKLM, 'SOFTWARE\JavaSoft\Java Runtime Environment', 'CurrentVersion', version);
  if Result then begin
    Result := CompareText(version, '1.4') >= 0;
  end;

  if not Result then begin
    SuppressibleMsgBox('This setup requires the Java Runtime Environment version 1.4 or higher. Please download and install the Java Runtime Environment and run this setup again.', mbError, MB_OK, IDOK);
  end;
end;

#ifdef SPLASH_FADEIN
function SetLayeredWindowAttributes(hWnd: HWND; crKey: HWND; bAlpha: Byte; dwFlags: LongWord): LongBool;
  external 'SetLayeredWindowAttributes@user32 stdcall';
  
function SetWindowLong(hWnd: HWND; v1: integer; v2: LongWord):LongWord;
  external 'SetWindowLongA@user32 stdcall';
function GetWindowLong(hWnd: HWND; v1: integer):LongWord;
  external 'GetWindowLongA@user32 stdcall';
#endif

procedure splashScreen();
var
  frm: TForm;
  img: TBitmapImage;
  sfile: String;
  i: byte;
begin
  frm := TForm.create(nil);
  with frm do begin
    BorderStyle := bsNone;
    Position := poScreenCenter;

    #ifdef SPLASH_FADEIN
    SetWindowLong(Handle, -20, GetWindowLong(Handle, -20) or $80000);
    SetLayeredWindowAttributes(Handle, 0, 0, 2);
    #endif
  end;

  sfile := ExpandConstant('{tmp}\splash.bmp');

  img := TBitmapImage.Create(frm);
  with img do begin
    ExtractTemporaryFile(ExtractFileName(sfile));
    Bitmap.LoadFromFile(sfile);
    Parent := frm;
    AutoSize := true;
  end;

  frm.ClientWidth := img.width;
  frm.ClientHeight := img.height;

  with frm do begin
    Show();
    Repaint();
    #ifdef SPLASH_FADEIN
    for i := 1 to 31 do begin
      SetLayeredWindowAttributes(Handle, 0, i*8, 2);
      sleep(5);
    end;
    sleep(1000);
    #else
    sleep(2000);
    #endif
    Close;
    Free;
  end;
end;

function InitializeSetup(): Boolean;
begin
  result := detectedDotNet() and detectedJava();
  if (result and not WizardSilent()) then begin
    splashScreen();
  end;
end;

procedure updateConfig();
var
  cfg: String;
  app: String;
  app_r: String;
begin
  app := ExpandConstant('{app}\');
  app_r := ExpandConstant('{app}\');
  StringChangeEx(app_r, '\', '/', true);
  if LoadStringFromFile(ExpandConstant('{app}\Composestar.ini'), cfg) then begin
    StringChangeEx(cfg, '{app}', app, true);
    StringChangeEx(cfg, '{app_r}', app_r, true);
    SaveStringToFile(ExpandConstant('{app}\Composestar.ini'), cfg, false);
  end;
  if LoadStringFromFile(ExpandConstant('{app}\PlatformConfigurations.xml'), cfg) then begin
    StringChangeEx(cfg, '{app}', app, true);
    StringChangeEx(cfg, '{app_r}', app_r, true);
    SaveStringToFile(ExpandConstant('{app}\PlatformConfigurations.xml'), cfg, false);
  end;
end;

procedure CurStepChanged(CurStep: TSetupStep);
begin
  if (CurStep = ssPostInstall) then updateConfig();
end;

procedure CurUninstallStepChanged(CurUninstallStep: TUninstallStep);
var
  cleandir: boolean;
  dir: String;
begin
  if (CurUninstallStep = usPostUninstall) then begin
    if (UninstallSilent()) then begin
      cleandir := true;
    end
    else begin
      cleandir := SuppressibleMsgBox('Do you want to clean up the installation directory and delete all left over files?', mbConfirmation, MB_YESNO, IDYES) = IDYES;
    end;
    if (cleandir) then begin
      dir := ExpandConstant('{app}');
      DelTree(dir, true, true, true);
    end;
  end;
end;
