package com.shenma.tvlauncher.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shenma.tvlauncher.AboutActivity;
import com.shenma.tvlauncher.Api;
import com.shenma.tvlauncher.ClearActivity;
import com.shenma.tvlauncher.EmpowerActivity;
import com.shenma.tvlauncher.HistoryActivity;
import com.shenma.tvlauncher.HomeActivity;
import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.SettingPlayActivity;
import com.shenma.tvlauncher.SettingWallpaperActivity;
import com.shenma.tvlauncher.UserActivity;
import com.shenma.tvlauncher.adapter.UserTypeAdapter;
import com.shenma.tvlauncher.application.MyVolley;
import com.shenma.tvlauncher.domain.Recommend;
import com.shenma.tvlauncher.domain.RecommendInfo;
import com.shenma.tvlauncher.network.GsonRequest;
import com.shenma.tvlauncher.utils.AES;
import com.shenma.tvlauncher.utils.Constant;
import com.shenma.tvlauncher.utils.GetTimeStamp;
import com.shenma.tvlauncher.utils.Logger;
import com.shenma.tvlauncher.utils.Md5Encoder;
import com.shenma.tvlauncher.utils.Rc4;
import com.shenma.tvlauncher.utils.Rsa;
import com.shenma.tvlauncher.utils.ScaleAnimEffect;
import com.shenma.tvlauncher.utils.SharePreferenceDataUtil;
import com.shenma.tvlauncher.utils.Utils;
import com.shenma.tvlauncher.view.cornerlabelview.CornerLabelView;
import com.shenma.tvlauncher.vod.SearchActivity;
import com.shenma.tvlauncher.vod.VodDetailsActivity;
import com.shenma.tvlauncher.vod.dao.VodDao;
import com.shenma.tvlauncher.vod.db.Album;

import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.shenma.tvlauncher.utils.Rc4.encry_RC4_string;

