package Composestar.DotNET.TYPEX;

import java.io.BufferedWriter;
import java.io.IOException;

import composestar.dotNET.tym.entities.MethodElement;

interface MethodEmitter
{
	void emit(MethodElement me, BufferedWriter bw) throws IOException;
}