package com.LKsmartTech.minhasfinancas.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table( name = "usuario" , schema = "financas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

      //Tive que criar o metodo construtor para o Builder não dar erro


    @Id
    @Column(name = "id")
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "email")
    private String email;

    @Column(name = "senha")
    private String senha;

//    public Usuario(Long id, String nome, String email, String senha) {
//        this.id = id;
//        this.nome = nome;
//        this.email = email;
//        this.senha = senha;
//    }
}
