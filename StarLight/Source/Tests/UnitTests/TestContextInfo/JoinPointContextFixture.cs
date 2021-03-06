// The following code was generated by Microsoft Visual Studio 2005.
// The test owner should check each test for validity.
#region Using directives
#if !NUNIT
using Microsoft.VisualStudio.TestTools.UnitTesting;
#else
using NUnit.Framework;
using TestClass = NUnit.Framework.TestFixtureAttribute;
using TestInitialize = NUnit.Framework.SetUpAttribute;
using TestCleanup = NUnit.Framework.TearDownAttribute;
using TestMethod = NUnit.Framework.TestAttribute;
#endif
using System;
using System.Text;
using System.Collections.Generic;
#endregion

namespace Composestar.StarLight.ContextInfo.UnitTests
{
    /// <summary>
    ///This is a test class for Composestar.StarLight.ContextInfo.JoinPointContext and is intended
    ///to contain all Composestar.StarLight.ContextInfo.JoinPointContext Unit Tests
    ///</summary>
    [TestClass()]
    public class JoinPointContextFixture
    {

#if !NUNIT
        private TestContext testContextInstance;

        /// <summary>
        ///Gets or sets the test context which provides
        ///information about and functionality for the current test run.
        ///</summary>
        public TestContext TestContext
        {
            get
            {
                return testContextInstance;
            }
            set
            {
                testContextInstance = value;
            }
        }
#endif

        #region Additional test attributes
        // 
        //You can use the following additional attributes as you write your tests:
        //
        //Use ClassInitialize to run code before running the first test in the class
        //
        //[ClassInitialize()]
        //public static void MyClassInitialize(TestContext testContext)
        //{
        //}
        //
        //Use ClassCleanup to run code after all tests in a class have run
        //
        //[ClassCleanup()]
        //public static void MyClassCleanup()
        //{
        //}
        //
        //Use TestInitialize to run code before running each test
        //
        //[TestInitialize()]
        //public void MyTestInitialize()
        //{
        //}
        //
        //Use TestCleanup to run code after each test has run
        //
        //[TestCleanup()]
        //public void MyTestCleanup()
        //{
        //}
        //
        #endregion


        /// <summary>
        /// Constructor test
        /// </summary>
        [TestMethod]
        public void ConstructorTest()
        {
            global::Composestar.StarLight.ContextInfo.JoinPointContext target = new global::Composestar.StarLight.ContextInfo.JoinPointContext();

            Assert.IsNotNull(target, "Could not create JPC"); 
        }


        /// <summary>
        /// Argumentses the test.
        /// </summary>
        [TestMethod ]
	    public void ArgumentsTest()
        {
            global::Composestar.StarLight.ContextInfo.JoinPointContext target = new global::Composestar.StarLight.ContextInfo.JoinPointContext();
            target.AddArgument(0, typeof(string), "ss");

            Assert.AreEqual(typeof(string), target.GetArgumentType(0), "Could not retrieve argumenttype");
            Assert.AreEqual("ss", (string) target.GetArgumentValue(0), "Could not retrieve argumentvalue");
            Assert.IsNotNull(target.GetArgumentInfo(0), "Could not retrieve ArgumentInfo");
            Assert.AreEqual("ss", target.GetGenericArgumentValue<string>(0), "Could not retrieve generic argumentvalue");

            global::Composestar.StarLight.ContextInfo.JoinPointContext.AddArgument("ss", 1, typeof(string), Composestar.StarLight.ContextInfo.ArgumentAttributes.In , target);
  
            Assert.AreEqual(typeof(string), target.GetArgumentType(1), "Could not retrieve argumenttype");
            Assert.AreEqual("ss", (string) target.GetArgumentValue(1), "Could not retrieve argumentvalue");
            Assert.IsNotNull(target.GetArgumentInfo(1), "Could not retrieve ArgumentInfo");
            Assert.AreEqual("ss", target.GetGenericArgumentValue<string>(1), "Could not retrieve generic argumentvalue");

            global::Composestar.StarLight.ContextInfo.JoinPointContext.AddArgument("ss", 2, typeof(string), target);

            Assert.AreEqual(typeof(string), target.GetArgumentType(2), "Could not retrieve argumenttype");
            Assert.AreEqual("ss", (string)target.GetArgumentValue(2), "Could not retrieve argumentvalue");
            Assert.IsNotNull(target.GetArgumentInfo(2), "Could not retrieve ArgumentInfo");
            Assert.AreEqual("ss", target.GetGenericArgumentValue<string>(2), "Could not retrieve generic argumentvalue");



        }

