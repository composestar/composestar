package Composestar.DotNET2.TYPEX;

import java.io.BufferedWriter;
import java.io.IOException;

import composestar.dotNET2.tym.entities.MethodElement;

interface MethodEmitter
{
	void emit(MethodElement me, BufferedWriter bw) throws IOException;
}
