//*****************************************************************************
//     ____                              ___                __ __      
//    /\  _`\                           /\_ \              _\ \\ \__   
//    \ \ \L\ \ __  __   _ __   _____   \//\ \       __   /\__  _  _\  
//     \ \ ,__//\ \/\ \ /\`'__\/\ '__`\   \ \ \    /'__`\ \/_L\ \\ \L_ 
//      \ \ \/ \ \ \_\ \\ \ \/ \ \ \L\ \   \_\ \_ /\  __/   /\_   _  _\
//       \ \_\  \ \____/ \ \_\  \ \ ,__/   /\____\\ \____\  \/_/\_\\_\/
//        \/_/   \/___/   \/_/   \ \ \/    \/____/ \/____/     \/_//_/ 
//                                \ \_\                                
//                                 \/_/                                            
//                  Purple# - The smart way of programming games
#region //
// Copyright (c) 2002-2003 by 
//   Markus Wöß
//   Bunnz@Bunnz.com
//   http://www.bunnz.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
#endregion
//*****************************************************************************
using System;
using System.Runtime.InteropServices;

namespace Purple.Tools
{
  //=================================================================
  /// <summary>
  /// Some memory tools.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>
  /// </remarks>
  //=================================================================
  public class Memory {
    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Copies a certain value multiple times to a certain ptr.
    /// </summary>
    /// <param name="target">The target pointer.</param>
    /// <param name="value">The value to copy.</param>
    /// <param name="count">The number of values to copy.</param>
    public static unsafe void BlockCopy(IntPtr target, int value, int count) {
      if ((count & 1) == 0) {
        ulong l = (ulong)value;
        l = l << 32;
        l = l + (l>>32);
        BlockCopy( target, (long)l, count / 2 );
      }
      else {
        int *p = (int *)target.ToPointer();
        for (int i=0; i<count; i++) {
          *p = value;
          p++;
        }
      }
    }

    /// <summary>
    /// Copies a certain value multiple times to a certain ptr.
    /// </summary>
    /// <param name="target">The target pointer.</param>
    /// <param name="value">The value to copy.</param>
    /// <param name="count">The number of values to copy.</param>
    public static unsafe void BlockCopy(IntPtr target, long value, int count) {
      long *p = (long *)target.ToPointer();
      for (long i=0; i<count; i++) {
        *p = value;
        p++;
      }
    }

    /// <summary>
    /// Copies a certain memory block to another block.
    /// </summary>
    /// <param name="source">The source array.</param>
    /// <param name="sourceOffset">The source offset in bytes.</param>
    /// <param name="target">The target memory pointer.</param>
    /// <param name="targetOffset">The target offset in bytes.</param>
    /// <param name="count">The number of bytes to copy.</param>
    public static unsafe void BlockCopy( Array source, int sourceOffset, IntPtr target, int targetOffset, int count) {
      GCHandle handle = GCHandle.Alloc( source, GCHandleType.Pinned );
      IntPtr sourcePtr = handle.AddrOfPinnedObject();
      int size = Marshal.SizeOf(source.GetType().GetElementType());
      BlockCopy( sourcePtr, sourceOffset*size, target, targetOffset*size, count*size);
      handle.Free();
    }

    /// <summary>
    /// Copies a certain memory block to another block.
    /// </summary>
    /// <param name="source">The source memory pointer.</param>
    /// <param name="sourceOffset">The source offset in bytes.</param>
    /// <param name="target">The target memory pointer.</param>
    /// <param name="targetOffset">The target offset in bytes.</param>
    /// <param name="count">The number of bytes to copy.</param>
    public static unsafe void BlockCopy( IntPtr source, int sourceOffset, IntPtr target, int targetOffset, int count) {
      if ((count & 7) == 0 && (sourceOffset & 7) == 0 && (targetOffset & 7) == 0) {
        // fast path
        int qwords = count/8;
        long *pSrc = (long *)source.ToPointer();
        pSrc += sourceOffset/8;
        long *pTarget = (long *)target.ToPointer();
        pTarget += targetOffset/8;
        for (int i=0; i<qwords; i++) {
          *pTarget = *pSrc;
          pTarget++;
          pSrc++;
        }
      } else if ((count & 3) == 0 && (sourceOffset & 3) == 0 && (targetOffset & 3) == 0) {
        // fast path
        int dwords = count/4;
        int *pSrc = (int *)source.ToPointer();
        pSrc += sourceOffset/4;
        int *pTarget = (int *)target.ToPointer();
        pTarget += targetOffset/4;
        for (int i=0; i<dwords; i++) {
          *pTarget = *pSrc;
          pTarget++;
          pSrc++;
        }
      } else {
        // slow path
        byte *pSrc = (byte *)source.ToPointer();
        pSrc += sourceOffset;
        byte *pTarget = (byte *)target.ToPointer();
        pTarget += targetOffset;
        for (int i=0; i<count; i++) {
          *pTarget = *pSrc;
          pTarget++;
          pSrc++;
        }
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
