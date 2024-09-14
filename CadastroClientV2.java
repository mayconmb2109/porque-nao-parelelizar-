/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cadastroserver;

import cadastroserver.model.Produto;
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author maycon
 */
public class CadastroClientV2 {

    public static void main(String[] args) {
        String host = "localhost";
        int porta = 4321;

        try {
            System.out.println("Tentando conectar ao servidor...");
            Socket socket = new Socket(host, porta);
            System.out.println("Conectado ao servidor em " + host + ":" + porta);

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            String login = "op1"; 
            String senha = "op1";
            out.writeObject(login);
            out.writeObject(senha);
            out.flush(); 
            System.out.println("Login e senha enviados.");
            
            Object respostaLogin = in.readObject();
            System.out.println("Resposta do login: " + respostaLogin);
            if (respostaLogin instanceof String && "Credenciais inválidas. Conexão encerrada.".equals(respostaLogin)) {
                System.out.println("Login ou senha inválidos. Encerrando conexão.");
                socket.close();
                return;
            }

            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

            SaidaFrame saidaFrame = new SaidaFrame(null); 
            saidaFrame.setVisible(true);

            ThreadClient threadClient = new ThreadClient(in, saidaFrame.texto);
            threadClient.start();

            String comando = "";

            while (!comando.equalsIgnoreCase("X")) {
                System.out.println("\nMenu:");
                System.out.println("L – Listar");
                System.out.println("E – Entrada");
                System.out.println("S – Saída");
                System.out.println("X – Finalizar");
                System.out.print("Escolha uma opção: ");

                comando = teclado.readLine().toUpperCase();
                System.out.println("Comando recebido: " + comando);

                switch (comando) {
                    case "L":
                        out.writeObject("L");
                        out.flush(); 
                        System.out.println("Comando 'L' enviado.");
                        break;

                    case "E":
                    case "S":
                        out.writeObject(comando);
                        out.flush(); 

                        System.out.print("Digite o Id da pessoa: ");
                        int pessoaId = Integer.parseInt(teclado.readLine());
                        out.writeObject(pessoaId);
                        out.flush();
                        System.out.println("Id da pessoa enviado: " + pessoaId);

                        System.out.print("Digite o Id do produto: ");
                        int produtoId = Integer.parseInt(teclado.readLine());
                        out.writeObject(produtoId);
                        out.flush(); 
                        System.out.println("Id do produto enviado: " + produtoId);

                        System.out.print("Digite a quantidade: ");
                        int quantidade = Integer.parseInt(teclado.readLine());
                        out.writeObject(quantidade);
                        out.flush(); 
                        System.out.println("Quantidade enviada: " + quantidade);

                        System.out.print("Digite o valor unitário: ");
                        double valorUnitario = Double.parseDouble(teclado.readLine());
                        out.writeObject(valorUnitario);
                        out.flush(); 
                        System.out.println("Valor unitário enviado: " + valorUnitario);

                        break;

                    case "X":
                        System.out.println("Finalizando a conexão.");
                        threadClient.interrupt(); 
                        out.writeObject("X");
                        out.flush(); 
                        socket.close();
                        break;

                    default:
                        System.out.println("Comando desconhecido.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Classe interna para gerenciar a leitura de mensagens do servidor e
     * atualizar o JTextArea.
     */
    static class ThreadClient extends Thread {

        private ObjectInputStream entrada;
        private JTextArea textArea;

        public ThreadClient(ObjectInputStream entrada, JTextArea textArea) {
            this.entrada = entrada;
            this.textArea = textArea;
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Object mensagem = entrada.readObject();

                        SwingUtilities.invokeLater(() -> {
                            if (mensagem instanceof String) {
                                textArea.append((String) mensagem + "\n");
                            } else if (mensagem instanceof List<?>) {
                                @SuppressWarnings("unchecked")
                                List<?> lista = (List<?>) mensagem;
                                for (Object item : lista) {
                                    if (item instanceof Produto) {
                                        Produto produto = (Produto) item;
                                        textArea.append(String.format("Produto: %s, Quantidade: %d\n", produto.getNome(), produto.getQuantidade()));
                                    }
                                }
                            } else {
                                textArea.append("Objeto desconhecido recebido.\n");
                            }
                        });

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(CadastroClientV2.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();                        
                        Thread.currentThread().interrupt();
                    }
                }
            } finally {
                SwingUtilities.invokeLater(() -> textArea.append("Conexão com o servidor encerrada.\n"));
            }
        }
    }

     static class SaidaFrame extends JDialog {
       
        public JTextArea texto;
        
        public SaidaFrame(JFrame owner) {
            super(owner, "Saída de Mensagens", false); 
           
            texto = new JTextArea();
            texto.setEditable(false);
           
            texto.setLineWrap(true);
            texto.setWrapStyleWord(true);
            
            JScrollPane scrollPane = new JScrollPane(texto);
            setLayout(new BorderLayout()); 
            add(scrollPane, BorderLayout.CENTER);
            
            setSize(400, 300);
            
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            
            setLocationRelativeTo(null);
        }
    }
}
