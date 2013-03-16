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
package wicketforge;

import com.intellij.CommonBundle;
import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.ide.util.PackageUtil;
import com.intellij.lang.properties.PropertiesUtil;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.psi.*;
import com.intellij.psi.impl.JavaConstantExpressionEvaluator;
import com.intellij.psi.impl.compiled.ClsClassImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectAndLibrariesScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.PackageWrapper;
import com.intellij.refactoring.move.moveClassesOrPackages.MoveClassesOrPackagesUtil;
import com.intellij.refactoring.util.RefactoringMessageUtil;
import com.intellij.refactoring.util.RefactoringUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.Query;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.facet.WicketForgeFacet;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Collection of utility methods for the plugin.
 */
public final class WicketForgeUtil {
    private WicketForgeUtil() {
    }

    /**
     * Returns true if the passed PsiClass instance is a subclass of Wicket's Component class.
     * This method works with Wicket versions through 1.3.
     *
     * @param clazz PsiClass
     * @return boolean
     */
    public static boolean isWicketComponent(@NotNull PsiClass clazz) {
        return isInheritor(clazz, Constants.WICKET_COMPONENT);
    }

    /**
     * Returns true if the passed PsiClass instance is a Wicket ResourceModel class.
     * This method works with Wicket versions through 1.3.
     *
     * @param clazz PsiClass
     * @return boolean
     */
    public static boolean isWicketResourceModel(@NotNull PsiClass clazz) {
        return isInheritor(clazz, Constants.WICKET_RESOURCEMODEL, Constants.WICKET_STRINGRESOURCEMODEL);
    }

    /**
     * Returns true if the PsiClass is an instance of a wicket page.
     *
     * @param clazz PsiClass
     * @return true if instance of a wicket page
     */
    public static boolean isWicketPage(@NotNull final PsiClass clazz) {
        return isInheritor(clazz, Constants.WICKET_PAGE);
    }

    /**
     * Returns true if the PsiClass is an instance of a wicket panel.
     *
     * @param clazz PsiClass
     * @return true if instance of a wicket panel
     */
    public static boolean isWicketPanel(@NotNull final PsiClass clazz) {
        return isInheritor(clazz, Constants.WICKET_PANEL);
    }

    /**
     * Returns true if the PsiClass is an instance of a wicket Page or WebMarkupContainerWithAssociatedMarkup.
     *
     * @param clazz PsiClass
     * @return true if instance of a wicket Page or WebMarkupContainerWithAssociatedMarkup
     */
    public static boolean isWicketComponentWithAssociatedMarkup(@NotNull final PsiClass clazz) {
        return isInheritor(clazz, Constants.WICKET_PAGE, Constants.WICKET_PANEL, Constants.WICKET_FORMCOMPONENTPANEL);
    }

    /**
     * Returns true if the PsiClass is an instance of a MarkupContainer.
     *
     * @param clazz PsiClass
     * @return true if instance of a MarkupContainer
     */
    public static boolean isMarkupContainer(@NotNull final PsiClass clazz) {
        return isInheritor(clazz, "org.apache.wicket.MarkupContainer");
    }

    private static boolean isInheritor(@NotNull PsiClass candidateClass, @NotNull String... baseClassQualifiedNames) {
        PsiClass workClass = candidateClass;
        while (workClass != null) {
            String candidateClassQualifiedName = workClass.getQualifiedName();
            if (candidateClassQualifiedName != null) { // just for first time, cause anonymous class returns null...
                for (String baseClassQualifiedName : baseClassQualifiedNames) {
                    if (baseClassQualifiedName.equals(candidateClassQualifiedName)) {
                        return true;
                    }
                }
            }
            workClass = workClass.getSuperClass();
        }
        return false;
        /* same (safer?) implementation thru ideas classes...
        Project project = candidateClass.getProject();
        JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
        for (String baseClassQualifiedName : baseClassQualifiedNames) {
            PsiClass superClass = psiFacade.findClass(baseClassQualifiedName, GlobalSearchScope.allScope(project));
            if (superClass != null && candidateClass.isInheritor(superClass, true)) {
                return true;
            }
        }
        return false;
        */
    }

