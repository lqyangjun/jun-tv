package com.shenma.tvlauncher.vod;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shenma.tvlauncher.Api;
import com.shenma.tvlauncher.EmpowerActivity;
import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.SettingWallpaperActivity;
import com.shenma.tvlauncher.UserActivity;
import com.shenma.tvlauncher.network.GsonRequest;
import com.shenma.tvlauncher.utils.AES;
import com.shenma.tvlauncher.utils.Constant;
import com.shenma.tvlauncher.utils.GetTimeStamp;
import com.shenma.tvlauncher.utils.Logger;
import com.shenma.tvlauncher.utils.Md5Encoder;
import com.shenma.tvlauncher.utils.Rc4;
import com.shenma.tvlauncher.utils.Rsa;
import com.shenma.tvlauncher.utils.SharePreferenceDataUtil;
import com.shenma.tvlauncher.utils.Utils;
import com.shenma.tvlauncher.vod.adapter.TypeDetailsSubMenuAdapter;
import com.shenma.tvlauncher.vod.adapter.VodtypeAdapter;
import com.shenma.tvlauncher.vod.domain.RequestVo;
import com.shenma.tvlauncher.vod.domain.VodDataInfo;
import com.shenma.tvlauncher.vod.domain.VodFilter;
import com.shenma.tvlauncher.vod.domain.VodFilterInfo;
import com.shenma.tvlauncher.vod.domain.VodTypeInfo;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shenma.tvlauncher.utils.Rc4.encry_RC4_string;

/**
 * @author joychang
 * @Description 片源选择
 */
public class VodTypeActivity extends Activity implements OnItemClickListener {
    private static final int PAGESIZE = 30;
    private static String VOD_TYPE;
    private final String TAG = "VodTypeActivity";
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    protected SharedPreferences sp;
    public static SharedPreferences Sp;
    private String VOD_DATA = "VOD_DATA";
    private String VOD_FILTER = "VOD_FILTER";
    private List<String> areas;
    private String author = "";
    private ImageView b_type_details_fliter;
    private Context context = this;
    private ListView filter_list_area;/*地区*/
    private ListView filter_list_seach;/*搜索*/
    private ListView filter_list_type;/*类型*/
    private ListView filter_list_year;/*年代*/
    private ListView filter_list_sort;/*排序*/
    private String[] sort = new String[]{"Hotdesc", "scoredesc", "updatedesc"};
    private int gHeight;
    private GridView gv_type_details_grid;
    private TextView iv_type_details_type;
    private TextView type_details_text;
    private int lastIndex = -1;
    private RequestQueue mQueue;
    private int vip;
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
                case 4:
                    tv_type_details_sum.setVisibility(View.VISIBLE);//显示多少部
                    return;
                case 5:
                    tv_type_details_sum.setVisibility(View.GONE);//显示多少部
                    return;
                case 6:
                    Utils.showToast(context,R.string.disconnect, R.drawable.toast_err);
                    startActivity(new Intent(context, UserActivity.class));
                    return;
                case 7:
                    Utils.showToast(context,R.string.Account_has_been_disabled, R.drawable.toast_err);
                    startActivity(new Intent(context, UserActivity.class));
                    return;
                case 8:
                    Utils.showToast(context,R.string.Account_information_error, R.drawable.toast_err);
                    startActivity(new Intent(context, UserActivity.class));
                    return;
                case 9:
                    Utils.showToast(context,R.string.Account_information_has_expired,R.drawable.toast_err);
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
    private LinearLayout menulayout;
    private int pageindex = 1;
    private long start;
    private int totalpage;
    private TextView tv_filter_year;
    private TextView tv_type_details_sum;
    private String type = null;
    private String typename = null;
    private List<String> types;
    private int vipstate;
    private int trystate = SharePreferenceDataUtil.getSharedIntData(this, "Trystate", 0);
    private ArrayList<VodDataInfo> vodDatas;
    private List<VodFilterInfo> vodFilter;
    private int vodpageindex;
    private VodtypeAdapter vodtypeAdapter;
    private VodTypeInfo vodtypeinfo;
    private List<String> years;
    private String filterString;

