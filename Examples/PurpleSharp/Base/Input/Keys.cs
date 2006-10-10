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

namespace Purple.Input {
	//=================================================================
	/// <summary>
	/// Key enumeration
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	/// </remarks>
	//=================================================================
	public enum Key {
		/// <summary></summary>
		Esc = 1,
		/// <summary></summary>
		N1,
		/// <summary></summary>
		N2,
		/// <summary></summary>
		N3,
		/// <summary></summary>
		N4,
		/// <summary></summary>
		N5,
		/// <summary></summary>
		N6,
		/// <summary></summary>
		N7,
		/// <summary></summary>
		N8,
		/// <summary></summary>
		N9,
		/// <summary></summary>
		N0,
		/// <summary></summary>
		Minus,
		/// <summary></summary>
		Equals,
		/// <summary></summary>
		Back,
		/// <summary></summary>
		Tab,
		/// <summary></summary>
		Q,
		/// <summary></summary>
		W,
		/// <summary></summary>
		E,
		/// <summary></summary>
		R,
		/// <summary></summary>
		T,
		/// <summary></summary>
		Y,
		/// <summary></summary>
		U,
		/// <summary></summary>
		I,
		/// <summary></summary>
		O,
		/// <summary></summary>
		P,
		/// <summary></summary>
		LeftBracket,
		/// <summary></summary>
		RightBracket,
		/// <summary></summary>
		Return,
		/// <summary></summary>
		LeftControl,
		/// <summary></summary>
		A,
		/// <summary></summary>
		S,
		/// <summary></summary>
		D,
		/// <summary></summary>
		F,
		/// <summary></summary>
		G,
		/// <summary></summary>
		H,
		/// <summary></summary>
		J,
		/// <summary></summary>
		K,
		/// <summary></summary>
		L,
		/// <summary></summary>
		Semicolon,
		/// <summary></summary>
		Apostrophe,
		/// <summary></summary>
		Grave,
		/// <summary></summary>
		LeftShift,
		/// <summary></summary>
		BackSlash,
		/// <summary></summary>
		Z,
		/// <summary></summary>
		X,
		/// <summary></summary>
		C,
		/// <summary></summary>
		V,
		/// <summary></summary>
		B,
		/// <summary></summary>
		N,
		/// <summary></summary>
		M,
		/// <summary></summary>
		Comma,
		/// <summary></summary>
		Period,
		/// <summary></summary>
		Slash,
		/// <summary></summary>
		RightShift,
		/// <summary></summary>
		Multiply,
		/// <summary></summary>
		LeftAlt,
		/// <summary></summary>
		Space,
		/// <summary></summary>
		Capital,
		/// <summary></summary>
		F1,
		/// <summary></summary>
		F2,
		/// <summary></summary>
		F3,
		/// <summary></summary>
		F4,
		/// <summary></summary>
		F5,
		/// <summary></summary>
		F6,
		/// <summary></summary>
		F7,
		/// <summary></summary>
		F8,
		/// <summary></summary>
		F9,
		/// <summary></summary>
		F10,
    /// <summary></summary>
    F11,
    /// <summary></summary>
    F12,
    /// <summary></summary>
    F13,
    /// <summary></summary>
    F14,
    /// <summary></summary>
    F15,
		/// <summary></summary>
		NumLock,
		/// <summary></summary>
		Scroll,
		/// <summary></summary>
		Num7,
		/// <summary></summary>
		Num8,
		/// <summary></summary>
		Subtract,
		/// <summary></summary>
		Num4,
		/// <summary></summary>
		Num5,
		/// <summary></summary>
		Num6,
		/// <summary></summary>
		Add,
		/// <summary></summary>
		Num1,
		/// <summary></summary>
		Num2,
		/// <summary></summary>
		Num3,
		/// <summary></summary>
		Num0,
		/// <summary></summary>
		Decimal,
		/// <summary></summary>
		OEM102,
		/// <summary></summary>
		Kana,
		/// <summary></summary>
		AbntC1,
		/// <summary></summary>
		Convert,
		/// <summary></summary>
		NoConvert,
		/// <summary></summary>
		Yen,
		/// <summary></summary>
		AnbtC2,
		/// <summary></summary>
		NumEquals,
		/// <summary></summary>
		PrevTrack,
		/// <summary></summary>
		At,
		/// <summary></summary>
		Colon,
		/// <summary></summary>
		Underline,
		/// <summary></summary>
		Kanji,
		/// <summary></summary>
		Stop,
		/// <summary></summary>
		Ax,
		/// <summary></summary>
		Unlabeled,
		/// <summary></summary>
		NextTrack,
		/// <summary></summary>
		Enter,
		/// <summary></summary>
		RightControl,
		/// <summary></summary>
		Mute,
		/// <summary></summary>
		Calculator,
		/// <summary></summary>
		PlayPause,
		/// <summary></summary>
		MediaStop,
		/// <summary></summary>
		VolumeDown,
		/// <summary></summary>
		VolumeUp,
		/// <summary></summary>
		WebHome,
		/// <summary></summary>
		NumComma,
		/// <summary></summary>
		Divide,
		/// <summary></summary>
		SysRQ,
		/// <summary></summary>
		RightAlt,
		/// <summary></summary>
		Pause,
		/// <summary></summary>
		Home,
		/// <summary></summary>
		Up,
		/// <summary></summary>
		Prior,
		/// <summary></summary>
		Left,
		/// <summary></summary>
		Right,
		/// <summary></summary>
		End,
		/// <summary></summary>
		Down,
		/// <summary></summary>
		Next,
		/// <summary></summary>
		Insert,
		/// <summary></summary>
		Delete,
		/// <summary></summary>
		LeftWin,
		/// <summary></summary>
		RightWin,
		/// <summary></summary>
		Apps,
		/// <summary></summary>
		Power,
		/// <summary></summary>
		Sleep,
		/// <summary></summary>
		Wake,
		/// <summary></summary>
		WebSearch,
		/// <summary></summary>
		WebFavorites,
		/// <summary></summary>
		WebRefresh,
		/// <summary></summary>
		WebStop,
		/// <summary></summary>
		WebForward,
		/// <summary></summary>
		WebBack,
		/// <summary></summary>
		MyComputer,
		/// <summary></summary>
		Mail,
		/// <summary></summary>
		MediaSelect,		
	}
}
