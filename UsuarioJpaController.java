/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import cadastroserver.model.Usuario;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 *
 * @author maycon
 */
public class UsuarioJpaController {

    private EntityManagerFactory emf = null;

    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public Usuario findUsuario(String login, String senha) {
        EntityManager em = getEntityManager();
        try {
            Query query = em.createQuery("SELECT u FROM Usuario u WHERE u.email = :login AND u.senha = :senha");
            query.setParameter("login", login);
            query.setParameter("senha", senha);

            try {
                return (Usuario) query.getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        } finally {
            em.close();
        }
    }
}
