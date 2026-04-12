package com.shenma.tvlauncher.vod;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shenma.tvlauncher.Api;
import com.shenma.tvlauncher.EmpowerActivity;
import com.shenma.tvlauncher.utils.Constant;
import com.shenma.tvlauncher.utils.GetTimeStamp;
import com.shenma.tvlauncher.utils.Md5Encoder;
import com.shenma.tvlauncher.utils.Rc4;
import com.shenma.tvlauncher.utils.Rsa;
import com.shenma.tvlauncher.utils.AES;
import com.shenma.tvlauncher.utils.SharePreferenceDataUtil;
import com.shenma.tvlauncher.view.AlwaysMarqueeTextView;
import com.shenma.tvlauncher.vod.domain.VodUrl;
import com.shenma.tvlauncher.vod.domain.VodUrlList;
import com.umeng.analytics.MobclickAgent;
import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.utils.Logger;
import com.shenma.tvlauncher.utils.Utils;
import com.shenma.tvlauncher.utils.VideoPlayUtils;
import com.shenma.tvlauncher.view.HomeDialog;
import com.shenma.tvlauncher.view.PlayerProgressBar;
import com.shenma.tvlauncher.vod.adapter.VodMenuAdapter;
import com.shenma.tvlauncher.vod.dao.VodDao;
import com.shenma.tvlauncher.vod.db.Album;
import com.shenma.tvlauncher.vod.domain.VideoInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import tv.danmaku.ijk.media.example.widget.media.IRenderView;
import tv.danmaku.ijk.media.example.widget.media.IjkVideoView;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnBufferingUpdateListener;
import tv.danmaku.ijk.media.player.IjkMediaMeta;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static com.shenma.tvlauncher.vod.VodDetailsActivity.now_source;

/**
 * @author joychang
 * @Description 开始播放
 */
public class VideoPlayerActivity extends Activity implements OnClickListener {
    private static final String TAG = "VideoPlayerActivity";
    private static final int TIME = 6000;/*显示时间*/
    private static final int TOUCH_BRIGHTNESS = 2;/*触摸亮度*/
    private static final int TOUCH_NONE = 0;/*停止触摸*/
    private static final int TOUCH_SEEK = 3;//进度
    private static final int TOUCH_VOLUME = 1;/*触摸音量*/
    public static int hmblposition = 0;/*画面*/
    public static int jmposition = 0;/*解码*/
    public static int phszposition = 0;/*偏好*/
    public static int qxdposition = 0;/*清晰度*/
    public static int xjposition = 0;/*选集*/
    public static int bsposition = 2;/*倍速*/
    public static int ptposition = 0;/*跳片头*/
    public static int pwposition = 0;/*跳片尾*/
    public static int nhposition = 2;/*播放器内核 0=自动 1=系统 2=IJK 3=EXO 4=阿里*/
    private static int controlHeight = 0;
    private static int menutype;/*菜单列表*/
    private static String rxByte;
    private final Object SYNC_Playing = new Object();
    private float Lightness;
    private String albumPic = null;
    private int collectionTime = 0;
    private View controlView;
    private PopupWindow controler;
    private int currentVolume;
    private VodDao dao;
    private String domain;
    private long firstTime = 0;
    private IjkVideoView iVV = null;
    private ImageButton ib_playStatus;
    private boolean isBack = false;/*是否返回*/
    private boolean isControllerShow = false;
    private Boolean isDestroy = Boolean.valueOf(false);
    private Boolean isLast = Boolean.valueOf(false);
    private boolean isMenuItemShow = false;
    private boolean isMenuShow = false;
    private Boolean isNext = Boolean.valueOf(false);
    private Boolean isPause = Boolean.valueOf(false);
    private boolean isSwitch = true;
    private String jump_time;
    private String jump_time_end;
    private long firstTimes = 0;//屏蔽重复请求
    private long firstTimen = 0;//屏蔽重复储存进度
    private long lastRxByte;
    private long lastSpeedTime;
    private AudioManager mAudioManager = null;
    private GestureDetector mGestureDetector = null;/*手势*/
    private HandlerThread mHandlerThread;
    private boolean mIsHwDecode = true;/*设置解码模式*/
    private int mLastPos = 0;/*最后播放进度*/
    private int mLastPos2 = 0;/*最后播放进度2*/
    private int vipstate;/*会员状态*/
    private String time;/*取会员Vip时间*/
    private int Trytime;/*可试看的时间*/
    private PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
    private PlayerProgressBar mProgressBar;
    private Handler mSpeedHandler;
    private int mSurfaceYDisplayRange;
    private Toast mToast = null;
    private int mTouchAction;
    private float mTouchX;
    private float mTouchY;
    private String mVideoSource = null;
    private WakeLock mWakeLock = null;
    private int maxVolume;
    private ListView menulist;
    private PopupWindow menupopupWindow;
    private String nextlink;
    private int playIndex = 0;
    private int playPreCode;
    private int screenHeight;
    private int screenWidth;
    private SeekBar seekBar;
    private String sourceId;
    private EventHandler mEventHandler;
    private SharedPreferences sp;
    private long speed;
    private Runnable speedRunnable;
    private View time_controlView;
    private PopupWindow time_controler;
    private TextView tv_currentTime;/*当前播放进度*/
    private TextView tv_menu;
    private TextView tv_mv_name;
    private TextView tv_mv_speed;
    private TextView tv_progress_time;
    private TextView tv_time;
    private TextView tv_totalTime;/*视频总时长*/
    private AlwaysMarqueeTextView tv_notice;/*视频跑马公告内容*/
    private LinearLayout tv_notice_root;/*视频跑马框架*/
    private String url;/*选集中的一集*/
    private String videoId;/*影视Id*/
    private int videoLength;/*视频总时长*/
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            // 调用窗口消息处理函数
            onMessage(msg);
        }
    };
    private VodMenuAdapter vmAdapter;
    private String vodname;/*影视名称*/
    private String vodstate;
    private String vodtype = null;
    private final Handler mediaHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WindowMessageID.NET_FAILED:/*未使用*/
                    Logger.d(TAG, "mPlayerStatus=" + mPlayerStatus);
                    if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
                        iVV.stopPlayback();
                    }
                    finish();
                    return;
                case WindowMessageID.DATA_PREPARE_OK:
                    onCreateMenu();
                    return;
                case WindowMessageID.DATA_BASE64_PREPARE_OK:/*未使用*/
//                    VodUrl vodurl = (VodUrl) msg.obj;
//                    mVideoSource = vodurl.getUrl();
                    VodUrlList vodurl = (VodUrlList) msg.obj;
                    mVideoSource = vodurl.getUrl();
                    mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
                    if(mEventHandler.hasMessages(WindowMessageID.EVENT_PLAY)){
                        mEventHandler.removeMessages(WindowMessageID.EVENT_PLAY);
                    }
                    mEventHandler.sendEmptyMessage(WindowMessageID.EVENT_PLAY);
                    return;
                case WindowMessageID.PREPARE_VOD_DATA:
                    PrepareVodData(playIndex);
                    return;
                case WindowMessageID.SHOW_TV:
                    if (mSpeedHandler != null) {
                        mSpeedHandler.removeCallbacks(speedRunnabless);////移除卡顿校验
                    }
                    //mSpeedHandler.removeCallbacks(speedRunnabless);////移除卡顿校验
                    mProgressBar.setVisibility(View.VISIBLE);
                    startSpeed();
                    ptpositions = false;
                    return;
                case WindowMessageID.COLSE_SHOW_TV:
                    mProgressBar.setVisibility(View.GONE);
                    endSpeed();
                    return;
                case WindowMessageID.PROGRESSBAR_PROGRESS_RESET:
                    mProgressBar.setProgress(0);
                    return;
                case WindowMessageID.SELECT_SCALES:
                    selectScales(hmblposition);
                    return;
                case WindowMessageID.RESET_MOVIE_TIME:
                    ResetMovieTime();
                    return;
                case WindowMessageID.Try:
                    /*到期试看完毕付款界面*/
                    startActivity(new Intent(VideoPlayerActivity.this, EmpowerActivity.class));
                    finish();
                    Utils.showToast(VideoPlayerActivity.this, getString(R.string.Try_to_see_expired) + Trytime + getString(R.string.end), R.drawable.toast_err);
                    return;
                case WindowMessageID.NOTICE:
                    tv_notice.setSelected(true);
                    tv_notice.setMarqueeRepeatLimit(-1);
                    tv_notice.startScroll();
                    int Vod_Notice_end_time = SharePreferenceDataUtil.getSharedIntData(VideoPlayerActivity.this, "Vod_Notice_end_time", 0);
                    mediaHandler.sendEmptyMessageDelayed(WindowMessageID.NOTICE_GONE, Vod_Notice_end_time * 1000);
                    return;
                case WindowMessageID.NOTICE_GONE:
                    tv_notice_root.setVisibility(View.GONE);
                    return;
                case WindowMessageID.START_NOTICE_GONE:
                    getVodGongGao();
                    return;
                case WindowMessageID.START_LOGO:
                    if (!logo_url.equals("null") && !logo_url.equals("")){
                        vodlogoloadImg();
                        break;
                    }
                    tv_logo.setImageDrawable(getResources().getDrawable(R.drawable.sm_logo));
                    tv_logo.setVisibility(View.VISIBLE);
                    return;
                case WindowMessageID.Sniffing:
                    webView.stopLoading();
                    webView.setWebViewClient(null);
                    jxurls = 0;
                    if (Failed.equals("")){
                        iVV.setVideoPath(Utils.UrlEncodeChinese(jxurl), webHeaders);
                    }else{
                        iVV.setVideoPath(Utils.UrlEncodeChinese(Failed), webHeaders);
                    }
                    Fburl = Utils.UrlEncodeChinese(jxurl);
                    if (!jxurl.equals("")){
                        mEventHandler.sendEmptyMessage(WindowMessageID.SUCCESS);
                    }
//                    mEventHandler.sendEmptyMessage(WindowMessageID.SUCCESS);
                    return;
                default:
                    return;
            }
        }
    };
    private RequestQueue mQueue;
    private String Fburl;/*反馈地址*/
    private ImageView tv_logo;/*视频logo*/
    private List<VideoInfo> videoInfo = null;
    private int ClientID = 1;//当前解析客户端
    private int MaxClientID = 1;//最大解析客户端
    private int Type = 0;//播放类型 0=JSON 1=嗅探
    /*远程logo地址*/
    private String logo_url = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(VideoPlayerActivity.this, "Logo_url", null),Constant.d);
    /*解析客户端*/
    private String Client = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, "Client", ""),Constant.d);
    /*自动换源*/
    private int Auto_Source = SharePreferenceDataUtil.getSharedIntData(this, "Auto_Source", 0);
    private String jxurl;//嗅探出来的地址
    private int jxurls = 0;//嗅探清零
    private Map<String, String> headers;//解析给的请求头
    private HashMap<String, String> webHeaders;//嗅探到的请求头
    private String Conditions;//嗅探关键字
    private String userAgent;//播放器头
    private String Referer;//播放器来源
    /*嗅探调试模式*/
    private int Sniff_debug_mode = SharePreferenceDataUtil.getSharedIntData(this, "Sniff_debug_mode", 0);
    /*播放超时调试模式*/
    private int Play_timeout_debug = SharePreferenceDataUtil.getSharedIntData(this, "Play_timeout_debug", 0);
    /*导航模式*/
//    private int Navigation_mode = SharePreferenceDataUtil.getSharedIntData(this, "Navigation_mode", 0);
    private int Navigation_mode = 0;
    /*跑马公告起始时间*/
    int Vod_Notice_starting_time = SharePreferenceDataUtil.getSharedIntData(this, "Vod_Notice_starting_time", 0);
    private int Number;/*播放器总数*/
    private List<VodUrlList> list;
    private int PlayersNumber;/*当前播放器*/
    private int Numbermax = 0;/*最大播放源*/
    private int load = 0;
    private int Maxtimeout = 300;
    private int timeout = Maxtimeout;
    private WebView webView;
    private String Exclude_content;//排除嗅探相似关键字
    private List<VodUrl> source = now_source;
    private boolean isDialogShowing = false; // 上报对话框添加一个标志变量用于跟踪对话框的显示状态
    private boolean isCodeBlockExecuted = false; // 播放器监听错误添加一个标志变量用于跟踪代码块是否已经执行过
    public static String Failed = "";//防盗版验证
    private boolean ptpositions = false; // 跳过片头添加一个标志变量用于跟踪对跳过片头的状态

    private int seizing;//是否已卡住
    private int seizings;//是否已卡住
    private long currentPositions;
    public static long currentPosition;
    private Runnable speedRunnabless;//卡顿校验
    private int core = 99;//获取核心 99=用户默认 0=自动 1=系统 2=IJK 3=EXO 4= 阿里
    private int safe_mode = 0;//指定内核安全模式 0=安全模式(低于5.1系统强制使用IJK播放器) 1=强制使用指定内核

    private int Ad_block = 0;//遮挡开关 0=关闭 1=开启
    private int position = 0;//遮挡位置 0=上部跑马 1=下部跑马 2=上部+下部 3=上下+右侧二维码 4=上+右 5=下+右 6=右


    /*播放器指定内核授权是否开启
    * 0=关闭(默认)
    * 1=开启*/
    private int core_mode = SharePreferenceDataUtil.getSharedIntData(this, "core_mode", 0);

    /*广告遮挡
     * 0=关闭(默认)
     * 1=开启*/
    private int Adblock = SharePreferenceDataUtil.getSharedIntData(this, "Adblock", 0);

    private LinearLayout Ad_block_up;
    private LinearLayout Ad_block_down;
    private LinearLayout Ad_block_right;

    // private int Ewmsize = 250;
    private int EwmWidth = 0;
    private int EwmHeight = 0;
    private int Moviesize = 120;
    private int Tvplaysize = 70;
    private int headposition = 0;







    /*卡顿换源校验
     *  0 = 不校验(默认)
     *  1 = 校验
     */
//    int vod_caton_check = 0;////卡顿校验
    int vod_caton_check = SharePreferenceDataUtil.getSharedIntData(VideoPlayerActivity.this, "vod_caton_check", 0);////卡顿校验

    /*卡顿换源校验时间(默认8秒)
     */
