package com.shenma.tvlauncher.vod.adapter;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.vod.domain.VodDataInfo;

import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IjkMediaCodecInfo;

/***
 * 影视类型适配器
 *
 * @author joychang
 *
 */
public class VodDetailsAdapter extends BaseAdapter {
    private List<VodDataInfo> vodDatas;
    private LayoutInflater mInflater;
    private ImageLoader imageLoader;
    private Context context;
    private ViewHolder holder;
    private DisplayImageOptions options;

    public VodDetailsAdapter(Context context, ArrayList<VodDataInfo> datas, ImageLoader imageLoader) {
        this.context = context;
        this.vodDatas = datas;
        this.imageLoader = imageLoader;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.default_film_img)//默认图片
                .showImageForEmptyUri(R.drawable.default_film_img)
                .showImageOnFail(R.drawable.default_film_img)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(IjkMediaCodecInfo.RANK_SECURE))
                .build();

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.mv_video_details_recommend_item, null);
            this.holder = new ViewHolder();
            this.holder.iv_details_recommend_poster = (ImageView) convertView.findViewById(R.id.details_recommend_poster);
            this.holder.tv_details_recommend_name = (TextView) convertView.findViewById(R.id.details_recommend_name);
            this.holder.tv_details_recommend_score = (TextView) convertView.findViewById(R.id.details_recommend_score);
            convertView.setTag(this.holder);
        } else {
            this.holder = (ViewHolder) convertView.getTag();
        }
        VodDataInfo vd = (VodDataInfo) this.vodDatas.get(position);
        this.imageLoader.displayImage(vd.getPic(), this.holder.iv_details_recommend_poster, this.options);
        this.holder.tv_details_recommend_name.setText(vd.getTitle());
        if (vd.getScore() != null && vd.getScore() !=""){
            this.holder.tv_details_recommend_score.setVisibility(View.VISIBLE);
            this.holder.tv_details_recommend_score.setText(vd.getScore());
        }else{
            this.holder.tv_details_recommend_score.setVisibility(View.GONE);
        }
        return convertView;
    }

    public int getCount() {
        return this.vodDatas.size();
    }

    public Object getItem(int position) {
        return this.vodDatas.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    class ViewHolder {
        private ImageView iv_details_recommend_poster;
        private TextView tv_details_recommend_name;
        private TextView tv_details_recommend_score;
        ViewHolder() {
        }
    }
}
