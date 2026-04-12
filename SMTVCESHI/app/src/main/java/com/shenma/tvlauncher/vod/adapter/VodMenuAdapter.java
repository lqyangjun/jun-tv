package com.shenma.tvlauncher.vod.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.vod.VideoPlayerActivity;
import com.shenma.tvlauncher.vod.domain.VideoInfo;

import java.util.List;

/***
 * 播放菜单设配器
 *
 * @author joychang
 *
 */
public class VodMenuAdapter<T> extends BaseAdapter {
    private final Context context;
    private final LayoutInflater mInflater;
    private final List<T> medialist;
    private final int type;
    private boolean isMenuItemShow = false;

    public VodMenuAdapter(Context context, List<T> medialist, int type, Boolean isMenuItemShow) {
        this.context = context;
        this.medialist = medialist;
        this.type = type;
        this.isMenuItemShow = isMenuItemShow.booleanValue();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return this.medialist.size();
    }

    public Object getItem(int position) {
        return this.medialist.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.mv_controler_menu_item, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = convertView.findViewById(R.id.tv_menu_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (this.type == 0) {
            viewHolder.textView.setText(((VideoInfo) this.medialist.get(position)).title);
        } else {
            viewHolder.textView.setText((String) this.medialist.get(position));
        }
        int mPosition = 0;
        viewHolder.textView.setTextColor(this.context.getResources().getColor(R.color.white));
        if (this.isMenuItemShow) {
            switch (this.type) {
                case 0:
                    mPosition = VideoPlayerActivity.xjposition;
                    break;
                case 1:
                    mPosition = VideoPlayerActivity.bsposition;
                    break;
                case 2:
                    mPosition = VideoPlayerActivity.jmposition;
                    break;
                case 3:
                    mPosition = VideoPlayerActivity.hmblposition;
                    break;
                case 4:
                    mPosition = VideoPlayerActivity.phszposition;
                    break;
                case 5:
                    mPosition = VideoPlayerActivity.ptposition;
                    break;
                case 6:
                    mPosition = VideoPlayerActivity.pwposition;
                    break;
                case 7:
                    mPosition = VideoPlayerActivity.nhposition;
                    break;
            }
            if (mPosition == position) {
                viewHolder.textView.setTextColor(context.getResources().getColor(R.color.text_focus));
                convertView.setBackgroundResource(android.R.color.transparent);
            }else{
                convertView.setBackgroundResource(android.R.color.transparent);
            }
        }
        return convertView;
    }
}

class ViewHolder {
    public TextView textView;
}


