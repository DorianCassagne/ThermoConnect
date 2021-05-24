package me.dcal.thermoconnectapp.Services;

import java.util.List;

import me.dcal.thermoconnectapp.Modeles.BodyTerrarium;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface SimpleService {
   @POST("connexion")
   public Call<Boolean> connexion(@Body BodyConnexion bodyConnexion);
   @POST("createuser")
   public Call<Boolean> createuser(@Body BodyConnexion bodyConnexion);
   @POST("ajoutTerrarium")
   public Call<Integer> ajoutTerrarium(@Body BodyTerrarium bodyTerrarium);
   /*@Multipart
   @POST("")
   public Call<Object> test(@Part BodyConnexion bodyConnexion);*/

   @Multipart
   @POST("upload")
   Call<Integer> upload(
           @Part("description") BodyConnexion description,
           @Part List<MultipartBody.Part> file
   );

}