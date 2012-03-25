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

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.html.HtmlTag;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import wicketforge.Constants;
import wicketforge.model.WicketTagRegistry;
import wicketforge.model.tags.WicketTag;
import wicketforge.model.tags.WicketTagAttribute;

/**
 */
public class WicketElementAttributeCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(final CompletionParameters p, final CompletionResultSet rs) {
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            public void run() {
                PsiFile f = p.getOriginalFile();
                if (f.getFileType() == StdFileTypes.HTML) {
                    PsiElement psiElement = p.getPosition();
                    if (psiElement instanceof XmlToken) {
                        XmlToken position = (XmlToken) psiElement;
                        if (isWicketElementAttribute(position)) {
                            addAttributeResults(position, rs);
                        }
                    }
                }
            }
        });
    }

    private void addAttributeResults(XmlToken position, CompletionResultSet rs) {
        WicketTag wicketTag = WicketTagRegistry.getTag(getParentElementName(position));
        if (wicketTag == null || wicketTag.getAttributes() == null) {
            return;
        }

        for (WicketTagAttribute attr : wicketTag.getAttributes()) {
            LookupElementBuilder lookupElementBuilder =
                    LookupElementBuilder.create(attr.getName() + "=\"\"")
                            .setPresentableText(attr.getName())
                            .setIcon(Constants.HTML_ICON)
                            .setInsertHandler(new AttributeInsertHandler());
            rs.addElement(lookupElementBuilder);
        }
    }

    private boolean isWicketElementAttribute(XmlToken position) {
        PsiElement element = position.getParent();
        if (!(element instanceof XmlAttribute)) {
            return false;
        }

        XmlAttribute attribute = (XmlAttribute) element;
        XmlTag tag = attribute.getParent();
        return tag.getName().startsWith("wicket");
    }

    private String getParentElementName(XmlToken position) {
        PsiElement element = position.getParent();
        XmlAttribute attribute = (XmlAttribute) element;
        HtmlTag tag = (HtmlTag) attribute.getParent();
        return tag.getName();
    }

    private class AttributeInsertHandler implements InsertHandler<LookupElement> {

        public void handleInsert(InsertionContext insertionContext, LookupElement lookupElement) {
            CaretModel caretModel = insertionContext.getEditor().getCaretModel();
            caretModel.moveToOffset(caretModel.getOffset()-1);
        }
    }

}
