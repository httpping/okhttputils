package com.vpnet.util;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;

import com.vpnet.IShowDialog;

/**
 * 实现多请求对话框集成操作
 * @author tp
 *
 */
public class MulitRequestUtil implements Serializable{
	
	
	List<Call> mCalls = new LinkedList<Call>();
	
	/**
	 * 是否需要对话框显示处理操作， 不设置则没有对话框操作
	 */
	IShowDialog onShowDialog;
	
	public  MulitRequestUtil(){
		
	}
	public  MulitRequestUtil(IShowDialog showDialog){
		this.onShowDialog = showDialog;
	}
	
	
	public void show(){
		if (onShowDialog !=null) {
			onShowDialog.show();
		}
		
	}
	
	public void add(Call call){
		mCalls.add(call);
	}
	
	/**
	 * 是否完成
	 * @param call
	 * @return
	 */
	public boolean isFinish(Call call){
		
		mCalls.remove(call);
		if (mCalls.size() !=0) {
			return false;
		}
		
		if (onShowDialog !=null) { // dismiss
			onShowDialog.dismiss();
		}
		return true;
	}
	
	
	public IShowDialog getOnShowDialog() {
		return onShowDialog;
	}
	public void setOnShowDialog(IShowDialog onShowDialog) {
		this.onShowDialog = onShowDialog;
	}
	
	
}
