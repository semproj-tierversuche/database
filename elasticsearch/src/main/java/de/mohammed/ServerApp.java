package de.mohammed;

import de.mohammed.service.ImportService;
import de.mohammed.service.QueryService;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.TestPortProvider;

import java.util.Arrays;

public class ServerApp {

    private static NettyJaxrsServer netty;

    public static void main(String[] args) throws Exception {
        ResteasyDeployment deployment=new ResteasyDeployment();
        deployment.setActualResourceClasses(Arrays.<Class>asList(ImportService.class, QueryService.class));
        start(deployment);
    }

    public static void start(ResteasyDeployment deployment) throws Exception
    {
        netty = new NettyJaxrsServer();
        netty.setDeployment(deployment);
        netty.setPort(8081);
        netty.setRootResourcePath("");
        netty.setSecurityDomain(null);
        netty.start();
    }

}
