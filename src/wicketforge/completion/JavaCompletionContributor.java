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
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.Constants;
import wicketforge.WicketForgeUtil;
import wicketforge.psi.hierarchy.ClassItem;
import wicketforge.psi.hierarchy.WicketClassHierarchy;
import wicketforge.psi.hierarchy.WicketMarkupHierarchy;

/**
 */
public class JavaCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(final CompletionParameters p, final CompletionResultSet rs) {
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            public void run() {
                // lets do some basic checks...
                PsiFile f = p.getOriginalFile();
                if (f.getFileType() == StdFileTypes.HTML) {
                    PsiElement psiElement = p.getOriginalPosition();
                    if (psiElement instanceof XmlToken) {
                        XmlAttributeValue wicketIdAttribute = getWicketIdAttribute((XmlToken) psiElement);
                        if (wicketIdAttribute != null) {
                            PsiClass clazz = WicketForgeUtil.getMarkupClass(f);
                            if (clazz != null) {
                                // ... before we search for our parent AttributeItem
                                String parentPath = WicketMarkupHierarchy.findPathOf(wicketIdAttribute, true);
                                if (parentPath != null) {
                                    ClassItem item = WicketClassHierarchy.create(clazz).getWicketIdPathMap().get(parentPath);
                                    if (item != null) {
                                        for (ClassItem child : item.getChilds()) {
                                            rs.addElement(LookupElementBuilder.create(child.getWicketId()).setIcon(child.getIcon()).setTypeText(".java").setTailText("  " + child.getLocationString(), true));
                                        }
                                    }
                                }
                                rs.stopHere();
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * @param position
     * @return XmlAttributeValue if position is wicket:id attribute else null
     */
    @Nullable
    private XmlAttributeValue getWicketIdAttribute(@NotNull XmlToken position) {
        if (!(position.getParent() instanceof XmlAttributeValue)) {
            return null;
        }
        XmlAttributeValue attributeValue = (XmlAttributeValue) position.getParent();
        if (!(attributeValue.getParent() instanceof XmlAttribute)) {
            return null;
        }
        XmlAttribute attribute = (XmlAttribute) attributeValue.getParent();
        return Constants.WICKET_ID.equals(attribute.getName()) ? attributeValue : null;
    }
}