    /*创建时的回调函数*/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod);
        findViewById(R.id.vod).setBackgroundResource(R.drawable.video_details_bg);
        initIntent();
        initView();
        initData();
        initMenuData();
        sp = getSharedPreferences("shenma", 0);
        Sp = getSharedPreferences("initData", MODE_PRIVATE);
    }

    /*停止时*/
    protected void onStop() {
        super.onStop();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * 获取影视类型
     */
    private void initIntent() {
        String Api_url = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, "Api_url", ""),Constant.d);
        String BASE_HOST = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, "BASE_HOST", ""),Constant.d);
        type = getIntent().getStringExtra("TYPE");
        if (type != null) {
            VOD_TYPE = Api_url + "/api.php/"+ BASE_HOST +"/vod/?ac=list&class="+ type.toLowerCase();
            typename = getIntent().getStringExtra("TYPENAME");
        }
    }

    /**
     * 初始化
     */
    private void initView() {
        mQueue = Volley.newRequestQueue(context, new HurlStack());
        findViewById();
        loadViewLayout();
        setListener();
        processLogic("");
        gHeight = gv_type_details_grid.getHeight();
        //Logger.i("VodTypeActivity", "gHeight=" + gHeight);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        iv_type_details_type.setText(typename);
        getFilterDataFromServer();
        getVodcategory();
    }

    /*取类目公告*/
    private void getVodcategory() {
        mQueue = Volley.newRequestQueue(this, new HurlStack());
        String User_url = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.s, ""),Constant.d);
        final String RC4KEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.kd, ""),Constant.d);
        final String RSAKEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.tb, ""),Constant.d);
        final String AESKEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.um, ""),Constant.d);
        final String AESIV = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.im, ""),Constant.d);
        final String Appkey = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.yk, ""),Constant.d);
        int miType = SharePreferenceDataUtil.getSharedIntData(this, Constant.ue, 1);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, User_url + "/api.php?app=" + Api.APPID + "&act=category_notice",
                new com.android.volley.Response.Listener<String>() {
                    public void onResponse(String response) {
                        VodcategoryResponse(response);
                    }
                }, new com.android.volley.Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
