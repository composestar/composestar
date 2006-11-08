using System;
using System.Xml.Serialization;	 // For serialization of an object to an XML Document file.
using System.Runtime.Serialization.Formatters.Binary; // For serialization of an object to an XML Binary file.
using System.IO;				 // For reading/writing data to an XML file.
using System.IO.IsolatedStorage; // For accessing user isolated data.
using System.Collections.Generic;
using System.IO.Compression;

namespace Composestar.Repository
{
    /// <summary>
    /// Serialization format types.
    /// </summary>
    public enum SerializedFormat
    {
        /// <summary>
        /// Binary serialization format.
        /// </summary>
        Binary,

        /// <summary>
        /// Document serialization format.
        /// </summary>
        Document,

        /// <summary>
        /// Document serialization format using GZIP compression.
        /// </summary>
        DocumentCompressed
    }

    /// <summary>
    /// Facade to XML serialization and deserialization of strongly typed objects to/from an XML file.
    /// 
    /// <seealso cref="http://samples.gotdotnet.com/">XML Serialization</seealso>
    /// <seealso cref="http://samples.gotdotnet.com/QuickStart/howto/default.aspx?url=/quickstart/howto/doc/xmlserialization/rwobjfromxml.aspx">GotDotNet</seealso>
    /// </summary>
    public static class ObjectXMLSerializer<T> where T : class // Specify that T must be a class.
    {
        #region Load methods

        /// <summary>
        /// Loads an object from an XML file in Document format.
        /// </summary>
        /// <example>
        /// <code>
        /// serializableObject = ObjectXMLSerializer&lt;SerializableObject&gt;.Load(@"C:\XMLObjects.xml");
        /// </code>
        /// </example>
        /// <param name="path">Path of the file to load the object from.</param>
        /// <returns>Object loaded from an XML file in Document format.</returns>
        public static T Load(string path)
        {
            T serializableObject = LoadFromDocumentFormat(null, path, null);
            return serializableObject;
        }

        /// <summary>
        /// Loads an object from an XML file using a specified serialized format.
        /// </summary>
        /// <example>
        /// <code>
        /// serializableObject = ObjectXMLSerializer&lt;SerializableObject&gt;.Load(@"C:\XMLObjects.xml", SerializedFormat.Binary);
        /// </code>
        /// </example>		
        /// <param name="path">Path of the file to load the object from.</param>
        /// <param name="serializedFormat">XML serialized format used to load the object.</param>
        /// <returns>Object loaded from an XML file using the specified serialized format.</returns>
        public static T Load(string path, SerializedFormat serializedFormat)
        {
            T serializableObject = null;

            switch (serializedFormat)
            {
                case SerializedFormat.Binary:
                    serializableObject = LoadFromBinaryFormat(path, null);
                    break;

                case SerializedFormat.DocumentCompressed:
                    serializableObject = LoadFromDocumentCompressedFormat(null, path, null);
                    break;

                case SerializedFormat.Document:
                default:
                    serializableObject = LoadFromDocumentFormat(null, path, null);
                    break;
            }

            return serializableObject;
        }

        /// <summary>
        /// Loads an object from an XML file in Document format, supplying extra data types to enable deserialization of custom types within the object.
        /// </summary>
        /// <param name="path">Path of the file to load the object from.</param>
        /// <param name="serializedFormat">The serialized format.</param>
        /// <param name="extraTypes">Extra data types to enable deserialization of custom types within the object.</param>
        /// <returns>
        /// Object loaded from an XML file in Document format.
        /// </returns>
        /// <example>
        /// 	<code>
        /// serializableObject = ObjectXMLSerializer&lt;SerializableObject&gt;.Load(@"C:\XMLObjects.xml", SerializedFormat.Document, new Type[] { typeof(MyCustomType) });
        /// </code>
        /// </example>
        public static T Load(string path, SerializedFormat serializedFormat, System.Type[] extraTypes)
        {
            T serializableObject = null;

            switch (serializedFormat)
            {
                case SerializedFormat.Binary:
                    serializableObject = LoadFromBinaryFormat(path, null);
                    break;

                case SerializedFormat.DocumentCompressed:
                    serializableObject = LoadFromDocumentCompressedFormat(extraTypes, path, null);
                    break;

                case SerializedFormat.Document:
                default:
                    serializableObject = LoadFromDocumentFormat(extraTypes, path, null);
                    break;
            }

            return serializableObject;
        }

