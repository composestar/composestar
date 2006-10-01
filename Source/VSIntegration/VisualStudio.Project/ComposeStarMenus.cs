
using System;
using System.ComponentModel.Design;

namespace Composestar.StarLight.VisualStudio.Project
{
    /// <summary>
    /// CommandIDs matching the commands defined items from PkgCmdID.h and guids.h
    /// </summary>
    public sealed class ComposeStarMenus
    {
        internal static readonly Guid guidComposeStarProjectCmdSet = new Guid("{F2048BCB-30CA-4f8b-90E0-DC260C611B42}");
        internal static readonly CommandID SetAsMain = new CommandID(guidComposeStarProjectCmdSet, 0x3001);
    }
}

