package ua.edu.lnu.dcservice.dao.impl;

import ua.edu.lnu.dcservice.dao.GenericDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

@Transactional
public abstract class HibernateDao<E, K extends Serializable> implements GenericDao<E, K> {
    @PersistenceContext
    private EntityManager em;

    private Class<E> entityClass;

    public HibernateDao(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public List<E> findAll() {
        return session().createQuery("FROM " + entityClass.getName(), entityClass).getResultList();
    }

    @Override
    public List<E> findPart(int limit, int offset) {
        TypedQuery<E> query = session().createQuery("FROM " + entityClass.getName(), entityClass);
        query.setMaxResults(limit);
        query.setFirstResult(offset);

        return query.getResultList();
    }

    @Override
    public long countAll() {
        Query query = session().createQuery("SELECT COUNT(*) FROM " + entityClass.getName());

        return (long) query.getSingleResult();
    }

    @Override
    public E get(K key) {
        return session().find(entityClass, key);
    }

    @Override
    public void persist(E entity) {
        session().persist(entity);
    }

    @Override
    public E merge(E entity) {
        return session().merge(entity);
    }

    @Override
    public void remove(K id) {
        session().remove(getReference(id));
    }

    @Override
    public E getReference(K key) {
        return session().getReference(entityClass, key);
    }

    @Override
    public void flush() {
        session().flush();
    }

    protected EntityManager session() {
        return em;
    }
}