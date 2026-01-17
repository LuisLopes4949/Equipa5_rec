package com.upt.lp.despesaspessoais.client;

import com.upt.lp.despesaspessoais.entity.Categoria;
import com.upt.lp.despesaspessoais.entity.Despesas;
import com.upt.lp.despesaspessoais.entity.Utilizador;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class MenuApp {

    static Scanner scanner = new Scanner(System.in);
    static RestTemplate api = new RestTemplate();
    static String BASE_URL = "http://localhost:8080/api";
    static Utilizador utilizadorLogado = null;

    public static void main(String[] args) {
        while (true) {
            if (utilizadorLogado == null) {
                menuVisitante();
            } else {
                menuPrincipal();
            }
        }
    }

    // --- MENUS ---

    static void menuVisitante() {
        System.out.println("\n=== GESTÃO DE DESPESAS ===");
        System.out.println("1. Login");
        System.out.println("2. Registar Conta");
        System.out.println("0. Sair");
        System.out.print("Opção: ");

        String opcao = scanner.nextLine();
        switch (opcao) {
            case "1" -> fazerLogin();
            case "2" -> registarConta();
            case "0" -> {
                System.out.println("A encerrar sistema...");
                System.exit(0);
            }
            default -> System.out.println("Opção inválida.");
        }
    }

    static void menuPrincipal() {
        System.out.println("\n=== BEM-VINDO, " + utilizadorLogado.getNome().toUpperCase() + " ===");
        System.out.println("1. Consultar Despesas");
        System.out.println("2. Registar Nova Despesa");
        System.out.println("3. Listar Categorias");
        System.out.println("4. Criar Nova Categoria");
        System.out.println("9. Logout");
        System.out.print("Opção: ");

        String opcao = scanner.nextLine();
        switch (opcao) {
            case "1" -> listarMinhasDespesas();
            case "2" -> criarNovaDespesa();
            case "3" -> listarCategorias();
            case "4" -> criarCategoria();
            case "9" -> {
                utilizadorLogado = null;
                System.out.println("Sessão terminada.");
            }
            default -> System.out.println("Opção inválida.");
        }
    }

    // --- AÇÕES DE AUTENTICAÇÃO ---

    static void fazerLogin() {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        Utilizador loginData = new Utilizador();
        loginData.setEmail(email);
        loginData.setPassword(password);

        try {
            Utilizador user = api.postForObject(BASE_URL + "/utilizadores/login", loginData, Utilizador.class);
            utilizadorLogado = user;
            System.out.println("Login efetuado.");

        } catch (ResourceAccessException e) {
            System.out.println("O servidor está desligado!");
        } catch (RestClientResponseException e) {
            System.out.println("Email não existe ou Password incorreta.");
        } catch (Exception e) {
            System.out.println("Ocorreu um erro inesperado: " + e.getMessage());
        }
    }

    static void registarConta() {
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();

        Utilizador novo = new Utilizador(nome, email, pass);
        try {
            api.postForObject(BASE_URL + "/utilizadores", novo, Utilizador.class);
            System.out.println("Conta criada. Podes fazer login.");
        } catch (Exception e) {
            System.out.println("Não foi possível criar conta. Email já existe?");
        }
    }

    // --- FUNCIONALIDADES ---

    static void listarMinhasDespesas() {
        try {
            String url = BASE_URL + "/despesas/user/" + utilizadorLogado.getId();
            Despesas[] lista = api.getForObject(url, Despesas[].class);

            System.out.println("\n--- HISTÓRICO DE DESPESAS ---");
            if (lista == null || lista.length == 0) {
                System.out.println("Sem registos encontrados.");
            } else {
                System.out.printf("%-12s | %-10s | %-15s | %s%n", "DATA", "VALOR", "CATEGORIA", "DESCRIÇÃO");
                System.out.println("----------------------------------------------------------");
                for (Despesas d : lista) {
                    String catNome = (d.getCategoria() != null) ? d.getCategoria().getNome() : "N/A";
                    System.out.printf("%s | %6.2f Eur | %-15s | %s%n", 
                        d.getData(), d.getValor(), catNome, d.getDescricao());
                }
            }
        } catch (Exception e) {
            System.out.println("Falha ao obter lista de despesas.");
        }
    }

    static void listarCategorias() {
        try {
            String url = BASE_URL + "/categorias?userId=" + utilizadorLogado.getId();
            
            Categoria[] cats = api.getForObject(url, Categoria[].class);
            System.out.println("\n--- LISTA DE CATEGORIAS ---");
            System.out.println("ID | NOME");
            System.out.println("---+----------------");
            if (cats != null) {
                for (Categoria c : cats) {
                    System.out.println(c.getId() + "  | " + c.getNome());
                }
            }
        } catch (Exception e) {
            System.out.println("Falha ao obter categorias.");
        }
    }

    static void criarNovaDespesa() {
        System.out.println("\n--- NOVA DESPESA ---");
        
        try {
            System.out.print("Descrição: ");
            String desc = scanner.nextLine();

            System.out.print("Valor (ex: 12.50): ");
            String valorStr = scanner.nextLine().replace(",", ".");
            Double valor = Double.parseDouble(valorStr);

            System.out.print("Data (AAAA-MM-DD) [Enter para hoje]: ");
            String dataStr = scanner.nextLine();
            LocalDate data = dataStr.isEmpty() ? LocalDate.now() : LocalDate.parse(dataStr);

            System.out.print("ID da Categoria (Opção 3 para ver lista): ");
            Long catId = Long.parseLong(scanner.nextLine());

            Despesas nova = new Despesas();
            nova.setDescricao(desc);
            nova.setValor(valor);
            nova.setData(data);

            String url = BASE_URL + "/despesas?userId=" + utilizadorLogado.getId() + "&catId=" + catId;
            
            api.postForObject(url, nova, Despesas.class);
            System.out.println("Despesa registada.");

        } catch (NumberFormatException e) {
            System.out.println("O valor ou ID tem de ser numérico.");
        } catch (DateTimeParseException e) {
            System.out.println("Formato de data inválido. Use AAAA-MM-DD.");
        } catch (Exception e) {
            System.out.println("Não foi possível guardar a despesa.");
        }
    }

    static void criarCategoria() {
        System.out.println("\n--- NOVA CATEGORIA ---");
        System.out.print("Nome da Categoria: ");
        String nome = scanner.nextLine();

        if (nome.trim().isEmpty()) {
            System.out.println("O nome não pode estar vazio.");
            return;
        }

        Categoria nova = new Categoria();
        nova.setNome(nome);
        nova.setNomeIcone("tag"); 
        nova.setCorHex("#000000"); 

        try {
            String url = BASE_URL + "/categorias?userId=" + utilizadorLogado.getId();
            
            api.postForObject(url, nova, Categoria.class);
            System.out.println("Categoria '" + nome + "' criada (Privada).");
        } catch (Exception e) {
            System.out.println("Falha ao criar categoria.");
        }
    }
}