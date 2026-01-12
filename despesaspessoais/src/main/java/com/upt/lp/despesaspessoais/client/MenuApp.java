package com.upt.lp.despesaspessoais.client;

import com.upt.lp.despesaspessoais.entity.Utilizador;
import com.upt.lp.despesaspessoais.entity.Despesas;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.Scanner;

public class MenuApp {

    private static final String API_URL = "http://localhost:8080/api";
    private static final RestTemplate rest = new RestTemplate();
    private static final Scanner sc = new Scanner(System.in);
    
    // Guardamos o ID do utilizador logado
    private static Long utilizadorLogadoId = null; 

    public static void main(String[] args) {
        while (true) {
            if (utilizadorLogadoId == null) {
                mostrarMenuInicial();
            } else {
                mostrarMenuPrincipal();
            }
        }
    }

    // --- MENUS ---

    private static void mostrarMenuInicial() {
        System.out.println("\n=== BEM-VINDO AO GESTOR DE DESPESAS ===");
        System.out.println("1. Login");
        System.out.println("2. Registar Conta");
        System.out.println("0. Sair");
        System.out.print("Opção: ");
        
        int opcao = lerOpcao();
        switch (opcao) {
            case 1 -> fazerLogin();
            case 2 -> registarConta();
            case 0 -> System.exit(0);
            default -> System.out.println("Opção inválida!");
        }
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("\n=== MENU PRINCIPAL ===");
        System.out.println("1. Ver Minhas Despesas");
        System.out.println("2. Nova Despesa");
        System.out.println("9. Logout");
        System.out.print("Opção: ");

        int opcao = lerOpcao();
        switch (opcao) {
            case 1 -> listarDespesas();
            case 2 -> criarDespesa();
            case 9 -> logout();
            default -> System.out.println("Opção inválida!");
        }
    }

    // --- AÇÕES ---

    private static void fazerLogin() {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();

        Utilizador loginRequest = new Utilizador();
        loginRequest.setEmail(email);
        loginRequest.setPassword(pass);

        try {
            // Chama o Backend: POST /api/utilizadores/login
            Utilizador user = rest.postForObject(API_URL + "/utilizadores/login", loginRequest, Utilizador.class);
            if (user != null) {
                utilizadorLogadoId = user.getId();
                System.out.println("Login com sucesso! Olá, " + user.getNome());
            }
        } catch (HttpClientErrorException e) {
            // Erros 400, 401, 404 (Dados errados)
            System.out.println("Login falhou: " + e.getResponseBodyAsString()); // Mostra o erro real
        } catch (Exception e) {
            // Erros de servidor ou conexão
            System.out.println("Erro Crítico: " + e.getMessage());
        }
    }

    private static void registarConta() {
        System.out.println("\n--- REGISTO ---");
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();
        
        Utilizador novo = new Utilizador(nome, email, pass);

        try {
            rest.postForObject(API_URL + "/utilizadores", novo, Utilizador.class);
            System.out.println("Conta criada! Faz login agora.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void listarDespesas() {
        try {
            String url = API_URL + "/despesas/user/" + utilizadorLogadoId;
            // Recebe um Array de Despesas
            Despesas[] despesas = rest.getForObject(url, Despesas[].class);
            
            System.out.println("\n--- AS TUAS DESPESAS ---");
            if (despesas != null) {
                for (Despesas d : despesas) {
                    System.out.println(d.getData() + " | " + d.getDescricao() + " | " + d.getValor() + "€");
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar despesas.");
        }
    }
    
    // Método simples para criar despesa (podes melhorar pedindo categoria)
    private static void criarDespesa() {
        // ... (Para este funcionar, precisas de pedir Categoria ID também, conforme a tua lógica)
        System.out.println("(Implementa aqui a criação de despesa tal como fizeste no Postman)");
    }

    private static void logout() {
        utilizadorLogadoId = null;
        System.out.println("Sessão terminada.");
    }

    private static int lerOpcao() {
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}