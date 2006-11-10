using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using System.Diagnostics.CodeAnalysis;
using Composestar.StarLight.Entities.Concerns;
using Composestar.StarLight.Entities.LanguageModel;

namespace Composestar.StarLight.Entities.Configuration
{
    /// <summary>
    /// Contains all the settings for the current starlight project that is currenlty building.
    /// </summary>
    [Serializable]
    [XmlRoot("ConfigurationContainer", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class ConfigurationContainer
    {
        private List<ConcernElement> _concerns = new List<ConcernElement>();

        /// <summary>
        /// Gets or sets the concerns.
        /// </summary>
        /// <value>The concerns.</value>
        [XmlArray("Concerns")]
        [XmlArrayItem("Concern")]
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
        public List<ConcernElement> Concerns
        {
            get { return _concerns; }
            set { _concerns = value; }
        }

        private string _intermediateOutputPath;

        /// <summary>
        /// Gets or sets the intermediate output path.
        /// </summary>
        /// <value>The intermediate output path.</value>
        [XmlAttribute]
        public string IntermediateOutputPath
        {
            get { return _intermediateOutputPath; }
            set { _intermediateOutputPath = value; }
        }
        private string _specificationFILTH;

        /// <summary>
        /// Gets or sets the specification FILTH.
        /// </summary>
        /// <value>The specification FILTH.</value>
        [XmlAttribute]
        [SuppressMessage("Microsoft.Naming", "CA1705:LongAcronymsShouldBePascalCased")]
        public string SpecificationFILTH
        {
            get { return _specificationFILTH; }
            set { _specificationFILTH = value; }
        }

        private string _installFolder;

        /// <summary>
        /// Gets or sets the install folder.
        /// </summary>
        /// <value>The install folder.</value>
        [XmlAttribute]
        public string InstallFolder
        {
            get { return _installFolder; }
            set { _installFolder = value; }
        }

        private short _compiletimeDebugLevel;

        /// <summary>
        /// Gets or sets the compiletime debug level.
        /// </summary>
        /// <value>The compiletime debug level.</value>
        [XmlAttribute]
        public short CompiletimeDebugLevel
        {
            get { return _compiletimeDebugLevel; }
            set { _compiletimeDebugLevel = value; }
        }

        private List<AssemblyConfig> _assemblies = new List<AssemblyConfig>();

        /// <summary>
        /// Gets or sets the assemblies.
        /// </summary>
        /// <value>The assemblies.</value>
        [XmlArray("Assemblies")]
        [XmlArrayItem("AssemblyConfig")]
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
        public List<AssemblyConfig> Assemblies
        {
            get { return _assemblies; }
            set { _assemblies = value; }
        }

        private List<FilterTypeElement> _filterTypes = new List<FilterTypeElement>();

        /// <summary>
        /// Gets or sets the filter types.
        /// </summary>
        /// <value>The filter types.</value>
        [XmlArray("FilterTypes")]
        [XmlArrayItem("FilterType")]
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
        public List<FilterTypeElement> FilterTypes
        {
            get
            {
                return _filterTypes;
            }
            set
            {
                _filterTypes = value;
            }
        }

        private List<FilterActionElement> _filterActions = new List<FilterActionElement>();

        /// <summary>
        /// Gets or sets the filter actions.
        /// </summary>
        /// <value>The filter actions.</value>
        [XmlArray("FilterActions")]
        [XmlArrayItem("FilterAction")]
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
        public List<FilterActionElement> FilterActions
        {
            get
            {
                return _filterActions;
            }
            set
            {
                _filterActions = value;
            }
        }

    }
}
