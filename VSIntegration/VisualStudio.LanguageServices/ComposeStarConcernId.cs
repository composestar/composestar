
using System;

using Microsoft.VisualStudio.Shell.Interop;

namespace Composestar.StarLight.VisualStudio.LanguageServices {
    /// <summary>
    /// Class used to identify a module. The module is identify using the hierarchy that
    /// contains it and its item id inside the hierarchy.
    /// </summary>
    internal sealed class ConcernId {
        private IVsHierarchy ownerHierarchy;
        private uint itemId;
        public ConcernId(IVsHierarchy owner, uint id) {
            this.ownerHierarchy = owner;
            this.itemId = id;
        }
        public IVsHierarchy Hierarchy {
            get { return ownerHierarchy; }
        }
        public uint ItemID {
            get { return itemId; }
        }
        public override int GetHashCode() {
            int hash = 0;
            if (null != ownerHierarchy) {
                hash = ownerHierarchy.GetHashCode();
            }
            hash = hash ^ (int)itemId;
            return hash;
        }
        public override bool Equals(object obj) {
            ConcernId other = obj as ConcernId;
            if (null == obj) {
                return false;
            }
            if (!ownerHierarchy.Equals(other.ownerHierarchy)) {
                return false;
            }
            return (itemId == other.itemId);
        }
    }
}