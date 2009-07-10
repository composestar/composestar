#ifndef StarlightVersion
#define StarlightVersion "0.0.1"
#endif

; The GUID of the VS2005 Integration package
#define VSIntegrationGUID "{9d31cb73-40a7-4dcc-8c01-bbfbbb66001c}"

#include "isxdl\isxdl.iss"

[Setup]
AppName=StarLight
AppVerName=StarLight {#StarlightVersion}
VersionInfoCompany=Software Engineering Group; University of Twente
VersionInfoCopyright=(C) 2009 Software Engineering Group; University of Twente
VersionInfoDescription=Setup bootstrapper for StarLight
VersionInfoProductName=StarLight
VersionInfoProductVersion={#StarlightVersion}
VersionInfoVersion=1.0.0.0
OutputBaseFilename=Setup
OutputDir=dist
DefaultDirName=
CreateAppDir=no
Uninstallable=no
DisableDirPage=yes
DisableFinishedPage=yes
DisableProgramGroupPage=yes
DisableReadyMemo=no
DisableReadyPage=no
DisableStartupPrompt=yes
WizardImageFile=WizardStarlight.bmp
WizardSmallImageFile=WizardStarlightSmall.bmp

[Messages]
BeveledLabel=http://composestar.sourceforge.net
WelcomeLabel2=This will install [name/ver] and all its dependencies on your computer.%n%nIt is recommended that you close all other applications before continuing.
ClickNext=Click Next/Install to continue, or Cancel to exit Setup.

[Files]
Source: "ProjectAggregator2.msi"; DestDir: "{tmp}"; Flags: dontcopy external;
Source: "StarLightSetup_{#StarlightVersion}.msi"; DestDir: "{tmp}"; Flags: dontcopy external;

[Run]
; external deps
Filename: "{ini:{tmp}{\}dep.ini,install,msi31}"; Description: "{cm:msi31_title}"; StatusMsg: "{cm:depinstall_status,{cm:msi31_title}}"; Parameters: "/quiet /norestart"; Flags: skipifdoesntexist
Filename: "{ini:{tmp}{\}dep.ini,install,dotnetfx35sp1}"; Description: "{cm:dotnetfx35sp1_title}"; StatusMsg: "{cm:depinstall_status,{cm:dotnetfx35sp1_title}}"; Parameters: "/lang:enu /quiet /norestart"; Flags: skipifdoesntexist
Filename: "{ini:{tmp}{\}dep.ini,install,jre6}"; Description: "{cm:jre6_title}"; StatusMsg: "{cm:depinstall_status,{cm:jre6_title}}"; Parameters: "/s /quiet /norestart"; Flags: skipifdoesntexist

; the rest
Filename: "msiexec"; Parameters: "/i ""{src}\StarLightSetup_{#StarlightVersion}.msi"""; StatusMsg: "Launching StarLight Setup..."; Flags: skipifsilent hidewizard; Check: JREinstalled(true)
; for silent install
Filename: "msiexec"; Parameters: "/quiet /i ""{src}\StarLightSetup_{#StarlightVersion}.msi"""; Flags: skipifnotsilent;
Filename: "msiexec"; Parameters: "/quiet /i ""{src}\ProjectAggregator2.msi"""; StatusMsg: "Installing ProjectAggregator2..."; Flags: runhidden; Check: NeedProjectAggregator2


[CustomMessages]
install_starlight=Install StarLight {#StarlightVersion}
install_label=&Install
NoJREFatal=Java Runtime Environment is not available. Setup will abort.

DependenciesDir=MyProgramDependencies
depdownload_title=Download dependencies
depinstall_title=Install dependencies
depinstall_status=Installing %1... (May take a few minutes)
depdownload_msg=The following applications are required before setup can continue:%n%1%nDownload and install now?
depinstall_missing=%1 must be installed before setup can continue. Please install %1 and run Setup again.

dotnetfx35sp1_title=.NET Framework 3.5 Service Pack 1
dotnetfx35sp1_size=3 MB - 232 MB
msi31_title=Windows Installer 3.1
msi31_size=2.5 MB
jre6_title=Java Runtime Environment 6
jre6_size=10 MB

[Code]	
// Based on http://www.codeproject.com/KB/install/dotnetfx_innosetup_instal.aspx
const
	dotnetfx35sp1_url = 'http://download.microsoft.com/download/0/6/1/061f001c-8752-4600-a198-53214c69b51f/dotnetfx35setup.exe';
	msi31_url = 'http://download.microsoft.com/download/1/4/7/147ded26-931c-4daf-9095-ec7baf996f46/WindowsInstaller-KB893803-v2-x86.exe';
	jre6_url = 'http://javadl.sun.com/webapps/download/AutoDL?BundleId=32267';
	
var
	installMemo, downloadMemo, downloadMessage: string;
	missingDeps: boolean;
	dotNetInstalled: boolean;

procedure InstallPackage(PackageName, FileName, Title, Size, URL: string);
var
	path: string;
begin
  missingDeps := true;
	installMemo := installMemo + '%1' + Title + #13;
	path := ExpandConstant('{src}{\}') + CustomMessage('DependenciesDir') + '\' + FileName;
	if not FileExists(path) then begin
		path := ExpandConstant('{tmp}{\}') + FileName;
		
		if not FileExists(path) then begin
			downloadMemo := downloadMemo + '%1' + Title + #13;
			downloadMessage := downloadMessage + Title + ' (' + Size + ')' + #13;
			
			isxdl_AddFile(URL, path);
		end;
	end;
	SetIniString('install', PackageName, path, ExpandConstant('{tmp}{\}dep.ini'));
end;

function NextButtonClick(CurPage: Integer): Boolean;
begin
	Result := true;
	if CurPage = wpReady then begin
		if missingDeps then begin
      if isxdl_DownloadFiles(StrToInt(ExpandConstant('{wizardhwnd}'))) = 0 then begin
				Result := false;
			end;
		end;
	end;
end;

function ShouldSkipPage(PageID: Integer): Boolean;
begin
  Result := false;
  if (PageID = wpReady) and not missingDeps then Result := true;
end;

function UpdateReadyMemo(Space, NewLine, MemoUserInfoInfo, MemoDirInfo, MemoTypeInfo, MemoComponentsInfo, MemoGroupInfo, MemoTasksInfo: String): String;
var
	s: string;
begin
	if downloadMemo <> '' then begin
		s := s + CustomMessage('depdownload_title') + ':' + NewLine + FmtMessage(downloadMemo, [Space]) + NewLine;
	end;
	if installMemo <> '' then begin
		s := s + CustomMessage('depinstall_title') + ':' + NewLine + FmtMessage(installMemo, [Space]) + NewLine;
	end;

	Result := s + CustomMessage('install_starlight');
end;

function GetFullVersion(VersionMS, VersionLS: cardinal): string;
var
	version: string;
begin
	version := IntToStr(word(VersionMS shr 16));
	version := version + '.' + IntToStr(word(VersionMS and not $ffff0000));
	version := version + '.' + IntToStr(word(VersionLS shr 16));
	version := version + '.' + IntToStr(word(VersionLS and not $ffff0000));
	Result := version;
end;

function fileversion(file: string): string;
var
	versionMS, versionLS: cardinal;
begin
	if (GetVersionNumbers(file, versionMS, versionLS)) then begin
		Result := GetFullVersion(versionMS, versionLS);
	end else
		Result := '0';
end;

procedure checkMSI31();
begin
	if (fileversion(ExpandConstant('{sys}{\}msi.dll')) < '3.1') then begin
		InstallPackage('msi31', 'msi31.exe', CustomMessage('msi31_title'), CustomMessage('msi31_size'), msi31_url);
	end;
end;

procedure checkDotNet();
begin
	if not (RegKeyExists(HKLM, 'Software\Microsoft\NET Framework Setup\NDP\v2.0.50727')
    or RegKeyExists(HKLM, 'Software\Microsoft\NET Framework Setup\NDP\v3.0')
    or RegKeyExists(HKLM, 'Software\Microsoft\NET Framework Setup\NDP\v3.5')) then begin
		InstallPackage('dotnetfx35sp1', 'dotnetfx35sp1.exe', CustomMessage('dotnetfx35sp1_title'), CustomMessage('dotnetfx35sp1_size'), dotnetfx35sp1_url);
		dotNetInstalled := true;
	end;
end;

function JREinstalled(showError: boolean): boolean;
var
	version: string;
begin
  RegQueryStringValue(HKLM, 'SOFTWARE\JavaSoft\Java Runtime Environment', 'CurrentVersion', version);
	Result := version >= '1.5';
	if not result and showError then SuppressibleMsgBox(CustomMessage('NoJREFatal'), mbError, MB_OK, MB_OK);
end;

procedure checkJRE();
begin
	if not JREinstalled(false) then begin
		InstallPackage('jre6', 'jre6.exe', CustomMessage('jre6_title'), CustomMessage('jre6_size'), jre6_url);
	end;
end;

function NeedProjectAggregator2(): Boolean;
begin
  // Check if VSIntegration component was installed
  result := RegKeyExists(HKLM, 'SOFTWARE\Microsoft\VisualStudio\8.0\Packages\{#VSIntegrationGUID}');
end;

function InitializeSetup(): Boolean;
begin	
  missingDeps := false;
  checkMSI31();
  checkDotNet();
  checkJRE();
	result := true
end;

procedure CurPageChanged(CurPageID: Integer);
begin
  if (CurPageID = wpWelcome) and (not missingDeps) then begin
    WizardForm.NextButton.Caption := CustomMessage('install_label');
  end;
end;

function NeedRestart(): Boolean;
begin
  Result := dotNetInstalled;
end;

