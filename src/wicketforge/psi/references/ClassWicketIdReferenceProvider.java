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
package wicketforge.psi.references;

import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import wicketforge.facet.WicketForgeFacet;
import wicketforge.search.MarkupIndex;
import wicketforge.util.WicketPsiUtil;

/**
 */
public class ClassWicketIdReferenceProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        PsiExpressionList expressionList = (PsiExpressionList) element.getParent();
        PsiExpression[] expressions = expressionList.getExpressions();
        if (expressions.length > 0 && expressions[0].equals(element)) {
            if (WicketForgeFacet.hasFacetOrIsFromLibrary(element)) {
                PsiElement parent = expressionList.getParent(); // can be PsiCallExpression or PsiAnonymousClass
                PsiCallExpression callExpression = (PsiCallExpression) (parent instanceof PsiCallExpression ? parent : parent.getParent());
                PsiClass classToBeCreated = WicketPsiUtil.getClassToBeCreated(callExpression);
                if (classToBeCreated != null && WicketPsiUtil.isWicketComponent(classToBeCreated) && !WicketPsiUtil.isWicketPage(classToBeCreated)) {
                    PsiClass wicketClass = WicketPsiUtil.getParentWicketClass(callExpression);
                    if (wicketClass != null && MarkupIndex.getBaseFile(wicketClass) != null) {
                        return new PsiReference[] {new ClassWicketIdReference((PsiLiteralExpression) element, wicketClass)};
                    }
                }
            }
        }
        return PsiReference.EMPTY_ARRAY;
    }
}
