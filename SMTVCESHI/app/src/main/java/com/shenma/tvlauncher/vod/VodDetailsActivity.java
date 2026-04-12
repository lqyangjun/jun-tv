package com.shenma.tvlauncher.vod;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.shenma.tvlauncher.Api;
import com.shenma.tvlauncher.EmpowerActivity;
import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.SettingWallpaperActivity;
import com.shenma.tvlauncher.UserActivity;
import com.shenma.tvlauncher.network.GsonRequest;
import com.shenma.tvlauncher.utils.AES;
import com.shenma.tvlauncher.utils.Constant;
import com.shenma.tvlauncher.utils.GetTimeStamp;
import com.shenma.tvlauncher.utils.ImageUtil;
import com.shenma.tvlauncher.utils.Logger;
import com.shenma.tvlauncher.utils.Md5Encoder;
import com.shenma.tvlauncher.utils.Rc4;
import com.shenma.tvlauncher.utils.Rsa;
import com.shenma.tvlauncher.utils.SharePreferenceDataUtil;
import com.shenma.tvlauncher.utils.Utils;
import com.shenma.tvlauncher.view.Reflect3DImage;
import com.shenma.tvlauncher.vod.adapter.DetailsBottomGridAdapter;
import com.shenma.tvlauncher.vod.adapter.DetailsBottomListAdapter;
import com.shenma.tvlauncher.vod.adapter.VodDetailsAdapter;
import com.shenma.tvlauncher.vod.dao.VodDao;
import com.shenma.tvlauncher.vod.db.Album;
import com.shenma.tvlauncher.vod.domain.AboutInfo;
import com.shenma.tvlauncher.vod.domain.RequestVo;
import com.shenma.tvlauncher.vod.domain.VideoDetailInfo;
import com.shenma.tvlauncher.vod.domain.VideoInfo;
import com.shenma.tvlauncher.vod.domain.VodDataInfo;
import com.shenma.tvlauncher.vod.domain.VodUrl;
import com.shenma.tvlauncher.vod.domain.VodUrlList;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.shenma.tvlauncher.utils.Rc4.encry_RC4_string;

/**
 * @author joychang
 * @Description 线路选择
 */

public class VodDetailsActivity extends Activity {
    private final String TAG = "VodDetailsActivity";
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;
    private String nextlink = null;
    private String vodtype = null;
    private String vodstate = null;
    private TextView tv_details_name, tv_details_rate, tv_details_director,
            tv_details_type, tv_details_actors, tv_details_playTimes, tv_details_area,
            tv_details_video_introduce, tv_details_year;
    private ImageView iv_details_poster;
    private Button b_details_replay, b_details_play, b_details_choose, b_details_favicon,
            b_details_colection, b_details_introduce,b_details_search;
    private RadioGroup rg_video_details_resources;
    private VodDetailsAdapter vodDetailsAdapter;
    private Context context;
    private String domain;
    private String vodname;
//    private List<VodUrl> now_source = null;
    public static List<VodUrl> now_source = null;
    private String videoId;
    private String top_type;
    private LinearLayout details_recommend, details_key_arts, details_video_introduce;
    private ListView details_key_list;
    private GridView details_key_lists;
    private ImageView details_key_list_and_grid;
    private GridView details_key_grid, gv_recommend_grid;
    private DetailsBottomGridAdapter bg_Adapter;
    private DetailsBottomListAdapter bl_Adapter;
    private List<Album> albums;
    private Album album = null;
    private String sourceId = null;
    private int gv_postion = 0;
    private ArrayList<VodDataInfo> aboutlist = null;
    private RequestQueue mQueue;
    private VodDao dao;
    private String albumPic;/*影片图片路径*/
    private int rbWidth = 90;/*线路宽度*/
    private int rbHeigth = 36;/*线路高度*/
    protected SharedPreferences sp;
    public static SharedPreferences Sp;
    public static SharedPreferences Sd;
    private int vipstate;
    private int trystate = SharePreferenceDataUtil.getSharedIntData(this, "Trystate", 0);
    private int xuanjitype = SharePreferenceDataUtil.getSharedIntData(this, "xuanjitype", 1);
    private int xuanjinumber = SharePreferenceDataUtil.getSharedIntData(this, "xuanjinumber", 20);
    private int  remember_source = SharePreferenceDataUtil.getSharedIntData(this, "remember_source", 0);
    private int  Same_source_search = SharePreferenceDataUtil.getSharedIntData(this, "Same_source_search", 0);
    private Handler mediaHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Utils.showToast(context, R.string.request_failure, R.drawable.toast_err);
                    return;
                case 2:
                    Utils.showToast(context,R.string.Account_expiration, R.drawable.toast_err);
                    startActivity(new Intent(context, EmpowerActivity.class));
                    return;

