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
package wicketforge.psi.hierarchy;

import com.intellij.psi.PsiElement;
import com.intellij.psi.XmlRecursiveElementVisitor;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;
import wicketforge.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class MarkupWicketIdHierarchy {
    private Map<String, MarkupWicketIdItem> wicketIdPathMap;
    private MarkupWicketIdItem root;

    @NotNull
    public static MarkupWicketIdHierarchy create(@NotNull XmlFile xmlFile) {
        return new MarkupWicketIdHierarchy(xmlFile);
    }

    private MarkupWicketIdHierarchy(@NotNull final XmlFile xmlFile) {
        this.wicketIdPathMap = new HashMap<String, MarkupWicketIdItem>();
        this.root = new MarkupWicketIdItem();
        this.wicketIdPathMap.put("", root);

        xmlFile.accept(new XmlRecursiveElementVisitor() {
            private StringBuilder sb = new StringBuilder();
            private MarkupWicketIdItem current = root;

            @Override
            public void visitElement(PsiElement element) {
                // save
                int i = sb.length();
                MarkupWicketIdItem item = current;
                try {
                    // visit
                    super.visitElement(element);
                } finally {
                    // restore
                    if (sb.length() != i) {
                        sb.setLength(i);
                    }
                    current = item;
                }
            }

            @Override
            public void visitXmlAttribute(XmlAttribute attribute) {
                if (Constants.WICKET_ID.equals(attribute.getName())) {
                    XmlAttributeValue attributeValue = attribute.getValueElement();
                    if (attributeValue != null) {
                        String wicketId = attributeValue.getValue();
                        if (wicketId != null) {
                            MarkupWicketIdItem item = new MarkupWicketIdItem(wicketId, attribute, attributeValue);
                            current.addChild(item);
                            sb.append(Constants.HIERARCHYSEPARATOR).append(wicketId);
                            wicketIdPathMap.put(sb.toString(), item);
                            current = item;
                        }
                    }
                }
                super.visitXmlAttribute(attribute);
            }
        });
    }

    @NotNull
    public Map<String, MarkupWicketIdItem> getWicketIdPathMap() {
        return wicketIdPathMap;
    }

    @NotNull
    public MarkupWicketIdItem getRoot() {
        return root;
    }
}
