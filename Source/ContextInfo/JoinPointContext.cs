using System;
using System.Collections.Generic;
using System.Runtime.CompilerServices;
using System.Text;
using System.Diagnostics;
using System.ComponentModel;

namespace Composestar.StarLight.ContextInfo
{

    /// <summary>
    /// The JoinPointContext class is used to pass information about the joinpoint to methods.
    /// </summary>
    [DebuggerNonUserCode()]
    public sealed class JoinPointContext
    {

        #region Private Variables

        private Dictionary<short, ArgumentInfo> _arguments;

        private Dictionary<string, object> _properties;

        private object _sender;
        private object _target;
        private string _methodName;
        private ArgumentInfo _returnValue;

        #endregion

        #region Properties

        /// <summary>
        /// Gets or sets the sender.
        /// </summary>
        /// <value>The sender.</value>
        public object Sender
        {
            get { return _sender; }
            set { _sender = value; }
        }
        /// <summary>
        /// Gets or sets the target.
        /// </summary>
        /// <value>The target.</value>
        public object Target
        {
            get { return _target; }
            set { _target = value; }
        }

        /// <summary>
        /// Gets or sets the name of the method.
        /// </summary>
        /// <value>The name of the method.</value>
        public string MethodName
        {
            get { return _methodName; }
            set { _methodName = value; }
        }

        #endregion

        #region ctor

        /// <summary>
        /// Initializes a new instance of the <see cref="T:JoinPointContext"/> class.
        /// </summary>
        public JoinPointContext()
        {
            _arguments = new Dictionary<short, ArgumentInfo>();
            _properties = new Dictionary<string, object>();
        }

        #endregion

        /// <summary>
        /// Adds the argument of a method to the list of arguments.
        /// </summary>
        /// <param name="ordinal">The ordinal of the argument.</param>
        /// <param name="argumentType">Type of the argument.</param>
        /// <param name="value">The value of the argument.</param>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public void AddArgument(short ordinal, Type argumentType, object value)
        {
            if (_arguments.ContainsKey(ordinal))
                _arguments[ordinal] = new ArgumentInfo(argumentType, value);
            else
                _arguments.Add(ordinal, new ArgumentInfo(argumentType, value));
        }

        /// <summary>
        /// Gets the argument value based on the ordinal.
        /// </summary>
        /// <param name="ordinal">The ordinal of the argument.</param>
        /// <returns>An <c>object</c> containing the value, or a <c>null</c> when the ordinal was not present in the argument list.</returns>
        /// <example>Use the <c>GetArgumentValue</c> function to retrieve a value from the list of arguments.
        /// <code>
        /// JoinPointContext jpc = new JoinPointContext();
        /// jpc.AddArgument(1, typeof(int), 1024);
        /// Object o = jpc.GetArgumentValue(1);
        /// int value = (int) o;
        /// </code>
        /// </example> 
        [MethodImpl(MethodImplOptions.Synchronized)]
        public object GetArgumentValue(short ordinal)
        {
            ArgumentInfo ai;
            if (_arguments.TryGetValue(ordinal, out ai))
                return ai.Value;
            else
                return null;
        }

        /// <summary>
        /// Gets the argument value using a generic type.
        /// </summary>
        /// <param name="ordinal">The ordinal.</param>
        /// <returns>A strong typed value or the <c>default(T)</c> when the value could not be found.</returns>
        /// <example>Use the <c>GetGenericArgumentValue</c> function to retrieve a value from the list of arguments using a generic type.
        /// <code>
        /// JoinPointContext jpc = new JoinPointContext();
        /// jpc.AddArgument(1, typeof(int), 1024);
        /// int value = jpc.GetArgumentValue&lt;int&gt;(1);        
        /// </code>
        /// </example> 
        [MethodImpl(MethodImplOptions.Synchronized)]
        public T GetGenericArgumentValue<T>(short ordinal)
        {
            ArgumentInfo ai;
            if (_arguments.TryGetValue(ordinal, out ai))
                return ((T)ai.Value);
            else
                return default(T);
        }

        /// <summary>
        /// Gets the type of the argument.
        /// </summary>
        /// <param name="ordinal">The ordinal.</param>
        /// <returns>The type of the argument, or a <c>null</c> when the type could not be found.</returns>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public Type GetArgumentType(short ordinal)
        {
            ArgumentInfo ai;
            if (_arguments.TryGetValue(ordinal, out ai))
                return ai.Type;
            else
                return null;
        }

        /// <summary>
        /// Property to get the number of arguments
        /// </summary>
        /// <value>The number of arguments.</value>
        public int ArgumentCount
        {
            get
            {
                return _arguments.Count;
            }
        }

        /// <summary>
        /// Gets a value indicating whether this JoinPointContext has a return value.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this JoinPointContext has a return value; otherwise, <c>false</c>.
        /// </value>
        public bool HasReturnValue
        {
            get { return (_returnValue != null); }
        }

        /// <summary>
        /// Gets or sets the type of the return.
        /// </summary>
        /// <value>The type of the return.</value>
        public Type ReturnType
        {
            get
            {
                if (HasReturnValue)
                    return _returnValue.Type;
                else
                    return null;
            }
            set
            {
                if (_returnValue == null)
                    _returnValue = new ArgumentInfo(value, null);
                else
                    _returnValue.Type = value;
            }
        }

        /// <summary>
        /// Gets or sets the return value.
        /// </summary>
        /// <value>The return value.</value>
        public Object ReturnValue
        {
            get
            {
                if (HasReturnValue)
                    return _returnValue.Value;
                else
                    return null;
            }
            set
            {
                if (_returnValue == null)
                    throw new ArgumentNullException("returnType");
                else
                    _returnValue.Value = value;
            }
        }

        /// <summary>
        /// Adds a property to the JoinPointContext, for example to use in a following FilterAction
        /// </summary>
        /// <param name="key">The key of the property</param>
        /// <param name="property">The value of the property</param>
        public void AddProperty(string key, object property)
        {
            _properties[key] = property;
        }

        /// <summary>
        /// Returns a property, or <code>null</code> if the property does not exist.
        /// </summary>
        /// <param name="key">The key of the property</param>
        /// <returns>The property, or <code>null</code> if the property does not exist.</returns>
        public object GetProperty(string key)
        {
            if(_properties.ContainsKey(key))
            {
                return _properties[key];
            }
            else
            {
                return null;
            }
        }


        /// <summary>
        /// Internal class for storing the arguments.
        /// </summary>
        private sealed class ArgumentInfo
        {

            /// <summary>
            /// Initializes a new instance of the <see cref="T:ArgumentInfo"/> class.
            /// </summary>
            public ArgumentInfo()
            {
            }

            /// <summary>
            /// Initializes a new instance of the <see cref="T:ArgumentInfo"/> class.
            /// </summary>
            /// <param name="type">The type.</param>
            /// <param name="value">The value.</param>
            public ArgumentInfo(Type type, Object value)
            {
                _type = type;
                _value = value;
            }

            private Type _type;

            /// <summary>
            /// Gets or sets the type.
            /// </summary>
            /// <value>The type.</value>
            public Type Type
            {
                get { return _type; }
                set { _type = value; }
            }

            private object _value;

            /// <summary>
            /// Gets or sets the value.
            /// </summary>
            /// <value>The value.</value>
            public object Value
            {
                get { return _value; }
                set { _value = value; }
            }

        }

    }
}