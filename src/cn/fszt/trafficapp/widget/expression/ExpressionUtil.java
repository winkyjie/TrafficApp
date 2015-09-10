package cn.fszt.trafficapp.widget.expression;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.ExpressionData;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;


public class ExpressionUtil {
	
	public static List<ExpressionData> expressionList = new ArrayList<ExpressionData>();
	
	/**
	 * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
	 * @param context
	 * @param spannableString
	 * @param patten
	 * @param start
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws NumberFormatException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
    public static void dealExpression(Context context,SpannableString spannableString, Pattern patten, int start, int densityDpi) throws SecurityException, NoSuchFieldException, NumberFormatException, IllegalArgumentException, IllegalAccessException {
    	Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            if (matcher.start() < start) {
                continue;
            }
            Field field = R.drawable.class.getDeclaredField(key);
			int resId = Integer.parseInt(field.get(null).toString());		//通过上面匹配得到的字符串来生成图片资源id
            if (resId != 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
                if(densityDpi == 320){
					bitmap = big(bitmap,70,70);
				}else if(densityDpi == 240){
					bitmap = big(bitmap,40,40);
				}else if(densityDpi == 160){
					bitmap = big(bitmap,24,24);
				}else if(densityDpi == 440){
					bitmap = big(bitmap,130,130);
				}else if(densityDpi == 480){
					bitmap = big(bitmap,160,160);
				}else if(densityDpi == 640){
					bitmap = big(bitmap,280,280);
				}else{
					bitmap = big(bitmap,100,100);
				}
                ImageSpan imageSpan = new ImageSpan(bitmap,ImageSpan.ALIGN_BASELINE);				//通过图片资源id来得到bitmap，用一个ImageSpan来包装
                int end = matcher.start() + key.length();					//计算该图片名字的长度，也就是要替换的字符串的长度
                spannableString.setSpan(imageSpan, matcher.start()-1, end+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);	//将该图片替换字符串中规定的位置中
                if (end < spannableString.length()) {						//如果整个字符串还未验证完，则继续。。
                    dealExpression(context,spannableString,  patten, end, densityDpi);
                }
                break;
            }
        }
    }
    
    /**
     * 得到一个SpanableString对象，通过传入的字符串,并进行正则判断
     * @param context
     * @param str
     * @return
     */
    public static SpannableString getExpressionString(Context context,String str,String zhengze, int densityDpi){
    	SpannableString spannableString = new SpannableString(str);
        Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);		//通过传入的正则表达式来生成一个pattern
        try {
            dealExpression(context,spannableString, sinaPatten, 0, densityDpi);
        } catch (Exception e) {
            Log.e("dealExpression", e.getMessage());
        }
        return spannableString;
    }
    
    public static Bitmap big(Bitmap b,float x,float y){
	    int w=b.getWidth();
	    int h=b.getHeight();
	    float sx=(float)x/w;//要强制转换，不转换我的在这总是死掉。
	    float sy=(float)y/h;
	    Matrix matrix = new Matrix();
	    matrix.postScale(sx, sy); // 长和宽放大缩小的比例
	    Bitmap resizeBmp = Bitmap.createBitmap(b, 0, 0, w,
	       h, matrix, true);
	    return resizeBmp;
    } 

    
    /**
	 * 初始化表情信息
	 */
	public static void initExpression() {
		
		ExpressionData exp1 = new ExpressionData(R.drawable.em_1, "[em_1]");
		ExpressionData exp2 = new ExpressionData(R.drawable.em_2, "[em_2]");
		ExpressionData exp3 = new ExpressionData(R.drawable.em_3, "[em_3]");
		ExpressionData exp4 = new ExpressionData(R.drawable.em_4, "[em_4]");
		ExpressionData exp5 = new ExpressionData(R.drawable.em_5, "[em_5]");
		ExpressionData exp6 = new ExpressionData(R.drawable.em_6, "[em_6]");
		ExpressionData exp7 = new ExpressionData(R.drawable.em_7, "[em_7]");
		ExpressionData exp8 = new ExpressionData(R.drawable.em_8, "[em_8]");
		ExpressionData exp9 = new ExpressionData(R.drawable.em_9, "[em_9]");
		ExpressionData exp10 = new ExpressionData(R.drawable.em_10, "[em_10]");

		ExpressionData exp11 = new ExpressionData(R.drawable.em_11, "[em_11]");
		ExpressionData exp12 = new ExpressionData(R.drawable.em_12, "[em_12]");
		ExpressionData exp13 = new ExpressionData(R.drawable.em_13, "[em_13]");
		ExpressionData exp14 = new ExpressionData(R.drawable.em_14, "[em_14]");
		ExpressionData exp15 = new ExpressionData(R.drawable.em_15, "[em_15]");
		ExpressionData exp16 = new ExpressionData(R.drawable.em_16, "[em_16]");
		ExpressionData exp17 = new ExpressionData(R.drawable.em_17, "[em_17]");
		ExpressionData exp18 = new ExpressionData(R.drawable.em_18, "[em_18]");
		ExpressionData exp19 = new ExpressionData(R.drawable.em_19, "[em_19]");
		ExpressionData exp20 = new ExpressionData(R.drawable.em_20, "[em_20]");

		ExpressionData exp21 = new ExpressionData(R.drawable.em_21, "[em_21]");
		ExpressionData exp22 = new ExpressionData(R.drawable.em_22, "[em_22]");
		ExpressionData exp23 = new ExpressionData(R.drawable.em_23, "[em_23]");
		ExpressionData exp24 = new ExpressionData(R.drawable.em_24, "[em_24]");
		ExpressionData exp25 = new ExpressionData(R.drawable.em_25, "[em_25]");
		ExpressionData exp26 = new ExpressionData(R.drawable.em_26, "[em_26]");
		ExpressionData exp27 = new ExpressionData(R.drawable.em_27, "[em_27]");
		ExpressionData exp28 = new ExpressionData(R.drawable.em_28, "[em_28]");
		ExpressionData exp29 = new ExpressionData(R.drawable.em_29, "[em_29]");
		ExpressionData exp30 = new ExpressionData(R.drawable.em_30, "[em_30]");

		ExpressionData exp31 = new ExpressionData(R.drawable.em_31, "[em_31]");
		ExpressionData exp32 = new ExpressionData(R.drawable.em_32, "[em_32]");
		ExpressionData exp33 = new ExpressionData(R.drawable.em_33, "[em_33]");
		ExpressionData exp34 = new ExpressionData(R.drawable.em_34, "[em_34]");
		ExpressionData exp35 = new ExpressionData(R.drawable.em_35, "[em_35]");
		ExpressionData exp36 = new ExpressionData(R.drawable.em_36, "[em_36]");
		ExpressionData exp37 = new ExpressionData(R.drawable.em_37, "[em_37]");
		ExpressionData exp38 = new ExpressionData(R.drawable.em_38, "[em_38]");
		ExpressionData exp39 = new ExpressionData(R.drawable.em_39, "[em_39]");
		ExpressionData exp40 = new ExpressionData(R.drawable.em_40, "[em_40]");

		ExpressionData exp41 = new ExpressionData(R.drawable.em_41, "[em_41]");
		ExpressionData exp42 = new ExpressionData(R.drawable.em_42, "[em_42]");
		ExpressionData exp43 = new ExpressionData(R.drawable.em_43, "[em_43]");
		ExpressionData exp44 = new ExpressionData(R.drawable.em_44, "[em_44]");
		ExpressionData exp45 = new ExpressionData(R.drawable.em_45, "[em_45]");
		ExpressionData exp46 = new ExpressionData(R.drawable.em_46, "[em_46]");
		ExpressionData exp47 = new ExpressionData(R.drawable.em_47, "[em_47]");
		ExpressionData exp48 = new ExpressionData(R.drawable.em_48, "[em_48]");
		ExpressionData exp49 = new ExpressionData(R.drawable.em_49, "[em_49]");
		ExpressionData exp50 = new ExpressionData(R.drawable.em_50, "[em_50]");

		ExpressionData exp51 = new ExpressionData(R.drawable.em_51, "[em_51]");
		ExpressionData exp52 = new ExpressionData(R.drawable.em_52, "[em_52]");
		ExpressionData exp53 = new ExpressionData(R.drawable.em_53, "[em_53]");
		ExpressionData exp54 = new ExpressionData(R.drawable.em_54, "[em_54]");
		ExpressionData exp55 = new ExpressionData(R.drawable.em_55, "[em_55]");
		ExpressionData exp56 = new ExpressionData(R.drawable.em_56, "[em_56]");
		ExpressionData exp57 = new ExpressionData(R.drawable.em_57, "[em_57]");
		ExpressionData exp58 = new ExpressionData(R.drawable.em_58, "[em_58]");
		ExpressionData exp59 = new ExpressionData(R.drawable.em_59, "[em_59]");
		ExpressionData exp60 = new ExpressionData(R.drawable.em_60, "[em_60]");

		ExpressionData exp61 = new ExpressionData(R.drawable.em_61, "[em_61]");
		ExpressionData exp62 = new ExpressionData(R.drawable.em_62, "[em_62]");
		ExpressionData exp63 = new ExpressionData(R.drawable.em_63, "[em_63]");
		ExpressionData exp64 = new ExpressionData(R.drawable.em_64, "[em_64]");
		ExpressionData exp65 = new ExpressionData(R.drawable.em_65, "[em_65]");
		ExpressionData exp66 = new ExpressionData(R.drawable.em_66, "[em_66]");
		ExpressionData exp67 = new ExpressionData(R.drawable.em_67, "[em_67]");
		ExpressionData exp68 = new ExpressionData(R.drawable.em_68, "[em_68]");
		ExpressionData exp69 = new ExpressionData(R.drawable.em_69, "[em_69]");
		ExpressionData exp70 = new ExpressionData(R.drawable.em_70, "[em_70]");
		
		ExpressionData exp71 = new ExpressionData(R.drawable.em_71, "[em_71]");
		ExpressionData exp72 = new ExpressionData(R.drawable.em_72, "[em_72]");
		ExpressionData exp73 = new ExpressionData(R.drawable.em_73, "[em_73]");
		ExpressionData exp74 = new ExpressionData(R.drawable.em_74, "[em_74]");
		ExpressionData exp75 = new ExpressionData(R.drawable.em_75, "[em_75]");
		ExpressionData exp76 = new ExpressionData(R.drawable.em_76, "[em_76]");
		ExpressionData exp77 = new ExpressionData(R.drawable.em_77, "[em_77]");
		ExpressionData exp78 = new ExpressionData(R.drawable.em_78, "[em_78]");
		ExpressionData exp79 = new ExpressionData(R.drawable.em_79, "[em_79]");
		ExpressionData exp80 = new ExpressionData(R.drawable.em_80, "[em_80]");
		
		ExpressionData exp81 = new ExpressionData(R.drawable.em_81, "[em_81]");
		ExpressionData exp82 = new ExpressionData(R.drawable.em_82, "[em_82]");
		ExpressionData exp83 = new ExpressionData(R.drawable.em_83, "[em_83]");
		ExpressionData exp84 = new ExpressionData(R.drawable.em_84, "[em_84]");
		ExpressionData exp85 = new ExpressionData(R.drawable.em_85, "[em_85]");
		ExpressionData exp86 = new ExpressionData(R.drawable.em_86, "[em_86]");
		ExpressionData exp87 = new ExpressionData(R.drawable.em_87, "[em_87]");
		ExpressionData exp88 = new ExpressionData(R.drawable.em_88, "[em_88]");
		ExpressionData exp89 = new ExpressionData(R.drawable.em_89, "[em_89]");
		ExpressionData exp90 = new ExpressionData(R.drawable.em_90, "[em_90]");
		
		ExpressionData exp91  = new ExpressionData(R.drawable.em_91 , "[em_91]");
		ExpressionData exp92  = new ExpressionData(R.drawable.em_92 , "[em_92]");
		ExpressionData exp93  = new ExpressionData(R.drawable.em_93 , "[em_93]");
		ExpressionData exp94  = new ExpressionData(R.drawable.em_94 , "[em_94]");
		ExpressionData exp95  = new ExpressionData(R.drawable.em_95 , "[em_95]");
		ExpressionData exp96  = new ExpressionData(R.drawable.em_96 , "[em_96]");
		ExpressionData exp97  = new ExpressionData(R.drawable.em_97 , "[em_97]");
		ExpressionData exp98  = new ExpressionData(R.drawable.em_98 , "[em_98]");
		ExpressionData exp99  = new ExpressionData(R.drawable.em_99 , "[em_99]");
		ExpressionData exp100 = new ExpressionData(R.drawable.em_100, "[em_100]");
		
		ExpressionData exp101 = new ExpressionData(R.drawable.em_101, "[em_101]");
		ExpressionData exp102 = new ExpressionData(R.drawable.em_102, "[em_102]");
		ExpressionData exp103 = new ExpressionData(R.drawable.em_103, "[em_103]");
		ExpressionData exp104 = new ExpressionData(R.drawable.em_104, "[em_104]");
		ExpressionData exp105 = new ExpressionData(R.drawable.em_105, "[em_105]");
		ExpressionData exp106 = new ExpressionData(R.drawable.em_106, "[em_106]");
		ExpressionData exp107 = new ExpressionData(R.drawable.em_107, "[em_107]");
		ExpressionData exp108 = new ExpressionData(R.drawable.em_108, "[em_108]");
		ExpressionData exp109 = new ExpressionData(R.drawable.em_109, "[em_109]");
		ExpressionData exp110 = new ExpressionData(R.drawable.em_110, "[em_110]");
		
		ExpressionData exp111 = new ExpressionData(R.drawable.em_111, "[em_111]");
		ExpressionData exp112 = new ExpressionData(R.drawable.em_112, "[em_112]");
		ExpressionData exp113 = new ExpressionData(R.drawable.em_113, "[em_113]");
		ExpressionData exp114 = new ExpressionData(R.drawable.em_114, "[em_114]");
		ExpressionData exp115 = new ExpressionData(R.drawable.em_115, "[em_115]");
		ExpressionData exp116 = new ExpressionData(R.drawable.em_116, "[em_116]");
		ExpressionData exp117 = new ExpressionData(R.drawable.em_117, "[em_117]");
		ExpressionData exp118 = new ExpressionData(R.drawable.em_118, "[em_118]");
		ExpressionData exp119 = new ExpressionData(R.drawable.em_119, "[em_119]");
		ExpressionData exp120 = new ExpressionData(R.drawable.em_120, "[em_120]");
		
		ExpressionData exp121 = new ExpressionData(R.drawable.em_121, "[em_121]");
		ExpressionData exp122 = new ExpressionData(R.drawable.em_122, "[em_122]");
		ExpressionData exp123 = new ExpressionData(R.drawable.em_123, "[em_123]");
		ExpressionData exp124 = new ExpressionData(R.drawable.em_124, "[em_124]");
		ExpressionData exp125 = new ExpressionData(R.drawable.em_125, "[em_125]");	
		
		ExpressionData exp126 = new ExpressionData(R.drawable.em_126, "[em_126]");
		ExpressionData exp127 = new ExpressionData(R.drawable.em_127, "[em_127]");
		ExpressionData exp128 = new ExpressionData(R.drawable.em_128, "[em_128]");

		expressionList.add(exp1);
		expressionList.add(exp2);
		expressionList.add(exp3);
		expressionList.add(exp4);
		expressionList.add(exp5);
		expressionList.add(exp6);
		expressionList.add(exp7);
		expressionList.add(exp8);
		expressionList.add(exp9);
		expressionList.add(exp10);

		expressionList.add(exp11);
		expressionList.add(exp12);
		expressionList.add(exp13);
		expressionList.add(exp14);
		expressionList.add(exp15);
		expressionList.add(exp16);
		expressionList.add(exp17);
		expressionList.add(exp18);
		expressionList.add(exp19);
		expressionList.add(exp20);

		expressionList.add(exp21);
		expressionList.add(exp22);
		expressionList.add(exp23);
		expressionList.add(exp24);
		expressionList.add(exp25);
		expressionList.add(exp26);
		expressionList.add(exp27);
		expressionList.add(exp28);
		expressionList.add(exp29);
		expressionList.add(exp30);

		expressionList.add(exp31);
		expressionList.add(exp32);
		expressionList.add(exp33);
		expressionList.add(exp34);
		expressionList.add(exp35);
		expressionList.add(exp36);
		expressionList.add(exp37);
		expressionList.add(exp38);
		expressionList.add(exp39);
		expressionList.add(exp40);

		expressionList.add(exp41);
		expressionList.add(exp42);
		expressionList.add(exp43);
		expressionList.add(exp44);
		expressionList.add(exp45);
		expressionList.add(exp46);
		expressionList.add(exp47);
		expressionList.add(exp48);
		expressionList.add(exp49);
		expressionList.add(exp50);

		expressionList.add(exp51);
		expressionList.add(exp52);
		expressionList.add(exp53);
		expressionList.add(exp54);
		expressionList.add(exp55);
		expressionList.add(exp56);
		expressionList.add(exp57);
		expressionList.add(exp58);
		expressionList.add(exp59);
		expressionList.add(exp60);

		expressionList.add(exp61);
		expressionList.add(exp62);
		expressionList.add(exp63);
		expressionList.add(exp64);
		expressionList.add(exp65);
		expressionList.add(exp66);
		expressionList.add(exp67);
		expressionList.add(exp68);
		expressionList.add(exp69);
		expressionList.add(exp70);
		
		expressionList.add(exp71);
		expressionList.add(exp72);
		expressionList.add(exp73);
		expressionList.add(exp74);
		expressionList.add(exp75);
		expressionList.add(exp76);
		expressionList.add(exp77);
		expressionList.add(exp78);
		expressionList.add(exp79);
		expressionList.add(exp80);
		
		expressionList.add(exp81);
		expressionList.add(exp82);
		expressionList.add(exp83);
		expressionList.add(exp84);
		expressionList.add(exp85);
		expressionList.add(exp86);
		expressionList.add(exp87);
		expressionList.add(exp88);
		expressionList.add(exp89);
		expressionList.add(exp90);
		
		expressionList.add(exp91);
		expressionList.add(exp92);
		expressionList.add(exp93);
		expressionList.add(exp94);
		expressionList.add(exp95);
		expressionList.add(exp96);
		expressionList.add(exp97);
		expressionList.add(exp98);
		expressionList.add(exp99);
		expressionList.add(exp100);
		
		expressionList.add(exp101);
		expressionList.add(exp102);
		expressionList.add(exp103);
		expressionList.add(exp104);
		expressionList.add(exp105);
		expressionList.add(exp106);
		expressionList.add(exp107);
		expressionList.add(exp108);
		expressionList.add(exp109);
		expressionList.add(exp110);
		
		expressionList.add(exp111);
		expressionList.add(exp112);
		expressionList.add(exp113);
		expressionList.add(exp114);
		expressionList.add(exp115);
		expressionList.add(exp116);
		expressionList.add(exp117);
		expressionList.add(exp118);
		expressionList.add(exp119);
		expressionList.add(exp120);
		
		expressionList.add(exp121);
		expressionList.add(exp122);
		expressionList.add(exp123);
		expressionList.add(exp124);
		expressionList.add(exp125);
		
		expressionList.add(exp126);
		expressionList.add(exp127);
		expressionList.add(exp128);
	}
	

}