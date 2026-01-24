package com.upt.lp.despesaspessoais.client;

import com.upt.lp.despesaspessoais.entity.Categoria;
import com.upt.lp.despesaspessoais.entity.Despesas;
import com.upt.lp.despesaspessoais.entity.Utilizador;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
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

    public static void menuVisitante() {
        System.out.println("=== GESTÃO DE DESPESAS ===");
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

    public static void menuPrincipal() {
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

    // --- MÉTODOS DE MENU SECUNDÁRIOS ---

    public static void menuCategorias() {
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

    public static void menuFiltros() {
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

    // --- FUNÇÕES DE LÓGICA ---

    public static void fazerLogin() {
        try {
            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            Utilizador u = new Utilizador();
            u.setEmail(email);
            u.setPassword(password);

            utilizadorLogado = api.postForObject(BASE_URL + "/utilizadores/login", u, Utilizador.class);
            System.out.println("Login efetuado com sucesso!");

        } catch (Exception e) {
            System.out.println("Login inválido.");
        }
    }

    public static void registarConta() {
        System.out.print("Nome: ");
        String nome = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        if (!email.contains("@") || !email.contains(".")) {
            System.out.println("Email inválido.");
            return;
        }

        // Validação da Password (Mínimo 8)
        String password;
        while (true) {
            System.out.print("Password (min 8 caracteres): ");
            password = scanner.nextLine();
            if (password.length() >= 8) {
                break;
            }
            System.out.println("Password muito curta! Mínimo de 8 caracteres.");
        }

        try {
            Utilizador novo = new Utilizador();
            novo.setNome(nome);
            novo.setEmail(email);
            novo.setPassword(password);

            // CORRIGIDO: URL sem "/registar"
            api.postForObject(BASE_URL + "/utilizadores", novo, Utilizador.class);

            System.out.println("Conta criada com sucesso!");

        } catch (HttpClientErrorException e) {
            System.out.println("Erro no pedido (Email repetido?): " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("Erro ao registar conta: " + e.getMessage());
        }
    }

    public static void listarMinhasDespesas() {
        try {
            String url = BASE_URL + "/despesas/user/" + utilizadorLogado.getId();
            Despesas[] lista = api.getForObject(url, Despesas[].class);
            mostrarTabela(lista);
        } catch (Exception e) {
            System.out.println("Erro ao listar despesas.");
        }
    }

    public static void filtrarPorAno() {
        try {
            System.out.print("Ano: ");
            int ano = Integer.parseInt(scanner.nextLine());
            String url = BASE_URL + "/despesas/filtro/ano?userId="+ utilizadorLogado.getId() + "&ano=" + ano;
            Despesas[] lista = api.getForObject(url, Despesas[].class);
            mostrarTabela(lista);
        } catch (Exception e) {
            System.out.println("Erro ao filtrar por ano.");
        }
    }
    
    public static void filtrarPorCategoria() {
        try {
            listarCategorias();
            System.out.print("ID da categoria: ");
            Long catId = Long.parseLong(scanner.nextLine());
            String url = BASE_URL + "/despesas/filtro/categoria?userId="+ utilizadorLogado.getId() + "&catId=" + catId;
            Despesas[] lista = api.getForObject(url, Despesas[].class);
            mostrarTabela(lista);
        } catch (Exception e) {
            System.out.println("Erro ao filtrar por categoria.");
        }
    }

    public static void filtrarPorValor() {
        try {
            System.out.print("Valor mínimo: ");
            double min = Double.parseDouble(scanner.nextLine().replace(",", "."));
            System.out.print("Valor máximo: ");
            double max = Double.parseDouble(scanner.nextLine().replace(",", "."));
            String url = BASE_URL + "/despesas/filtro/valor?userId="
                    + utilizadorLogado.getId() + "&min=" + min + "&max=" + max;
            Despesas[] lista = api.getForObject(url, Despesas[].class);
            mostrarTabela(lista);
        } catch (Exception e) {
            System.out.println("Erro ao filtrar por valor.");
        }
    }

    public static void mostrarTabela(Despesas[] lista) {
        if (lista == null || lista.length == 0) {
            System.out.println("Nenhuma despesa encontrada.");
            return;
        }
        System.out.println("\nID / DATA / VALOR / CATEGORIA / DESCRIÇÃO");
        for (Despesas d : lista) {
            String categoria = (d.getCategoria() != null)
                    ? d.getCategoria().getNome()
                    : "Sem categoria";
            System.out.println(
                    d.getId() + " | " + d.getData() + " | " + d.getValor() + "€ | " + categoria + " | " + d.getDescricao());
        }
    }

    public static void listarCategorias() {
        try {
            String url = BASE_URL + "/categorias?userId=" + utilizadorLogado.getId();
            Categoria[] catg = api.getForObject(url, Categoria[].class);
            if (catg == null || catg.length == 0) {
                System.out.println("Sem categorias.");
                return;
            }
            for (Categoria c : catg) {
                System.out.println(c.getId() + " - " + c.getNome());
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar categorias.");
        }
    }

    public static void criarNovaDespesa() {
        try {
            System.out.print("Descrição: ");
            String desc = scanner.nextLine();
            System.out.print("Valor: ");
            double valor = Double.parseDouble(scanner.nextLine().replace(",", "."));
            System.out.print("ID da categoria: ");
            Long catId = Long.parseLong(scanner.nextLine());

            Despesas d = new Despesas();
            d.setDescricao(desc);
            d.setValor(valor);
            d.setData(LocalDate.now());

            String url = BASE_URL + "/despesas?userId=" + utilizadorLogado.getId() + "&catId=" + catId;
            api.postForObject(url, d, Despesas.class);
            System.out.println("Despesa criada.");

        } catch (HttpClientErrorException e) {
            System.out.println("Erro: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("Erro ao criar despesa.");
        }
    }

    public static void criarCategoria() {
        System.out.print("Nome da categoria: ");
        String nome = scanner.nextLine();
        if (nome.isBlank()) {
            System.out.println("Nome inválido.");
            return;
        }
        try {
            Categoria c = new Categoria();
            c.setNome(nome);
            String url = BASE_URL + "/categorias?userId=" + utilizadorLogado.getId();
            api.postForObject(url, c, Categoria.class);
            System.out.println("Categoria criada.");
        } catch (Exception e) {
            System.out.println("Erro ao criar categoria.");
        }
    }

    public static void eliminarCategoria() {
        try {
            listarCategorias();
            System.out.print("ID da categoria: ");
            Long id = Long.parseLong(scanner.nextLine());
            String url = BASE_URL + "/categorias/" + id + "?userId=" + utilizadorLogado.getId();
            api.delete(url);
            System.out.println("Categoria eliminada.");
        } catch (Exception e) {
            System.out.println("Erro ao eliminar categoria.");
        }
    }

    public static void eliminarDespesa() {
        try {
            listarMinhasDespesas();
            System.out.print("ID da despesa: ");
            Long id = Long.parseLong(scanner.nextLine());
            String url = BASE_URL + "/despesas/" + id + "?userId=" + utilizadorLogado.getId();
            api.delete(url);
            System.out.println("Despesa eliminada.");
        } catch (Exception e) {
            System.out.println("Erro ao eliminar despesa.");
        }
    }
}