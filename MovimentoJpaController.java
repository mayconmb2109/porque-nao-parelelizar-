package controller;

import cadastroserver.model.Movimento;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

/**
 *
 * @author maycon
 */
public class MovimentoJpaController {

    private EntityManagerFactory emf = null;

    public MovimentoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Movimento movimento) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = null;
        
        try {
            // Iniciar a transação
            transaction = em.getTransaction();
            transaction.begin();
            
            // Persistir o objeto movimento
            em.persist(movimento);
            
            // Confirmar a transação
            transaction.commit();
        } catch (Exception ex) {
            // Em caso de erro, fazer rollback da transação
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            ex.printStackTrace();
            throw new RuntimeException("Erro ao tentar persistir o movimento", ex);
        } finally {
            // Fechar o EntityManager
            em.close();
        }
    }
    
    public Movimento findMovimento(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Movimento.class, id);
        } finally {
            em.close();
        }
    }
}