public class RecommendFragment extends BaseFragment implements OnFocusChangeListener, OnClickListener {
    private final String TAG = "RecommendFragment";
    public ImageLoader imageLoader;
    public RequestQueue mQueue;
    public ImageView[] re_typeLogs;
    protected SharedPreferences sp;
    public static SharedPreferences Sp;
    ScaleAnimEffect animEffect;
    private List<RecommendInfo> data = null;
    private Intent i;
    private final Handler mediaHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Utils.showToast(context, R.string.request_failure, R.drawable.toast_err);
                    return;
                case 2:
                    Utils.showToast(context,R.string.Account_expiration, R.drawable.toast_err);
                    startActivity(new Intent(context, EmpowerActivity.class));
                    return;
                case 3:
                    Utils.showToast(context,R.string.disconnect, R.drawable.toast_err);
                    startActivity(new Intent(context, UserActivity.class));
                    return;
                case 4:
                    Utils.showToast(context,R.string.Account_has_been_disabled, R.drawable.toast_err);
                    startActivity(new Intent(context, UserActivity.class));
                    return;
                case 5:
                    Utils.showToast(context,R.string.request_failures, R.drawable.toast_shut);
                    return;
                case 6:
                    Utils.showToast(context,R.string.Account_information_has_expired, R.drawable.toast_err);
                    startActivity(new Intent(context, UserActivity.class));
                    return;
                case 7:
                    Utils.showToast(context,R.string.Account_information_error, R.drawable.toast_err);
                    startActivity(new Intent(context, UserActivity.class));
                    return;
                case 8:
                    Utils.showToast(context,R.string.Please_log_in_to_your_account_first, R.drawable.toast_err);
                    startActivity(new Intent(context, UserActivity.class));
                    return;
                default:
                    return;
            }
        }
    };
    private FrameLayout[] re_fls;
    private int[] re_typebgs;
    private ImageView[] rebgs;
    private TextView tv_intro = null;
    private TextView[] tvs;
    private View view;
    private int vipstate;
    private int trystate = SharePreferenceDataUtil.getSharedIntData(getActivity(), "Trystate", 0);
    private CornerLabelView  jb_1,jb_2,jb_3,jb_4,jb_5,jb_6;
    LinearLayout home_top_records;
    private VodDao dao;
    /*创建时的回调函数*/
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d("RecommendFragment", "onCreate()........");
        sp = getActivity().getSharedPreferences("shenma", 0);
        Sp = getActivity().getSharedPreferences("initData", MODE_PRIVATE);
        dao = new VodDao(getActivity());
        initHistoryData();
    }

    /*在创建视图时查看*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.d(TAG, "onCreateView()........");
        if (container == null) {
            return null;
        }
        if (null == view) {
            int Interface_Style = SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0);
            if (Interface_Style == 0){
                /*旧UI1*/
                view = inflater.inflate(R.layout.layout_recommend, container,false);
            }else if(Interface_Style == 1){
                /*新UI*/
                view = inflater.inflate(R.layout.layout_recommends, container,false);
            }else if(Interface_Style == 2){
                /*新UI圆角2*/
                view = inflater.inflate(R.layout.layout_recommendss, container,false);
            }else if(Interface_Style == 3){
                /*旧UI圆角3*/
                view = inflater.inflate(R.layout.layout_recommendsss, container,false);
            }
            init();
        } else {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            if (viewGroup != null)
                viewGroup.removeView(view);
        }
        if (data == null) {
            initData();
        }
        return view;
    }

    /*停止时*/
    @Override
    public void onStop() {
        super.onStop();
        Logger.d(TAG, "onStop()........");
        if (null != mQueue) {
            mQueue.stop();
        }
    }

    /*销毁时*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d(TAG, "onDestroy()........");
        if (null != mQueue) {
            mQueue.cancelAll(this);
        }
    }

    /*恢复时*/
    @Override
    public void onResume() {
        super.onResume();
        Logger.d(TAG, "onResume()........");
        initHistoryData();
    }

    /*分离时*/
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /*初始化*/
    private void init() {
        loadViewLayout();
        findViewById();
        setListener();
        //re_fls[0].requestFocus();
        int Interface_Style = SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0);
        if(Interface_Style == 2){
            TextView text_hist1=view.findViewById(R.id.text_hist1);
            TextView text_hist_time1=view.findViewById(R.id.text_hist_time1);
            TextView text_hist2=view.findViewById(R.id.text_hist2);
            TextView text_hist_time2=view.findViewById(R.id.text_hist_time2);
            if(Albumls!=null){
                if(Albumls.size()==1){
                    text_hist1.setText(Albumls.get(0).getAlbumTitle()+"("+Albumls.get(0).getAlbumState()+")");
                }else  if(Albumls.size()>1){
                    text_hist1.setText(Albumls.get(0).getAlbumTitle()+"("+Albumls.get(0).getAlbumState()+")");
                    text_hist2.setText(Albumls.get(1).getAlbumTitle()+"("+Albumls.get(1).getAlbumState()+")");
                }
            }
            home_top_records=view.findViewById(R.id.home_top_records);
            home_top_records.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    String username = sp.getString("userName", null);
                    if (username == null) {
                        Utils.showToast(context,R.string.Please_log_in_to_your_account_first, R.drawable.toast_err);
                        startActivity(new Intent(context, UserActivity.class));
                        return;
                    }
                    Intent intent = new Intent(getActivity(), HistoryActivity.class);
                    startActivity(intent);
                }
            });
        }

    }

    /*初始化数据*/
    private void initData() {
        String Api_url = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(getActivity(), "Api_url", ""),Constant.d);
        String BASE_HOST = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(getActivity(), "BASE_HOST", ""),Constant.d);
        mQueue = Volley.newRequestQueue(context, new HurlStack());
        imageLoader = MyVolley.getImageLoader();
        GsonRequest<Recommend> mRecommend = new GsonRequest<Recommend>(Method.POST, Api_url + "/api.php/" + BASE_HOST +"/top",
                Recommend.class, createMyReqSuccessListener(), createMyReqErrorListener()) {
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
                params.put("Style", String.valueOf(SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0)));
                return params;
            }



            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(getActivity(), "Authorization", ""),Constant.d));  /*设置其他请求头*/
                return headers;
            }
        };

        mQueue.add(mRecommend);     //     执行
    }

    /*推荐侦听器*/
    private Listener<Recommend> createMyReqSuccessListener() {
        final int Home_text_shadow = SharePreferenceDataUtil.getSharedIntData(getActivity(), "Home_text_shadow", 0);
        return new Listener<Recommend>() {
            public void onResponse(Recommend response) {
                data = response.getData();
                int paramInt = 0;
                String paramUrl;
                for (int i = 0; i < data.size(); i++) {
                    int Interface_Style = SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0);
                    if (Interface_Style == 0||Interface_Style == 1||Interface_Style == 3){
                        paramInt = i + 3;
                    }else if(Interface_Style == 2){
                        paramInt = i;
                    }
                    try {
                        tvs[i].setText(data.get(i).getTjinfo());
                    }
                    catch (Exception e){
                        return;
                    }
                    paramUrl = data.get(i).getTjpicur();
                    /*no0010*/
                    if (Home_text_shadow == 0){
                        tvs[i].setVisibility(View.VISIBLE);
                    }
                    Logger.v("joychang", "paramUrl=" + paramUrl);
                    //Logger.d(TAG, "getTjtype = "+data.get(i).getTjtype()+"...getTjid="+data.get(i).getTjid());
                    setTypeImage(paramInt, paramUrl);
                }
            }
        };
    }

    /*设置类型图像*/
    private void setTypeImage(int paramInt, String paramUrl) {
        imageLoader.get(paramUrl,
                ImageLoader.getImageListener(re_typeLogs[paramInt],
                        re_typebgs[paramInt],
                        re_typebgs[paramInt]));
    }

    /*请求失败*/
    private ErrorListener createMyReqErrorListener() {
        return new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Logger.e("joychang", "请求超时");
                } else if (error instanceof AuthFailureError) {
                    Logger.e("joychang", "AuthFailureError=" + error.toString());
                }
            }
        };
    }

    /*加载视图布局*/
    protected void loadViewLayout() {
        int Interface_Style = SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0);
        if (Interface_Style == 0 || Interface_Style == 3){
            /*旧UI*/
            re_fls = new FrameLayout[9];
            re_typeLogs = new ImageView[9];
            re_typebgs = new int[9];
            rebgs = new ImageView[9];
        }else if(Interface_Style == 1){
            /*新UI*/
            re_fls = new FrameLayout[11];
            re_typeLogs = new ImageView[11];
            re_typebgs = new int[11];
            rebgs = new ImageView[11];
        }else if(Interface_Style == 2){
            /*新UI圆角*/
            re_fls = new FrameLayout[9];
            re_typeLogs = new ImageView[9];
            re_typebgs = new int[9];
            rebgs = new ImageView[9];
        }
        if(Interface_Style == 0 || Interface_Style == 1 ||Interface_Style == 3){
            tvs = new TextView[6];
        }else if (Interface_Style == 2) {
            tvs = new TextView[9];
        }

        animEffect = new ScaleAnimEffect();
    }

    /*按ID查找视图*/
    protected void findViewById() {
        re_fls[0] = (FrameLayout) view.findViewById(R.id.fl_re_0);
        re_fls[1] = (FrameLayout) view.findViewById(R.id.fl_re_1);
        re_fls[2] = (FrameLayout) view.findViewById(R.id.fl_re_2);
        re_fls[3] = (FrameLayout) view.findViewById(R.id.fl_re_3);
        re_fls[4] = (FrameLayout) view.findViewById(R.id.fl_re_4);
        re_fls[5] = (FrameLayout) view.findViewById(R.id.fl_re_5);

        int Interface_Style = SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0);
        if(Interface_Style == 0 || Interface_Style == 1 ||Interface_Style == 3){
            re_fls[6] = (FrameLayout) view.findViewById(R.id.fl_re_6);
            re_fls[7] = (FrameLayout) view.findViewById(R.id.fl_re_7);
            re_fls[8] = (FrameLayout) view.findViewById(R.id.fl_re_8);
        }else if (Interface_Style == 2){
            /*新UI圆角*/
            re_fls[6] = (FrameLayout) view.findViewById(R.id.fl_re_6);
            re_fls[7] = (FrameLayout) view.findViewById(R.id.fl_re_7);
            re_fls[8] = (FrameLayout) view.findViewById(R.id.fl_re_8);
            //re_fls[9] = (FrameLayout) view.findViewById(R.id.fl_re_9);

        }



        re_typeLogs[0] = (ImageView) view.findViewById(R.id.iv_re_0);
        re_typeLogs[1] = (ImageView) view.findViewById(R.id.iv_re_1);
        re_typeLogs[2] = (ImageView) view.findViewById(R.id.iv_re_2);
        re_typeLogs[3] = (ImageView) view.findViewById(R.id.iv_re_3);
        re_typeLogs[4] = (ImageView) view.findViewById(R.id.iv_re_4);
        re_typeLogs[5] = (ImageView) view.findViewById(R.id.iv_re_5);

        if(Interface_Style == 0 || Interface_Style == 1 ||Interface_Style == 3){
            re_typeLogs[6] = (ImageView) view.findViewById(R.id.iv_re_6);
            re_typeLogs[7] = (ImageView) view.findViewById(R.id.iv_re_7);
            re_typeLogs[8] = (ImageView) view.findViewById(R.id.iv_re_8);
        }else if (Interface_Style == 2){
            /*新UI圆角*/
            re_typeLogs[6] = (ImageView) view.findViewById(R.id.iv_re_6);
            re_typeLogs[7] = (ImageView) view.findViewById(R.id.iv_re_7);
            re_typeLogs[8] = (ImageView) view.findViewById(R.id.iv_re_8);
            //re_typeLogs[9] = (ImageView) view.findViewById(R.id.iv_re_9);
        }


        jb_1 = (CornerLabelView) view.findViewById(R.id.jb_1);
        jb_2 = (CornerLabelView ) view.findViewById(R.id.jb_2);
        jb_3 = (CornerLabelView ) view.findViewById(R.id.jb_3);
        jb_4 = (CornerLabelView ) view.findViewById(R.id.jb_4);
        jb_5 = (CornerLabelView ) view.findViewById(R.id.jb_5);
        jb_6 = (CornerLabelView ) view.findViewById(R.id.jb_6);
        int CornerLabelView = SharePreferenceDataUtil.getSharedIntData(getActivity(), "CornerLabelView", 0);
        if (CornerLabelView == 1){
            jb_1.setVisibility(View.GONE);
            jb_2.setVisibility(View.GONE);
            jb_3.setVisibility(View.GONE);
            jb_4.setVisibility(View.GONE);
            jb_5.setVisibility(View.GONE);
            jb_6.setVisibility(View.GONE);
        }
        if(Interface_Style == 1){
            /*新UI*/
            re_fls[9] = (FrameLayout) view.findViewById(R.id.fl_re_9);
            re_fls[10] = (FrameLayout) view.findViewById(R.id.fl_re_10);
            re_typeLogs[9] = (ImageView) view.findViewById(R.id.iv_re_9);
            re_typeLogs[10] = (ImageView) view.findViewById(R.id.iv_re_10);
            rebgs[9] = (ImageView) view.findViewById(R.id.re_bg_9);
            rebgs[10] = (ImageView) view.findViewById(R.id.re_bg_9);
        }else if (Interface_Style == 2){
            /*新UI圆角*/
        }
        re_typebgs[0] = R.drawable.fl_re_1;
        re_typebgs[1] = R.drawable.fl_re_1;
        re_typebgs[2] = R.drawable.fl_re_1;
        re_typebgs[3] = R.drawable.fl_re_1;
        re_typebgs[4] = R.drawable.fl_re_1;
        re_typebgs[5] = R.drawable.fl_re_1;
        rebgs[0] = (ImageView) view.findViewById(R.id.re_bg_0);
        rebgs[1] = (ImageView) view.findViewById(R.id.re_bg_1);
        rebgs[2] = (ImageView) view.findViewById(R.id.re_bg_2);
        rebgs[3] = (ImageView) view.findViewById(R.id.re_bg_3);
        rebgs[4] = (ImageView) view.findViewById(R.id.re_bg_4);
        rebgs[5] = (ImageView) view.findViewById(R.id.re_bg_5);

        if(Interface_Style == 0 || Interface_Style == 1 ||Interface_Style == 3){
            rebgs[6] = (ImageView) view.findViewById(R.id.re_bg_6);
            rebgs[7] = (ImageView) view.findViewById(R.id.re_bg_7);
            rebgs[8] = (ImageView) view.findViewById(R.id.re_bg_8);
        }else if (Interface_Style == 2){
            /*新UI*/
            rebgs[6] = (ImageView) view.findViewById(R.id.re_bg_6);
            rebgs[7] = (ImageView) view.findViewById(R.id.re_bg_7);
            rebgs[8] = (ImageView) view.findViewById(R.id.re_bg_8);
            //rebgs[9] = (ImageView) view.findViewById(R.id.re_bg_9);

            re_typebgs[6] = R.drawable.fl_re_1;
            re_typebgs[7] = R.drawable.fl_re_1;
            re_typebgs[8] = R.drawable.fl_re_1;
            //re_typebgs[9] = R.drawable.fl_re_1;
        }




        if(Interface_Style == 0 || Interface_Style == 1 ||Interface_Style == 3){
            tvs[0] = (TextView) view.findViewById(R.id.tv_re_3);
            tvs[1] = (TextView) view.findViewById(R.id.tv_re_4);
            tvs[2] = (TextView) view.findViewById(R.id.tv_re_5);
            tvs[3] = (TextView) view.findViewById(R.id.tv_re_6);
            tvs[4] = (TextView) view.findViewById(R.id.tv_re_7);
            tvs[5] = (TextView) view.findViewById(R.id.tv_re_8);
        }else if (Interface_Style == 2){
            tvs[0] = (TextView) view.findViewById(R.id.tv_re_0);
            tvs[1] = (TextView) view.findViewById(R.id.tv_re_1);
            tvs[2] = (TextView) view.findViewById(R.id.tv_re_2);
            tvs[3] = (TextView) view.findViewById(R.id.tv_re_3);
            tvs[4] = (TextView) view.findViewById(R.id.tv_re_4);
            tvs[5] = (TextView) view.findViewById(R.id.tv_re_5);
            tvs[6] = (TextView) view.findViewById(R.id.tv_re_6);
            tvs[7] = (TextView) view.findViewById(R.id.tv_re_7);
            tvs[8] = (TextView) view.findViewById(R.id.tv_re_8);
           // tvs[9] = (TextView) view.findViewById(R.id.tv_re_9);
        }


    }

    /*没用*/
