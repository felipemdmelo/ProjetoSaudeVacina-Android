package com.felipemdmelo.projetosaudevacina_android.webservice

import com.felipemdmelo.projetosaudevacina_android.webservice.services.PostoSaudeService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitInit {

    private val retrofit: Retrofit

    init {
        retrofit = Retrofit.Builder()
                .baseUrl("http://projetosaudevacinaapi.azurewebsites.net/api/")
                //.baseUrl("http://192.168.0.103/ProjetoSaudeVacina.API/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    fun getPostoSaudeService(): PostoSaudeService {
        return retrofit.create(PostoSaudeService::class.java)
    }
}