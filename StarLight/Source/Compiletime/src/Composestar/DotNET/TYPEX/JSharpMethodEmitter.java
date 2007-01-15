/**
 * 
 */
package Composestar.DotNET.TYPEX;

import java.io.IOException;
import java.io.Writer;

import composestar.dotNET.tym.entities.MethodElement;

class JSharpMethodEmitter implements MethodEmitter
{
	public void emit(MethodElement method, Writer writer) throws IOException
	{
		writer.append(method.toString());
		writer.append("\n");
	}
}