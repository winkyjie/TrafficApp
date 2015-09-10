package cn.fszt.trafficapp.util;

import com.baidu.mapapi.map.Symbol;

public class MapSymbolUtil {

	// 正常模式

	public static Symbol lineSymbol_zs() {
		// 设定样式 阻塞
		Symbol lineSymbol_zs = new Symbol();
		Symbol.Color lineColor_zs = lineSymbol_zs.new Color();
		lineColor_zs.red = 255;
		lineColor_zs.green = 0;
		lineColor_zs.blue = 0;
		lineColor_zs.alpha = 155;
		lineSymbol_zs.setLineSymbol(lineColor_zs, 4);
		return lineSymbol_zs;
	}

	public static Symbol lineSymbol_ct() {
		// 设定样式 畅通
		Symbol lineSymbol_ct = new Symbol();
		Symbol.Color lineColor_ct = lineSymbol_ct.new Color();
		lineColor_ct.red = 0;
		lineColor_ct.green = 170;
		lineColor_ct.blue = 0;
		lineColor_ct.alpha = 155;
		lineSymbol_ct.setLineSymbol(lineColor_ct, 4);
		return lineSymbol_ct;
	}

	public static Symbol lineSymbol_hm() {
		// 设定样式 缓慢
		Symbol lineSymbol_hm = new Symbol();
		Symbol.Color lineColor_hm = lineSymbol_hm.new Color();
		lineColor_hm.red = 0;
		lineColor_hm.green = 0;
		lineColor_hm.blue = 255;
		lineColor_hm.alpha = 155;
		lineSymbol_hm.setLineSymbol(lineColor_hm, 4);
		return lineSymbol_hm;
	}

	// 色弱模式

	public static Symbol lineSymbol_sr_zs() {
		// 设定样式 阻塞
		Symbol lineSymbol_sr_zs = new Symbol();
		Symbol.Color lineColor_sr_zs = lineSymbol_sr_zs.new Color();
		lineColor_sr_zs.red = 0;
		lineColor_sr_zs.green = 0;
		lineColor_sr_zs.blue = 0;
		lineColor_sr_zs.alpha = 155;
		lineSymbol_sr_zs.setLineSymbol(lineColor_sr_zs, 4);
		return lineSymbol_sr_zs;
	}

	public static Symbol lineSymbol_sr_ct() {
		// 设定样式 畅通
		Symbol lineSymbol_sr_ct = new Symbol();
		Symbol.Color lineColor_sr_ct = lineSymbol_sr_ct.new Color();
		lineColor_sr_ct.red = 255;
		lineColor_sr_ct.green = 165;
		lineColor_sr_ct.blue = 0;
		lineColor_sr_ct.alpha = 155;
		lineSymbol_sr_ct.setLineSymbol(lineColor_sr_ct, 4);
		return lineSymbol_sr_ct;
	}

	public static Symbol lineSymbol_sr_hm() {
		// 设定样式 缓慢
		Symbol lineSymbol_sr_hm = new Symbol();
		Symbol.Color lineColor_sr_hm = lineSymbol_sr_hm.new Color();
		lineColor_sr_hm.red = 255;
		lineColor_sr_hm.green = 255;
		lineColor_sr_hm.blue = 255;
		lineColor_sr_hm.alpha = 155;
		lineSymbol_sr_hm.setLineSymbol(lineColor_sr_hm, 4);
		return lineSymbol_sr_hm;
	}
}
