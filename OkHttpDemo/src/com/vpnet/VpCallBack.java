package com.vpnet;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 网络访问请求 返回
 * @author tp
 *
 */
public abstract class VpCallBack   {

	
	public VpRequestParams params;
	
	
	public abstract void onFailure(VpResponse response);
	public abstract void onResponse(VpResponse response);
	
	public void onFinish( ){}
	/**
	 * 备用
	 * @param call
	 */
	public void onStart(){}
	 
}
