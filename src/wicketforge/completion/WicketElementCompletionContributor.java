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
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.html.HtmlTag;
import com.intellij.psi.xml.XmlToken;
import wicketforge.Constants;
import wicketforge.visitor.CompletionResult;

/**
 */
public class WicketElementCompletionContributor extends CompletionContributor {

    private static final CompletionResult[] WICKET_ELEMENTS = {
        new CompletionResult("wicket:panel", "Inserts a <wicket:panel> element."),
        new CompletionResult("wicket:head", "Inserts a <wicket:head> element."),
        new CompletionResult("wicket:message", "Inserts a <wicket:message> element."),
        new CompletionResult("wicket:enclosure", "Inserts a <wicket:enclosure> element."),
        new CompletionResult("wicket:container", "Inserts a <wicket:container> element."),
        new CompletionResult("wicket:extend", "Inserts a <wicket:extend> element.")
    };

    @Override
    public void fillCompletionVariants(final CompletionParameters p, final CompletionResultSet rs) {
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            public void run() {
                PsiFile f = p.getOriginalFile();
                if (f.getFileType() == StdFileTypes.HTML) {
                    PsiElement psiElement = p.getPosition();
                    if (psiElement instanceof XmlToken) {
                        XmlToken position = (XmlToken) psiElement;
                        if (isWicketElement(position)) {
                            addElementResults(rs);
                        }
                    }
                }
            }
        });
    }

    private void addElementResults(CompletionResultSet rs) {
        for (CompletionResult res : WICKET_ELEMENTS) {
            LookupElementBuilder lookupElementBuilder =
                    LookupElementBuilder.create(res.getKey())
                            .setIcon(Constants.HTML_ICON)
                            .setTailText("  " + res.getDescription(), true);
            rs.addElement(lookupElementBuilder);
        }
    }

    private boolean isWicketElement(XmlToken position) {
        PsiElement element = position.getParent();
        return (element instanceof HtmlTag);
    }

}
