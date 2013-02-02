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
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.util.PsiNavigateUtil;
import org.jetbrains.annotations.NotNull;
import wicketforge.psi.hierarchy.ClassWicketIdHierarchy;
import wicketforge.psi.hierarchy.ClassWicketIdItem;

import java.util.List;

/**
 */
public class WicketClassStructureTreeModel extends TextEditorBasedStructureViewModel {
    private StructureViewTreeElement root;

    public WicketClassStructureTreeModel(@NotNull PsiFile psiFile, @NotNull PsiClass psiClass) {
        super(psiFile);
        ClassWicketIdHierarchy hierarchy = ClassWicketIdHierarchy.create(psiClass);
        root = new ClassTreeElement(hierarchy.getRoot());
    }

    @NotNull
    public StructureViewTreeElement getRoot() {
        return root;
    }

    private class ClassTreeElement implements StructureViewTreeElement {
        private ClassWicketIdItem classWicketIdItem;
        private TreeElement[] children;

        private ClassTreeElement(ClassWicketIdItem classWicketIdItem) {
            this.classWicketIdItem = classWicketIdItem;
        }

        public Object getValue() {
            return classWicketIdItem;
        }

        public void navigate(boolean requestFocus) {
            // todo mm -> navigate (multiple choices on getReferences)
//           PsiNavigateUtil.navigate(classWicketIdItem.getReferences()...);
            PsiNavigateUtil.navigate(classWicketIdItem.getNewComponentItems().get(0).getWicketIdExpression());
        }

        public boolean canNavigate() {
            return true;
        }

        public boolean canNavigateToSource() {
            return true;
        }

        public ItemPresentation getPresentation() {
            return classWicketIdItem;
        }

        public TreeElement[] getChildren() {
            if (children == null) {
                List<ClassWicketIdItem> classWicketIdItemChildren = classWicketIdItem.getChildren();
                children = new TreeElement[classWicketIdItemChildren.size()];
                for (int i = 0, n = classWicketIdItemChildren.size(); i < n; i++) {
                    children[i] = new ClassTreeElement(classWicketIdItemChildren.get(i));
                }
            }
            return children;
        }
    }
}