//                VodGongGaoError(error);
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
                headers.put("Authorization", Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(VodTypeActivity.this, "Authorization", ""),Constant.d));  /*设置其他请求头*/
                return headers;
            }
        };
        mQueue.add(stringRequest);

    }

    /*获取成功*/
    public void VodcategoryResponse(String response) {
        String RC4KEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.kd, ""), Constant.d);
        String RSAKEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.tb, ""),Constant.d);
        String AESKEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.um, ""),Constant.d);
        String AESIV = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.im, ""),Constant.d);
        //Log.i(TAG, "RequestResponse: " + response);
        try {
            JSONObject jSONObject = new JSONObject(response);
            int code = jSONObject.optInt("code");
            String msg = jSONObject.optString("msg");
            if (code == 200){
                int miType = SharePreferenceDataUtil.getSharedIntData(this, Constant.ue, 1);
                if (miType == 1) {
                    type_details_text.setText(URLDecoder.decode(Rc4.decry_RC4(msg,RC4KEY), "UTF-8"));
                } else if (miType == 2) {
                    type_details_text.setText(URLDecoder.decode(Rsa.decrypt_Rsa(msg,RSAKEY), "UTF-8"));
                } else if (miType == 3) {
                    type_details_text.setText(URLDecoder.decode(AES.decrypt_Aes(AESKEY,msg, AESIV), "UTF-8"));
                }
                type_details_text.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化菜单数据
     */
    private void initMenuData() {
    }

    /*按ID查找视图*/
    protected void findViewById() {
        iv_type_details_type = (TextView) findViewById(R.id.type_details_type);
        type_details_text = (TextView) findViewById(R.id.type_details_text);
        tv_type_details_sum = (TextView) findViewById(R.id.type_details_sum);
        b_type_details_fliter = (ImageView) findViewById(R.id.type_details_fliter);
        gv_type_details_grid = (GridView) findViewById(R.id.type_details_grid);
        gv_type_details_grid.setSelector(new ColorDrawable(0));
        menulayout = (LinearLayout) findViewById(R.id.type_details_menulayout);
        tv_filter_year = (TextView) menulayout.findViewById(R.id.tv_filter_year);
        filter_list_type = (ListView) menulayout.findViewById(R.id.filter_list_type);
        filter_list_type.setChoiceMode(1);
        filter_list_year = (ListView) menulayout.findViewById(R.id.filter_list_year);
        filter_list_year.setChoiceMode(1);
        filter_list_area = (ListView) menulayout.findViewById(R.id.filter_list_area);
        filter_list_area.setChoiceMode(1);
        filter_list_seach = (ListView) menulayout.findViewById(R.id.filter_list_seach);
        filter_list_seach.setChoiceMode(1);
        filter_list_sort = (ListView) menulayout.findViewById(R.id.filter_list_sort);
        filter_list_sort.setChoiceMode(1);
    }

    /*键盘点击事件*/
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*按下搜索*/
        if (parent.equals(filter_list_seach)) {
            if (position == 0) {
                Intent intent = new Intent(this, SearchActivity.class);
                intent.putExtra("VOD_TYPE", VOD_TYPE);
                intent.putExtra("TYPE", type);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else if (position == 1) {
                clearFilter();
            }
        }
        /*按下类型*/
        if (parent.equals(filter_list_type)) {
            filter_list_type.setItemChecked(position, true);
            ((TypeDetailsSubMenuAdapter) filter_list_type.getAdapter()).setSelctItem(position);
            setFilterString();
        }
        /*按下年代*/
        if (parent.equals(filter_list_year)) {
            filter_list_year.setItemChecked(position, true);
            ((TypeDetailsSubMenuAdapter) filter_list_year.getAdapter()).setSelctItem(position);
            setFilterString();
        }
        /*按下地区*/
        if (parent.equals(filter_list_area)) {
            filter_list_area.setItemChecked(position, true);
            ((TypeDetailsSubMenuAdapter) filter_list_area.getAdapter()).setSelctItem(position);
            setFilterString();
        }
        /*按下排序*/
        if (parent.equals(filter_list_sort)) {
            filter_list_sort.setItemChecked(position, true);
            ((TypeDetailsSubMenuAdapter) filter_list_sort.getAdapter()).setSelctItem(position);
            setFilterString();
        }
    }

    /*加载视图布局*/
    protected void loadViewLayout() {
    }

    /**
     * 获取视频筛选信息
     */
    protected void getFilterDataFromServer() {
        String Api_url = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, "Api_url", ""),Constant.d);
        String BASE_HOST = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, "BASE_HOST", ""),Constant.d);
        RequestVo vo = new RequestVo();
        vo.context = context;
        vo.requestUrl = Api_url + "/api.php/"+ BASE_HOST + "/vod/?&ac=flitter" + "&class=" + type.toLowerCase();
        vo.type = VOD_FILTER;
        getDataFromServer(vo);
    }

    /*筛选栏*/
    private void setFilterString() {
        filterString = "";
        vodDatas = null;
        pageindex = 1;
        int s = filter_list_sort.getCheckedItemPosition();
        if (s >= 0) {
            filterString = new StringBuilder(String.valueOf(filterString)).append("&sort=") + sort[s];
        }
        int j = filter_list_area.getCheckedItemPosition();
        if (j >= 0) {
            filterString = new StringBuilder(String.valueOf(filterString)).append("&area=").append(Utils.getEcodString((String) filter_list_area.getAdapter().getItem(j))).toString();
        }
        int k = filter_list_type.getCheckedItemPosition();
        if (k >= 0) {
            filterString = new StringBuilder(String.valueOf(filterString)).append("&type=").append(Utils.getEcodString((String) filter_list_type.getAdapter().getItem(k))).toString();
        }
        int m = filter_list_year.getCheckedItemPosition();
        if (m >= 0) {
            filterString = new StringBuilder(String.valueOf(filterString)).append("&year=").append(Utils.getEcodString((String) filter_list_year.getAdapter().getItem(m))).toString();
        }
        processLogic(filterString);
    }

    /*清除过滤器*/
    private void clearFilter() {
        ((TypeDetailsSubMenuAdapter) filter_list_sort.getAdapter()).setSelctItem(-1);
        filter_list_sort.setItemChecked(-1, true);
        ((TypeDetailsSubMenuAdapter) filter_list_area.getAdapter()).setSelctItem(-1);
        filter_list_area.setItemChecked(-1, true);
        ((TypeDetailsSubMenuAdapter) filter_list_type.getAdapter()).setSelctItem(-1);
        filter_list_type.setItemChecked(-1, true);
        ((TypeDetailsSubMenuAdapter) filter_list_year.getAdapter()).setSelctItem(-1);
        filter_list_year.setItemChecked(-1, true);
        ((TypeDetailsSubMenuAdapter) filter_list_seach.getAdapter()).setSelctItem(-1);
        filter_list_seach.setItemChecked(-1, true);
        vodDatas = null;
        pageindex = 1;
        filterString = "";//清除搜索
        processLogic("");
    }

    /**
     * 获取视频列表
     */
    protected void processLogic(String filter) {
        RequestVo vo = new RequestVo();
        vo.context = context;
        vo.type = VOD_DATA;
        vo.requestUrl = VOD_TYPE + "&page=" + pageindex + filter;
        Log.d("joychang", "vo.requestUrl=" + vo.requestUrl);
        start = System.currentTimeMillis();
        getDataFromServer(vo);
    }

    /**
     * 从服务器上获取数据，并回调处理
     *
     * @param reqVo
     */
    protected void getDataFromServer(RequestVo reqVo) {
        showProgressDialog();
        if (Utils.hasNetwork(context)) {
            if (reqVo.type == VOD_DATA) {
                GsonRequest<VodTypeInfo> mVodData = new GsonRequest<VodTypeInfo>(Method.POST, reqVo.requestUrl,
                        VodTypeInfo.class, createVodDataSuccessListener(), createVodDataErrorListener()){
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
                        headers.put("Authorization", Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(VodTypeActivity.this, "Authorization", ""),Constant.d));  /*设置其他请求头*/
                        return headers;
                    }
                };
                mQueue.add(mVodData);     //     执行
            } else if (reqVo.type == VOD_FILTER) {
                GsonRequest<VodFilter> mVodData = new GsonRequest<VodFilter>(Method.POST, reqVo.requestUrl,
                        VodFilter.class, createVodFilterSuccessListener(), createVodFilterErrorListener()){
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
                        headers.put("Authorization", Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(VodTypeActivity.this, "Authorization", ""),Constant.d));  /*设置其他请求头*/
                        return headers;
                    }
                };
                mQueue.add(mVodData);     //     执行
            }
        } else {}
    }

    //数据筛选请求成功
    private Listener<VodFilter> createVodFilterSuccessListener() {
        return new Listener<VodFilter>() {
            public void onResponse(VodFilter response) {
                String Api_url = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(context, "Api_url", ""),Constant.d);
                String BASE_HOST = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(context, "BASE_HOST", ""),Constant.d);
                if (response != null) {
                    vodFilter = response.getFlitter();
                    ArrayList<String> seachs = new ArrayList<String>();
                    seachs.add("搜索");
                    seachs.add("清空筛选");
                    ArrayList<String> sorts = new ArrayList<String>();
                    sorts.add("热度优先");
                    sorts.add("评分最高");
                    sorts.add("最近更新");
                    {
                        String name;
                        if (vodFilter.size() > 0) {
                            name = vodFilter.get(0).getField();
                            if (name.equals("type")) {
                                types = Arrays.asList(vodFilter.get(0).getValues());
                            } else if (name.equals("year")) {
                                years = Arrays.asList(vodFilter.get(0).getValues());
                            } else if (name.equals("area")) {
                                areas = Arrays.asList(vodFilter.get(0).getValues());
                            }
                        }
                        if (vodFilter.size() > 1) {
                            name = vodFilter.get(1).getField();
                            if (name.equals("type")) {
                                types = Arrays.asList(vodFilter.get(1).getValues());
                            } else if (name.equals("year")) {
                                years = Arrays.asList(vodFilter.get(1).getValues());
                            } else if (name.equals("area")) {
                                areas = Arrays.asList(vodFilter.get(1).getValues());
                            }
                        }
                        if (vodFilter.size() > 2) {
                            name = vodFilter.get(2).getField();
                            if (name.equals("type")) {
                                types = Arrays.asList(vodFilter.get(2).getValues());
                            } else if (name.equals("year")) {
                                years = Arrays.asList(vodFilter.get(2).getValues());
                            } else if (name.equals("area")) {
                                areas = Arrays.asList(vodFilter.get(2).getValues());
                            }
                        }
                    }

                    if (types != null && types.size() > 0) {
                        filter_list_type.setAdapter(new TypeDetailsSubMenuAdapter(context, types));
                    }
                    if (years != null && years.size() > 0) {
                        filter_list_year.setAdapter(new TypeDetailsSubMenuAdapter(context, years));
                    }
                    if (areas != null && areas.size() > 0) {
                        filter_list_area.setAdapter(new TypeDetailsSubMenuAdapter(context, areas));
                    }
                    filter_list_seach.setAdapter(new TypeDetailsSubMenuAdapter(context, seachs));
                    filter_list_sort.setAdapter(new TypeDetailsSubMenuAdapter(context, sorts));
                }
            }
        };
    }

    private ErrorListener createVodFilterErrorListener() {
        return new ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        };
    }

    //影视数据请求成功
    private Listener<VodTypeInfo> createVodDataSuccessListener() {
        return new Listener<VodTypeInfo>() {
            public void onResponse(VodTypeInfo response) {
                closeProgressDialog();
                if (response != null) {
                    //Log.v("joychang", "Vod用时==" + (System.currentTimeMillis() - start));
                    //Logger.v("joychang", "获取数据成功!pageindex=" + pageindex);
                    if (vodDatas == null || vodDatas.size() <= 0) {
                        vodpageindex = 1;
                        vodtypeinfo = response;
                        //Logger.v("joychang", "vodtypeinfo" + vodtypeinfo.getPageindex() + "...." + vodtypeinfo.getVideonum());
                        tv_type_details_sum.setText("共" + vodtypeinfo.getVideonum() + "部");

                        int EpisodesNumber = SharePreferenceDataUtil.getSharedIntData(context, "EpisodesNumber", 0);
                        mediaHandler.sendEmptyMessage(EpisodesNumber == 1 ? 4 : 5);

                        totalpage = vodtypeinfo.getTotalpage();
                        ArrayList<VodDataInfo> vodDatalist = (ArrayList<VodDataInfo>) response
                                .getData();
                        if (vodDatalist == null || vodDatalist.size() <= 0) {
//                            pageindex = 2;
                            /*类目下空数据不闪退*/
//                            VodtypeAdapter.vodDatas.clear();
//                            vodtypeAdapter.notifyDataSetChanged();
//                            return;
                        }
                        vodDatas = vodDatalist;
                        vodtypeAdapter = new VodtypeAdapter(context, vodDatas, imageLoader);
                        gv_type_details_grid.setAdapter(vodtypeAdapter);
                        return;
                    }
                    vodtypeinfo = response;
                    ArrayList<VodDataInfo> vodDatalist = (ArrayList<VodDataInfo>) response
                            .getData();
                    if (vodDatalist != null && vodDatalist.size() > 0) {
                        vodDatas.addAll(vodDatalist);
                        VodTypeActivity vodTypeActivity = VodTypeActivity.this;
                        VodTypeActivity vodTypeActivity2 = VodTypeActivity.this;
                        int access$20 = vodTypeActivity2.vodpageindex + 1;
                        vodTypeActivity2.vodpageindex = access$20;
                        vodTypeActivity.vodpageindex = access$20;
                        vodtypeAdapter.changData(vodDatas);
                        return;
                    }
                    return;
                }
                if (vodDatas == null || vodDatas.size() <= 0) {
                    vodDatas = new ArrayList<VodDataInfo>();
                    vodtypeAdapter = new VodtypeAdapter(context, vodDatas, imageLoader);
                    gv_type_details_grid.setAdapter(vodtypeAdapter);
                    pageindex = 0;
                } else {
                    pageindex = vodpageindex;
                }
                //Logger.v("joychang", "获取数据失败!dataCallBack...pageindex=" + pageindex);
            }
        };
    }

    //影视数据请求失败
    private ErrorListener createVodDataErrorListener() {
        return new ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    //Logger.e("joychang", "请求超时");
                    Utils.showToast(context, getString(R.string.str_data_loading_error), (int) R.drawable.toast_err);
                    if (vodDatas == null || vodDatas.size() <= 0) {
                        pageindex = 0;
                    } else {
                        pageindex = vodpageindex;
                    }
                } else if (error instanceof ParseError) {
                    tv_type_details_sum.setText("共0部");
                    pageindex = 2;
                    /*类目下空数据不闪退*/
//                    VodtypeAdapter.vodDatas.clear();
//                    vodtypeAdapter.notifyDataSetChanged();
                    Utils.showToast(context, R.string.No_Content, (int) R.drawable.toast_err);
                    //Logger.e("joychang", "ParseError=" + error.toString());
                } else if (error instanceof AuthFailureError) {
                    //Logger.e("joychang", "AuthFailureError=" + error.toString());
                }
                closeProgressDialog();
            }
        };
    }

    /*集合列表*/
    protected void setListener() {
        filter_list_type.setOnItemClickListener(this);
        filter_list_year.setOnItemClickListener(this);
        filter_list_area.setOnItemClickListener(this);
        filter_list_seach.setOnItemClickListener(this);
        filter_list_sort.setOnItemClickListener(this);
        b_type_details_fliter.setOnClickListener(new OnClickListener() {
            public void onClick(View v) { showFilter(); }
        });
        gv_type_details_grid.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                menulayout.setVisibility(View.VISIBLE);
                gv_type_details_grid.clearFocus();
                gv_type_details_grid.setFocusable(false);
                return Boolean.valueOf(menulayout.requestFocus()).booleanValue();
            }
        });
        gv_type_details_grid.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String username = sp.getString("userName", null);
                if (username != null) {
                    GetMotion(position);
                } else if (username == null) {
                    Utils.showToast(context, R.string.Please_log_in_to_your_account_first , R.drawable.toast_err);
                    startActivity(new Intent(VodTypeActivity.this, UserActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });
        gv_type_details_grid.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int i = totalItemCount - visibleItemCount;
                //Logger.v("joychang", "<<<firstVisibleItem=" + firstVisibleItem + ".....i=" + i);
                if (i != 0 && firstVisibleItem >= i) {
                    pageDown();
                }
            }
        });
        gv_type_details_grid.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    /**
     * 向下翻页
     */
    private void pageDown() {
        //Logger.v("joychang", "pageindex=" + pageindex + "....vodpageindex=" + vodpageindex);
        if (pageindex < totalpage && pageindex <= vodpageindex) {
            pageindex++;
            //Logger.v("joychang", "请求页数===" + pageindex);
            if (filterString == null){
                processLogic("");
            }else {
                processLogic(filterString);
            }
//            processLogic("");
        }
    }

    /*按下键时*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Logger.i(TAG, "KeyEvent.KEYCODE_BACK");
                if (menulayout.getVisibility() == View.VISIBLE) {
                    menulayout.clearFocus();
                    menulayout.setVisibility(View.GONE);
                    gv_type_details_grid.setFocusable(true);
                    return true;
                }
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

            case KeyEvent.KEYCODE_MENU:
                showFilter();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /*显示筛选器*/
    protected void showFilter() {
        if (menulayout.getVisibility() != View.VISIBLE) {
            menulayout.setVisibility(View.VISIBLE);
            gv_type_details_grid.clearFocus();
            gv_type_details_grid.setFocusable(false);
            menulayout.requestFocus();
            Logger.i(TAG, "menulayout=GONE");
        } else {
            Logger.i(TAG, "menulayout=VISIBIE");
            menulayout.clearFocus();
            menulayout.setVisibility(View.GONE);
            gv_type_details_grid.setFocusable(true);
        }
    }

    private class WindowMessageID {
        public static final int RECOMMEND_EXPIRE = 2;
        public static final int RECOMMEND_OFFSITE = 3;
        public static final int RESPONSE_NO_SUCCESS = 1;

        private WindowMessageID() {
        }
    }

    //显示提示框
    protected void showProgressDialog() {
        Utils.loadingShow_tv(this, R.string.str_data_loading);
    }

    //关闭提示框
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
                String User_url = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.s, ""),Constant.d);
                final String RC4KEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.kd, ""),Constant.d);
                final String RSAKEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.tb, ""),Constant.d);
                final String AESKEY = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.um, ""),Constant.d);
                final String AESIV = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, Constant.im, ""),Constant.d);
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
                        headers.put("Authorization", Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(VodTypeActivity.this, "Authorization", ""),Constant.d));  /*设置其他请求头*/
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
                Intent intent = new Intent(VodTypeActivity.this, VodDetailsActivity.class);
                intent.putExtra("vodtype", type);
                intent.putExtra("vodstate", ((VodDataInfo) vodDatas.get(position)).getState());
                intent.putExtra("nextlink", ((VodDataInfo) vodDatas.get(position)).getNextlink());
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else{
                mediaHandler.sendEmptyMessage(2);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
