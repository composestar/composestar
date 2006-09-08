// Guids.cs
// MUST match guids.h
using System;

namespace Trese.ComposestarProject
{
    static class GuidList
    {
        public const string guidComposestarProjectPkgString = "51162ad8-0500-470e-96ac-d0e4fb7260c4";
        public const string guidComposestarProjectCmdSetString = "b353fb04-a697-44a0-b780-19c05d73c111";

        public static readonly Guid guidComposestarProjectPkg = new Guid(guidComposestarProjectPkgString);
        public static readonly Guid guidComposestarProjectCmdSet = new Guid(guidComposestarProjectCmdSetString);
    };
}