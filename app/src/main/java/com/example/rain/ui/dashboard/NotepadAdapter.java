package com.example.rain.ui.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rain.R;
import com.example.rain.bean.NotepadBean;

import java.util.List;

public class NotepadAdapter extends RecyclerView.Adapter<NotepadAdapter.ViewHolder> {
    private LayoutInflater layoutInflater;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private OnRecyclerItemLongListener mOnItemLong = null;
    private List<NotepadBean> list;
    private static final String displayTime = "displayTime_shp";
    private static final String display = "display_shp";
    private SharedPreferences sharedPreferences;

    public NotepadAdapter(Context context, List<NotepadBean> list) {
        this.layoutInflater = LayoutInflater.from(context);
        this.list = list;
        sharedPreferences = context.getSharedPreferences(displayTime, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.notepad_item_layout, parent, false);
        if (!sharedPreferences.getBoolean(display, true)) {
            itemView.findViewById(R.id.item_time).setVisibility(View.INVISIBLE);
        }else {
            itemView.findViewById(R.id.item_time).setVisibility(View.VISIBLE);
        }
        return new ViewHolder(itemView, mOnItemClickListener, mOnItemLong);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final NotepadBean noteInfo = list.get(position);
        holder.tvNotepadContent.setText(noteInfo.getNotepadContent());
        holder.tvNotepadTime.setText(noteInfo.getNotepadTime());

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }


    // RecyclerView 没有自带的点击监听方法，所以要实现 view 的接口
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private OnRecyclerViewItemClickListener mOnItemClickListener;
        private OnRecyclerItemLongListener mOnItemLong;
        TextView tvNotepadContent;
        TextView tvNotepadTime;

        ViewHolder(View itemView, OnRecyclerViewItemClickListener mListener, OnRecyclerItemLongListener longListener) {
            super(itemView);
            this.mOnItemClickListener = mListener;
            this.mOnItemLong = longListener;
            tvNotepadContent = itemView.findViewById(R.id.item_content);
            tvNotepadTime = itemView.findViewById(R.id.item_time);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        // 单击事件
        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                //这里使用getLayoutPosition方法获取数据
                mOnItemClickListener.onItemClick(v, getLayoutPosition());

            }
        }

        // 长按事件
        @Override
        public boolean onLongClick(View v) {
            if (mOnItemLong != null) {
                mOnItemLong.onItemLongClick(v, getAdapterPosition());
            }
            return true;
        }
    }

    // 接口调用方法
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);

    }

    public interface OnRecyclerItemLongListener {
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnRecyclerItemLongListener listener) {
        this.mOnItemLong = listener;
    }
}
