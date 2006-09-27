using System;
using System.Collections.Generic;
using System.Text;

using com.db4o;

using Composestar.Repository.LanguageModel;

namespace Composestar.Repository.Db4oContainers
{
    static class Db4oConfiguration
    {
        private static bool _isInitialized = false;

        public static void Configure()
        {
            if (!_isInitialized)
            {
                Db4o.Configure().CallConstructors(false);

                Db4o.Configure().ObjectClass(typeof(MethodElement)).CascadeOnUpdate(true);

                // Indexes
                Db4o.Configure().ObjectClass(typeof(TypeElement)).ObjectField("_id").Indexed(true);
                Db4o.Configure().ObjectClass(typeof(TypeElement)).ObjectField("_fullName").Indexed(true);
                Db4o.Configure().ObjectClass(typeof(TypeElement)).ObjectField("_AFQN").Indexed(true);
                Db4o.Configure().ObjectClass(typeof(TypeElement)).ObjectField("_fromDLL").Indexed(true);

                Db4o.Configure().ObjectClass(typeof(FieldElement)).ObjectField("_parentTypeId").Indexed(true);

                Db4o.Configure().ObjectClass(typeof(MethodElement)).ObjectField("_parentTypeId").Indexed(true);
                Db4o.Configure().ObjectClass(typeof(MethodElement)).ObjectField("_signature").Indexed(true);

                Db4o.Configure().ObjectClass(typeof(ParameterElement)).ObjectField("_parentMethodId").Indexed(true);

                Db4o.Configure().ObjectClass(typeof(AttributeElement)).ObjectField("_parentId").Indexed(true);
            }
        }
    }
}
