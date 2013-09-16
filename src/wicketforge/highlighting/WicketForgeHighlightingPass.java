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
package wicketforge.highlighting;

import com.intellij.codeHighlighting.TextEditorHighlightingPass;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.UpdateHighlightersUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.Constants;
import wicketforge.facet.WicketForgeFacet;
import wicketforge.psi.references.ClassWicketIdReference;
import wicketforge.psi.references.MarkupWicketIdReference;
import wicketforge.util.WicketPsiUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 */
class WicketForgeHighlightingPass extends TextEditorHighlightingPass {
    private PsiFile file;

    private volatile Collection<HighlightInfo> highlights = Collections.emptyList();

    WicketForgeHighlightingPass(@NotNull PsiFile file, @NotNull Editor editor) {
        super(file.getProject(), editor.getDocument());
        this.file = file;
    }

    @Override
    public void doCollectInformation(@NotNull ProgressIndicator progress) {
        if (!WicketForgeFacet.hasFacetOrIsFromLibrary(file)) {
            return;
        }
        if (file instanceof XmlFile) {

            // Markup

            final List<HighlightInfo> workList = new SmartList<HighlightInfo>();
            file.accept(new XmlRecursiveElementVisitor() {
                @Override
                public void visitXmlAttribute(XmlAttribute attribute) {
                    super.visitXmlAttribute(attribute);
                    if (Constants.WICKET_ID.equals(attribute.getName())) {
                        XmlAttributeValue attributeValue = attribute.getValueElement();
                        if (attributeValue != null && hasReference(attributeValue, MarkupWicketIdReference.class)) {
                            HighlightInfo highlightInfo = createHighlightInfo(WicketForgeColorSettingsPage.HIGHLIGHT_MARKUPWICKETID, attributeValue.getTextRange());
                            if (highlightInfo != null) {
                                workList.add(highlightInfo);
                            }
                        }
                    }
                }
            });
            highlights = workList;

        } else if (file instanceof PsiJavaFile) {

            // Class

            final List<HighlightInfo> workList = new SmartList<HighlightInfo>();
            for (PsiClass psiClass : ((PsiJavaFile) file).getClasses()) {
                psiClass.accept(new JavaRecursiveElementVisitor() {
                    @Override
                    public void visitNewExpression(PsiNewExpression expression) {
                        super.visitNewExpression(expression);
                        PsiClass psiClass = WicketPsiUtil.getClassFromNewExpression(expression);
                        // if its a component
                        if (psiClass != null && WicketPsiUtil.isWicketComponent(psiClass)) {
                            // highlight wicketId expression (but only if its not a page)
                            if (!WicketPsiUtil.isWicketPage(psiClass)) {
                                PsiExpression wicketIdExpression = WicketPsiUtil.getWicketIdExpressionFromArguments(expression);
                                if (wicketIdExpression != null) {
                                    // only PsiLiteralExpression are resolvable wicketIds
                                    HighlightInfo highlightInfo = createHighlightInfo(
                                            hasReference(wicketIdExpression, ClassWicketIdReference.class) ? WicketForgeColorSettingsPage.HIGHLIGHT_JAVAWICKETID : WicketForgeColorSettingsPage.HIGHLIGHT_JAVAWICKETID_NOTRESOLVABLE,
                                            wicketIdExpression.getTextRange());
                                    if (highlightInfo != null) {
                                        workList.add(highlightInfo);
                                    }
                                }
                            }
                        }
                    }
                });
            }
            highlights = workList;

        }
    }

    private boolean hasReference(@NotNull final PsiElement element, @NotNull final Class<? extends PsiReference> referenceClass) {
        for (PsiReference reference : element.getReferences()) {
            if (reference.getClass().equals(referenceClass)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void doApplyInformationToEditor() {
        assert myDocument != null; // editor.getDocument() is notnull
        UpdateHighlightersUtil.setHighlightersToEditor(myProject, myDocument, 0, myDocument.getTextLength(), highlights, getColorsScheme(), getId());
    }

    @Nullable
    private static HighlightInfo createHighlightInfo(HighlightInfoType type, TextRange textRange) {
        return HighlightInfo.newHighlightInfo(type).range(textRange).create();
    }
}