                case 6:
                    Utils.showToast(context,R.string.disconnect, R.drawable.toast_err);
                    startActivity(new Intent(context, UserActivity.class));
                    return;
                case 7:
                    Utils.showToast(context,R.string. Account_has_been_disabled,R.drawable.toast_err);
                    startActivity(new Intent(context, UserActivity.class));
                    return;
                case 8:
                    Utils.showToast(context,R.string.Account_information_error, R.drawable.toast_err);
                    startActivity(new Intent(context, UserActivity.class));
                    return;
                case 9:
                    Utils.showToast(context,R.string.Account_information_has_expired, R.drawable.toast_err);
                    startActivity(new Intent(context, UserActivity.class));
                    return;
                case 10:
                    Utils.showToast(context,R.string.request_failures, R.drawable.toast_shut);
                    return;
                default:
                    return;
            }
        }
    };
    public List<VodUrlList> G = new ArrayList();//G
    public List<VodUrlList> lv_lists;
    private List<String> gv_lists;
    public int j0;
    private String Api_url = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, "Api_url", ""),Constant.d);
    private String BASE_HOST = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, "BASE_HOST", ""),Constant.d);

    /*创建时的回调函数*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //Logger.d(TAG, "onCreate.....");
        setContentView(R.layout.mv_video_details);
        context = this;
        dao = new VodDao(this);
        initData();
        initView();
        sp = getSharedPreferences("shenma", 0);
        Sp = getSharedPreferences("initData", MODE_PRIVATE);
        Sd = getSharedPreferences("TempData", MODE_PRIVATE);
    }

    /*启动*/
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        //Logger.d(TAG, "onStart.....");
    }

    /*停止*/
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        closeProgressDialog();
        if (null != mQueue) {
            mQueue.stop();
        }
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        //Logger.d(TAG, "onStop.....");
    }

    /*暂停*/
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        //Logger.d(TAG, "onStop.....");
    }

    /*销毁*/
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (null != mQueue) {
            mQueue.cancelAll(this);
        }
        //Logger.d(TAG, "onDestroy.....");
    }

    /*恢复*/
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        //Logger.v(TAG, " onResume()获取的videoId=" + videoId);
        if (null != videoId) {
            albums = dao.queryAlbumById(videoId, 2);
            if (null != albums && albums.size() > 0) {
                album = albums.get(0);
            }
            if (null != album && !vodtype.equals("LIVE")) {
                b_details_replay.setVisibility(View.VISIBLE);
                b_details_replay.requestFocus();
                //Logger.v(TAG, "onResume()续播playIndex==" + album.getPlayIndex() + "...collectionTime==" + album.getCollectionTime());
            }
        }
        super.onResume();
    }

    /* 初始化数据*/
    private void initData() {
        Intent intent = getIntent();
        vodtype = intent.getStringExtra("vodtype");
        vodstate = intent.getStringExtra("vodstate");
        nextlink = intent.getStringExtra("nextlink");
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.hao260x366)
                .showImageForEmptyUri(R.drawable.hao260x366)
                .showImageOnFail(R.drawable.hao260x366)
                .resetViewBeforeLoading(true).cacheInMemory(true)
                .cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(300)).build();
    }

    /*初始化控件*/
    private void initView() {
        rbWidth = getResources().getDimensionPixelSize(R.dimen.sm_90);
        rbHeigth = getResources().getDimensionPixelSize(R.dimen.sm_36);
        findViewById();
        loadViewLayout();
        setListener();
        processLogic();
    }

    /*按ID查找视图*/
    protected void findViewById() {
        iv_details_poster = (ImageView) findViewById(R.id.iv_details_poster);
        tv_details_name = (TextView) findViewById(R.id.tv_details_name);
        tv_details_year = (TextView) findViewById(R.id.tv_details_year);
        tv_details_rate = (TextView) findViewById(R.id.tv_details_rate);
        tv_details_director = (TextView) findViewById(R.id.tv_details_director);
        tv_details_type = (TextView) findViewById(R.id.tv_details_type);
        tv_details_actors = (TextView) findViewById(R.id.tv_details_actors);
        tv_details_playTimes = (TextView) findViewById(R.id.tv_details_playTimes);
        tv_details_area = (TextView) findViewById(R.id.tv_details_area);
        tv_details_video_introduce = (TextView) findViewById(R.id.tv_details_video_introduce);
        b_details_replay = (Button) findViewById(R.id.b_details_replay);
        b_details_play = (Button) findViewById(R.id.b_details_play);
        b_details_choose = (Button) findViewById(R.id.b_details_choose);
        b_details_favicon = (Button) findViewById(R.id.b_details_favicon);
        b_details_colection = (Button) findViewById(R.id.b_details_colection);
        b_details_introduce = (Button) findViewById(R.id.b_details_introduce);

        b_details_search = (Button) findViewById(R.id.b_details_search);
        if (Same_source_search == 1){
            b_details_search.setVisibility(View.VISIBLE);
        }

        details_recommend = (LinearLayout) findViewById(R.id.details_recommend);
        gv_recommend_grid = (GridView) findViewById(R.id.recommend_grid);
        gv_recommend_grid.setSelector(new ColorDrawable(Color.TRANSPARENT));
        details_key_arts = (LinearLayout) findViewById(R.id.details_key_arts);
        details_video_introduce = (LinearLayout) findViewById(R.id.details_video_introduce);

        details_key_list = (ListView) findViewById(R.id.details_key_list);
        details_key_list.setSelector(new ColorDrawable(0));

        details_key_lists = (GridView) findViewById(R.id.details_key_lists);
        details_key_lists.setSelector(new ColorDrawable(0));

        details_key_list_and_grid = (ImageView) findViewById(R.id.details_key_list_and_grid);

        details_key_grid = (GridView) findViewById(R.id.details_key_grid);
        details_key_grid.setSelector(new ColorDrawable(0));

        if (xuanjitype == 1){//读取主控配置
            details_key_lists.setVisibility(View.GONE);
        }else {
            details_key_list.setVisibility(View.GONE);
            details_key_list_and_grid.setVisibility(View.GONE);
            details_key_grid.setVisibility(View.GONE);
        }
        rg_video_details_resources = (RadioGroup) findViewById(R.id.rg_video_details_resources);
        if (vodtype.equals("MOVIE")) {
            b_details_favicon.setVisibility(View.VISIBLE);
            //b_details_choose.setVisibility(View.GONE);
            b_details_choose.setVisibility(View.VISIBLE);
        } else {
            b_details_colection.setVisibility(View.VISIBLE);
            b_details_choose.setVisibility(View.VISIBLE);
        }
    }

    /*加载视图布局*/
    protected void loadViewLayout() {

    }

    /*创建底部布局*/
    protected void CreateBottomLayout() {
        int number;
        if (xuanjitype == 1){
            number = xuanjinumber;//20集一组
        }else{
            number = 20000;//20集一组 读取主控配置
        }
        List<VodUrlList> list = G;
        if (list != null && list.size() > 0) {
            List<VodUrlList> list2 = G;
            ArrayList arrayList = new ArrayList();
            list2.size();
            int size = list2.size() / number;
            int i = 0;
            while (i < size) {
                StringBuilder sb = new StringBuilder();
                sb.append((i * number) + 1);
                sb.append("-");
                i++;
                sb.append(i * number);
                arrayList.add(sb.toString());
            }
            int i2 = (i * number) + 1;
            if (i2 <= list2.size()) {
                arrayList.add(i2 + "-" + list2.size());
            }
            gv_lists = arrayList;
            bg_Adapter = new DetailsBottomGridAdapter(VodDetailsActivity.this, gv_lists);
            details_key_grid.setAdapter(bg_Adapter);
            lv_lists = Utils.getVideolvDatas(G, 0, number);

            if (xuanjitype == 1) {//读取主控配置
                bl_Adapter = new DetailsBottomListAdapter(VodDetailsActivity.this, lv_lists, 0);
                details_key_list.setAdapter(bl_Adapter);
            }else {
                bl_Adapter = new DetailsBottomListAdapter(VodDetailsActivity.this, lv_lists, 1);
                details_key_lists.setAdapter(bl_Adapter);
            }
        }

        details_key_grid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gv_postion = position;
                /*列表*/
                lv_lists = Utils.getVideolvDatas(G, position, number);
                bl_Adapter.changData(lv_lists);
            }


        });

        details_key_list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                ArrayList arrayLista = new ArrayList();
                for (int i = 0; i < now_source.size(); i++) {
                    arrayLista.add(now_source.get(i));
                }
                if (arrayLista.size() <= 0) {
                    Utils.showToast(context, R.string.No_data_source, R.drawable.toast_err);
                    return;
                }
                List<VodUrlList> list = null;
                for (int i = 0; i < now_source.size(); i++) {
                    RadioButton radioButton = (RadioButton) rg_video_details_resources.getChildAt(i);
                    Log.e("rb", radioButton.getId() + "");
                    if (radioButton.isChecked()) {
                        domain = now_source.get(i).getType();
                        list = now_source.get(i).getList();
                    }
                }
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < list.size(); i++) {
                    VideoInfo vinfo = new VideoInfo();
                    vinfo.title = list.get(i).getTitle();
                    vinfo.url = list.get(i).getUrl();
                    arrayList.add(vinfo);
                }
                Sd.edit().putString("arrayList", new Gson().toJson(arrayList)).apply(); // 使用apply()以避免阻塞主线程
                Intent intent = null;
                intent = new Intent(VodDetailsActivity.this, VideoPlayerActivity.class);