//    private int getPX(int i) {
//        return getResources().getDimensionPixelSize(i);
//    }

    /*设置侦听器*/
    protected void setListener() {
        for (int i = 0; i < re_typeLogs.length; i++) {
            re_typeLogs[i].setOnClickListener(this);
            //if(ISTV){
//				re_typeLogs[i].setOnFocusChangeListener(this);
            //}
            re_typeLogs[i].setOnFocusChangeListener(this);
            rebgs[i].setVisibility(View.GONE);
        }
    }

    /*聚焦变化时*/
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int paramInt = 0;
        switch (v.getId()) {
            case R.id.iv_re_0:
                paramInt = 0;
                break;
            case R.id.iv_re_1:
                paramInt = 1;
                break;
            case R.id.iv_re_2:
                paramInt = 2;
                break;
            case R.id.iv_re_3:
                paramInt = 3;
                break;
            case R.id.iv_re_4:
                paramInt = 4;
                break;
            case R.id.iv_re_5:
                paramInt = 5;
                break;
            case R.id.iv_re_6:
                paramInt = 6;
                break;
            case R.id.iv_re_7:
                paramInt = 7;
                break;
            case R.id.iv_re_8:
                paramInt = 8;
                break;
            case R.id.iv_re_9:
                paramInt = 9;
                break;
            case R.id.iv_re_10:
                paramInt = 10;
                break;
        }
        if (hasFocus) {
            showOnFocusTranslAnimation(paramInt);
            if (null != home.whiteBorder) {
                home.whiteBorder.setVisibility(View.VISIBLE);
            }
            flyAnimation(paramInt);
        } else {
            showLooseFocusTranslAinimation(paramInt);
        }
        for (TextView tv : tvs) {
            if (tv.getVisibility() != View.GONE) {
                tv.setVisibility(View.GONE);
            }
        }

    }
    private List<Album> Albumls = null;
    private void initHistoryData() {
        Albumls = dao.queryAllAppsByType(2);
        //Collections.reverse(Albumls);//将影片内容倒序
    }
    /**
     * 飞框焦点动画
     *
     * @param paramInt
     */
    private void flyAnimation(int paramInt) {
        int[] location = new int[2];
        re_typeLogs[paramInt].getLocationOnScreen(location);
        int width = re_typeLogs[paramInt].getWidth();
        int height = re_typeLogs[paramInt].getHeight();
        float x = (float) location[0];
        float y = (float) location[1];
        Logger.v("joychang", "paramInt=" + paramInt + "..x=" + x + "...y=" + y);
        switch (paramInt) {
            case 0:
                if (mHeight > 1000 && mWidth > 1000) {
                    //1080p
                    x = getResources().getDimensionPixelSize(R.dimen.sm_49);
                    y = getResources().getDimensionPixelSize(R.dimen.sm_190) - 3;
                } else {
                    x = getResources().getDimensionPixelSize(R.dimen.sm_21);
                    y = getResources().getDimensionPixelSize(R.dimen.sm_164);
                }
                break;
            case 1:
                if (mHeight > 1000 && mWidth > 1000) {
                    //1080p
                    y = getResources().getDimensionPixelSize(R.dimen.sm_310) + 14;
                    x = getResources().getDimensionPixelSize(R.dimen.sm_49);
                } else {
                    y = 298;
                    x = getResources().getDimensionPixelSize(R.dimen.sm_21);
                }
                //y = getResources().getDimensionPixelSize(R.dimen.sm_316);
                break;
            case 2:
                if (mHeight > 1000 && mWidth > 1000) {
                    //1080p
                    x = getResources().getDimensionPixelSize(R.dimen.sm_49);
                    y = getResources().getDimensionPixelSize(R.dimen.sm_450) - 1;
                } else {
                    x = 42 - 21;
                    y = 425 + 4;
                }
                break;
            case 3:
                if (mHeight > 1000 && mWidth > 1000) {
                    //1080p
                    width = width + 24 + 14;
                    height = height + 13 + 8;
                    x = getResources().getDimensionPixelSize(R.dimen.sm_370) - 2;
                    y = getResources().getDimensionPixelSize(R.dimen.sm_252) + 1;
                } else {
                    width = width + 24;
                    height = height + 16;
                    x = (float) 188 + 154;
                    y = (float) 189 + 40;
                }
                break;
            case 4:
                if (mHeight > 1000 && mWidth > 1000) {
                    //1080p
                    width = width + 13 + 6;
                    height = height + 7 + 5;
                    x = getResources().getDimensionPixelSize(R.dimen.sm_246) - 2;
                    y = getResources().getDimensionPixelSize(R.dimen.sm_456) + 12;
                } else {
                    width = width + 13;
                    height = height + 8;
                    x = (float) 188 + 28;
                    y = (float) 436 + 8;
                }
                break;
            case 5:
                if (mHeight > 1000 && mWidth > 1000) {
                    //1080p
                    width = width + 13 + 6;
                    height = height + 7 + 5;
                    x = getResources().getDimensionPixelSize(R.dimen.sm_481) + 2;
                    y = getResources().getDimensionPixelSize(R.dimen.sm_456) + 12;
                } else {
                    width = width + 13;
                    height = height + 8;
                    x = (float) 420 + 38;
                    y = (float) 436 + 8;
                }
                break;
            case 6:
                if (mHeight > 1000 && mWidth > 1000) {
                    //1080p
                    width = width + 15 + 8;
                    height = height + 22 + 13;
                    x = getResources().getDimensionPixelSize(R.dimen.sm_746) + 3;
                    y = getResources().getDimensionPixelSize(R.dimen.sm_320) + 9;
                } else {
                    width = width + 18;
                    height = height + 26;
                    x = (float) 654 + 75;
                    y = (float) 189 + 115;
                }
                break;
            case 7:
                if (mHeight > 1000 && mWidth > 1000) {
                    //1080p
                    width = width + 17 + 10;
                    height = height + 12 + 5;
                    x = getResources().getDimensionPixelSize(R.dimen.sm_1000) + 73;
                    y = getResources().getDimensionPixelSize(R.dimen.sm_220) + 1;
                } else {
                    width = width + 17;
                    height = height + 14;
                    x = (float) 924 + 111;
                    y = (float) 189 + 8;
                }
                break;
            case 8:
                if (mHeight > 1000 && mWidth > 1000) {
                    //1080p
                    width = width + 17 + 10;
                    height = height + 12 + 5;
                    x = getResources().getDimensionPixelSize(R.dimen.sm_1000) + 73;
                    y = getResources().getDimensionPixelSize(R.dimen.sm_435) - 2;
                } else {
                    width = width + 17;
                    height = height + 14;
                    x = (float) 924 + 111;
                    y = (float) 394 + 18;
                }
                break;

        }
        Logger.d(TAG, "X=" + x + "---Y=" + y);
        home.flyWhiteBorder(width, height, x, y);
    }

    /**
     *显示焦点平移动画
     *
     * @param paramInt
     */
    private void showOnFocusTranslAnimation(int paramInt) {

        re_fls[paramInt].bringToFront();//将当前FrameLayout置为顶层
        Animation mtAnimation = null;
        Animation msAnimation = null;

        int Interface_Style = SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0);
        if (Interface_Style == 0||Interface_Style == 3){
            /*旧UI*/
            switch (paramInt) {
                case 0:
                    mtAnimation = animEffect.translAnimation(0.0f, -20.0f, 0.0f, -5.0f);
                    break;
                case 1:
                    mtAnimation = animEffect.translAnimation(0.0f, -20.0f, 0.0f, 1.0f);
                    break;
                case 2:
                    mtAnimation = animEffect.translAnimation(0.0f, -20.0f, 0.0f, 5.0f);
                    break;
                case 3:
                    mtAnimation = animEffect.translAnimation(0.0f, -10.0f, 0.0f, -5.0f);
                    break;
                case 4:
                    mtAnimation = animEffect.translAnimation(0.0f, -20.0f, 0.0f, 5.0f);
                    break;
                case 5:
                    mtAnimation = animEffect.translAnimation(0.0f, -10.0f, 0.0f, 5.0f);
                    break;
                case 6:
                    mtAnimation = animEffect.translAnimation(0.0f, 10.0f, 0.0f, 0.0f);
                    break;
                case 7:
                    mtAnimation = animEffect.translAnimation(0.0f, 20.0f, 0.0f, -5.0f);
                    break;
                case 8:
                    mtAnimation = animEffect.translAnimation(0.0f, 20.0f, 0.0f, 5.0f);
                    break;
                default:
                    break;
            }
        }else if(Interface_Style == 1){
            /*新UI*/
            switch (paramInt) {
                case 0:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 1:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 2:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 3:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 4:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 5:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 6:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 7:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 8:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 9:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 10:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                default:
                    break;
            }
        }else if(Interface_Style == 2){
            /*新UI圆角*/
            switch (paramInt) {
                case 0:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 1:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 2:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 3:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 4:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 5:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 6:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 7:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 8:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 9:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 10:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                default:
                    break;
            }
        }

        msAnimation = animEffect.ScaleAnimation(1.0F, 1.05F, 1.0F, 1.05F);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(msAnimation);
        set.addAnimation(mtAnimation);
        set.setFillAfter(true);
