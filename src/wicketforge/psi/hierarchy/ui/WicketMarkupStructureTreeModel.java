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
package wicketforge.psi.hierarchy.ui;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.PsiNavigateUtil;
import org.jetbrains.annotations.NotNull;
import wicketforge.psi.hierarchy.AttributeItem;
import wicketforge.psi.hierarchy.WicketMarkupHierarchy;

import java.util.List;

/**
 */
public class WicketMarkupStructureTreeModel extends TextEditorBasedStructureViewModel {
    private StructureViewTreeElement root;

    public WicketMarkupStructureTreeModel(@NotNull PsiFile psiFile) {
        super(psiFile);
        WicketMarkupHierarchy hierarchy = WicketMarkupHierarchy.create((XmlFile) psiFile);
        root = new MarkupTreeElement(hierarchy.getRoot());
    }

    @NotNull
    public StructureViewTreeElement getRoot() {
        return root;
    }

    private static class MarkupTreeElement implements StructureViewTreeElement {
        private AttributeItem attributeItem;
        private TreeElement[] children;

        private MarkupTreeElement(AttributeItem attributeItem) {
            this.attributeItem = attributeItem;
        }

        public Object getValue() {
            return attributeItem;
        }

        public void navigate(boolean requestFocus) {
            PsiNavigateUtil.navigate(attributeItem.getAttributeValue());
        }

        public boolean canNavigate() {
            return true;
        }

        public boolean canNavigateToSource() {
            return true;
        }

        public ItemPresentation getPresentation() {
            return attributeItem;
        }

        public TreeElement[] getChildren() {
            if (children == null) {
                children = new TreeElement[attributeItem.getChildren().size()];
                List<AttributeItem> attributeItemChildren = attributeItem.getChildren();
                for (int i = 0, n = attributeItemChildren.size(); i < n; i++) {
                    children[i] = new MarkupTreeElement(attributeItemChildren.get(i));
                }
            }
            return children;
        }
    }
}
