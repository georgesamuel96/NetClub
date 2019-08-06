package com.egcoders.technologysolution.netclub.remote;

import com.egcoders.technologysolution.netclub.model.category.Category;
import com.egcoders.technologysolution.netclub.model.category.CategorySelected;
import com.egcoders.technologysolution.netclub.model.post.CreatePostResponse;
import com.egcoders.technologysolution.netclub.model.category.DeleteCategoriesResponse;
import com.egcoders.technologysolution.netclub.model.post.DeletePostResponse;
import com.egcoders.technologysolution.netclub.model.post.SavePostResponse;
import com.egcoders.technologysolution.netclub.model.profile.ForgetPasswordResponse;
import com.egcoders.technologysolution.netclub.model.post.GetPostResponse;
import com.egcoders.technologysolution.netclub.model.post.Post;
import com.egcoders.technologysolution.netclub.model.post.PostResponse;
import com.egcoders.technologysolution.netclub.model.category.SelectCategoryResponse;
import com.egcoders.technologysolution.netclub.model.post.UpdatePostResponse;
import com.egcoders.technologysolution.netclub.model.profile.UserData;
import com.egcoders.technologysolution.netclub.model.profile.UserResponse;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {

    private static ClientApi service;
    private static ApiManager apiManager;

    private ApiManager(){

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.egcoders.net/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ClientApi.class);
    }

    public static ApiManager getInstance(){
        if(apiManager == null){
            apiManager = new ApiManager();
        }
        return apiManager;
    }

    public void loginUser(String email, String password, Callback<UserResponse> callback){
        Call<UserResponse> userCall = service.loginUser(email, password);
        userCall.enqueue(callback);
    }

    public void categorySelected(String token, int user_id, Callback<CategorySelected> callback){

        Call<CategorySelected> categoryResponse = service.categorySelected(token, user_id);
        categoryResponse.enqueue(callback);
    }

    public void showCategories(String token, Callback<Category> callback){

        Call<Category> categories = service.showCategories(token);
        categories.enqueue(callback);
    }

    public void deleteCategorySelected(String token, int user_id, Callback<DeleteCategoriesResponse> callback){
        Call<DeleteCategoriesResponse> done = service.deleteCategorySelected(token, user_id);
        done.enqueue(callback);
    }

    public void selectCategory(String token, int category_id, int user_id, Callback<SelectCategoryResponse> callback){
        Call<SelectCategoryResponse> select = service.selectCategory(token, category_id, user_id);
        select.enqueue(callback);
    }

    public void createUser(String image, UserData user, Callback<UserResponse> callback) {


        MultipartBody.Part userImage = null;

        RequestBody userEmail = RequestBody.create(MediaType.parse("multipart/form-data"), user.getEmail());
        RequestBody userName = RequestBody.create(MediaType.parse("multipart/form-data"), user.getEmail());
        RequestBody userPassword = RequestBody.create(MediaType.parse("multipart/form-data"), user.getPassword());
        RequestBody userContent = RequestBody.create(MediaType.parse("multipart/form-data"), user.getUserStatus());
        RequestBody userBirth = RequestBody.create(MediaType.parse("multipart/form-data"), user.getBirth_date());
        RequestBody userPhone = RequestBody.create(MediaType.parse("multipart/form-data"), user.getPhone());

        if(image != null){
            File file = new File(image);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            userImage = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
        }

        Call<UserResponse> userCall = service.createUser(userEmail, userName, userPassword, userContent, userBirth, userPhone, userImage);
        userCall.enqueue(callback);
    }

    public void activateUser(String token, String activate, Callback<UserResponse> callback){
        Call<UserResponse> active = service.activateUser(token, activate);
        active.enqueue(callback);
    }

    public void createPost(String token, String image, Post post, Callback<CreatePostResponse> callback){

        MultipartBody.Part postImage = null;

        RequestBody title = RequestBody.create(MediaType.parse("multipart/form-data"), post.getCategory());
        RequestBody content = RequestBody.create(MediaType.parse("multipart/form-data"), post.getContent());
        RequestBody categoryId = RequestBody.create(MediaType.parse("multipart/form-data"), post.getCategoryId() + "");

        if(image != null){
            File file = new File(image);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            postImage = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
        }

        Call<CreatePostResponse> postCall = service.createPost(token, title, content, categoryId, postImage);
        postCall.enqueue(callback);
    }

    public void forgetPassword(String email, Callback<ForgetPasswordResponse> callback){
        Call<ForgetPasswordResponse> forgetPass = service.forgetPassword(email);
        forgetPass.enqueue(callback);
    }

    public void resetPasswordPassword(String code, String password, Callback<ForgetPasswordResponse> callback){
        Call<ForgetPasswordResponse> forgetPass = service.resetPassword(code, password);
        forgetPass.enqueue(callback);
    }

    public void showPosts(String token, Callback<PostResponse> callback){
        Call<PostResponse> post = service.showPosts(token);
        post.enqueue(callback);
    }

    public void showMorePosts(String token, String url, Callback<PostResponse> callback){
        Call<PostResponse> post = service.showMorePosts(token, url);
        post.enqueue(callback);
    }

    public void deletePost(String token, int id, Callback<DeletePostResponse> callback){
        Call<DeletePostResponse> post = service.deletePost(token, id);
        post.enqueue(callback);
    }

    public void showPost(String token, int id, Callback<GetPostResponse> callback){
        Call<GetPostResponse> post = service.showPost(token, id);
        post.enqueue(callback);
    }

    public void updatePost(String token, String image, Post post, Callback<UpdatePostResponse> callback){

        MultipartBody.Part postImage = null;

        RequestBody title = RequestBody.create(MediaType.parse("multipart/form-data"), post.getCategory());
        RequestBody content = RequestBody.create(MediaType.parse("multipart/form-data"), post.getContent());
        RequestBody categoryId = RequestBody.create(MediaType.parse("multipart/form-data"), post.getCategoryId() + "");

        if(image != null){
            File file = new File(image);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            postImage = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
        }

        Call<UpdatePostResponse> postCall = service.updatePost(token, post.getId(), title, content, categoryId, postImage);
        postCall.enqueue(callback);
    }

    public void showPostsUser(String token, int id, Callback<PostResponse> callback){
        Call<PostResponse> post = service.showPostsUser(token, id);
        post.enqueue(callback);
    }

    public void showMorePostsUser(String token, int id, String url, Callback<PostResponse> callback){
        Call<PostResponse> post = service.showMorePostsUser(token, id, url);
        post.enqueue(callback);
    }

    public void savePost(String token, int id, Callback<SavePostResponse> callback){
        Call<SavePostResponse> savePost = service.savePost(token, id);
        savePost.enqueue(callback);
    }

    public void unSavePost(String token, int id, Callback<SavePostResponse> callback){
        Call<SavePostResponse> unsavePost = service.unSavePost(token, id);
        unsavePost.enqueue(callback);
    }

    public void getSavedPosts(String token, Callback<PostResponse> callback){
        Call<PostResponse> posts = service.getSavedPosts(token);
        posts.enqueue(callback);
    }
}
