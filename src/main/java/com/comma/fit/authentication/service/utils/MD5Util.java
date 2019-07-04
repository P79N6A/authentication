/*   
 * Copyright © 2014-2016 jia-fu.cn All Rights Reserved.
 *   
 * This software is the confidential and proprietary information of   
 * TAOLUE Co.,Ltd. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with TAOLUE Co.,Ltd.    
 *   
 */

package com.comma.fit.authentication.service.utils;

import org.apache.commons.codec.digest.DigestUtils;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

/**
 * Description: TODO
 * User: Lijiang
 * Date: 2018-12-18
 * Time: 16:29
 */
public class MD5Util {

	/**
	 * 将传入的字符串做MD5摘要
	 * 
	 * @param text
	 *            需要摘要的字符串
	 * @return 摘要结果
	 */
	public static String sign(String text) {
		return DigestUtils.md5Hex(getContentBytes(text, "utf-8"));
	}
	
	/**
	 * 将传入的明文做MD5摘要，比较传入的摘要，判断是否相等
	 * 
	 * @param text
	 *            需要摘要的字符串
	 * @param sign
	 *            需要比较的摘要对象
	 * @return 验证结果
	 */
	public static boolean verify(String text, String sign) {
		String mysign = DigestUtils.md5Hex(getContentBytes(text, "utf-8"));
		if (mysign.equals(sign)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param content
	 * @param charset
	 * @return
	 * @throws SignatureException
	 * @throws UnsupportedEncodingException
	 */
	private static byte[] getContentBytes(String content, String charset) {
		if (charset == null || "".equals(charset)) {
			return content.getBytes();
		}
		try {
			return content.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
		}
	}
	
	
	private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7",  
            "8", "9", "a", "b", "c", "d", "e", "f"};
	

  

    

    
    public static String md5(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte[] byteDigest = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (byte element : byteDigest) {
				i = element;
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					buf.append("0");
				}
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

//	public static void main(String[] args) {
//		System.out.println(MD5Util.md5("123123"));
//	}
}
