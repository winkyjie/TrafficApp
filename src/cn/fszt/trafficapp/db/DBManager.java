package cn.fszt.trafficapp.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.fszt.trafficapp.domain.AudioListData;
import cn.fszt.trafficapp.domain.AudioProgrammeData;
import cn.fszt.trafficapp.domain.AwardlistData;
import cn.fszt.trafficapp.domain.BaoliaoAndBaoguangData;
import cn.fszt.trafficapp.domain.BaomingData;
import cn.fszt.trafficapp.domain.CarInfoData;
import cn.fszt.trafficapp.domain.CarlifeAndHuodongData;
import cn.fszt.trafficapp.domain.ChatData;
import cn.fszt.trafficapp.domain.HomeInfoData;
import cn.fszt.trafficapp.domain.ActListData;
import cn.fszt.trafficapp.domain.HuodongItemData;
import cn.fszt.trafficapp.domain.MainAdListData;
import cn.fszt.trafficapp.domain.MainHotListData;
import cn.fszt.trafficapp.domain.MsglistData;
import cn.fszt.trafficapp.domain.NewsData;
import cn.fszt.trafficapp.domain.NewsItemData;
import cn.fszt.trafficapp.domain.NewsSpaceData;
import cn.fszt.trafficapp.domain.OauthUsers;
import cn.fszt.trafficapp.domain.RadioHudongData;
import cn.fszt.trafficapp.domain.ReplaysData;
import cn.fszt.trafficapp.domain.ReplaysItemData;
import cn.fszt.trafficapp.domain.ShareListHeaderData;
import cn.fszt.trafficapp.domain.TextData;
import cn.fszt.trafficapp.domain.TuangouData;
import cn.fszt.trafficapp.domain.VoteData;

/**
 * 封装常用增删改查业务方法
 * 
 * @author AeiouKong
 *
 */
public class DBManager {
	private DBHelper helper;
	private SQLiteDatabase db;

	public DBManager(Context context) {
		helper = new DBHelper(context);
		// boolean b = helper.deleteDatabase(context);
		// System.out.println("能否删除数据库====="+b);
		// 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
		// mFactory);
		// 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
		db = helper.getWritableDatabase();
	}

