package com.example.lab6_20220270.api;

import com.example.lab6_20220270.model.RegistroRequest;
import com.example.lab6_20220270.model.RegistroResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MicroserviceApi {
    @POST("registro")
    Call<RegistroResponse> validarRegistro(@Body RegistroRequest request);
}
