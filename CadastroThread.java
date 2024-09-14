/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cadastroserver;

import cadastroserver.model.Produto;
import cadastroserver.model.Usuario;
import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 *
 * @author maycon
 */
public class CadastroThread extends Thread {
     private ProdutoJpaController ctrl;
    private UsuarioJpaController ctrlUsu;
    private Socket s1;
    
    public CadastroThread(ProdutoJpaController ctrl, UsuarioJpaController ctrlUsu, Socket s1) {
        this.ctrl = ctrl;
        this.ctrlUsu = ctrlUsu;
        this.s1 = s1;
    }

    @Override
    public void run() {
        try {           
            ObjectOutputStream out = new ObjectOutputStream(s1.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(s1.getInputStream());

            String login = (String) in.readObject();
            String senha = (String) in.readObject();

            Usuario usuario = ctrlUsu.findUsuario(login, senha);
            if (usuario == null) {
                out.writeObject("Credenciais inválidas. Conexão encerrada.");
                out.close();
                in.close();
                s1.close();
                return;
            }

           // out.writeObject("Autenticado com sucesso!");

            boolean running = true;
            while (running) {
                String comando = (String) in.readObject();

                if (comando.equals("L")) {
                    List<Produto> produtos = ctrl.findProdutoEntities();
                    out.writeObject(produtos);
                } else if (comando.equals("exit")) {
                    running = false;
                    out.writeObject("Conexão encerrada.");
                } else {                    
                    out.writeObject("Comando inválido.");
                }
            }
            
            out.close();
            in.close();
            s1.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
