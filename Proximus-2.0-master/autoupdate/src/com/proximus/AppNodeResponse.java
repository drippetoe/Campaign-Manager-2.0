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

public class AppNodeResponse {
	protected boolean success;
	protected String message;
	protected List<AppNode> nodes;
	
	public AppNodeResponse() { }

	public boolean getSuccess() { return success; }
	public void setSuccess(boolean value) { success=value; }
	
	public String getMessage() { return message; }
	public void setMessage(String value) { message=value; }
	
    public List<AppNode> getNodes() {
    	if(nodes == null)
    		nodes = new ArrayList<AppNode>();

        return nodes;
    }
}
