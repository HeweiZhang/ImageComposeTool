package com.dd.test.puzzleview_android.activity.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dd.test.puzzleview_android.R;
import com.dd.test.puzzleview_android.activity.entity.ImageBean;
import com.dd.test.puzzleview_android.activity.view.SelectItem;

import java.util.List;

/**
 * Created by dd on 16/1/13.
 */
public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.ViewHolder> {


    private Context context;
    private List<ImageBean> picList;

    private OnRvItemClickListener onRvItemClickListener;

    public void setOnRvItemClickListener(OnRvItemClickListener onRvItemClickListener) {
        this.onRvItemClickListener = onRvItemClickListener;
    }

    public SelectAdapter(Context context, List<ImageBean> bean) {
        this.context = context;
        this.picList = bean;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = new SelectItem(context);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mImageView = ((SelectItem) view).getmImageView();
        viewHolder.deleteImg = ((SelectItem) view).getCheckImg();

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Glide.with(context)
                .load("file://" + picList.get(position).path)
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .into(holder.mImageView);

        if (onRvItemClickListener != null) {
            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRvItemClickListener.onItemClick(position);
                }
            });
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return picList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        ImageView deleteImg;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnRvItemClickListener {
        void onItemClick(int position);
    }
}