	public void addText(List<TextData> texts) {
		db.beginTransaction(); // 开始事务
		try {
			for (TextData text : texts) {
				db.execSQL("INSERT INTO text VALUES(null, ?, ?, ?, ?)",
						new Object[] { text.getAddress(), text.getEvent(), text.getCreatetime(), text.getRemark() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addHuodong(List<ActListData> huodongs, String contenttype) {
		db.beginTransaction(); // 开始事务
		try {
			for (ActListData huodong : huodongs) {
				db.execSQL("INSERT INTO huodong VALUES(null, ?, ?, ?, ?, ?, ?, ?)",
						new Object[] { huodong.getTitle(), huodong.getHdinfoid(), huodong.getNewimage(),
								huodong.getCreatedate(), contenttype, huodong.getCommentcount(), huodong.getViews() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addHuodongitem(List<HuodongItemData> huodongitems) {
		db.beginTransaction(); // 开始事务
		try {
			for (HuodongItemData huodongitem : huodongitems) {
				db.execSQL(
						"INSERT INTO huodongitem VALUES(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
						new Object[] { huodongitem.getHdinfoid(), huodongitem.getTitle(), huodongitem.getContent(),
								huodongitem.getCreatedate(), huodongitem.getNewimage(), huodongitem.getNewimagedesc(),
								huodongitem.getImagepath1(), huodongitem.getImagedesc1(), huodongitem.getImagepath2(),
								huodongitem.getImagedesc2(), huodongitem.getImagepath3(), huodongitem.getImagedesc3(),
								huodongitem.getImagepath4(), huodongitem.getImagedesc4(), huodongitem.getImagepath5(),
								huodongitem.getImagedesc5(), huodongitem.getImagepath6(), huodongitem.getImagedesc6(),
								huodongitem.getImagepath7(), huodongitem.getImagedesc7(), huodongitem.getImagepath8(),
								huodongitem.getImagedesc8(), huodongitem.getImagepath9(), huodongitem.getImagedesc9(),
								huodongitem.getImagepath10(), huodongitem.getImagedesc10(), huodongitem.getVoicepath(),
								huodongitem.getVoicedesc(), huodongitem.getVideopath(), huodongitem.getVideodesc() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addNews(List<NewsData> newss) {
		db.beginTransaction(); // 开始事务
		try {
			for (NewsData news : newss) {
				db.execSQL("INSERT INTO news VALUES(null, ?, ?, ?, ?, ?)", new Object[] { news.getTitle(),
						news.getHdnewid(), news.getNewimage(), news.getCreatedate(), news.getIstop() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addNewsitem(List<NewsItemData> newsitems) {
		db.beginTransaction(); // 开始事务
		try {
			for (NewsItemData newsitem : newsitems) {
				db.execSQL(
						"INSERT INTO newsitem VALUES(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
						new Object[] { newsitem.getHdnewid(), newsitem.getTitle(), newsitem.getContent(),
								newsitem.getCreatedate(), newsitem.getNewimage(), newsitem.getNewimagedesc(),
								newsitem.getImagepath1(), newsitem.getImagedesc1(), newsitem.getImagepath2(),
								newsitem.getImagedesc2(), newsitem.getImagepath3(), newsitem.getImagedesc3(),
								newsitem.getImagepath4(), newsitem.getImagedesc4(), newsitem.getImagepath5(),
								newsitem.getImagedesc5(), newsitem.getImagepath6(), newsitem.getImagedesc6(),
								newsitem.getImagepath7(), newsitem.getImagedesc7(), newsitem.getImagepath8(),
								newsitem.getImagedesc8(), newsitem.getImagepath9(), newsitem.getImagedesc9(),
								newsitem.getImagepath10(), newsitem.getImagedesc10(), newsitem.getVoicepath(),
								newsitem.getVoicedesc(), newsitem.getVideopath(), newsitem.getVideodesc() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addReplays(List<ReplaysData> replayss) {
		db.beginTransaction(); // 开始事务
		try {
			for (ReplaysData replays : replayss) {
				db.execSQL("INSERT INTO replays VALUES(null, ?, ?, ?, ?, ?)",
						new Object[] { replays.getHdreplaytypeid(), replays.getTypename(), replays.getImagepath(),
								replays.getLivetime(), replays.getDjname() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addHudongchat(List<ChatData> hudongchats, String islive) {
		db.beginTransaction(); // 开始事务
		try {
			for (ChatData hudongchat : hudongchats) {
				db.execSQL("INSERT INTO hudongchat VALUES(null,?,?,?,?,?,?,?,?,?,?)",
						new Object[] { hudongchat.getHdmenuid(), hudongchat.getNickname(), hudongchat.getHeadimg(),
								hudongchat.getContent(), hudongchat.getHdcommentid(), hudongchat.getCreatedate(),
								hudongchat.getIstop(), hudongchat.getImagepath(), hudongchat.getVoicepath(), islive });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addReplayitem(List<ReplaysItemData> replayitems) {
		db.beginTransaction(); // 开始事务
		try {
			for (ReplaysItemData replayitem : replayitems) {
				db.execSQL("INSERT INTO replayitem VALUES(null,?,?,?,?,?,?)",
						new Object[] { replayitem.getHdreplaytypeid(), replayitem.getHdreplayid(),
								replayitem.getImagepath(), replayitem.getTitle(), replayitem.getVoicepath(),
								replayitem.getCreatedate() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addOauthusers(List<OauthUsers> oauthusers) {
		db.beginTransaction(); // 开始事务
		try {
			for (OauthUsers oauthuser : oauthusers) {
				db.execSQL("INSERT INTO oauthusers VALUES(null,?,?,?,?,?,?)",
						new Object[] { oauthuser.getAuth_time(), oauthuser.getExpires_in(), oauthuser.getOpenid(),
								oauthuser.getAccess_token(), oauthuser.getValidate_time(), oauthuser.getSite() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addS_carlife(List<CarlifeAndHuodongData> carlifes) {
		db.beginTransaction(); // 开始事务
		try {
			for (CarlifeAndHuodongData carlife : carlifes) {
				db.execSQL("INSERT INTO s_carlife VALUES(null, ?, ?, ?, ?, ?, ?, ?, ?)",
						new Object[] { carlife.getConnected_uid(), carlife.getHdinfoid(), carlife.getHdinfocommentid(),
								carlife.getContent(), carlife.getImagepath(), carlife.getVoicepath(),
								carlife.getVideopath(), carlife.getCreatedate() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addS_huodong(List<CarlifeAndHuodongData> huodongs) {
		db.beginTransaction(); // 开始事务
		try {
			for (CarlifeAndHuodongData huodong : huodongs) {
				db.execSQL("INSERT INTO s_huodong VALUES(null, ?, ?, ?, ?, ?, ?, ?, ?)",
						new Object[] { huodong.getConnected_uid(), huodong.getHdinfoid(), huodong.getHdinfocommentid(),
								huodong.getContent(), huodong.getImagepath(), huodong.getVoicepath(),
								huodong.getVideopath(), huodong.getCreatedate() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addS_baoming(List<BaomingData> baomings) {
		db.beginTransaction(); // 开始事务
		try {
			for (BaomingData baoming : baomings) {
				db.execSQL("INSERT INTO s_baoming VALUES(null, ?, ?, ?, ?, ?)",
						new Object[] { baoming.getConnected_uid(), baoming.getHdactivityresultid(),
								baoming.getHdinfoid(), baoming.getRemark(), baoming.getCreatedate() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addS_tuangou(List<TuangouData> tuangous) {
		db.beginTransaction(); // 开始事务
		try {
			for (TuangouData tuangou : tuangous) {
				db.execSQL("INSERT INTO s_tuangou VALUES(null, ?, ?, ?, ?, ?)",
						new Object[] { tuangou.getConnected_uid(), tuangou.getHdgroupresultid(), tuangou.getHdinfoid(),
								tuangou.getRemark(), tuangou.getCreatedate() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addS_vote(List<VoteData> votes) {
		db.beginTransaction(); // 开始事务
		try {
			for (VoteData vote : votes) {
				db.execSQL("INSERT INTO s_vote VALUES(null, ?, ?, ?, ?, ?, ?)",
						new Object[] { vote.getConnected_uid(), vote.getHdpersonid(), vote.getHdvoteid(),
								vote.getHdvotelogid(), vote.getRemark(), vote.getCreatedate() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addS_radiohudong(List<RadioHudongData> radiohudongs) {
		db.beginTransaction(); // 开始事务
		try {
			for (RadioHudongData radiohudong : radiohudongs) {
				db.execSQL("INSERT INTO s_radiohudong VALUES(null, ?, ?, ?, ?, ?, ?, ?)",
						new Object[] { radiohudong.getConnected_uid(), radiohudong.getHdcommentid(),
								radiohudong.getContent(), radiohudong.getImagepath(), radiohudong.getVoicepath(),
								radiohudong.getVideopath(), radiohudong.getCreatedate() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addS_news(List<NewsSpaceData> newss) {
		db.beginTransaction(); // 开始事务
		try {
			for (NewsSpaceData news : newss) {
				db.execSQL("INSERT INTO s_news VALUES(null, ?, ?, ?, ?, ?, ?, ?, ?)",
						new Object[] { news.getConnected_uid(), news.getHdnewid(), news.getHdnewcommentid(),
								news.getContent(), news.getImagepath(), news.getVoicepath(), news.getVideopath(),
								news.getCreatedate() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addBaoliaoandbaoguangByType(List<BaoliaoAndBaoguangData> baoliaoandbaoguangs, String type) {
		db.beginTransaction(); // 开始事务
		try {
			for (BaoliaoAndBaoguangData baoliaoandbaoguang : baoliaoandbaoguangs) {
				db.execSQL(
						"INSERT INTO baoliaoandbaoguang VALUES(null,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
						new Object[] { type, baoliaoandbaoguang.getConnected_uid(), baoliaoandbaoguang.getNickname(),
								baoliaoandbaoguang.getHeadimg(), baoliaoandbaoguang.getIsdj(),
								baoliaoandbaoguang.getIntro(), baoliaoandbaoguang.getContent(),
								baoliaoandbaoguang.getImagepath(), baoliaoandbaoguang.getImagepath_small(),
								baoliaoandbaoguang.getVideopath(), baoliaoandbaoguang.getCreatetime(),
								baoliaoandbaoguang.getStcollectid(), baoliaoandbaoguang.getReplycount(),
								baoliaoandbaoguang.getLikecount(), baoliaoandbaoguang.getIstransmit(),
								baoliaoandbaoguang.getTransmiturl(), baoliaoandbaoguang.getIstoppath(),
								baoliaoandbaoguang.getIsdjpath() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addHomeinfo(List<HomeInfoData> homeinfos) {
		db.beginTransaction(); // 开始事务
		try {
			for (HomeInfoData homeinfo : homeinfos) {
				db.execSQL("INSERT INTO homeinfo VALUES(null,?,?,?,?)", new Object[] { homeinfo.getHdinfoid(),
						homeinfo.getTitle(), homeinfo.getHdtype(), homeinfo.getNewimage() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addM_msglist(List<MsglistData> msglists) {
		db.beginTransaction(); // 开始事务
		try {
			for (MsglistData msglist : msglists) {
				db.execSQL("INSERT INTO m_msglist VALUES(null, ?, ?, ?, ?)", new Object[] { msglist.getMsgid(),
						msglist.getMsgtitle(), msglist.getMsgtype(), msglist.getStarttime() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addM_awardlist(List<AwardlistData> awardlists) {
		db.beginTransaction(); // 开始事务
		try {
			for (AwardlistData awardlist : awardlists) {
				db.execSQL("INSERT INTO m_awardlist VALUES(null, ?, ?, ?, ?, ?)",
						new Object[] { awardlist.getHdawardid(), awardlist.getMobile(), awardlist.getNickname(),
								awardlist.getAwardcontent(), awardlist.getCreatetime() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addShareListHeader(List<ShareListHeaderData> headers) {
		db.beginTransaction(); // 开始事务
		try {
			for (ShareListHeaderData header : headers) {
				db.execSQL("INSERT INTO sharelistheader VALUES(null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
						new Object[] { header.getHdcommentid(), header.getHdmenuid(), header.getNickname(),
								header.getHeadimg(), header.getCreatedate(), header.getLikecount(),
								header.getReplycount(), header.getContent(), header.getConnected_uid(),
								header.getIsdjpath(), header.getIstoppath() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addMainHotList(List<MainHotListData> hots) {
		db.beginTransaction(); // 开始事务
		try {
			for (MainHotListData hot : hots) {
				db.execSQL("INSERT INTO mainhotlist VALUES(null, ?, ?, ?, ?, ?)", new Object[] { hot.getFlag(),
						hot.getId(), hot.getImagepath(), hot.getTitle(), hot.getCreatedate() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addMainAdList(List<MainAdListData> ads) {
		db.beginTransaction(); // 开始事务
		try {
			for (MainAdListData ad : ads) {
				db.execSQL("INSERT INTO mainadlist VALUES(null, ?, ?, ?, ?)",
						new Object[] { ad.getHdinfoid(), ad.getTitle(), ad.getPrice(), ad.getImagepath() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addAudioList(List<AudioListData> audios) {
		db.beginTransaction(); // 开始事务
		try {
			for (AudioListData audio : audios) {
				db.execSQL("INSERT INTO audiolist VALUES(null, ?, ?, ?, ?, ?)", new Object[] { audio.getHdaudiotypeid(),
						audio.getTypename(), audio.getTypedesc(), audio.getIsusered(), audio.getImagepath() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addAudioProgramme(List<AudioProgrammeData> audioprogrammes) {
		db.beginTransaction(); // 开始事务
		try {
			for (AudioProgrammeData audioprogramme : audioprogrammes) {
				db.execSQL("INSERT INTO audioprogramme VALUES(null, ?, ?, ?, ?, ?, ?, ?)",
						new Object[] { audioprogramme.getHdaudioid(), audioprogramme.getHdaudiotypeid(),
								audioprogramme.getTitle(), audioprogramme.getVoicepath(), audioprogramme.getImagepath(),
								audioprogramme.getCreatedate(), audioprogramme.getPlaycount() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	/**
	 * 音频表添加数据(多个)
	 */
	public void addListAudioDownLoad(List<AudioProgrammeData> audios) {
		db.beginTransaction(); // 开始事务
		try {
			for (AudioProgrammeData audio : audios) {
				db.execSQL("INSERT INTO audiodownload VALUES(null, ?, ?, ?, ?, ?, ?, ?,?)",
						new Object[] { audio.getHdaudioid(), audio.getHdaudiotypeid(), audio.getTitle(),
								audio.getVoicepath(), audio.getImagepath(), audio.getCreatedate(), audio.getPlaycount(),
								audio.getFilepath() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	/**
	 * 音频表添加数据
	 */
	public void addAudioDownLoad(AudioProgrammeData audio) {
		db.beginTransaction(); // 开始事务
		try {
			db.execSQL("INSERT INTO audiodownload VALUES(null, ?, ?, ?, ?, ?, ?, ?,?)",
					new Object[] { audio.getHdaudioid(), audio.getHdaudiotypeid(), audio.getTitle(),
							audio.getVoicepath(), audio.getImagepath(), audio.getCreatedate(), audio.getPlaycount(),
							audio.getFilepath() });
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addCarInfo(CarInfoData carinfo) {
		db.beginTransaction(); // 开始事务
		try {
			db.execSQL("INSERT INTO carinfo VALUES(null, ?, ?, ?, ?, ?,?)",
					new Object[] { carinfo.getHdpersoncarid(), carinfo.getConnected_uid(), carinfo.getCarnum(),
							carinfo.getCartypecode(), carinfo.getLastfour(), carinfo.getCreatedate() });
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void deleteText() {
		db.delete("text", null, null);
	}

	public void deleteHuodongByContenttype(String contenttype) {
		db.delete("huodong", "contenttype = ?", new String[] { contenttype });
	}

	public void deleteHuodongitemById(String hdinfoid) {
		db.delete("huodongitem", "hdinfoid=?", new String[] { hdinfoid });
	}

	public void deleteNews() {
		db.delete("news", null, null);
	}

	public void deleteNewsitemById(String hdnewid) {
		db.delete("newsitem", "hdnewid=?", new String[] { hdnewid });
	}

	public void deleteReplays() {
		db.delete("replays", null, null);
	}

	public void deleteOauthusers() {
		db.delete("oauthusers", null, null);
	}

	public void deleteS_carlife() {
		db.delete("s_carlife", null, null);
	}

	public void deleteS_carlifeByhdinfocommentid(String hdinfocommentid) {
		db.delete("s_carlife", "hdinfocommentid=?", new String[] { hdinfocommentid });
	}

	public void deleteS_carlifeByconnected_uid(String connected_uid) {
		db.delete("s_carlife", "connected_uid=?", new String[] { connected_uid });
	}

	public void deleteS_huodong() {
		db.delete("s_huodong", null, null);
	}

	public void deleteS_huodongByhdinfocommentid(String hdinfocommentid) {
		db.delete("s_huodong", "hdinfocommentid=?", new String[] { hdinfocommentid });
	}

	public void deleteS_huodongByconnected_uid(String connected_uid) {
		db.delete("s_huodong", "connected_uid=?", new String[] { connected_uid });
	}

	public void deleteS_baoming() {
		db.delete("s_baoming", null, null);
	}

	public void deleteS_baomingByhdactivityresultid(String hdactivityresultid) {
		db.delete("s_baoming", "hdactivityresultid=?", new String[] { hdactivityresultid });
	}

	public void deleteS_baomingByconnected_uid(String connected_uid) {
		db.delete("s_baoming", "connected_uid=?", new String[] { connected_uid });
	}

	public void deleteS_tuangou() {
		db.delete("s_tuangou", null, null);
	}

	public void deleteS_tuangouByhdgroupresultid(String hdgroupresultid) {
		db.delete("s_tuangou", "hdgroupresultid=?", new String[] { hdgroupresultid });
	}

	public void deleteS_tuangouByconnected_uid(String connected_uid) {
		db.delete("s_tuangou", "connected_uid=?", new String[] { connected_uid });
	}

	public void deleteS_vote() {
		db.delete("s_vote", null, null);
	}

	public void deleteS_voteByconnected_uid(String connected_uid) {
		db.delete("s_vote", "connected_uid=?", new String[] { connected_uid });
	}

	public void deleteS_radiohudong() {
		db.delete("s_radiohudong", null, null);
	}

	public void deleteS_radiohudongByhdcommentid(String hdcommentid) {
		db.delete("s_radiohudong", "hdcommentid=?", new String[] { hdcommentid });
	}

	public void deleteS_radiohudongByconnected_uid(String connected_uid) {
		db.delete("s_radiohudong", "connected_uid=?", new String[] { connected_uid });
	}

	public void deleteS_newsByconnected_uid(String connected_uid) {
		db.delete("s_news", "connected_uid=?", new String[] { connected_uid });
	}

	public void deleteS_newsByhdnewcommentid(String hdnewcommentid) {
		db.delete("s_news", "hdnewcommentid=?", new String[] { hdnewcommentid });
	}

	// islive=0 直播，islive=1 点播
	public void deleteHudongchatById(String hdmenuid, String islive) {
		db.delete("hudongchat", "hdmenuid=? and islive=?", new String[] { hdmenuid, islive });
	}

	public void deleteHudongchat(String islive) {
		db.delete("hudongchat", "islive=?", new String[] { islive });
	}

	public void deleteReplayitemById(String hdreplaytypeid) {
		db.delete("replayitem", "hdreplaytypeid=?", new String[] { hdreplaytypeid });
	}

	public void deleteBaoliaoandbaoguangByType(String type) {
		db.delete("baoliaoandbaoguang", "type=?", new String[] { type });
	}

	public void deleteHomeinfo() {
		db.delete("homeinfo", null, null);
	}

	public void deleteM_msglist() {
		db.delete("m_msglist", null, null);
	}

	public void deleteM_awardlist() {
		db.delete("m_awardlist", null, null);
	}

	public void deleteShareListHeader() {
		db.delete("sharelistheader", null, null);
	}

	public void deleteMainHotList() {
		db.delete("mainhotlist", null, null);
	}

	public void deleteMainAdList() {
		db.delete("mainadlist", null, null);
	}

	public void deleteAudioList() {
		db.delete("audiolist", null, null);
	}

	public void deleteAudioProgrammeByHdaudiotypeid(String hdaudiotypeid) {
		db.delete("audioprogramme", "hdaudiotypeid=?", new String[] { hdaudiotypeid });
	}

	public void deleteAudioDownLoadByHdaudiotypeid(String hdaudiotypeid, String audioId) {
		db.delete("audiodownload", "hdaudiotypeid=? and hdaudioid=?", new String[] { hdaudiotypeid, audioId });
	}

	public void deleteCarInfoByCarInfoId(String carinfoId) {
		db.delete("carinfo", "hdpersoncarid=?", new String[] { carinfoId });
	}

	public void deleteCarInfoAll() {
		db.delete("carinfo", null, null);
	}

	public List<TextData> queryText() {
		ArrayList<TextData> texts = new ArrayList<TextData>();
		Cursor c = queryTextByTheCursor();
		while (c.moveToNext()) {
			TextData text = new TextData();
			text.set_id(c.getInt(c.getColumnIndex("_id")));
			text.setAddress(c.getString(c.getColumnIndex("address")));
			text.setCreatetime(c.getString(c.getColumnIndex("createtime")));
			text.setEvent(c.getString(c.getColumnIndex("event")));
			text.setRemark(c.getString(c.getColumnIndex("remark")));
			texts.add(text);
		}
		c.close();
		return texts;
	}

	public List<ActListData> queryHuodongByContenttype(String contenttype) {
		ArrayList<ActListData> huodongs = new ArrayList<ActListData>();
		Cursor c = queryHuodongByTheCursor(contenttype);
		while (c.moveToNext()) {
			ActListData huodong = new ActListData();
			huodong.set_id(c.getInt(c.getColumnIndex("_id")));
			huodong.setTitle(c.getString(c.getColumnIndex("title")));
			huodong.setHdinfoid(c.getString(c.getColumnIndex("hdinfoid")));
			huodong.setNewimage(c.getString(c.getColumnIndex("newimage")));
			huodong.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			huodong.setCommentcount(c.getString(c.getColumnIndex("commentcount")));
			huodong.setViews(c.getString(c.getColumnIndex("views")));
			huodongs.add(huodong);
		}
		c.close();
		return huodongs;
	}

	public List<HuodongItemData> queryHuodongitemById(String hdinfoid) {
		ArrayList<HuodongItemData> huodongitems = new ArrayList<HuodongItemData>();
		Cursor c = queryHuodongitemByTheCursor(hdinfoid);
		while (c.moveToNext()) {
			HuodongItemData huodongitem = new HuodongItemData();
			huodongitem.set_id(c.getInt(c.getColumnIndex("_id")));
			huodongitem.setTitle(c.getString(c.getColumnIndex("title")));
			huodongitem.setContent(c.getString(c.getColumnIndex("content")));
			huodongitem.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			huodongitem.setNewimage(c.getString(c.getColumnIndex("newimage")));
			huodongitem.setNewimagedesc(c.getString(c.getColumnIndex("newimagedesc")));
			huodongitem.setImagepath1(c.getString(c.getColumnIndex("imagepath1")));
			huodongitem.setImagedesc1(c.getString(c.getColumnIndex("imagedesc1")));
			huodongitem.setImagepath2(c.getString(c.getColumnIndex("imagepath2")));
			huodongitem.setImagedesc2(c.getString(c.getColumnIndex("imagedesc2")));
			huodongitem.setImagepath3(c.getString(c.getColumnIndex("imagepath3")));
			huodongitem.setImagedesc3(c.getString(c.getColumnIndex("imagedesc3")));
			huodongitem.setImagepath4(c.getString(c.getColumnIndex("imagepath4")));
			huodongitem.setImagedesc4(c.getString(c.getColumnIndex("imagedesc4")));
			huodongitem.setImagepath5(c.getString(c.getColumnIndex("imagepath5")));
			huodongitem.setImagedesc5(c.getString(c.getColumnIndex("imagedesc5")));
			huodongitem.setImagepath6(c.getString(c.getColumnIndex("imagepath6")));
			huodongitem.setImagedesc6(c.getString(c.getColumnIndex("imagedesc6")));
			huodongitem.setImagepath7(c.getString(c.getColumnIndex("imagepath7")));
			huodongitem.setImagedesc7(c.getString(c.getColumnIndex("imagedesc7")));
			huodongitem.setImagepath8(c.getString(c.getColumnIndex("imagepath8")));
			huodongitem.setImagedesc8(c.getString(c.getColumnIndex("imagedesc8")));
			huodongitem.setImagepath9(c.getString(c.getColumnIndex("imagepath9")));
			huodongitem.setImagedesc9(c.getString(c.getColumnIndex("imagedesc9")));
			huodongitem.setImagepath10(c.getString(c.getColumnIndex("imagepath10")));
			huodongitem.setImagedesc10(c.getString(c.getColumnIndex("imagedesc10")));
			huodongitem.setVoicepath(c.getString(c.getColumnIndex("voicepath")));
			huodongitem.setVoicedesc(c.getString(c.getColumnIndex("voicedesc")));
			huodongitem.setVideopath(c.getString(c.getColumnIndex("videopath")));
			huodongitem.setVideodesc(c.getString(c.getColumnIndex("videodesc")));
			huodongitems.add(huodongitem);
		}
		c.close();
		return huodongitems;
	}

	public List<NewsData> queryNews() {
		ArrayList<NewsData> newss = new ArrayList<NewsData>();
		Cursor c = queryNewsByTheCursor();
		while (c.moveToNext()) {
			NewsData news = new NewsData();
			news.set_id(c.getInt(c.getColumnIndex("_id")));
			news.setTitle(c.getString(c.getColumnIndex("title")));
			news.setHdnewid(c.getString(c.getColumnIndex("hdnewid")));
			news.setNewimage(c.getString(c.getColumnIndex("newimage")));
			news.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			news.setIstop(c.getString(c.getColumnIndex("istop")));
			newss.add(news);
		}
		c.close();
		return newss;
	}

	public List<NewsItemData> queryNewsitemById(String hdnewid) {
		ArrayList<NewsItemData> newsitems = new ArrayList<NewsItemData>();
		Cursor c = queryNewsitemByTheCursor(hdnewid);
		while (c.moveToNext()) {
			NewsItemData newsitem = new NewsItemData();
			newsitem.set_id(c.getInt(c.getColumnIndex("_id")));
			newsitem.setTitle(c.getString(c.getColumnIndex("title")));
			newsitem.setContent(c.getString(c.getColumnIndex("content")));
			newsitem.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			newsitem.setNewimage(c.getString(c.getColumnIndex("newimage")));
			newsitem.setNewimagedesc(c.getString(c.getColumnIndex("newimagedesc")));
			newsitem.setImagepath1(c.getString(c.getColumnIndex("imagepath1")));
			newsitem.setImagedesc1(c.getString(c.getColumnIndex("imagedesc1")));
			newsitem.setImagepath2(c.getString(c.getColumnIndex("imagepath2")));
			newsitem.setImagedesc2(c.getString(c.getColumnIndex("imagedesc2")));
			newsitem.setImagepath3(c.getString(c.getColumnIndex("imagepath3")));
			newsitem.setImagedesc3(c.getString(c.getColumnIndex("imagedesc3")));
			newsitem.setImagepath4(c.getString(c.getColumnIndex("imagepath4")));
			newsitem.setImagedesc4(c.getString(c.getColumnIndex("imagedesc4")));
			newsitem.setImagepath5(c.getString(c.getColumnIndex("imagepath5")));
			newsitem.setImagedesc5(c.getString(c.getColumnIndex("imagedesc5")));
			newsitem.setImagepath6(c.getString(c.getColumnIndex("imagepath6")));
			newsitem.setImagedesc6(c.getString(c.getColumnIndex("imagedesc6")));
			newsitem.setImagepath7(c.getString(c.getColumnIndex("imagepath7")));
			newsitem.setImagedesc7(c.getString(c.getColumnIndex("imagedesc7")));
			newsitem.setImagepath8(c.getString(c.getColumnIndex("imagepath8")));
			newsitem.setImagedesc8(c.getString(c.getColumnIndex("imagedesc8")));
			newsitem.setImagepath9(c.getString(c.getColumnIndex("imagepath9")));
			newsitem.setImagedesc9(c.getString(c.getColumnIndex("imagedesc9")));
			newsitem.setImagepath10(c.getString(c.getColumnIndex("imagepath10")));
			newsitem.setImagedesc10(c.getString(c.getColumnIndex("imagedesc10")));
			newsitem.setVoicepath(c.getString(c.getColumnIndex("voicepath")));
			newsitem.setVoicedesc(c.getString(c.getColumnIndex("voicedesc")));
			newsitem.setVideopath(c.getString(c.getColumnIndex("videopath")));
			newsitem.setVideodesc(c.getString(c.getColumnIndex("videodesc")));
			newsitems.add(newsitem);
		}
		c.close();
		return newsitems;
	}

	public List<ReplaysData> queryReplays() {
		ArrayList<ReplaysData> replayss = new ArrayList<ReplaysData>();
		Cursor c = queryReplaysByTheCursor();
		while (c.moveToNext()) {
			ReplaysData replays = new ReplaysData();
			replays.set_id(c.getInt(c.getColumnIndex("_id")));
			replays.setHdreplaytypeid(c.getString(c.getColumnIndex("hdreplaytypeid")));
			replays.setTypename(c.getString(c.getColumnIndex("typename")));
			replays.setImagepath(c.getString(c.getColumnIndex("imagepath")));
			replays.setLivetime(c.getString(c.getColumnIndex("livetime")));
			replays.setDjname(c.getString(c.getColumnIndex("djname")));
			replayss.add(replays);
		}
		c.close();
		return replayss;
	}

	public List<ChatData> queryHudongchatById(String hdmenuid, String islive) {
		ArrayList<ChatData> hudongchats = new ArrayList<ChatData>();
		Cursor c = queryHudongchatByTheCursor(hdmenuid, islive);
		while (c.moveToNext()) {
			ChatData hudongchat = new ChatData();
			hudongchat.set_id(c.getInt(c.getColumnIndex("_id")));
			hudongchat.setHdmenuid(c.getString(c.getColumnIndex("hdmenuid")));
			hudongchat.setNickname(c.getString(c.getColumnIndex("nickname")));
			hudongchat.setHeadimg(c.getString(c.getColumnIndex("headimg")));
			hudongchat.setContent(c.getString(c.getColumnIndex("content")));
			hudongchat.setHdcommentid(c.getString(c.getColumnIndex("hdcommentid")));
			hudongchat.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			hudongchat.setIstop(c.getString(c.getColumnIndex("istop")));
			hudongchat.setImagepath(c.getString(c.getColumnIndex("imagepath")));
			hudongchat.setVoicepath(c.getString(c.getColumnIndex("voicepath")));
			hudongchat.setIslive(islive);
			hudongchats.add(hudongchat);
		}
		c.close();
		return hudongchats;
	}

	public List<ChatData> queryHudongchat(String islive) {
		ArrayList<ChatData> hudongchats = new ArrayList<ChatData>();
		Cursor c = queryHudongchatByTheCursor(islive);
		while (c.moveToNext()) {
			ChatData hudongchat = new ChatData();
			hudongchat.set_id(c.getInt(c.getColumnIndex("_id")));
			hudongchat.setHdmenuid(c.getString(c.getColumnIndex("hdmenuid")));
			hudongchat.setNickname(c.getString(c.getColumnIndex("nickname")));
			hudongchat.setHeadimg(c.getString(c.getColumnIndex("headimg")));
			hudongchat.setContent(c.getString(c.getColumnIndex("content")));
			hudongchat.setHdcommentid(c.getString(c.getColumnIndex("hdcommentid")));
			hudongchat.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			hudongchat.setIstop(c.getString(c.getColumnIndex("istop")));
			hudongchat.setImagepath(c.getString(c.getColumnIndex("imagepath")));
			hudongchat.setVoicepath(c.getString(c.getColumnIndex("voicepath")));
			hudongchat.setIslive(islive);
			hudongchats.add(hudongchat);
		}
		c.close();
		return hudongchats;
	}

	public List<ReplaysItemData> queryReplayitemById(String hdreplaytypeid) {
		ArrayList<ReplaysItemData> replayitems = new ArrayList<ReplaysItemData>();
		Cursor c = queryReplayitemByTheCursor(hdreplaytypeid);
		while (c.moveToNext()) {
			ReplaysItemData replayitem = new ReplaysItemData();
			replayitem.set_id(c.getInt(c.getColumnIndex("_id")));
			replayitem.setHdreplaytypeid(c.getString(c.getColumnIndex("hdreplaytypeid")));
			replayitem.setHdreplayid(c.getString(c.getColumnIndex("hdreplayid")));
			replayitem.setImagepath(c.getString(c.getColumnIndex("imagepath")));
			replayitem.setTitle(c.getString(c.getColumnIndex("title")));
			replayitem.setVoicepath(c.getString(c.getColumnIndex("voicepath")));
			replayitem.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			replayitems.add(replayitem);
		}
		c.close();
		return replayitems;
	}

	public List<OauthUsers> queryOauthusers() {
		ArrayList<OauthUsers> oauthusers = new ArrayList<OauthUsers>();
		Cursor c = queryOauthusersByTheCursor();
		while (c.moveToNext()) {
			OauthUsers oauthuser = new OauthUsers();
			oauthuser.setAuth_time(c.getString(c.getColumnIndex("auth_time")));
			oauthuser.setExpires_in(c.getString(c.getColumnIndex("expires_in")));
			oauthuser.setOpenid(c.getString(c.getColumnIndex("openid")));
			oauthuser.setAccess_token(c.getString(c.getColumnIndex("access_token")));
			oauthuser.setValidate_time(c.getString(c.getColumnIndex("validate_time")));
			oauthuser.setSite(c.getString(c.getColumnIndex("site")));
			oauthusers.add(oauthuser);
		}
		c.close();
		return oauthusers;
	}

	public List<CarlifeAndHuodongData> queryS_carlife() {
		ArrayList<CarlifeAndHuodongData> carlifes = new ArrayList<CarlifeAndHuodongData>();
		Cursor c = queryS_carlifeByTheCursor();
		while (c.moveToNext()) {
			CarlifeAndHuodongData carlife = new CarlifeAndHuodongData();
			carlife.setConnected_uid(c.getString(c.getColumnIndex("connected_uid")));
			carlife.setHdinfoid(c.getString(c.getColumnIndex("hdinfoid")));
			carlife.setHdinfocommentid(c.getString(c.getColumnIndex("hdinfocommentid")));
			carlife.setContent(c.getString(c.getColumnIndex("content")));
			carlife.setImagepath(c.getString(c.getColumnIndex("imagepath")));
			carlife.setVoicepath(c.getString(c.getColumnIndex("voicepath")));
			carlife.setVideopath(c.getString(c.getColumnIndex("videopath")));
			carlife.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			carlifes.add(carlife);
		}
		c.close();
		return carlifes;
	}

	public List<CarlifeAndHuodongData> queryS_carlifeByConnected_uid(String connected_uid) {
		ArrayList<CarlifeAndHuodongData> carlifes = new ArrayList<CarlifeAndHuodongData>();
		Cursor c = queryS_carlifeByConnected_uidTheCursor(connected_uid);
		while (c.moveToNext()) {
			CarlifeAndHuodongData carlife = new CarlifeAndHuodongData();
			carlife.setConnected_uid(c.getString(c.getColumnIndex("connected_uid")));
			carlife.setHdinfoid(c.getString(c.getColumnIndex("hdinfoid")));
			carlife.setHdinfocommentid(c.getString(c.getColumnIndex("hdinfocommentid")));
			carlife.setContent(c.getString(c.getColumnIndex("content")));
			carlife.setImagepath(c.getString(c.getColumnIndex("imagepath")));
			carlife.setVoicepath(c.getString(c.getColumnIndex("voicepath")));
			carlife.setVideopath(c.getString(c.getColumnIndex("videopath")));
			carlife.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			carlifes.add(carlife);
		}
		c.close();
		return carlifes;
	}

	public List<CarlifeAndHuodongData> queryS_huodong() {
		ArrayList<CarlifeAndHuodongData> huodongs = new ArrayList<CarlifeAndHuodongData>();
		Cursor c = queryS_huodongByTheCursor();
		while (c.moveToNext()) {
			CarlifeAndHuodongData huodong = new CarlifeAndHuodongData();
			huodong.setConnected_uid(c.getString(c.getColumnIndex("connected_uid")));
			huodong.setHdinfoid(c.getString(c.getColumnIndex("hdinfoid")));
			huodong.setHdinfocommentid(c.getString(c.getColumnIndex("hdinfocommentid")));
			huodong.setContent(c.getString(c.getColumnIndex("content")));
			huodong.setImagepath(c.getString(c.getColumnIndex("imagepath")));
			huodong.setVoicepath(c.getString(c.getColumnIndex("voicepath")));
			huodong.setVideopath(c.getString(c.getColumnIndex("videopath")));
			huodong.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			huodongs.add(huodong);
		}
		c.close();
		return huodongs;
	}

	public List<CarlifeAndHuodongData> queryS_huodongByConnected_uid(String connected_uid) {
		ArrayList<CarlifeAndHuodongData> huodongs = new ArrayList<CarlifeAndHuodongData>();
		Cursor c = queryS_huodongByConnected_uidTheCursor(connected_uid);
		while (c.moveToNext()) {
			CarlifeAndHuodongData huodong = new CarlifeAndHuodongData();
			huodong.setConnected_uid(c.getString(c.getColumnIndex("connected_uid")));
			huodong.setHdinfoid(c.getString(c.getColumnIndex("hdinfoid")));
			huodong.setHdinfocommentid(c.getString(c.getColumnIndex("hdinfocommentid")));
			huodong.setContent(c.getString(c.getColumnIndex("content")));
			huodong.setImagepath(c.getString(c.getColumnIndex("imagepath")));
			huodong.setVoicepath(c.getString(c.getColumnIndex("voicepath")));
			huodong.setVideopath(c.getString(c.getColumnIndex("videopath")));
			huodong.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			huodongs.add(huodong);
		}
		c.close();
		return huodongs;
	}

	public List<BaomingData> queryS_baoming() {
		ArrayList<BaomingData> baomings = new ArrayList<BaomingData>();
		Cursor c = queryS_baomingByTheCursor();
		while (c.moveToNext()) {
			BaomingData baoming = new BaomingData();
			baoming.setConnected_uid(c.getString(c.getColumnIndex("connected_uid")));
			baoming.setHdactivityresultid(c.getString(c.getColumnIndex("hdactivityresultid")));
			baoming.setHdinfoid(c.getString(c.getColumnIndex("hdinfoid")));
			baoming.setRemark(c.getString(c.getColumnIndex("remark")));
			baoming.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			baomings.add(baoming);
		}
		c.close();
		return baomings;
	}

	public List<BaomingData> queryS_baomingByConnected_uid(String connected_uid) {
		ArrayList<BaomingData> baomings = new ArrayList<BaomingData>();
		Cursor c = queryS_baomingByConnected_uidTheCursor(connected_uid);
		while (c.moveToNext()) {
			BaomingData baoming = new BaomingData();
			baoming.setConnected_uid(c.getString(c.getColumnIndex("connected_uid")));
			baoming.setHdactivityresultid(c.getString(c.getColumnIndex("hdactivityresultid")));
			baoming.setHdinfoid(c.getString(c.getColumnIndex("hdinfoid")));
			baoming.setRemark(c.getString(c.getColumnIndex("remark")));
			baoming.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			baomings.add(baoming);
		}
		c.close();
		return baomings;
	}

	public List<TuangouData> queryS_tuangou() {
		ArrayList<TuangouData> tuangous = new ArrayList<TuangouData>();
		Cursor c = queryS_tuangouByTheCursor();
		while (c.moveToNext()) {
			TuangouData tuangou = new TuangouData();
			tuangou.setConnected_uid(c.getString(c.getColumnIndex("connected_uid")));
			tuangou.setHdgroupresultid(c.getString(c.getColumnIndex("hdgroupresultid")));
			tuangou.setHdinfoid(c.getString(c.getColumnIndex("hdinfoid")));
			tuangou.setRemark(c.getString(c.getColumnIndex("remark")));
			tuangou.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			tuangous.add(tuangou);
		}
		c.close();
		return tuangous;
	}

	public List<TuangouData> queryS_tuangouByConnected_uid(String connected_uid) {
		ArrayList<TuangouData> tuangous = new ArrayList<TuangouData>();
		Cursor c = queryS_tuangouByConnected_uidTheCursor(connected_uid);
		while (c.moveToNext()) {
			TuangouData tuangou = new TuangouData();
			tuangou.setConnected_uid(c.getString(c.getColumnIndex("connected_uid")));
			tuangou.setHdgroupresultid(c.getString(c.getColumnIndex("hdgroupresultid")));
			tuangou.setHdinfoid(c.getString(c.getColumnIndex("hdinfoid")));
			tuangou.setRemark(c.getString(c.getColumnIndex("remark")));
			tuangou.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			tuangous.add(tuangou);
		}
		c.close();
		return tuangous;
	}

	public List<VoteData> queryS_vote() {
		ArrayList<VoteData> votes = new ArrayList<VoteData>();
		Cursor c = queryS_voteByTheCursor();
		while (c.moveToNext()) {
			VoteData vote = new VoteData();
			vote.setConnected_uid(c.getString(c.getColumnIndex("connected_uid")));
			vote.setHdpersonid(c.getString(c.getColumnIndex("hdpersonid")));
			vote.setHdvoteid(c.getString(c.getColumnIndex("hdvoteid")));
			vote.setHdvotelogid(c.getString(c.getColumnIndex("hdvotelogid")));
			vote.setRemark(c.getString(c.getColumnIndex("remark")));
			vote.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			votes.add(vote);
		}
		c.close();
		return votes;
	}

	public List<VoteData> queryS_voteByConnected_uid(String connected_uid) {
		ArrayList<VoteData> votes = new ArrayList<VoteData>();
		Cursor c = queryS_voteByConnected_uidTheCursor(connected_uid);
		while (c.moveToNext()) {
			VoteData vote = new VoteData();
			vote.setConnected_uid(c.getString(c.getColumnIndex("connected_uid")));
			vote.setHdpersonid(c.getString(c.getColumnIndex("hdpersonid")));
			vote.setHdvoteid(c.getString(c.getColumnIndex("hdvoteid")));
			vote.setHdvotelogid(c.getString(c.getColumnIndex("hdvotelogid")));
			vote.setRemark(c.getString(c.getColumnIndex("remark")));
			vote.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			votes.add(vote);
		}
		c.close();
		return votes;
	}

	public List<RadioHudongData> queryS_radiohudong() {
		ArrayList<RadioHudongData> radiohudongs = new ArrayList<RadioHudongData>();
		Cursor c = queryS_radiohudongByTheCursor();
		while (c.moveToNext()) {
			RadioHudongData radiohudong = new RadioHudongData();
			radiohudong.setConnected_uid(c.getString(c.getColumnIndex("connected_uid")));
			radiohudong.setHdcommentid(c.getString(c.getColumnIndex("hdcommentid")));
			radiohudong.setContent(c.getString(c.getColumnIndex("content")));
			radiohudong.setImagepath(c.getString(c.getColumnIndex("imagepath")));
			radiohudong.setVoicepath(c.getString(c.getColumnIndex("voicepath")));
			radiohudong.setVideopath(c.getString(c.getColumnIndex("videopath")));
			radiohudong.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			radiohudongs.add(radiohudong);
		}
		c.close();
		return radiohudongs;
	}

	public List<RadioHudongData> queryS_radiohudongByConnected_uid(String connected_uid) {
		ArrayList<RadioHudongData> radiohudongs = new ArrayList<RadioHudongData>();
		Cursor c = queryS_radiohudongByConnected_uidTheCursor(connected_uid);
		while (c.moveToNext()) {
			RadioHudongData radiohudong = new RadioHudongData();
			radiohudong.setConnected_uid(c.getString(c.getColumnIndex("connected_uid")));
			radiohudong.setHdcommentid(c.getString(c.getColumnIndex("hdcommentid")));
			radiohudong.setContent(c.getString(c.getColumnIndex("content")));
			radiohudong.setImagepath(c.getString(c.getColumnIndex("imagepath")));
			radiohudong.setVoicepath(c.getString(c.getColumnIndex("voicepath")));
			radiohudong.setVideopath(c.getString(c.getColumnIndex("videopath")));
			radiohudong.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			radiohudongs.add(radiohudong);
		}
		c.close();
		return radiohudongs;
	}

	public List<BaoliaoAndBaoguangData> queryBaoliaoandBaoguangByType(String type) {
		ArrayList<BaoliaoAndBaoguangData> baoliaoandbaoguangs = new ArrayList<BaoliaoAndBaoguangData>();
		Cursor c = queryBaoliaoandbaoguangByTypeTheCursor(type);
		while (c.moveToNext()) {
			BaoliaoAndBaoguangData baoliaoandbaoguang = new BaoliaoAndBaoguangData();
			baoliaoandbaoguang.setConnected_uid(c.getString(c.getColumnIndex("connected_uid")));
			baoliaoandbaoguang.setNickname(c.getString(c.getColumnIndex("nickname")));
			baoliaoandbaoguang.setHeadimg(c.getString(c.getColumnIndex("headimg")));
			baoliaoandbaoguang.setIsdj(c.getString(c.getColumnIndex("isdj")));
			baoliaoandbaoguang.setIntro(c.getString(c.getColumnIndex("intro")));
			baoliaoandbaoguang.setContent(c.getString(c.getColumnIndex("content")));
			baoliaoandbaoguang.setImagepath(c.getString(c.getColumnIndex("imagepath")));
			baoliaoandbaoguang.setImagepath_small(c.getString(c.getColumnIndex("imagepath_small")));
			baoliaoandbaoguang.setVideopath(c.getString(c.getColumnIndex("videopath")));
			baoliaoandbaoguang.setCreatetime(c.getString(c.getColumnIndex("createtime")));
			baoliaoandbaoguang.setStcollectid(c.getString(c.getColumnIndex("stcollectid")));
			baoliaoandbaoguang.setReplycount(c.getString(c.getColumnIndex("replycount")));
			baoliaoandbaoguang.setLikecount(c.getString(c.getColumnIndex("likecount")));
			baoliaoandbaoguang.setIstransmit(c.getString(c.getColumnIndex("istransmit")));
			baoliaoandbaoguang.setTransmiturl(c.getString(c.getColumnIndex("transmiturl")));
			baoliaoandbaoguang.setIstoppath(c.getString(c.getColumnIndex("istoppath")));
			baoliaoandbaoguang.setIsdjpath(c.getString(c.getColumnIndex("isdjpath")));
			baoliaoandbaoguangs.add(baoliaoandbaoguang);
		}
		c.close();
		return baoliaoandbaoguangs;
	}

	public List<NewsSpaceData> queryS_newsByConnected_uid(String connected_uid) {
		ArrayList<NewsSpaceData> newss = new ArrayList<NewsSpaceData>();
		Cursor c = queryS_newsByConnected_uidTheCursor(connected_uid);
		while (c.moveToNext()) {
			NewsSpaceData news = new NewsSpaceData();
			news.setConnected_uid(c.getString(c.getColumnIndex("connected_uid")));
			news.setHdnewid(c.getString(c.getColumnIndex("hdnewid")));
			news.setHdnewcommentid(c.getString(c.getColumnIndex("hdnewcommentid")));
			news.setContent(c.getString(c.getColumnIndex("content")));
			news.setImagepath(c.getString(c.getColumnIndex("imagepath")));
			news.setVoicepath(c.getString(c.getColumnIndex("voicepath")));
			news.setVideopath(c.getString(c.getColumnIndex("videopath")));
			news.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			newss.add(news);
		}
		c.close();
		return newss;
	}

	public ArrayList<HomeInfoData> queryHomeinfo() {
		ArrayList<HomeInfoData> homeinfos = new ArrayList<HomeInfoData>();
		Cursor c = queryHomeinfoByTheCursor();
		while (c.moveToNext()) {
			HomeInfoData homeinfo = new HomeInfoData();
			homeinfo.setHdinfoid(c.getString(c.getColumnIndex("hdinfoid")));
			homeinfo.setTitle(c.getString(c.getColumnIndex("title")));
			homeinfo.setHdtype(c.getString(c.getColumnIndex("hdtype")));
			homeinfo.setNewimage(c.getString(c.getColumnIndex("newimage")));
			homeinfos.add(homeinfo);
		}
		c.close();
		return homeinfos;
	}

	public List<MsglistData> queryM_msglist() {
		ArrayList<MsglistData> msglists = new ArrayList<MsglistData>();
		Cursor c = querymsglistByTheCursor();
		while (c.moveToNext()) {
			MsglistData msglist = new MsglistData();
			msglist.setMsgid(c.getString(c.getColumnIndex("msgid")));
			msglist.setMsgtitle(c.getString(c.getColumnIndex("msgtitle")));
			msglist.setMsgtype(c.getString(c.getColumnIndex("msgtype")));
			msglist.setStarttime(c.getString(c.getColumnIndex("starttime")));
			msglists.add(msglist);
		}
		c.close();
		return msglists;
	}

	public List<AwardlistData> queryM_awardlist() {
		ArrayList<AwardlistData> awardlists = new ArrayList<AwardlistData>();
		Cursor c = queryawardlistByTheCursor();
		while (c.moveToNext()) {
			AwardlistData awardlist = new AwardlistData();
			awardlist.setHdawardid(c.getString(c.getColumnIndex("hdawardid")));
			awardlist.setMobile(c.getString(c.getColumnIndex("mobile")));
			awardlist.setCreatetime(c.getString(c.getColumnIndex("nickname")));
			awardlist.setAwardcontent(c.getString(c.getColumnIndex("awardcontent")));
			awardlist.setCreatetime(c.getString(c.getColumnIndex("createtime")));
			awardlists.add(awardlist);
		}
		c.close();
		return awardlists;
	}

	public List<ShareListHeaderData> queryShareListHeader() {
		ArrayList<ShareListHeaderData> headers = new ArrayList<ShareListHeaderData>();
		Cursor c = querysharelistheaderByTheCursor();
		while (c.moveToNext()) {
			ShareListHeaderData header = new ShareListHeaderData();
			header.setHdcommentid(c.getString(c.getColumnIndex("hdcommentid")));
			header.setHdmenuid(c.getString(c.getColumnIndex("hdmenuid")));
			header.setNickname(c.getString(c.getColumnIndex("nickname")));
			header.setHeadimg(c.getString(c.getColumnIndex("headimg")));
			header.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			header.setLikecount(c.getString(c.getColumnIndex("likecount")));
			header.setReplycount(c.getString(c.getColumnIndex("replycount")));
			header.setContent(c.getString(c.getColumnIndex("content")));
			header.setConnected_uid(c.getString(c.getColumnIndex("connected_uid")));
			header.setIsdjpath(c.getString(c.getColumnIndex("isdjpath")));
			header.setIstoppath(c.getString(c.getColumnIndex("istoppath")));
			headers.add(header);
		}
		c.close();
		return headers;
	}

	public List<MainHotListData> queryMainHotList() {
		ArrayList<MainHotListData> hots = new ArrayList<MainHotListData>();
		Cursor c = querymainhotlistByTheCursor();
		while (c.moveToNext()) {
			MainHotListData hot = new MainHotListData();
			hot.setFlag(c.getString(c.getColumnIndex("flag")));
			hot.setId(c.getString(c.getColumnIndex("id")));
			hot.setImagepath(c.getString(c.getColumnIndex("imagepath")));
			hot.setTitle(c.getString(c.getColumnIndex("title")));
			hot.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			hots.add(hot);
		}
		c.close();
		return hots;
	}

	public List<MainAdListData> queryMainAdList() {
		ArrayList<MainAdListData> ads = new ArrayList<MainAdListData>();
		Cursor c = querymainadlistByTheCursor();
		while (c.moveToNext()) {
			MainAdListData ad = new MainAdListData();
			ad.setHdinfoid(c.getString(c.getColumnIndex("hdinfoid")));
			ad.setTitle(c.getString(c.getColumnIndex("title")));
			ad.setPrice(c.getString(c.getColumnIndex("price")));
			ad.setImagepath(c.getString(c.getColumnIndex("imagepath")));
			ads.add(ad);
		}
		c.close();
		return ads;
	}

	public List<AudioListData> queryAudioList() {
		ArrayList<AudioListData> audios = new ArrayList<AudioListData>();
		Cursor c = queryaudiolistByTheCursor();
		while (c.moveToNext()) {
			AudioListData audio = new AudioListData();
			audio.setHdaudiotypeid(c.getString(c.getColumnIndex("hdaudiotypeid")));
			audio.setTypename(c.getString(c.getColumnIndex("typename")));
			audio.setTypedesc(c.getString(c.getColumnIndex("typedesc")));
			audio.setIsusered(c.getString(c.getColumnIndex("isusered")));
			audio.setImagepath(c.getString(c.getColumnIndex("imagepath")));
			audios.add(audio);
		}
		c.close();
		return audios;
	}

	public List<AudioProgrammeData> queryAudioProgrammeByHdaudiotypeid(String hdaudiotypeid) {
		ArrayList<AudioProgrammeData> audioprogrammes = new ArrayList<AudioProgrammeData>();
		Cursor c = queryAudioProgrammeByHdaudiotypeidTheCursor(hdaudiotypeid);
		while (c.moveToNext()) {
			AudioProgrammeData audioprogramme = new AudioProgrammeData();
			audioprogramme.setHdaudioid(c.getString(c.getColumnIndex("hdaudioid")));
			audioprogramme.setHdaudiotypeid(c.getString(c.getColumnIndex("hdaudiotypeid")));
			audioprogramme.setTitle(c.getString(c.getColumnIndex("title")));
			audioprogramme.setVoicepath(c.getString(c.getColumnIndex("voicepath")));
			audioprogramme.setImagepath(c.getString(c.getColumnIndex("imagepath")));
			audioprogramme.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			audioprogramme.setPlaycount(c.getString(c.getColumnIndex("playcount")));
			audioprogrammes.add(audioprogramme);
		}
		c.close();
		return audioprogrammes;
	}

	public List<AudioProgrammeData> queryAudioDownLoadByHdaudiotypeid(String hdaudiotypeid) {
		ArrayList<AudioProgrammeData> audios = new ArrayList<AudioProgrammeData>();
		Cursor c = queryAudioDownLoadByHdaudiotypeidTheCursor(hdaudiotypeid);
		while (c.moveToNext()) {
			AudioProgrammeData audio = new AudioProgrammeData();
			audio.setHdaudioid(c.getString(c.getColumnIndex("hdaudioid")));
			audio.setHdaudiotypeid(c.getString(c.getColumnIndex("hdaudiotypeid")));
			audio.setTitle(c.getString(c.getColumnIndex("title")));
			audio.setVoicepath(c.getString(c.getColumnIndex("voicepath")));
			audio.setImagepath(c.getString(c.getColumnIndex("imagepath")));
			audio.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			audio.setPlaycount(c.getString(c.getColumnIndex("playcount")));
			audio.setFilepath(c.getString(c.getColumnIndex("filepath")));
			audios.add(audio);
		}
		c.close();
		return audios;
	}

	public List<AudioProgrammeData> queryAudioDownLoadByHdaudioTypeIdAndHdaudioId(String hdaudiotypeid,
			String hdaudioid) {
		ArrayList<AudioProgrammeData> audios = new ArrayList<AudioProgrammeData>();
		Cursor c = queryAudioDownLoadByHdaudioIdTheCursor(hdaudiotypeid, hdaudioid);
		while (c.moveToNext()) {
			AudioProgrammeData audio = new AudioProgrammeData();
			audio.setHdaudioid(c.getString(c.getColumnIndex("hdaudioid")));
			audio.setHdaudiotypeid(c.getString(c.getColumnIndex("hdaudiotypeid")));
			audio.setTitle(c.getString(c.getColumnIndex("title")));
			audio.setVoicepath(c.getString(c.getColumnIndex("voicepath")));
			audio.setImagepath(c.getString(c.getColumnIndex("imagepath")));
			audio.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			audio.setPlaycount(c.getString(c.getColumnIndex("playcount")));
			audio.setFilepath(c.getString(c.getColumnIndex("filepath")));
			audios.add(audio);
		}
		c.close();
		return audios;
	}

	public CarInfoData queryCarInfoByCarNum(String carNum) {
		Cursor c = queryCarInfoByCarNumTheCursor(carNum);
		CarInfoData carinfo = new CarInfoData();
		carinfo.setHdpersoncarid(c.getString(c.getColumnIndex("hdpersoncarid")));
		carinfo.setConnected_uid(c.getString(c.getColumnIndex("connected_uid")));
		carinfo.setCarnum(c.getString(c.getColumnIndex("carnum")));
		carinfo.setCartypecode(c.getString(c.getColumnIndex("cartypecode")));
		carinfo.setLastfour(c.getString(c.getColumnIndex("lastfour")));
		carinfo.setCreatedate(c.getString(c.getColumnIndex("createdate")));
		c.close();
		return carinfo;
	}

	public List<CarInfoData> queryCarInfoAll() {
		ArrayList<CarInfoData> carinfos = new ArrayList<CarInfoData>();
		Cursor c = queryCarInfoAllTheCursor();
		while (c.moveToNext()) {
			CarInfoData carinfo = new CarInfoData();
			carinfo.setHdpersoncarid(c.getString(c.getColumnIndex("hdpersoncarid")));
			carinfo.setConnected_uid(c.getString(c.getColumnIndex("connected_uid")));
			carinfo.setCarnum(c.getString(c.getColumnIndex("carnum")));
			carinfo.setCartypecode(c.getString(c.getColumnIndex("cartypecode")));
			carinfo.setLastfour(c.getString(c.getColumnIndex("lastfour")));
			carinfo.setCreatedate(c.getString(c.getColumnIndex("createdate")));
			carinfos.add(carinfo);
		}
		c.close();
		return carinfos;
	}

	public List<String> queryCarNumAllByCarInfos() {
		List<String> list = new ArrayList<String>();
		Cursor c = db.rawQuery("SELECT carnum FROM carinfo", new String[] {});
		while (c.moveToNext()) {
			list.add(c.getString(c.getColumnIndex("carnum")).replace(",", ""));
		}
		c.close();
		return list;
	}

	/**
	 * query all persons, return cursor
	 * 
	 * @return Cursor
	 */
	public Cursor queryTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM person", null);
		return c;
	}

	public Cursor queryTextByTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM text", null);
		return c;
	}

	public Cursor queryHuodongByTheCursor(String contenttype) {
		Cursor c = db.rawQuery("SELECT * FROM huodong where contenttype=?", new String[] { contenttype });
		return c;
	}

	public Cursor queryHuodongitemByTheCursor(String hdinfoid) {
		Cursor c = db.rawQuery("SELECT * FROM huodongitem where hdinfoid=?", new String[] { hdinfoid });
		return c;
	}

	public Cursor queryNewsByTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM news", null);
		return c;
	}

	public Cursor queryNewsitemByTheCursor(String hdnewid) {
		Cursor c = db.rawQuery("SELECT * FROM newsitem where hdnewid=?", new String[] { hdnewid });
		return c;
	}

	public Cursor queryReplaysByTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM replays", null);
		return c;
	}

	public Cursor queryHudongchatByTheCursor(String hdmenuid, String islive) {
		Cursor c = db.rawQuery("SELECT * FROM hudongchat where hdmenuid = ? and islive=?",
				new String[] { hdmenuid, islive });
		return c;
	}

	public Cursor queryHudongchatByTheCursor(String islive) {
		Cursor c = db.rawQuery("SELECT * FROM hudongchat where islive=?", new String[] { islive });
		return c;
	}

	public Cursor queryReplayitemByTheCursor(String hdreplaytypeid) {
		Cursor c = db.rawQuery("SELECT * FROM replayitem where hdreplaytypeid = ?", new String[] { hdreplaytypeid });
		return c;
	}

	public Cursor queryOauthusersByTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM oauthusers", null);
		return c;
	}

	public Cursor queryS_carlifeByTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM s_carlife", null);
		return c;
	}

	public Cursor queryS_carlifeByConnected_uidTheCursor(String connected_uid) {
		Cursor c = db.rawQuery("SELECT * FROM s_carlife where connected_uid = ?", new String[] { connected_uid });
		return c;
	}

	public Cursor queryS_huodongByTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM s_huodong", null);
		return c;
	}

	public Cursor queryS_huodongByConnected_uidTheCursor(String connected_uid) {
		Cursor c = db.rawQuery("SELECT * FROM s_huodong where connected_uid = ?", new String[] { connected_uid });
		return c;
	}

	public Cursor queryS_baomingByTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM s_baoming", null);
		return c;
	}

	public Cursor queryS_baomingByConnected_uidTheCursor(String connected_uid) {
		Cursor c = db.rawQuery("SELECT * FROM s_baoming where connected_uid = ?", new String[] { connected_uid });
		return c;
	}

	public Cursor queryS_tuangouByTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM s_tuangou", null);
		return c;
	}

	public Cursor queryS_tuangouByConnected_uidTheCursor(String connected_uid) {
		Cursor c = db.rawQuery("SELECT * FROM s_tuangou where connected_uid = ?", new String[] { connected_uid });
		return c;
	}

	public Cursor queryS_voteByTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM s_vote", null);
		return c;
	}

	public Cursor queryS_voteByConnected_uidTheCursor(String connected_uid) {
		Cursor c = db.rawQuery("SELECT * FROM s_vote where connected_uid = ?", new String[] { connected_uid });
		return c;
	}

	public Cursor queryS_radiohudongByTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM s_radiohudong", null);
		return c;
	}

	public Cursor queryS_radiohudongByConnected_uidTheCursor(String connected_uid) {
		Cursor c = db.rawQuery("SELECT * FROM s_radiohudong where connected_uid = ?", new String[] { connected_uid });
		return c;
	}

	public Cursor queryBaoliaoandbaoguangByTypeTheCursor(String type) {
		Cursor c = db.rawQuery("SELECT * FROM baoliaoandbaoguang where type = ?", new String[] { type });
		return c;
	}

	public Cursor queryS_newsByConnected_uidTheCursor(String connected_uid) {
		Cursor c = db.rawQuery("SELECT * FROM s_news where connected_uid = ?", new String[] { connected_uid });
		return c;
	}

	public Cursor queryHomeinfoByTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM homeinfo", null);
		return c;
	}

	public Cursor queryawardlistByTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM m_awardlist", null);
		return c;
	}

	public Cursor querymsglistByTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM m_msglist", null);
		return c;
	}

	public Cursor querysharelistheaderByTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM sharelistheader", null);
		return c;
	}

	public Cursor querymainhotlistByTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM mainhotlist", null);
		return c;
	}

	public Cursor querymainadlistByTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM mainadlist", null);
		return c;
	}

	public Cursor queryaudiolistByTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM audiolist", null);
		return c;
	}

	public Cursor queryAudioProgrammeByHdaudiotypeidTheCursor(String hdaudiotypeid) {
		Cursor c = db.rawQuery("SELECT * FROM audioprogramme where hdaudiotypeid = ?", new String[] { hdaudiotypeid });
		return c;
	}

	public Cursor queryAudioDownLoadByHdaudiotypeidTheCursor(String hdaudiotypeid) {
		Cursor c = db.rawQuery("SELECT * FROM audiodownload where hdaudiotypeid = ?", new String[] { hdaudiotypeid });
		return c;
	}

	public Cursor queryAudioDownLoadByHdaudioIdTheCursor(String hdaudiotypeid, String hdaudioid) {
		Cursor c = db.rawQuery("SELECT * FROM audiodownload where hdaudiotypeid = ? and hdaudioid=?",
				new String[] { hdaudiotypeid, hdaudioid });
		return c;
	}

	public Cursor queryCarInfoByCarNumTheCursor(String carNum) {
		Cursor c = db.rawQuery("SELECT * FROM carinfo where carnum = ?", new String[] { carNum });
		return c;
	}

	public Cursor queryCarInfoAllTheCursor() {
		Cursor c = db.rawQuery("SELECT * FROM carinfo", new String[] {});
		return c;
	}

	/**
	 * close database
	 */
	public void closeDB() {
		db.close();
	}
}