        /// <summary>
        /// Loads an object from an XML file in Document format, supplying extra data types to enable deserialization of custom types within the object.
        /// </summary>
        /// <example>
        /// <code>
        /// serializableObject = ObjectXMLSerializer&lt;SerializableObject&gt;.Load(@"C:\XMLObjects.xml", new Type[] { typeof(MyCustomType) });
        /// </code>
        /// </example>
        /// <param name="path">Path of the file to load the object from.</param>
        /// <param name="extraTypes">Extra data types to enable deserialization of custom types within the object.</param>
        /// <returns>Object loaded from an XML file in Document format.</returns>
        public static T Load(string path, System.Type[] extraTypes)
        {
            T serializableObject = LoadFromDocumentFormat(extraTypes, path, null);
            return serializableObject;
        }

        /// <summary>
        /// Loads an object from an XML file in Document format, located in a specified isolated storage area.
        /// </summary>
        /// <example>
        /// <code>
        /// serializableObject = ObjectXMLSerializer&lt;SerializableObject&gt;.Load("XMLObjects.xml", IsolatedStorageFile.GetUserStoreForAssembly());
        /// </code>
        /// </example>
        /// <param name="fileName">Name of the file in the isolated storage area to load the object from.</param>
        /// <param name="isolatedStorageDirectory">Isolated storage area directory containing the XML file to load the object from.</param>
        /// <returns>Object loaded from an XML file in Document format located in a specified isolated storage area.</returns>
        public static T Load(string fileName, IsolatedStorageFile isolatedStorageDirectory)
        {
            T serializableObject = LoadFromDocumentFormat(null, fileName, isolatedStorageDirectory);
            return serializableObject;
        }

        /// <summary>
        /// Loads an object from an XML file located in a specified isolated storage area, using a specified serialized format.
        /// </summary>
        /// <example>
        /// <code>
        /// serializableObject = ObjectXMLSerializer&lt;SerializableObject&gt;.Load("XMLObjects.xml", IsolatedStorageFile.GetUserStoreForAssembly(), SerializedFormat.Binary);
        /// </code>
        /// </example>		
        /// <param name="fileName">Name of the file in the isolated storage area to load the object from.</param>
        /// <param name="isolatedStorageDirectory">Isolated storage area directory containing the XML file to load the object from.</param>
        /// <param name="serializedFormat">XML serialized format used to load the object.</param>        
        /// <returns>Object loaded from an XML file located in a specified isolated storage area, using a specified serialized format.</returns>
        public static T Load(string fileName, IsolatedStorageFile isolatedStorageDirectory, SerializedFormat serializedFormat)
        {
            T serializableObject = null;

            switch (serializedFormat)
            {
                case SerializedFormat.Binary:
                    serializableObject = LoadFromBinaryFormat(fileName, isolatedStorageDirectory);
                    break;

                case SerializedFormat.DocumentCompressed:
                    serializableObject = LoadFromDocumentCompressedFormat(null, fileName, isolatedStorageDirectory);
                    break;

                case SerializedFormat.Document:
                default:
                    serializableObject = LoadFromDocumentFormat(null, fileName, isolatedStorageDirectory);
                    break;
            }

            return serializableObject;
        }

