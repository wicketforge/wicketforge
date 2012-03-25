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
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.WicketForgeUtil;
import wicketforge.psi.hierarchy.AttributeItem;
import wicketforge.psi.hierarchy.WicketClassHierarchy;
import wicketforge.psi.hierarchy.WicketMarkupHierarchy;

/**
 */
public class HtmlCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(final CompletionParameters p, final CompletionResultSet rs) {
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            public void run() {
                // lets do some basic checks...
                PsiFile f = p.getOriginalFile();
                if (f.getFileType() == StdFileTypes.JAVA) {
                    PsiElement psiElement = p.getOriginalPosition();
                    if (psiElement instanceof PsiJavaToken) {
                        PsiExpression wicketIdExpression = getWicketIdExpression((PsiJavaToken) psiElement);
                        if (wicketIdExpression != null) {
                            PsiNewExpression wicketNewExpression = getWicketNewExpression(wicketIdExpression);
                            if (wicketNewExpression != null) {
                                PsiClass psiClass = WicketForgeUtil.getParentWicketClass(wicketNewExpression);
                                if (psiClass != null) {
                                    PsiFile markup = WicketForgeUtil.getMarkupFile(psiClass);
                                    if (markup != null) {
                                        // ... before we search for our parent AttributeItem
                                        String parentPath = WicketClassHierarchy.findPathOf(psiClass, wicketIdExpression, true, true);
                                        if (parentPath != null) {
                                            AttributeItem item = WicketMarkupHierarchy.create((XmlFile) markup).getWicketIdPathMap().get(parentPath);
                                            if (item != null) {
                                                for (AttributeItem child : item.getChilds()) {
                                                    rs.addElement(LookupElementBuilder.create(child.getWicketId()).setIcon(child.getIcon()).setTypeText(".html").setTailText("  " + child.getLocationString(), true));
                                                }
                                            }
                                        }
                                        rs.stopHere();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * @param position
     * @return          possible candidate for wicketId (psiExpression) of position or null if not matches
     */
    @Nullable
    private PsiExpression getWicketIdExpression(@NotNull PsiJavaToken position) {
        PsiElement element = position.getParent();
        if (!(element instanceof PsiLiteralExpression)) {
            return null;
        }

        return element.getParent() instanceof PsiExpressionList ? (PsiExpression) element : null;
    }

    @Nullable
    private PsiNewExpression getWicketNewExpression(@NotNull PsiExpression wicketIdExpression) {
        PsiExpressionList expressionList = (PsiExpressionList) wicketIdExpression.getParent();
        PsiElement parent = expressionList.getParent();
        if (parent instanceof PsiAnonymousClass) {
            parent = parent.getParent();
        }
        if (!(parent instanceof PsiNewExpression)) {
            return null;
        }

        PsiClass psiClass = WicketForgeUtil.getClassFromNewExpression((PsiNewExpression) parent);

        return psiClass != null && WicketForgeUtil.isWicketComponent(psiClass) ? (PsiNewExpression) parent : null;
    }
}
