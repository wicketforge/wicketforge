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

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.facet.WicketForgeFacet;
import wicketforge.util.FilenameConstants;
import wicketforge.util.WicketFilenameUtil;

import java.util.Collections;
import java.util.List;

final class ResourceInfo {
    @NotNull
    public final String qualifiedName;
    @Nullable
    public final String locale;

    private ResourceInfo(@Nullable String packageName, @NotNull String className, @Nullable String locale) {
        this.qualifiedName = packageName == null ? className : packageName + '.' + className;
        this.locale = locale;
    }

    @Nullable
    public static ResourceInfo from(@NotNull VirtualFile file, @NotNull Project project) {
        FileType fileType = file.getFileType();
        if (StdFileTypes.HTML.equals(file.getFileType())) {
            return from(file, project, FilenameConstants.MARKUP_EXTENSIONS);
        } else if (StdFileTypes.PROPERTIES.equals(fileType) || StdFileTypes.XML.equals(fileType)) {
            return from(file, project, FilenameConstants.PROPERTIES_EXTENSIONS);
        }
        return null;
    }

    @Nullable
    private static ResourceInfo from(@NotNull VirtualFile file, @NotNull Project project, @NotNull String[] fileExtensions) {
        VirtualFile dir = file.getParent();
        if (dir == null || !dir.isDirectory()) {
            return null;
        }

        String packageName = getPackageNameFromAdditionalResourcePaths(file, project);
        if (packageName == null) {
            packageName = ProjectRootManager.getInstance(project).getFileIndex().getPackageNameByDirectory(dir);
        }

        // extract className from filename -> remove extensions
        String className = WicketFilenameUtil.removeExtension(file.getName(), fileExtensions);
        // extract locale
        String locale = WicketFilenameUtil.extractLocale(className);
        className = StringUtil.replace(WicketFilenameUtil.extractBasename(className), "$", ".");
        return new ResourceInfo(packageName, className, locale);
    }

    @Nullable
    private static String getPackageNameFromAdditionalResourcePaths(@NotNull VirtualFile file, @NotNull Project project) {
        VirtualFile dir = file.getParent();
        if (dir == null || !dir.isDirectory()) {
            return null;
        }
        List<Module> modules = new SmartList<Module>();
        Module module = ModuleUtil.findModuleForFile(file, project);
        if (module != null) {
            // if we have a module -> only get resourcepaths from this one
            modules.add(module);
        } else {
            // else scan all modules
            Collections.addAll(modules, ModuleManager.getInstance(project).getModules());
        }
        for (Module module1 : modules) {
            WicketForgeFacet facet = WicketForgeFacet.getInstance(module1);
            if (facet != null) {
                for (VirtualFilePointer virtualFilePointer : facet.getResourcePaths()) {
                    VirtualFile virtualFile = virtualFilePointer.getFile();
                    if (virtualFile != null && virtualFile.isValid()) {
                        String packageName = VfsUtil.getRelativePath(dir, virtualFile, '.');
                        if (packageName != null) {
                            return packageName;
                        }
                    }
                }
            }
        }
        return null;
    }
}
