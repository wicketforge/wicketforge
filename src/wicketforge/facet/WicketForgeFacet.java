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

import com.intellij.facet.Facet;
import com.intellij.facet.FacetManager;
import com.intellij.facet.FacetType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * WicketForgeFacet
 */
public class WicketForgeFacet extends Facet<WicketForgeFacetConfiguration> {
    private List<VirtualFilePointer> resourcePaths;

    public WicketForgeFacet(@NotNull FacetType facetType, @NotNull Module module, String name, @NotNull WicketForgeFacetConfiguration configuration, Facet underlyingFacet) {
        super(facetType, module, name, configuration, underlyingFacet);
    }

    @NotNull
    public List<VirtualFilePointer> getResourcePaths() {
        if (resourcePaths == null) {
            resourcePaths = new ArrayList<VirtualFilePointer>();
            for (String resourceUrl : getConfiguration().resourceUrls) {
                resourcePaths.add(VirtualFilePointerManager.getInstance().create(resourceUrl, getModule(), null));
            }
        }
        return resourcePaths;
    }

    public void setResourcePaths(@NotNull List<VirtualFilePointer> resourcePaths) {
        this.resourcePaths = resourcePaths;
        WicketForgeFacetConfiguration configuration = getConfiguration();
        configuration.resourceUrls.clear();
        for (VirtualFilePointer virtualFilePointer : resourcePaths) {
            configuration.resourceUrls.add(FileUtil.toSystemIndependentName(virtualFilePointer.getUrl()));
        }
    }

    @Nullable
    public static WicketForgeFacet getInstance(@NotNull final Module module) {
        return FacetManager.getInstance(module).getFacetByType(WicketForgeFacetType.ID);
    }

    /**
     * Returns true if the passed Module contains the Wicket's Component class.
     * This method works with Wicket version 1.3 and higher.
     *
     * @param module Module
     * @return boolean
     */
    public static boolean isLibraryPresent(@Nullable Module module) {
        if (module == null) {
            return false;
        }
        JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(module.getProject());
        return psiFacade.findClass(Constants.WICKET_COMPONENT, GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module)) != null;
    }

    /**
     * @param element
     * @return true if element has WicketForgeFacet or is from library
     */
    public static boolean hasFacetOrIsFromLibrary(@Nullable PsiElement element) {
        if (element != null) {
            VirtualFile vf = PsiUtil.getVirtualFile(element);
            if (vf != null) {
                Project project = element.getProject();
                Module module = ModuleUtil.findModuleForFile(vf, project);
                // if we got a module -> check if WicketForgeFacet available
                if (module != null) {
                    return getInstance(module) != null;
                }
                // else check if file from lib
                return isFileFromLibrary(vf, project);
            }
        }
        return false;
    }

    /**
     * @param vf
     * @return true if file is from library
     */
    private static boolean isFileFromLibrary(@NotNull VirtualFile vf, @NotNull Project project) {
        ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
        return projectFileIndex.isInLibrarySource(vf) || projectFileIndex.isInLibraryClasses(vf);
    }

    /**
     * @param element
     * @return true if element is from library
     */
    public static boolean isFromLibrary(@Nullable PsiElement element) {
        if (element != null) {
            VirtualFile vf = PsiUtil.getVirtualFile(element);
            if (vf != null) {
                return isFileFromLibrary(vf, element.getProject());
            }
        }
        return false;
    }
}