//		set.setFillEnabled(true);
        set.setAnimationListener(new MyOnFocusAnimListenter(paramInt));
//		ImageView iv = re_typeLogs[paramInt];
//		iv.setAnimation(set);
//		set.startNow(); TODO
        re_fls[paramInt].startAnimation(set);
        //re_fls[paramInt].startAnimation(set);

    }

    /**
     * 失去焦点缩小
     *
     * @param paramInt
     */
    private void showLooseFocusTranslAinimation(int paramInt) {
        Animation mAnimation = null;
        Animation mtAnimation = null;
        Animation msAnimation = null;
        AnimationSet set = null;
        int Interface_Style = SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0);
        if (Interface_Style == 0||Interface_Style == 3){
            /*旧UI*/
            switch (paramInt) {
                case 0:
                    mtAnimation = animEffect.translAnimation(-20.0f, 0.0f, -5.0f, 0.0f);
                    break;
                case 1:
                    mtAnimation = animEffect.translAnimation(-20.0f, 0.0f, 1.0f, 0.0f);
                    break;
                case 2:
                    mtAnimation = animEffect.translAnimation(-20.0f, 0.0f, 5.0f, 0.0f);
                    break;
                case 3:
                    mtAnimation = animEffect.translAnimation(-10.0f, 0.0f, -5.0f, 0.0f);
                    break;
                case 4:
                    mtAnimation = animEffect.translAnimation(-20.0f, 0.0f, 5.0f, 0.0f);
                    break;
                case 5:
                    mtAnimation = animEffect.translAnimation(-10.0f, 0.0f, 5.0f, 0.0f);
                    break;
                case 6:
                    mtAnimation = animEffect.translAnimation(10.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 7:
                    mtAnimation = animEffect.translAnimation(20.0f, 0.0f, -5.0f, 0.0f);
                    break;
                case 8:
                    mtAnimation = animEffect.translAnimation(20.0f, 0.0f, 5.0f, 0.0f);
                    break;

                default:
                    break;

            }
        }else if(Interface_Style == 1){
            /*新UI*/
            switch (paramInt) {
                case 0:
                    //mtAnimation = animEffect.translAnimation(-20.0f, 0.0f, -5.0f, 0.0f);
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 1:
                    //mtAnimation = animEffect.translAnimation(-20.0f, 0.0f, 1.0f, 0.0f);
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 2:
                    //mtAnimation = animEffect.translAnimation(-20.0f, 0.0f, 5.0f, 0.0f);
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 3:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    //mtAnimation = animEffect.translAnimation(-10.0f, 0.0f, -5.0f, 0.0f);
                    break;
                case 4:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    //mtAnimation = animEffect.translAnimation(-20.0f, 0.0f, 5.0f, 0.0f);
                    break;
                case 5:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    //mtAnimation = animEffect.translAnimation(-10.0f, 0.0f, 5.0f, 0.0f);
                    break;
                case 6:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    //mtAnimation = animEffect.translAnimation(10.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 7:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    //mtAnimation = animEffect.translAnimation(20.0f, 0.0f, -5.0f, 0.0f);
                    break;
                case 8:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    //mtAnimation = animEffect.translAnimation(20.0f, 0.0f, 5.0f, 0.0f);
                    break;
                case 9:///
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 10:///
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                default:
                    break;

            }
        }else if(Interface_Style == 2){
            /*新UI圆角*/
            switch (paramInt) {
                case 0:
                    //mtAnimation = animEffect.translAnimation(-20.0f, 0.0f, -5.0f, 0.0f);
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 1:
                    //mtAnimation = animEffect.translAnimation(-20.0f, 0.0f, 1.0f, 0.0f);
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 2:
                    //mtAnimation = animEffect.translAnimation(-20.0f, 0.0f, 5.0f, 0.0f);
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 3:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    //mtAnimation = animEffect.translAnimation(-10.0f, 0.0f, -5.0f, 0.0f);
                    break;
                case 4:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    //mtAnimation = animEffect.translAnimation(-20.0f, 0.0f, 5.0f, 0.0f);
                    break;
                case 5:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    //mtAnimation = animEffect.translAnimation(-10.0f, 0.0f, 5.0f, 0.0f);
                    break;
                case 6:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    //mtAnimation = animEffect.translAnimation(10.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 7:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    //mtAnimation = animEffect.translAnimation(20.0f, 0.0f, -5.0f, 0.0f);
                    break;
                case 8:
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    //mtAnimation = animEffect.translAnimation(20.0f, 0.0f, 5.0f, 0.0f);
                    break;
                case 9:///
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                case 10:///
                    mtAnimation = animEffect.translAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    break;
                default:
                    break;

            }
        }

        msAnimation = animEffect.ScaleAnimation(1.05F, 1.0F, 1.05F, 1.0F);
        set = new AnimationSet(true);
        set.addAnimation(msAnimation);
        set.addAnimation(mtAnimation);
        set.setFillAfter(true);
//		set.setFillEnabled(true);
        set.setAnimationListener(new MyLooseFocusAnimListenter(paramInt));
//		ImageView iv = re_typeLogs[paramInt];
//		iv.setAnimation(set);
//		set.startNow();
//		mAnimation.setAnimationListener(new MyLooseFocusAnimListenter(paramInt));
        rebgs[paramInt].setVisibility(View.GONE);
        re_fls[paramInt].startAnimation(set);
    }

    /**
     * 根据状态来下载或者打开app
     *
     * @param apkurl
     * @param packName
     * @author drowtram
     */
    private void startOpenOrDownload(String apkurl, String packName, String fileName) {
        //判断当前应用是否已经安装
        for (PackageInfo pack : home.packLst) {
            if (pack.packageName.equals(packName)) {
                //已安装了apk，则直接打开
                Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(packName);
                startActivity(intent);
                return;
            }
        }
        //如果没有安装，则查询本地是否有安装包文件，有则直接安装
        if (!Utils.startCheckLoaclApk(home, fileName)) {
            //如果没有安装包  则进行下载安装
            Utils.startDownloadApk(home, apkurl, null);
        }
    }

    /*单击*/
    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.iv_re_0:
                String username = sp.getString("userName", null);
                if (SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 0||SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 3){
                    /*旧UI搜索*/
                    if (username == null && username == null) {
                        mediaHandler.sendEmptyMessage(8);
                        break;
                    }
                    i = new Intent();
                    i.setClass(home, SearchActivity.class);
                    i.putExtra("TYPE", "ALL");
                    startActivity(i);

                    break;
                }else if(SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 1){
                    /*新UI个人中心*/
                    i = new Intent();
                    i.setClass(home, UserActivity.class);
                    startActivity(i);
                }else if(SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 2){
                    /*新UI圆角推荐1*/
                    username = sp.getString("userName", null);
                    if (username == null && username == null) {
                        mediaHandler.sendEmptyMessage(8);
                        break;
                    }
                    GetMotion(0);
                }
                break;
            case R.id.iv_re_1:

                if (SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 0 || SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 3){
                    /*旧UI个人中心*/
                    i = new Intent();
                    i.setClass(home, UserActivity.class);
                    startActivity(i);
                }else if(SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 1){
                    /*新UI开通会员-个性主题*/
//                    username = sp.getString("userName", null);
//                    if (username == null) {
//                        if (username == null) {
//                            Utils.showToast("请先登录账号!",context, R.drawable.toast_err);
//                            startActivity(new Intent(context, UserActivity.class));
//                            break;
//                        }
//                    }
                    i = new Intent();
//                    i.setClass(home, EmpowerActivity.class);
                    i.setClass(home, SettingWallpaperActivity.class);
                    startActivity(i);
                }else if(SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 2){
                    /*新UI圆角推荐2*/
                    username = sp.getString("userName", null);
                    if (username == null && username == null) {
                        mediaHandler.sendEmptyMessage(8);
                        break;
                    }
                    GetMotion(1);
                }

                break;
            case R.id.iv_re_2:
                if (SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 0 || SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 3){
                    /*旧UI历史记录*/
                    username = sp.getString("userName", null);
                    if (username == null && username == null) {
                        mediaHandler.sendEmptyMessage(8);
                        break;
                    }
                    startActivity(new Intent(context, HistoryActivity.class));
                }else if(SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 1){
                    /*新UI播放设置*/
                    i = new Intent();
                    i.setClass(home, SettingPlayActivity.class);
                    startActivity(i);
                }else if(SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 2){
                    /*新UI圆角推荐3*/
                    username = sp.getString("userName", null);
                    if (username == null && username == null) {
                        mediaHandler.sendEmptyMessage(8);
                        break;
                    }
                    GetMotion(2);
                }

                break;
            case R.id.iv_re_3:
