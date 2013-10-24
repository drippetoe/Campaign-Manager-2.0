/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.icap.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 *
 * @author Eric Johansson <ejohansson@proximusmobility.com>
 */
@Singleton
@Startup
@LocalBean
public class ICAPServerStart {

    @PersistenceContext
    private EntityManager em;
    
    public ICAPServerStart() {        
    }


    @PostConstruct
    private void startup() {
        System.out.println("Startup ICAP Server...");

        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));
        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(new IcapServerChannelPipeline());
        // Bind and start to accept incoming connections.
        bootstrap.bind(new InetSocketAddress(8099));
        System.out.println("Running localhost:8099");
    }

    @PreDestroy
    private void shutdown() {
    }
}
