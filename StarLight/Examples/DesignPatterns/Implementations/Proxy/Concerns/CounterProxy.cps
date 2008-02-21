concern CounterProxy{
	filtermodule CounterProxyFM{
		internals
			counter : Composestar.Patterns.Proxy.RequestCounter;

		inputfilters
			count : Before = { [*.regularRequest] counter.regularRequest}
	}
	superimposition{
		selectors
			realSubject = { RealSubject | classHasAnnotationWithName(RealSubject, 'Composestar.Patterns.Proxy.Annotations.RealSubject') };
			
		filtermodules
			realSubject <- CounterProxyFM;
	}
	implementation in JSharp by	RequestCounter as "RequestCounter.jsl"
	{
		package Composestar.Patterns.Proxy;

		import Composestar.StarLight.ContextInfo.JoinPointContext;

		public class RequestCounter{

			private int regularRequests = 0;

			public void regularRequest(JoinPointContext jpc) {
				regularRequests++;
				System.out.println("[RequestCounter:] That was regular request nr. " +
					regularRequests);		
			}
		}
	}
}