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
package wicketforge.visitor;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiNewExpression;
import org.jetbrains.annotations.NotNull;
import wicketforge.WicketForgeUtil;

@Deprecated
public class WicketClassVisitor extends JavaRecursiveElementVisitor {
    private PsiClass psiClass;

    public WicketClassVisitor() {
    }

    /**
     * @param psiClass only visit this psiClass with associatedMarkup. inner classes with own markup will not de visited.
     */
    public WicketClassVisitor(@NotNull PsiClass psiClass) {
        this.psiClass = psiClass;
    }

    @Override
    public void visitClass(PsiClass aClass) {
        if (psiClass != null && !aClass.equals(psiClass) && WicketForgeUtil.isWicketComponentWithAssociatedMarkup(aClass)) {
            // we do not visit inner classes that have own markup
            return;
        }
        super.visitClass(aClass);
    }

    @Override
    public void visitNewExpression(PsiNewExpression expression) {
        super.visitNewExpression(expression);

        PsiClass psiClass = WicketForgeUtil.getClassFromNewExpression(expression);
        if (psiClass == null) {
            return;
        }

        if (WicketForgeUtil.isWicketPage(psiClass)) {
            visitNewExpressionWicketPage(expression);
        } else if (WicketForgeUtil.isWicketComponent(psiClass)) {
            PsiExpression wicketIdExpression = WicketForgeUtil.getWicketIdExpressionFromArguments(expression);
            if (wicketIdExpression != null) {
                String wicketId = WicketForgeUtil.getWicketIdFromExpression(wicketIdExpression);
                if (wicketId != null) {
                    visitNewExpressionWicketComponent(wicketId, wicketIdExpression, expression);
                }
            }
        }
    }

    public void visitNewExpressionWicketPage(PsiNewExpression expression) {
        //
    }

    public void visitNewExpressionWicketComponent(String wicketId, PsiExpression expressionId, PsiNewExpression expressionNew) {
        //
    }
}
