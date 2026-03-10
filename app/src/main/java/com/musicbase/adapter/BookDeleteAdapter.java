package com.musicbase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.musicbase.R;
import com.musicbase.entity.BookVOBean;
import com.musicbase.preferences.Preferences;

import java.util.ArrayList;
import java.util.List;

public class BookDeleteAdapter extends BaseAdapter {

    private List<BookVOBean.DataBean> list = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;

    public BookDeleteAdapter(Context context, List<BookVOBean.DataBean> list, int cancel_position) {
        super();
        this.context = context;
        this.list = list;

        inflater = LayoutInflater.from(context);
    }

    public List<BookVOBean.DataBean> getList() {
        return list;
    }

    public void setList(List<BookVOBean.DataBean> list) {
        this.list = list;
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_bookall, null);
            holder.iv = (ImageView) convertView.findViewById(R.id.iv_book);
            holder.title = (TextView) convertView.findViewById(R.id.tv_book);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final BookVOBean.DataBean bean = list.get(position);
        String image_url = Preferences.IMAGE_HTTP_LOCATION + bean.getCourseImgPathVertical();
        Glide.with(context).load(image_url).into(holder.iv);

        holder.title.setText(bean.getCourseName());
        return convertView;
    }

    private class ViewHolder {
        private ImageView iv;
        private TextView title;
    }

}
