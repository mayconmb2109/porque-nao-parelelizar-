/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cadastroserver;

import cadastroserver.model.Usuario;
import controller.UsuarioJpaController;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author maycon
 */
public class Teste {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CadastroServerPU");
        /*EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        // Criar uma nova entidade e persistir
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Maria");
        em.persist(pessoa);

        em.getTransaction().commit();

        em.close();
        emf.close();*/
        // Simulação de login e senha fornecidos
        UsuarioJpaController usuarioController = new UsuarioJpaController(emf);

        String email = "op1";
        String senha = "op1";

        // Buscar o usuário
        Usuario usuario = usuarioController.findUsuario(email, senha);
        if (usuario != null) {
            System.out.println("Usuário autenticado: " + usuario.getNome());
        } else {
            System.out.println("Credenciais inválidas.");
        }

        // Fechar EntityManagerFactory
        emf.close();
    }
}
