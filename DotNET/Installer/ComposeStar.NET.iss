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

#define NAME "Compose*/.NET"
#define SAFE_NAME "ComposeStar.NET"

; if defined use the slow fade-in
#define SPLASH_FADEIN

; find the absolute path to regasm
#define FIND_REGASM

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
SetupIconFile=build\composestar.ico
Compression=lzma
SolidCompression=true
VersionInfoVersion={#VERSION}.{#BUILD}
VersionInfoCompany=University of Twente
VersionInfoDescription={#NAME} Setup
VersionInfoTextVersion={#VERSION}.{#BUILD}
PrivilegesRequired=poweruser
ShowLanguageDialog=auto
AppVersion={#VERSION}.{#BUILD}
UninstallDisplayIcon={app}\composestar.ico
MinVersion=4.1.2222,5.0.2195
AppID={{7ED194F6-701D-4A30-88FE-A87B3EF156B3}
WizardSmallImageFile=logo_small.bmp

[Languages]
Name: english; MessagesFile: compiler:Default.isl

[Components]
Name: core; Description: {#NAME}; Flags: fixed; Types: full compact custom
Name: core\addin; Description: VisualStudio AddIn; Flags: dontinheritcheck; Types: full compact
Name: docs; Description: Documentation; Types: full
Name: examples; Description: Examples; Types: full

[Files]
Source: src\*; DestDir: {app}; Flags: ignoreversion recursesubdirs; Excludes: .svn; Components: core
Source: build\*; DestDir: {app}; Flags: ignoreversion; Excludes: .svn; Components: core
Source: build\documentation\*; DestDir: {app}\documentation; Flags: ignoreversion recursesubdirs; Excludes: .svn; Components: docs
Source: build\bin\*; DestDir: {app}\bin; Flags: ignoreversion recursesubdirs; Excludes: .svn; Components: core;
Source: build\lib\*; DestDir: {app}\lib; Flags: ignoreversion recursesubdirs; Excludes: .svn; Components: core; AfterInstall: RegAsm
Source: build\ComposestarVSAddin\*; DestDir: {app}\ComposestarVSAddin; Flags: ignoreversion; Excludes: .svn; Components: core\addin; AfterInstall: RegAsm
Source: build\examplesDotNET\*; DestDir: {app}\examplesDotNET; Flags: ignoreversion recursesubdirs; Excludes: .svn,build.xml,TestCases; Components: examples

Source: VJSSupUILibSetup.msi; DestDir: {app}; Flags: ignoreversion deleteafterinstall
Source: kb891690.msp; DestDir: {app}; Flags: ignoreversion deleteafterinstall
Source: {#SPLASH_IMAGE}; DestDir: {tmp}; DestName: splash.bmp; Flags: ignoreversion dontcopy noencryption

[INI]
Filename: {app}\{#SAFE_NAME} Homepage.url; Section: InternetShortcut; Key: URL; String: http://composestar.sourceforge.net

[Icons]
Name: {group}\{cm:ProgramOnTheWeb,{#SAFE_NAME}}; Filename: {app}\{#SAFE_NAME} Homepage.url
Name: {group}\Annotated Reference Manual; Filename: {app}\documentation\ARM.pdf; WorkingDir: {app}\documentation\; Components: docs
Name: {group}\MessageAPI Documentation; Filename: {app}\documentation\MessageAPI\index.html; WorkingDir: {app}\documentation\; Components: docs
Name: {group}\{cm:UninstallProgram,{#SAFE_NAME}}; Filename: {uninstallexe}

[Run]
Filename: msiexec.exe; parameters: "/qb /i ""{app}\VJSSupUILibSetup.msi"""; Components: core; StatusMsg: Installing VisualJ# 1.1 UI supplemental; Flags: waituntilterminated; Check: IsAdminLoggedOn
Filename: msiexec.exe; parameters: "/qn /update ""{app}\kb891690.msp"""; Components: core; StatusMsg: Updating VisualJ# 1.1; Flags: waituntilterminated; Check: IsAdminLoggedOn

[UninstallDelete]
Type: files; Name: {app}\{#SAFE_NAME} Homepage.url

[Registry]
Root: HKCU; Subkey: SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect; ValueType: string; ValueName: CommandLineSafe; ValueData: 0; Flags: uninsdeletevalue uninsdeletekeyifempty
Root: HKCU; Subkey: SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect; ValueType: string; ValueName: CommandPreload; ValueData: 0; Flags: uninsdeletevalue uninsdeletekeyifempty
Root: HKCU; Subkey: SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect; ValueType: string; ValueName: Description; ValueData: Compose* VS AddIn; Flags: uninsdeletevalue uninsdeletekeyifempty
Root: HKCU; Subkey: SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect; ValueType: string; ValueName: FriendlyName; ValueData: Compose*; Flags: uninsdeletevalue uninsdeletekeyifempty
Root: HKCU; Subkey: SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect; ValueType: dword; ValueName: LoadBehavior; ValueData: 3; Flags: uninsdeletevalue uninsdeletekeyifempty
Root: HKCU; Subkey: SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect; ValueType: string; ValueName: ComposestarPath; ValueData: {app}; Flags: uninsdeletevalue uninsdeletekeyifempty
Root: HKCU; Subkey: SOFTWARE\Microsoft\VisualStudio\7.1\Languages\File Extensions\.cps; ValueData: {{B2F072B0-ABC1-11D0-9D62-00C04FD9DFD9}; ValueType: string; Flags: uninsdeletevalue uninsdeletekeyifempty

; only works for admins
Root: HKLM; Subkey: SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect; ValueType: string; ValueName: CommandLineSafe; ValueData: 0; Flags: uninsdeletevalue uninsdeletekeyifempty noerror
Root: HKLM; Subkey: SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect; ValueType: string; ValueName: CommandPreload; ValueData: 0; Flags: uninsdeletevalue uninsdeletekeyifempty noerror
Root: HKLM; Subkey: SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect; ValueType: string; ValueName: Description; ValueData: Compose* VS AddIn; Flags: uninsdeletevalue uninsdeletekeyifempty noerror
Root: HKLM; Subkey: SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect; ValueType: string; ValueName: FriendlyName; ValueData: Compose*; Flags: uninsdeletevalue uninsdeletekeyifempty noerror
Root: HKLM; Subkey: SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect; ValueType: dword; ValueName: LoadBehavior; ValueData: 3; Flags: uninsdeletevalue uninsdeletekeyifempty noerror
Root: HKLM; Subkey: SOFTWARE\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect; ValueType: string; ValueName: ComposestarPath; ValueData: {app}; Flags: uninsdeletevalue uninsdeletekeyifempty noerror
Root: HKLM; Subkey: SOFTWARE\Microsoft\VisualStudio\7.1\Languages\File Extensions\.cps; ValueData: {{B2F072B0-ABC1-11D0-9D62-00C04FD9DFD9}; ValueType: string; Flags: uninsdeletevalue uninsdeletekeyifempty noerror

; Add a key so assemblies can be seen in VS .NET
Root: HKLM; Subkey: "Software\Microsoft\.NETFramework\AssemblyFolders\{#SAFE_NAME}"; ValueType: string; ValueData: "{app}\lib"; Flags: deletekey uninsdeletekey noerror

[Code]
procedure kwadd(lst: TStringList; s: String);
var
  i: integer;
begin
  for i := 0 to lst.count-1 do begin
    if (CompareStr(lst[i], s) = 0) then exit;
  end;
  lst.add(s);
end;

var
  registeredAssemblies: TStringList;
  regasmexe: String; // full path to regasm.exe when found

procedure RegAsm();
begin
  if (CompareText(ExtractFileExt(CurrentFileName), '.dll') = 0) then begin
    kwadd(registeredAssemblies, CurrentFileName);
  end;
end;

procedure RealRegAsm(FileName: String);
var
  dummy: integer;
  assembly: string;
begin
  assembly := ExpandConstant(FileName);
  Log('RegAsm: '+regasmexe+' '+assembly);
  Exec(regasmexe, '"'+assembly+'" /codebase', '', SW_HIDE, ewWaitUntilTerminated, dummy);
end;

procedure UnRegAsm(FileName: String);
var
  dummy: integer;
  assembly: string;
begin
  assembly := ExpandConstant(FileName);
  Log('UnRegAsm: '+assembly);
  Exec('regasm', '"'+assembly+'" /unregister', '', SW_HIDE, ewWaitUntilTerminated, dummy);
end;

function GetCORSystemDirectory(pbuffer: PChar; cchBuffer: DWORD; var dwlength: DWORD): HWND;
  external 'GetCORSystemDirectory@mscoree stdcall';

function detectedDotNet(): Boolean;
var
  dnpath,tmp: string;
  pathlen: DWORD;
  i: integer;
begin
  Result := RegKeyExists(HKLM, 'SOFTWARE\Microsoft\.NETFramework\policy\v1.1');
  if Result = false then begin
    SuppressibleMsgBox('This setup requires the .NET Framework v1.1. Please download and install the .NET Framework and run this setup again.', mbError, MB_OK, IDOK);
  end
  else begin
    // find absolute location to regasm
    // although, if regasm isn't in the path the compile process won't work either (since csc or vjs are not in the path)
    #ifdef FIND_REGASM
    SetLength(dnpath, 255*2);
    GetCORSystemDirectory(PChar(dnpath), 255, pathlen);
    tmp := ''; // ugly hack to convert MBCS
    for i := 0 to pathlen-2 do begin
      tmp := tmp+dnpath[i*2+1];
    end;
    if (FileExists(tmp+'regasm.exe')) then begin
      regasmexe := tmp+'regasm.exe';
    end
    else begin
    #endif
      regasmexe := 'regasm.exe';
    #ifdef FIND_REGASM
    end;
    #endif
    Log('RegAsm = '+regasmexe);
  end;
end;

function detectedJava(): Boolean;
var
    version: String;
begin
  Result := RegQueryStringValue(HKLM, 'SOFTWARE\JavaSoft\Java Runtime Environment', 'CurrentVersion', version);
  if not Result then begin
    Result := RegQueryStringValue(HKLM, 'SOFTWARE\JavaSoft\Java Development Kit', 'CurrentVersion', version);
  end;
  
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
  registeredAssemblies := TStringList.Create;
  registeredAssemblies.Duplicates := dupIgnore;
  
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
  
  keylist: TStringList;
begin
  app := ExpandConstant('{app}\');
  app_r := ExpandConstant('{app}\');
  StringChangeEx(app_r, '\', '/', true);
  if LoadStringFromFile(ExpandConstant('{app}\Composestar.ini'), cfg) then begin
    StringChangeEx(cfg, '{app}', app, true);
    StringChangeEx(cfg, '{app_r}', app_r, true);
    SaveStringToFile(ExpandConstant('{app}\Composestar.ini'), cfg, false);
  end;
  //if LoadStringFromFile(ExpandConstant('{app}\PlatformConfigurations.xml'), cfg) then begin
  //  StringChangeEx(cfg, '{app}', app, true);
  //  StringChangeEx(cfg, '{app_r}', app_r, true);
  //  SaveStringToFile(ExpandConstant('{app}\PlatformConfigurations.xml'), cfg, false);
  //end;
  
  if (IsAdminLoggedOn() and IsComponentSelected('core\addin')) then begin
    if RegQueryStringValue(HKLM, 'SOFTWARE\Microsoft\VisualStudio\7.1', 'InstallDir', cfg) then
      keylist := TStringList.create();
      if (FileExists(cfg+'\usertype.dat')) then begin
        keylist.LoadFromFile(cfg+'\usertype.dat');
      end;

      kwadd(keylist, 'concern');
      kwadd(keylist, 'filtermodule');
      kwadd(keylist, 'inputfilters');
      kwadd(keylist, 'outputfilters');
      kwadd(keylist, 'internals');
      kwadd(keylist, 'externals');
      kwadd(keylist, 'conditions');
      kwadd(keylist, 'superimposition');
      kwadd(keylist, 'selectors');
      kwadd(keylist, 'filtermodules');
      kwadd(keylist, 'implementation');
      kwadd(keylist, 'by');
      kwadd(keylist, 'as');
      kwadd(keylist, 'in');

      kwadd(keylist, 'true');
      kwadd(keylist, 'false');
      kwadd(keylist, 'True');
      kwadd(keylist, 'False');
      kwadd(keylist, 'inner');

      kwadd(keylist, 'Dispatch');
      kwadd(keylist, 'Send');
      kwadd(keylist, 'Meta');
      kwadd(keylist, 'Error');
      kwadd(keylist, 'Prepend');
      kwadd(keylist, 'Append');

      kwadd(keylist, 'dispatch');
      kwadd(keylist, 'send');
      kwadd(keylist, 'meta');
      kwadd(keylist, 'error');
      kwadd(keylist, 'prepend');
      kwadd(keylist, 'append');

      kwadd(keylist, 'constraints');
      kwadd(keylist, 'pre');

      keylist.SaveToFile(cfg+'\usertype.dat');
      keylist.Free;
  end;
end;

procedure CurStepChanged(CurStep: TSetupStep);
var
  data: String;
  pg: TOutputProgressWizardPage;
  i: integer;
begin
  if (CurStep = ssInstall) then begin
    if RegQueryStringValue(HKEY_LOCAL_MACHINE, 'SOFTWARE\ComposeStar.NET', 'registeredAssemblies', data) then begin
      registeredAssemblies.commatext := data;
    end;
  end
  else if (CurStep = ssPostInstall) then begin
    pg := CreateOutputProgressPage('Registrating .NET Assemblies', 'Please wait while the .NET assemblies are being registraded to the system.');
    pg.show();
    try
      for i := 0 to registeredAssemblies.count-1 do begin
        pg.SetText('Registrating .NET Assemblies...', ExpandConstant(registeredAssemblies[i]));
        pg.SetProgress(i, registeredAssemblies.count-1);
        RealRegAsm(registeredAssemblies[i]);
      end;
    finally
      pg.hide();
    end;
  
    updateConfig();
  end;
end;

procedure RegisterPreviousData(PreviousDataKey: Integer);
begin
  try
    RegWriteStringValue(HKEY_LOCAL_MACHINE, 'SOFTWARE\ComposeStar.NET', 'registeredAssemblies', registeredAssemblies.commatext)
  except
  end;
end;

procedure CurUninstallStepChanged(CurUninstallStep: TUninstallStep);
var
  cleandir: boolean;
  dir: String;
  i: integer;
begin
  if (CurUninstallStep = usUninstall) then begin
    registeredAssemblies := TStringList.create;
    if RegQueryStringValue(HKEY_LOCAL_MACHINE, 'SOFTWARE\ComposeStar.NET', 'registeredAssemblies', dir) then begin
      registeredAssemblies.commatext := dir;
      for i := 0 to registeredAssemblies.count-1 do begin
        UnRegAsm(registeredAssemblies[i]);
      end;
      
      try
        RegDeleteValue(HKEY_LOCAL_MACHINE, 'SOFTWARE\ComposeStar.NET', 'registeredAssemblies');
        RegDeleteKeyIfEmpty(HKEY_LOCAL_MACHINE, 'SOFTWARE\ComposeStar.NET');
      except
      end;
    end;
  end
  else if (CurUninstallStep = usPostUninstall) then begin
    if (DirExists(ExpandConstant('{app}'))) then begin
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
end;
