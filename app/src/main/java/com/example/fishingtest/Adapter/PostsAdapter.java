package com.example.fishingtest.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fishingtest.Model.Post;
import com.example.fishingtest.R;

import java.util.ArrayList;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter{
    List<Post> myPostsData = new ArrayList<>();
    Context mContext;

    public PostsAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.cardview_posts_items, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        MyViewHolder mHolder = (MyViewHolder) viewHolder;
        Post post = myPostsData.get(i);
        mHolder.textView.setText(post.getMeasuredData());
    }

    @Override
    public int getItemCount() {
        return myPostsData.size();
    }

    public void refreshItems(List<Post> items) {
        myPostsData.clear();
        myPostsData.addAll(items);
        notifyDataSetChanged();
    }

    public void addItems(List<Post> items) {
        myPostsData.addAll(items);
    }
    public void addItem(Post item){
        myPostsData.add(item);
        notifyDataSetChanged();
    }
    public void deleteItem(int position) {
        myPostsData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, myPostsData.size() - 1);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.post_content);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,"第"+getLayoutPosition()+"项被选中",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
