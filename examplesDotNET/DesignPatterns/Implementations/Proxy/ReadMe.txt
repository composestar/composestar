This example demonstrates the Proxy design pattern.

OutputImplementation implements the RealSubject role. The concern BlockerProxy implements the Proxy role and blocks unsafe requests (a protection proxy). The concern CounterProxy implements the Proxy role and counts regular requests.

These two Proxies are directly supported by Compose* because they add behavior to existing methods. With these examples the RealSubject and its Proxy are created simultanously. Proxies that delay RealSubject creation (e.g. a virtual proxy) or if RealSubject is not directly available (e.g. a remote proxy) cannot be implemented this way.



