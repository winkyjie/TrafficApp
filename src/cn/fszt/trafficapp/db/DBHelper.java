package cn.fszt.trafficapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 创建数据库和创建表
 * @author AeiouKong
 *
 */
public class DBHelper extends SQLiteOpenHelper {  
	  
    private static final String DATABASE_NAME = "fm924.db";  
    private static final int DATABASE_VERSION = 30;  
      
    public DBHelper(Context context) {  
        //CursorFactory设置为null,使用默认值   
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }  
  
    //数据库第一次被创建时onCreate会被调用   
    @Override  
    public void onCreate(SQLiteDatabase db) {  
    	
        db.execSQL("CREATE TABLE IF NOT EXISTS text" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, address VARCHAR, event VARCHAR," +
                " createtime VARCHAR, remark TEXT)");  
        db.execSQL("CREATE TABLE IF NOT EXISTS huodong" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR, hdinfoid VARCHAR," +
                " newimage VARCHAR, createdate VARCHAR, contenttype VARCHAR, commentcount VARCHAR,"
                + "views VARCHAR)");  
        db.execSQL("CREATE TABLE IF NOT EXISTS news" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR, hdnewid VARCHAR," +
                " newimage VARCHAR, createdate VARCHAR, istop VARCHAR)");  
		db.execSQL("CREATE TABLE IF NOT EXISTS replays" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, hdreplaytypeid VARCHAR, typename VARCHAR," +
                " imagepath VARCHAR, livetime VARCHAR, djname VARCHAR)"); 
		db.execSQL("CREATE TABLE IF NOT EXISTS hudongchat" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, hdmenuid VARCHAR, nickname VARCHAR, headimg VARCHAR, content VARCHAR," +
                " hdcommentid VARCHAR, createdate VARCHAR, istop VARCHAR, imagepath VARCHAR" +
                ", voicepath VARCHAR, islive VARCHAR)");
		db.execSQL("CREATE TABLE IF NOT EXISTS replayitem" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, hdreplaytypeid VARCHAR, hdreplayid VARCHAR, imagepath VARCHAR," +
                " title VARCHAR, voicepath VARCHAR, createdate VARCHAR)");
		db.execSQL("CREATE TABLE IF NOT EXISTS oauthusers" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, auth_time VARCHAR, expires_in VARCHAR, openid VARCHAR," +
                " access_token VARCHAR, validate_time VARCHAR, site VARCHAR)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS s_carlife" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, connected_uid VARCHAR, hdinfoid VARCHAR," +
                " hdinfocommentid VARCHAR, content VARCHAR, imagepath VARCHAR, voicepath VARCHAR, videopath VARCHAR," +
                " createdate VARCHAR)"); 
		db.execSQL("CREATE TABLE IF NOT EXISTS s_huodong" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, connected_uid VARCHAR, hdinfoid VARCHAR," +
                " hdinfocommentid VARCHAR, content VARCHAR, imagepath VARCHAR, voicepath VARCHAR, videopath VARCHAR," +
                " createdate VARCHAR)"); 
		db.execSQL("CREATE TABLE IF NOT EXISTS s_baoming" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, connected_uid VARCHAR, hdactivityresultid VARCHAR, hdinfoid VARCHAR," +
                " remark VARCHAR, createdate VARCHAR)");
		db.execSQL("CREATE TABLE IF NOT EXISTS s_tuangou" +  
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, connected_uid VARCHAR, hdgroupresultid VARCHAR, hdinfoid VARCHAR," +
                " remark VARCHAR, createdate VARCHAR)");
		db.execSQL("CREATE TABLE IF NOT EXISTS s_vote" +  
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, connected_uid VARCHAR, hdpersonid VARCHAR, hdvoteid VARCHAR," +
                " hdvotelogid VARCHAR, remark VARCHAR, createdate VARCHAR)");
		db.execSQL("CREATE TABLE IF NOT EXISTS s_radiohudong" +  
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, connected_uid VARCHAR, hdcommentid VARCHAR," +
                " content VARCHAR, imagepath VARCHAR, voicepath VARCHAR, videopath VARCHAR," +
                " createdate VARCHAR)"); 
		db.execSQL("CREATE TABLE IF NOT EXISTS s_news" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, connected_uid VARCHAR, hdnewid VARCHAR," +
                " hdnewcommentid VARCHAR, content VARCHAR, imagepath VARCHAR, voicepath VARCHAR, videopath VARCHAR," +
                " createdate VARCHAR)"); 
		
		db.execSQL("CREATE TABLE IF NOT EXISTS baoliaoandbaoguang" +  
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, type VARCHAR, connected_uid VARCHAR, nickname VARCHAR," +
				" headimg VARCHAR, isdj VARCHAR, intro VARCHAR, content VARCHAR, imagepath VARCHAR,"+ 
				" imagepath_small VARCHAR, videopath VARCHAR, createtime VARCHAR, stcollectid VARCHAR"+ 
				", replycount VARCHAR, likecount VARCHAR, istransmit VARCHAR, transmiturl VARCHAR, istoppath VARCHAR,"
				+ "isdjpath VARCHAR)"); 
		
		db.execSQL("CREATE TABLE IF NOT EXISTS homeinfo" +  
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, hdinfoid VARCHAR, title VARCHAR, hdtype VARCHAR," +
				" newimage VARCHAR)"); 
		
		db.execSQL("CREATE TABLE IF NOT EXISTS m_msglist" +  
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, msgid VARCHAR, msgtitle VARCHAR, msgtype VARCHAR," +
				" starttime VARCHAR)"); 
		
		db.execSQL("CREATE TABLE IF NOT EXISTS m_awardlist" +  
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, hdawardid VARCHAR, mobile VARCHAR, nickname VARCHAR," +
				" awardcontent VARCHAR, createtime VARCHAR)"); 
		
		db.execSQL("CREATE TABLE IF NOT EXISTS sharelistheader" +  
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, hdcommentid VARCHAR, hdmenuid VARCHAR, nickname VARCHAR," +
				" headimg VARCHAR, createdate VARCHAR, likecount VARCHAR, replycount VARCHAR, content VARCHAR"
				+ ", connected_uid VARCHAR, isdjpath VARCHAR, istoppath VARCHAR)"); 
		
		db.execSQL("CREATE TABLE IF NOT EXISTS mainhotlist" +  
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, flag VARCHAR, id VARCHAR, imagepath VARCHAR, title VARCHAR,"
				+ "createdate VARCHAR)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS mainadlist" +  
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, hdinfoid VARCHAR, title VARCHAR, price VARCHAR, imagepath VARCHAR)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS audiolist" +  
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, hdaudiotypeid VARCHAR, typename VARCHAR, typedesc VARCHAR, "
				+ "isusered VARCHAR, imagepath VARCHAR)"); 
		