//                intent.putParcelableArrayListExtra("videoinfo", arrayList);
                intent.putExtra("albumPic", albumPic);//图片路径
                intent.putExtra("vodtype", vodtype);//影片类型
                intent.putExtra("vodstate", vodstate);//影片情势
                intent.putExtra("nextlink", nextlink);
                intent.putExtra("videoId", videoId);//影片ID
                intent.putExtra("vodname", vodname);//电影的名字
                intent.putExtra("sourceId", sourceId);//源id
                intent.putExtra("domain", domain);//domain
                intent.putExtra("playIndex", gv_postion * 20 + position);//剧集标
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }

        });

        details_key_lists.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                ArrayList arrayLista = new ArrayList();
                for (int i = 0; i < now_source.size(); i++) {
                    arrayLista.add(now_source.get(i));
                }
                if (arrayLista.size() <= 0) {
                    Utils.showToast(context, R.string.No_data_source, R.drawable.toast_err);
                    return;
                }
                List<VodUrlList> list = null;
                for (int i = 0; i < now_source.size(); i++) {
                    RadioButton radioButton = (RadioButton) rg_video_details_resources.getChildAt(i);
                    Log.e("rb", radioButton.getId() + "");
                    if (radioButton.isChecked()) {
                        domain = now_source.get(i).getType();
                        list = now_source.get(i).getList();
                    }
                }
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < list.size(); i++) {
                    VideoInfo vinfo = new VideoInfo();
                    vinfo.title = list.get(i).getTitle();
                    vinfo.url = list.get(i).getUrl();
                    arrayList.add(vinfo);
                }
                Sd.edit().putString("arrayList", new Gson().toJson(arrayList)).apply(); // 使用apply()以避免阻塞主线程
                Intent intent = null;
                intent = new Intent(VodDetailsActivity.this, VideoPlayerActivity.class);
//                intent.putParcelableArrayListExtra("videoinfo", arrayList);
                intent.putExtra("albumPic", albumPic);//图片路径
                intent.putExtra("vodtype", vodtype);//影片类型
                intent.putExtra("vodstate", vodstate);//影片情势
                intent.putExtra("nextlink", nextlink);
                intent.putExtra("videoId", videoId);//影片ID
                intent.putExtra("vodname", vodname);//电影的名字
                intent.putExtra("sourceId", sourceId);//源id
                intent.putExtra("domain", domain);//domain
                intent.putExtra("playIndex", gv_postion * 20 + position);//剧集标
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }

        });
    }

    /*背部按压*/
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        //super.onBackPressed();
        if (details_key_arts.getVisibility() == View.VISIBLE || details_video_introduce.getVisibility() == View.VISIBLE) {
            details_video_introduce.setVisibility(View.GONE);
            details_key_arts.setVisibility(View.GONE);
            details_recommend.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
    }

    /*集合列表*/
    protected void setListener() {

        /*播放按钮*/
        b_details_play.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                /*将播放数据传给播放器*/
                ArrayList arrayLista = new ArrayList();
                for (int i = 0; i < now_source.size(); i++) {
                    arrayLista.add(now_source.get(i));
                }
                if (arrayLista.size() <= 0) {
                    Utils.showToast(context, R.string.No_data_source, R.drawable.toast_err);
                    return;
                }
                List<VodUrlList> list = null;
                for (int i = 0; i < now_source.size(); i++) {
                    RadioButton radioButton = (RadioButton) rg_video_details_resources.getChildAt(i);
                    Log.e("rb", radioButton.getId() + "");
                    if (radioButton.isChecked()) {
                        domain = now_source.get(i).getType();
                        list = now_source.get(i).getList();
                    }
                }

//                Log.e("vodUrlLists==", new Gson().toJson(list) );

                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < list.size(); i++) {
                    VideoInfo vinfo = new VideoInfo();
                    vinfo.title = list.get(i).getTitle();
                    vinfo.url = list.get(i).getUrl();
                    arrayList.add(vinfo);
                }
                Sd.edit().putString("arrayList", new Gson().toJson(arrayList)).apply(); // 使用apply()以避免阻塞主线程
                Intent intent = null;
                intent = new Intent(VodDetailsActivity.this, VideoPlayerActivity.class);
