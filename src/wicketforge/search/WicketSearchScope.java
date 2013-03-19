package wicketforge.search;

import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopes;
import org.jetbrains.annotations.NotNull;
import wicketforge.facet.WicketForgeFacet;

public final class WicketSearchScope {
    public WicketSearchScope() {
    }

    public static GlobalSearchScope resourcesInModuleWithDependenciesAndLibraries(@NotNull Module module, boolean includeTests) {
        GlobalSearchScope scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, includeTests);
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

    public static GlobalSearchScope classInModuleWithDependenciesAndLibraries(@NotNull Module module, boolean includeTests) {
        return GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module);
    }
}
