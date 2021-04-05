package me.codinglamer.grpcandroidclientduplicate.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.codinglamer.grpcandroidclientduplicate.R;
import me.codinglamer.grpcjavaserver.PostServiceOuterClass;

public class PostsRvAdapter extends RecyclerView.Adapter<PostsRvAdapter.PostsViewHolder> {

    private List<PostServiceOuterClass.PostResponse> posts;

    public PostsRvAdapter(List<PostServiceOuterClass.PostResponse> posts) {
        this.posts = posts;
    }

    public void addItem(PostServiceOuterClass.PostResponse post) {
        posts.add(post);
        notifyDataSetChanged();
    }

    public void addItems(List<PostServiceOuterClass.PostResponse> posts) {
        this.posts.addAll(posts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post, parent, false);
        return new PostsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
        PostServiceOuterClass.PostResponse currentPost = posts.get(position);
        holder.bind(currentPost);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class PostsViewHolder extends RecyclerView.ViewHolder {

        public final Context context;
        public final TextView title;
        public final TextView description;

        public PostsViewHolder(View itemView) {
            super(itemView);
            this.context = itemView.getContext();
            this.title = itemView.findViewById(R.id.title);
            this.description = itemView.findViewById(R.id.description);
        }

        public void bind(PostServiceOuterClass.PostResponse post) {
            title.setText(post.getTitle());
            description.setText(post.getDescription());
        }
    }
}