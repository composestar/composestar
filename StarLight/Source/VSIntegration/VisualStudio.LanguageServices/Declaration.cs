using System;
using System.Collections.Generic;
using System.Reflection;
using System.Diagnostics;
using System.IO;

using Microsoft.VisualStudio.TextManager.Interop;

namespace Composestar.StarLight.VisualStudio.LanguageServices
{
    public class Declaration : IComparable
    {
        public enum DeclarationType
        {
            Snippet,
            Class,
            Predicate,
            Unknown
        }
        public Declaration(string title)
        {
            this.Type = DeclarationType.Unknown;
            this.shortcut = "";
            this.title = title;
            this.description = "";
        }

        public Declaration(string shortcut, string title, DeclarationType type, string description)
        {
            this.Type = type;
            this.shortcut = shortcut;
            this.title = title;
            this.description = description;
        }

        public Declaration(VsExpansion expansion)
        {
            this.Type = DeclarationType.Snippet;
            this.shortcut = expansion.shortcut;
            this.title = expansion.title;
            this.description = expansion.description;
        }

        protected Declaration()
        {
        }

        public int CompareTo(object obj)
        {
            Declaration decl = (Declaration)obj;
            return (this.title.CompareTo(decl.title));
        }

        public override bool Equals(Object obj)
        {
            if (!(obj is Declaration))
                return false;
            return (this.CompareTo(obj) == 0);
        }

        public override int GetHashCode()
        {
            char[] c = this.title.ToCharArray();
            return (int)c[0];
        }

        // Disable the "IdentifiersShouldNotMatchKeywords" warning.
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1062")]
        public static bool operator ==(Declaration d1, Declaration d2)
        {
            return d1.Equals(d2);
        }

        // Disable the "IdentifiersShouldNotMatchKeywords" warning.
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1062")]
        public static bool operator !=(Declaration d1, Declaration d2)
        {
            return !(d1 == d2);
        }

        // Disable the "IdentifiersShouldNotMatchKeywords" warning.
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1062")]
        public static bool operator <(Declaration d1, Declaration d2)
        {
            return (d1.CompareTo(d2) < 0);
        }

        // Disable the "IdentifiersShouldNotMatchKeywords" warning.
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1062")]
        public static bool operator >(Declaration d1, Declaration d2)
        {
            return (d1.CompareTo(d2) > 0);
        }

        public DeclarationType Type
        {
            get { return type; }
            set { type = value; }
        }

        public string Description
        {
            get { return description; }
            set { description = value; }
        }

        public string Shortcut
        {
            get { return shortcut; }
            set { shortcut = value; }
        }

        public string Title
        {
            get { return title; }
            set { title = value; }
        }

        private DeclarationType type;
        private string description;
        private string shortcut;
        private string title;
    }
}
