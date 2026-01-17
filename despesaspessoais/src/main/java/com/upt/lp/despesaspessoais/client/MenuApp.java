package com.upt.lp.despesaspessoais.client;

import com.upt.lp.despesaspessoais.entity.Categoria;
import com.upt.lp.despesaspessoais.entity.Despesas;
import com.upt.lp.despesaspessoais.entity.Utilizador;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.regex.Pattern;

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
                System.out.println("A encerrar o sistema...");
                System.exit(0);
            }
            default -> System.out.println("Opção inválida.");
        }
    }

    static void menuPrincipal() {
        System.out.println("\n=== BEM-VINDO, " + utilizadorLogado.getNome().toUpperCase() + " ===");
        System.out.println("1. Consultar Todas as Despesas");
        System.out.println("2. Registar Nova Despesa");
        System.out.println("3. Menu de Categorias (Listar/Criar/Apagar)");
        System.out.println("4. Pesquisar Despesas (Filtros)");
        System.out.println("5. Eliminar Despesa");
        System.out.println("9. Logout");
        System.out.print("Opção: ");

        String opcao = scanner.nextLine();
        switch (opcao) {
            case "1" -> listarMinhasDespesas();
            case "2" -> criarNovaDespesa();
            case "3" -> menuCategorias();
            case "4" -> menuFiltros();
            case "5" -> eliminarDespesa();
            case "9" -> {
                utilizadorLogado = null;
                System.out.println("Sessão terminada.");
            }
            default -> System.out.println("Opção inválida.");
        }
    }

    static void menuCategorias() {
        System.out.println("\n--- GESTÃO DE CATEGORIAS ---");
        System.out.println("1. Listar Categorias");
        System.out.println("2. Criar Nova Categoria");
        System.out.println("3. Eliminar Categoria");
        System.out.println("0. Voltar");
        System.out.print("Opção: ");

        String op = scanner.nextLine();
        switch (op) {
            case "1" -> listarCategorias();
            case "2" -> criarCategoria();
            case "3" -> eliminarCategoria();
            case "0" -> {} 
            default -> System.out.println("Opção inválida.");
        }
    }

    static void menuFiltros() {
        System.out.println("\n--- PESQUISAR DESPESAS ---");
        System.out.println("1. Por Ano");
        System.out.println("2. Por Categoria");
        System.out.println("3. Por Intervalo de Valor (Min/Max)");
        System.out.println("0. Voltar");
        System.out.print("Opção: ");

        String op = scanner.nextLine();
        switch (op) {
            case "1" -> filtrarPorAno();
            case "2" -> filtrarPorCategoria();
            case "3" -> filtrarPorValor();
            case "0" -> {}
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
            System.out.println("Login efetuado com sucesso.");
        } catch (ResourceAccessException e) {
            System.out.println("O servidor está desligado! Verifique a conexão.");
        } catch (RestClientResponseException e) {
            System.out.println("Email não existe ou Password incorreta.");
        } catch (Exception e) {
            System.out.println("Ocorreu um erro inesperado: " + e.getMessage());
        }
    }

    static void registarConta() {
        System.out.println("\n--- CRIAR CONTA ---");
        
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        if (nome.trim().isEmpty()) {
            System.out.println("O nome não pode estar vazio.");
            return;
        }

        // Validação de Email
        String email;
        while (true) {
            System.out.print("Email: ");
            email = scanner.nextLine();
            if (Pattern.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", email)) {
                break;
            }
            System.out.println("Email inválido! Certifique-se que usa o formato 'exemplo@mail.com'.");
        }

        // Validação de Password (Mínimo 8 caracteres)
        String pass;
        while (true) {
            System.out.print("Password (min 8 caracteres): ");
            pass = scanner.nextLine();
            if (pass.length() >= 8) {
                break;
            }
            System.out.println("Password muito curta! Mínimo de 8 caracteres.");
        }

        Utilizador novo = new Utilizador(nome, email, pass);
        try {
            api.postForObject(BASE_URL + "/utilizadores", novo, Utilizador.class);
            System.out.println("Conta criada com sucesso. Pode fazer login.");
        } catch (Exception e) {
            System.out.println("Não foi possível criar a conta. Esse email já existe?");
        }
    }

    // --- FUNCIONALIDADES E FILTROS ---

    static void listarMinhasDespesas() {
        try {
            String url = BASE_URL + "/despesas/user/" + utilizadorLogado.getId();
            Despesas[] lista = api.getForObject(url, Despesas[].class);
            mostrarTabela(lista); 
        } catch (Exception e) {
            System.out.println("Falha ao obter a lista de despesas.");
        }
    }

    static void filtrarPorAno() {
        try {
            System.out.print("Qual o ano (ex: 2025): ");
            int ano = Integer.parseInt(scanner.nextLine());
            
            String url = BASE_URL + "/despesas/filtro/ano?userId=" + utilizadorLogado.getId() + "&ano=" + ano;
            Despesas[] lista = api.getForObject(url, Despesas[].class);
            mostrarTabela(lista);

        } catch (Exception e) {
            System.out.println("Erro ao filtrar por ano.");
        }
    }

    static void filtrarPorCategoria() {
        try {
            listarCategorias();
            System.out.print("ID da Categoria a pesquisar: ");
            Long catId = Long.parseLong(scanner.nextLine());

            String url = BASE_URL + "/despesas/filtro/categoria?userId=" + utilizadorLogado.getId() + "&catId=" + catId;
            Despesas[] lista = api.getForObject(url, Despesas[].class);
            mostrarTabela(lista);

        } catch (Exception e) {
            System.out.println("Erro ao filtrar por categoria.");
        }
    }

    static void filtrarPorValor() {
        try {
            System.out.print("Valor Mínimo: ");
            Double min = Double.parseDouble(scanner.nextLine().replace(",", "."));
            System.out.print("Valor Máximo: ");
            Double max = Double.parseDouble(scanner.nextLine().replace(",", "."));

            String url = BASE_URL + "/despesas/filtro/valor?userId=" + utilizadorLogado.getId() + "&min=" + min + "&max=" + max;
            Despesas[] lista = api.getForObject(url, Despesas[].class);
            mostrarTabela(lista);

        } catch (Exception e) {
            System.out.println("Erro ao filtrar por valor.");
        }
    }

    static void mostrarTabela(Despesas[] lista) {
        System.out.println("\n--- RESULTADOS ---");
        if (lista == null || lista.length == 0) {
            System.out.println("Nenhuma despesa encontrada.");
        } else {
            System.out.printf("%-4s | %-12s | %-10s | %-15s | %s%n", "ID", "DATA", "VALOR", "CATEGORIA", "DESCRIÇÃO");
            System.out.println("-----+--------------+------------+-----------------+----------------");
            for (Despesas d : lista) {
                String catNome = (d.getCategoria() != null) ? d.getCategoria().getNome() : "N/A";
                System.out.printf("%-4d | %s | %6.2f Eur | %-15s | %s%n", 
                    d.getId(), d.getData(), d.getValor(), catNome, d.getDescricao());
            }
        }
        System.out.println("--------------------------------------------------------------------");
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
            System.out.println("Falha ao obter as categorias.");
        }
    }

    static void criarNovaDespesa() {
        System.out.println("\n--- NOVA DESPESA ---");
        try {
            System.out.print("Descrição: ");
            String desc = scanner.nextLine();
            System.out.print("Valor (ex: 12,50): ");
            Double valor = Double.parseDouble(scanner.nextLine().replace(",", "."));
            System.out.print("Data (AAAA-MM-DD) [Enter para hoje]: ");
            String dataStr = scanner.nextLine();
            LocalDate data = dataStr.isEmpty() ? LocalDate.now() : LocalDate.parse(dataStr);
            System.out.print("ID da Categoria (Ver menu categorias se não souber): ");
            Long catId = Long.parseLong(scanner.nextLine());

            Despesas nova = new Despesas();
            nova.setDescricao(desc);
            nova.setValor(valor);
            nova.setData(data);

            String url = BASE_URL + "/despesas?userId=" + utilizadorLogado.getId() + "&catId=" + catId;
            api.postForObject(url, nova, Despesas.class);
            System.out.println("Despesa registada com sucesso.");

        } catch (Exception e) {
            System.out.println("Erro ao registar a despesa. Verifique os dados inseridos.");
        }
    }

    static void criarCategoria() {
        System.out.println("\n--- NOVA CATEGORIA ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        if (nome.trim().isEmpty()) { System.out.println("O nome não pode estar vazio!"); return; }

        Categoria nova = new Categoria();
        nova.setNome(nome);
        nova.setNomeIcone("tag"); 
        nova.setCorHex("#000000"); 

        try {
            String url = BASE_URL + "/categorias?userId=" + utilizadorLogado.getId();
            api.postForObject(url, nova, Categoria.class);
            System.out.println("Categoria criada com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro ao criar a categoria.");
        }
    }

    static void eliminarCategoria() {
        System.out.println("\n--- ELIMINAR CATEGORIA ---");
        listarCategorias();
        System.out.print("Digite o ID da categoria a eliminar: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            String url = BASE_URL + "/categorias/" + id + "?userId=" + utilizadorLogado.getId();
            api.delete(url);
            System.out.println("Categoria eliminada com sucesso.");
        } catch (HttpClientErrorException e) {
            System.out.println("Erro do Servidor: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("Erro ao eliminar a categoria.");
        }
    }

    static void eliminarDespesa() {
        System.out.println("\n--- ELIMINAR DESPESA ---");
        listarMinhasDespesas(); 
        
        System.out.print("Digite o ID da despesa a eliminar: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            String url = BASE_URL + "/despesas/" + id + "?userId=" + utilizadorLogado.getId();
            api.delete(url);
            System.out.println("Despesa eliminada com sucesso.");

        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
        } catch (HttpClientErrorException e) {
            System.out.println("Erro do Servidor: " + e.getResponseBodyAsString()); 
        } catch (Exception e) {
            System.out.println("Erro ao eliminar a despesa.");
        }
    }
}