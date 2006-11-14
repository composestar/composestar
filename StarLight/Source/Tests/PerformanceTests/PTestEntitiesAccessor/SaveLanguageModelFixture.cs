using System;
using System.Collections.Generic;
using System.Text;
using NTime.Framework;

using Composestar.Repository;
using Composestar.StarLight.Entities.Configuration;
using Composestar.StarLight.TestMocks.LanguageModel;

namespace Composestar.Repository.Tests.PerformanceTests
{
    [TimerFixture]
    public class SaveLanguageModelFixture
    {
        EntitiesAccessor entitiesAccessor = null;
        private SimpleLanguageModelMock tinyModel = null;
        private SimpleLanguageModelMock bigModel = null;

        [TimerFixtureSetUp]
        public void GlobalSetUp()
        {
            // initialize one-time initialization data in this testfixture.
            tinyModel = new SimpleLanguageModelMock(1, 10, 0, 6, 0);
            bigModel = new SimpleLanguageModelMock(2, 1200, 10, 40, 0);

            entitiesAccessor = EntitiesAccessor.Instance;
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void SaveTinyLanguageModel()
        {
            foreach (AssemblyConfig assembly in tinyModel.GetLanguageModel())
            {
                if (assembly.Assembly != null)
                    entitiesAccessor.SaveAssemblyElement(assembly.SerializedFileName, assembly.Assembly);

            }
        }

        [TimerDurationTest(30, Unit = TimePeriod.Second)]
        public void SaveBigLanguageModel()
        {
            foreach (AssemblyConfig assembly in bigModel.GetLanguageModel())
            {
                if (assembly.Assembly != null)
                    entitiesAccessor.SaveAssemblyElement(assembly.SerializedFileName, assembly.Assembly);

            }
        }
    }
}