        /// <summary>
        /// Loads an object from an XML file in Document format, located in a specified isolated storage area, and supplying extra data types to enable deserialization of custom types within the object.
        /// </summary>
        /// <example>
        /// <code>
        /// serializableObject = ObjectXMLSerializer&lt;SerializableObject&gt;.Load("XMLObjects.xml", IsolatedStorageFile.GetUserStoreForAssembly(), new Type[] { typeof(MyCustomType) });
        /// </code>
        /// </example>		
        /// <param name="fileName">Name of the file in the isolated storage area to load the object from.</param>
        /// <param name="isolatedStorageDirectory">Isolated storage area directory containing the XML file to load the object from.</param>
        /// <param name="extraTypes">Extra data types to enable deserialization of custom types within the object.</param>
        /// <returns>Object loaded from an XML file located in a specified isolated storage area, using a specified serialized format.</returns>
        public static T Load(string fileName, IsolatedStorageFile isolatedStorageDirectory, System.Type[] extraTypes)
        {
            T serializableObject = LoadFromDocumentFormat(null, fileName, isolatedStorageDirectory);
            return serializableObject;
        }

        #endregion

        #region Save methods

        /// <summary>
        /// Saves an object to an XML file in Document format.
        /// </summary>
        /// <example>
        /// <code>        
        /// SerializableObject serializableObject = new SerializableObject();
        /// 
        /// ObjectXMLSerializer&lt;SerializableObject&gt;.Save(serializableObject, @"C:\XMLObjects.xml");
        /// </code>
        /// </example>
        /// <param name="serializableObject">Serializable object to be saved to file.</param>
        /// <param name="path">Path of the file to save the object to.</param>
        public static void Save(T serializableObject, string path)
        {
            SaveToDocumentFormat(serializableObject, null, path, null);
        }

        /// <summary>
        /// Saves an object to an XML file using a specified serialized format.
        /// </summary>
        /// <example>
        /// <code>
        /// SerializableObject serializableObject = new SerializableObject();
        /// 
        /// ObjectXMLSerializer&lt;SerializableObject&gt;.Save(serializableObject, @"C:\XMLObjects.xml", SerializedFormat.Binary);
        /// </code>
        /// </example>
        /// <param name="serializableObject">Serializable object to be saved to file.</param>
        /// <param name="path">Path of the file to save the object to.</param>
        /// <param name="serializedFormat">XML serialized format used to save the object.</param>
        public static void Save(T serializableObject, string path, SerializedFormat serializedFormat)
        {
            switch (serializedFormat)
            {
                case SerializedFormat.Binary:
                    SaveToBinaryFormat(serializableObject, path, null);
                    break;

                case SerializedFormat.DocumentCompressed:
                    SaveToDocumentCompressedFormat(serializableObject, null, path, null);
                    break;

                case SerializedFormat.Document:
                default:
                    SaveToDocumentFormat(serializableObject, null, path, null);
                    break;
            }
        }

        /// <summary>
        /// Saves an object to an XML file using a specified serialized format.
        /// </summary>
        /// <param name="serializableObject">Serializable object to be saved to file.</param>
        /// <param name="path">Path of the file to save the object to.</param>
        /// <param name="serializedFormat">XML serialized format used to save the object.</param>
        /// <param name="extraTypes">Extra data types to enable serialization of custom types within the object.</param>
        /// <example>
        /// 	<code>
        /// SerializableObject serializableObject = new SerializableObject();
        /// ObjectXMLSerializer&lt;SerializableObject&gt;.Save(serializableObject, @"C:\XMLObjects.xml", SerializedFormat.Binary, new Type[] { typeof(MyCustomType) });
        /// </code>
        /// </example>
        public static void Save(T serializableObject, string path, SerializedFormat serializedFormat, System.Type[] extraTypes)
        {
            switch (serializedFormat)
            {
                case SerializedFormat.Binary:
                    SaveToBinaryFormat(serializableObject, path, null);
                    break;

                case SerializedFormat.DocumentCompressed:
                    SaveToDocumentCompressedFormat(serializableObject, extraTypes, path, null);
                    break;

                case SerializedFormat.Document:
                default:
                    SaveToDocumentFormat(serializableObject, extraTypes, path, null);
                    break;
            }
        }

