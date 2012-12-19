package com.baidu.common.cache.core;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.common.cache.core.CacheComponentImpl.DeletePolicy;
import com.baidu.common.cache.utils.LogUtil;

public class CacheManage extends Activity {

	private ICacheComponent mICacheComponent;
	private String mCachePath1;
	private String mCachePath2;
	
	private Button mInsertBtn;
	private Button mDisplayBtn;
	private Button mInsert2Btn;
	private EditText mMsgText;
	private int mNum = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cache_manage);
		setupViews();
		mCachePath1 = "/sdcard/data/Android/" + getPackageName() + "/cache";
		mCachePath2 = "/data/data/" + getPackageName() + "/cache1";
		mICacheComponent = (ICacheComponent) CacheComponentFactory.createInterface(this);
		if(!mICacheComponent.addCachePath(mCachePath1, DeletePolicy.TOTAL_SIZE, 2048))
		{
			LogUtil.e("Cache path don't exist!");
		}
		
		if(!mICacheComponent.addCachePath(mCachePath2, DeletePolicy.FILE_COUNT, 10))
		{
			LogUtil.e("Cache path don't exist!");
		}
	}
	
	public void setupViews()
	{
		mInsertBtn = (Button) findViewById(R.id.button1);
		mDisplayBtn = (Button) findViewById(R.id.button2);
		mInsert2Btn = (Button) findViewById(R.id.button3);
		mMsgText = (EditText) findViewById(R.id.editText1);
		
		mInsertBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String value = "hello world!hello world!" +
						"hello world!hello world!hello world!hello world!" +
						"hello world!hello world!hello world!hello world!" +
						"hello world!hello world!hello world!hello world!" +
						"hello world!hello world!hello world!hello world!" +
						"hello world!hello world!hello world!hello world!" +
						"hello world!hello world!hello world!hello world!" +
						"hello world!hello world!hello world!hello world!" +
						"hello world!hello world!hello world!hello world!" +
						"hello world!hello world!hello world!hello world!" +
						"hello world!hello world!hello world!hello world!hello world!";
				String key = "test" + (mNum++);
				mICacheComponent.putString(mCachePath1, key, value);
			}
		});
		
		mDisplayBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mICacheComponent.clearCache();
			}
		});
		
		mInsert2Btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String value = "hello world!hello world!" +
						"hello world!hello world!hello world!hello world!" +
						"hello world!hello world!hello world!hello world!" +
						"hello world!hello world!hello world!hello world!" +
						"hello world!hello world!hello world!hello world!" +
						"hello world!hello world!hello world!hello world!" +
						"hello world!hello world!hello world!hello world!" +
						"hello world!hello world!hello world!hello world!" +
						"hello world!hello world!hello world!hello world!" +
						"hello world!hello world!hello world!hello world!" +
						"hello world!hello world!hello world!hello world!hello world!";
				String key = "test" + (mNum++);
				mICacheComponent.putString(mCachePath2, key, value);
			}
		});
	}
}
