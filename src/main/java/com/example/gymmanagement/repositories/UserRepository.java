package com.example.gymmanagement.repositories;

import com.example.gymmanagement.entities.User;
import com.example.gymmanagement.entities.UserRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.*;
import java.util.List;

@ApplicationScoped
public class UserRepository {

    @PersistenceUnit(unitName = "GymPU")
    private EntityManagerFactory emf;

    @PersistenceContext(unitName = "GymPU")
    private EntityManager emRead;

    public User createUser(User user) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(user);
            tx.commit();
            return user;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void save(User user) {
        if (user.getId() == null) {
            createUser(user);
        } else {
            updateUser(user);
        }
    }

    public User updateUser(User user) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            User updated = em.merge(user);
            tx.commit();
            return updated;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void deleteById(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            User user = em.find(User.class, id);
            if (user != null) {
                em.remove(user);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<User> findAllTrainers(boolean includeInactive) {
        String queryString = "SELECT u FROM User u WHERE u.role = :role";
        if (!includeInactive) {
            queryString += " AND u.isActive = true";
        }
        TypedQuery<User> query = emRead.createQuery(queryString, User.class);
        query.setParameter("role", UserRole.TRAINER);
        return query.getResultList();
    }

    public User findById(Long id) { return emRead.find(User.class, id); }
    public List<User> findAll() { return emRead.createQuery("SELECT u FROM User u", User.class).getResultList(); }

    public User findByUsername(String username) {
        try {
            return emRead.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<User> findAllMembers() {
        return emRead.createQuery("SELECT u FROM User u WHERE u.role = :role", User.class)
                .setParameter("role", UserRole.MEMBER)
                .getResultList();
    }

    public List<User> findByRole(UserRole role) {
        return emRead.createQuery("SELECT u FROM User u WHERE u.role = :role", User.class)
                .setParameter("role", role)
                .getResultList();
    }

    public long countByRole(UserRole role) {
        return emRead.createQuery("SELECT count(u) FROM User u WHERE u.role = :role", Long.class)
                .setParameter("role", role)
                .getSingleResult();
    }
}