    /**
     * Returns the markup file for the passed PsiClass.  Null is returned if the markup file cannot be found.
     *
     * @param psiClass the PsiClass
     * @return the markup PsiFile or null if no such file exists.
     */
    @Nullable
    public static PsiFile getMarkupFile(@NotNull PsiClass psiClass) {
        return getResourceFile(psiClass, getMarkupFileName(psiClass));
    }

    /**
     * Returns the markup file for the passed PsiClass.  Null is returned if the markup file cannot be found.
     *
     * @param psiClass the PsiClass
     * @return the markup PsiFile or null if no such file exists.
     */
    @Nullable
    public static PropertiesFile getPropertiesFile(@NotNull PsiClass psiClass) {
        PsiFile psiFile = getResourceFile(psiClass, getPropertiesFileName(psiClass, Constants.PropertiesType.PROPERTIES), getPropertiesFileName(psiClass, Constants.PropertiesType.XML));
        return PropertiesUtil.getPropertiesFile(psiFile);
    }

    /**
     * Returns a resource file from same package like PsiClass.
     *
     * @param psiClass the PsiClass
     * @param resourceNames resourceNames to Find
     * @return the markup PsiFile or null if no such file exists.
     */
    @Nullable
    private static PsiFile getResourceFile(@NotNull PsiClass psiClass, @NotNull String... resourceNames) {
        PsiFile psiFile = psiClass.getContainingFile();
        if (!(psiFile instanceof PsiJavaFile)) {
            return null;
        }

        PsiPackage psiPackage = JavaPsiFacade.getInstance(psiClass.getProject()).findPackage(((PsiJavaFile) psiFile).getPackageName());
        if (psiPackage == null) {
            return null;
        }

        // try first to find resource from alternate file paths
        Module module = ModuleUtil.findModuleForPsiElement(psiClass);
        if (module != null) {
            WicketForgeFacet wicketForgeFacet = WicketForgeFacet.getInstance(module);
            if (wicketForgeFacet != null) {
                if (!wicketForgeFacet.getResourcePaths().isEmpty()) {
                    String relPath = psiPackage.getQualifiedName().replace('.', '/') + '/';
                    for (VirtualFilePointer virtualFilePointer : wicketForgeFacet.getResourcePaths()) {
                        VirtualFile virtualFile = virtualFilePointer.getFile();
                        if (virtualFile != null && virtualFile.isValid()) {
                            virtualFile = virtualFile.findFileByRelativePath(relPath);
                            if (virtualFile != null && virtualFile.isValid()) {
                                PsiDirectory psiDirectory = PsiManager.getInstance(module.getProject()).findDirectory(virtualFile);
                                if (psiDirectory != null) {
                                    for (String resourceName : resourceNames) {
                                        PsiFile file = psiDirectory.findFile(resourceName);
                                        if (file != null) {
                                            return file;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // try to find in classpath
        GlobalSearchScope scope = module != null ? GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module) : GlobalSearchScope.allScope(psiClass.getProject());
        for (PsiDirectory psiDirectory : psiPackage.getDirectories(scope)) {
            for (String resourceName : resourceNames) {
                PsiFile file = psiDirectory.findFile(resourceName);
                if (file != null) {
                    return file;
                }
            }
        }
        return null;
    }

    /**
     * Returns the class of an associated markup file
     *
     * @param psiFile the markup file
     * @return the associated PsiClass or null if no such class exists.
     */
    @Nullable
    public static PsiClass getMarkupClass(@NotNull PsiFile psiFile) {
        PsiDirectory psiDirectory = psiFile.getContainingDirectory();
        if (psiDirectory == null) {
            return null;
        }

        PsiPackage psiPackage = null;

        // try first to find package from alternate file paths
        Module module = ModuleUtil.findModuleForPsiElement(psiFile);
        if (module != null) {
            WicketForgeFacet wicketForgeFacet = WicketForgeFacet.getInstance(module);
            if (wicketForgeFacet != null) {
                for (VirtualFilePointer virtualFilePointer : wicketForgeFacet.getResourcePaths()) {
                    VirtualFile virtualFile = virtualFilePointer.getFile();
                    if (virtualFile != null && virtualFile.isValid()) {
                        if (VfsUtil.isAncestor(virtualFile, psiDirectory.getVirtualFile(), false)) {
                            String packageName = VfsUtil.getRelativePath(psiDirectory.getVirtualFile(), virtualFile, '.');
                            psiPackage = JavaPsiFacade.getInstance(psiFile.getProject()).findPackage(packageName == null ? "" : packageName);
                            if (psiPackage != null) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        // if package not already resolved (from alternate file paths) -> get Package from dir
        if (psiPackage == null) {
            psiPackage = JavaDirectoryService.getInstance().getPackage(psiDirectory);
        }
        if (psiPackage == null) {
            return null;
        }
        //
        StringBuilder sb = new StringBuilder(psiPackage.getQualifiedName());
        if (sb.length() > 0) {
            sb.append('.');
        }
        String filename = psiFile.getName();
        int index = filename.lastIndexOf('.');
        if (index >= 0) {
            filename = filename.substring(0, index);
        }
        sb.append(StringUtil.replace(filename, "$", "."));

        GlobalSearchScope scope = module != null ? GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module) : GlobalSearchScope.allScope(psiFile.getProject()); 
        PsiClass psiClass = JavaPsiFacade.getInstance(psiFile.getProject()).findClass(sb.toString(), scope);
        if (psiClass instanceof ClsClassImpl) {
            PsiClass sourceMirrorClass = ((ClsClassImpl) psiClass).getSourceMirrorClass();
            if (sourceMirrorClass != null) {
                psiClass = sourceMirrorClass;
            }
        }
        return psiClass;
    }

    /**
     * Returns the markup file name that is associated with the class
     *
     * @param clazz the PsiClass
     * @return the markup file name
     */
    @NotNull
    public static String getMarkupFileName(@NotNull PsiClass clazz) {
        return getResourceFileName(clazz) + ".html";
    }

    /**
     * Returns the markup file name that is associated with the class
     *
     * @param clazz the PsiClass
     * @return the markup file name
     */
    @NotNull
    public static String getPropertiesFileName(@NotNull PsiClass clazz, @NotNull Constants.PropertiesType propertiesType) {
        switch (propertiesType) {
            case PROPERTIES:
                return getResourceFileName(clazz) + ".properties";
            case XML:
                Module module = ModuleUtil.findModuleForPsiElement(clazz);
                return getResourceFileName(clazz) + "." + WicketVersion.getVersion(module).getXmlPropertiesFileExtension();
            default:
                throw new IllegalArgumentException("Unsupported PropertiesType " + propertiesType);
        }
    }

    /**
     * Return the (resources) name of a PsiClass (works for inner classes too).
     *
     * @param clazz The PsiClass
     * @return      ResourceFileName ex 'MyClass' or 'MyClass$MyInnerClass'
     */
    @NotNull
    private static String getResourceFileName(@NotNull PsiClass clazz) {
        StringBuilder sb = new StringBuilder(clazz.getName());

        PsiClass workPsiClass = clazz;
        while ((workPsiClass = workPsiClass.getContainingClass()) != null) {
            sb.insert(0, '$').insert(0, workPsiClass.getName());
        }

        return sb.toString();
    }

    /**
     * @param element   PsiElement
     * @return          PsiClass of Page/Pane from element
     */
    @Nullable
    public static PsiClass getParentWicketClass(@NotNull PsiElement element) {
        PsiClass psiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class, false);
        while (psiClass != null) {
            if (WicketForgeUtil.isWicketComponentWithAssociatedMarkup(psiClass)) {
                return getConcreteClass(psiClass);
            }
            psiClass = PsiTreeUtil.getParentOfType(psiClass, PsiClass.class, true);
        }
        return null;
    }

    @Nullable
    public static PsiClass getConcreteClass(@Nullable PsiClass psiClass) {
        while (psiClass != null && psiClass.getName() == null) { // parentWicketClass needs a name (ex anonymous classes dont have) so we get its superclass (issue 48)
            psiClass = psiClass.getSuperClass();
        }
        return psiClass;
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
            protected void run(Result<VirtualFile> result) throws Throwable {
                VirtualFile[] roots = getResourceRoots(module);
                if (roots.length == 0) return;

                if (roots.length == 1) {
                    result.setResult(roots[0]);
                } else {
                    PsiDirectory defaultDir = PackageUtil.findPossiblePackageDirectoryInModule(module, packageName);
                    result.setResult(MoveClassesOrPackagesUtil.chooseSourceRoot(targetPackage, roots, defaultDir));
                }
            }
        }.execute().getResultObject();

        if (selectedRoot == null) {
            return null;
        }

        try {
            return new WriteCommandAction<PsiDirectory>(project, CodeInsightBundle.message("create.directory.command")) {
                protected void run(Result<PsiDirectory> result) throws Throwable {
                    result.setResult(RefactoringUtil.createPackageDirectoryInSourceRoot(targetPackage, selectedRoot));
                }
            }.execute().getResultObject();
        } catch (IncorrectOperationException e) {
            Messages.showMessageDialog(project, e.getMessage(), CommonBundle.getErrorTitle(), Messages.getErrorIcon());
            return null;
        }
    }

    @Nullable
    public static PsiClass findWicketApplicationClass(@NotNull Project project) {
        PsiClass wicketApplicationClass =
            JavaPsiFacade.getInstance(project).findClass(Constants.WICKET_APPLICATION, new ProjectAndLibrariesScope(project));

        if (wicketApplicationClass == null) {
            return null;
        }

        Query<PsiClass> query = ClassInheritorsSearch.search(wicketApplicationClass, new ProjectAndLibrariesScope(project), true);
        Collection<PsiClass> matches = query.findAll();
        if (matches.isEmpty()) {
            return null;
        }

        // iterate over the matches and return the first class we find that isn't in org.apache.wicket
        for (PsiClass match : matches) {
            String qualifiedName = match.getQualifiedName();
            if (qualifiedName != null && !qualifiedName.contains("org.apache.wicket")) {
                return match;
            }
        }
        return null;
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
        fillWicketProperties(directory, props);
        try {
            return FileTemplateUtil.createFromTemplate(template, fileName, props, directory);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create template for '" + fileName + "'", e);
        }
    }

    private static void fillWicketProperties(@NotNull PsiElement psiElement, @NotNull Properties props) {
        Module module = ModuleUtil.findModuleForPsiElement(psiElement);
        props.put(Constants.PROP_WICKET_DTD, WicketVersion.getVersion(module).getDtd());
    }

    @Nullable
    // todo mm -> check if we can deprecate this -> i think this one should be like wicketforge.psi.hierarchy.ClassWicketIdReferences -> resolveClassFromNewExpression
    public static PsiClass getClassFromNewExpression(@NotNull PsiNewExpression expression) {
        PsiMethod constructor = expression.resolveConstructor();
        if (constructor == null || !constructor.getContainingFile().isPhysical()) {
            return null;
        }
        return constructor.getContainingClass();
    }

    @Nullable
    public static PsiExpression getWicketIdExpressionFromArguments(@NotNull PsiNewExpression expression) {
        PsiExpressionList expressionList = expression.getArgumentList();
        if (expressionList != null) {
            PsiExpression[] psiExpressions = expressionList.getExpressions();
            if (psiExpressions.length > 0) {
                return psiExpressions[0];
            }
        }
        return null;
    }

    @Nullable
    public static String getWicketIdFromExpression(@NotNull PsiExpression expression) {
        Object object = JavaConstantExpressionEvaluator.computeConstantExpression(expression, false);
        return object instanceof String ? (String) object : null;
    }
}
