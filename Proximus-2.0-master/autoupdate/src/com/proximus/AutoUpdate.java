package com.proximus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.google.gson.Gson;
import com.proximus.common.AppNode;
import com.proximus.common.AppNodeSoftware;
import com.proximus.common.AppNodeSoftwareFile;
import com.proximus.configuration.ClientConfiguration;
import com.proximus.configuration.ProximusConfigurator;

public class AutoUpdate
{
	protected static Logger logger = Logger.getLogger("Proximus.AutoUpdate");

	public AutoUpdate() {
	}

	/**
	 * Sends an HTTP GET request to a url
	 * 
	 * @param endpoint
	 *            - The URL of the server. (Example:
	 *            " http://www.yahoo.com/search")
	 * @param requestParameters
	 *            - all the request parameters (Example:
	 *            "param1=val1&param2=val2"). Note: This method will add the
	 *            question mark (?) to the request - DO NOT add it yourself
	 * @return - The response from the end point
	 */
	public static String sendGetRequest(String endpoint, String requestParameters)
	{
		String result = null;
		if (endpoint.startsWith("http://"))
		{
			// Send a GET request to the servlet
			try
			{
				// Send data
				String urlStr = endpoint;
				if (requestParameters != null && requestParameters.length() > 0)
				{
					urlStr += "?" + requestParameters;
				}

				URL url = new URL(urlStr);
				URLConnection conn = url.openConnection();
				// Get the response
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuffer sb = new StringBuffer();
				String line;
				while ((line = rd.readLine()) != null)
				{
					sb.append(line);
				}

				rd.close();
				result = sb.toString();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		return result;
	}

	public static void getBinaryFileRequest(String uri, String targetUri)
	{
		try
		{
			// -- download the file...
			URL u = new URL(uri);
			URLConnection uc = u.openConnection();
			String contentType = uc.getContentType();
			int contentLength = uc.getContentLength();

			final int BUFF_SIZE = 1024;
			byte[] buffer = new byte[BUFF_SIZE];

			// -- setup the input source...
			InputStream in = new BufferedInputStream(uc.getInputStream());

			// -- setup the output target...
			String filename = targetUri + "/"
			        + u.getFile().substring(u.getFile().lastIndexOf('/') + 1);
			logger.debug("dumping contents to file: " + filename);
			BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(filename), BUFF_SIZE);

			logger.debug("starting download process...");
			int byteCount = 0;
			long totalBytesRead = 0;
			try
			{
				do
				{
					byteCount = in.read(buffer);
					totalBytesRead += byteCount;
					if (byteCount == -1) break;
					bout.write(buffer, 0, byteCount);
					bout.flush();
				}
				while (true);
			}
			catch (Exception excp)
			{
				excp.printStackTrace();
			}
			finally
			{
				bout.close();
				in.close();
			}
			
			if ( contentLength != 0 && contentLength != totalBytesRead)
			{
				logger.error("Actual Bytes Read doesn't match expected length");
			}


			in.close();
			bout.flush();
			bout.close();
		}
		catch (Exception exp)
		{
			exp.printStackTrace();
		}
	}

	public static void main(String[] args)
	{

		try
		{
			String resourcePath = "/root/proximus/bin/resources";
			if (args.length != 0) resourcePath = args[0];

			// BasicConfigurator.configure(/* new FileAppender(new
			// PatternLayout("%d [%t] %-5p %c - %m%n"),
			// "./proximus-aupdate.log") */);
			PropertyConfigurator.configure("appUpdate.prop");

			ProximusConfigurator.resourcePath(resourcePath);
			ClientConfiguration configs = ClientConfiguration.getInstance();
			logger.debug("Current configuration version: " + configs.getVersionInfo().getMajor()
			        + "." + configs.getVersionInfo().getMinor() + "."
			        + configs.getVersionInfo().getBuild());

			String autoUpdateResponse = sendGetRequest(
			        "http://appserver-dev.proximusmobility.net:8989/sysops/D0001", null);
			Gson gson = new Gson();
			SysOpsResponse response = gson.fromJson(autoUpdateResponse, SysOpsResponse.class);
			if (response == null)
			{
				logger.debug("Could not convert response!");
			}
			else
			{
				AppNodeResponse appResponse = response.getAppNodes();
				if (appResponse == null)
				{
					logger.debug("AppNodeResponse is not available!!!!!");
				}
				else
				{
					logger.debug("Download Status: "
					        + (appResponse.getSuccess() ? "success" : "failed") + ", Message: "
					        + appResponse.getMessage());
					if (appResponse.getSuccess())
					{
						// download the source file.

						// execute the command
						AppNode node = appResponse.getNodes().get(0);
						logger.debug("App-Server Version: " + node.getVersion());

						// update each version of the software that is
						// provided/allowed
						// based on current version vs. software version.
						for (int softwareNdx = 0; softwareNdx < node.getSoftware().size(); softwareNdx++)
						{
							AppNodeSoftware software = node.getSoftware().get(softwareNdx);

							String[] versionParts = software.getVersion().split("\\.");
							logger.debug("Verifying software entry(" + software.getId()
							        + ") - version: " + software.getVersion());

							int iMajor = 0, iMinor = 0, iBuild = 0;
							if (versionParts.length >= 1) iMajor = Integer
							        .parseInt(versionParts[0]);
							if (versionParts.length >= 2) iMinor = Integer
							        .parseInt(versionParts[1]);
							if (versionParts.length == 3) iBuild = Integer
							        .parseInt(versionParts[2]);

							boolean installUpdate = false;
							if (iMajor > configs.getVersionInfo().getMajor()) installUpdate = true;
							else if (iMinor > configs.getVersionInfo().getMinor()) installUpdate = true;
							else if (iBuild > configs.getVersionInfo().getBuild()) installUpdate = true;

							if (!installUpdate)
							{
								logger.debug("Skipping this update. Client software newer than update.");
							}
							else
							{
								logger.debug("Installing software update.");

								//
								// install all parts of this software
								// packages.
								for (int fileNdx = 0; fileNdx < software.getFiles().size(); fileNdx++)
								{
									AppNodeSoftwareFile updateFile = software.getFiles().get(
									        fileNdx);

									logger.debug("Downloading: " + updateFile.getSourceUri());
									getBinaryFileRequest(updateFile.getSourceUri(), "./");
									logger.debug("downloading complete\n\n");

									try
									{
										String line;
										Process p = Runtime.getRuntime().exec(
										        updateFile.getExecCmd());
										System.out.println("Executing: "
										        + updateFile.getExecCmd());
										BufferedReader input = new BufferedReader(
										        new InputStreamReader(p.getInputStream()));
										while ((line = input.readLine()) != null)
										{
											System.out.println(line);
											logger.debug(line);
										}

										logger.debug("process is done!");
										System.out.println("process is done!");
										input.close();
									}
									catch (Exception err)
									{
										err.printStackTrace();
										logger.debug(err);
									}
								}

								//
								// update version information
								configs.getVersionInfo().setMajor((short) iMajor);
								configs.getVersionInfo().setMinor((short) iMinor);
								configs.getVersionInfo().setBuild((short) iBuild);
								configs.getVersionInfo().setVersion(
								        "" + iMajor + "." + iMinor + "." + iBuild);
								configs.flushConfig();
							}
						}
					}
				}
			}
		}
		catch (Exception exp)
		{
			if (logger != null) logger.error(exp);
			else System.out.println(exp);
		}
	}
}