package com.tzx.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类：存放一些需要特殊处理的方法
 * 
 * @author Administrator
 *
 */
public class Tools {
	/**
	 * 使用正则表达式提取中括号中的内容
	 * 
	 * @param msg
	 * @return
	 */
	public static String extractMessageByRegular(String msg) {
		String test = "";
		Pattern p = Pattern.compile("<(.*?)>(.*)");
		Matcher m = p.matcher(msg);
		while (m.find()) {
			test = m.group(1);
		}
		return test;
	}

	/**
	 * shp文件对应sqlserver 数据类型的映射 输入shp的数据类型，输出sql Server的数据类型
	 * 
	 * @param type
	 * @return
	 */
	public static String shq2msSql(String type) {
		if (type != null && !type.isEmpty()) {
			switch (type) {
			case "MultiLineString":
				return "geometry";
			case "MultiPolygon":
				return "geometry";
			case "Integer":
				return "int";
			case "Double":
				return "float";
			case "String":
				return "varchar(128)";
			default:
				return "varchar(128)";
			}
		}
		return "varchar(128)";
	}
}
