package me.codinglamer.grpcandroidclientduplicate.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import me.codinglamer.grpcandroidclientduplicate.App;
import me.codinglamer.grpcandroidclientduplicate.R;
import me.codinglamer.grpcandroidclientduplicate.database.PostEntity;
import me.codinglamer.grpcjavaserver.PostServiceOuterClass;

public class DatabaseActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostsRvAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        initComponents();

        loadData();
    }

    private void initComponents() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostsRvAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    private void loadData() {
        Observable.fromCallable(() -> App.getInstance().getDb().postDao().getAll())
                .subscribeOn(Schedulers.io())
                .subscribe(this::initializeData);
    }

    private void initializeData(List<PostEntity> postEntities) {
        List<PostServiceOuterClass.PostResponse> posts = new ArrayList<>();
        for (PostEntity postEntity : postEntities) {
            PostServiceOuterClass.PostResponse post = PostServiceOuterClass.PostResponse
                    .newBuilder()
                    .setId(postEntity.id)
                    .setTitle(postEntity.title)
                    .setDescription(postEntity.description)
                    .build();
            posts.add(post);
        }

        adapter.addItems(posts);
    }
}