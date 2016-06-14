package com.vpnet;

import java.io.Serializable;

import okhttp3.Call;
import okhttp3.Headers;

public class VpResponse implements Serializable {
	
	public String errorMsg;
	public String body;
	public Headers headers;
	public VpCallBack callBack;
	public Call call ;
}
