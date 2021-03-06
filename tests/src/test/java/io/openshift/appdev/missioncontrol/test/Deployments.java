package io.openshift.appdev.missioncontrol.test;

import java.io.File;

import io.openshift.appdev.missioncontrol.service.openshift.spi.OpenShiftServiceSpi;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import io.openshift.appdev.missioncontrol.service.openshift.impl.OpenShiftProjectImpl;

/**
 * Obtains deployments for shared use in tests
 *
 * @author <a href="mailto:alr@redhat.com">Andrew Lee Rubinger</a>
 */
class Deployments {

    private Deployments() {
        // No instances
    }

    static WebArchive getMavenBuiltWar() {
        final WebArchive webArchive = ShrinkWrap.createFromZipFile(
                WebArchive.class,
                new File("../web/target/launchpad-missioncontrol.war"));
        return webArchive;
    }

    /**
     * Test hooks so we can do some cleanupCreatedProject
     *
     * @return
     */
    static WebArchive getTestDeployment() {
        final WebArchive archive = ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(
                        true,
                        OpenShiftServiceSpi.class.getPackage(),
                        OpenShiftProjectImpl.class.getPackage())
                .addPackage(WebDriverProviderHack.class.getPackage());
        final File[] deps = Resolvers.use(MavenResolverSystem.class).
                loadPomFromFile("../services/openshift-service-impl/pom.xml").
                importRuntimeAndTestDependencies().
                resolve().
                withTransitivity().
                asFile();
        final File[] ourTestDeps = Resolvers.use(MavenResolverSystem.class).
                loadPomFromFile("pom.xml").
                importTestDependencies().
                resolve().
                withTransitivity().
                asFile();
        archive.addAsLibraries(deps);
        archive.addAsLibraries(ourTestDeps);
        return archive;
    }
}
