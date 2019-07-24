package com.egcoders.technologysolution.netclub.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.Utils.Utils;
import com.egcoders.technologysolution.netclub.data.SharedPreferenceConfig;
import com.egcoders.technologysolution.netclub.data.UserPresenter;
import com.egcoders.technologysolution.netclub.data.UserProfile;
import com.egcoders.technologysolution.netclub.data.UserSharedPreference;
import com.egcoders.technologysolution.netclub.model.PostData;
import com.egcoders.technologysolution.netclub.model.UserData;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class EditUserProfileActivity extends AppCompatActivity implements UserProfile.View {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        userImage = (CircleImageView) findViewById(R.id.userProfile);
        userName = (TextInputLayout) findViewById(R.id.name);
        userEmail = (TextInputLayout) findViewById(R.id.email);
        userPhone = (TextInputLayout) findViewById(R.id.phone);
        userBirthday = (TextInputLayout) findViewById(R.id.date);
        changeBtn = (Button) findViewById(R.id.change);
        resetPass = (TextView) findViewById(R.id.resetPass);

        presenter = new UserPresenter(this, this);
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

                    if (imagePath != null) {
                        presenter.setUserDataWithPhoto(user, imagePath);
                    }
                    else {
                        presenter.setUserDataNoPhoto(user);
                    }
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
    }

    @Override
    public void showUserPosts(PostData post) {

    }

    @Override
    public void showMorePosts(PostData post) {

    }

    @Override
    public void showUserSavePosts(PostData post) {

    }

    @Override
    public void showMoreSavePosts(PostData post) {

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
            imagePath = getRealPathFromURI_API19(data.getData());

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                userImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("NewApi")
    public String getRealPathFromURI_API19(Uri uri){

        String filePath = "";

        String wholeID = DocumentsContract.getDocumentId(uri);
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
                sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }
}
