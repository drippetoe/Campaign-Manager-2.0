using System;
using System.Security.Cryptography;
using System.Text;
using System.Web;

/**
 * @Copyright 2012, Proximus Mobility, LLC., all rights reserved
 * @author dshaw
 */
namespace ProximusTokenExample
{
	class ProximusToken
	{
		string myCompanySalt;
		
		public ProximusToken (string myCompanySalt)
		{
			this.myCompanySalt = myCompanySalt;
		}
		
		public string generateAuthenticationToken (string url)
		{
			using (MD5 md5Hash = MD5.Create()) {
				byte[] data = md5Hash.ComputeHash (Encoding.UTF8.GetBytes (this.myCompanySalt + url));
				StringBuilder sBuilder = new StringBuilder ();
				
				for (int i = 0; i < data.Length; i++) {
					sBuilder.Append (data [i].ToString ("x2"));
				}
				return sBuilder.ToString ().ToUpper ();
			}
		}
	}
	
	class MainClass
	{
		public static void Main (string[] args)
		{
			ProximusToken tokenGenerator = new ProximusToken ("MYCOMPANYISAWESOME");
				
			string URL_BASE = "http://devices.proximusmobility.com/api/";
			string username = HttpUtility.UrlEncode ("user@domain.com");
			string password = "iwadasnin2012wisawuts";
            
			string encodingUrl = URL_BASE + username + "/" + password + "/params1/params2/params..n";
			string token = tokenGenerator.generateAuthenticationToken (encodingUrl);
			string requestUrl = URL_BASE + username + "/" + token + "/params1/params2/params..n";
        
			Console.WriteLine ("Raw URL was " + encodingUrl);
			Console.WriteLine ("Token would be " + token);
			Console.WriteLine ("Request URL would be " + requestUrl); 
		}
	}
}
