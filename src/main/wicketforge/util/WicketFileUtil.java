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
package wicketforge.util;

import com.intellij.CommonBundle;
import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.ide.util.PackageUtil;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.refactoring.PackageWrapper;
import com.intellij.refactoring.move.moveClassesOrPackages.MoveClassesOrPackagesUtil;
import com.intellij.refactoring.util.RefactoringMessageUtil;
import com.intellij.refactoring.util.RefactoringUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.Constants;
import wicketforge.facet.WicketForgeFacet;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public final class WicketFileUtil {
    private WicketFileUtil() {
    }

    @NotNull
    public static VirtualFile[] getResourceRoots(@NotNull Module module) {
        // all module source roots
        VirtualFile[] result = ModuleRootManager.getInstance(module).getSourceRoots();
        // alternate paths
        WicketForgeFacet wicketForgeFacet = WicketForgeFacet.getInstance(module);
        if (wicketForgeFacet != null) {
            List<VirtualFile> alternateFiles = new SmartList<VirtualFile>();
            // add all valid alternate paths to list
            for (VirtualFilePointer virtualFilePointer : wicketForgeFacet.getResourcePaths()) {
                VirtualFile virtualFile = virtualFilePointer.getFile();
                if (virtualFile != null && virtualFile.isValid()) {
                    alternateFiles.add(virtualFile);
                }
            }
            // if we have valid alternate paths
            if (!alternateFiles.isEmpty()) {
                // add all module source roots and list as new result
                alternateFiles.addAll(Arrays.asList(result));
                result = alternateFiles.toArray(new VirtualFile[alternateFiles.size()]);
            }
        }
        //
        return result;
    }

    /**
     * @param packageName   PackageName like 'com.foo.bar'
     * @param project       Project
     * @param module        Module
     * @return              Selected Directory or null if canceled/error
     */
    @Nullable
    public static PsiDirectory selectTargetDirectory(@NotNull final String packageName, @NotNull final Project project, @NotNull final Module module) {
        final PackageWrapper targetPackage = new PackageWrapper(PsiManager.getInstance(project), packageName);

        final VirtualFile selectedRoot = new ReadAction<VirtualFile>() {
            @Override
            protected void run(@NotNull Result<VirtualFile> result) throws Throwable {
                VirtualFile[] roots = getResourceRoots(module);
                if (roots.length == 0) return;

                if (roots.length == 1) {
                    result.setResult(roots[0]);
                } else {
                    PsiDirectory defaultDir = PackageUtil.findPossiblePackageDirectoryInModule(module, packageName);
                    result.setResult(MoveClassesOrPackagesUtil.chooseSourceRoot(targetPackage, new SmartList<VirtualFile>(roots), defaultDir));
                }
            }
        }.execute().getResultObject();

        if (selectedRoot == null) {
            return null;
        }

        try {
            return new WriteCommandAction<PsiDirectory>(project, CodeInsightBundle.message("create.directory.command")) {
                @Override
                protected void run(@NotNull Result<PsiDirectory> result) throws Throwable {
                    result.setResult(RefactoringUtil.createPackageDirectoryInSourceRoot(targetPackage, selectedRoot));
                }
            }.execute().getResultObject();
        } catch (IncorrectOperationException e) {
            Messages.showMessageDialog(project, e.getMessage(), CommonBundle.getErrorTitle(), Messages.getErrorIcon());
            return null;
        }
    }

    /**
     * Creates and returns the file for the passed PsiClass.
     *
     * @param fileName     the name of the file to create
     * @param directory    the directory to create in
     * @param templateName the Markup Template name
     * @return the created Element from Template
     */
    @Nullable
    public static PsiElement createFileFromTemplate(@NotNull String fileName, @NotNull PsiDirectory directory, @NotNull String templateName) {
        String errorMessage = RefactoringMessageUtil.checkCanCreateFile(directory, fileName);
        if (errorMessage != null) {
            Messages.showMessageDialog(directory.getProject(), errorMessage, CommonBundle.getErrorTitle(), Messages.getErrorIcon());
            return null;
        }

        final FileTemplate template = FileTemplateManager.getInstance().getJ2eeTemplate(templateName);

        Properties props = FileTemplateManager.getInstance().getDefaultProperties();
        props.put(Constants.PROP_WICKET_NS, WicketVersion.getVersion(directory).getNS());
        try {
            return FileTemplateUtil.createFromTemplate(template, fileName, props, directory);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create template for '" + fileName + "'", e);
        }
    }

    /**
     * @param vf
     * @return true if file is in library
     */
    public static boolean isInLibrary(@NotNull VirtualFile vf, @NotNull Project project) {
        ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
        return projectFileIndex.isInLibrarySource(vf) || projectFileIndex.isInLibraryClasses(vf);
    }
}
