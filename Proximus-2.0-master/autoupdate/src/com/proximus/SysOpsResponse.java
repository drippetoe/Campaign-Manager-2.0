package com.proximus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.*;
import com.proximus.common.AppNode;
import com.proximus.common.AppNodeSoftware;
import com.proximus.common.AppNodeSoftwareFile;

import java.util.List;
import java.util.ArrayList;

public class SysOpsResponse {
	protected AppNodeResponse appnodes;
	
	/**
	 * 
	 */
	public SysOpsResponse() {
		// TODO Auto-generated constructor stub
	}
	
	public AppNodeResponse getAppNodes() { return appnodes; }
	public void setAppNodes(AppNodeResponse value) { appnodes = value; }
}