//    private int seizing_time = 8;//卡住校验间隔时间
    private int seizing_time = SharePreferenceDataUtil.getSharedIntData(VideoPlayerActivity.this, "seizing_time", 8);//卡住校验间隔时间
    public static SharedPreferences Sd;
    // private BroadcastReceiver pauseVideoReceiver;


    /*创建时的回调函数*/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mv_videoplayer);
        dao = new VodDao(this);
        Utils.stopAutoBrightness(this);
        if (Navigation_mode == 1){
            Navigation();
        }
        Sd = getSharedPreferences("TempData", MODE_PRIVATE);
        SharePreferenceDataUtil.setSharedIntData(VideoPlayerActivity.this, "LIVE",0);
        initView();
        initData();
        // //收听广播
        // pauseVideoReceiver = new BroadcastReceiver() {
        //     @Override
        //     public void onReceive(Context context, Intent intent) {
        //         if (intent.getAction().equals("com.example.MY_ACTION_PAUSE_VIDEO")) {
        //             onPause();
        //         }
        //         if (intent.getAction().equals("com.example.MY_ACTION_RESUME_VIDEO")) {
        //             onResume();
        //         }
        //     }
        // };
        // // 注册广播
        // LocalBroadcastManager.getInstance(this).registerReceiver(pauseVideoReceiver,
        //         new IntentFilter("com.example.MY_ACTION_PAUSE_VIDEO"));
        // LocalBroadcastManager.getInstance(this).registerReceiver(pauseVideoReceiver,
        //         new IntentFilter("com.example.MY_ACTION_RESUME_VIDEO"));
    }

    /*销毁时*/
    protected void onDestroy() {
        super.onDestroy();
        isDestroy = Boolean.valueOf(true);
        stopPlayback();
        if (mSpeedHandler != null) {
            mSpeedHandler.removeCallbacks(speedRunnabless);////移除卡顿校验
        }
        //mSpeedHandler.removeCallbacks(speedRunnabless);////移除卡顿校验
        currentPosition = 0;//初始播放进度时间
        Utils.startAutoBrightness(this);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        Logger.d(TAG, "onDestroy");
        // //销毁广播
        // if (pauseVideoReceiver != null){
        //     LocalBroadcastManager.getInstance(this).unregisterReceiver(pauseVideoReceiver);
        // }
    }

    /*停止时*/
    protected void onStop() {
        super.onStop();
        isDestroy = Boolean.valueOf(true);
        mHandler.removeMessages(WindowMessageID.SWITCH_CODE);
        mHandler.removeMessages(WindowMessageID.HIDE_CONTROLER);
        mHandler.removeMessages(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
        xjposition = 0;
        mHandlerThread.quit();
        Logger.d(TAG, "onStop");
    }

    /*恢复时*/
    protected void onResume() {
        hideMenu();//隐藏侧边菜单栏
        xjposition = playIndex;//恢复剧集选择
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
        Map m_value = new HashMap();
        m_value.put("vodname", vodname);
        m_value.put("domain", domain);
        m_value.put("vodtype", vodtype);
        MobclickAgent.onEvent(this, "VOD_PLAY", m_value);
        isDestroy = Boolean.valueOf(false);
        Logger.d(TAG, "onResume...mPlayerStatus=" + mPlayerStatus);
        acquireWakeLock();

        /*开启后台事件处理线程*/
        if(!mHandlerThread.isAlive()){
            mHandlerThread = new HandlerThread("event handler thread", Process.THREAD_PRIORITY_BACKGROUND);
            mHandlerThread.start();
            mEventHandler = new EventHandler(mHandlerThread.getLooper());
        }

        /*发起一次播放任务,当然您不一定要在这发起*/
        if(null!=mVideoSource && !"".equals(mVideoSource) && mPlayerStatus==PLAYER_STATUS.PLAYER_IDLE){
            mEventHandler.sendEmptyMessage(WindowMessageID.EVENT_PLAY);
            Logger.d(TAG, "onResume... 发起播放");
        }

        mSpeedHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case WindowMessageID.START_SPEED:
                        tv_mv_speed.setText(rxByte);
                        //Log.i(TAG, "speed=" + rxByte);
                        return;
                    default:
                        return;
                }
            }
        };
        /*获取网速*/
        speedRunnable = new Runnable() {
            public void run() {
                load = load + 1;
                /*播放超时调试模式*/
                if (Play_timeout_debug == 1){
                    if ((ClientID + 1) <= MaxClientID){
                        if (Maxtimeout != 300)
                        Utils.showToast(VideoPlayerActivity.this,  getString(R.string.analysis) + ClientID + getString(R.string.Used_time) + load /2 + getString(R.string.second) + timeout + getString(R.string.Change_resolution) + (ClientID + 1), R.drawable.toast_shut);
                    }else{
                        if (Maxtimeout != 300)
                        Utils.showToast(VideoPlayerActivity.this,  getString(R.string.analysis) + ClientID + getString(R.string.Used_time) + load /2 + getString(R.string.second) + timeout + getString(R.string.Change_resolutions), R.drawable.toast_shut);
                    }
                }

                if (load >= timeout * 2){
                    if (ClientID < MaxClientID){
                        setVideoUrl(Client + Integer.toString(ClientID + 1 ));
                        load = 0;
                    }else{
                        load = 0;
                        timeout = 300;
                        if (Auto_Source == 1){
                            /*换源*/
                            Switchsource(1);
                        }else{
                            /*关闭菊花*/
                            mediaHandler.sendEmptyMessage(WindowMessageID.COLSE_SHOW_TV);
                            Switchsource(3);
                        }
                    }
                }

                if (!(lastRxByte == 0 || lastSpeedTime == 0)) {
                    long nowtime = System.currentTimeMillis();
                    long nowRxbyte = TrafficStats.getTotalRxBytes();
                    long rxbyte = nowRxbyte - lastRxByte;
                    long time = nowtime - lastSpeedTime;
                    if (!(rxbyte == 0 || time == 0)) {
                        speed = ((rxbyte / time) * 1000) / IjkMediaMeta.AV_CH_SIDE_RIGHT;
                        if (speed >= IjkMediaMeta.AV_CH_SIDE_RIGHT) {
                            rxByte = new StringBuilder(String.valueOf(String.valueOf(speed / IjkMediaMeta.AV_CH_SIDE_RIGHT))).append("MB/S").toString();
                        } else {
                            rxByte = new StringBuilder(String.valueOf(String.valueOf(speed))).append("KB/S").toString();
                        }
                        mSpeedHandler.sendEmptyMessage(WindowMessageID.START_SPEED);
                    }
                    lastRxByte = nowRxbyte;
                    lastSpeedTime = nowtime;
                }
                mSpeedHandler.postDelayed(speedRunnable, 500);
            }
        };

        /*卡顿校验*/
        speedRunnabless = new Runnable() {
            private long lastPosition = -1;
            public void run() {
//                long currentPosition = iVV.getCurrentPosition();
                currentPositions = iVV.getCurrentPosition();
                if (currentPositions == lastPosition) {
                    currentPosition = currentPositions;//卡住的时候记忆播放的位置
                    if (!iVV.isPlaying()) {//暂停
                        seizing = 0;
//                        System.out.println("点播进度暂停了！");
                    }else{
                        seizing = 1;
                        if (Number > 1){
                            seizings = 1;
                            /*解析调试*/
                            if (Play_timeout_debug == 1){
                                Utils.showToast(VideoPlayerActivity.this, getString(R.string.seizing), R.drawable.toast_shut);
                            }
                            if (PlayersNumber >= Number){
                                PlayersNumber = 0;//
                                //Numbermax
                                //PlayersNumber
                                if (Play_timeout_debug == 1){
                                    Utils.showToast(VideoPlayerActivity.this, getString(R.string.no_source), R.drawable.toast_shut);
                                }
                            }else{
                                mediaHandler.sendEmptyMessage(WindowMessageID.SHOW_TV);
                                Switchsource(4);
                            }
                        }else{
                            if (Play_timeout_debug == 1){
                                Utils.showToast(VideoPlayerActivity.this, getString(R.string.single), R.drawable.toast_shut);
                            }
                        }
//                        System.out.println("点播进度卡住了！");
                    }

                } else {
                    // 更新上一次的位置
                    lastPosition = currentPositions;
                    //没有卡住不记忆位置
                    currentPosition = 0;
//                    System.out.println("点播进度正常！");
                }
                if (seizing != 1){
                    /*播放中校验间隔*/
                    mSpeedHandler.postDelayed(speedRunnabless, seizing_time * 1000);
                }
            }
        };


        if (mPlayerStatus == PLAYER_STATUS.PLAYER_BACKSTAGE) {
            if (!iVV.isPlaying()) {
                /*修复后台暂停后一直转圈的问题*/
                iVV.start();
                mHandler.sendEmptyMessage(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
                ib_playStatus.setImageResource(R.drawable.media_pause);
            }else{
                mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
                iVV.start();
                mHandler.sendEmptyMessage(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
                ib_playStatus.setImageResource(R.drawable.media_pause);
                mediaHandler.sendEmptyMessage(WindowMessageID.COLSE_SHOW_TV);
            }
        } else {
            mProgressBar.setProgress(0);
            mediaHandler.sendEmptyMessage(WindowMessageID.SHOW_TV);
        }
    }

    /*暂停时*/
    protected void onPause() {
        super.onPause();
        isDestroy = Boolean.valueOf(true);
        Logger.d(TAG, "onPause...mPlayerStatus=" + mPlayerStatus);
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
        releaseWakeLock();
        if (iVV.isPlaying()) {
            iVV.pause();
            mLastPos = iVV.getCurrentPosition();
            mHandler.removeMessages(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
            mPlayerStatus = PLAYER_STATUS.PLAYER_BACKSTAGE;
            ib_playStatus.setImageResource(R.drawable.media_playstatus);
        }
        hideController();
        mediaHandler.sendEmptyMessage(WindowMessageID.COLSE_SHOW_TV);
    }

    /*初始化视频数据*/
    private void PrepareVodData(int postion) {
        isSwitch = true;
        if (videoInfo != null && videoInfo.size() > 0 && videoInfo.size() >= playIndex) {
            if (!vodtype.equals("MOVIE") || videoInfo.size() != 1) {
                TextView textView = tv_mv_name;
                textView.setText(vodname + "-" + videoInfo.get(playIndex).title);
            } else {
                tv_mv_name.setText(vodname);
            }
            this.url = videoInfo.get(postion).url;
            Logger.v(TAG, "vodUrl==" + ("domain=" + domain + "&url=" + url));
            mediaHandler.sendEmptyMessage(WindowMessageID.DATA_PREPARE_OK);
            mEventHandler.sendEmptyMessage(WindowMessageID.EVENT_PLAY);
        }
    }

    /*切换剧集*/
    private void SelecteVod(int postion) {
        /*指定内核*/
        String playcore = sp.getString(Constant.hd, "IJK");
        if ("自动".equals(playcore)) {
            nhposition = 0;
        } else if ("系统".equals(playcore)) {
            nhposition = 1;
        } else if ("IJK".equals(playcore)) {
            nhposition = 2;
        } else if ("EXO".equals(playcore)) {
            nhposition = 3;
        } else if ("阿里".equals(playcore)) {
            nhposition = 4;
        }
        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
        isSwitch = true;
        stopPlayback();
        mHandler.sendEmptyMessage(WindowMessageID.SHOW_TV);
        mHandler.removeMessages(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
        mLastPos = 0;
        if (!vodtype.equals("MOVIE") || videoInfo.size() == 1) {
            TextView textView = tv_mv_name;
            textView.setText(vodname + "-" + videoInfo.get(playIndex).title);
        } else {
            tv_mv_name.setText(vodname);
        }
        url = ((VideoInfo) videoInfo.get(postion)).url;
        setVideoUrl(Client);

        if (!jump_time.equals("0")) {
            /*跳片头*/
            //iVV.seekTo(Integer.parseInt(jump_time) * 1000);
        }
    }

    /*停止播放*/
    private void stopPlayback() {
        if(iVV != null){
            iVV.stopPlayback();
        }
    }

    /*初始化数据*/
    private void initData() {
        String jsonString = Sd.getString("arrayList", null);
        java.lang.reflect.Type listType = new TypeToken<ArrayList<VideoInfo>>() {}.getType();
        ArrayList<VideoInfo> recoveredList = new Gson().fromJson(jsonString, listType);
        Intent intent = getIntent();
        videoInfo = new ArrayList();
//        videoInfo = intent.getParcelableArrayListExtra("videoinfo");
        videoInfo = recoveredList;
        albumPic = intent.getStringExtra("albumPic");
        vodtype = intent.getStringExtra("vodtype");
        videoId = intent.getStringExtra("videoId");
        vodname = intent.getStringExtra("vodname");
        domain = intent.getStringExtra("domain");
        nextlink = intent.getStringExtra("nextlink");
        vodstate = intent.getStringExtra("vodstate");
        sourceId = intent.getStringExtra("sourceId");
        playIndex = intent.getIntExtra("playIndex", 0);
        mLastPos = intent.getIntExtra("collectionTime", 0);
        /*查询所有源*/
        ArrayList arrayLista = new ArrayList();
        for (int i = 0; i < source.size(); i++) {
            arrayLista.add(source.get(i));
        }
        Number =  arrayLista.size();
        PlayersNumber = Integer.parseInt(sourceId);
        int vod_Logo = SharePreferenceDataUtil.getSharedIntData(this, "vod_Logo", 0);
        if (vod_Logo == 1){
            mediaHandler.sendEmptyMessage(WindowMessageID.START_LOGO);
        }
        //Log.i(TAG, "mLastPos======" + mLastPos);
        xjposition = playIndex;
        if (playIndex >= videoInfo.size()){//选定线路不包含续播内容
            Utils.showToast(VideoPlayerActivity.this, R.string.selected, R.drawable.toast_err);
            iVV.stopPlayback();
            finish();
            return;
        }
        /*根据id查询数据库获取上次播放的时间和剧集数*/
        if (vodtype.equals("MOVIE") && videoInfo.size() == 1) {
            tv_mv_name.setText(vodname);
        } else {
            tv_mv_name.setText(videoInfo.get(playIndex).title);
        }
//        System.out.println("解码模式：硬解" + vodtype);
        PrepareVodData(playIndex);
    }

    /*换源*/
    private void PrepareVodDataa(){
        /*查询当前源的播放列表*/
        list = null;
        for (int i = 0; i < PlayersNumber; i++) {
            domain = source.get(i).getType();
            list = source.get(i).getList();
        }
        /*得到查询播放器的列表*/
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            VideoInfo vinfo = new VideoInfo();
            vinfo.title = list.get(i).getTitle();
            vinfo.url = list.get(i).getUrl();
            arrayList.add(vinfo);
        }
        /*判断当前线路是否有当前播放的剧集*/
        if (arrayList.size() >= playIndex + 1){
//            System.out.println("线路" + PlayersNumber + "有当前选集" + list);

            ArrayList arrayLista = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
                VideoInfo vinfo = new VideoInfo();
                vinfo.title = list.get(i).getTitle();
                vinfo.url = list.get(i).getUrl();
                arrayLista.add(vinfo);
            }
            videoInfo = arrayLista;
            SelecteVod(playIndex);
        }else{
            /*换源*/
            Switchsource(1);
//            System.out.println("线路" + PlayersNumber + "没有当前选集" + list);
        }
    }

    /*上报/换源*/
    private void Switchsource(int msg) {
        if (msg == 2){
            Utils.showToast(this, R.string.str_no_data_error, R.drawable.toast_err);
            iVV.stopPlayback();
            finish();
        }
        if (msg == 3){
            /*上报类型*/
            if (SharePreferenceDataUtil.getSharedIntData(this, "Fb_type", 0) == 0){
                showUpdateDialog(getString(R.string.so_sorry) + vodname + "-" + ((VideoInfo) videoInfo.get(playIndex)).title + getString(R.string.abnormal), this);
                iVV.stopPlayback();
            }else{
                Utils.showToast(this, getString(R.string.so_sorry) + vodname + "-" + ((VideoInfo) videoInfo.get(playIndex)).title + getString(R.string.abnormal), R.drawable.toast_err);
                if(videoInfo.size() > playIndex + 1){
                    //playIndex++;
                    Numbermax = 0;

                    playIndex = playIndex + 1;
                    xjposition = playIndex;
                    collectionTime = 0;
                    mLastPos = 0;
                    PlayersNumber = 0;

                    currentPosition = 0;
                    seizing = 0;
                    SelecteVod(playIndex);
//                    mediaHandler.sendEmptyMessage(WindowMessageID.PREPARE_VOD_DATA);
                    mediaHandler.sendEmptyMessage(WindowMessageID.SHOW_TV);
//                    mediaHandler.sendEmptyMessage(WindowMessageID.PROGRESSBAR_PROGRESS_RESET);
                    iVV.start();
                }else{
                    iVV.stopPlayback();
                    finish();
                }
                isCodeBlockExecuted = false; // 标记代码块已经执行过
                /*进行上报*/
                getFailreport(vodname,((VideoInfo) videoInfo.get(playIndex)).title,domain);
            }
        }

        if (msg == 1){
            if (Number > 1){
                Numbermax = Numbermax + 1;
                if (Numbermax >= Number){
                    Switchsource(3);
                }else{
                    if (PlayersNumber + 1 > Number){
                        sourceId = Integer.toString(1);
                        PlayersNumber = 1;
                    }else{
                        sourceId = Integer.toString(PlayersNumber + 1);
                        PlayersNumber ++;
                    }
                    PrepareVodDataa();
                }
            }else{
                Switchsource(3);
            }
        }

        if (msg == 4){
            seizing = 0;
            if (Number > 1){
                Numbermax = Numbermax + 1;
                if (Numbermax >= Number){
//                    Switchsource(3);
                    Utils.showToast(VideoPlayerActivity.this, "最大", R.drawable.toast_smile);
                }else{
                    if (PlayersNumber + 1 > Number){
                        sourceId = Integer.toString(1);
                        PlayersNumber = 1;
                    }else{
                        sourceId = Integer.toString(PlayersNumber + 1);
                        PlayersNumber ++;
                    }
                    PrepareVodDataa();
                }
            }else{
//                Switchsource(3);
                Utils.showToast(VideoPlayerActivity.this, "单源", R.drawable.toast_smile);
            }
        }

    }

    /*初始化视图*/
    private void initView() {
        sp = getSharedPreferences("shenma", 0);
        time = sp.getString("vip", "");/*取会员Vip时间*/
        /*no0015*/
        Trytime = SharePreferenceDataUtil.getSharedIntData(this, Constant.gD, 0);/*可试看的时间*/
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        String playratio = sp.getString(Constant.oh, "全屏拉伸");
        if ("原始比例".equals(playratio)) {
            hmblposition = 0;
        } else if ("4:3 缩放".equals(playratio)) {
            hmblposition = 1;
        } else if ("16:9缩放".equals(playratio)) {
            hmblposition = 2;
        } else if ("全屏拉伸".equals(playratio)) {
            hmblposition = 3;
        }else if ("等比缩放".equals(playratio)) {
            hmblposition = 4;
        }else if ("全屏裁剪".equals(playratio)) {
            hmblposition = 5;
        }
        String playcore = sp.getString(Constant.hd, "IJK");
        if ("自动".equals(playcore)) {
            nhposition = 0;
        } else if ("系统".equals(playcore)) {
            nhposition = 1;
        } else if ("IJK".equals(playcore)) {
            nhposition = 2;
        } else if ("EXO".equals(playcore)) {
            nhposition = 3;
        } else if ("阿里".equals(playcore)) {
            nhposition = 4;
        }
        jump_time = Utils.stringDrawNum(sp.getString("play_jump", "0"));
        jump_time_end = Utils.stringDrawNum(sp.getString("play_jump_end", "0"));
        /*检查片头片尾*/
        getJumpdata();

        setDecode();
        getPlayPreferences();
        getScreenSize();
        loadViewLayout();
        findViewById();
        setListener();
        setvvListener();
         /*开启后台事件处理线程*/
        mHandlerThread = new HandlerThread("event handler thread", Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mEventHandler = new EventHandler(mHandlerThread.getLooper());
    }

    /*设置解码*/
    private void setDecode() {
        if (sp.getInt("mIsHwDecode", 1) == 0) {
            mIsHwDecode = false;
            jmposition = 0;
            return;
        }
        mIsHwDecode = true;
        jmposition = 1;
    }

    /*检测跳片头片尾数据*/
    private void getJumpdata() {
        /*片头检测*/
        switch (jump_time) {
            case "0":
                ptposition = 0;
                break;
            case "10":
                ptposition = 1;
                break;
            case "15":
                ptposition = 2;
                break;
            case "20":
                ptposition = 3;
                break;
            case "30":
                ptposition = 4;
                break;
            case "60":
                ptposition = 5;
                break;
            case "90":
                ptposition = 6;
                break;
            case "120":
                ptposition = 7;
                break;
            case "150":
                ptposition = 8;
                break;
            case "180":
                ptposition = 9;
                break;
            case "240":
                ptposition = 10;
                break;
            case "300":
                ptposition = 11;
                break;
            default:
                ptposition = 0;
                break;
        }
        /*片尾检测*/
        switch (jump_time_end) {
            case "0":
                pwposition = 0;
                break;
            case "10":
                pwposition = 1;
                break;
            case "15":
                pwposition = 2;
                break;
            case "20":
                pwposition = 3;
                break;
            case "30":
                pwposition = 4;
                break;
            case "60":
                pwposition = 5;
                break;
            case "90":
                pwposition = 6;
                break;
            case "120":
                pwposition = 7;
                break;
            case "150":
                pwposition = 8;
                break;
            case "180":
                pwposition = 9;
                break;
            case "240":
                pwposition = 10;
                break;
            case "300":
                pwposition = 11;
                break;
            default:
                pwposition = 0;
                break;
        }
    }

    /*偏好设置*/
    private void getPlayPreferences() {
        playPreCode = sp.getInt("playPre", 0);
        if (playPreCode == 0) {
            /*选集*/
            phszposition = 0;
        } else {
            /*调节音量*/
            phszposition = 1;
        }
    }

    /*按ID查找视图*/
    private void findViewById() {
        Ad_block_up = (LinearLayout) findViewById(R.id.Ad_block_up);////换台遮挡
        Ad_block_down = (LinearLayout) findViewById(R.id.Ad_block_down);////换台遮挡
        Ad_block_right = (LinearLayout) findViewById(R.id.Ad_block_right);////换台遮挡

        webView = findViewById(R.id.webview);
        tv_logo = findViewById(R.id.tv_logo);
//        tv_logo = (ImageView) findViewById(R.id.tv_logo);
        tv_notice_root = findViewById(R.id.tv_notice_root);/*视频跑马公告内容*/
        tv_notice = findViewById(R.id.tv_notice);/*视频跑马公告内容*/
        seekBar = controlView.findViewById(R.id.seekbar);
        tv_currentTime = controlView.findViewById(R.id.tv_currentTime);
        tv_totalTime = controlView.findViewById(R.id.tv_totalTime);
        tv_menu = controlView.findViewById(R.id.tv_menu);
        ib_playStatus = controlView.findViewById(R.id.ib_playStatus);
        ib_playStatus.setOnClickListener(this);
        mProgressBar = findViewById(R.id.progressBar);
        tv_progress_time = findViewById(R.id.tv_progress_time);
        tv_mv_speed = findViewById(R.id.tv_mv_speed);
        tv_time = time_controlView.findViewById(R.id.tv_time);
        tv_mv_name = time_controlView.findViewById(R.id.tv_mv_name);
        mProgressBar.setVisibility(View.GONE);
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        iVV = findViewById(R.id.i_video_view);
        iVV.setHudView((TableLayout) findViewById(R.id.hubview));
        int Decode = sp.getInt("mIsHwDecode", 1);
        //System.out.println("解码模式：" + Decode);
        if (Decode == 1) {
            iVV.setDecode(Boolean.valueOf(true));
        } else if (Decode == 0) {
            iVV.setDecode(Boolean.valueOf(false));
        }
    }

    /*加载视图布局*/
    private void loadViewLayout() {
        controlView = getLayoutInflater().inflate(R.layout.mv_media_controler, null);
        controler = new PopupWindow(controlView);
        time_controlView = getLayoutInflater().inflate(R.layout.mv_media_time_controler, null);
        time_controler = new PopupWindow(time_controlView);
    }

    /*事件处理程序*/
    class EventHandler extends Handler {
        public EventHandler(Looper looper) {
            super(looper);
        }
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WindowMessageID.EVENT_PLAY:
                    /*如果已经播放了，等待上一次播放结束*/
                    if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
                        synchronized (SYNC_Playing) {
                            try {
                                SYNC_Playing.wait();
                                Logger.i(TAG, "SYNC_Playing.wait()");
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public final void run() {
                            /*访问解析*/
                            setVideoUrl(Client);
                        }
                    });
                    return;

                case WindowMessageID.SUCCESS:
                    /*续播原版*/
                    /*
                    if (mLastPos > 0) {
                        //续播
//                        iVV.seekTo(mLastPos);
//                        mLastPos = 0;
                    }else if(!jump_time.equals("0")){
                        //跳片头
//                        iVV.seekTo(Integer.parseInt(jump_time) * 1000);
                    }
                    */

                    if (Adblock == 1){
                        /*片头广告跳过*/
                        if (headposition != 0 ){
//                        System.out.println("解码模式：硬解" + headposition);
                            if (headposition * 1000 > mLastPos){
                                iVV.seekTo(headposition * 1000);
                            }else{
                                iVV.seekTo(mLastPos);
                            }
                        }
                    }

                    if (seizings == 1){
                        iVV.seekTo((int) currentPosition);
                    }

                    /*续播*/
                    if(mLastPos > 0){
                        if (vodtype.equals("LIVE")){//直播状态不续播
                            mLastPos2 = 0;
                        }else{
                            mLastPos2 = mLastPos;
                            mLastPos = 0;
                        }
                    }

                    /*续播简写*/
                    /*
                    mLastPos2 = (mLastPos > 0 && !vodtype.equals("LIVE")) ? mLastPos : 0;
                    mLastPos = (mLastPos > 0 && vodtype.equals("LIVE")) ? 0 : mLastPos;
                    */

                    /*试看*/
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        if (new Date(System.currentTimeMillis()).getTime() > format.parse(GetTimeStamp.timeStamp2Date(time, "")).getTime()) {
                            /*账户已过期*/
                            vipstate = 1;
                        } else {
                            /*账户未过期*/
                            vipstate = 0;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    /*永久会员*/
                    if (time.equals("999999999")) {
                        /*设置账户状态为未过期*/
                        vipstate = 0;
                    }
                    iVV.start();
                    mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
                    long secondTime = System.currentTimeMillis();
                    Album album = new Album();
                    album.setAlbumId(videoId);
                    album.setAlbumSourceType(sourceId);
                    album.setCollectionTime(collectionTime);
                    album.setPlayIndex(playIndex);
                    album.setAlbumPic(albumPic);
                    album.setAlbumType(vodtype);
                    album.setAlbumTitle(vodname);
                    album.setAlbumState("观看时间:未知");
                    album.setNextLink(nextlink);
                    album.setTime(String.valueOf(secondTime));
                    album.setTypeId(2);
                    dao.addAlbums(album);
                    return;
                default:
                    return;
            }
        }

    }

    /*拼接解析参数*/
    public void setVideoUrl(String Client) {
        //Log.i(TAG, "setVideoUrl: " + url);
        /*no0011*/
        /*读取提交方式 0=GET 1=POST*/
        int Submission_method = SharePreferenceDataUtil.getSharedIntData(this, "Submission_method", 0);
        /*视频地址编码*/
        String urlEncode = Utils.UrlEncodeChinese(Client + "/?url=" + url);
        /*开始请求解析*/
        analysisUrl(urlEncode,Submission_method);
    }

    /*开始请求解析*/
    private void analysisUrl(String urls ,int way) {
        /*no0001*/
        int Timeout = SharePreferenceDataUtil.getSharedIntData(this, "Timeout", 25);
        /*加载get访问*/
        mQueue = Volley.newRequestQueue(this, new HurlStack());
        /*提交参数*/
        final String account = sp.getString("userName", "");/*帐号*/
        final String password = sp.getString("passWord", "");/*密码*/
        final String token = sp.getString("ckinfo", "");/*token*/
        final String machineid = Utils.GetAndroidID(this);/*安卓ID*/
        final double value = Double.valueOf(Utils.getVersion(VideoPlayerActivity.this).toString());/*版本号*/
        final String name = Utils.getEcodString(vodname + "-" + ((VideoInfo) videoInfo.get(playIndex)).title);/*片名*/

        /*GET提交方式*/
        if (way ==  0){
            String url = urls + "&app=" + Api.APPID + "&account=" + account + "&password=" + password + "&token=" + token + "&machineid=" + machineid + "&edition=" + value + "&vodname=" + name + "&line=" + domain + "&new=1";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new com.android.volley.Response.Listener<String>() {
                        public void onResponse(String response) {
                            analysisUrlResponse(response);
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    analysisUrlError(error);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    // 添加您的请求参数
                    params.put("app", Api.APPID);
                    params.put("account", account);
                    params.put("password", password);
                    params.put("token", token);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(VideoPlayerActivity.this, "Authorization", ""),Constant.d));  /*设置其他请求头*/
                    return headers;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(Timeout * 1000,//请求的超时时间（以毫秒为单位），即请求在等待响应的最长时间
                    0,//最大重试次数。如果请求失败，将会重试的次数。
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));//重试的时间间隔倍数。每次重试的等待时间将是前一个重试时间乘以该倍数。
            mQueue.add(stringRequest);

        }

        /*POST提交方式*/
        if (way ==  1){
            String url = urls;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>() {
                        public void onResponse(String response) {
                            analysisUrlResponse(response);
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    analysisUrlError(error);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    // 添加您的请求参数
                    params.put("app", Api.APPID);
                    params.put("account", account);
                    params.put("password", password);
                    params.put("token", token);
                    params.put("machineid", machineid);
                    params.put("edition", String.valueOf(value));
                    params.put("vodname", name);
                    params.put("line", domain);
                    params.put("new", "1");
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(VideoPlayerActivity.this, "Authorization", ""),Constant.d));  /*设置其他请求头*/
                    return headers;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(Timeout * 1000,//请求的超时时间（以毫秒为单位），即请求在等待响应的最长时间
                    0,//最大重试次数。如果请求失败，将会重试的次数。
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));//重试的时间间隔倍数。每次重试的等待时间将是前一个重试时间乘以该倍数。
            mQueue.add(stringRequest);

        }
        //Log.i(TAG, "analysisUrl: " + way);
    }

    /*解析地址获取成功*/
    public void analysisUrlResponse(String response) {
        //Log.i(TAG, "analysisUrlResponse: " + response);
        try {
            /*解析结果不加密*/
            JSONObject jSONObject = new JSONObject(response);
            /*解析结果加密*/
//            JSONObject jSONObject = new JSONObject(Rc4.decry_RC4(response, Constant.dKEY));

            int code = jSONObject.optInt("code");/*状态码*/
            int encrypt = jSONObject.optInt("encrypt");/*加密信息 0=不加密 1=加密*/
            if (code == 200){
                JSONObject data = jSONObject.getJSONObject("data");/*url地址*/
                JSONObject header = data.getJSONObject("header");/*头等配置信息*/
                userAgent = header.getString("User-Agent");
                if (header.has("Referer")) {
                    Referer = header.getString("Referer");
                }
                if (header.has("referer")) {
                    Referer = header.getString("referer");
                }
                final String url = data.getString("url");
                ClientID = data.getInt("ClientID");
                MaxClientID = data.getInt("MaxClientID");
                Maxtimeout = data.getInt("Maxtimeout");
                Exclude_content = data.getString("Exclude_content");
                timeout = Maxtimeout;
                core = data.getInt("Core");
                safe_mode = data.getInt("Safe");
                Ad_block = data.getInt("Ad_block");
                position = data.getInt("position");
                // Ewmsize = data.getInt("Ewmsize");
                EwmWidth = data.getInt("EwmWidth");
                EwmHeight = data.getInt("EwmHeight");
                Moviesize = data.getInt("Moviesize");
                Tvplaysize = data.getInt("Tvplaysize");
                headposition = data.getInt("headposition");
                /*有指定内核授权*/
                if (core_mode == 1){
//                    System.out.println("解码模式：硬解有指定内核功能的授权");
                    /*指定内核*/
                    if (core != 99){
//                        System.out.println("解码模式：硬解指定内核");
                        if (safe_mode == 1){
                            nhposition = core;
//                            System.out.println("解码模式：硬解指定内核强制模式" + nhposition);
                        }else{
//                            System.out.println("解码模式：硬解指定内核安全模式");
                            if (Build.VERSION.SDK_INT < 22){
                                core = 99;
//                                String playcore = sp.getString(Constant.hd, "IJK");
//                                if ("自动".equals(playcore)) {
//                                    nhposition = 0;
//                                } else if ("系统".equals(playcore)) {
//                                    nhposition = 1;
//                                } else if ("IJK".equals(playcore)) {
//                                    nhposition = 2;
//                                } else if ("EXO".equals(playcore)) {
//                                    nhposition = 3;
//                                }
                                nhposition = 2;
                            }else{
                                nhposition = core;
                            }
                        }

                    }else{
//                        System.out.println("解码模式：硬解不指定内核");
                        core = 99;
                        String playcore = sp.getString(Constant.hd, "IJK");
                        if ("自动".equals(playcore)) {
                            nhposition = 0;
                        } else if ("系统".equals(playcore)) {
                            nhposition = 1;
                        } else if ("IJK".equals(playcore)) {
                            nhposition = 2;
                        } else if ("EXO".equals(playcore)) {
                            nhposition = 3;
                        } else if ("阿里".equals(playcore)) {
                            nhposition = 4;
                        }
                    }
                }

                Conditions = data.getString("Conditions");
                Type = data.getInt("Type");
                iVV.setUserAgent(userAgent);
                iVV.setReferer(Referer);
                headers = new HashMap<>();
                Iterator<String> keys = header.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String value = header.getString(key);
                    headers.put(key, value);
                    //Log.d("key==", key);
                    //Log.d("value==", value);
                }
                if (Type == 0){
                    webView.setVisibility(View.GONE);
                    /*直接播放*/
                    if (encrypt == 1){
                        if (!url.equals("")){
                            /*播放地址如果中文用url编码和User-Agent头等信息发送被播放器*/
                            if (Failed.equals("")){
                                iVV.setVideoPath(Utils.UrlEncodeChinese(Rc4.decryptBase64(url,Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.yk, ""),Constant.d))), headers);
                            }else{
                                iVV.setVideoPath(Utils.UrlEncodeChinese(Failed), headers);
                            }
                            /*设置反馈地址*/
                            Fburl = Utils.UrlEncodeChinese(Rc4.decryptBase64(url,Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.yk, ""),Constant.d)));
                            mEventHandler.sendEmptyMessage(WindowMessageID.SUCCESS);
                        }else{
                            timeout = 1;
                        }
                    }else{
                        if (!url.equals("")){
                            /*播放地址如果中文用url编码和User-Agent头等信息发送被播放器*/
                            if (Failed.equals("")){
                                iVV.setVideoPath(Utils.UrlEncodeChinese(url), headers);
                            }else{
                                iVV.setVideoPath(Utils.UrlEncodeChinese(Failed), headers);
                            }
                            /*设置反馈地址*/
                            Fburl = Utils.UrlEncodeChinese(url);
                            mEventHandler.sendEmptyMessage(WindowMessageID.SUCCESS);
                        }else{
                            timeout = 1;
                        }
                    }
                }else{
                    /*嗅探后播放*/
                    if (Build.VERSION.SDK_INT < 23){
                        /*嗅探调试模式*/
                        if (Sniff_debug_mode == 1){
                            Toast.makeText(this, R.string.Sniffing_messages, Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        if (encrypt == 1){
                            if (!url.equals("")){
                                jxurls = 0;
                                initializeWebView();
                                Sniffing(Utils.UrlEncodeChinese(Rc4.decryptBase64(url,Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.yk, ""),Constant.d))));
                            }else{
                                timeout = 1;
                            }
                        }else{
                            if (!url.equals("")){
                                jxurls = 0;
                                initializeWebView();
                                Sniffing(url);
                            }else{
                                timeout = 1;
                            }
                        }
                    }
                }
            }else{
                timeout = 1;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*嗅探配置*/
    private void initializeWebView() {
        /*嗅探播放*/
        webView.stopLoading();
        /*嗅探调试模式*/
        if (Sniff_debug_mode == 1){
            webView.setVisibility(View.VISIBLE);
            Toast.makeText(this, R.string.Sniffing_message, Toast.LENGTH_SHORT).show();
        }
        webView.setWebViewClient(new WebViewClient());
        WebSettings settings = webView.getSettings();
        settings.setUserAgentString(userAgent);//访问嗅探用的头
        settings.setJavaScriptEnabled(true);//支持JavaScript
        settings.setDefaultTextEncodingName("utf-8");//设置默认文本编码名称
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);//不缓存
        settings.setPluginState(WebSettings.PluginState.ON);//设置插件状态
        settings.setDisplayZoomControls(false);//设置显示缩放控件
        settings.setAllowFileAccess(true);//设置允许文件访问
        settings.setSupportZoom(true);//设置支持缩放
        settings.setAllowContentAccess(true);//设置允许内容访问
        settings.setBuiltInZoomControls(true);//设置内置缩放控件
        settings.setUseWideViewPort(true);//设置使用宽视图端口
        settings.setLoadWithOverviewMode(true);//设置加载概览模式
        settings.setSavePassword(true);//设置保存密码
        settings.setSaveFormData(true);//设置保存表单数据
        settings.setTextZoom(100);//设置文本缩放
        settings.setDomStorageEnabled(true);//WebView中插件的状态为开启
        settings.setSupportMultipleWindows(true);//设置支持多个窗口
        if (Build.VERSION.SDK_INT >= 21) {
            settings.setMixedContentMode(0);
        }
        if (Build.VERSION.SDK_INT >= 17) {
            settings.setMediaPlaybackRequiresUserGesture(true);//设置媒体播放要求用户手势
        }
        if (Build.VERSION.SDK_INT >= 16) {
            settings.setAllowFileAccessFromFileURLs(true);//设置允许从文件URL访问文件
            settings.setAllowUniversalAccessFromFileURLs(true);//设置允许从文件URL进行通用访问
        }
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLoadsImagesAutomatically(true);//自动设置加载图像
        settings.setAppCacheEnabled(true);//设置应用缓存已启用
        settings.setAppCachePath(getCacheDir().getAbsolutePath());//设置应用程序缓存路径
        settings.setDatabaseEnabled(true);//设置数据库已启用
        settings.setGeolocationDatabasePath(getDir("database", 0).getPath());//设置地理位置数据库路径
        settings.setGeolocationEnabled(true);//的意思是设置是否允许使用地理位置功能。如果设置为true，则允许网页通过JavaScript代码获取用户的地理位置信息。
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.createInstance(getApplicationContext());
        }
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= 21) {
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        }
    }

    /*执行嗅探*/
    private void Sniffing(String url){
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            /*判断api低于21*/
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    url = request.getUrl().toString();
                } else {
                    url = request.toString();
                }
                //System.out.println("返回数据链接:" + url);
                try {
                    JSONObject jsonObject = new JSONObject(Conditions);
                    Iterator<String> keys = jsonObject.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        if (url.contains(jsonObject.getString(key)) && !url.contains(Exclude_content)) {
                            // 拦截请求
                            webHeaders = new HashMap<>();
                            try {
                                /*判断安卓5.0以上*/
                                Map<String, String> hds = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                    hds = request.getRequestHeaders();
                                }
                                /*原版*/
//                                Map<String, String> hds = request.getRequestHeaders();
                                for (String k : hds.keySet()) {
                                    if (k.equalsIgnoreCase("user-agent")
                                            || k.equalsIgnoreCase("referer")
                                            || k.equalsIgnoreCase("origin")) {
                                        webHeaders.put(k, " " + hds.get(k));
                                    }
                                }
                            } catch (Throwable th) {
                                // 异常处理
                            }

                            jxurl = url;
                            /*嗅探重复校验*/
                            if (jxurls != 0){
                                return null;
                            }
                            jxurls = 1;
                            mediaHandler.sendEmptyMessage(WindowMessageID.Sniffing);
                            //System.out.println("播放连接:" + url + webHeaders);
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    /*解析地址获取失败*/
    public void analysisUrlError(VolleyError volleyError) {
        //Log.i(TAG, "analysisUrlError: " + volleyError);
        if (volleyError instanceof TimeoutError) {
//            Maxtimeout = 300;
//            Utils.showToast(this, "客户端超时!", R.drawable.toast_shut);
//            iVV.stopPlayback();
//            finish();
//            System.out.println("请求超时");
        }
        if (volleyError instanceof AuthFailureError) {
//            Maxtimeout = 300;
//            Utils.showToast(this, "客户端认证错误!", R.drawable.toast_shut);
//            iVV.stopPlayback();
//            finish();
            //System.out.println("身份验证失败错误");
        }
        if(volleyError instanceof NetworkError) {
//            Maxtimeout = 300;
//            Utils.showToast(this, "请检查网络!", R.drawable.toast_shut);
//            iVV.stopPlayback();
//            finish();
//            System.out.println("请检查网络");
        }
        if(volleyError instanceof ServerError) {
//            Maxtimeout = 300;
            Utils.showToast(this, "客户端不存在!", R.drawable.toast_shut);
            iVV.stopPlayback();
            finish();
            //System.out.println("错误404");
        }

    }

    /*设置侦听器*/
    private void setListener() {
        tv_menu.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                /*单击*/
                /*手势监听*/
                hideController();
                showMenu();
            }
        });
        mGestureDetector = new GestureDetector(new SimpleOnGestureListener() {
            public boolean onDoubleTap(MotionEvent e) {
                /*双击*/
                hideController();
                showMenu();
                return true;
            }

            public boolean onSingleTapConfirmed(MotionEvent e) {
                // TODO Auto-generated method stub
                /*单击时*/
                if (isControllerShow) {
                    cancelDelayHide();
                    hideController();
                } else {
                    showController();
                    hideControllerDelay();
                }
                hideMenu();
                //return super.onSingleTapConfirmed(e);
                return true;
            }

            public void onLongPress(MotionEvent e) {
                // TODO Auto-generated method stub
                /*长按*/
                //super.onLongPress(e);
//                hideController();
//                showMenu();
                Logger.i(TAG, "mGestureDetector...onLongPress");
                /*进行倍速播放*/
                if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE){
                    setSpeed(2.0f,0);
                }
            }



            public boolean onDown(MotionEvent e) {
                // TODO Auto-generated method stub
                /*向下*/
                Logger.i(TAG, "mGestureDetector...onDown");
                return super.onDown(e);
            }


            public boolean onSingleTapUp(MotionEvent e) {
                // TODO Auto-generated method stub
                /*单次点击*/
                Logger.i(TAG, "mGestureDetector...onSingleTapUp");
                return super.onSingleTapUp(e);
            }

            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Logger.i(TAG, "mGestureDetector...onFling");
                return false;
            }

            public boolean onDoubleTapEvent(MotionEvent e) {
                // TODO Auto-generated method stub
                Logger.i(TAG, "mGestureDetector...onDoubleTapEvent");
                return super.onDoubleTapEvent(e);
            }

            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Logger.i(TAG, "mGestureDetector...onScroll");
                return false;
            }
        });
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                hideControllerDelay();
                int iseekPos = seekBar.getProgress();
                /*SeekBark完成seek时执行seekTo操作并更新界面*/
                iVV.seekTo(iseekPos * 1000);
                //Log.v(TAG, "seek to " + iseekPos);
                mHandler.sendEmptyMessage(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                /*SeekBar开始seek时停止更新*/
                mHandler.removeMessages(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                //ib_rewind.setImageResource(R.drawable.media_rewind);
                //ib_fastForward.setImageResource(R.drawable.media_fastforward)
                updateTextViewWithTimeFormat(tv_currentTime, progress);
                /*检查播放进度*/
                updateprogress(progress);
            }
        });
    }

    /*播放器VV监听*/
    private void setvvListener() {

        /*设置缓冲监听*/
        iVV.setOnPlayingBufferCacheListener(new OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer mp, int percent) {

                if (percent > 1){
                    if (percent > 98){
                        seekBar.setSecondaryProgress(iVV.getDuration() / 1000);
//                        System.out.println("缓冲完毕");
                        return;
                    }
                    seekBar.setSecondaryProgress(iVV.getDuration() /1000 * iVV.getBufferPercentage() /100);
                    //seekBar.setSecondaryProgress(iVV.getDuration() /1000 * percent /100);
//                    System.out.println("缓冲:播放进度-" + iVV.getCurrentPosition()/1000 + " 回调缓冲进度1-" + percent + "回调缓冲进度2-" + iVV.getBufferPercentage());
                }
            }
        });

        /*设置错误监听*/
        iVV.setOnErrorListener(new OnErrorListener() {
            public boolean onError(IMediaPlayer arg0, int what, int extra) {
                Logger.d(TAG, "what=" + what);
                synchronized (SYNC_Playing) {
                    if (isCodeBlockExecuted) {
                        return true; // 如果代码块已经执行过，则直接返回，避免重复执行
                    }
                    SYNC_Playing.notify();
                    Logger.i(TAG, "onError...SYNC_Playing.notify()");
                    /*更改状态*/
//                    mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
//                    mediaHandler.sendEmptyMessage(WindowMessageID.COLSE_SHOW_TV);
//                    mHandler.removeMessages(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
//                    isDestroy = Boolean.valueOf(false);

                    /*最后添加失败不换解码尝试*/
                    mHandler.sendEmptyMessage(WindowMessageID.PLAY_ERROR);

                }
//                if (!isDestroy.booleanValue()) {
//                    mHandler.sendEmptyMessage(WindowMessageID.HIDE_MENU);
//                    if (isSwitch) {
//                        mHandler.sendEmptyMessage(WindowMessageID.SWITCH_CODE);
//                    } else if (videoInfo.size() > playIndex + 1) {
//                        mHandler.sendEmptyMessage(WindowMessageID.PLAY_ERROR);
//                    } else {
//                        mHandler.sendEmptyMessage(WindowMessageID.ERROR);
//                    }
//                }
                isCodeBlockExecuted = true; // 标记代码块已经执行过
                return true;
            }
        });

        /*设置就绪监听*/
        iVV.setOnPreparedListener(new OnPreparedListener() {
            public void onPrepared(IMediaPlayer arg0) {
                // TODO Auto-generated method stub
                //Log.v(TAG, "setOnPreparedListener");
				// 获取 DisplayMetrics 对象
                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                // DPI 缩放因子
                int dpi = displayMetrics.densityDpi;
//                Utils.showToast(VideoPlayerActivity.this,"当前设备dpi是" + dpi, R.drawable.toast_shut);

                // 设置LinearLayout为可见

                /*验证广告遮挡是否已授权*/
                if (Adblock == 1){
                    if (Ad_block == 1){
                        Navigation_mode = 0;
                        if (position == 0){
                            Ad_block_up.setVisibility(View.VISIBLE);
                            Ad_block_down.setVisibility(View.GONE);
                            Ad_block_right.setVisibility(View.GONE);
                        }else if (position == 1){
                            Ad_block_down.setVisibility(View.VISIBLE);
                            Ad_block_up.setVisibility(View.GONE);
                            Ad_block_right.setVisibility(View.GONE);
                        }else if (position == 2){
                            Ad_block_up.setVisibility(View.VISIBLE);
                            Ad_block_down.setVisibility(View.VISIBLE);
                            Ad_block_right.setVisibility(View.GONE);
                        }else if (position == 3){
                            Ad_block_up.setVisibility(View.VISIBLE);
                            Ad_block_down.setVisibility(View.VISIBLE);
                            Ad_block_right.setVisibility(View.VISIBLE);
                        }else if (position == 4){
                            Ad_block_up.setVisibility(View.VISIBLE);
                            Ad_block_right.setVisibility(View.VISIBLE);
                            Ad_block_down.setVisibility(View.GONE);
                        }else if (position == 5){
                            Ad_block_down.setVisibility(View.VISIBLE);
                            Ad_block_right.setVisibility(View.VISIBLE);
                            Ad_block_up.setVisibility(View.GONE);
                        }else if (position == 6){
                            Ad_block_down.setVisibility(View.GONE);
                            Ad_block_right.setVisibility(View.VISIBLE);
                            Ad_block_up.setVisibility(View.GONE);
                        }
                    }else{
                        Navigation_mode = SharePreferenceDataUtil.getSharedIntData(VideoPlayerActivity.this, "Navigation_mode", 0);
                        Ad_block_up.setVisibility(View.GONE);
                        Ad_block_down.setVisibility(View.GONE);
                        Ad_block_right.setVisibility(View.GONE);
                    }
                }else{
                    Navigation_mode = SharePreferenceDataUtil.getSharedIntData(VideoPlayerActivity.this, "Navigation_mode", 0);
                }



                // 获取FrameLayout.LayoutParams（假设Ad_block_right的直接父容器是FrameLayout）
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) Ad_block_right.getLayoutParams();
                // 设置新的高度值，这里使用具体的像素值或资源值
//                 params.height = getResources().getDimensionPixelSize(R.dimen.sm_250);





//                System.out.println("解码模式：硬解屏幕像素" + screenWidth + "-" +  screenHeight );

//                System.out.println("解码模式：硬解视频像素" + iVV.getVideoWidth() + "-" + iVV.getVideoHeight());

                int width;
                int height;
                float specAspectRatio = (float) screenWidth / (float) screenHeight;
                float displayAspectRatio;
                displayAspectRatio = (float) iVV.getVideoWidth() / (float) iVV.getVideoHeight();
                if (iVV.getVideoSarNum() > 0 && iVV.getmVideoSarDen() > 0)
                    displayAspectRatio = displayAspectRatio * iVV.getVideoSarNum() / iVV.getmVideoSarDen();
                boolean shouldBeWider = displayAspectRatio > specAspectRatio;
                if (shouldBeWider) {
                    width = screenWidth;
                    height = (int) (width / displayAspectRatio);
                } else {
                    height = screenHeight;
                    width = (int) (height * displayAspectRatio);
                }
//                System.out.println("解码模式：硬解" + width + "=" + height  );
                double pmzhb = ((double) width / height);//硬解视频纵横比


//                double pmzhb = ((double) iVV.getVideoWidth() / iVV.getVideoHeight());//硬解视频纵横比
//                System.out.println("解码模式：硬解视频纵横比" + pmzhb);

                double zdspgd = (screenWidth/pmzhb);//最大视频高度

//                System.out.println("解码模式：硬解最大视频高度" + zdspgd);

                int zdsd = (int)(screenHeight - zdspgd) / 2;//黑边高度


//                System.out.println("解码模式：硬解黑边高度" + (screenHeight - zdspgd) /2);

                if (zdsd <= 0){
                    zdsd = 0;
                }

                double scaledVideoWidth = Math.min(screenWidth, screenHeight * pmzhb);

                int extraSpace = screenWidth - (int) scaledVideoWidth;

                int blackBarSize = extraSpace / 2;
                
                if (blackBarSize <= 0){
                    blackBarSize = 0;
                }



                // int EwmsizeId;
                // if (Ewmsize == 100) {
                //     EwmsizeId = R.dimen.sm_100;
                // } else if(Ewmsize == 150){
                //     EwmsizeId = R.dimen.sm_150;
                // } else if(Ewmsize == 200){
                //     EwmsizeId = R.dimen.sm_200;
                // } else if(Ewmsize == 250){
                //     EwmsizeId = R.dimen.sm_250;
                // } else if(Ewmsize == 300){
                //     EwmsizeId = R.dimen.sm_300;
                // } else if(Ewmsize == 350){
                //     EwmsizeId = R.dimen.sm_350;
                // }else {
                //     EwmsizeId = R.dimen.sm_250;
                // }
                // int EwmsizeInPx = getResources().getDimensionPixelSize(EwmsizeId);
                // Ad_block_right.getLayoutParams().height = EwmsizeInPx;
                // Ad_block_right.getLayoutParams().width = EwmsizeInPx;
                
                int ewmwidth = (blackBarSize + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EwmWidth, getResources().getDisplayMetrics()) );
                int ewmheight = (zdsd + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EwmHeight, getResources().getDisplayMetrics()));
                Ad_block_right.getLayoutParams().height = ewmheight;
                Ad_block_right.getLayoutParams().width = ewmwidth;

                if (vodtype.equals("MOVIE")){
					int MoviesizeInPx;
                    if (dpi > 240){
                        MoviesizeInPx = (zdsd + ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Moviesize, getResources().getDisplayMetrics()) /2));
                    }else{
                        MoviesizeInPx = (zdsd + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Moviesize, getResources().getDisplayMetrics()) );
                    }
                    //int MoviesizeInPx = (zdsd + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Moviesize, getResources().getDisplayMetrics()) );
