/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager;

import java.util.List;

/**
 *
 * @author eric
 */
public interface AbstractManagerInterface<T> {

    public int count();

    public void create(T entity);

    public void delete(T entity);

    public T find(Object id);

    public List<T> findAll();

    public List<T> findRange(int[] range);

    public T update(T entity);
}
