
package com.baidu.common.cache.core;

import java.io.InputStream;

/**
 * 类型转换接口类
 * 
 * @date 2012-12-23
 * @version 1.0
 * @author huangweigan
 */
public interface ITypeConvert {

    public Object convertInputStreamToObject(InputStream is);

    public InputStream convertObjectToInputStream(Object obj);

}