		db.execSQL("CREATE TABLE IF NOT EXISTS audioprogramme" +  
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, hdaudioid VARCHAR, hdaudiotypeid VARCHAR, title VARCHAR, "
				+ "voicepath VARCHAR, imagepath VARCHAR, createdate VARCHAR, playcount VARCHAR)"); 
		db.execSQL("CREATE TABLE IF NOT EXISTS audiodownload" +  
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, hdaudioid VARCHAR, hdaudiotypeid VARCHAR, title VARCHAR, "
				+ "voicepath VARCHAR, imagepath VARCHAR, createdate VARCHAR, playcount VARCHAR,filepath VARCHAR)"); 
		db.execSQL("CREATE TABLE IF NOT EXISTS carinfo" +  
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, hdpersoncarid VARCHAR, connected_uid VARCHAR, carnum VARCHAR, "
				+ "cartypecode VARCHAR, lastfour VARCHAR, createdate VARCHAR)"); 
    }  
  
    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade   
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
//        db.execSQL("ALTER TABLE person ADD COLUMN other STRING");  
        db.execSQL("DROP TABLE IF EXISTS text");  
        db.execSQL("DROP TABLE IF EXISTS huodong");  
        db.execSQL("DROP TABLE IF EXISTS news");  
        db.execSQL("DROP TABLE IF EXISTS replays");  
        db.execSQL("DROP TABLE IF EXISTS huodongitem");  
//        db.execSQL("DROP TABLE IF EXISTS newsitem");  
        db.execSQL("DROP TABLE IF EXISTS hudongchat");  
        db.execSQL("DROP TABLE IF EXISTS replayitem");  
        
        db.execSQL("DROP TABLE IF EXISTS s_carlife");  
        db.execSQL("DROP TABLE IF EXISTS s_huodong"); 
        db.execSQL("DROP TABLE IF EXISTS s_vote"); 
        db.execSQL("DROP TABLE IF EXISTS s_radiohudong"); 
        db.execSQL("DROP TABLE IF EXISTS s_baoming"); 
        db.execSQL("DROP TABLE IF EXISTS s_tuangou");
        db.execSQL("DROP TABLE IF EXISTS s_news");
        
        db.execSQL("DROP TABLE IF EXISTS m_msglist");
        db.execSQL("DROP TABLE IF EXISTS m_awardlist");
        
        db.execSQL("DROP TABLE IF EXISTS homeinfo");
        db.execSQL("DROP TABLE IF EXISTS baoliaoandbaoguang"); 
        db.execSQL("DROP TABLE IF EXISTS sharelistheader"); 
        db.execSQL("DROP TABLE IF EXISTS mainhotlist"); 
        db.execSQL("DROP TABLE IF EXISTS mainadlist"); 
        db.execSQL("DROP TABLE IF EXISTS audiolist"); 
        db.execSQL("DROP TABLE IF EXISTS audioprogramme"); 
        db.execSQL("DROP TABLE IF EXISTS audiodownload"); 
        db.execSQL("DROP TABLE IF EXISTS carinfo"); 
        onCreate(db); 
    }  
    
    public boolean deleteDatabase(Context context){
    	return context.deleteDatabase(DATABASE_NAME);
    }
}  

