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

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.Constants;
import wicketforge.util.WicketPsiUtil;

import java.util.Map;

public final class HierarchyUtil {
    private HierarchyUtil() {
    }

    @Nullable
    public static String findPathOf(@NotNull PsiClass psiClass, @NotNull PsiExpression wicketIdExpression, boolean parent, boolean incomplete) {
        ClassWicketIdHierarchy hierarchy = ClassWicketIdHierarchy.create(psiClass);
        for (Map.Entry<String, ClassWicketIdItem> entry : hierarchy.getWicketIdPathMap().entrySet()) {
            for (ClassWicketIdNewComponentItem newComponentItem : entry.getValue().getNewComponentItems()) {
                if (wicketIdExpression.equals(newComponentItem.getWicketIdExpression())) {
                    String path = entry.getKey();
                    return parent ? path.substring(0, path.lastIndexOf(Constants.HIERARCHYSEPARATOR)) : path;
                }
            }
        }
        if (incomplete) {
            // ok, wicket expression is not added yet so we dont know hierarchy of our component
            // lets try to find textual position in hierarchy, not best method but we have no other option
            // If component gets added later, correct hierarchy gets checked...
            final TextRange wicketIdTextRange = wicketIdExpression.getTextRange();
            String bestPath = "";
            TextRange bestTextRange = psiClass.getTextRange();
            // go thru all new references
            for (Map.Entry<String, ClassWicketIdItem> entry : hierarchy.getWicketIdPathMap().entrySet()) {
                for (ClassWicketIdNewComponentItem newComponentItem : entry.getValue().getNewComponentItems()) {
                    TextRange textRange = newComponentItem.getNewExpression().getTextRange();
                    // if wicketId is in new-references-textRange and this is inner of current best...
                    if (textRange.contains(wicketIdTextRange) && bestTextRange.contains(textRange)) {
                        // then we have a better candidate
                        bestTextRange = textRange;
                        bestPath = entry.getKey();
                    }
                }
            }
            return parent ? bestPath : bestPath + Constants.HIERARCHYSEPARATOR + WicketPsiUtil.getWicketIdFromExpression(wicketIdExpression);
        }
        return null;
    }

    @Nullable
    public static String findPathOf(@NotNull XmlAttributeValue attributeValue, boolean parent) {
        PsiFile psiFile = attributeValue.getContainingFile();
        if (psiFile instanceof XmlFile) {
            MarkupWicketIdHierarchy hierarchy = MarkupWicketIdHierarchy.create((XmlFile) psiFile);
            for (Map.Entry<String, MarkupWicketIdItem> entry : hierarchy.getWicketIdPathMap().entrySet()) {
                if (attributeValue.equals(entry.getValue().getAttributeValue())) {
                    String path = entry.getKey();
                    return parent ? path.substring(0, path.lastIndexOf(Constants.HIERARCHYSEPARATOR)) : path;
                }
            }
        }
        return null;
    }
}
