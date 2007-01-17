package Composestar.DotNET.TYPEX;

import java.io.BufferedWriter;
import java.io.IOException;

import composestar.dotNET.tym.entities.MethodElement;

class CSharpMethodEmitter implements MethodEmitter
{
	public void emit(MethodElement me, BufferedWriter bw) throws IOException
	{
		bw.append(me.toString());
		bw.append("\n");
	}
}