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
package wicketforge.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.JavaConstantExpressionEvaluator;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.Constants;

import java.util.Collection;

/**
 * Collection of utility methods for the plugin.
 */
public final class WicketPsiUtil {
    private WicketPsiUtil() {
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
        return isInheritor(clazz, Constants.WICKET_PANEL, Constants.WICKET_FORMCOMPONENTPANEL);
    }

    /**
     * Returns true if the PsiClass is an instance of a wicket border.
     *
     * @param clazz PsiClass
     * @return true if instance of a wicket border
     */
    public static boolean isWicketBorder(@NotNull final PsiClass clazz) {
        return isInheritor(clazz, Constants.WICKET_BORDER);
    }

    /**
     * Returns true if the PsiClass is an instance of a wicket Page or WebMarkupContainerWithAssociatedMarkup.
     *
     * @param clazz PsiClass
     * @return true if instance of a wicket Page or WebMarkupContainerWithAssociatedMarkup
     */
    public static boolean isWicketComponentWithAssociatedMarkup(@NotNull final PsiClass clazz) {
        return isInheritor(clazz, Constants.WICKET_PAGE, Constants.WICKET_PANEL, Constants.WICKET_FORMCOMPONENTPANEL, Constants.WICKET_BORDER);
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
     * @param element   PsiElement
     * @return          PsiClass of Page/Pane from element
     */
    @Nullable
    public static PsiClass getParentWicketClass(@NotNull PsiElement element) {
        PsiClass psiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class, false);
        while (psiClass != null) {
            if (WicketPsiUtil.isWicketComponentWithAssociatedMarkup(psiClass)) {
                return getConcreteClass(psiClass);
            }
            psiClass = PsiTreeUtil.getParentOfType(psiClass, PsiClass.class, true);
        }
        return null;
    }

    @Nullable
    private static PsiClass getConcreteClass(@Nullable PsiClass psiClass) {
        while (psiClass != null && psiClass.getName() == null) { // parentWicketClass needs a name (ex anonymous classes dont have) so we get its superclass (issue 48)
            psiClass = psiClass.getSuperClass();
        }
        return psiClass;
    }

    @Nullable
    public static PsiClass findWicketApplicationClass(@NotNull Project project) {
        PsiClass wicketApplicationClass =
            JavaPsiFacade.getInstance(project).findClass(Constants.WICKET_APPLICATION, GlobalSearchScope.allScope(project));

        if (wicketApplicationClass == null) {
            return null;
        }

        Query<PsiClass> query = ClassInheritorsSearch.search(wicketApplicationClass, GlobalSearchScope.allScope(project), true);
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

    /**
     * @param element
     * @return true if element is in library
     */
    public static boolean isInLibrary(@Nullable PsiElement element) {
        if (element != null) {
            VirtualFile vf = PsiUtil.getVirtualFile(element);
            if (vf != null) {
                return WicketFileUtil.isInLibrary(vf, element.getProject());
            }
        }
        return false;
    }
}
