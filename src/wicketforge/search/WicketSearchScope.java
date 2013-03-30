/*
 * Copyright 2013 The WicketForge-Team
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
package wicketforge.search;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopes;
import org.jetbrains.annotations.NotNull;
import wicketforge.facet.WicketForgeFacet;

public final class WicketSearchScope {
    private WicketSearchScope() {
    }

    @NotNull
    public static GlobalSearchScope resourcesInModuleWithDependenciesAndLibraries(@NotNull Module module) {
        GlobalSearchScope scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, true);
        // add all additional resource paths
        WicketForgeFacet facet = WicketForgeFacet.getInstance(module);
        if (facet != null) {
            for (VirtualFilePointer filePointer : facet.getResourcePaths()) {
                VirtualFile virtualFile = filePointer.getFile();
                if (virtualFile != null) {
                    scope = scope.uniteWith(GlobalSearchScopes.directoryScope(module.getProject(), virtualFile, true));
                }
            }
        }
        return scope;
    }

    @NotNull
    public static GlobalSearchScope classInModuleWithDependenciesAndLibraries(@NotNull Module module) {
        return GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, true);
    }
}
