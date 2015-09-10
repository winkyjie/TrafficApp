package cn.fszt.trafficapp.widget.oauth.sina;

import cn.fszt.trafficapp.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

//public class WeiboBaseActivity extends Activity implements IWeiboHandler.Response{
//
//	/** 微博微博分享接口实例 */
//    private IWeiboShareAPI  mWeiboShareAPI = null;
//    public String content;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//    	super.onCreate(savedInstanceState);
//    	// 创建微博分享接口实例
//        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.APP_KEY);
//        
//        // 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
//        // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
//        // NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
//        
//		// 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
//        // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
//        // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
//        // 失败返回 false，不调用上述回调
//        if (savedInstanceState != null) {
//            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
//        }
//    }
//    
//    /**
//     * @see {@link Activity#onNewIntent}
//     */	
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        
//        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
//        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
//        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
//        mWeiboShareAPI.handleWeiboResponse(intent, this);
//    }
//    
//	/**
//     * 接收微客户端博请求的数据。
//     * 当微博客户端唤起当前应用并进行分享时，该方法被调用。
//     * 
//     * @param baseRequest 微博请求数据对象
//     * @see {@link IWeiboShareAPI#handleWeiboRequest}
//     */
//    @Override
//    public void onResponse(BaseResponse baseResp) {
//        switch (baseResp.errCode) {
//        case WBConstants.ErrorCode.ERR_OK:
////            Toast.makeText(this, "成功", Toast.LENGTH_LONG).show();
//            break;
//        case WBConstants.ErrorCode.ERR_CANCEL:
////            Toast.makeText(this, "取消1", Toast.LENGTH_LONG).show();
//            break;
//        case WBConstants.ErrorCode.ERR_FAIL:
////            Toast.makeText(this, 
////                    "失败" + "Error Message: " + baseResp.errMsg, 
////                    Toast.LENGTH_LONG).show();
//            break;
//        }
//    }
//    
//    public void send(String content){
//    	this.content = content;
//    	mWeiboShareAPI.registerApp();
//    	try {
//            // 检查微博客户端环境是否正常，如果未安装微博，弹出对话框询问用户下载微博客户端
//            if (mWeiboShareAPI.checkEnvironment(true)) {                    
//                sendMessage(true,true,false,false,false,false);
//            }
//        } catch (WeiboShareException e) {
//            e.printStackTrace();
////            Toast.makeText(WBShareActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }
//    /**
//     * 第三方应用发送请求消息到微博，唤起微博分享界面。
//     * @see {@link #sendMultiMessage} 或者 {@link #sendSingleMessage}
//     */
//    private void sendMessage(boolean hasText, boolean hasImage, 
//			boolean hasWebpage, boolean hasMusic, boolean hasVideo, boolean hasVoice) {
//        
//        if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
//            int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
//            if (supportApi >= 10351 /*ApiUtils.BUILD_INT_VER_2_2*/) {
//                sendMultiMessage(hasText, hasImage, hasWebpage, hasMusic, hasVideo, hasVoice);
//            } else {
//                sendSingleMessage(hasText, hasImage, hasWebpage, hasMusic, hasVideo/*, hasVoice*/);
//            }
//        } else {
////            Toast.makeText(this, R.string.weibosdk_demo_not_support_api_hint, Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    /**
//     * 第三方应用发送请求消息到微博，唤起微博分享界面。
//     * 注意：当 {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
//     * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
//     * 
//     * @param hasText    分享的内容是否有文本
//     * @param hasImage   分享的内容是否有图片
//     * @param hasWebpage 分享的内容是否有网页
//     * @param hasMusic   分享的内容是否有音乐
//     * @param hasVideo   分享的内容是否有视频
//     * @param hasVoice   分享的内容是否有声音
//     */
//    private void sendMultiMessage(boolean hasText, boolean hasImage, boolean hasWebpage,
//            boolean hasMusic, boolean hasVideo, boolean hasVoice) {
//        
//        // 1. 初始化微博的分享消息
//        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
//        if (hasText) {
//            weiboMessage.textObject = getTextObj();
//        }
//        
//        if (hasImage) {
//            weiboMessage.imageObject = getImageObj();
//        }
//        
//        // 2. 初始化从第三方到微博的消息请求
//        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
//        // 用transaction唯一标识一个请求
//        request.transaction = String.valueOf(System.currentTimeMillis());
//        request.multiMessage = weiboMessage;
//        
//        // 3. 发送请求消息到微博，唤起微博分享界面
//        mWeiboShareAPI.sendRequest(request);
//    }
//
//    /**
//     * 第三方应用发送请求消息到微博，唤起微博分享界面。
//     * 当{@link IWeiboShareAPI#getWeiboAppSupportAPI()} < 10351 时，只支持分享单条消息，即
//     * 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
//     * 
//     * @param hasText    分享的内容是否有文本
//     * @param hasImage   分享的内容是否有图片
//     * @param hasWebpage 分享的内容是否有网页
//     * @param hasMusic   分享的内容是否有音乐
//     * @param hasVideo   分享的内容是否有视频
//     */
//    private void sendSingleMessage(boolean hasText, boolean hasImage, boolean hasWebpage,
//            boolean hasMusic, boolean hasVideo/*, boolean hasVoice*/) {
//        
//        // 1. 初始化微博的分享消息
//        WeiboMessage weiboMessage = new WeiboMessage();
//        if (hasText) {
//            weiboMessage.mediaObject = getTextObj();
//        }
//        if (hasImage) {
//            weiboMessage.mediaObject = getImageObj();
//        }
//        
//        // 2. 初始化从第三方到微博的消息请求
//        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
//        // 用transaction唯一标识一个请求
//        request.transaction = String.valueOf(System.currentTimeMillis());
//        request.message = weiboMessage;
//        
//        // 3. 发送请求消息到微博，唤起微博分享界面
//        mWeiboShareAPI.sendRequest(request);
//    }
//
//    /**
//     * 创建文本消息对象。
//     * 
//     * @return 文本消息对象。
//     */
//    private TextObject getTextObj() {
//        TextObject textObject = new TextObject();
//        textObject.text = content;
//        return textObject;
//    }
//
//    /**
//     * 创建图片消息对象。
//     * 
//     * @return 图片消息对象。
//     */
//    private ImageObject getImageObj() {
//        ImageObject imageObject = new ImageObject();
//        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.qrcode);;
//        
//        imageObject.setImageObject(bitmapDrawable.getBitmap());
//        return imageObject;
//    }
//
//}
