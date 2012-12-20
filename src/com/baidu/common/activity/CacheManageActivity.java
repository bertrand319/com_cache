package com.baidu.common.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.common.R;
import com.baidu.common.cache.core.CacheComponentFactory;
import com.baidu.common.cache.core.CacheComponentImpl;
import com.baidu.common.cache.core.CacheComponentImpl.DeletePolicy;
import com.baidu.common.cache.core.ICacheCallBack;
import com.baidu.common.cache.core.ICacheComponent;
import com.baidu.common.cache.utils.LogUtil;

public class CacheManageActivity extends Activity {

	private ICacheComponent mICacheComponent;
	private String mCachePath1;
	private String mCachePath2;
	
	private Button mInsertBtn;
	private Button mInsert2Btn;
	private EditText mMsgText;
	private Button mDisplayBtn;
	private Button mClearBtn;
	private int mNum = 1;
	private String mLastPath;
	private String mLastKey;
	
	Handler mHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case CacheComponentImpl.MSG_GET_FAIL:
				Toast.makeText(CacheManageActivity.this, "Get Cache Fail!", Toast.LENGTH_SHORT).show();
				break;
			case CacheComponentImpl.MSG_GET_SUCCESS:
				Toast.makeText(CacheManageActivity.this, "Get Cache Success!", Toast.LENGTH_SHORT).show();
				mMsgText.setText(msg.obj.toString());
				break;
			case CacheComponentImpl.MSG_PUT_FAIL:
				Toast.makeText(CacheManageActivity.this, "Put Cache Fail!", Toast.LENGTH_SHORT).show();
				break;
			case CacheComponentImpl.MSG_PUT_SUCCESS:
				Toast.makeText(CacheManageActivity.this, "Put Cache Success!", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cache_manage);
		setupViews();
		mCachePath1 = "/sdcard/data/Android/" + getPackageName() + "/cache";
		mCachePath2 = "/data/data/" + getPackageName() + "/cache1";
		mICacheComponent = (ICacheComponent) CacheComponentFactory.createInterface(this);
		mICacheComponent.setCallBackListner(mICacheCallBack);
		if(!mICacheComponent.addCachePath(mCachePath1, DeletePolicy.TOTAL_SIZE, 100))
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
		mClearBtn = (Button) findViewById(R.id.button2);
		mInsert2Btn = (Button) findViewById(R.id.button3);
		mDisplayBtn = (Button) findViewById(R.id.button4);
		mMsgText = (EditText) findViewById(R.id.editText1);
		
		mInsertBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String key = "test" + (mNum++);
				String value = String.valueOf(key.hashCode()) + key;
				mICacheComponent.putString(mCachePath1, key, value);
				mLastPath = mCachePath1;
				mLastKey = key;
			}
		});
		
		mClearBtn.setOnClickListener(new OnClickListener() {
			
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
				String key = "test" + (mNum++);
				String value = String.valueOf(key.hashCode()) + key;
				mICacheComponent.putString(mCachePath2, key, value);
				mLastPath = mCachePath2;
				mLastKey = key;
			}
		});
		
		mDisplayBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mICacheComponent.getString(mLastPath, mLastKey);
			}
		});
	}
	
	public ICacheCallBack mICacheCallBack = new ICacheCallBack()
	{

		@Override
		public void onCallBack(int errorNo, Object obj) {
			// TODO Auto-generated method stub
			mHandler.sendMessage(Message.obtain(mHandler, errorNo, obj));
		}
	};
}
