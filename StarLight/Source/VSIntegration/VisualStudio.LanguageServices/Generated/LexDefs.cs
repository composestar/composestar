/***************************************************************************

Copyright (c) Microsoft Corporation. All rights reserved.
This code is licensed under the Visual Studio SDK license terms.
THIS CODE IS PROVIDED *AS IS* WITHOUT WARRANTY OF
ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING ANY
IMPLIED WARRANTIES OF FITNESS FOR A PARTICULAR
PURPOSE, MERCHANTABILITY, OR NON-INFRINGEMENT.

***************************************************************************/

using System;

namespace Composestar.StarLight.VisualStudio.LanguageServices.Parser
{
    //
    // These are the dummy declarations for stand-alone lex applications
    // normally these declarations would come from the parser.
    // 

    public interface IColorScan
    {
        void SetSource(string source, int offset);
        int GetNext(ref int state, out int start, out int end);
    }

    public interface IErrorHandler
    {
        int ErrNum { get; }
        int WrnNum { get; }
        void AddError(string msg, int lin, int col, int len, int severity);
    }
}
