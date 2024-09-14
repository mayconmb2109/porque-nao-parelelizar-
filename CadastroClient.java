/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cadastroserver;

import cadastroserver.model.Produto;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 *
 * @author maycon
 */
public class CadastroClient {

    public static void main(String[] args) {
        String host = "localhost";
        int porta = 4321;

        try {
            // Instanciar um Socket apontando para localhost, na porta 4321
            Socket socket = new Socket(host, porta);
            System.out.println("Conectado ao servidor em " + host + ":" + porta);

            // Encapsular os canais de entrada e saída do Socket
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());


            String login = "op1"; 
            String senha = "op1"; 
            out.writeObject(login);
            out.writeObject(senha);
            
            String comando = "L";
            out.writeObject(comando);
            
            Object resposta = in.readObject();
            
            if (resposta instanceof List<?>) {
                
                List<?> produtos = (List<?>) resposta;
                for (Object obj : produtos) {
                    if (obj instanceof Produto) {
                        Produto produto = (Produto) obj;
                        System.out.println("Produto: " + produto.getNome());
                    } else {
                        System.out.println("Recebido um objeto que não é um Produto.");
                    }
                }
            } else {
                System.out.println("Recebido um tipo inesperado: " + resposta.getClass().getName()+resposta);
            }
            
            in.close();
            out.close();
            socket.close();
            System.out.println("Conexão encerrada.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
