package com.vpnet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.MultipartBody.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.vpnet.VpRequestParams.FileWrapper;
import com.vpnet.util.VpUrlUtil;

/**
 * 网络请求客户端
 * 
 * @author tp
 * 
 */
public class VpHttpClient {
	public static final String TAG = VpHttpClient.class.getSimpleName();
   public static final   MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

	List<Call> calls = new LinkedList<Call>();
	static OkHttpClient client;
	Context mContext; // 上下文
	
	IShowDialog onShowDialog;//加载对话框

	public VpHttpClient(Context context) {
		mContext = context;

	}
	
	/**
	 * 单利
	 * @return
	 */
	public OkHttpClient getOkHttpClient(){
		
		if (client == null) {
			synchronized (VpHttpClient.class) {
				if (client == null) {
					 client = new OkHttpClient.Builder()
				        .connectTimeout(10, TimeUnit.SECONDS)
				        .writeTimeout(180, TimeUnit.SECONDS)
				        .readTimeout(180, TimeUnit.SECONDS)
				        .build();
				}
			}
			
		}
	   
	    
	    return client;
	}
	
	/**
	 * 表单验证 post
	 * @param url
	 * @param method
	 * @param params
	 * @param callBack
	 * @return 
	 */
	private Call requestForm(String url,String method, VpRequestParams params, VpCallBack callBack) {
		NetLog.d(TAG, "requestForm: " + url);
		
		 okhttp3.FormBody.Builder builder = new FormBody.Builder();
		//add 参数
		 for (ConcurrentHashMap.Entry<String, String> entry : params.urlParams.entrySet()) {//增加url params
			 builder.add(entry.getKey(), entry.getValue());
		 }
		
		 FormBody requestBody = builder.build();

		 okhttp3.Request.Builder reqestBuilder = new Request.Builder().url(url);
		 for (ConcurrentHashMap.Entry<String, String> entry : params.heads.entrySet()) {//增加头
			   reqestBuilder.addHeader(entry.getKey(), entry.getValue());
		 }
 
		reqestBuilder.post(requestBody);
		
		//对话框
		showDialog(params.isShowDialog());

		Call call =null;
		if (!params.isRepeatable) {//不允许重复尝试
	        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
	        //cookie enabled
	        okHttpClientBuilder.retryOnConnectionFailure(false);
	        okHttpClientBuilder.readTimeout(20, TimeUnit.SECONDS);
	        okHttpClientBuilder.writeTimeout(20, TimeUnit.SECONDS);
	        OkHttpClient onRepeatable = okHttpClientBuilder.build();
			call= onRepeatable.newCall(reqestBuilder.build());
		}else {
			call= getOkHttpClient().newCall(reqestBuilder.build());	
		}
		calls.add(call);
		
		call.enqueue(new CallBackHandler(callBack));
		return call;
	}
	
	/**
	 * json 内容 传送 body text
	 * @param url
	 * @param method
	 * @param params
	 * @param callBack
	 * @return
	 */
	private Call requestJson(String url,String method, VpRequestParams params, VpCallBack callBack) {
		
		
		okhttp3.Request.Builder reqestBuilder = new Request.Builder().url(url).addHeader("Content-Type", "application/json; charset=UTF-8");
		if ("post".equals(method)) {
			RequestBody requestBody = RequestBody.create(JSON_TYPE, params.jsonParams);
			reqestBuilder.post(requestBody);
		}else {
			reqestBuilder.get();
		}		
		
		for (ConcurrentHashMap.Entry<String, String> entry : params.heads.entrySet()) {//增加头
			   reqestBuilder.addHeader(entry.getKey(), entry.getValue());
		 }
		
		//对话框
		showDialog(params.isShowDialog());

		
		Request request = reqestBuilder.build();
		Call call =null;
		if (!params.isRepeatable) {//不允许重复尝试
	        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
	        //cookie enabled
	        okHttpClientBuilder.retryOnConnectionFailure(false);
	        okHttpClientBuilder.readTimeout(20, TimeUnit.SECONDS);
	        okHttpClientBuilder.writeTimeout(20, TimeUnit.SECONDS);
	        OkHttpClient onRepeatable = okHttpClientBuilder.build();
			call= onRepeatable.newCall(request);
		}else {
			call= getOkHttpClient().newCall(request);	
		}
		
		calls.add(call);
		call.enqueue(new CallBackHandler(callBack));
		return call;
	}
	
	/**
	 * 请求分配
	 * @param url
	 * @param method
	 * @param params
	 * @param callBack
	 * @return
	 */
	private Call request(String url,String method, VpRequestParams params, VpCallBack callBack) {
		NetLog.d("request", "url :" + url);
		if (TextUtils.isEmpty(params.jsonParams)) { //Form表单
			 return requestForm(url, method, params, callBack);
		}else { // body text
			return  requestJson(url, method, params, callBack);
		}
		
	}
	
	/**
	 * get 请求 
	 * @param url
	 * @param params
	 * @param callBack
	 * @return
	 */
	public Call get (String url, VpRequestParams params, VpCallBack callBack) {
		if (callBack !=null) {
			callBack.params = params;
		}
		try {//add params
			url =  VpUrlUtil.getUrlWithQueryString(false, url, params);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			new CallBackHandler(callBack).onFailure(null, e);
			return null;
		}
		
		
		
		okhttp3.Request.Builder reqestBuilder = new Request.Builder().url(url); 
		
		for (ConcurrentHashMap.Entry<String, String> entry : params.heads.entrySet()) {//增加头
			   reqestBuilder.addHeader(entry.getKey(), entry.getValue());
		 }
		
		reqestBuilder.get();
		Request request = reqestBuilder.build();
		Call newCall = getOkHttpClient().newCall(request);
		
		//对话框
		showDialog(params.isShowDialog());
		
		calls.add(newCall);
		newCall.enqueue(new CallBackHandler(callBack));
		
		
		return newCall;
	}
	