//                    int MoviesizeInPx = (zdsd + Moviesize);
//                    System.out.println("解码模式：硬解电影" + MoviesizeInPx);
                    Ad_block_up.getLayoutParams().height = MoviesizeInPx;
                    Ad_block_down.getLayoutParams().height = MoviesizeInPx;

                }else {
					int TvplaysizeInPx;
                    if (dpi > 240){
                        int s = dpi / 240;
                        TvplaysizeInPx = (zdsd + ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Tvplaysize, getResources().getDisplayMetrics()) /2));
                    }else{
                        TvplaysizeInPx = (zdsd + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Tvplaysize, getResources().getDisplayMetrics()) );
                    }
                    //int TvplaysizeInPx = (zdsd + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Tvplaysize, getResources().getDisplayMetrics()) );
//                    int TvplaysizeInPx = (zdsd + Tvplaysize);
//                    System.out.println("解码模式：硬解电视剧" + TvplaysizeInPx);
                    Ad_block_up.getLayoutParams().height = TvplaysizeInPx;
                    Ad_block_down.getLayoutParams().height = TvplaysizeInPx;
                }

                if (Play_timeout_debug == 1){
                    Ad_block_down.setBackgroundResource(R.drawable.button_normal_bga);
                    Ad_block_up.setBackgroundResource(R.drawable.button_normal_bga);
                    Ad_block_right.setBackgroundResource(R.drawable.button_normal_bga);
                }



