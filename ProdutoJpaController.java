/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import cadastroserver.model.Produto;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

/**
 *
 * @author maycon
 */
public class ProdutoJpaController {

    private EntityManagerFactory emf = null;

    public ProdutoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<Produto> findProdutoEntities() {
        EntityManager em = getEntityManager();
        try {
            Query query = em.createQuery("SELECT p FROM Produto p");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Produto findProduto(int produtoId) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Produto.class, produtoId);
        } finally {
            em.close();
        }
    }
     public void edit(Produto produto) throws Exception {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = null;

        try {            
            transaction = em.getTransaction();
            transaction.begin();
            
            em.merge(produto);
            
            transaction.commit();
        } catch (Exception ex) {            
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            ex.printStackTrace();
            throw new RuntimeException("Erro ao tentar atualizar o produto", ex);
        } finally {            
            em.close();
        }
    }
}
