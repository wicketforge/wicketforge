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
import wicketforge.psi.hierarchy.ClassItem;
import wicketforge.psi.hierarchy.WicketClassHierarchy;

import java.util.List;

/**
 */
public class WicketClassStructureTreeModel extends TextEditorBasedStructureViewModel {
    private StructureViewTreeElement root;

    public WicketClassStructureTreeModel(@NotNull PsiFile psiFile, @NotNull PsiClass psiClass) {
        super(psiFile);
        WicketClassHierarchy hierarchy = WicketClassHierarchy.create(psiClass);
        root = new ClassTreeElement(hierarchy.getRoot());
    }

    @NotNull
    public StructureViewTreeElement getRoot() {
        return root;
    }

    private class ClassTreeElement implements StructureViewTreeElement {
        private ClassItem classItem;
        private TreeElement[] children;

        private ClassTreeElement(ClassItem classItem) {
            this.classItem = classItem;
        }

        public Object getValue() {
            return classItem;
        }

        public void navigate(boolean requestFocus) {
            // todo mm -> navigate (multiple choices on getReferences)
//           PsiNavigateUtil.navigate(classItem.getReferences()...);
            PsiNavigateUtil.navigate(classItem.getReferences().get(0).getWicketIdExpression());
        }

        public boolean canNavigate() {
            return true;
        }

        public boolean canNavigateToSource() {
            return true;
        }

        public ItemPresentation getPresentation() {
            return classItem;
        }

        public TreeElement[] getChildren() {
            if (children == null) {
                children = new TreeElement[classItem.getChildren().size()];
                List<ClassItem> classItemChildren = classItem.getChildren();
                for (int i = 0, n = classItemChildren.size(); i < n; i++) {
                    children[i] = new ClassTreeElement(classItemChildren.get(i));
                }
            }
            return children;
        }
    }
}