        /// <summary>
        /// Saves an object to an XML file in Document format, supplying extra data types to enable serialization of custom types within the object.
        /// </summary>
        /// <example>
        /// <code>        
        /// SerializableObject serializableObject = new SerializableObject();
        /// 
        /// ObjectXMLSerializer&lt;SerializableObject&gt;.Save(serializableObject, @"C:\XMLObjects.xml", new Type[] { typeof(MyCustomType) });
        /// </code>
        /// </example>
        /// <param name="serializableObject">Serializable object to be saved to file.</param>
        /// <param name="path">Path of the file to save the object to.</param>
        /// <param name="extraTypes">Extra data types to enable serialization of custom types within the object.</param>
        public static void Save(T serializableObject, string path, System.Type[] extraTypes)
        {
            SaveToDocumentFormat(serializableObject, extraTypes, path, null);
        }

        /// <summary>
        /// Saves an object to an XML file in Document format, located in a specified isolated storage area.
        /// </summary>
        /// <example>
        /// <code>        
        /// SerializableObject serializableObject = new SerializableObject();
        /// 
        /// ObjectXMLSerializer&lt;SerializableObject&gt;.Save(serializableObject, "XMLObjects.xml", IsolatedStorageFile.GetUserStoreForAssembly());
        /// </code>
        /// </example>
        /// <param name="serializableObject">Serializable object to be saved to file.</param>
        /// <param name="fileName">Name of the file in the isolated storage area to save the object to.</param>
        /// <param name="isolatedStorageDirectory">Isolated storage area directory containing the XML file to save the object to.</param>
        public static void Save(T serializableObject, string fileName, IsolatedStorageFile isolatedStorageDirectory)
        {
            SaveToDocumentFormat(serializableObject, null, fileName, isolatedStorageDirectory);
        }

        /// <summary>
        /// Saves an object to an XML file located in a specified isolated storage area, using a specified serialized format.
        /// </summary>
        /// <example>
        /// <code>        
        /// SerializableObject serializableObject = new SerializableObject();
        /// 
        /// ObjectXMLSerializer&lt;SerializableObject&gt;.Save(serializableObject, "XMLObjects.xml", IsolatedStorageFile.GetUserStoreForAssembly(), SerializedFormat.Binary);
        /// </code>
        /// </example>
        /// <param name="serializableObject">Serializable object to be saved to file.</param>
        /// <param name="fileName">Name of the file in the isolated storage area to save the object to.</param>
        /// <param name="isolatedStorageDirectory">Isolated storage area directory containing the XML file to save the object to.</param>
        /// <param name="serializedFormat">XML serialized format used to save the object.</param>        
        public static void Save(T serializableObject, string fileName, IsolatedStorageFile isolatedStorageDirectory, SerializedFormat serializedFormat)
        {
            switch (serializedFormat)
            {
                case SerializedFormat.Binary:
                    SaveToBinaryFormat(serializableObject, fileName, isolatedStorageDirectory);
                    break;

                case SerializedFormat.DocumentCompressed:
                    SaveToDocumentCompressedFormat(serializableObject, null, fileName, isolatedStorageDirectory);
                    break;

                case SerializedFormat.Document:
                default:
                    SaveToDocumentFormat(serializableObject, null, fileName, isolatedStorageDirectory);
                    break;
            }
        }

        /// <summary>
        /// Saves an object to an XML file in Document format, located in a specified isolated storage area, and supplying extra data types to enable serialization of custom types within the object.
        /// </summary>
        /// <example>
        /// <code>
        /// SerializableObject serializableObject = new SerializableObject();
        /// 
        /// ObjectXMLSerializer&lt;SerializableObject&gt;.Save(serializableObject, "XMLObjects.xml", IsolatedStorageFile.GetUserStoreForAssembly(), new Type[] { typeof(MyCustomType) });
        /// </code>
        /// </example>		
        /// <param name="serializableObject">Serializable object to be saved to file.</param>
        /// <param name="fileName">Name of the file in the isolated storage area to save the object to.</param>
        /// <param name="isolatedStorageDirectory">Isolated storage area directory containing the XML file to save the object to.</param>
        /// <param name="extraTypes">Extra data types to enable serialization of custom types within the object.</param>
        public static void Save(T serializableObject, string fileName, IsolatedStorageFile isolatedStorageDirectory, System.Type[] extraTypes)
        {
            SaveToDocumentFormat(serializableObject, null, fileName, isolatedStorageDirectory);
        }