//                if (vodtype.equals("MOVIE")){
//
//
//                    int MoviesizeId;
//                    if (Moviesize == 60) {
//                        MoviesizeId = R.dimen.sm_60;
//                    } else if (Moviesize == 65) {
//                        MoviesizeId = R.dimen.sm_65;
//                    }else if (Moviesize == 70) {
//                        MoviesizeId = R.dimen.sm_70;
//                    }else if (Moviesize == 75) {
//                        MoviesizeId = R.dimen.sm_75;
//                    }else if (Moviesize == 80) {
//                        MoviesizeId = R.dimen.sm_80;
//                    }else if (Moviesize == 85) {
//                        MoviesizeId = R.dimen.sm_85;
//                    }else if (Moviesize == 90) {
//                        MoviesizeId = R.dimen.sm_90;
//                    }else if (Moviesize == 95) {
//                        MoviesizeId = R.dimen.sm_95;
//                    }else if(Moviesize == 100){
//                        MoviesizeId = R.dimen.sm_100;
//                    }else if (Moviesize == 105) {
//                        MoviesizeId = R.dimen.sm_105;
//                    } else if(Moviesize == 110){
//                        MoviesizeId = R.dimen.sm_110;
//                    } else if(Moviesize == 115){
//                        MoviesizeId = R.dimen.sm_115;
//                    } else if(Moviesize == 120){
//                        MoviesizeId = R.dimen.sm_120;
//                    } else if(Moviesize == 125){
//                        MoviesizeId = R.dimen.sm_125;
//                    }else {
//                        MoviesizeId = R.dimen.sm_120;
//                    }
//                    int MoviesizeInPx = getResources().getDimensionPixelSize(MoviesizeId);
//                    Ad_block_up.getLayoutParams().height = MoviesizeInPx;
//                    Ad_block_down.getLayoutParams().height = MoviesizeInPx;
////                    System.out.println("解码模式：硬解Movie");
//                }else {
//                    int TvplaysizeId;
//                    if (Tvplaysize == 60) {
//                        TvplaysizeId = R.dimen.sm_60;
//                    } else if (Tvplaysize == 65) {
//                        TvplaysizeId = R.dimen.sm_65;
//                    }else if (Tvplaysize == 70) {
//                        TvplaysizeId = R.dimen.sm_70;
//                    }else if (Tvplaysize == 75) {
//                        TvplaysizeId = R.dimen.sm_75;
//                    }else if (Tvplaysize == 80) {
//                        TvplaysizeId = R.dimen.sm_80;
//                    }else if (Tvplaysize == 85) {
//                        TvplaysizeId = R.dimen.sm_85;
//                    }else if (Tvplaysize == 90) {
//                        TvplaysizeId = R.dimen.sm_90;
//                    }else if (Tvplaysize == 95) {
//                        TvplaysizeId = R.dimen.sm_95;
//                    }else if(Tvplaysize == 100){
//                        TvplaysizeId = R.dimen.sm_100;
//                    }else if (Tvplaysize == 105) {
//                        TvplaysizeId = R.dimen.sm_105;
//                    } else if(Tvplaysize == 110){
//                        TvplaysizeId = R.dimen.sm_110;
//                    } else if(Tvplaysize == 115){
//                        TvplaysizeId = R.dimen.sm_115;
//                    } else if(Tvplaysize == 120){
//                        TvplaysizeId = R.dimen.sm_120;
//                    } else if(Tvplaysize == 125){
//                        TvplaysizeId = R.dimen.sm_125;
//                    }else {
//                        TvplaysizeId = R.dimen.sm_70;
//                    }
//                    int TvplaysizeInPx = getResources().getDimensionPixelSize(TvplaysizeId);
//                    Ad_block_up.getLayoutParams().height = TvplaysizeInPx;
//                    Ad_block_down.getLayoutParams().height = TvplaysizeInPx;
////                    System.out.println("解码模式：硬解Tvplay");
//                }





                // 应用新的布局参数
                Ad_block_right.setLayoutParams(params);


                if (Auto_Source == 1){
                    if (vod_caton_check == 1){
                        /*起始校验时间*/
                        mSpeedHandler.postDelayed(speedRunnabless, seizing_time * 1000);////设置卡顿校验
                    }
                }

                seizings = 0;

                /*清除缓冲进度*/
                seekBar.setSecondaryProgress(0);
                /*开启视频跑马公告*/
                mediaHandler.sendEmptyMessageDelayed(WindowMessageID.START_NOTICE_GONE, Vod_Notice_starting_time * 1000);
                /*续播*/
                if(mLastPos > 0){
                    if (vodtype.equals("LIVE")){//直播状态不续播
                        mLastPos2 = 0;
                    }else{
                        mLastPos2 = mLastPos;
                        mLastPos = 0;
                    }
                }

                mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
                Numbermax = 0;

                mediaHandler.sendEmptyMessage(WindowMessageID.COLSE_SHOW_TV);
                mediaHandler.sendEmptyMessage(WindowMessageID.SELECT_SCALES);
                mHandler.sendEmptyMessage(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
            }
        });

        /*设置完成监听*/
        iVV.setOnCompletionListener(new OnCompletionListener() {
            public void onCompletion(IMediaPlayer arg0) {
                Logger.d(TAG, "onCompletion中Thread=" + Thread.currentThread().getName());
                // TODO Auto-generated method stub
                synchronized (SYNC_Playing) {
                    SYNC_Playing.notify();
                    Logger.i(TAG, "onCompletion...SYNC_Playing.notify()");
                }
                mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
                mHandler.removeMessages(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
                Logger.i(TAG, "isNext=" + isNext + ".....isLast=" + isLast + "....isPause=" + isPause + "....isDestroy=" + isDestroy);
                if (isNext.booleanValue()) {
                    if (videoInfo.size() > playIndex + 1) {
                        Numbermax = 0;

                        playIndex = playIndex + 1;
                        xjposition = playIndex;
                        collectionTime = 0;
                        mLastPos = 0;
//                        mediaHandler.sendEmptyMessage(WindowMessageID.PREPARE_VOD_DATA);
                        mediaHandler.sendEmptyMessage(WindowMessageID.SHOW_TV);
//                        mediaHandler.sendEmptyMessage(WindowMessageID.PROGRESSBAR_PROGRESS_RESET);
                    } else {
                        iVV.stopPlayback();
                        finish();
                    }
                    isNext = Boolean.valueOf(false);
                } else if (isLast.booleanValue()) {
                    mHandler.removeMessages(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
                    if (playIndex > 0) {
                        Numbermax = 0;

                        playIndex = playIndex - 1;
                        collectionTime = 0;
                        xjposition = playIndex;
                        mLastPos = 0;
//                        mediaHandler.sendEmptyMessage(WindowMessageID.PREPARE_VOD_DATA);
                        mediaHandler.sendEmptyMessage(WindowMessageID.SHOW_TV);
//                        mediaHandler.sendEmptyMessage(WindowMessageID.PROGRESSBAR_PROGRESS_RESET);
                    }
                    isLast = Boolean.valueOf(false);
                } else if (isPause.booleanValue()) {
                    isPause = Boolean.valueOf(false);
                } else if (!isDestroy.booleanValue()) {
                    mHandler.sendEmptyMessage(WindowMessageID.HIDE_MENU);
                    if (videoInfo.size() > playIndex + 1) {
                        Numbermax = 0;

                        playIndex = playIndex + 1;
                        xjposition = playIndex;
                        collectionTime = 0;
                        mLastPos = 0;
                        PlayersNumber = 0;

                        currentPosition = 0;
                        seizing = 0;
                        SelecteVod(playIndex);
                        iVV.start();
//                        mediaHandler.sendEmptyMessage(WindowMessageID.PREPARE_VOD_DATA);
                        mediaHandler.sendEmptyMessage(WindowMessageID.SHOW_TV);
//                        mediaHandler.sendEmptyMessage(WindowMessageID.PROGRESSBAR_PROGRESS_RESET);
                        Utils.showToast(VideoPlayerActivity.this, getString(R.string.Coming_soon) + vodname + "-" + ((VideoInfo) videoInfo.get(playIndex)).title, R.drawable.toast_smile);
                        return;
                    }
                    iVV.stopPlayback();
                    finish();
                }
            }
        });

        /*设置信息监听*/
        iVV.setOnInfoListener(new OnInfoListener() {
            public boolean onInfo(IMediaPlayer arg0, int what, int extra) {
                //Log.v(TAG, "setOnInfoListener" + what + "--" + extra);
                switch (what) {
                    /*开始缓冲*/
                    case IjkMediaPlayer.MEDIA_INFO_BUFFERING_START:/*701*/
                        /*显示缓冲转圈*/
//                        mediaHandler.sendEmptyMessage(WindowMessageID.SHOW_TV);
//                        mHandler.removeMessages(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
                        break;
                    case IjkMediaPlayer.MEDIA_INFO_BUFFERING_END:/*702*/
                        /*不显示缓冲转圈*/
//                        mediaHandler.sendEmptyMessage(WindowMessageID.COLSE_SHOW_TV);
//                        mHandler.sendEmptyMessage(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
                        break;
                }
                return true;
            }
        });

    }

    /*获取屏幕大小*/
    private void getScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        screenHeight = display.getHeight();
        screenWidth = display.getWidth();
        controlHeight = screenHeight / 4;
    }

    /*显示控制器*/
    private void showController() {
        if (iVV != null) {
            tv_time.setText(Utils.getStringTime(":"));
            time_controler.setAnimationStyle(R.style.AnimationTimeFade);
            time_controler.showAtLocation(iVV, Gravity.TOP, 0, 0);
            controler.setAnimationStyle(R.style.AnimationFade);
            controler.showAtLocation(iVV, Gravity.BOTTOM, 0, 0);
            time_controler.update(0, 0, screenWidth, controlHeight / 3);
            controler.update(0, 0, screenWidth, controlHeight / 2);
            isControllerShow = true;
            mHandler.sendEmptyMessageDelayed(WindowMessageID.HIDE_CONTROLER, TIME);
        }
    }

    /*隐藏控制器*/
    private void hideController() {
        if (controler != null && controler.isShowing()) {
            controler.dismiss();
            time_controler.dismiss();
            isControllerShow = false;
        }
    }

    /*取消延迟隐藏*/
    private void cancelDelayHide() {
        mHandler.removeMessages(WindowMessageID.HIDE_CONTROLER);
    }

    /*隐藏控制器延迟*/
    private void hideControllerDelay() {
        cancelDelayHide();
        mHandler.sendEmptyMessageDelayed(WindowMessageID.HIDE_CONTROLER, TIME);
    }

    /*显示菜单*/
    private void showMenu() {
        if (menupopupWindow != null) {
            vmAdapter = new VodMenuAdapter(this, VideoPlayUtils.getData(0), 8, Boolean.valueOf(isMenuItemShow));
            menulist.setAdapter(vmAdapter);
            menupopupWindow.setAnimationStyle(R.style.AnimationMenu);
            menupopupWindow.showAtLocation(iVV, Gravity.TOP | Gravity.RIGHT, 0, 0);
            menupopupWindow.update(0, 0, getResources().getDimensionPixelSize(R.dimen.sm_350), screenHeight);
            isMenuShow = true;
            isMenuItemShow = false;
            return;
        }
        Utils.showToast(this, R.string.incomplete, R.drawable.toast_shut);
    }

    /*隐藏菜单*/
    private void hideMenu() {
        if (Navigation_mode == 1){
            Navigation();
        }
        if (menupopupWindow != null && menupopupWindow.isShowing()) {
            menupopupWindow.dismiss();
        }
    }

    /*窗口消息处理函数*/
    /*msg 窗口消息*/
    private void onMessage(Message msg) {
        if (msg != null) {
            switch (msg.what) {
                case WindowMessageID.ERROR:
                    if (ClientID < MaxClientID){
                        setVideoUrl(Client + Integer.toString(ClientID + 1 ));
                    }else{
                        if (Auto_Source == 1){
                            /*换源*/
                            Switchsource(1);
                        }else{
                            /*关闭菊花*/
                            //mediaHandler.sendEmptyMessage(WindowMessageID.COLSE_SHOW_TV);
                            Switchsource(3);
                        }
                    }
                    return;
                case WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION:
                    int currPosition = iVV.getCurrentPosition();
                    videoLength = iVV.getDuration();
                    updateTextViewWithTimeFormat(tv_currentTime, currPosition / 1000);
                    updateTextViewWithTimeFormat(tv_totalTime, videoLength / 1000);
                    seekBar.setMax(videoLength / 1000);
                    seekBar.setProgress(currPosition / 1000);
                    mLastPos = currPosition;
                    mHandler.sendEmptyMessageDelayed(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION, 200);
                    return;
                case WindowMessageID.HIDE_CONTROLER:
                    hideController();
                    return;
                case WindowMessageID.HIDE_PROGRESS_TIME:
                    tv_progress_time.setVisibility(View.GONE);
                    return;
                case WindowMessageID.HIDE_MENU:
                    hideMenu();
                    return;
                case WindowMessageID.PLAY_ERROR:/*上报*/
                    if (ClientID < MaxClientID){
                        setVideoUrl(Client + Integer.toString(ClientID + 1 ));
                    }else{
                        if (Auto_Source == 1){
                            /*换源*/
                            Switchsource(1);
                        }else{
                            mediaHandler.sendEmptyMessage(WindowMessageID.COLSE_SHOW_TV);
                            Switchsource(3);
                        }
                    }
                    return;
                case WindowMessageID.SWITCH_CODE:
                    switchCode();
                    isSwitch = false;
                    return;
                default:
                    return;
            }
        }
    }

    /*反馈对话框*/
    private void showUpdateDialog(String str, Context context) {
        if (isDialogShowing) {
            return; // 如果对话框已经显示，则直接返回，避免重复显示
        }
        HomeDialog.Builder builder = new HomeDialog.Builder(context);
        builder.setTitle(R.string.Tips);
        builder.setMessage(str);
        builder.setPositiveButton(R.string.continues, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (videoInfo.size() > playIndex + 1) {
                    //playIndex++;
                    Numbermax = 0;

                    playIndex = playIndex + 1;
                    xjposition = playIndex;
                    PlayersNumber = 0;

                    currentPosition = 0;
                    seizing = 0;
                    SelecteVod(playIndex);
                    collectionTime = 0;
                    mLastPos = 0;
//                    mediaHandler.sendEmptyMessage(WindowMessageID.PREPARE_VOD_DATA);
                    mediaHandler.sendEmptyMessage(WindowMessageID.SHOW_TV);
//                    mediaHandler.sendEmptyMessage(WindowMessageID.PROGRESSBAR_PROGRESS_RESET);
                } else {
                    iVV.stopPlayback();
                    finish();
                }
                dialog.dismiss();
                isDialogShowing = false; // 对话框关闭后将标志变量设置为false
            }
        });
        builder.setNeutralButton(R.string.forget_it, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
                isDialogShowing = false; // 对话框关闭后将标志变量设置为false
            }
        });
        builder.create().show();
        isDialogShowing = true; // 对话框显示时将标志变量设置为true
        isCodeBlockExecuted = false; // 标记代码块已经执行过
        /*进行上报*/
        getFailreport(vodname,((VideoInfo) videoInfo.get(playIndex)).title,domain);
    }

    /**
     * 进行上报
     *
     * @param vodname 剧集名称
     *
     * @param episode 剧集数量
     *
     * @param line 剧集线路
     */
    private void getFailreport(String vodname,String episode,String line){
        /*加载get访问*/
        mQueue = Volley.newRequestQueue(this, new HurlStack());
        final String account = sp.getString("userName", "");
        final String name = vodname;
        final String vodepisode = episode;
        final String vodline = line;
        String Fb_url = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.ot, ""),Constant.d);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Fb_url,
                new com.android.volley.Response.Listener<String>() {
                    public void onResponse(String response) {
                        /*不回成功调结果*/
                    }
                }, new com.android.volley.Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                /*不回调失败结果*/
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // 添加您的请求参数
                params.put("account", account);//上报帐号
                params.put("line", vodline);//上报线路名称
                params.put("url", Fburl);//上报播放地址
                //params.put("episode", vodepisode);//上报第几集
                params.put("vodname", name + "-" + vodepisode);//上报名字
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(VideoPlayerActivity.this, "Authorization", ""),Constant.d));  /*设置其他请求头*/
                return headers;
            }
        };
        mQueue.add(stringRequest);

    }

    /*使用时间格式更新文本视图*/
    private void updateTextViewWithTimeFormat(TextView view, int second) {
        int mm = (second % 3600) / 60;
        int ss = second % 60;
        view.setText(String.format("%02d:%02d:%02d", Integer.valueOf(second / 3600), Integer.valueOf(mm), Integer.valueOf(ss)));
    }

    /*调度键盘事件*/
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            if (isBack) {
                hideControllerDelay();
                iVV.seekTo(mLastPos);
                mHandler.sendEmptyMessage(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
                isBack = false;
            }
            return super.dispatchKeyEvent(event);
        }
        int keyCode = event.getKeyCode();
        long secondTime;
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:/*返回键*/
                hideController();
                secondTime = System.currentTimeMillis();
                if (secondTime - firstTime <= 2000) {
                    collectionTime = iVV.getCurrentPosition();
                    if (collectionTime != 0) {
                        Album album = new Album();
                        album.setAlbumId(videoId);
                        album.setAlbumSourceType(sourceId);
                        album.setCollectionTime(collectionTime);
                        album.setPlayIndex(playIndex);
                        album.setAlbumPic(albumPic);
                        album.setAlbumType(vodtype);
                        album.setAlbumTitle(vodname);
                        //album.setAlbumState(vodstate);
                        //album.setAlbumState("观看:" + collectionTime / 1000 + "秒");
                        album.setAlbumState("观看:" + collectionTime / 1000 / 60 + "分钟");
                        album.setNextLink(nextlink);
                        album.setTime(String.valueOf(secondTime));
                        album.setTypeId(2);
                        dao.addAlbums(album);
//                        Logger.d("joychang", "退出时间点=" + collectionTime + "...videoId==" + videoId);
//                        Logger.v(TAG, "存入数据库=+videoId" + videoId + "---sourceId=" + getString(Integer.parseInt(sourceId)) + "----collectionTime=" + collectionTime);
                    }
                    isDestroy = Boolean.valueOf(true);
                    stopPlayback();
                    finish();
                    break;
                }
                Utils.showToast(this, R.string.onbackpressed, R.drawable.toast_err);
                firstTime = secondTime;
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:/*上键*/
                if (playPreCode != 0) {
                    mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FX_FOCUS_NAVIGATION_UP);
                    break;
                }

                secondTime = System.currentTimeMillis();
                if (secondTime - firstTime <= 2000) {
                    if (playIndex <= 0) {
                        Utils.showToast(this, R.string.vod_onpressed_play_frist, R.drawable.toast_shut);
                        break;
                    }
                    if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
                        playIndex--;
                        Numbermax = 0;

                        xjposition = playIndex;
                        PlayersNumber = 0;

                        currentPosition = 0;
                        seizing = 0;
                        SelecteVod(playIndex);
//                        mediaHandler.sendEmptyMessage(WindowMessageID.PREPARE_VOD_DATA);
                        mediaHandler.sendEmptyMessage(WindowMessageID.SHOW_TV);
//                        mediaHandler.sendEmptyMessage(WindowMessageID.PROGRESSBAR_PROGRESS_RESET);
                    }
                    if (!isControllerShow) {
                        showController();
                    }
                    mediaHandler.sendEmptyMessage(WindowMessageID.RESET_MOVIE_TIME);
                    break;
                }
                Utils.showToast(this, R.string.vod_onpressed_play_last, R.drawable.toast_smile);
                firstTime = secondTime;
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:/*下键*/
                if (playPreCode != 0) {
                    mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FX_FOCUS_NAVIGATION_UP);
                    break;
                }
                secondTime = System.currentTimeMillis();
                if (secondTime - firstTime <= 5000) {
                    if (videoInfo.size() <= playIndex + 1) {
                        Utils.showToast(this, R.string.finale, R.drawable.toast_shut);
                        break;
                    }
                    if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
                        //playIndex++;
                        Numbermax = 0;

                        playIndex = playIndex + 1;
                        xjposition = playIndex;
                        PlayersNumber = 0;

                        currentPosition = 0;
                        seizing = 0;
                        SelecteVod(playIndex);
//                        mediaHandler.sendEmptyMessage(WindowMessageID.PREPARE_VOD_DATA);
                        mediaHandler.sendEmptyMessage(WindowMessageID.SHOW_TV);
//                        mediaHandler.sendEmptyMessage(WindowMessageID.PROGRESSBAR_PROGRESS_RESET);
                    }
                    if (!isControllerShow) {
                        showController();
                    }
                    mediaHandler.sendEmptyMessage(WindowMessageID.RESET_MOVIE_TIME);
                    break;
                }
                Utils.showToast(this, R.string.vod_onpressed_play_next, R.drawable.toast_smile);
                firstTime = secondTime;
                return true;
            case WindowMessageID.HIDE_PROGRESS_TIME:
                if (!isControllerShow) {
                    showController();
                }
                rewind();
                break;
            case WindowMessageID.HIDE_MENU:
                if (!isControllerShow) {
                    showController();
                }
                fastForward();
                break;
            case WindowMessageID.START_SPEED:
                if (!isControllerShow) {
                    showController();
                }
                if (!iVV.isPlaying()) {
                    iVV.start();
                    mHandler.sendEmptyMessage(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
                    ib_playStatus.setImageResource(R.drawable.media_pause);
                    break;
                }
                iVV.pause();
                mHandler.removeMessages(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
                ib_playStatus.setImageResource(R.drawable.media_playstatus);
                break;
            case IjkMediaMeta.FF_PROFILE_H264_BASELINE:
                if (!isControllerShow) {
                    showController();
                }
                if (!iVV.isPlaying()) {
                    iVV.start();//暂停播放
                    mHandler.sendEmptyMessage(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
                    ib_playStatus.setImageResource(R.drawable.media_pause);
                    break;
                }
                iVV.pause();//继续播放
                mHandler.removeMessages(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
                ib_playStatus.setImageResource(R.drawable.media_playstatus);
                break;
            case KeyEvent.KEYCODE_MENU:
                hideController();
                showMenu();
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    /*快进*/
    private void fastForward() {
        if ((videoLength / 1000) - (mLastPos / 1000) > 30) {
            mLastPos += 30000;
        } else {
            /*快进最多比视频总时常少10秒*/
            mLastPos = videoLength - 10000;
        }
        isBack = true;
        cancelDelayHide();
        mHandler.removeMessages(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
        seekBar.setProgress(mLastPos / 1000);
    }

    /*快退*/
    private void rewind() {
        if (mLastPos > 30) {
            mLastPos -= 30000;
        } else {
            mLastPos = 0;
        }
        isBack = true;
        cancelDelayHide();
        mHandler.removeMessages(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
        seekBar.setProgress(mLastPos / 1000);
    }

    /*重置电影时间*/
    private void ResetMovieTime() {
        updateTextViewWithTimeFormat(tv_currentTime, 0);
        updateTextViewWithTimeFormat(tv_totalTime, 0);
        seekBar.setProgress(0);
    }

    /*菜单信息*/
    public void onCreateMenu() {
        View menuView = View.inflate(this, R.layout.mv_controler_menu, null);
        menulist = menuView.findViewById(R.id.media_controler_menu);
        menupopupWindow = new PopupWindow(menuView, -2, -2);
        menupopupWindow.setOutsideTouchable(true);
        menupopupWindow.setTouchable(true);
        menupopupWindow.setFocusable(true);
        menulist.setOnItemClickListener(new OnItemClickListener() {
            @SuppressLint("WrongConstant")
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (isMenuShow) {
                    isMenuShow = false;
                    isMenuItemShow = true;
                    switch (position) {
                        case 0:
                            /*选集*/
                            menutype = 0;
                            menulist.setAdapter(new VodMenuAdapter(VideoPlayerActivity.this, videoInfo, 0, Boolean.valueOf(isMenuItemShow)));
                            menulist.setSelection(xjposition);
                            return;
                        case 1:
                            /*倍速*/
                            menutype = 1;
                            menulist.setAdapter(new VodMenuAdapter(VideoPlayerActivity.this, VideoPlayUtils.getData(1), 1, Boolean.valueOf(isMenuItemShow)));
                            menulist.setSelection(bsposition);
                            return;
                        case 2:
                            /*解码*/
                            menutype = 2;
                            menulist.setAdapter(new VodMenuAdapter(VideoPlayerActivity.this, VideoPlayUtils.getData(2), 2, Boolean.valueOf(isMenuItemShow)));
                            menulist.setSelection(jmposition);
                            return;
                        case 3:
                            /*比例*/
                            menutype = 3;
                            menulist.setAdapter(new VodMenuAdapter(VideoPlayerActivity.this, VideoPlayUtils.getData(3), 3, Boolean.valueOf(isMenuItemShow)));
                            menulist.setSelection(hmblposition);
                            return;
                        case 4:
                            /*偏好*/
                            menutype = 4;
                            menulist.setAdapter(new VodMenuAdapter(VideoPlayerActivity.this, VideoPlayUtils.getData(4), 4, Boolean.valueOf(isMenuItemShow)));
                            menulist.setSelection(phszposition);
                            return;
                        case 5:
                            /*片头*/
                            menutype = 5;
                            menulist.setAdapter(new VodMenuAdapter(VideoPlayerActivity.this, VideoPlayUtils.getData(5), 5, Boolean.valueOf(isMenuItemShow)));
                            menulist.setSelection(ptposition);
                            return;
                        case 6:
                            /*片尾*/
                            menutype = 6;
                            menulist.setAdapter(new VodMenuAdapter(VideoPlayerActivity.this, VideoPlayUtils.getData(6), 6, Boolean.valueOf(isMenuItemShow)));
                            menulist.setSelection(pwposition);
                            return;
                        case 7:
                            /*内核*/
                            menutype = 7;
                            menulist.setAdapter(new VodMenuAdapter(VideoPlayerActivity.this, VideoPlayUtils.getData(7), 7, Boolean.valueOf(isMenuItemShow)));
                            menulist.setSelection(nhposition);
                            return;
                        default:
                            return;
                    }
                } else if (isMenuItemShow) {
                    Editor editor;
                    switch (menutype) {
                        case 0:
                            /*选集*/
                            if (videoInfo.size() > position) {
                                isNext = Boolean.valueOf(true);
                                isSwitch = true;
                                Numbermax = 0;

                                playIndex = position;
                                PlayersNumber = 0;

                                currentPosition = 0;
                                seizing = 0;
                                if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
                                    SelecteVod(playIndex);
                                    //System.out.println("position等于" + playIndex);
//                                    mediaHandler.sendEmptyMessage(WindowMessageID.PREPARE_VOD_DATA);
                                    mediaHandler.sendEmptyMessage(WindowMessageID.SHOW_TV);
//                                    mediaHandler.sendEmptyMessage(WindowMessageID.PROGRESSBAR_PROGRESS_RESET);
                                }
                                if (!isControllerShow) {
                                    showController();
                                }
                                mediaHandler.sendEmptyMessage(WindowMessageID.RESET_MOVIE_TIME);
                            }
                            xjposition = position;
                            hideMenu();
                            return;
                        case 1:
                            /*倍速*/
                            bsposition = position;
                            switch(position)
                            {
                                case 0:
                                    setSpeed(0.5f,0);
                                    break;
                                case 1:
                                    setSpeed(0.75f,0);
                                    break;
                                case 2:
                                    setSpeed(1.0f,0);
                                    break;
                                case 3:
                                    setSpeed(1.25f,0);
                                    break;
                                case 4:
                                    setSpeed(1.5f,0);
                                    break;
                                case 5:
                                    setSpeed(2.0f,0);
                                    break;
                                case 6:
                                    setSpeed(3.0f,0);
                                    break;
                                case 7:
                                    setSpeed(4.0f,0);
                                    break;
                                case 8:
                                    setSpeed(5.0f,0);
                                    break;
                            }
                            hideMenu();
                            return;
                        case 2:
                            /*解码*/
                            jmposition = position;
                            if (position == 0) {
                                editor = sp.edit();
                                editor.putInt("mIsHwDecode", 0);
                                editor.putString(Constant.mg, "软解码");
                                editor.commit();
                            } else if (position == 1) {
                                editor = sp.edit();
                                editor.putInt("mIsHwDecode", 1);
                                editor.putString(Constant.mg, "硬解码");
                                editor.commit();
                            }
                            setDecode();
                            if (position == 1) {
                                iVV.setDecode(Boolean.valueOf(true));
                            } else if (position == 0) {
                                iVV.setDecode(Boolean.valueOf(false));
                            }
                            isPause = Boolean.valueOf(true);
                            if (iVV.isPlaying()) {
                                mLastPos = iVV.getCurrentPosition();
                            }
                            if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
                                mediaHandler.sendEmptyMessage(WindowMessageID.SHOW_TV);
                                iVV.resume();
                                iVV.seekTo(mLastPos);
                            }
                            hideMenu();
                            return;
                        case 3:
                            /*画面比例*/
                            hmblposition = position;
                            selectScales(hmblposition);
                            editor = sp.edit();
                            if (position == 0) {
                                editor.putString(Constant.oh, "原始比例");
                                editor.commit();
                            } else if (position == 1) {
                                editor.putString(Constant.oh, "4:3 缩放");
                                editor.commit();
                            } else if (position == 2) {
                                editor.putString(Constant.oh, "16:9缩放");
                                editor.commit();
                            } else if (position == 3) {
                                editor.putString(Constant.oh, "全屏拉伸");
                                editor.commit();
                            }else if (position == 4) {
                                editor.putString(Constant.oh, "等比缩放");
                                editor.commit();
                            }else if (position == 5) {
                                editor.putString(Constant.oh, "全屏裁剪");
                                editor.commit();
                            }
                            hideMenu();
                            return;
                        case 4:
                            /*偏好设置*/
                            phszposition = position;
                            editor = sp.edit();
                            if (position == 0) {
                                editor.putInt("playPre", 0);
                                editor.commit();
                            } else if (position == 1) {
                                editor.putInt("playPre", 1);
                                editor.commit();
                            }
                            getPlayPreferences();
                            hideMenu();
                            return;
                        case 5:
                            /*跳过片头*/
                            String[] jumpTimes = {"0秒", "10秒", "15秒", "20秒", "30秒", "60秒", "90秒", "120秒", "150秒", "180秒", "240秒", "300秒"};
                            int[] jumpValues = {0, 10, 15, 20, 30, 60, 90, 120, 150, 180, 240, 300};
                            ptposition = position;
                            editor = sp.edit();
                            editor.putString("play_jump", jumpTimes[position]);
                            editor.commit();
                            setJump(jumpValues[position]);
                            hideMenu();
                            return;
                        case 6:
                            //跳过片尾
                            String[] jumpEndStrings = {"0秒", "10秒", "15秒", "20秒", "30秒", "60秒", "90秒", "120秒", "150秒", "180秒", "240秒", "300秒"};
                            int[] jumpEndValues = {0, 10, 15, 20, 30, 60, 90, 120, 150, 180, 240, 300};
                            pwposition = position;
                            editor = sp.edit();
                            editor.putString("play_jump_end", jumpEndStrings[position]);
                            editor.commit();
                            setJump_end(jumpEndValues[position]);
                            hideMenu();
                            return;
                        case 7:
                            //内核
                            nhposition = position;
                            if (position == 0) {
                                editor = sp.edit();
                                editor.putString(Constant.hd, "自动");
                                editor.commit();
                            } else if (position == 1) {
                                editor = sp.edit();
                                editor.putString(Constant.hd, "系统");
                                editor.commit();
                            } else if (position == 2) {
                                editor = sp.edit();
                                editor.putString(Constant.hd, "IJK");
                                editor.commit();
                            } else if (position == 3) {
                                editor = sp.edit();
                                editor.putString(Constant.hd, "EXO");
                                editor.commit();
                            } else if (position == 4) {
                                editor = sp.edit();
                                editor.putString(Constant.hd, "阿里");
                                editor.commit();
                            }
                            if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
                                SelecteVod(playIndex);
                                mediaHandler.sendEmptyMessage(WindowMessageID.SHOW_TV);
                            }
//                          iVV.togglePlayer();
                            hideMenu();
                            return;
                        default:
                            return;
                    }
                }
            }
        });
        menulist.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 1) {
                    switch (keyCode) {
                        case 4:
                            if (!isMenuItemShow) {
                                if (isMenuShow) {
                                    menupopupWindow.dismiss();
                                    break;
                                }
                            }
                            isMenuShow = true;
                            isMenuItemShow = false;
                            menulist.setAdapter(new VodMenuAdapter(VideoPlayerActivity.this, VideoPlayUtils.getData(0), 8, Boolean.valueOf(isMenuItemShow)));
                            break;
                    }
                }
                return false;
            }
        });
    }

    /*分辨率切换*/
    private void selectScales(int paramInt) {
        if (iVV != null) {
            /*倍速记忆*/
            if (bsposition == 0){
                setSpeed(0.5f,0);
            }
            if (bsposition == 1){
                setSpeed(0.75f,0);
            }
            if (bsposition == 2){
                setSpeed(1.0f,0);
            }
            if (bsposition == 3){
                setSpeed(1.25f,0);
            }
            if (bsposition == 4){
                setSpeed(1.5f,0);
            }
            if (bsposition == 5){
                setSpeed(2.0f,0);
            }
            if (bsposition == 6){
                setSpeed(2.0f,0);
            }
            if (bsposition == 7){
                setSpeed(4.0f,0);
            }
            if (bsposition == 8){
                setSpeed(5.0f,0);
            }

            /*比例切换*/
            switch (paramInt) {
                case 0:
                    /*原始比例*/
                    if (Adblock == 1){
                        if (Ad_block == 1){//广告遮挡开启后强制拉到等比缩放
                            iVV.toggleAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
                            return;
                        }
                    }
                    iVV.toggleAspectRatio(IRenderView.AR_ASPECT_WRAP_CONTENT);
                    break;
                case 1:
                    /*4:3比例*/
                    if (Adblock == 1){
                        if (Ad_block == 1){//广告遮挡开启后强制拉到等比缩放
                            if (position == 3||position == 4||position == 5||position == 6){
                                iVV.toggleAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
                                return;
                            }
                        }
                    }
                    iVV.toggleAspectRatio(IRenderView.AR_4_3_FIT_PARENT);
                    break;
                case 2:
                    /*16:9比例*/
                    if (Adblock == 1){
                        if (Ad_block == 1){//广告遮挡开启后强制拉到等比缩放
                            if (position == 3||position == 4||position == 5||position == 6){
                                iVV.toggleAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
                                return;
                            }
                        }
                    }
                    iVV.toggleAspectRatio(IRenderView.AR_16_9_FIT_PARENT);
                    break;
                case 3:
                    /*全屏拉伸*/
                    if (Adblock == 1){
                        if (Ad_block == 1){//广告遮挡开启后强制拉到等比缩放
                            if (position == 3||position == 4||position == 5||position == 6){
                                iVV.toggleAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
                                return;
                            }
                        }
                    }
                    iVV.toggleAspectRatio(IRenderView.AR_MATCH_PARENT);
                    break;
                case 4:
                    /*等比缩放*/
                    iVV.toggleAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
                    break;
                case 5:
                    /*全屏裁剪*/
                    if (Adblock == 1){
                        if (Ad_block == 1){//广告遮挡开启后强制拉到等比缩放
                            if (position == 3||position == 4||position == 5||position == 6){
                                iVV.toggleAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
                                return;
                            }
                        }
                    }
                    iVV.toggleAspectRatio(IRenderView.AR_ASPECT_FILL_PARENT);
                    break;
            }
        }
    }

    /*触摸式事件*/
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        boolean result = mGestureDetector.onTouchEvent(event);
        DisplayMetrics screen = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(screen);
        if (mSurfaceYDisplayRange == 0)
            mSurfaceYDisplayRange = Math.min(screen.widthPixels,
                    screen.heightPixels);
        float y_changed = event.getRawY() - mTouchY;
        float x_changed = event.getRawX() - mTouchX;

        float coef = Math.abs(y_changed / x_changed);
        float xgesturesize = ((x_changed / screen.xdpi) * 2.54f);
        Logger.i("joychang", "y_changed="+y_changed+"...x_changed="+x_changed+"...coef="+coef+"...xgesturesize="+xgesturesize);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Logger.i(TAG, "MotionEvent.ACTION_DOWN.......");
                boolean isSeekTouch = true;
                mTouchAction = TOUCH_NONE;
                mTouchY = event.getRawY();
                mTouchX = event.getRawX();
                maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                Lightness = Utils.GetLightness(VideoPlayerActivity.this);
                break;
            case MotionEvent.ACTION_MOVE:
                Logger.i(TAG, "MotionEvent.ACTION_MOVE.......");
                if(coef > 2){
                    isSeekTouch = false;
                    /*音量和亮度*/
                    if(mTouchX > (screenWidth / 2)){
                        /*音量*/
                        doVolumeTouch(y_changed);
                    }
                    if (mTouchX < (screenWidth / 2)) {
                        /*亮度*/
                        doBrightnessTouch(y_changed);
                    }
                }
                doSeekTouch(coef, xgesturesize,false);
                break;
            case MotionEvent.ACTION_UP:
                if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE){
                    switch (bsposition) {
                        case 0:
                            setSpeed(0.5f,1);
                            break;
                        case 1:
                            setSpeed(0.75f,1);
                            break;
                        case 2:
                            setSpeed(1.0f,1);
                            break;
                        case 3:
                            setSpeed(1.25f,1);
                            break;
                        case 4:
                            setSpeed(1.5f,1);
                            break;
                        case 5:
                            setSpeed(2.0f,1);
                            break;
                        case 6:
                            setSpeed(3.0f,1);
                            break;
                        case 7:
                            setSpeed(4.0f,1);
                            break;
                        case 8:
                            setSpeed(5.0f,1);
                            break;
                    }
                }
                Logger.i(TAG, "MotionEvent.ACTION_UP.......");
                doSeekTouch(coef, xgesturesize, true);
                break;
        }
        return true;
    }

    /*调节音量*/
    private void doVolumeTouch(float y_changed){
        if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_VOLUME)
            return;
        mTouchAction = TOUCH_VOLUME;
        int delta = -(int) ((y_changed / mSurfaceYDisplayRange) * maxVolume);
        int vol = (int) Math.min(Math.max(currentVolume + delta, 0), maxVolume);
        Logger.d("doVolumeTouch", "vol===="+vol+"...delta="+delta);
        if (delta != 0) {
            if(vol < 1 ){
                showVolumeToast(R.drawable.mv_ic_volume_mute, maxVolume, vol,true);
            }else if(vol >= 1 && vol < maxVolume / 2){
                showVolumeToast(R.drawable.mv_ic_volume_low, maxVolume, vol,true);
            }else if(vol >= maxVolume / 2){
                showVolumeToast(R.drawable.mv_ic_volume_high, maxVolume, vol,true);
            }
        }
    }

    /*调节亮度*/
    private void doBrightnessTouch(float y_changed){
        if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_BRIGHTNESS)
            return;
        mTouchAction = TOUCH_BRIGHTNESS;
        float delta = -y_changed / mSurfaceYDisplayRange * 2f;
        int vol = (int) ((Math.min(Math.max(Lightness + delta, 0.01f)*255, 255)));
        if (delta != 0) {
            if(vol<5){
                showVolumeToast(R.drawable.mv_ic_brightness, 255, 0,false);
            }else{
                showVolumeToast(R.drawable.mv_ic_brightness, 255,vol,false);
            }
            Logger.d("doBrightnessTouch", "Lightness="+Lightness+"....vol="+vol+"...delta="+delta+"....mSurfaceYDisplayRange="+mSurfaceYDisplayRange);
        }
    }

    /*左右触摸事件*/
    private void doSeekTouch(float coef, float gesturesize, boolean seek) {
        if (((double) coef) <= 0.5d && Math.abs(gesturesize) >= 1.0f) {
            if (mTouchAction == TOUCH_NONE || mTouchAction == TOUCH_SEEK) {
                mTouchAction = TOUCH_SEEK;
                int time = iVV.getCurrentPosition() / 1000;
                int jump = (int) ((((double) Math.signum(gesturesize)) * ((600000.0d * Math.pow(gesturesize / 8.0f, 4.0d)) + 3000.0d)) / 1000.0d);
                Logger.d("doSeekTouch", "jump=" + jump);
                if (jump > 0 && time + jump > videoLength) {
                    jump = videoLength - time;
                }
                if (jump < 0 && time + jump < 0) {
                    jump = -time;
                }
                if (videoLength > 0) {
                    tv_progress_time.setVisibility(View.VISIBLE);
                    updateTextViewWithTimeFormat(tv_progress_time, time + jump);
                    mHandler.removeMessages(WindowMessageID.HIDE_PROGRESS_TIME);
                    mHandler.sendEmptyMessageDelayed(WindowMessageID.HIDE_PROGRESS_TIME, 2000);
                    /*滑动快进*/
                    if (seek) {
                        /*滑动超过视频时间*/
                        if ((time + jump) * 1000 > videoLength){
                            if(videoInfo.size() > playIndex + 1){
                                Numbermax = 0;

                                playIndex = playIndex + 1;
                                xjposition = playIndex;
                                collectionTime = 0;
                                mLastPos = 0;
                                PlayersNumber = 0;

                                currentPosition = 0;
                                seizing = 0;
                                SelecteVod(playIndex);
//                                mediaHandler.sendEmptyMessage(WindowMessageID.PREPARE_VOD_DATA);
                                mediaHandler.sendEmptyMessage(WindowMessageID.SHOW_TV);
//                                mediaHandler.sendEmptyMessage(WindowMessageID.PROGRESSBAR_PROGRESS_RESET);
                                iVV.start();
                            }else {
                                Utils.showToast(VideoPlayerActivity.this, "已是最后一集了!", R.drawable.toast_smile);
                            }
                        }else {
                            iVV.seekTo((time + jump) * 1000);
                            mHandler.sendEmptyMessage(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
                        }
                    }
                }
            }
        }
    }

    /*单击事件*/
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.ib_playStatus:
                if(!isControllerShow)
                    showController();
                if (iVV.isPlaying()) {
                    iVV.pause();//暂停播放
                    mHandler.removeMessages(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
                    ib_playStatus.setImageResource(R.drawable.media_playstatus);
                } else {
                    iVV.start();//继续播放
                    mHandler.sendEmptyMessage(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
                    ib_playStatus.setImageResource(R.drawable.media_pause);
                }
                break;
        }
    }

    /*显示音量吐司*/
    private void showVolumeToast(int resId, int max, int current, Boolean isVolume) {
        if (isVolume.booleanValue()) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
        } else {
            Utils.SetLightness(this, current);
        }
        View view;
        ImageView center_image;
        ProgressBar center_progress;
        if (mToast == null) {
            mToast = new Toast(this);
            view = LayoutInflater.from(this).inflate(R.layout.mv_media_volume_controler, null);
            center_image = view.findViewById(R.id.center_image);
            center_progress = view.findViewById(R.id.center_progress);
            center_progress.setMax(max);
            center_progress.setProgress(current);
            center_image.setImageResource(resId);
            mToast.setView(view);
        } else {
            view = mToast.getView();
            center_image = view.findViewById(R.id.center_image);
            center_progress = view.findViewById(R.id.center_progress);
            center_progress.setMax(max);
            center_progress.setProgress(current);
            center_image.setImageResource(resId);
        }
        mToast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL,0, 0);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    /*显示网速*/
    private void startSpeed() {
        mSpeedHandler.removeCallbacks(speedRunnable);
        lastRxByte = TrafficStats.getTotalRxBytes();
        lastSpeedTime = System.currentTimeMillis();
        mSpeedHandler.postDelayed(speedRunnable, 0);
        tv_mv_speed.setVisibility(View.VISIBLE);
    }

    /*结束网速*/
    private void endSpeed() {
        load = 0;
        mSpeedHandler.removeCallbacks(speedRunnable);
        tv_mv_speed.setVisibility(View.GONE);
    }

    /*转换解码方式*/
    private void switchCode() {
        int Decode = sp.getInt("mIsHwDecode", 1);
        if (Decode == 1) {
            mIsHwDecode = true;
            jmposition = 1;
        } else {
            mIsHwDecode = false;
            jmposition = 0;
        }
        if (Decode == 1) {
            iVV.setDecode(Boolean.valueOf(true));
        } else if (Decode == 0) {
            iVV.setDecode(Boolean.valueOf(false));
        }
        isPause = Boolean.valueOf(true);
        iVV.resume();
        xjposition = playIndex;
        collectionTime = 0;
        //mLastPos = 0;
//        mediaHandler.sendEmptyMessage(WindowMessageID.DATA_PREPARE_OK);
        mediaHandler.sendEmptyMessage(WindowMessageID.SHOW_TV);
//        mediaHandler.sendEmptyMessage(WindowMessageID.PROGRESSBAR_PROGRESS_RESET);
    }

    /*获取唤醒锁定*/
    private void acquireWakeLock() {
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,this.getClass().getCanonicalName());
            mWakeLock.acquire();

        }
    }

    /*释放唤醒锁定*/
    private void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    /*播放器状态*/
    private enum PLAYER_STATUS {
        PLAYER_IDLE,//空闲
        PLAYER_PREPARING,//准备
        PLAYER_PREPARED,//准备好
        PLAYER_BACKSTAGE//后台
    }

    /*设置倍速*/
    private void setSpeed(float speed,int source){
        if (source == 1){
            /*长按倍速*/
            iVV.setSpeed(speed);
            if (speed != 1.0){
                return;
            }
        }else{
            /*切换倍速*/
            float Speed;
            if (nhposition == 2 || nhposition == 0){
                if (speed > 2.0f){
                    iVV.setSpeed(2.0f);
                    bsposition = 5;
                    Speed = 2;
                }else{
                    iVV.setSpeed(speed);
                    Speed = speed;
                }
            }else{
                iVV.setSpeed(speed);
                Speed = speed;
            }
            if (speed != 1.0){
                    Utils.showToast(VideoPlayerActivity.this, getString(R.string.have) + Speed + getString(R.string.speed), R.drawable.toast_smile);
                    return;
            }
        }
    }

    /*检查当前视频播放进度*/
    private void updateprogress(int progress){

        /*导航是否显示*/
        if (Navigation_mode == 1){
            Navigation();
        }
        /*视频总时长*/
        int Videoduration = videoLength / 1000;
        /*跳片头时长*/
        int sum1 = Integer.parseInt(jump_time);
        /*跳片尾时长*/
        int sum2 = Integer.parseInt(jump_time_end);
        /*跳片头片尾总时长*/
        int sum3 = sum1 + sum2 + 30;

        /*试看*/
        /*会员过期启用试看模式*/
        if (vipstate == 1){
            /*读取试看时间*/
            if (progress >= Trytime * 60){
                /*停止试看*/
                iVV.stopPlayback();
//                mediaHandler.sendEmptyMessage(WindowMessageID.Try);
                /*延迟0.5秒跳试看*/
                mediaHandler.sendEmptyMessageDelayed(WindowMessageID.Try, 500);
            }
        }

        /*解析失败切换解析续播*/
        if (mLastPos2 > 0){
            iVV.seekTo(mLastPos2);
            mLastPos2 = 0;
        }

        /*验证视跳片头片尾总时长是否大于视频时长*/
        if (Videoduration > sum3) {
            /*跳过片头*/
            if (Videoduration > sum1 && !jump_time.equals("0") && progress < sum1) {
                if (mLastPos/ 1000 != 0) {
                	if (ptpositions) {
                        return; // 如果对话框已经跳过，则直接返回，避免重复显示
                    }
                    ptpositions = true; // 对话框显示时将标志变量设置为true
	                Utils.showToast(VideoPlayerActivity.this, vodname + "-" + videoInfo.get(playIndex).title + getString(R.string.titles) + jump_time + getString(R.string.seconds), R.drawable.toast_smile);
	                iVV.seekTo(sum1 * 1000);
                }
            }

            if (Videoduration > sum2 && !jump_time_end.equals("0") && progress > Videoduration - sum2) {
                /*跳过片尾*/
                Utils.showToast(VideoPlayerActivity.this, vodname + "-" + videoInfo.get(playIndex).title + getString(R.string.trailer) + jump_time_end + getString(R.string.seconds), R.drawable.toast_smile);
                if(videoInfo.size() > playIndex + 1){
                    //playIndex++;
                    Numbermax = 0;

                    playIndex = playIndex + 1;
                    xjposition = playIndex;
                    collectionTime = 0;
                    mLastPos = 0;
                    PlayersNumber = 0;

                    currentPosition = 0;
                    seizing = 0;
                    SelecteVod(playIndex);
                    mHandler.sendEmptyMessage(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
//                    mediaHandler.sendEmptyMessage(WindowMessageID.PREPARE_VOD_DATA);
                    mediaHandler.sendEmptyMessage(WindowMessageID.SHOW_TV);
//                    mediaHandler.sendEmptyMessage(WindowMessageID.PROGRESSBAR_PROGRESS_RESET);
                    iVV.start();
                }else{
                    iVV.stopPlayback();
                    finish();
                }
            }
        }
    }

    /*设置跳过片头*/
    private void setJump(int speed){
        if (speed==0){
            Utils.showToast(VideoPlayerActivity.this, R.string.cancellationpt, R.drawable.toast_smile);
            jump_time = Integer.toString(speed);
            return;
        }
        jump_time = Integer.toString(speed);
    }

    /*设置跳过片尾*/
    private void setJump_end(int speed){
        if (speed==0){
            Utils.showToast(VideoPlayerActivity.this, R.string.cancellationpw, R.drawable.toast_smile);
            jump_time_end = Integer.toString(speed);
            return;
        }
        jump_time_end = Integer.toString(speed);
    }

    /*取视频跑马公告*/
    private void getVodGongGao() {
        mQueue = Volley.newRequestQueue(this, new HurlStack());
        String User_url = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.s, ""),Constant.d);
        final String RC4KEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.kd, ""),Constant.d);
        final String RSAKEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.tb, ""),Constant.d);
        final String AESKEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.um, ""),Constant.d);
        final String AESIV = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.im, ""),Constant.d);
        final String Appkey = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.yk, ""),Constant.d);
        int miType = SharePreferenceDataUtil.getSharedIntData(this, Constant.ue, 1);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, User_url + "/api.php?app=" + Api.APPID + "&act=vod_notice",
                new com.android.volley.Response.Listener<String>() {
                    public void onResponse(String response) {
                        VodGongGaoResponse(response);
                    }
                }, new com.android.volley.Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                VodGongGaoError(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String codedata = "t=" + GetTimeStamp.timeStamp();
                String rc4data = null;
                if (miType == 1) {
                    rc4data = Rc4.encry_RC4_string(codedata, RC4KEY);
                } else if (miType == 2) {
                    try {
                        rc4data = Rsa.encrypt_Rsa(codedata, RSAKEY);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (miType == 3) {
                    rc4data = AES.encrypt_Aes(AESKEY,codedata, AESIV);
                }

                String sign = Md5Encoder.encode(new StringBuilder(String.valueOf(codedata)).append("&").append(Appkey).toString());
                Map<String, String> params = new HashMap<>();
                // 添加您的请求参数
                params.put("data", rc4data);
                params.put("sign", sign);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(VideoPlayerActivity.this, "Authorization", ""),Constant.d));  /*设置其他请求头*/
                return headers;
            }
        };
        mQueue.add(stringRequest);

    }
    
    /*视频logo*/
    private void vodlogoloadImg() {
        tv_logo.setVisibility(View.VISIBLE);
        Glide.with(this).load(logo_url).into(tv_logo);
    }

    /*获取成功*/
    public void VodGongGaoResponse(String response) {
        String RC4KEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.kd, ""),Constant.d);
        String RSAKEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.tb, ""),Constant.d);
        String AESKEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.um, ""),Constant.d);
        String AESIV = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.im, ""),Constant.d);
        try {
            JSONObject jSONObject = new JSONObject(response);
            int code = jSONObject.optInt("code");/*状态码*/
            String msg = jSONObject.optString("msg");/*状态信息*/
            if (code == 200){
                tv_notice_root.setVisibility(View.VISIBLE);
                int miType = SharePreferenceDataUtil.getSharedIntData(this, Constant.ue, 1);
                if (miType == 1) {
                    tv_notice.setText(URLDecoder.decode(Rc4.decry_RC4(msg,RC4KEY), "UTF-8"));
                } else if (miType == 2) {
                    tv_notice.setText(URLDecoder.decode(Rsa.decrypt_Rsa(msg,RSAKEY), "UTF-8"));
                } else if (miType == 3) {
                    tv_notice.setText(URLDecoder.decode(AES.decrypt_Aes(AESKEY,msg, AESIV), "UTF-8"));
                }
                mediaHandler.sendEmptyMessage(WindowMessageID.NOTICE);
            }else{
                tv_notice_root.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*获取成失败*/
    public void VodGongGaoError(VolleyError volleyError) {
        //Log.i(TAG, "RequestError: " + volleyError);
        if (volleyError instanceof TimeoutError) {
            tv_notice_root.setVisibility(View.GONE);
//            System.out.println("请求超时");
        }
        if (volleyError instanceof AuthFailureError) {
            tv_notice_root.setVisibility(View.GONE);
            //System.out.println("身份验证失败错误");
        }
        if(volleyError instanceof NetworkError) {
            tv_notice_root.setVisibility(View.GONE);
//            System.out.println("请检查网络");
        }
        if(volleyError instanceof ServerError) {
            tv_notice_root.setVisibility(View.GONE);
            //System.out.println("错误404");
        }

    }

    /*隐藏导航*/
    public void Navigation(){
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    /*内部消息ID定义类*/
    private class WindowMessageID {

        /**
         * @brief 服务请求成功。
         */
        public static final int SUCCESS = 0x00000001;
        /**
         * @brief 服务请求失败。
         */
        public static final int NET_FAILED = 0x00000002;
        /**
         * @brief 请求出错。
         */
        public final static int ERROR = 0x00000003;
        /**
         * @brief 播放。
         */
        public final static int EVENT_PLAY = 0x00000004;

        /**
         * @brief 刷新时间。
         */
        public static final int UI_EVENT_UPDATE_CURRPOSITION = 0x00000005;
        /**
         * @brief 显示进度条。
         */
        public final static int PROGRESS_CHANGED = 0x00000006;
        /**
         * @brief 隐藏进度条。
         */
        public final static int HIDE_CONTROLER = 0x00000007;

        /**
         * @brief 播放数据准备OK。
         */
        public static final int DATA_PREPARE_OK = 0x00000008;
        /**
         * @brief base64后的数据准备OK。
         */
        public static final int DATA_BASE64_PREPARE_OK = 0x00000009;

        /**
         * @brief 准备数据
         */
        public static final int PREPARE_VOD_DATA = 0x000000010;

        public static final int SHOW_TV = 0x00000011;

        public static final int COLSE_SHOW_TV = 0x00000012;

        public static final int PROGRESSBAR_PROGRESS_RESET = 0x00000013;

        /**
         * @brief 设置视频显示比例
         */
        public static final int SELECT_SCALES = 0x00000014;

        /**
         * @brief 快进时间显示隐藏
         */
        public static final int HIDE_PROGRESS_TIME = 0x00000015;

        /**
         * @brief 菜单隐藏
         */
        public static final int HIDE_MENU = 0x00000016;

        /**
         * @brief 网速
         */
        public static final int START_SPEED = 0x00000017;

        /**
         * @brief 重置时间
         */
        public static final int RESET_MOVIE_TIME = 0x00000018;

        /**
         * @brief 播放异常
         */
        public static final int PLAY_ERROR = 0x00000019;

        /**
         * @brief 切换解码
         */
        public static final int SWITCH_CODE = 0x00000020;

        /**
         * @brief 试看到期跳转
         */
        public static final int Try = 0x00000023;

        /**
         * @brief 视频跑马
         */
        public static final int NOTICE = 0x00000024;

        /**
         * @brief 视频跑马隐藏
         */
        public static final int NOTICE_GONE = 0x00000025;

        /**
         * @brief 启动跑马公告
         */
        public static final int START_NOTICE_GONE = 0x00000026;

        /**
         * @brief 停止跑马公告
         */
        public static final int START_LOGO = 0x00000027;

        /**
         * @brief 嗅探
         */
        public static final int Sniffing = 0x000000028;

    }

}