//                intent.putParcelableArrayListExtra("videoinfo", arrayList);
                intent.putExtra("albumPic", albumPic);//图片路径
                intent.putExtra("vodtype", vodtype);//影片类型
                intent.putExtra("vodstate", vodstate);//影片情势
                intent.putExtra("nextlink", nextlink);
                intent.putExtra("videoId", videoId);//影片ID
                intent.putExtra("vodname", vodname);//电影的名字
                intent.putExtra("sourceId", sourceId);//源id
                intent.putExtra("domain", domain);//domain
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        /*续播按钮*/
        b_details_replay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                /*将播放数据传给播放器*/
                ArrayList arrayLista = new ArrayList();
                for (int i = 0; i < now_source.size(); i++) {
                    arrayLista.add(now_source.get(i));
                }
                if (arrayLista.size() <= 0) {
                    Utils.showToast(context, R.string.No_data_source, R.drawable.toast_err);
                    return;
                }
                List<VodUrlList> list = null;
                for (int i = 0; i < now_source.size(); i++) {
                    RadioButton radioButton = (RadioButton) rg_video_details_resources.getChildAt(i);
                    Log.e("rb", radioButton.getId() + "");
                    if (radioButton.isChecked()) {
                        domain = now_source.get(i).getType();
                        list = now_source.get(i).getList();
                    }
                }
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < list.size(); i++) {
                    VideoInfo vinfo = new VideoInfo();
                    vinfo.title = list.get(i).getTitle();
                    vinfo.url = list.get(i).getUrl();
                    arrayList.add(vinfo);
                }
                Sd.edit().putString("arrayList", new Gson().toJson(arrayList)).apply(); // 使用apply()以避免阻塞主线程
                Intent intent = null;
                intent = new Intent(VodDetailsActivity.this, VideoPlayerActivity.class);
//                intent.putParcelableArrayListExtra("videoinfo", arrayList);
                intent.putExtra("albumPic", albumPic);//图片路径
                intent.putExtra("vodtype", vodtype);//影片类型
                intent.putExtra("vodstate", vodstate);//影片情势
                intent.putExtra("nextlink", nextlink);
                intent.putExtra("videoId", videoId);//影片ID
                intent.putExtra("vodname", vodname);//电影的名字
                intent.putExtra("sourceId", sourceId);//源id
                intent.putExtra("domain", domain);//domain
                intent.putExtra("playIndex", album.getPlayIndex());//剧集标
                intent.putExtra("collectionTime", album.getCollectionTime());//时间点
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


        /*选集按钮*/
        b_details_choose.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (details_key_arts.getVisibility() == View.VISIBLE) {
                    details_key_arts.setVisibility(View.GONE);
                    details_video_introduce.setVisibility(View.GONE);
                    details_recommend.setVisibility(View.VISIBLE);
                } else {
                    details_key_arts.setVisibility(View.VISIBLE);
                    details_recommend.setVisibility(View.GONE);
                    details_video_introduce.setVisibility(View.GONE);
                }
                CreateBottomLayout();
            }
        });

        /*收藏按钮*/
        b_details_favicon.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (null != videoId) {
                    Boolean b = dao.queryZJById(videoId, 1);
                    if (b) {
                        dao.deleteByWhere(videoId, vodtype, 1);
                        b_details_favicon.setBackgroundResource(R.drawable.video_details_favicon_selector);
                        Utils.showToast(VodDetailsActivity.this, R.string.Cancel_collection_successful, R.drawable.toast_smile);
                    } else {
                        Album al = new Album();
                        al.setAlbumId(videoId);
                        al.setAlbumType(vodtype);
                        al.setTypeId(1);
                        al.setAlbumState(vodstate);
                        al.setNextLink(nextlink);
                        al.setAlbumPic(albumPic);
                        al.setAlbumTitle(vodname);
                        dao.addAlbums(al);
                        b_details_favicon.setBackgroundResource(R.drawable.video_details_yifavicon_selector);
                        Utils.showToast(VodDetailsActivity.this, R.string.Collection_successful, R.drawable.toast_smile);
                    }
                }
            }
        });

        /*追剧按钮*/
        b_details_colection.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (null != videoId) {
                    Boolean b = dao.queryZJById(videoId, 0);
                    if (b) {
                        dao.deleteByWhere(videoId, vodtype, 0);
                        b_details_colection.setBackgroundResource(R.drawable.video_details_zhuiju_selector);
                        Utils.showToast(VodDetailsActivity.this, R.string.Successfully_cancelled_the_following_drama, R.drawable.toast_smile);
                    } else {
                        Album al = new Album();
                        al.setAlbumId(videoId);
                        al.setAlbumType(vodtype);
                        al.setAlbumState(vodstate);
                        al.setNextLink(nextlink);
                        al.setTypeId(0);
                        al.setAlbumPic(albumPic);
                        al.setAlbumTitle(vodname);
                        dao.addAlbums(al);
                        b_details_colection.setBackgroundResource(R.drawable.video_details_yizhuiju_selector);
                        Utils.showToast(VodDetailsActivity.this, R.string.Successfully_followed_the_drama, R.drawable.toast_smile);
                    }
                }
            }
        });
        /*详情按钮*/
        b_details_introduce.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (details_video_introduce.getVisibility() == View.VISIBLE) {
                    details_video_introduce.setVisibility(View.GONE);
                    details_recommend.setVisibility(View.VISIBLE);
                    details_key_arts.setVisibility(View.GONE);
                } else {
                    details_key_arts.setVisibility(View.GONE);
                    details_video_introduce.setVisibility(View.VISIBLE);
                    details_recommend.setVisibility(View.GONE);
                }
            }
        });

        /*搜索按钮*/
        b_details_search.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(VodDetailsActivity.this, SearchActivity.class);
                intent.putExtra("TYPE", "ALL");
                intent.putExtra("NAME", vodname);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        /*监听视频源的选中*/
        rg_video_details_resources.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i2 = 0; i2 < now_source.size(); i2++) {
                    RadioButton radioButton = (RadioButton) rg_video_details_resources.getChildAt(i2);
                    if (radioButton != null && radioButton.isChecked()) {
                        fillRadioGroup(now_source.get(i2).getName());
                        gv_postion = 0;
                        CreateBottomLayout();
                    }
                }
                sourceId = String.valueOf(checkedId);

