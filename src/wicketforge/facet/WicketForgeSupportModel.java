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
package wicketforge.facet;

import com.intellij.openapi.module.Module;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.facet.ui.WicketVersion;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class WicketForgeSupportModel {
    private WicketVersion version;
    private boolean dateTime;
    private boolean extensions;
    private boolean velocity;
    private boolean spring;
    private boolean guice;
    private boolean jmx;
    private boolean springAnnot;
    private boolean authRoles;

    private WicketForgeSupportModel() { }

    @NotNull
    public static WicketForgeSupportModel createModel(@NotNull Module module) {
        WicketForgeSupportModel model = new WicketForgeSupportModel();

        JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(module.getProject());
        GlobalSearchScope scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module);

        if (psiFacade.findClass("org.apache.wicket.Application", scope) != null) {
            model.version = evaluateVersion(psiFacade, scope);
            model.dateTime = psiFacade.findClass("org.apache.wicket.datetime.DateConverter", scope) != null;
            model.velocity = psiFacade.findClass("org.apache.wicket.velocity.Initializer", scope) != null;
            model.extensions = psiFacade.findClass("org.apache.wicket.extensions.Initializer", scope) != null;
            model.spring = psiFacade.findClass("org.apache.wicket.spring.SpringWebApplication", scope) != null;
            model.springAnnot = psiFacade.findClass("org.apache.wicket.spring.injection.annot.AnnotSpringInjector", scope) != null;
            model.guice = psiFacade.findClass("org.apache.wicket.guice.GuiceWebApplication", scope) != null;
            model.jmx = psiFacade.findClass("org.apache.wicket.jmx.Application", scope) != null;
            model.authRoles = psiFacade.findClass("org.apache.wicket.authentication.AuthenticatedWebApplication", scope) != null;
        }

        return model;
    }

    private static WicketVersion evaluateVersion(JavaPsiFacade psiFacade, GlobalSearchScope scope) {
        PsiClass c = psiFacade.findClass("org.apache.wicket.Component", scope);
        if (c == null) {
            return null;
        }

        List<String> methods = new ArrayList<String>();
        for (PsiMethod m : c.getMethods()) {
            methods.add(m.getName());
        }
        if (methods.contains("getMarkup")) {
            return WicketVersion.WICKET_1_5;
        } else if (methods.contains("getDefaultModel")) {
            return WicketVersion.WICKET_1_4;
        }
        return WicketVersion.WICKET_1_3;
    }

    @Nullable
    public WicketVersion getVersion() {
        return version;
    }

    public boolean isDateTime() {
        return dateTime;
    }

    public boolean isExtensions() {
        return extensions;
    }

    public boolean isVelocity() {
        return velocity;
    }

    public boolean isSpring() {
        return spring;
    }

    public boolean isGuice() {
        return guice;
    }

    public boolean isJmx() {
        return jmx;
    }

    public boolean isSpringAnnot() {
        return springAnnot;
    }

    public boolean isAuthRoles() {
        return authRoles;
    }
}
