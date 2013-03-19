package wicketforge.search;

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

import java.util.Collections;
import java.util.List;

public final class WicketIndexUtil {
    private WicketIndexUtil() {
    }

    @Nullable
    public static ResourceInfo getClassNameFromMarkup(@NotNull VirtualFile file, @NotNull Project project) {
        return getClassNameFromResource(file, project, ".html");
    }

    @Nullable
    public static ResourceInfo getClassNameFromProperties(@NotNull VirtualFile file, @NotNull Project project) {
        return getClassNameFromResource(file, project, ".properties.xml", ".properties", ".xml");
    }

    @Nullable
    private static ResourceInfo getClassNameFromResource(@NotNull VirtualFile file, @NotNull Project project, @NotNull String... fileExtensions) {
        VirtualFile dir = file.getParent();
        if (dir == null || !dir.isDirectory()) {
            return null;
        }

        String packageName = getPackageNameFromAdditionalResourcePaths(file, project);
        if (packageName == null) {
            packageName = ProjectRootManager.getInstance(project).getFileIndex().getPackageNameByDirectory(dir);
        }

        // extract className from filename -> remove extensions
        String className = file.getName();
        for (String fileExtension : fileExtensions) {
            if (className.endsWith(fileExtension)) {
                className = className.substring(0, className.length() - fileExtension.length());
            }
        }
        // extract locale
        int index = className.indexOf('_');
        String locale = index > 0 ? className.substring(index) : null;
        className = StringUtil.replace(index > 0 ? className.substring(0, index) : className, "$", ".");
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
            modules.add(module);
        } else {
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

    public static class ResourceInfo {
        public String qualifiedName;
        public String locale;

        public ResourceInfo(@Nullable String packageName, @NotNull String className, @Nullable String locale) {
            this.qualifiedName = packageName == null ? className : packageName + '.' + className;
            this.locale = locale;
        }
    }
}