        #endregion

        #region Private

        /// <summary>
        /// Creates the file stream.
        /// </summary>
        /// <param name="isolatedStorageFolder">The isolated storage folder.</param>
        /// <param name="path">The path.</param>
        /// <returns></returns>
        private static FileStream CreateFileStream(IsolatedStorageFile isolatedStorageFolder, string path)
        {
            FileStream fileStream = null;

            if (isolatedStorageFolder == null)
                fileStream = new FileStream(path, FileMode.OpenOrCreate);
            else
                fileStream = new IsolatedStorageFileStream(path, FileMode.OpenOrCreate, isolatedStorageFolder);

            return fileStream;
        }

        /// <summary>
        /// Loads from binary format.
        /// </summary>
        /// <param name="path">The path.</param>
        /// <param name="isolatedStorageFolder">The isolated storage folder.</param>
        /// <returns></returns>
        private static T LoadFromBinaryFormat(string path, IsolatedStorageFile isolatedStorageFolder)
        {
            T serializableObject = null;

            using (FileStream fileStream = CreateFileStream(isolatedStorageFolder, path))
            {
                BinaryFormatter binaryFormatter = new BinaryFormatter();
                serializableObject = binaryFormatter.Deserialize(fileStream) as T;
            }

            return serializableObject;
        }

        /// <summary>
        /// Loads from document format.
        /// </summary>
        /// <param name="extraTypes">The extra types.</param>
        /// <param name="path">The path.</param>
        /// <param name="isolatedStorageFolder">The isolated storage folder.</param>
        /// <returns></returns>
        private static T LoadFromDocumentFormat(System.Type[] extraTypes, string path, IsolatedStorageFile isolatedStorageFolder)
        {
            T serializableObject = null;

            using (TextReader textReader = CreateTextReader(isolatedStorageFolder, path))
            {
                XmlSerializer xmlSerializer = CreateXmlSerializer(extraTypes);
                serializableObject = xmlSerializer.Deserialize(textReader) as T;

            }

            return serializableObject;
        }

        /// <summary>
        /// Loads from document compressed format.
        /// </summary>
        /// <param name="extraTypes">The extra types.</param>
        /// <param name="path">The path.</param>
        /// <param name="isolatedStorageFolder">The isolated storage folder.</param>
        /// <returns></returns>
        private static T LoadFromDocumentCompressedFormat(System.Type[] extraTypes, string path, IsolatedStorageFile isolatedStorageFolder)
        {
            T serializableObject = null;

            using (Stream compressReader = CreateCompressionReader(isolatedStorageFolder, path))
            {
                XmlSerializer xmlSerializer = CreateXmlSerializer(extraTypes);
                serializableObject = xmlSerializer.Deserialize(compressReader) as T;
            }

            return serializableObject;
        }

        /// <summary>
        /// Creates the compression reader.
        /// </summary>
        /// <param name="isolatedStorageFolder">The isolated storage folder.</param>
        /// <param name="path">The path.</param>
        /// <returns></returns>
        private static Stream CreateCompressionReader(IsolatedStorageFile isolatedStorageFolder, string path)
        {
            Stream compressStream = null;

            if (isolatedStorageFolder == null)
                compressStream = new BufferedStream(new GZipStream(new FileStream(path, FileMode.Open), CompressionMode.Decompress));
            else
                compressStream = new BufferedStream(new GZipStream(new IsolatedStorageFileStream(path, FileMode.Open, isolatedStorageFolder), CompressionMode.Decompress));

            return compressStream;
        }

        /// <summary>
        /// Creates the text reader.
        /// </summary>
        /// <param name="isolatedStorageFolder">The isolated storage folder.</param>
        /// <param name="path">The path.</param>
        /// <returns></returns>
        private static TextReader CreateTextReader(IsolatedStorageFile isolatedStorageFolder, string path)
        {
            TextReader textReader = null;

            if (isolatedStorageFolder == null)
                textReader = new StreamReader(path);
            else
                textReader = new StreamReader(new IsolatedStorageFileStream(path, FileMode.Open, isolatedStorageFolder));

            return textReader;
        }

