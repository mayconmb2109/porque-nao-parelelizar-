/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package cadastroserver;

import controller.MovimentoJpaController;
import controller.PessoaJpaController;
import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author maycon
 */
public class CadastroServer {
    
    private static final Map<String, String> credenciais = new HashMap<>();

    static {
        credenciais.put("usuario1", "senha1");
        credenciais.put("usuario2", "senha2");
    }
    
    public static void main(String[] args) {        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CadastroServerPU");
        
        ProdutoJpaController ctrlProd = new ProdutoJpaController(emf);
        UsuarioJpaController ctrlUsu = new UsuarioJpaController(emf);
        MovimentoJpaController ctrlMov = new MovimentoJpaController(emf);
        PessoaJpaController ctrlPessoa = new PessoaJpaController(emf);

        int porta = 4321;

        try {            
            ServerSocket serverSocket = new ServerSocket(porta);
            System.out.println("Servidor escutando na porta " + porta);
           
            while (true) {                
                Socket socket = serverSocket.accept();
                System.out.println("Novo cliente conectado!");
                
                CadastroThread2 thread = new CadastroThread2(ctrlProd, ctrlUsu, ctrlMov, ctrlPessoa, socket);
                thread.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    
        /*try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Servidor iniciado. Aguardando conexão...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Cliente conectado.");
                    
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    
                    out.println("Digite seu login:");
                    String login = in.readLine();
                    out.println("Digite sua senha:");
                    String senha = in.readLine();
                    
                    if (validaCredenciais(login, senha)) {
                        out.println("Login bem-sucedido. Envie 'L' para listar os produtos.");
                    } else {
                        out.println("Credenciais inválidas. Conexão encerrada.");
                        continue;
                    }
                    
                    String comando;
                    while ((comando = in.readLine()) != null) {
                        if (comando.equalsIgnoreCase("L")) {
                            out.println(listaProdutos());
                        } else {
                            out.println("Comando não reconhecido.");
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Erro na conexão com o cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
        }*/
    }
    private static boolean validaCredenciais(String login, String senha) {
        return credenciais.containsKey(login) && credenciais.get(login).equals(senha);
    }

    // Método que retorna a lista de produtos (exemplo)
    private static String listaProdutos() {
        return "1 - Produto A\n2 - Produto B\n3 - Produto C";
    }
    
}
