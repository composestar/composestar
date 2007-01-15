/**
 * 
 */
package Composestar.DotNET.TYPEX;

import java.io.IOException;
import java.io.Writer;

import composestar.dotNET.tym.entities.MethodElement;

interface MethodEmitter
{
	void emit(MethodElement method, Writer writer) throws IOException;
}