        /// <summary>
        /// Creates the text writer.
        /// </summary>
        /// <param name="isolatedStorageFolder">The isolated storage folder.</param>
        /// <param name="path">The path.</param>
        /// <returns></returns>
        private static TextWriter CreateTextWriter(IsolatedStorageFile isolatedStorageFolder, string path)
        {
            TextWriter textWriter = null;

            if (isolatedStorageFolder == null)
                textWriter = new StreamWriter(path);
            else
                textWriter = new StreamWriter(new IsolatedStorageFileStream(path, FileMode.OpenOrCreate, isolatedStorageFolder));

            return textWriter;
        }

        /// <summary>
        /// Creates the compression writer.
        /// </summary>
        /// <param name="isolatedStorageFolder">The isolated storage folder.</param>
        /// <param name="path">The path.</param>
        /// <returns></returns>
        private static Stream CreateCompressionWriter(IsolatedStorageFile isolatedStorageFolder, string path)
        {
            Stream compressStream = null;

            if (isolatedStorageFolder == null)
                compressStream = new BufferedStream(new GZipStream(new FileStream(path, FileMode.Create), CompressionMode.Compress));
            else
                compressStream = new BufferedStream(new GZipStream(new IsolatedStorageFileStream(path, FileMode.OpenOrCreate, isolatedStorageFolder), CompressionMode.Compress));

            return compressStream;
        }

        /// <summary>
        /// Creates the XML serializer.
        /// </summary>
        /// <param name="extraTypes">The extra types.</param>
        /// <returns></returns>
        private static XmlSerializer CreateXmlSerializer(System.Type[] extraTypes)
        {
            Type objectType = typeof(T);

            XmlSerializer xmlSerializer = null;

            if (extraTypes != null)
                xmlSerializer = new XmlSerializer(objectType, extraTypes);
            else
                xmlSerializer = new XmlSerializer(objectType);

            return xmlSerializer;
        }

        /// <summary>
        /// Saves to document format.
        /// </summary>
        /// <param name="serializableObject">The serializable object.</param>
        /// <param name="extraTypes">The extra types.</param>
        /// <param name="path">The path.</param>
        /// <param name="isolatedStorageFolder">The isolated storage folder.</param>
        private static void SaveToDocumentFormat(T serializableObject, System.Type[] extraTypes, string path, IsolatedStorageFile isolatedStorageFolder)
        {
            using (TextWriter textWriter = CreateTextWriter(isolatedStorageFolder, path))
            {
                XmlSerializer xmlSerializer = CreateXmlSerializer(extraTypes);
                xmlSerializer.Serialize(textWriter, serializableObject);
            }
        }

        /// <summary>
        /// Saves to document compressed format.
        /// </summary>
        /// <param name="serializableObject">The serializable object.</param>
        /// <param name="extraTypes">The extra types.</param>
        /// <param name="path">The path.</param>
        /// <param name="isolatedStorageFolder">The isolated storage folder.</param>
        private static void SaveToDocumentCompressedFormat(T serializableObject, System.Type[] extraTypes, string path, IsolatedStorageFile isolatedStorageFolder)
        {
            using (Stream compressStream = CreateCompressionWriter(isolatedStorageFolder, path))
            {
                XmlSerializer xmlSerializer = CreateXmlSerializer(extraTypes);
                xmlSerializer.Serialize(compressStream, serializableObject);
            }
        }

        /// <summary>
        /// Saves to binary format.
        /// </summary>
        /// <param name="serializableObject">The serializable object.</param>
        /// <param name="path">The path.</param>
        /// <param name="isolatedStorageFolder">The isolated storage folder.</param>
        private static void SaveToBinaryFormat(T serializableObject, string path, IsolatedStorageFile isolatedStorageFolder)
        {
            using (FileStream fileStream = CreateFileStream(isolatedStorageFolder, path))
            {
                BinaryFormatter binaryFormatter = new BinaryFormatter();
                binaryFormatter.Serialize(fileStream, serializableObject);
            }
        }

        #endregion
    
    }
}