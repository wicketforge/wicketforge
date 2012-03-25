/*
 * Copyright 2010 The WicketForge-Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicketforge.facet.ui;

import com.intellij.facet.ui.libraries.LibraryInfo;
import com.sun.istack.internal.NotNull;

import static com.intellij.facet.ui.libraries.MavenLibraryUtil.createSubMavenJarInfo;

/**
 *
 */
public enum WicketVersion {
    WICKET_1_3("1.3", "http://wicket.apache.org/dtds.data/wicket-xhtml1.3-strict.dtd",
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket", Release.V1_3, "org.apache.wicket.Application"), createSubMavenJarInfo("org/slf4j", "slf4j-api", "1.4.2", "org.slf4j.Logger")},
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket-datetime", Release.V1_3, "org.apache.wicket.datetime.DateConverter")},
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket-extensions", Release.V1_3, "org.apache.wicket.extensions.Initializer")},
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket-velocity", Release.V1_3, "org.apache.wicket.velocity.Initializer")},
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket-spring", Release.V1_3, "org.apache.wicket.spring.SpringWebApplication")},
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket-spring-annot", Release.V1_3, "org.apache.wicket.spring.injection.annot.AnnotSpringInjector")},
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket-jmx", Release.V1_3, "org.apache.wicket.jmx.Application")},
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket-auth-roles", Release.V1_3, "org.apache.wicket.authentication.AuthenticatedWebApplication")},
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket-guice", Release.V1_3, "org.apache.wicket.guice.GuiceComponentInjector")}
    ),
    WICKET_1_4("1.4", "http://wicket.apache.org/dtds.data/wicket-xhtml1.4-strict.dtd",
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket", Release.V1_4, "org.apache.wicket.Application"), createSubMavenJarInfo("org/slf4j", "slf4j-api", "1.5.8", "org.slf4j.Logger")},
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket-datetime", Release.V1_4, "org.apache.wicket.datetime.DateConverter")},
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket-extensions", Release.V1_4, "org.apache.wicket.extensions.Initializer")},
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket-velocity", Release.V1_4, "org.apache.wicket.velocity.Initializer")},
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket-spring", Release.V1_4, "org.apache.wicket.spring.SpringWebApplication")},
            null,
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket-jmx", Release.V1_4, "org.apache.wicket.jmx.Application")},
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket-auth-roles", Release.V1_4, "org.apache.wicket.authentication.AuthenticatedWebApplication")},
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket-guice", Release.V1_4, "org.apache.wicket.guice.GuiceComponentInjector")}

    ),
    WICKET_1_5("1.5", "http://wicket.apache.org/dtds.data/wicket-xhtml1.4-strict.dtd", // at the moment there is no 1.5 dtd...
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket", Release.V1_5, "org.apache.wicket.Application"), createSubMavenJarInfo("org/slf4j", "slf4j-api", "1.5.8", "org.slf4j.Logger")},
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket-datetime", Release.V1_5, "org.apache.wicket.datetime.DateConverter")},
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket-extensions", Release.V1_5, "org.apache.wicket.extensions.Initializer")},
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket-velocity", Release.V1_5, "org.apache.wicket.velocity.Initializer")},
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket-spring", Release.V1_5, "org.apache.wicket.spring.SpringWebApplication")},
            null,
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket-jmx", Release.V1_5, "org.apache.wicket.jmx.Application")},
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket-auth-roles", Release.V1_5, "org.apache.wicket.authentication.AuthenticatedWebApplication")},
            new LibraryInfo[]{createSubMavenJarInfo("org/apache/wicket", "wicket-guice", Release.V1_5, "org.apache.wicket.guice.GuiceComponentInjector")}

    );

    public static WicketVersion HIGHEST_STABLE = WICKET_1_4;

    private static interface Release {
        public static final String V1_3 = "1.3.7";
        public static final String V1_4 = "1.4.13";
        public static final String V1_5 = "1.5-M3"; // -> when change -> check if new dtd available for 1.5
    }

    private final String name;
    private final LibraryInfo[] core;
    private final LibraryInfo[] dateTime;
    private final LibraryInfo[] extensions;
    private final LibraryInfo[] velocity;
    private final LibraryInfo[] spring;
    private final LibraryInfo[] springAnnotations;
    private final LibraryInfo[] jmx;
    private final LibraryInfo[] authRoles;
    private final LibraryInfo[] guice;
    private String dtd;

    private WicketVersion(@NotNull String name,
                          @NotNull String dtd,
                          LibraryInfo[] core,
                          LibraryInfo[] dateTime,
                          LibraryInfo[] extensions,
                          LibraryInfo[] velocity,
                          LibraryInfo[] spring,
                          LibraryInfo[] springAnnotations,
                          LibraryInfo[] jmx,
                          LibraryInfo[] authRoles,
                          LibraryInfo[] guice) {
        this.name = name;
        this.dtd = dtd;
        this.core = core;
        this.dateTime = dateTime;
        this.extensions = extensions;
        this.velocity = velocity;
        this.spring = spring;
        this.springAnnotations = springAnnotations;
        this.jmx = jmx;
        this.authRoles = authRoles;
        this.guice = guice;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getDtd() {
        return dtd;
    }

    public LibraryInfo[] getCore() {
        return core;
    }

    public LibraryInfo[] getDateTime() {
        return dateTime;
    }

    public LibraryInfo[] getExtensions() {
        return extensions;
    }

    public LibraryInfo[] getVelocity() {
        return velocity;
    }

    public LibraryInfo[] getSpring() {
        return spring;
    }

    public LibraryInfo[] getSpringAnnotations() {
        return springAnnotations;
    }

    public LibraryInfo[] getJmx() {
        return jmx;
    }

    public LibraryInfo[] getAuthRoles() {
        return authRoles;
    }

    public LibraryInfo[] getGuice() {
        return guice;
    }

    public String toString() {
        return name;
    }

    public boolean isAtLeast(WicketVersion level) {
      return compareTo(level) >= 0;
    }
}
