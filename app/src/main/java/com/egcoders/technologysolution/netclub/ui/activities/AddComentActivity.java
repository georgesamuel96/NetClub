package com.egcoders.technologysolution.netclub.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.egcoders.technologysolution.netclub.model.post.Comment;
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
    private SharedPreferenceConfig preferenceConfig;
    private ImageView addComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coment);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");

        postId = getIntent().getIntExtra("postId", -1);

        noComments = (TextView) findViewById(R.id.noComments);
        commentEditText = (EditText) findViewById(R.id.commentBtn);
        addComment = (ImageView) findViewById(R.id.addComment);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new CommentAdapter(commentList);
        recyclerView.setAdapter(adapter);

        presenter = new CommentsPresenter(this, this);

        //presenter.getComments(postId);

        preferenceConfig = new SharedPreferenceConfig(this);

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getComment();
            }
        });
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
    public void getComment() {
        String commentText = commentEditText.getText().toString().trim();
        if(commentText.equals("")){
            return;
        }
        //presenter.addComment(commentText, postId);

        Comment comment = new Comment();
        comment.setUserId(preferenceConfig.getSharedPrefConfig());
        comment.setContent(commentText);
        comment.setTimeStamp(Long.toString(System.currentTimeMillis()));
        Map<String, Object> userMap = preferenceConfig.getCurrentUser();
        comment.setUserProfile(userMap.get("profile_url").toString());
        comment.setUserProfileThumb(userMap.get("profileThumb").toString());
        comment.setUserName(userMap.get("name").toString());
        comment.setUserStatue(userMap.get("userStatue").toString());
        commentList.add(comment);
        if(commentList.size() == 1)
            noComments.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
        commentEditText.setText("");
    }
}
