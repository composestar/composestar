#region Using directives
using System;
using System.Collections.Generic;
using System.Runtime.CompilerServices;
using System.Text;
using System.Diagnostics;
using System.ComponentModel;
#endregion

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
        private object _startTarget;
        private string _startSelector;
        private object _currentTarget;
        private string _currentSelector;
        private object _substitutionTarget;
        private string _substitutionSelector;
        private ArgumentInfo _returnValue;
        private bool _hasReturnValueSet;

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
        /// Gets or sets the start-target.
        /// </summary>
        /// <value>The start-target.</value>
        public object StartTarget
        {
            get { return _startTarget; }
            set { _startTarget = value; }
        }

        /// <summary>
        /// Gets or sets the start-selector.
        /// </summary>
        /// <value>The start-selector.</value>
        public string StartSelector
        {
            get { return _startSelector; }
            set { _startSelector = value; }
        }

        /// <summary>
        /// Gets or sets the current-target.
        /// </summary>
        /// <value>The current-target.</value>
        public object CurrentTarget
        {
            get { return _currentTarget; }
            set { _currentTarget = value; }
        }

        /// <summary>
        /// Gets or sets the current-selector.
        /// </summary>
        /// <value>The current-selector.</value>
        public string CurrentSelector
        {
            get { return _currentSelector; }
            set { _currentSelector = value; }
        }

        /// <summary>
        /// Gets or sets the substitution-target.
        /// </summary>
        /// <value>The substitution-target.</value>
        public object SubstitutionTarget
        {
            get { return _substitutionTarget; }
            set { _substitutionTarget = value; }
        }

        /// <summary>
        /// Gets or sets the substitution-selector.
        /// </summary>
        /// <value>The substitution-selector.</value>
        public string SubstitutionSelector
        {
            get { return _substitutionSelector; }
            set { _substitutionSelector = value; }
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

        #region Arguments Handling 

        #region Add Arguments

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
        /// Adds the argument of a method to the list of arguments.
        /// </summary>
        /// <param name="ordinal">The ordinal.</param>
        /// <param name="argumentType">Type of the argument.</param>
        /// <param name="argumentAttributes">The argument attributes.</param>
        /// <param name="value">The value.</param>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public void AddArgument(short ordinal, Type argumentType, ArgumentAttributes argumentAttributes, object value )
        {
            if (_arguments.ContainsKey(ordinal))
                _arguments[ordinal] = new ArgumentInfo(argumentType, value, argumentAttributes);
            else
                _arguments.Add(ordinal, new ArgumentInfo(argumentType, value, argumentAttributes));
        }

        /// <summary>
        /// Adds the argument of a method to the list of arguments in the given JoinPointContext.
        /// This static method is usefull to add an argument that is on the stack, because we cannot 
        /// directly place the JoinPointContext object before it on the stack.
        /// </summary>
        /// <param name="value">The value of the argument.</param>
        /// <param name="ordinal">The ordinal of the argument.</param>
        /// <param name="argumentType">Type of the argument.</param>
        /// <param name="context">The JoinPointContext to which the argument needs to be added</param>
        public static void AddArgument(object value, short ordinal, Type argumentType, JoinPointContext context)
        {
            if(context != null)
            {
                context.AddArgument(ordinal, argumentType, value);
            }
        }

        /// <summary>
        /// Adds the argument of a method to the list of arguments in the given JoinPointContext.
        /// This static method is usefull to add an argument that is on the stack, because we cannot 
        /// directly place the JoinPointContext object before it on the stack.
        /// </summary>
        /// <param name="value">The value.</param>
        /// <param name="ordinal">The ordinal.</param>
        /// <param name="argumentType">Type of the argument.</param>
        /// <param name="argumentAttributes">The argument attributes.</param>
        /// <param name="context">The context.</param>
        public static void AddArgument(object value, short ordinal, Type argumentType, ArgumentAttributes argumentAttributes, JoinPointContext context)
        {
            if (context != null)
            {
                context.AddArgument(ordinal, argumentType, argumentAttributes, value);
            }
        }

        #endregion

        #region Retrieve Arguments

        /// <summary>
        /// Gets the argument value based on the ordinal.
        /// </summary>
        /// <param name="ordinal">The ordinal of the argument.</param>
        /// <returns>An <c>object</c> containing the value, or a <see langword="null"/> when the ordinal was not present in the argument list.</returns>
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
        /// Gets the argument attributes.
        /// </summary>
        /// <param name="ordinal">The ordinal.</param>
        /// <returns></returns>
        /// <exception cref="ArgumentNullException">
        /// This exception will be raised when the ordinal could not be found in the list of arguments.
        /// </exception>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public ArgumentAttributes GetArgumentAttributes(short ordinal)
        {
            ArgumentInfo ai;
            if (_arguments.TryGetValue(ordinal, out ai))
                return ai.Attributes;
            else
                throw new ArgumentNullException("ordinal");
        }

        /// <summary>
        /// Get the <see cref="T:ArgumentInfo"></see> object or <see langword="null"></see> when the <paramref name="ordinal"/> is not found.
        /// </summary>
        /// <param name="ordinal">The ordinal of the argument.</param>
        /// <returns>Argument info object or <see langword="null"></see> when the <paramref name="ordinal"/> is not found.</returns>
        public ArgumentInfo GetArgumentInfo(short ordinal)
        {
            ArgumentInfo ai;
            _arguments.TryGetValue(ordinal, out ai);
            
            return ai;
            
        } // GetArgumentInfo(ordinal)

        /// <summary>
        /// Gets the argument value using a generic type.
        /// </summary>
        /// <param name="ordinal">The ordinal.</param>
        /// <typeparam name="T">The type to return.</typeparam> 
        /// <returns>
        /// A strong typed value or the <c>default(T)</c> when the value could not be found.
        /// </returns>
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
        /// <returns>The type of the argument, or a <see langword="null"/> when the type could not be found.</returns>
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
        /// Get an ordered list of arguments.
        /// </summary>
        /// <value>The arguments.</value>
        /// <returns>List</returns>
        public Dictionary<short, ArgumentInfo> GetArguments
        {
            get
            {
                return _arguments;
            } // get
        } // GetArguments()

        #endregion

        #endregion

        #region Set Target

        /// <summary>
        /// Sets the target in the given JoinPointContext to obj. This static method is usefull
        /// to store a target that is on the stack, because we cannot directly place the JoinPointContext 
        /// object before it on the stack.
        /// </summary>
        /// <param name="obj">The target object to store.</param>
        /// <param name="context">The JoinPointContext to store it in.</param>
        public static void SetTarget(object obj, JoinPointContext context)
        {
            if (context != null)
            {
                context.StartTarget = obj;
            }
        }

        #endregion

        #region Return Values

        /// <summary>
        /// Gets a value indicating whether this JoinPointContext has a return value.
        /// </summary>
        /// <value>
        /// 	<see langword="true"/> if this JoinPointContext has a return value; otherwise, <see langword="false"/>.
        /// </value>
        public bool HasReturnValue
        {
            get 
            { 
                return _hasReturnValueSet; 
            }
        }

        /// <summary>
        /// Gets or sets the type of the return value.
        /// </summary>
        /// <value>The type of the return value.</value>
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
        /// <exception cref="ArgumentNullException">
        /// Thrown when the <see cref="ReturnType"></see> property is <see langword="null" />.
        /// </exception>
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
                {
                    _returnValue.Value = value;
                    _hasReturnValueSet = true;
                } // else
            }
        }

        /// <summary>
        /// Sets the returnvalue in the given JoinPointContext to obj. This static method is usefull
        /// to store a returnvalue that is on the stack, because we cannot directly place the JoinPointContext
        /// object before it on the stack.
        /// </summary>
        /// <param name="obj">The object to store as return value.</param>
        /// <param name="context">The join point context it applies to.</param>
        public static void SetReturnValue(Object obj, JoinPointContext context){
            if(context != null)
            {
                context.ReturnValue = obj;
            }
        }

        #endregion

        #region Context Properties

        /// <summary>
        /// Adds a property to the JoinPointContext, so you can use it in a following FilterAction.
        /// </summary>
        /// <param name="key">The key of the property</param>
        /// <param name="property">The value of the property</param>
        /// <example>
        /// See the following example:
        /// <code>
        /// public override void Execute(JoinPointContext context)
        /// {
        ///     long starttime = DateTime.Now.Ticks;
        ///     context.AddProperty("starttime", starttime);
        /// }
        /// </code>
        /// </example> 
        /// <exception cref="ArgumentNullException">
        /// Thrown when the <paramref name="key"/> is empty or <see langword="null" />.
        /// </exception>
        public void AddProperty(string key, object property)
        {
            if (string.IsNullOrEmpty(key))
            {
                throw new ArgumentNullException("key"); 
            } // if

            _properties[key] = property;
        }

        /// <summary>
        /// Returns a property, or <see langword="null" /> if the property does not exist.
        /// </summary>
        /// <param name="key">The key of the property</param>
        /// <returns>The property, or <see langword="null" /> if the property does not exist.</returns>
        /// <example>
        /// The following example shows a StopTimerAction.
        /// <code>
        /// public override void Execute(JoinPointContext context)
        /// {
        ///     long stoptime = DateTime.Now.Ticks;
        ///     long starttime = (long)context.GetProperty("starttime");
        ///     TimeSpan buildtime = new TimeSpan(stoptime - starttime);
        ///     double timervalue = Convert.ToDouble(buildtime.Milliseconds);
        ///     Console.WriteLine("The execution of message: " + context.GetProperty("target") + "." + 
        ///     context.GetProperty("selector") + " took: " + timervalue + " msecs");
        /// }
        /// </code>
        /// </example>
        /// <exception cref="ArgumentNullException">
        /// Thrown when the <paramref name="key"/> is empty or <see langword="null" />.
        /// </exception>
        public object GetProperty(string key)
        {
            if (string.IsNullOrEmpty(key))
            {
                throw new ArgumentNullException("key");
            } // if

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
        /// Returns a strong typed property, or the default value of <typeparamref name="T"/> if the property does not exist.
        /// </summary>
        /// <param name="key">The key of the property</param>
        /// <typeparam name="T">The type to return.</typeparam> 
        /// <returns>
        /// The property based on the type of T, or default if the property does not exist.
        /// </returns>
        /// <example>
        /// The following example shows a StopTimerAction using the <see cref="M:GetGenericProperty(string)"/> method.
        /// <code>
        /// public override void Execute(JoinPointContext context)
        /// {
        /// long stoptime = DateTime.Now.Ticks;
        /// long starttime = context.GetGenericProperty&lt;long&gt;("starttime");
        /// TimeSpan buildtime = new TimeSpan(stoptime - starttime);
        /// double timervalue = Convert.ToDouble(buildtime.Milliseconds);
        /// Console.WriteLine("The execution of message: " + context.GetProperty("target") + "." +
        /// context.GetProperty("selector") + " took: " + timervalue + " msecs");
        /// }
        /// </code>
        /// </example>
        /// <exception cref="ArgumentNullException">
        /// Thrown when the <paramref name="key"/> is empty or <see langword="null"/>.
        /// </exception>
        public T GetGenericProperty<T>(string key)
        {
            if (string.IsNullOrEmpty(key))
            {
                throw new ArgumentNullException("key");
            } // if

            if (_properties.ContainsKey(key))
            {
                return ((T)_properties[key]);
            }
            else
            {
                return default(T);
            }
        }

        #endregion
      
    }

    #region ArgumentInfo class

    /// <summary>
    /// Internal class for storing the arguments.
    /// </summary>
    public sealed class ArgumentInfo
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

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ArgumentInfo"/> class.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <param name="value">The value.</param>
        /// <param name="argumentAttributes">The argument attributes.</param>
        public ArgumentInfo(Type type, Object value, ArgumentAttributes argumentAttributes)
        {
            _type = type;
            _value = value;
            _argumentAttributes = argumentAttributes;
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

        /// <summary>
        /// Returns true when the argument is an 'in' argument
        /// </summary>
        /// <returns><see langword="true" /> when the argument is an input argument, <see langword="false" /> otherelse.</returns>
        public bool IsIn()
        {
            return (Attributes & ArgumentAttributes.In) == ArgumentAttributes.In;
        }

        /// <summary>
        /// Returns true when the argument is an 'out' argument
        /// </summary>
        /// <returns><see langword="true" /> when the argument is an output argument, <see langword="false" /> otherelse.</returns>
        public bool IsOut()
        {
            return (Attributes & ArgumentAttributes.Out) == ArgumentAttributes.Out;
        }

        private ArgumentAttributes _argumentAttributes;

        /// <summary>
        /// Gets or sets the argument attributes.
        /// </summary>
        /// <value>The argument attributes.</value>
        public ArgumentAttributes Attributes
        {
            get { return _argumentAttributes; }
            set { _argumentAttributes = value; }
        }

    }

    #endregion

    #region Argument Attributes

    /// <summary>
    /// Argument attributes. This is a <see cref="T:System.FlagsAttribute" /> enumeration so multiple options are possible.
    /// </summary>
    [Flags]
    public enum ArgumentAttributes : short
    {
        /// <summary>
        /// Argument is an input argument.
        /// </summary>
        In = 0x0001,
        /// <summary>
        /// Argument is an output argument.
        /// </summary>
        Out = 0x0002,
        /// <summary>
        /// Argument is optional.
        /// </summary>
        Optional = 0x0010,
        /// <summary>
        /// Argument has a default value.
        /// </summary>
        HasDefault = 0x1000,
        /// <summary>
        /// Argument has a field marshal.
        /// </summary>
        HasFieldMarshal = 0x2000
    }

    #endregion
}
