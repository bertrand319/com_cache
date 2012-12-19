
package com.baidu.common.cache.disc.naming;

/**
 * Generates names for files at disc cache
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public interface FileNameGenerator {

    String generate(String imageUri);
}
