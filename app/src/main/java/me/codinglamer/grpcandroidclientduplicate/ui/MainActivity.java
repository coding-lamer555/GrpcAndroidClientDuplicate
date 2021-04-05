package me.codinglamer.grpcandroidclientduplicate.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import me.codinglamer.grpcandroidclientduplicate.App;
import me.codinglamer.grpcandroidclientduplicate.R;
import me.codinglamer.grpcandroidclientduplicate.database.PostEntity;
import me.codinglamer.grpcjavaserver.PostServiceGrpc;
import me.codinglamer.grpcjavaserver.PostServiceOuterClass;

public class MainActivity extends AppCompatActivity {

    // Replace with the IPv4-address of the device on which the server was started
    // (it must be on the same network as the device on which the client will be launched)
    private static final String ADDRESS = "192.168.0.26";
    private static final int POST = 8080;

    private RecyclerView recyclerView;
    private PostsRvAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Completable.fromRunnable(() -> App.getInstance().getDb().postDao().deleteAll())
                .subscribeOn(Schedulers.io())
                .doOnError(Throwable::printStackTrace)
                .subscribe();

        initComponents();

        connectToServer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.database) {
            startActivity(new Intent(this, DatabaseActivity.class));
        }

        return true;
    }

    private void initComponents() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostsRvAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    private void connectToServer() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(ADDRESS, POST)
                .usePlaintext()
                .build();

        PostServiceGrpc.PostServiceStub stub = PostServiceGrpc.newStub(channel);

        PostServiceOuterClass.PostRequest request = PostServiceOuterClass.PostRequest
                .newBuilder()
                .build();

        stub.getPosts(request,
                new StreamObserver<PostServiceOuterClass.PostResponse>() {
                    @Override
                    public void onNext(PostServiceOuterClass.PostResponse value) {
                        runOnUiThread(() -> {
                            updateDatabase(value);
                            updateRecyclerView(value);
                        });
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                    }

                    @Override
                    public void onCompleted() {
                        channel.shutdownNow();
                    }
                });
    }

    private void updateDatabase(PostServiceOuterClass.PostResponse post) {
        PostEntity postEntity = new PostEntity();
        postEntity.id = post.getId();
        postEntity.title = post.getTitle();
        postEntity.description = post.getDescription();

        Completable.fromRunnable(() -> App.getInstance().getDb().postDao().insert(postEntity))
                .subscribeOn(Schedulers.io())
                .doOnError(Throwable::printStackTrace)
                .subscribe();
    }

    private void updateRecyclerView(PostServiceOuterClass.PostResponse post) {
        adapter.addItem(post);

        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(this) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        smoothScroller.setTargetPosition(adapter.getItemCount() - 1);
        recyclerView.getLayoutManager().startSmoothScroll(smoothScroller);
    }
}