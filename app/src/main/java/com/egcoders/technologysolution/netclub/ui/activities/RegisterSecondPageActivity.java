package com.egcoders.technologysolution.netclub.ui.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.egcoders.technologysolution.netclub.R;
import com.egcoders.technologysolution.netclub.Utils.Permissions;
import com.egcoders.technologysolution.netclub.Utils.Utils;
import com.egcoders.technologysolution.netclub.data.interfaces.Register;
import com.egcoders.technologysolution.netclub.data.presenter.RegisterPresenter;
import com.egcoders.technologysolution.netclub.model.profile.UserData;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterSecondPageActivity extends AppCompatActivity implements Register.View, View.OnClickListener {

    private TextInputLayout userNameText;
    private TextInputLayout phoneText;
    private TextInputLayout birthdayText;
    private Button register;
    private CircleImageView userImage;
    private TextInputLayout emailText, passwordText, confrimPassText;
    private TextView loginText;
    private Utils utils;
    private Register.Presenter presenter;
    private static final int IMG_REQUEST = 777;
    private Bitmap bitmap = null;
    private String imagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_second_page);

        /*
            Get Permissions (READ & WRITE) for external storage
         */
        Permissions.permissionStorage(RegisterSecondPageActivity.this);
        init();
    }

    private void init() {
        userNameText = findViewById(R.id.name);
        birthdayText = findViewById(R.id.postDate);
        emailText = findViewById(R.id.email);
        phoneText = findViewById(R.id.phone);
        passwordText = findViewById(R.id.password);
        confrimPassText = findViewById(R.id.confirmPassword);
        register = findViewById(R.id.register);
        userImage = findViewById(R.id.user_image);
        loginText = findViewById(R.id.login);
        register.setOnClickListener(this);
        userImage.setOnClickListener(this);
        loginText.setOnClickListener(this);
        utils = new Utils(this);
        presenter = new RegisterPresenter(RegisterSecondPageActivity.this, this);
        birthdayText.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                Calendar currentDate = Calendar.getInstance();
                int mYear = currentDate.get(Calendar.YEAR);
                int mMonth = currentDate.get(Calendar.MONTH);
                int mDay = currentDate.get(Calendar.DAY_OF_MONTH);
                birthdayText.getEditText().setText("");
                DatePickerDialog mDatePicker = new DatePickerDialog(RegisterSecondPageActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                                Calendar myCalendar = Calendar.getInstance();
                                myCalendar.set(Calendar.YEAR, selectedyear);
                                myCalendar.set(Calendar.MONTH, selectedmonth);
                                myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                                String myFormat = "yyyy-MM-dd";
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                                birthdayText.getEditText().setText(sdf.format(myCalendar.getTime()));

                            }
                        }, mYear, mMonth, mDay);
                mDatePicker.show();
            }
        });
    }

    private Boolean missingValue(String userName, String date, String phone, String email, String pass, String confirmPass){

        if(userName.equals("") || date.equals("") || phone.equals("") || email.equals("") || pass.equals("") ||
                confirmPass.equals("") || imagePath == null){

            if(userName.equals("")){
                userNameText.setError(getString(R.string.enter_your_name));
            }
            else if(email.equals("")){
                emailText.setError(getString(R.string.enter_your_email));
            }
            else if(date.equals("")){
                birthdayText.setError(getString(R.string.enter_your_birthday));
            }
            else if(phone.equals("")){
                phoneText.setError(getString(R.string.enter_your_phone));
            }
            else if(pass.equals("")){
                passwordText.setError(getString(R.string.enter_your_password));
            }
            else if(confirmPass.equals("")){
                confrimPassText.setError(getString(R.string.enter_confrim_pass));
            }
            else if(!isEmailValid(email)){
                utils.showMessage(getString(R.string.email), getString(R.string.write_correct_email));
            }
            else if(pass.length() < 8){
                utils.showMessage(getString(R.string.password), getString(R.string.pass_characters_8));
            }
            else if(phone.length() < 11){
                utils.showMessage(getString(R.string.phone_number), getString(R.string.write_correct_phone));
            }
            else if(imagePath == null){
                utils.showMessage(getString(R.string.your_image), getString(R.string.choose_profile));
            }
            return true;
        }
        return false;
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void showMessage(String message) {

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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.register:{
                final String userName = userNameText.getEditText().getText().toString().trim();
                final String birthday = birthdayText.getEditText().getText().toString().trim();
                final String phone = phoneText.getEditText().getText().toString().trim();
                final String email = emailText.getEditText().getText().toString().trim();
                final String password = passwordText.getEditText().getText().toString().trim();
                final String confirmPass = confrimPassText.getEditText().getText().toString().trim();
                final String userStatue = getString(R.string.user);

                if(!missingValue(userName, birthday, phone, email, password, confirmPass)) {
                    if(password.equals(confirmPass)){

                        UserData user = new UserData();
                        user.setName(userName);
                        user.setBirth_date(birthday);
                        user.setPhone(phone);
                        user.setEmail(email);
                        user.setPassword(password);
                        user.setUserStatus(userStatue);

                        presenter.setUser(user, imagePath);
                    }
                    else{
                        utils.showMessage(getString(R.string.password), getString(R.string.same_pass_confim));
                        passwordText.getEditText().setText("");
                        confrimPassText.getEditText().setText("");
                    }
                }
                else {

                }
                break;
            }
            case R.id.user_image:
                chooseImage();
                break;
            case R.id.login:
                finish();
                break;
        }
    }
}
