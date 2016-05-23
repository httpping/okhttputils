package com.example.okhttpdemo;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.vpnet.IShowDialog;
import com.vpnet.NetLog;
import com.vpnet.VpRequestParams;
import com.vpnet.VpCallBack;
import com.vpnet.VpHttpClient;


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
        
        findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//
				net();
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
    	
    	for (int i = 0; i < 1000; i++) {
			netForm(i+1);
		}
    }
    
    public void netForm(int c){
    	VpRequestParams params = new VpRequestParams();
        params.put("name", "tanp post");
        params.put("age", "谭平sssssdf");
        params.put("count", c);
       // params.putJsonParams("{}");
        String path ="/sdcard/loveu/s3.jpg";
		File file = new File(path);
		/*try {
			params.put("f1", file,"image/jpg",null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}*/
        String url ="http://httpbin.org/post";
        Call call = client.post(url, params, new VpCallBack() {

			@Override
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
				
			}

			 
		});
        
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
        String url ="http://httpbin.org/get";
        client.get(url, params, new VpCallBack() {

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
			 
		});
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