//                username = sp.getString("userName", null);
//                if (username == null && username == null) {
//                    mediaHandler.sendEmptyMessage(8);
//                    break;
//                }
//                /*推荐一*/
//                GetMotion(0);

                if (SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 0 || SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 3){
                    /*旧UI推荐1*/
                    username = sp.getString("userName", null);
                    if (username == null && username == null) {
                        mediaHandler.sendEmptyMessage(8);
                        break;
                    }
                    /*推荐一*/
                    GetMotion(0);
                }else if(SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 1){
                    /*新ui推荐1*/
                    username = sp.getString("userName", null);
                    if (username == null && username == null) {
                        mediaHandler.sendEmptyMessage(8);
                        break;
                    }
                    /*推荐一*/
                    GetMotion(0);
                }else if(SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 2){
                    /*新UI圆角推荐4*/
                    username = sp.getString("userName", null);
                    if (username == null && username == null) {
                        mediaHandler.sendEmptyMessage(8);
                        break;
                    }
                    GetMotion(3);
                }
                break;
            case R.id.iv_re_4:
//                username = sp.getString("userName", null);
//                if (username == null && username == null) {
//                    mediaHandler.sendEmptyMessage(8);
//                    break;
//                }
//                /*推荐二*/
//                GetMotion(1);
                if (SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 0 || SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 3){
                    /*旧UI推荐2*/
                    username = sp.getString("userName", null);
                    if (username == null && username == null) {
                        mediaHandler.sendEmptyMessage(8);
                        break;
                    }
                    /*推荐二*/
                    GetMotion(1);
                }else if(SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 1){
                    username = sp.getString("userName", null);
                    if (username == null && username == null) {
                        mediaHandler.sendEmptyMessage(8);
                        break;
                    }
                    /*推荐二*/
                    GetMotion(1);
                }else if(SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 2){
                    /*新UI圆角推荐5*/
                    username = sp.getString("userName", null);
                    if (username == null && username == null) {
                        mediaHandler.sendEmptyMessage(8);
                        break;
                    }
                    GetMotion(4);
                }
                break;
            case R.id.iv_re_5:
