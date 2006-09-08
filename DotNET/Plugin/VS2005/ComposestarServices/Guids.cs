// Guids.cs
// MUST match guids.h
using System;

namespace Trese.ComposestarSupport
{
    static class GuidList
    {
        public const string guidComposestarSupportPkgString = "1c9e99c9-41d2-40af-825e-14bd7369d894";
        public const string guidComposestarSupportCmdSetString = "c3297703-a799-4046-88ec-4fd63fa95c3c";
        public const string guidEditorFactoryString = "86841820-ffd1-46e0-a249-c81f569544fe";

        public static readonly Guid guidComposestarSupportPkg = new Guid(guidComposestarSupportPkgString);
        public static readonly Guid guidComposestarSupportCmdSet = new Guid(guidComposestarSupportCmdSetString);
        public static readonly Guid guidEditorFactory = new Guid(guidEditorFactoryString);
    };
}