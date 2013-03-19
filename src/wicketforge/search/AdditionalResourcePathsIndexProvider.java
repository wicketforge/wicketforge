package wicketforge.search;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.util.indexing.IndexableSetContributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.facet.WicketForgeFacet;

import java.util.HashSet;
import java.util.Set;

public class AdditionalResourcePathsIndexProvider extends IndexableSetContributor {
    @NotNull
    @Override
    public Set<VirtualFile> getAdditionalProjectRootsToIndex(@Nullable Project project) {
        if (project != null) {
            Set<VirtualFile> files = new HashSet<VirtualFile>();
            for (Module module : ModuleManager.getInstance(project).getModules()) {
                WicketForgeFacet facet = WicketForgeFacet.getInstance(module);
                if (facet != null) {
                    for (VirtualFilePointer filePointer : facet.getResourcePaths()) {
                        VirtualFile file = filePointer.getFile();
                        if (file != null) {
                            files.add(file);
                        }
                    }
                }
            }
            return files;
        }
        return EMPTY_FILE_SET;
    }

    @Override
    public Set<VirtualFile> getAdditionalRootsToIndex() {
        return EMPTY_FILE_SET;
    }
}
