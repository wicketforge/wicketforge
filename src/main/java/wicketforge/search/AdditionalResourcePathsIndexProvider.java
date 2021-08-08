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
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.util.indexing.IndexableSetContributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.facet.WicketForgeFacet;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AdditionalResourcePathsIndexProvider extends IndexableSetContributor {

    private static final Set<VirtualFile> EMPTY_FILE_SET = Collections.emptySet();

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
