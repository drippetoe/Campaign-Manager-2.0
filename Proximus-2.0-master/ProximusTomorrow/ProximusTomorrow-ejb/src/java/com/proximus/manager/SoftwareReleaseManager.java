/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.manager;

import com.proximus.data.Device;
import com.proximus.data.SoftwareRelease;
import java.io.File;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author dshaw
 */
@Stateless
public class SoftwareReleaseManager extends AbstractManager<SoftwareRelease> implements SoftwareReleaseManagerLocal {

    private static final Logger logger = Logger.getLogger(SoftwareReleaseManager.class);
    @PersistenceContext(name = "ProximusTomorrow-ejbPU")
    private EntityManager em;

    public SoftwareReleaseManager() {
        super(SoftwareRelease.class);
    }

    @Override
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Return the platform release, not single releases
     * @param platform
     * @param major
     * @param minor
     * @param build
     * @return
     */
    @Override
    public SoftwareRelease getRelease(String platform, Long major, Long minor, Long build, Device d) {
        Query q;
        if ( d == null )
        {
            q= em.createQuery(
                "SELECT r FROM SoftwareRelease r WHERE r.platform = ?1 "
                + "AND r.major = ?2 "
                + "AND r.minor = ?3 "
                + "AND r.build = ?4 "
                + "ORDER BY r.releaseDate DESC, r.single DESC");
        }
        else
        {
            q= em.createQuery(
                "SELECT r FROM SoftwareRelease r WHERE r.platform = ?1 "
                + "AND r.major = ?2 "
                + "AND r.minor = ?3 "
                + "AND r.build = ?4 "
                + "AND (r.device = ?5 OR r.device IS NULL) "
                + "ORDER BY r.releaseDate DESC, r.single DESC");
            q.setParameter(5, d);
        }
        q.setParameter(1, platform);
        q.setParameter(2, major);
        q.setParameter(3, minor);
        q.setParameter(4, build);

        List<SoftwareRelease> results = (List<SoftwareRelease>) q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        else
        {
            return null;
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    public File getFileForSoftwareRelease(SoftwareRelease sr) {
        File file = new File(sr.getPath());
        if (file.exists()) {
            return file;
        }
        return null;
    }

    @Override
    public SoftwareRelease getReleaseForDevice(Device d) {
        return getRelease(d.getPlatform(), d.getMajor(), d.getMinor(), d.getBuild(), d);
    }
}
