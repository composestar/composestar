using System;
using System.IO;
using System.Runtime.InteropServices;
using System.Text;
using System.Collections;

namespace Ini
{
	/// <summary>
	/// Create a New INI file to store or load data
	/// </summary>
	public class IniFile
	{
		public string path;

		[DllImport("kernel32")]
		private static extern long WritePrivateProfileString(string section,string key,string val,string filePath);
		[DllImport("kernel32")]
		private static extern int GetPrivateProfileString(string section,string key,string def,StringBuilder retVal,int size,string filePath);
		
		[DllImport("kernel32")]
		private static extern int GetPrivateProfileSection(string section, Byte[] retVal, int size, string filePath);

		/// <summary>
		/// INIFile Constructor.
		/// </summary>
		/// <param name="INIPath"></param>
		public IniFile(string INIPath)
		{
			path = INIPath;
		}
		/// <summary>
		/// Write Data to the INI File
		/// </summary>
		/// <param name="Section"></param>
		/// Section name
		/// <param name="Key"></param>
		/// Key Name
		/// <param name="Value"></param>
		/// Value Name
		public void IniWriteValue(string Section,string Key,string Value)
		{
			WritePrivateProfileString(Section,Key,Value,this.path);
		}
		
		/// <summary>
		/// Read Data Value From the Ini File
		/// </summary>
		/// <param name="Section"></param>
		/// <param name="Key"></param>
		/// <param name="Path"></param>
		/// <returns></returns>
		public string IniReadValue(string Section,string Key)
		{
			StringBuilder temp = new StringBuilder(512);
			int i = GetPrivateProfileString(Section,Key,"",temp,512,this.path);
			return temp.ToString();

		}

		public string[] IniReadSection( string Section ) 
		{
			
			System.Collections.ArrayList ret = new System.Collections.ArrayList();
			Byte[] info = new Byte[65536];
			int count = GetPrivateProfileSection( Section, info, 65536, this.path );
			for( int i = 0; i < info.Length; i++ ) 
			{
				if( info[i] == '\0' ) 
				{
					break;
				}
				System.Text.StringBuilder key = new StringBuilder();
				System.Text.StringBuilder val = new StringBuilder();
				bool inValue = false;
				
				while( info[i] != '\0' ) 
				{
					if( info[i] == '=' ) 
					{
						inValue = true;
					}
					else if( !inValue )
					{
						key.Append( (char) info[i] );
					}
					else 
					{
						val.Append( (char) info[i] );
					}

					i++;
				}
				
				if( !key.ToString().StartsWith( "#" ) )
					ret.Add( val.ToString() );
			}
			string dummy = "";
			return (string[]) ret.ToArray( dummy.GetType() );
		}
	}
}
