/*
 * This file is part of WeaveC project [http://weavec.sf.net].
 * Copyright (C) 2005 University of Twente.
 *
 * Licensed under the BSD License.
 * [http://www.opensource.org/licenses/bsd-license.php]
 * 
 * Redistribution and use in source and binary forms, with or without
   modification, are permitted provided that the following conditions
   are met:
 * 1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the University of Twente nor the names of its 
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY AUTHOR AND CONTRIBUTORS ``AS IS'' AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODSOR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * $Id: ParserTest.java,v 1.1 2006/03/16 14:08:54 johantewinkel Exp $
 */
package Composestar.C.wrapper.parsing;

import java.io.*;

import antlr.*;

public class ParserTest
{
    public static void main(String[] args)
    {
        for (int i=0; i<args.length; i++)
        {
        try
            {
            String programName = args[i];
//      System.out.println("\nworking on " + programName);
//      System.out.flush();
//      System.err.println("\nworking on " + programName);
//      System.err.flush();
            DataInputStream dis = null;
            if (programName.equals("-")) {
                dis = new DataInputStream( System.in );
            }   
            else {
                dis = new DataInputStream(new FileInputStream(programName));
            }
            GnuCLexer lexer =
                new GnuCLexer ( dis );
            lexer.setTokenObjectClass("Composestar.C.wrapper.parsing.CToken");
            lexer.initialize();
            // Parse the input expression.
            GnuCParser parser = new GnuCParser ( lexer );
            
            // set AST node type to TNode or get nasty cast class errors
            parser.setASTNodeType(TNode.class.getName());
            TNode.setTokenVocabulary("Composestar.C.wrapper.parsing.GnuCTokenTypes");

            // invoke parser
            try {
                parser.translationUnit();
            }
            catch (RecognitionException e) {
                System.err.println("Fatal IO error:\n"+e);
                System.exit(1);
            }
            catch (TokenStreamException e) {
                System.err.println("Fatal IO error:\n"+e);
                System.exit(1);
            }

            // Garbage collection hint
            System.gc();
            
//      System.out.println(lexer.getPreprocessorInfoChannel());
//    TNode.printTree(parser.getAST());      System.out.flush();  

            // Garbage collection hint
            System.gc();

            /*GnuCEmitter e = new GnuCEmitter(lexer.getPreprocessorInfoChannel());
            
            // set AST node type to TNode or get nasty cast class errors
            e.setASTNodeType(TNode.class.getName());

            // walk that tree
            e.translationUnit( parser.getAST() );

            // Garbage collection hint
            System.gc();*/

            }
        catch ( Exception e )
            {
            System.err.println ( "exception: " + e);
            e.printStackTrace();
            }
        }
    }
}

