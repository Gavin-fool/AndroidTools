package com.alier.com.commons.utils;

import java.util.HashMap;

public class CoordsTransform {

	public static double pi = 3.14159265358979324;
	public static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
	public static double a = 6378245.0;
	public static double ee = 0.00669342162296594323;
	
	/**
	 * bd09坐标转国测局02坐标
	 * @param lat
	 * @param lon
	 */
	public static double[] bd09_To_Gcj02(double lat, double lon){
		double x = lon - 0.0065, y = lat - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
		double gg_lon = z * Math.cos(theta);
		double gg_lat = z * Math.sin(theta);
		return new double[]{gg_lon, gg_lat};
	}
	
	/**
	 * 国测局02坐标坐标转bd09
	 * @param lat
	 * @param lon
	 */
	public static double[] Gcj02_To_bd09(double lat, double lon){
		double x = lon, y = lat;
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
		double bd_lon = z * Math.cos(theta) + 0.0065;
		double bd_lat = z * Math.sin(theta) + 0.006;
		return new double[]{bd_lon, bd_lat};
	}
	
	/**
	 * 国测局02坐标转84坐标
	 * @param lat
	 * @param lon
	 */
	public static double[] Gcj02_To_Gps84(double lat, double lon){
		double lontitude = lon
				- (((Double) transform(lon, lat).get("lon")).doubleValue() - lon);
		double latitude = (lat - (((Double) ( transform(lon, lat))
				.get("lat")).doubleValue() - lat));
		
		return new double[]{lontitude, latitude};
	}
	
	public static HashMap<String, Double> transform(double lon, double lat) {
		HashMap<String, Double> localHashMap = new HashMap<String, Double>();
		if (outOfChina(lat, lon)) {
			localHashMap.put("lon", Double.valueOf(lon));
			localHashMap.put("lat", Double.valueOf(lat));
			return localHashMap;
		}
		double dLat = transformLat(lon - 105.0, lat - 35.0);
		double dLon = transformLon(lon - 105.0, lat - 35.0);
		double radLat = lat / 180.0 * pi;
		double magic = Math.sin(radLat);
		magic = 1 - ee * magic * magic;
		double sqrtMagic = Math.sqrt(magic);
		dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
		dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
		double mgLat = lat + dLat;
		double mgLon = lon + dLon;
		localHashMap.put("lon", mgLon);
		localHashMap.put("lat", mgLat);
		return localHashMap;
	}
	
	/**
	 * 84坐标转国测局02坐标
	 * @param lat
	 * @param lon
	 */
	public static double[] gps84_To_Gcj02(double lat, double lon) {
		if (outOfChina(lat, lon)) {
			return new double[]{lon, lat};
		}
		double dLat = transformLat(lon - 105.0, lat - 35.0);
		double dLon = transformLon(lon - 105.0, lat - 35.0);
		double radLat = lat / 180.0 * pi;
		double magic = Math.sin(radLat);
		magic = 1 - ee * magic * magic;
		double sqrtMagic = Math.sqrt(magic);
		dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
		dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
		double mgLat = lat + dLat;
		double mgLon = lon + dLon;
		return new double[]{mgLon, mgLat};
	}
	
	public static double transformLat(double x, double y) {
		double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
				+ 0.2 * Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
		ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
		return ret;
	}
	
	public static double transformLon(double x, double y) {
		double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
				* Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
		ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0
				* pi)) * 2.0 / 3.0;
		return ret;
	}
	/**
	 * 判断是否是中国内的坐标
	 */
	public static boolean outOfChina(double lat, double lon) {
		if (lon < 72.004 || lon > 137.8347)
			return true;
		if (lat < 0.8293 || lat > 55.8271)
			return true;
		return false;
	}
	
}
