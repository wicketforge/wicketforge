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
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl;
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
import wicketforge.Constants;
import wicketforge.WicketForgeUtil;
import wicketforge.facet.WicketForgeFacet;
import wicketforge.psi.references.ClassWicketIdReference;
import wicketforge.psi.references.MarkupWicketIdReference;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 */
public class WicketForgeHighlightingPass extends TextEditorHighlightingPass {
    private PsiFile file;
    private int startOffset;
    private int endOffset;

    private volatile Collection<HighlightInfo> highlights = Collections.emptyList();

    public WicketForgeHighlightingPass(@NotNull PsiFile file, @NotNull Editor editor, int startOffset, int endOffset) {
        super(file.getProject(), editor.getDocument());
        this.file = file;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    @Override
    public void doCollectInformation(ProgressIndicator progress) {
        if (!WicketForgeFacet.hasFacetOrIsFromLibrary(file)) {
            return;
        }
        if (file instanceof XmlFile) {
            final List<HighlightInfo> highlights = new SmartList<HighlightInfo>();
            file.accept(new XmlRecursiveElementVisitor() {
                @Override
                public void visitXmlAttribute(XmlAttribute attribute) {
                    super.visitXmlAttribute(attribute);
                    if (Constants.WICKET_ID.equals(attribute.getName())) {
                        XmlAttributeValue attributeValue = attribute.getValueElement();
                        if (attributeValue != null && hasReference(attributeValue, MarkupWicketIdReference.class)) {
                            highlights.add(new WicketIdHighlightInfo(WicketForgeColorSettingsPage.HIGHLIGHT_MARKUPWICKETID, attributeValue.getTextRange()));
                        }
                    }
                }
            });
            this.highlights = highlights;
        } else if (file instanceof PsiJavaFile) {
            final List<HighlightInfo> highlights = new SmartList<HighlightInfo>();
            for (PsiClass psiClass : ((PsiJavaFile) file).getClasses()) {
                psiClass.accept(new JavaRecursiveElementVisitor() {
                    @Override
                    public void visitNewExpression(PsiNewExpression expression) {
                        super.visitNewExpression(expression);
                        PsiClass psiClass = WicketForgeUtil.getClassFromNewExpression(expression);
                        // if its a component
                        if (psiClass != null && WicketForgeUtil.isWicketComponent(psiClass)) {
                            // highlight wicketId expression (but only if its not a page)
                            if (!WicketForgeUtil.isWicketPage(psiClass)) {
                                PsiExpression wicketIdExpression = WicketForgeUtil.getWicketIdExpressionFromArguments(expression);
                                if (wicketIdExpression != null) {
                                    // only PsiLiteralExpression are resolvable wicketIds
                                    highlights.add(new WicketIdHighlightInfo(
                                            hasReference(wicketIdExpression, ClassWicketIdReference.class) ?
                                                    WicketForgeColorSettingsPage.HIGHLIGHT_JAVAWICKETID :
                                                    WicketForgeColorSettingsPage.HIGHLIGHT_JAVAWICKETID_NOTRESOLVABLE,
                                            wicketIdExpression.getTextRange())
                                    );
                                }
                            }
                            /*
                            // highlight new component
                            PsiJavaCodeReferenceElement clazzReference = expression.getClassOrAnonymousClassReference();
                            if (clazzReference != null) {
                                highlights.add(new WicketIdHighlightInfo(WicketForgeColorSettingsPage.HIGHLIGHT_JAVANEWWICKETCOMPONENT, clazzReference.getTextRange()));
                            }
                            */
                        }
                    }
                });
            }
            this.highlights = highlights;
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
        UpdateHighlightersUtil.setHighlightersToEditor(myProject, myDocument, startOffset, endOffset, highlights, getId());
        DaemonCodeAnalyzer daemonCodeAnalyzer = DaemonCodeAnalyzer.getInstance(myProject);
        ((DaemonCodeAnalyzerImpl) daemonCodeAnalyzer).getFileStatusMap().markFileUpToDate(myDocument, file, getId());
    }

    private static class WicketIdHighlightInfo extends HighlightInfo {
        private WicketIdHighlightInfo(HighlightInfoType type, TextRange textRange) {
            super(type, textRange.getStartOffset(), textRange.getEndOffset(), null, null);
        }
    }
}
