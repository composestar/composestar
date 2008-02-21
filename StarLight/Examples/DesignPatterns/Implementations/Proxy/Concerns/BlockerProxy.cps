concern BlockerProxy{
	filtermodule BlockerProxyFM{
		internals
			blocker : Composestar.Patterns.Proxy.RequestBlocker;

		inputfilters
			block : Dispatch = { [*.unsafeRequest] blocker.unsafeRequest}
	}
	superimposition{
		selectors
			realSubject = { RealSubject | classHasAnnotationWithName(RealSubject, 'Composestar.Patterns.Proxy.Annotations.RealSubject') };
			
		filtermodules
			realSubject <- BlockerProxyFM;
	}
	implementation in JSharp by	RequestBlocker as "RequestBlocker.jsl"
	{
		package Composestar.Patterns.Proxy;

		public class RequestBlocker{

			public void unsafeRequest(String s) {
				System.out.println("[RequestBlocker:] " + s + " blocked.");		
			}
		}
	}
}