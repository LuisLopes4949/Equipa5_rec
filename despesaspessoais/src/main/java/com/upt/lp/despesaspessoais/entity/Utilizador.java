package com.upt.lp.despesaspessoais.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
public class Utilizador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;

    // @JsonIgnore evita o loop infinito ao mostrar os dados
    @OneToMany(mappedBy = "utilizador", cascade = CascadeType.ALL)
    @JsonIgnore 
    private List<Despesas> despesas;

    public Utilizador() {}

    public Utilizador(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Despesas> getDespesas() { return despesas; }
    public void setDespesas(List<Despesas> despesas) { this.despesas = despesas; }
}