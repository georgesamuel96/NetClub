package com.egcoders.technologysolution.netclub.remote;

import com.egcoders.technologysolution.netclub.model.category.Category;
import com.egcoders.technologysolution.netclub.model.category.CategorySelected;
import com.egcoders.technologysolution.netclub.model.post.CreatePostResponse;
import com.egcoders.technologysolution.netclub.model.category.DeleteCategoriesResponse;
import com.egcoders.technologysolution.netclub.model.post.DeletePostResponse;
import com.egcoders.technologysolution.netclub.model.post.SavePostResponse;
import com.egcoders.technologysolution.netclub.model.profile.ForgetPasswordResponse;
import com.egcoders.technologysolution.netclub.model.post.GetPostResponse;
import com.egcoders.technologysolution.netclub.model.post.PostResponse;
import com.egcoders.technologysolution.netclub.model.category.SelectCategoryResponse;
import com.egcoders.technologysolution.netclub.model.post.UpdatePostResponse;
import com.egcoders.technologysolution.netclub.model.profile.UserResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

public interface ClientApi {

    @FormUrlEncoded
    @POST("/net-club/api/login")
    Call<UserResponse> loginUser(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("/net-club/api/category/getSelected")
    Call<CategorySelected> categorySelected(@Header("Authorization") String token, @Field("user_id") int user_id);

    @POST("/net-club/api/category/show")
    Call<Category> showCategories(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("/net-club/api/category/deleteSelected")
    Call<DeleteCategoriesResponse> deleteCategorySelected(@Header("Authorization") String token, @Field("user_id") int user_id);

    @FormUrlEncoded
    @POST("/net-club/api/category/select")
    Call<SelectCategoryResponse> selectCategory(@Header("Authorization") String token, @Field("category_id") int category_id,
                                                @Field("user_id") int user_id);

    @Multipart
    @POST("/net-club/api/register")
    Call<UserResponse> createUser(@Part("email") RequestBody email, @Part ("name")RequestBody name,
                                  @Part ("password")RequestBody password, @Part ("userStatus")RequestBody content,
                                  @Part ("birth_date")RequestBody birth_date, @Part ("phone")RequestBody phone,
                                  @Part MultipartBody.Part image);
    @FormUrlEncoded
    @POST("/net-club/api/update")
    Call<UserResponse> activateUser(@Header("Authorization") String token, @Field("activate") String activate);

    @Headers({"Accept: application/json"})
    @Multipart
    @POST("/net-club/api/post/create")
    Call<CreatePostResponse> createPost(@Header("Authorization") String token, @Part("title") RequestBody title, @Part ("description")RequestBody description,
                                        @Part ("category_id")RequestBody category_id, @Part MultipartBody.Part image);

    @FormUrlEncoded
    @POST("/net-club/api/forgetPassword")
    Call<ForgetPasswordResponse> forgetPassword(@Field("email") String email);

    @FormUrlEncoded
    @POST("/net-club/api/resetPassword")
    Call<ForgetPasswordResponse> resetPassword(@Field("code") String code, @Field("password") String password);

    @POST("/net-club/api/post/show")
    Call<PostResponse> showPosts(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("/net-club/api/post/show")
    Call<GetPostResponse> showPost(@Header("Authorization") String token, @Field("id") int id);

    @POST
    Call<PostResponse> showMorePosts(@Header("Authorization") String token, @Url String url);

    @FormUrlEncoded
    @POST("/net-club/api/post/delete")
    Call<DeletePostResponse>deletePost(@Header("Authorization") String token, @Field("id") int id);

    @Headers({"Accept: application/json"})
    @Multipart
    @POST("/net-club/api/post/update")
    Call<UpdatePostResponse> updatePost(@Header("Authorization") String token, @Part("post_id") int post_id,
                                        @Part("title") RequestBody title, @Part ("description")RequestBody description,
                                        @Part ("category_id")RequestBody category_id, @Part MultipartBody.Part image);


    @FormUrlEncoded
    @POST("/net-club/api/post/user")
    Call<PostResponse> showPostsUser(@Header("Authorization") String token, @Field("user_id") int user_id);

    @FormUrlEncoded
    @POST
    Call<PostResponse> showMorePostsUser(@Header("Authorization") String token, @Field("user_id") int user_id, @Url String url);

    @FormUrlEncoded
    @POST("/net-club/api/post/save")
    Call<SavePostResponse> savePost(@Header("Authorization") String token, @Field("post_id") int post_id);

    @FormUrlEncoded
    @POST("/net-club/api/post/unSaved")
    Call<SavePostResponse> unSavePost(@Header("Authorization") String token, @Field("post_id") int post_id);

    @POST("/net-club/api/post/getSaved")
    Call<PostResponse> getSavedPosts(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST
    Call<PostResponse> showMoreSavedPosts(@Header("Authorization") String token, @Url String url);
}
