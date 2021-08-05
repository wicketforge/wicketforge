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
package wicketforge.codeInsight;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.intellij.psi.xml.XmlTokenType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.Constants;
import wicketforge.facet.WicketForgeFacet;
import wicketforge.search.ClassIndex;
import wicketforge.util.WicketPsiUtil;

import java.util.Collection;
import java.util.List;

/**
 */
public class WicketMarkupLineMarkerProvider implements LineMarkerProvider {
    @Override
    @Nullable
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
        // look for root tag
        if (element instanceof XmlToken && ((XmlToken) element).getTokenType() == XmlTokenType.XML_START_TAG_START &&
                element.getParent() instanceof XmlTag && element.getParent().getParent() instanceof XmlDocument) {
            PsiFile file = element.getContainingFile();
            if (file != null) {
                if (WicketForgeFacet.hasFacetOrIsFromLibrary(file)) {
                    final PsiClass psiClass = ClassIndex.getAssociatedClass(file);
                    if (psiClass != null && WicketPsiUtil.isWicketComponentWithAssociatedMarkup(psiClass)) {
                        return NavigableLineMarkerInfo.create(element, new NavigatablePsiElement[]{psiClass}, Constants.TOJAVAREF);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {
    }
}
