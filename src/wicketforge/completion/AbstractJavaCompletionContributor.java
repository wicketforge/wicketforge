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
package wicketforge.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.psi.*;

/**
 */
public abstract class AbstractJavaCompletionContributor extends CompletionContributor {

    protected PsiNewExpression getElementNewExpression(PsiJavaToken position) {
        if (!(position.getParent() instanceof PsiLiteralExpression)) {
            return null;
        }

        PsiLiteralExpression expression = (PsiLiteralExpression) position.getParent();
        if (!(expression.getParent() instanceof PsiExpressionList)) {
            return null;
        }

        PsiExpressionList expressionList = (PsiExpressionList) expression.getParent();
        if (!(expressionList.getParent() instanceof PsiNewExpression)) {
            return null;
        }

        PsiNewExpression newExpression = (PsiNewExpression) expressionList.getParent();
        PsiMethod constructor = newExpression.resolveConstructor();
        if (constructor == null || !constructor.getContainingFile().isPhysical()) {
            return null;
        }
        return newExpression;
    }

}
