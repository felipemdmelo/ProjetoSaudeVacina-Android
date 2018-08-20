package com.felipemdmelo.projetosaudevacina_android.models

import java.util.*

class PostoSaude {

    var id: Int = 0
    var nome: String = ""
    var latitude: String = ""
    var longitude: String = ""
    var isAtivo: Boolean = false
    var dataCadastro: String = ""
    var dataInativacao: String? = null
    var endereco: Endereco = Endereco()

}