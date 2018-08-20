package com.felipemdmelo.projetosaudevacina_android.webservice.services

import com.felipemdmelo.projetosaudevacina_android.models.PostoSaude
import retrofit2.Call
import retrofit2.http.GET

interface PostoSaudeService {

    @GET("PostoSaude")
    fun listPostoSaude(): Call<List<PostoSaude>>
}