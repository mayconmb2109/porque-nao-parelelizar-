/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cadastroserver;

import cadastroserver.model.Movimento;
import cadastroserver.model.Pessoa;
import cadastroserver.model.Produto;
import cadastroserver.model.Usuario;
import controller.MovimentoJpaController;
import controller.PessoaJpaController;
import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maycon
 */
public class CadastroThread2 extends Thread {

    private ProdutoJpaController ctrlProd;
    private UsuarioJpaController ctrlUsu;
    private MovimentoJpaController ctrlMov;
    private PessoaJpaController ctrlPessoa;
    private Socket s1;
    private Usuario usuarioLogado;

    public CadastroThread2(ProdutoJpaController ctrlProd, UsuarioJpaController ctrlUsu, MovimentoJpaController ctrlMov, PessoaJpaController ctrlPessoa, Socket s1) {
        this.ctrlProd = ctrlProd;
        this.ctrlUsu = ctrlUsu;
        this.ctrlMov = ctrlMov;
        this.ctrlPessoa = ctrlPessoa;
        this.s1 = s1;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(s1.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(s1.getInputStream());

            // Autenticação do Usuário
            String login = (String) in.readObject();
            String senha = (String) in.readObject();
            System.out.println("Recebido login: " + login);
            System.out.println("Recebida senha: " + senha);
            usuarioLogado = ctrlUsu.findUsuario(login, senha);

            if (usuarioLogado == null) {
                out.writeObject("Credenciais inválidas. Conexão encerrada.");
                out.flush(); // Garante que a mensagem seja enviada imediatamente
                s1.close();
                return;
            }

            out.writeObject("Login bem-sucedido. Envie 'L' para listar os produtos.");
            out.flush(); // Garante que a mensagem seja enviada imediatamente

            // Loop de resposta
            boolean continuar = true;
            while (continuar) {
                String comando = (String) in.readObject();
                System.out.println("Comando recebido: " + comando);

                switch (comando) {
                    case "L":
                        // Listar produtos
                        List<Produto> produtos = ctrlProd.findProdutoEntities();
                        out.writeObject(produtos);
                        break;

                    case "E":
                    case "S":
                        // Entrada ou saída de produtos
                        processarMovimento(comando.charAt(0), in, out);
                        break;

                    case "X":
                        continuar = false;
                        break;

                    default:
                        out.writeObject("Comando desconhecido.");
                }
                out.flush(); // Garante que as respostas sejam enviadas imediatamente
            }

            s1.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processarMovimento(char tipoMovimento, ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        // Receber dados do movimento
        int pessoaId = (int) in.readObject();
        int produtoId = (int) in.readObject();
        int quantidade = (int) in.readObject();
        double valorUnitario = (double) in.readObject();

        // Criar um novo movimento
        Movimento movimento = new Movimento();
        movimento.setIdUsuario(usuarioLogado);
        movimento.setTipo(tipoMovimento); 
        Pessoa pessoa = new Pessoa(pessoaId);
        movimento.setIdPessoa(pessoa);
        movimento.setIdProduto(ctrlProd.findProduto(produtoId));
        movimento.setQuantidade(quantidade);
        movimento.setPrecoUnitario(BigDecimal.valueOf(valorUnitario));

        // Persistir o movimento
        ctrlMov.create(movimento);

        // Atualizar a quantidade do produto
        Produto produto = ctrlProd.findProduto(produtoId);
        if (produto != null) {
            try {
                int novaQuantidade = tipoMovimento == 'E' ?
                        produto.getQuantidade() + quantidade :
                        produto.getQuantidade() - quantidade;
                
                produto.setQuantidade(novaQuantidade);
                ctrlProd.edit(produto);
                
                out.writeObject("Movimento registrado com sucesso.");
            } catch (Exception ex) {
                Logger.getLogger(CadastroThread2.class.getName()).log(Level.SEVERE, null, ex);
                out.writeObject("Erro ao registrar movimento.");
            }
        } else {
            out.writeObject("Produto não encontrado.");
        }
    }
}