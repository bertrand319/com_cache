
package com.baidu.common.cache.core;

import com.baidu.common.cache.utils.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * String类型转换
 * 
 * @date 2012-12-23
 * @version 1.0
 * @author huangweigan
 */
public class StringTypeConvert implements ITypeConvert {

    @Override
    public Object convertInputStreamToObject(InputStream is) {
        // TODO Auto-generated method stub
        String res = null;
        if (is instanceof FileInputStream)
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = null;

            try {
                in = FileUtils.fileInputStreamToByteArrayInputStream((FileInputStream) is);
                FileUtils.copyStream(in, out);
                res = new String(out.toByteArray());
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            } finally {
                try {
                    if(is != null) is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    if(in != null) in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    if(out != null) out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
        return res;
    }

    @Override
    public InputStream convertObjectToInputStream(Object obj) {
        // TODO Auto-generated method stub
        InputStream is = null;
        if (obj instanceof String)
        {
            is = new ByteArrayInputStream(((String) obj).getBytes());
        }
        return is;
    }

}
