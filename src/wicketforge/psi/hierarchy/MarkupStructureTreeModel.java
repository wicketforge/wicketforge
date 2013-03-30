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

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.PsiNavigateUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 */
public class MarkupStructureTreeModel extends TextEditorBasedStructureViewModel {
    private StructureViewTreeElement root;
    private MarkupWicketIdHierarchy hierarchy;

    public MarkupStructureTreeModel(@NotNull XmlFile xmlFile) {
        super(xmlFile);
        hierarchy = MarkupWicketIdHierarchy.create(xmlFile);
        root = new MarkupTreeElement(hierarchy.getRoot());
    }

    @Override
    @NotNull
    public StructureViewTreeElement getRoot() {
        return root;
    }

    @Override
    protected boolean isSuitable(PsiElement element) {
        if (element instanceof XmlTag) {
            for (MarkupWicketIdItem markupWicketIdItem : hierarchy.getWicketIdPathMap().values()) {
                if (element.equals(markupWicketIdItem.getTag())) {
                    return true;
                }
            }
        }
        return super.isSuitable(element);
    }

    private static class MarkupTreeElement implements StructureViewTreeElement {
        private MarkupWicketIdItem markupWicketIdItem;
        private TreeElement[] children;

        private MarkupTreeElement(MarkupWicketIdItem markupWicketIdItem) {
            this.markupWicketIdItem = markupWicketIdItem;
        }

        @Override
        public Object getValue() {
            return markupWicketIdItem.getTag();
        }

        @Override
        public void navigate(boolean requestFocus) {
            PsiNavigateUtil.navigate(markupWicketIdItem.getAttributeValue());
        }

        @Override
        public boolean canNavigate() {
            return true;
        }

        @Override
        public boolean canNavigateToSource() {
            return true;
        }

        @Override
        public ItemPresentation getPresentation() {
            return markupWicketIdItem;
        }

        @Override
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