	/**
	 * 对话框显示
	 * @param isShow
	 */
	public void showDialog(boolean isShow){
		if (isShow && onShowDialog!=null) {
			onShowDialog.show();
		}
	}
	/**
	 * 关闭对话框
	 */
	public void dismissDialog(){
		if (onShowDialog!=null) {
			onShowDialog.dismiss();
		}
	}
	
	/**
	 * post 请求
	 * @param url
	 * @param params
	 * @param callBack
	 * @return
	 */
	public Call post (String url, VpRequestParams params, VpCallBack callBack) {
		if (callBack !=null) {
			callBack.params = params;
		}
		 return request(url, "post", params, callBack);
	}
	
	/**
	 * 上传文件
	 * @param url
	 * @param params
	 * @param callBack
	 * @return
	 */
	public Call postFile (String url, VpRequestParams params, VpCallBack callBack){
		if (callBack !=null) {
			callBack.params = params;
		}

		NetLog.d(TAG, "requestForm: " + url);
		
		Builder builder = new MultipartBody.Builder();
		//add 参数
		 for (ConcurrentHashMap.Entry<String, String> entry : params.urlParams.entrySet()) {//增加url params
			 builder.addFormDataPart(entry.getKey(), entry.getValue());
		 }
		 for (ConcurrentHashMap.Entry<String, FileWrapper> entry : params.fileParams.entrySet()) {//增加url file
			 builder.addFormDataPart(entry.getKey(), entry.getValue().customFileName,
			            RequestBody.create( MediaType.parse(entry.getValue().contentType), entry.getValue().file));
		 }
		builder.setType(MultipartBody.FORM);
		
		MultipartBody requestBody = builder.build();

		okhttp3.Request.Builder reqestBuilder = new Request.Builder().url(url);
		 for (ConcurrentHashMap.Entry<String, String> entry : params.heads.entrySet()) {//增加头
			   reqestBuilder.addHeader(entry.getKey(), entry.getValue());
		 }
		
		 //reqestBuilder.header("Content-Type",  "multipart/form-data; boundary=MIME_boundary");
 
		reqestBuilder.post(requestBody);
		 
		Call call = getOkHttpClient().newCall(reqestBuilder.build());
		calls.add(call);
		call.enqueue(new CallBackHandler(callBack));
		return call;
	
		
	}
	

	
	/**
	 * call handler，处理call回调事件
	 * @author tp
	 *
	 */
	private class CallBackHandler implements Callback{
		VpCallBack mCallBack;
		public CallBackHandler(VpCallBack callBack){
			this.mCallBack = callBack;
		}
		
		@Override
		public void onFailure(Call paramCall, IOException paramIOException) {
			
			paramIOException.printStackTrace();
			NetLog.d("result", "onFailure:"+paramIOException);
			
			VpResponse response = new VpResponse();
			response.callBack = mCallBack;
			response.errorMsg = paramIOException.getMessage();
			
			if (mCallBack !=null) {
				Message message = new Message();
				message.what = ON_FAIL;
				message.obj = response;
				message.setTarget(mHandler);
				message.sendToTarget();
			}
			
			onFinish(paramCall);
		}

		@Override
		public void onResponse(Call paramCall, Response paramResponse)
				throws IOException {
			NetLog.d("result", "onResponse");
			VpResponse response = new VpResponse();
			response.body = paramResponse.body().string();
			response.headers = paramResponse.headers();
			response.callBack = mCallBack;
			NetLog.d("result", ""+response.body);
			if (mCallBack !=null) {
				Message message = new Message();
				message.what = ON_RESPONSE;
				message.obj = response;
				message.setTarget(mHandler);
				message.sendToTarget();
			}
			onFinish(paramCall);
		}
		
		public void onFinish(Call call){
			//清理
			calls.remove(call);
			
			VpResponse response = new VpResponse();
			response.callBack = mCallBack;
			
			if (mCallBack !=null) {
				Message message = new Message();
				message.what = ON_FINISH;
				message.obj = response;
				message.setTarget(mHandler);
				message.sendToTarget();
			}
		}
		
	}
	
	
	private static final int ON_RESPONSE = 1025;
	private static final int ON_FAIL = 1026;
	private static final int ON_FINISH = 1027;
	Handler mHandler = new Handler(Looper.getMainLooper()){
		public void handleMessage(android.os.Message msg) {
			VpResponse response =  (VpResponse) msg.obj ;

			switch (msg.what) {
			case ON_RESPONSE:
				response.callBack.onResponse(response);
				break;
			case ON_FAIL:
				response.callBack.onFailure(response);
				break;
			case ON_FINISH:
				
				//close the dialog 
				if (response.callBack.params !=null && response.callBack.params.isShowDialog()) {
					dismissDialog();	
				}
				response.callBack.onFinish();
				break ;
			default:
				break;
			}
		};
	};

	
	
	
	
	public IShowDialog getOnShowDialog() {
		return onShowDialog;
	}

	public void setOnShowDialog(IShowDialog onShowDialog) {
		this.onShowDialog = onShowDialog;
	}

	/**
	 * 关闭所有
	 */
	public void cancelAll(){
		
		calls.clear();
		client.dispatcher().cancelAll();
		
		
	}
	
	/**
	 * 关闭Call
	 */
	public void cancel(Call call){
		if (call == null) {
			return ;
		}
		if (call !=null) {
			if (!call.isCanceled()) {
				call.cancel();
			}
		}
	}
}
