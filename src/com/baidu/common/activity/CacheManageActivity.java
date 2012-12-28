
package com.baidu.common.activity;

import java.io.File;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.common.R;
import com.baidu.common.cache.core.CacheComponentFactory;
import com.baidu.common.cache.core.CacheComponentImpl;
import com.baidu.common.cache.core.CacheComponentImpl.DiskPolicy;
import com.baidu.common.cache.core.CacheComponentImpl.MemoryPolicy;
import com.baidu.common.cache.core.ICacheCallBack;
import com.baidu.common.cache.core.ICacheComponent;
import com.baidu.common.cache.utils.LogUtil;

public class CacheManageActivity extends Activity {

    private ICacheComponent mICacheComponent;
    private String mCachePath1;
    private String mCachePath2;

    private Button mInsertBtn;
    private Button mInsert2Btn;
    private Button mInsertBitmap;
    private EditText mMsgText;
    private ImageView mImageView;
    private Button mDisplayBtn;
    private Button mClearBtn;
    private int mNum = 1;
    private String mLastPath;
    private String mLastKey;
    private boolean mIsImage;

    Handler mHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case CacheComponentImpl.MSG_GET_FAIL:
                    Toast.makeText(CacheManageActivity.this, "Get Cache Fail!", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case CacheComponentImpl.MSG_GET_SUCCESS:
                    Toast.makeText(CacheManageActivity.this, "Get Cache Success!",
                            Toast.LENGTH_SHORT).show();
                    if (mIsImage)
                    {
                        mImageView.setImageBitmap((Bitmap) (msg.obj));
                    }
                    else {
                        mMsgText.setText((String) msg.obj);
                    }
                    break;
                case CacheComponentImpl.MSG_PUT_FAIL:
                    Toast.makeText(CacheManageActivity.this, "Put Cache Fail!", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case CacheComponentImpl.MSG_PUT_SUCCESS:
                    Toast.makeText(CacheManageActivity.this, "Put Cache Success!",
                            Toast.LENGTH_SHORT).show();
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
        if (!mICacheComponent.addDiskCachePath(mCachePath1, DiskPolicy.TOTAL_SIZE, 100))
        {
            LogUtil.e("Cache path don't exist!");
        }

        final int memClass = ((ActivityManager) getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryClass();

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = 1024 * 1024 * memClass / 8;
        if (!mICacheComponent.addMemoryCache(mCachePath2, MemoryPolicy.FIFO, cacheSize))
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

        mInsertBitmap = (Button) findViewById(R.id.button5);
        mImageView = (ImageView) findViewById(R.id.imageView1);

        mInsertBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String key = "test" + (mNum++);
                String value = String.valueOf(key.hashCode()) + key;
                mICacheComponent.put(mCachePath1, key, value);
                mLastPath = mCachePath1;
                mLastKey = key;
                mIsImage = false;
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
                mICacheComponent.put(mCachePath2, key, value);
                mLastPath = mCachePath2;
                mLastKey = key;
                mIsImage = false;
            }
        });

        mDisplayBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mIsImage)
                    mICacheComponent.get(mLastPath, mLastKey, true);
                else
                    mICacheComponent.get(mLastPath, mLastKey);
            }
        });

        mInsertBitmap.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Bitmap bm = getBitmapFromFile(new File("/sdcard/girl.JPG"), 1024, 1024);
                String key = "test" + (mNum++);
                mICacheComponent.put(mCachePath1, key, bm);
                mLastPath = mCachePath2;
                mLastKey = key;
                mIsImage = true;
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

    public Bitmap getBitmapFromFile(File dst, int width, int height) {
        if (null != dst && dst.exists()) {
            BitmapFactory.Options opts = null;
            if (width > 0 && height > 0) {
                opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(dst.getPath(), opts);
                // 计算图片缩放比例
                final int minSideLength = Math.min(width, height);
                opts.inSampleSize = computeSampleSize(opts, minSideLength,
                        width * height);
                opts.inJustDecodeBounds = false;
                opts.inInputShareable = true;
                opts.inPurgeable = true;
            }
            try {
                return BitmapFactory.decodeFile(dst.getPath(), opts);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static int computeSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
                .floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}