        [TestMethod]
        [ExpectedExceptionAttribute(typeof(ArgumentNullException)) ]
        public void PropertyShouldTrowExceptionIfKeyIsEmpty()
        {
            global::Composestar.StarLight.ContextInfo.JoinPointContext target = new global::Composestar.StarLight.ContextInfo.JoinPointContext();
            target.AddProperty("", new object());
            Assert.Fail("did not fail");
            target.GetProperty("");
            Assert.Fail("did not fail"); 
        }

        /// <summary>
        /// Properties the context test.
        /// </summary>
        [TestMethod]
        public void PropertiesTest()
        {
              global::Composestar.StarLight.ContextInfo.JoinPointContext target = new global::Composestar.StarLight.ContextInfo.JoinPointContext();

            string key = "test";
            string obj = "a";

            target.AddProperty(key, obj);

            Assert.IsNotNull(target.GetProperty(key), "Could not retrieve property");
            Assert.AreEqual(obj, ((string) target.GetProperty(key)), "Property does not contain the stored object");
            Assert.AreEqual(obj, target.GetGenericProperty<string>(key), "Property does not contain the stored object");

            // Overwrite test
            int obj2 = 4;

            target.AddProperty(key, obj2);

            Assert.IsNotNull(target.GetProperty(key), "Could not retrieve property");
            Assert.AreEqual(obj2, ((int)target.GetProperty(key)), "Property does not contain the stored object");
            Assert.AreEqual(obj2, target.GetGenericProperty<int>(key), "Property does not contain the stored object");

        }

        /// <summary>
        ///A test for AddArgument (short, Type, object)
        ///</summary>
        [TestMethod()]
        public void AddArgumentTest()
        {
            global::Composestar.StarLight.ContextInfo.JoinPointContext target = new global::Composestar.StarLight.ContextInfo.JoinPointContext();

            short ordinal = 1; 

            global::System.Type argumentType = typeof(string);

            object value = "123"; 

            target.AddArgument(ordinal, argumentType, value);

            Assert.AreEqual(argumentType, target.GetArgumentType(ordinal));
            Assert.AreEqual(value, target.GetArgumentValue(ordinal));            
        }

        /// <summary>
        ///A test for GetArgumentType (short)
        ///</summary>
        [TestMethod()]
		[ExpectedExceptionAttribute(typeof(System.ArgumentOutOfRangeException))]
        public void GetArgumentTypeTest()
        {
            global::Composestar.StarLight.ContextInfo.JoinPointContext target = new global::Composestar.StarLight.ContextInfo.JoinPointContext();

            short ordinal = 0; 
            global::System.Type expected = typeof(int);
            global::System.Type actual;

            target.AddArgument(ordinal, expected, null); 

            actual = target.GetArgumentType(ordinal);

            Assert.AreEqual(expected, actual, "Composestar.StarLight.ContextInfo.JoinPointContext.GetArgumentType did not return" +
                    " the expected value.");
			actual = target.GetArgumentType(10);
            Assert.Fail("Should return null for ordinals not found"); 
        }

        /// <summary>
        ///A test for GetArgumentValue (short)
        ///</summary>
        [TestMethod()]
		[ExpectedExceptionAttribute(typeof(System.ArgumentOutOfRangeException))]
        public void GetArgumentValueTest()
        {
            global::Composestar.StarLight.ContextInfo.JoinPointContext target = new global::Composestar.StarLight.ContextInfo.JoinPointContext();

            short ordinal = 0; 

            object expected = "123";
            object actual;

            target.AddArgument(ordinal, typeof(short), expected); 
            actual = target.GetArgumentValue(ordinal);

            Assert.AreEqual(expected, actual, "Composestar.StarLight.ContextInfo.JoinPointContext.GetArgumentValue did not return the expected value.");

			actual = target.GetArgumentValue(1);
			Assert.Fail( "Should return null for ordinals not found");
        }

