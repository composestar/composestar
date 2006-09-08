concern CounterProxy{
	filtermodule CounterProxyFM{
		internals
			counter : Composestar.Patterns.Proxy.RequestCounter;

		inputfilters
			count : Meta = { [*.regularRequest] counter.regularRequest}
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

		import Composestar.RuntimeCore.FLIRT.Message.ReifiedMessage;

		public class RequestCounter{

			private int regularRequests = 0;

			public void regularRequest(ReifiedMessage message) {
				regularRequests++;
				System.out.println("[RequestCounter:] That was regular request nr. " +
					regularRequests);		
			}
		}
	}
}