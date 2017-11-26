package ua.edu.lnu.dcservice.dao;


import java.io.Serializable;
import java.util.List;

/**
 * @param <E> - entity type
 * @param <K> - primary key type
 */
public interface GenericDao<E, K extends Serializable> {
    List<E> findAll();

    List<E> findPart(int limit, int offset);

    long countAll();

    E get(K key);

    void persist(E entity);

    E merge(E entity);

    void remove(K id);

    E getReference(K key);

    void flush();
}