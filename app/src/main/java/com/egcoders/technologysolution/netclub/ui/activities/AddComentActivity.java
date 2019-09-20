package com.egcoders.technologysolution.netclub.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.egcoders.technologysolution.netclub.Utils.UserSharedPreference;
import com.egcoders.technologysolution.netclub.Utils.Utils;
import com.egcoders.technologysolution.netclub.model.post.Comment;
import com.egcoders.technologysolution.netclub.model.profile.UserData;
import com.egcoders.technologysolution.netclub.ui.adapter.CommentAdapter;
import com.egcoders.technologysolution.netclub.data.interfaces.Comments;
import com.egcoders.technologysolution.netclub.data.presenter.CommentsPresenter;
import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.Utils.SharedPreferenceConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddComentActivity extends AppCompatActivity implements Comments.View {


    private android.support.v7.widget.Toolbar toolbar;
    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private List<Comment> commentList = new ArrayList<>();
    private Comments.Presenter presenter;
    private int postId;
    private TextView noComments;
    private EditText commentEditText;
    private UserSharedPreference preference;
    private ImageView addComment;
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coment);

        init();
    }

    private void init() {
        postId = getIntent().getIntExtra("postId", -1);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        noComments = findViewById(R.id.noComments);
        commentEditText = findViewById(R.id.commentBtn);
        addComment = findViewById(R.id.addComment);
        initRV();
        presenter = new CommentsPresenter(this, this);
        utils = new Utils(this);
        presenter.getComments(String.valueOf(postId));
        preference = new UserSharedPreference(AddComentActivity.this);
        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment.setClickable(false);
                presenter.addComment(commentEditText.getText().toString(), String.valueOf(postId));
            }
        });
    }

    private void initRV() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new CommentAdapter(commentList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showComments(List<Comment> list) {
        commentList.clear();
        if(list.size() == 0)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    noComments.setVisibility(View.VISIBLE);
                }
            });
        else
            commentList.addAll(list);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void showComment(Comment comment) {
        commentEditText.setText("");
        commentList.add(comment);
        adapter.notifyDataSetChanged();
        addComment.setClickable(true);
    }

    @Override
    public void showError(String message) {
        utils.showMessage(getString(R.string.add_comment), message);
        addComment.setClickable(true);
    }
}
