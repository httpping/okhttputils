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

	
	
	
	public abstract void onFailure(Call paramCall, IOException paramIOException);
	public abstract void onResponse(Call paramCall, Response paramResponse) throws IOException;
	
	public void onFinish(Call call){}
	/**
	 * 备用
	 * @param call
	 */
	public void onStart(Call call){}
	 
}
