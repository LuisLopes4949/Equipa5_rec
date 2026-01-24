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
    
    @Column(unique = true)
    private String email;

 
    private String password; 

    @OneToMany(mappedBy = "utilizador")
    @JsonIgnore 
    private List<Despesas> despesas;


    public Utilizador() {}

  
    public Utilizador(String nome, String email, String password) {
        this.nome = nome;
        this.email = email;
        this.password = password;
    }

    // --- GETTERS E SETTERS ---
    public Long getId() { 
    	return id; 
    	}
    public void setId(Long id) { 
    	this.id = id; 
    	}

    public String getNome() { 
    	return nome; 
    	}
    public void setNome(String nome) { 
    	this.nome = nome; 
    	}

    public String getEmail() { 
    	return email; 
    	}
    public void setEmail(String email) { 
    	this.email = email; 
    	}

    public String getPassword() { 
    	return password; 
    	}
    public void setPassword(String password) { 
    	this.password = password; 
    	}

    public List<Despesas> getDespesas() { 
    	return despesas; 
    	}
    public void setDespesas(List<Despesas> despesas) { 
    	this.despesas = despesas; 
    	}
}