//                System.out.println("解码模式：硬解" + checkedId);

                if (vodtype.equals("MOVIE") && now_source != null && now_source.size() > 1) {
                    //Logger.v(TAG, "now_source数量=" + now_source.size());
                    b_details_choose.setVisibility(View.VISIBLE);
                }
                //sourceId = String.valueOf(checkedId);
                /*改版任何线路都可续播*/
                if(null != album && !vodtype.equals("LIVE")){
                    b_details_replay.setVisibility(View.VISIBLE);
                }else{
                    b_details_replay.setVisibility(View.GONE);
                }
                /*选集菜单*/
                if (details_key_arts.getVisibility() == View.VISIBLE) {
                    CreateBottomLayout();
                } else {
                    details_key_arts.setVisibility(View.GONE);
                    details_recommend.setVisibility(View.VISIBLE);
                }

            }

        });

    }

    public final void fillRadioGroup(String str) {
        int size;
        StringBuilder sb;
        String str2;
        for (int i = 0; i < now_source.size(); i++) {
            if (str.equals(now_source.get(i).getName())) {
                if (!top_type.equals("3") && (size = now_source.get(i).getList().size()) < j0) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.addAll(now_source.get(i).getList());
                    for (int i3 = 0; i3 <= j0; i3++) {
                        if (size < i3) {
                            VodUrlList vodUrlList = new VodUrlList();
                            vodUrlList.setUrl("*");
                            if (i3 < 10) {
                                sb = new StringBuilder();
                                str2 = "第0";
                            } else {
                                sb = new StringBuilder();
                                str2 = "第";
                            }
                            sb.append(str2);
                            sb.append(i3);
                            sb.append("集");
                            vodUrlList.setTitle(sb.toString());
                            arrayList.add(vodUrlList);
                        }
                    }
                    now_source.get(i).setList(arrayList);
                }
                G.clear();
                G.addAll(now_source.get(i).getList());
            }
        }
    }

    /*组拼数据*/
    protected void processLogic() {
        RequestVo vo = new RequestVo();
        vo.context = context;
        if (null != nextlink) {
            //vo.requestUrl = nextlink;
            vo.requestUrl = Api_url + "/api.php/" + BASE_HOST + "/vod/" + nextlink;
            //Logger.v(TAG, "访问:::" + nextlink);
            getDataFromServer(vo);
        }
    }

    /**
     * 从服务器上获取数据，并回调处理
     *
     * @param reqVo
     */
    protected void getDataFromServer(RequestVo reqVo) {
        showProgressDialog();
        mQueue = Volley.newRequestQueue(context, new HurlStack());
        if (Utils.hasNetwork(context)) {
            GsonRequest<VideoDetailInfo> mVodData = new GsonRequest<VideoDetailInfo>(Method.POST, reqVo.requestUrl,
                    VideoDetailInfo.class, createVodDataSuccessListener(), createVodDataErrorListener()){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    try {
                        params.put("data", AES.encrypt_Aes(Md5Encoder.encode(Constant.c), Md5Encoder.encode(Constant.d),Constant.c));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    params.put("sign", Base64.encodeToString(Utils.strRot13(Constant.c).getBytes(), Base64.DEFAULT));
                    params.put("time", GetTimeStamp.timeStamp());
                    params.put("key", encry_RC4_string(GetTimeStamp.timeStamp(),GetTimeStamp.timeStamp()));
                    params.put("os",  Integer.toString(android.os.Build.VERSION.SDK_INT));
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(VodDetailsActivity.this, "Authorization", ""),Constant.d));  /*设置其他请求头*/
                    return headers;
                }
            };
            mQueue.add(mVodData);
        }
    }

    /*影视详细数据请求成功*/
    private Response.Listener<VideoDetailInfo> createVodDataSuccessListener() {
        return new Response.Listener<VideoDetailInfo>() {
            @SuppressLint("ResourceType")
            @Override
            public void onResponse(VideoDetailInfo paramObject) {
                if (null != paramObject) {
                    VideoDetailInfo VideoDetailInfo = (VideoDetailInfo) paramObject;
                    List<VodUrl> video_list = VideoDetailInfo.getVideo_list();
                    now_source = video_list;
                    int i = 0;
                    for (int i2 = 0; i2 < video_list.size(); i2++) {
                        int size = video_list.get(i2).getList().size();
                        if (i < size) {
                            i = size;
                        }
                    }
                    j0 = i;
                    vodname = paramObject.getTitle();
                    videoId = paramObject.getId();
                    top_type = paramObject.getTop_type();
                    //Logger.v(TAG, "服务器获取的videoId=" + videoId);
                    albums = dao.queryAlbumById(videoId, 2);
                    if (null != albums && albums.size() > 0) {
                        album = albums.get(0);
                    }
                    b_details_play.requestFocus();
                    if (null != album && !vodtype.equals("LIVE")) {
                        b_details_replay.setVisibility(View.VISIBLE);
                        b_details_replay.requestFocus();
                        //Logger.v(TAG, "续播playIndex==" + album.getPlayIndex() + "...collectionTime==" + album.getCollectionTime());
                    } else {
                        b_details_replay.setVisibility(View.GONE);
                        b_details_play.requestFocus();
                    }
                    tv_details_name.setText(vodname);
                    tv_details_director.setText(Arrays.toString(paramObject.getDirector()).replace("[", "").replace("]", "").replace(",", " / ").replace("null", "暂无"));
                    tv_details_type.setText(Arrays.toString(paramObject.getType()).replace("[", "").replace("]", "").replace(",", " / ").replace("null", "暂无"));
                    tv_details_actors.setText(Arrays.toString(paramObject.getActor()).replace("[", "").replace("]", "").replace(",", " / ").replace("null", "暂无"));
                    tv_details_area.setText(Arrays.toString(paramObject.getArea()).replace("[", "").replace("]", "").replace(",", " / ").replace("null", "暂无"));
                    if (null != paramObject.getIntro() && !"".equals(paramObject.getIntro())) {
                        //详情
                        tv_details_video_introduce.setText("简介：" + paramObject.getIntro().replace("null", "暂无"));
                    } else {
                        tv_details_video_introduce.setText("简介：暂无");
                    }
                    /*图片地址数据*/
                    albumPic = paramObject.getImg_url();
                    imageLoader.displayImage(albumPic, iv_details_poster, options, new ImageLoadingListener() {

                        @Override
                        public void onLoadingStarted(String arg0, View arg1) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                            // TODO Auto-generated method stub
                            Drawable drawable = iv_details_poster.getDrawable();
                            Bitmap bitmap = ImageUtil.drawableToBitmap(drawable);
                            Bitmap bit = Reflect3DImage.skewImage(bitmap, 60);
                            iv_details_poster.setImageBitmap(bit);
                        }

                        @Override
                        public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                            // TODO Auto-generated method stub
                            Drawable drawable = iv_details_poster.getDrawable();
                            Bitmap bitmap = ImageUtil.drawableToBitmap(drawable);
                            Bitmap bit = Reflect3DImage.skewImage(bitmap, 60);
                            iv_details_poster.setImageBitmap(bit);
                        }

                        @Override
                        public void onLoadingCancelled(String arg0, View arg1) {
                            // TODO Auto-generated method stub
                            Drawable drawable = iv_details_poster.getDrawable();
                            Bitmap bitmap = ImageUtil.drawableToBitmap(drawable);
                            Bitmap bit = Reflect3DImage.skewImage(bitmap, 60);
                            iv_details_poster.setImageBitmap(bit);
                        }
                    });
                    /*视频地址数据*/
                    if (vodtype.equals("MOVIE")) {
                        //是否收藏
                        b_details_favicon.setBackgroundResource(dao.queryZJById(videoId, 1) ? R.drawable.video_details_yifavicon_selector : R.drawable.video_details_favicon_selector);
                        tv_details_year.setText(paramObject.getPubtime());
                        tv_details_playTimes.setText(paramObject.getPubtime());
                        if (null != paramObject.getCur_episode()) {
                            String state = paramObject.getTrunk();
                            if (vodstate == null || "".equals(vodstate)) {
                                vodstate = state;
                            }
                            tv_details_rate.setText(state);
                        } else {
                            tv_details_rate.setText("");
                        }
                        // 电影
//                        b_details_choose.setVisibility(View.GONE);
                    } else {
                        //是否追剧
                        //Logger.d(TAG, "是否追剧===" + dao.queryZJById(videoId, 0));
                        b_details_colection.setBackgroundResource(dao.queryZJById(videoId, 0) ? R.drawable.video_details_yizhuiju_selector : R.drawable.video_details_zhuiju_selector);
                        //电视剧、动漫、综艺
                        tv_details_year.setText(paramObject.getPubtime());
                        tv_details_playTimes.setText(paramObject.getPubtime());
                        if (null != paramObject.getCur_episode()) {
                            String state = paramObject.getTrunk();
                            if (vodstate == null || "".equals(vodstate)) {
                                vodstate = state;
                            }
                            tv_details_rate.setText(state);
                        } else {
                            tv_details_rate.setText("");
                        }
                    }
                    /*相关推荐数据*/
                    AboutInfo about = paramObject.getAbout();
                    if (null != about) {
                        ArrayList<VodDataInfo> similary = (ArrayList<VodDataInfo>) about.getSimilary();
                        ArrayList<VodDataInfo> actor = (ArrayList<VodDataInfo>) about.getActor();
                        if (null != similary && similary.size() > 0) {
                            aboutlist = similary;
                            vodDetailsAdapter = new VodDetailsAdapter(context,
                                    aboutlist, imageLoader);
                            //Logger.v(TAG, "similary==" + similary.size());

                        } else if (null != actor && actor.size() > 0) {
                            aboutlist = actor;
                            vodDetailsAdapter = new VodDetailsAdapter(context,
                                    aboutlist, imageLoader);
                        }

                    }

                    if (rg_video_details_resources.getChildCount() > 0) {
                        rg_video_details_resources.clearCheck();
                        rg_video_details_resources.removeAllViews();
                    }
                    LinkedList linkedList = new LinkedList();
                    List<VodUrl> list = now_source;

                    int num = 0;
                    if (album != null) {
                        String sourceType = album.getAlbumSourceType();
                        if (sourceType != null && !sourceType.isEmpty()) {
                            try {
                                num = Integer.parseInt(sourceType);
                            } catch (NumberFormatException e) {
                                // 如果解析失败，num已经是0，不需要额外操作
                                // 可以在这里添加日志或错误处理代码
                            }
                        }
                    }

                    if ((num-1) <= 0 ){
                        num = 0;
                    }else{
                        if (remember_source == 1)
                            num = num-1;
                        else
                            num = 0;
                    }

                    if (list != null && list.size() > 0) {
                        int radioButtonId = 1; // 保存RadioButton的id
                        /*没有更新集数显示*/
                        if (SharePreferenceDataUtil.getSharedIntData(VodDetailsActivity.this, "UpdateNumber", 0) == 0){
                            for (int i5 = 0; i5 < now_source.size(); i5++) {
                                linkedList.add(now_source.get(i5).getName());
                                Log.d("name---", now_source.get(i5).getName());
                                RadioButton radioButton = new RadioButton(VodDetailsActivity.this);
                                radioButton.setTextColor(context.getResources().getColorStateList(R.drawable.radiobutton_background_checked));
                                radioButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sm_22));
                                radioButton.setGravity(1);//居中还是偏左 0=偏左 1=居中
                                radioButton.setText(now_source.get(i5).getName());
                                radioButton.setButtonDrawable(R.drawable.detailsource_bg_s);
                                radioButton.setPadding(0, 0, getResources().getDimensionPixelSize(R.dimen.sm_15), 0);
                                radioButton.setId(radioButtonId); // 设置RadioButton的id
                                radioButtonId++; // 更新RadioButton的id
								
								radioButton.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                                //radioButton.setGravity(Gravity.CENTER); //文字居中
                                radioButton.setMarqueeRepeatLimit(Integer.MAX_VALUE); // 设置为最大整数值以实现无限循环
                                radioButton.setSingleLine(true); // 跑马灯效果需要设置为单行
                                rg_video_details_resources.addView(radioButton, getResources().getDimensionPixelSize(R.dimen.sm_130), rbHeigth);
                                if (i5 == num) {
                                    fillRadioGroup(now_source.get(i5).getName());
                                    rg_video_details_resources.check(radioButton.getId());
                                }
                            }
                        }else{
                            /*按钮没设置id的源*/
//                            for (int i5 = 0; i5 < now_source.size(); i5++) {
//                                String name = now_source.get(i5).getName();
//                                Log.d("name---", now_source.get(i5).getName());
//                                int listSize = now_source.get(i5).getList().size();
//                                String text = name + " " + listSize;
//                                SpannableString spannableString = new SpannableString(text);
//                                spannableString.setSpan(new SuperscriptSpan(), name.length() + 1, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                                spannableString.setSpan(new RelativeSizeSpan(0.6f), name.length() + 1, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                                RadioButton radioButton = new RadioButton(VodDetailsActivity.this);
//                                radioButton.setTextColor(context.getResources().getColorStateList(R.drawable.radiobutton_background_checked));
//                                radioButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sm_22));
//                                radioButton.setGravity(Gravity.END | Gravity.TOP); // 将文本设置为右上角对齐
//                                radioButton.setText(spannableString);
//                                radioButton.setButtonDrawable(R.drawable.detailsource_bg_s);
//                                radioButton.setPadding(0, 0, 0, 0);
//                                rg_video_details_resources.addView(radioButton, getResources().getDimensionPixelSize(R.dimen.sm_125), rbHeigth);
//                                if (i5 == 0) {
//                                    fillRadioGroup(name);
//                                    rg_video_details_resources.check(radioButton.getId());
//                                }
//                            }

//                            int radioButtonId = 1; // 保存RadioButton的id
                            for (int i5 = 0; i5 < now_source.size(); i5++) {
                                String name = now_source.get(i5).getName();
                                Log.d("name---", now_source.get(i5).getName());
                                int listSize = now_source.get(i5).getList().size();
                                String text = name + " " + listSize;
                                SpannableString spannableString = new SpannableString(text);
                                spannableString.setSpan(new SuperscriptSpan(), name.length() + 1, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannableString.setSpan(new RelativeSizeSpan(0.6f), name.length() + 1, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                RadioButton radioButton = new RadioButton(VodDetailsActivity.this);
                                radioButton.setTextColor(context.getResources().getColorStateList(R.drawable.radiobutton_background_checked));
                                radioButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sm_22));
                                radioButton.setGravity(Gravity.END | Gravity.TOP); // 将文本设置为右上角对齐
                                radioButton.setText(spannableString);
                                radioButton.setButtonDrawable(R.drawable.detailsource_bg_s);
                                radioButton.setPadding(0, 0, getResources().getDimensionPixelSize(R.dimen.sm_15), 0);
                                radioButton.setId(radioButtonId); // 设置RadioButton的id
                                radioButtonId++; // 更新RadioButton的id

                                radioButton.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                                //radioButton.setGravity(Gravity.CENTER); //文字居中
                                radioButton.setMarqueeRepeatLimit(Integer.MAX_VALUE); // 设置为最大整数值以实现无限循环
                                radioButton.setSingleLine(true); // 跑马灯效果需要设置为单行
                                //if (i5 != 0){
                                //    int paddingLeft = getResources().getDimensionPixelSize(R.dimen.sm_15);
                                //    radioButton.setPadding(paddingLeft, 0, 0, 0);
                                //}

                                rg_video_details_resources.addView(radioButton, getResources().getDimensionPixelSize(R.dimen.sm_180), rbHeigth);
                                if (i5 == num) {
                                    fillRadioGroup(name);
                                    rg_video_details_resources.check(radioButton.getId());
                                }
                            }
                        }
                    }

                    gv_recommend_grid.setAdapter(vodDetailsAdapter); // 为界面填充数据
                    gv_recommend_grid.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            String username = sp.getString("userName", null);
                            if (username != null) {
                                GetMotion(position);
                            } else if (username == null) {
                                Utils.showToast(context,R.string.Please_log_in_to_your_account_first, R.drawable.toast_err);
                                startActivity(new Intent(VodDetailsActivity.this, UserActivity.class));
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                        }
                    });
                }
                closeProgressDialog();
            }
        };
    }

    /*影视详细数据请求失败*/
    private Response.ErrorListener createVodDataErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.showToast(context,
                        getString(R.string.str_data_loading_error),
                        R.drawable.toast_err);
                if (error instanceof TimeoutError) {
                    //Logger.e(TAG, "请求超时");
                } else if (error instanceof AuthFailureError) {
                    //Logger.e(TAG, "AuthFailureError=" + error.toString());
                }
                closeProgressDialog();
            }
        };
    }

    /*显示提示框*/
    protected void showProgressDialog() {
        Utils.loadingShow_tv(VodDetailsActivity.this, R.string.str_data_loading);
    }

    /*关闭提示框*/
    protected void closeProgressDialog() {
        Utils.loadingClose_Tv();
    }

    /*心跳*/
    private void GetMotion(final int position) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        while (true) {
            try {
                if (new Date(System.currentTimeMillis()).getTime() < simpleDateFormat.parse(GetTimeStamp.timeStamp2Date(sp.getString("vip", null), "")).getTime() || sp.getString("vip", null).equals("999999999")) {
                    vipstate = 1;/*没到期*/
                } else {
                    vipstate = 0;/*已到期*/
                }
                mQueue = Volley.newRequestQueue(this, new HurlStack());
                String User_url = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.s, ""), Constant.d);
                final String RC4KEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.kd, ""),Constant.d);
                final String RSAKEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.tb, ""),Constant.d);
                String AESKEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.um, ""),Constant.d);
                String AESIV = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.im, ""),Constant.d);
                final String Appkey = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.yk, ""),Constant.d);
                int miType = SharePreferenceDataUtil.getSharedIntData(this, Constant.ue, 1);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, User_url + "/api.php?app=" + Api.APPID + "&act=motion",
                        new com.android.volley.Response.Listener<String>() {
                            public void onResponse(String response) {
                                GetMotionResponse(response,position);
                            }
                        }, new com.android.volley.Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        mediaHandler.sendEmptyMessage(1);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        String codedata = "token=" + sp.getString("ckinfo", null) + "&t=" + GetTimeStamp.timeStamp();
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
                        params.put("data", rc4data);
                        params.put("sign", sign);
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(VodDetailsActivity.this, "Authorization", ""),Constant.d));  /*设置其他请求头*/
                        return headers;
                    }
                };
                mQueue.add(stringRequest);
                return;
            } catch (ParseException ex) {
                ex.printStackTrace();
                continue;
            }
        }



    }

    /*心跳响应*/
    public void GetMotionResponse(String response,final int position) {
        String RC4KEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.kd, ""), Constant.d);
        String RSAKEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.tb, ""),Constant.d);
        String AESKEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.um, ""),Constant.d);
        String AESIV = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.im, ""),Constant.d);
        //Log.i(TAG, "GetMotionResponse: " + response);
        try {
            JSONObject jSONObject = new JSONObject(response);
            int code = jSONObject.optInt("code");
            if (code == 200){
                int miType = SharePreferenceDataUtil.getSharedIntData(this, Constant.ue, 1);
                JSONObject msg = null;
                if (miType == 1) {
                    msg = new JSONObject(Rc4.decry_RC4(jSONObject.optString("msg"),RC4KEY));
                } else if (miType == 2) {
                    msg = new JSONObject(Rsa.decrypt_Rsa(jSONObject.optString("msg"),RSAKEY));
                } else if (miType == 3) {
                    msg = new JSONObject(AES.decrypt_Aes(AESKEY,jSONObject.optString("msg"), AESIV));
                }
                String vip = msg.optString("vip");
                int Try = msg.optInt("Try");
                int Clientmode = msg.optInt("Clientmode");
                trystate = Try;
                sp.edit().putString("vip", vip).commit();
                Sp.edit()
                        .putInt("Submission_method", Clientmode)
                        .putInt("Trystate", Try)
                        .commit();
            }else if (code == 127) {/*其他设备登录*/
                mediaHandler.sendEmptyMessage(6);
                sp.edit().putString("userName", null).putString("passWord", null).putString("vip", null).putString("fen", null).putString("ckinfo", null).commit();
                return;
            }else if (code == 114) {/*账户封禁*/
                mediaHandler.sendEmptyMessage(7);
                sp.edit().putString("userName", null).putString("passWord", null).putString("vip", null).putString("fen", null).putString("ckinfo", null).commit();
                return;
            }else if (code == 125) {/*账户信息错误*/
                mediaHandler.sendEmptyMessage(8);
                sp.edit().putString("userName", null).putString("passWord", null).putString("vip", null).putString("fen", null).putString("ckinfo", null).commit();
                return;
            }else if (code == 127) {/*账户信息失效*/
                mediaHandler.sendEmptyMessage(9);
                sp.edit().putString("userName", null).putString("passWord", null).putString("vip", null).putString("fen", null).putString("ckinfo", null).commit();
                return;
            }else if (code == 201){/*201心跳失败*/
                mediaHandler.sendEmptyMessage(10);
                return;
            }else if (code == 106){/*201心跳失败*/
                mediaHandler.sendEmptyMessage(10);
                return;
            }

            if (vipstate == 1 || trystate == 1) {
                if (null != aboutlist && aboutlist.size() > 0) {
                    nextlink = aboutlist.get(position).getNextlink();
                    album = null;
                    initView();
                }
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else{
                mediaHandler.sendEmptyMessage(2);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
