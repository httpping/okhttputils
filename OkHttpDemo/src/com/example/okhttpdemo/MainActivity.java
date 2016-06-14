package com.example.okhttpdemo;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.vpnet.IShowDialog;
import com.vpnet.NetLog;
import com.vpnet.VpCallBack;
import com.vpnet.VpHttpClient;
import com.vpnet.VpRequestParams;
import com.vpnet.VpResponse;
import com.vpnet.util.MulitRequestUtil;


public class MainActivity extends Activity {

	VpHttpClient client;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        client = new VpHttpClient(this);
        client.setOnShowDialog(new IShowDialog() {
        	ProgressDialog dialog ;
			@Override
			public void show() {
				
				if (dialog ==null) {
					//对话框show
				}
				 
			}
			
			@Override
			public void dismiss() {
				//对话框 dismiss
			}
		});
        
        Log.d("", "");
        findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//
				net();
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						Looper.prepare();
						Looper.myLooper();
					//	Looper.prepare();
						Handler handler = new Handler(Looper.getMainLooper()){
							public void handleMessage(android.os.Message msg) {
								NetLog.d("handler", ""+Thread.currentThread().getName());
							};
						};
						handler.sendEmptyMessageDelayed(1, 1000);
					//	Looper.loop();
					}
					
				}).start();
			}
		});
        findViewById(R.id.button2).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//
				//netForm();
				newTest();
			}
		});
    }
   
    
    public int   count =0;
    public void newTest(){
    	
    	MulitRequestUtil mulitRequestUtil = new MulitRequestUtil(new IShowDialog() {
			
			@Override
			public void show() {
				NetLog.d("mu-result", "multi- start");
			}
			
			@Override
			public void dismiss() {
				NetLog.d("mu-result", "multi- end");
			}
		});
    	mulitRequestUtil.show();
    	for (int i = 0; i < 20; i++) {
			Call call = netForm(i+1,mulitRequestUtil);
			mulitRequestUtil.add(call);
		}
    	client.cancelAll();
    }
    
    public Call netForm(int c,final MulitRequestUtil mulitRequestUtil){
    	VpRequestParams params = new VpRequestParams();
        params.put("name", "tanp post");
        params.put("age", "谭平sssssdf");
        params.put("sex", 1);
        params.put("中文key", "中文value");
        params.put("count", c);
       // params.putJsonParams("{}");
        String path ="/sdcard/loveu/s3.jpg";
		File file = new File(path);
		/*try {
			params.put("f1", file,"image/jpg",null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}*/
        String url ="https://httpbin.org/post";
        Call call  = client.post(url, params, new VpCallBack() {

			@Override
			public void onFailure(VpResponse response) {
			}

			@Override
			public void onResponse(VpResponse response) {
				//NetLog.d("tag", Thread.currentThread() + "" +response.body);
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				
			}
			
			@Override
			public void onFinish(VpResponse response) {
				super.onFinish(response);
				mulitRequestUtil.isFinish(response.call);
			}

			/*@Override
			public void onFailure(Call paramCall, IOException paramIOException) {
				
			}

			@Override
			public void onResponse(Call paramCall, Response paramResponse)  throws IOException {
			     Headers headers = paramResponse.headers();
			    
			    String result =  paramResponse.body().string();
				NetLog.d("tag", "" + result);
				try {
					JSONObject jsonObject = new JSONObject(result);
					String count = jsonObject.getJSONObject("form").getString("count");
					NetLog.d("finish-start", "start: "+count);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//NetLog.d("tag", "" +headers.toString());
			
			}

			@Override
			public void onFinish(Call call) {
				count++;
				NetLog.d("finish", "count +" +count);
			}

			@Override
			public void onStart(Call call) {
				
			}*/ 

			 
		});
        
        return call; 
        	
       // client.cancel(call);
        
       /* testOkHttpMulti();
        testPost();*/
        //testPostFile();
    }
    
    public void net(){
    	  
        VpRequestParams params = new VpRequestParams();
        params.put("name", "tanp0");
        params.put("age", "谭平");
        params.putJsonParams("{}");
        params.putHead("hes", "1345");
        String url ="https://httpbin.org/get";
        client.get(url, params, new VpCallBack() {

			@Override
			public void onFailure(VpResponse response) {
				
			}

			@Override
			public void onResponse(VpResponse response) {
				NetLog.d("tag", Thread.currentThread() + "" +response.body);
				
			}/*

			@Override
			public void onFailure(Call paramCall, IOException paramIOException) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onResponse(Call paramCall, Response paramResponse)  throws IOException{
			  Headers headers = paramResponse.headers();
					NetLog.d("tag", Thread.currentThread() + "" +paramResponse.body().string());
				NetLog.d("tag", "" +headers.toString());

			}
			 
		*/});
    }
    
    
    public void testPostFile(){
    	String path ="/sdcard/loveu/s3.jpg";
		File file = new File(path);
		NetLog.i("file","file:" +file.exists());
		
		 MediaType MEDIA_TYPE_MARKDOWN
	      = MediaType.parse("image/jpg");
		 OkHttpClient client = new OkHttpClient.Builder()
	        .connectTimeout(10, TimeUnit.SECONDS)
	        .writeTimeout(1800, TimeUnit.SECONDS)
	        .readTimeout(180, TimeUnit.SECONDS)
	        .build();

    	    Request request = new Request.Builder()
    	        .url("https://api.github.com/markdown/raw")
    	        .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
    	        .build();

    	   client.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call paramCall, Response paramResponse)
					throws IOException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFailure(Call paramCall, IOException paramIOException) {
				// TODO Auto-generated method stub
				
			}
		});

    }
    
    
   
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	client.cancelAll();
    }
}
