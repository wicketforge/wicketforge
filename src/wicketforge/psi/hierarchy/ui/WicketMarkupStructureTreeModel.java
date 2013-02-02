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
import wicketforge.psi.hierarchy.MarkupWicketIdItem;
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
        private MarkupWicketIdItem markupWicketIdItem;
        private TreeElement[] children;

        private MarkupTreeElement(MarkupWicketIdItem markupWicketIdItem) {
            this.markupWicketIdItem = markupWicketIdItem;
        }

        public Object getValue() {
            return markupWicketIdItem;
        }

        public void navigate(boolean requestFocus) {
            PsiNavigateUtil.navigate(markupWicketIdItem.getAttributeValue());
        }

        public boolean canNavigate() {
            return true;
        }

        public boolean canNavigateToSource() {
            return true;
        }

        public ItemPresentation getPresentation() {
            return markupWicketIdItem;
        }

        public TreeElement[] getChildren() {
            if (children == null) {
                List<MarkupWicketIdItem> markupWicketIdItemChildren = markupWicketIdItem.getChildren();
                children = new TreeElement[markupWicketIdItemChildren.size()];
                for (int i = 0, n = markupWicketIdItemChildren.size(); i < n; i++) {
                    children[i] = new MarkupTreeElement(markupWicketIdItemChildren.get(i));
                }
            }
            return children;
        }
    }
}
