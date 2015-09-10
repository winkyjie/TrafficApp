package cn.fszt.trafficapp.util;

import java.util.HashMap;
import java.util.Map;

public class CarTypeDictionary {

	private Map<String, String> map=null;
	public CarTypeDictionary(){
		map = CarTypeDictionary();
	}
	
	public String GetMapValueByKey(String key) {
		if (map.containsKey(key)) {
			return map.get(key);
		} else {
			return key;
		}
	}

	public String GetMapkeyByValue(String value) {
		String key = null;
		for (String str : map.keySet()) {
			if (value.equals(map.get(str))) {
				key = str;
			}
		}
		return key;
	}

	/*
	 * 车型字典
	 */
	private Map<String, String> CarTypeDictionary() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("大型汽车", "01");
		map.put("小型汽车", "02");
		map.put("使馆汽车", "03");
		map.put("领馆汽车", "04");
		map.put("境外汽车", "05");
		map.put("外籍汽车", "06");
		map.put("两、三轮摩托车", "07");
		map.put("轻便摩托车", "08");
		map.put("使馆摩托车", "09");
		map.put("领馆摩托车", "10");
		map.put("境外摩托车", "11");
		map.put("外籍摩托车", "12");
		map.put("农用运输车", "13");
		map.put("拖拉机", "14");
		map.put("挂车", "15");
		map.put("教练汽车", "16");
		map.put("教练摩托车", "17");
		map.put("试验汽车", "18");
		map.put("试验摩托车", "19");
		map.put("临时入境汽车", "20");
		map.put("临时入境摩托车", "21");
		map.put("临时行驶车", "22");
		map.put("警用汽车", "23");
		map.put("警用摩托", "24");
		map.put("原农机号牌", "25");
		map.put("香港车", "26");
		map.put("澳门车", "27");
		return map;
	}
}