//                username = sp.getString("userName", null);
//                if (username == null && username == null) {
//                    mediaHandler.sendEmptyMessage(8);
//                    break;
//                }
//                /*推荐三*/
//                GetMotion(2);
                if (SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 0 || SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 3){
                    /*旧UI推荐3*/
                    username = sp.getString("userName", null);
                    if (username == null && username == null) {
                        mediaHandler.sendEmptyMessage(8);
                        break;
                    }
                    /*推荐三*/
                    GetMotion(2);
                }else if(SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 1){
                    username = sp.getString("userName", null);
                    if (username == null && username == null) {
                        mediaHandler.sendEmptyMessage(8);
                        break;
                    }
                    /*推荐三*/
                    GetMotion(2);
                }else if(SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 2){
                    /*新UI圆角推荐6*/
                    username = sp.getString("userName", null);
                    if (username == null && username == null) {
                        mediaHandler.sendEmptyMessage(8);
                        break;
                    }
                    GetMotion(5);
                }
                break;
            case R.id.iv_re_6:
                username = sp.getString("userName", null);
                if (username == null && username == null) {
                    mediaHandler.sendEmptyMessage(8);
                    break;
                }
                if(SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 2){
                    /*推荐四*/
                    GetMotion(6);
                }else {
                    /*推荐四*/
                    GetMotion(3);
                }


                break;
            case R.id.iv_re_7:

                username = sp.getString("userName", null);
                if (username == null && username == null) {
                    mediaHandler.sendEmptyMessage(8);
                    break;
                }
                if(SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 2){
                    /*推荐五*/
                    GetMotion(7);
                }else {
                    /*推荐五*/
                    GetMotion(4);
                }

                break;
            case R.id.iv_re_8:
                username = sp.getString("userName", null);
                if (username == null && username == null) {
                    mediaHandler.sendEmptyMessage(8);
                    break;
                }
                if(SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0) == 2){
                    /*推荐六*/
                    GetMotion(8);
                }else {
                    /*推荐六*/
                    GetMotion(5);
                }

                break;
            case R.id.iv_re_9:
                /*新ui清理记录*/
                i = new Intent();
                i.setClass(home, ClearActivity.class);
                startActivity(i);
                break;
            case R.id.iv_re_10:
                /*新ui关于我们*/
                i = new Intent();
                i.setClass(home, AboutActivity.class);
                startActivity(i);
                break;
        }
        home.overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

    private class WindowMessageID {
        public static final int RECOMMEND_EXPIRE = 2;
        public static final int RECOMMEND_OFFSITE = 3;
        public static final int RESPONSE_NO_SUCCESS = 1;

        private WindowMessageID() {
        }
    }

    /**
     * 获取焦点时动画监听
     *
     * @author joychang
     */
    public class MyOnFocusAnimListenter implements Animation.AnimationListener {

        private int paramInt;

        public MyOnFocusAnimListenter(int paramInt) {
            this.paramInt = paramInt;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Logger.v("joychang", "onAnimationEnd");
            rebgs[paramInt].setVisibility(View.VISIBLE);
//			Animation localAnimation =animEffect
//					.alphaAnimation(0.0F, 1.0F, 150L, 0L);
//			localImageView.startAnimation(localAnimation);

            int Interface_Style = SharePreferenceDataUtil.getSharedIntData(getActivity(), "Interface_Style", 0);
            if (Interface_Style == 0||Interface_Style == 1||Interface_Style == 3){
                if (paramInt >= 3 && paramInt <= 8) {
                    tvs[paramInt - 3].setVisibility(View.VISIBLE);
                }
            }else if(Interface_Style == 2){
                tvs[paramInt].setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

    }

    /**
     * 获取焦点时动画监听
     *
     * @author joychang
     */
    public class MyLooseFocusAnimListenter implements Animation.AnimationListener {

        private int paramInt;

        public MyLooseFocusAnimListenter(int paramInt) {
            this.paramInt = paramInt;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Logger.v("joychang", "onAnimationEnd");
//			Animation localAnimation =animEffect
//					.alphaAnimation(0.0F, 1.0F, 150L, 0L);
//			localImageView.startAnimation(localAnimation);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

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
                mQueue = Volley.newRequestQueue(getActivity(), new HurlStack());
                String User_url = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(getActivity(), Constant.s, ""),Constant.d);
                final String RC4KEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(getActivity(), Constant.kd, ""),Constant.d);
                final String RSAKEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(getActivity(), Constant.tb, ""),Constant.d);
                final String AESKEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(getActivity(), Constant.um, ""),Constant.d);
                final String AESIV = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(getActivity(), Constant.im, ""),Constant.d);
                final String Appkey = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(getActivity(), Constant.yk, ""),Constant.d);
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
                        int miType = SharePreferenceDataUtil.getSharedIntData(getActivity(), Constant.ue, 1);
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
                        headers.put("Authorization", Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(getActivity(), "Authorization", ""),Constant.d));  /*设置其他请求头*/
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
        String RC4KEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(getActivity(), Constant.kd, ""), Constant.d);
        String RSAKEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(getActivity(), Constant.tb, ""),Constant.d);
        String AESKEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(getActivity(), Constant.um, ""),Constant.d);
        String AESIV = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(getActivity(), Constant.im, ""),Constant.d);
        //Log.i(TAG, "GetMotionResponse: " + response);
        try {
            JSONObject jSONObject = new JSONObject(response);
            int code = jSONObject.optInt("code");
            if (code == 200){
                int miType = SharePreferenceDataUtil.getSharedIntData(getActivity(), Constant.ue, 1);
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
                mediaHandler.sendEmptyMessage(3);
                sp.edit().putString("userName", null).putString("passWord", null).putString("vip", null).putString("fen", null).putString("ckinfo", null).commit();
                return;
            }else if (code == 114) {/*账户封禁*/
                mediaHandler.sendEmptyMessage(4);
                sp.edit().putString("userName", null).putString("passWord", null).putString("vip", null).putString("fen", null).putString("ckinfo", null).commit();
                return;
            }else if (code == 125) {/*账户信息错误*/
                mediaHandler.sendEmptyMessage(7);
                sp.edit().putString("userName", null).putString("passWord", null).putString("vip", null).putString("fen", null).putString("ckinfo", null).commit();
                return;
            }else if (code == 127) {/*账户信息失效*/
                mediaHandler.sendEmptyMessage(6);
                sp.edit().putString("userName", null).putString("passWord", null).putString("vip", null).putString("fen", null).putString("ckinfo", null).commit();
                return;
            }else if (code == 201){/*201心跳失败*/
                mediaHandler.sendEmptyMessage(5);
                return;
            }else if (code == 106){/*201心跳失败*/
                mediaHandler.sendEmptyMessage(5);
                return;
            }
            if (vipstate == 1 || trystate == 1) {
                i = new Intent();
                i.setClass(home, VodDetailsActivity.class);
                i.putExtra("nextlink", data.get(position).getTjurl());
                i.putExtra("vodstate", data.get(position).getState());
                i.putExtra("vodtype", data.get(position).getTjtype().toUpperCase());
                startActivity(i);
            }else{
                mediaHandler.sendEmptyMessage(2);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