        /// <summary>
        ///A test for HasReturnValue
        ///</summary>
        [TestMethod()]
        public void HasReturnValueCheck()
        {
            global::Composestar.StarLight.ContextInfo.JoinPointContext target = new global::Composestar.StarLight.ContextInfo.JoinPointContext();

            bool val = false; 
            
            Assert.AreEqual(val, target.HasReturnValue, "Composestar.StarLight.ContextInfo.JoinPointContext.HasReturnValue was not set correctly.");

         }

        
        /// <summary>
        ///A test for CurrentSelector
        ///</summary>
        [TestMethod()]
        public void CurrentSelectorPersists()
        {
            global::Composestar.StarLight.ContextInfo.JoinPointContext target = new global::Composestar.StarLight.ContextInfo.JoinPointContext();

            string val = "TestMethodName";

            target.CurrentSelector  = val;


            Assert.AreEqual(val, target.CurrentSelector, "Composestar.StarLight.ContextInfo.JoinPointContext.CurrentSelector was not set correctly.");            
        }

        /// <summary>
        ///A test for ReturnType
        ///</summary>
        [TestMethod()]
        public void ReturnTypePersists()
        {
            global::Composestar.StarLight.ContextInfo.JoinPointContext target = new global::Composestar.StarLight.ContextInfo.JoinPointContext();

            global::System.Type val = typeof(double);

            target.ReturnType = val;


            Assert.AreEqual(val, target.ReturnType, "Composestar.StarLight.ContextInfo.JoinPointContext.ReturnType was not set correctly.");            
        }

        /// <summary>
        ///A test for ReturnValue
        ///</summary>
        [TestMethod()]
        [ExpectedException(typeof(ArgumentNullException)) ]
        public void ReturnValueMustRaiseExceptionWhenNoTypeHasBeenSet()
        {
            global::Composestar.StarLight.ContextInfo.JoinPointContext target = new global::Composestar.StarLight.ContextInfo.JoinPointContext();

            object val = "234"; 

            target.ReturnValue = val;
            
            
        }

        /// <summary>
        ///A test for ReturnValue
        ///</summary>
        [TestMethod()]
        public void ReturnValueCanBeSetWhenTypeHasBeenSet()
        {
            global::Composestar.StarLight.ContextInfo.JoinPointContext target = new global::Composestar.StarLight.ContextInfo.JoinPointContext();

            object val = "234"; 
            Type t = typeof(string);

            target.ReturnType = t; 

            target.ReturnValue = val;

            Assert.AreEqual(val, target.ReturnValue); 
            
        }

        /// <summary>
        ///A test for Target
        ///</summary>
        [TestMethod()]
        public void TargetTest()
        {
            global::Composestar.StarLight.ContextInfo.JoinPointContext target = new global::Composestar.StarLight.ContextInfo.JoinPointContext();

            object val = "tete";

            target.CurrentTarget = val;
            target.StartTarget = val;

            Assert.AreEqual(val, target.CurrentTarget, "Composestar.StarLight.ContextInfo.JoinPointContext.CurrentTarget was not set correctly.");
            Assert.AreEqual(val, target.StartTarget, "Composestar.StarLight.ContextInfo.JoinPointContext.StartTarget was not set correctly.");
  
        }


        [TestMethod()]
        public void SenderTest()
        {
            global::Composestar.StarLight.ContextInfo.JoinPointContext target = new global::Composestar.StarLight.ContextInfo.JoinPointContext();

            object val = "tete";

            target.Sender = val;


            Assert.AreEqual(val, target.Sender, "Composestar.StarLight.ContextInfo.JoinPointContext.Sender was not set correctly.");
            
        }

    }


}
