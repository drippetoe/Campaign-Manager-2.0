/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.runtime.johansson;

import com.proximus.data.Company;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author eric
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("ProximusTomorrow-ejbPULocalEric");
            EntityManager em = emf.createEntityManager();
            Query q = em.createQuery("SELECT c FROM Company c");

            List<Company> comps = (List<Company>) q.getResultList();

            for (Company company : comps) {
                System.out.println(company);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
