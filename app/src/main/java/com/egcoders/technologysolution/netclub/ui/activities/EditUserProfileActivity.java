package com.egcoders.technologysolution.netclub.ui.activities;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.Utils.GetImagePath;
import com.egcoders.technologysolution.netclub.data.interfaces.Message;
import com.egcoders.technologysolution.netclub.data.presenter.UserPresenter;
import com.egcoders.technologysolution.netclub.data.interfaces.UserProfile;
import com.egcoders.technologysolution.netclub.Utils.UserSharedPreference;
import com.egcoders.technologysolution.netclub.model.post.PostData;
import com.egcoders.technologysolution.netclub.model.post.SavePostData;
import com.egcoders.technologysolution.netclub.model.profile.UserData;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditUserProfileActivity extends AppCompatActivity implements UserProfile.View, Message {

    private static final String TAG = EditUserProfileActivity.class.getSimpleName();
    private CircleImageView userImage;
    private TextInputLayout userName, userPhone;
    private TextInputLayout userBirthday, userEmail;
    private Button changeBtn;
    private TextView resetPass;
    private UserProfile.Presenter presenter;
    private UserSharedPreference preference;
    private static final int IMG_REQUEST = 777;
    private Bitmap bitmap = null;
    private String imagePath = null;
    private LottieAnimationView animationView;
    private LottieAnimationView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        userImage = findViewById(R.id.userProfile);
        userName = findViewById(R.id.name);
        userEmail = findViewById(R.id.email);
        userPhone = findViewById(R.id.phone);
        userBirthday = findViewById(R.id.postDate);
        changeBtn = findViewById(R.id.change);
        resetPass = findViewById(R.id.resetPass);
        animationView = findViewById(R.id.successAnimation);
        loadingView = findViewById(R.id.loadingAnimation);

        presenter = new UserPresenter(this, this, this);
        preference = new UserSharedPreference(this);

        userEmail.getEditText().setFocusable(false);
        userEmail.getEditText().setClickable(false);

        presenter.showUserData();

        userBirthday.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                Calendar currentDate = Calendar.getInstance();
                int mYear = currentDate.get(Calendar.YEAR);
                int mMonth = currentDate.get(Calendar.MONTH);
                int mDay = currentDate.get(Calendar.DAY_OF_MONTH);
                userBirthday.getEditText().setText("");
                DatePickerDialog mDatePicker = new DatePickerDialog(EditUserProfileActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                                Calendar myCalendar = Calendar.getInstance();
                                myCalendar.set(Calendar.YEAR, selectedyear);
                                myCalendar.set(Calendar.MONTH, selectedmonth);
                                myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                                String myFormat = "yyyy-MM-dd";
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                                userBirthday.getEditText().setText(sdf.format(myCalendar.getTime()));

                            }
                        }, mYear, mMonth, mDay);
                mDatePicker.show();
            }
        });

        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserData user = new UserData();

                final String name, birthday, phone;
                name = userName.getEditText().getText().toString().trim();
                birthday = userBirthday.getEditText().getText().toString().trim();
                phone = userPhone.getEditText().getText().toString().trim();

                user.setName(name);
                user.setBirth_date(birthday);
                user.setPhone(phone);
                user.setPhoto_max(preference.getUser().getData().getPhoto_max());

                if(!missingValue(name, birthday, phone)) {
                    loadingView.setVisibility(View.VISIBLE);
                    presenter.setUserData(user, imagePath);
                }
            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditUserProfileActivity.this, ForgetPasswordActivity.class);
                intent.putExtra("email", preference.getUser().getData().getEmail());
                startActivity(intent);
            }
        });
    }

    private Boolean missingValue(String name, String date, String phone){

        if(name.equals("") || date.equals("") || phone.equals("")){

            if(name.equals("")){
                userName.setError("Enter your name");
            }
            else if(date.equals("")){
                userBirthday.setError("Enter your birthday");
            }
            else{
                userPhone.setError("Enter your phone");
            }

            return true;
        }
        return false;
    }

    @Override
    public void showUserData(UserData user) {
        userEmail.getEditText().setText(user.getEmail());
        userBirthday.getEditText().setText(user.getBirth_date());
        userPhone.getEditText().setText(user.getPhone());
        userName.getEditText().setText(user.getName());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile);
        Glide.with(EditUserProfileActivity.this).applyDefaultRequestOptions(requestOptions).load(user.getPhoto_max())
                .into(userImage);
        imagePath = user.getPhoto_max();
    }

    @Override
    public void showUserPosts(PostData post) {

    }

    @Override
    public void showMorePosts(PostData post) {

    }

    @Override
    public void showUserSavePosts(SavePostData post) {

    }

    @Override
    public void showMoreSavePosts(SavePostData post) {

    }

    private void chooseImage(){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(i.ACTION_GET_CONTENT);
        startActivityForResult(i, IMG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMG_REQUEST && resultCode == RESULT_OK  && data != null)
        {
            Uri path = data.getData();
            if (Build.VERSION.SDK_INT < 11) {
                imagePath = GetImagePath.getRealPathFromURI_BelowAPI11(getApplicationContext(), path);
            } else if (Build.VERSION.SDK_INT < 19) {
                imagePath = GetImagePath.getRealPathFromURI_API11to18(getApplicationContext(), path);
            } else {
                imagePath = GetImagePath.getRealPathFromURI_API19(getApplicationContext(), data.getData());
            }

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                userImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void successMessage(final UserData data) {
        loadingView.setVisibility(View.GONE);
        animationView.setVisibility(View.VISIBLE);
        animationView.playAnimation();
        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animationView.setVisibility(View.GONE);
                userName.getEditText().setText(data.getName());
                userPhone.getEditText().setText(data.getPhone());
                userBirthday.getEditText().setText(data.getBirth_date());
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void failMessage() {

    }
}
