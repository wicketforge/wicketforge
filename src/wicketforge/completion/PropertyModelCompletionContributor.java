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

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.psi.*;
import com.intellij.util.Icons;
import wicketforge.WicketForgeUtil;
import wicketforge.visitor.CompletionResult;
import wicketforge.visitor.PropertyModelVisitor;

import java.util.List;

/**
 */
public class PropertyModelCompletionContributor extends AbstractJavaCompletionContributor {

    @Override
    public void fillCompletionVariants(final CompletionParameters p, final CompletionResultSet rs) {
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            public void run() {
                PsiFile f = p.getOriginalFile();
                if (f.getFileType() == StdFileTypes.JAVA) {
                    PsiElement psiElement = p.getPosition();
                    if (psiElement instanceof PsiJavaToken) {
                        PsiJavaToken position = (PsiJavaToken) psiElement;
                        if (isWicketPropertyModel(position)) {
                            PropertyModelVisitor visitor = new PropertyModelVisitor();
                            visitor.visitNewExpression(getElementNewExpression(position));
                            List<CompletionResult> results = visitor.getResults();
                            addReferencesToResult(results, rs);
                            if (results != null && results.size() > 0) {
                                rs.stopHere();
                            }
                        }
                    }
                }
            }
        });
    }

    private void addReferencesToResult(List<CompletionResult> references, CompletionResultSet rs) {
        if (references != null && !references.isEmpty()) {
            for (CompletionResult s : references) {
                LookupElementBuilder lookupElementBuilder =
                        LookupElementBuilder.create(s.getKey())
                                .setIcon(Icons.METHOD_ICON)
                                .setTypeText(".java")
                                .setTailText("  " + s.getDescription(), true);
                rs.addElement(lookupElementBuilder);
            }
        }
    }

    private boolean isWicketPropertyModel(PsiJavaToken position) {
        PsiNewExpression newExpression = getElementNewExpression(position);
        if (newExpression == null) {
            return false;
        }
        
        PsiMethod constructor = newExpression.resolveConstructor();
        if (constructor == null) {
            return false;
        }

        PsiClass psiClass = constructor.getContainingClass();
        if (psiClass == null) {
            return false;
        }

        if (!WicketForgeUtil.isWicketPropertyModel(psiClass)) {
            return false;
        }

        PsiExpressionList constructorArgs = newExpression.getArgumentList();
        return constructorArgs != null;
    }

}