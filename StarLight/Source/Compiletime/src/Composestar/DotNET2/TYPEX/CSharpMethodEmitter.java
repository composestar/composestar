package Composestar.DotNET2.TYPEX;

import java.io.BufferedWriter;
import java.io.IOException;

import composestar.dotNET2.tym.entities.MethodElement;

class CSharpMethodEmitter implements MethodEmitter
{
	public void emit(MethodElement me, BufferedWriter bw) throws IOException
	{
		bw.append(me.toString());
		bw.append("\n");
	}
}
