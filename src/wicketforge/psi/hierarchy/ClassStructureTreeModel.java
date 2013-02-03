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
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNewExpression;
import com.intellij.util.PsiNavigateUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

/**
 */
public class ClassStructureTreeModel extends TextEditorBasedStructureViewModel {
    private ClassWicketIdReferences classWicketIdReferences;
    private StructureViewTreeElement root;

    public ClassStructureTreeModel(@NotNull PsiFile psiFile, @NotNull PsiClass psiClass) {
        super(psiFile);
        classWicketIdReferences = ClassWicketIdReferences.build(psiClass);
        root = new ClassTreeElement(psiClass);
    }

    @NotNull
    public StructureViewTreeElement getRoot() {
        return root;
    }

    private static final ItemPresentation EMPTY_PRESENTATION = new ItemPresentation() {
        @Override
        public String getPresentableText() {
            return "";
        }

        @Override
        public String getLocationString() {
            return null;
        }

        @Override
        public Icon getIcon(boolean open) {
            return null;
        }
    };

    private class ClassTreeElement implements StructureViewTreeElement {
        private TreeElement[] children;
        private final PsiElement psiElement;
        private final ClassWicketIdNewComponentItem newComponentItem;

        private ClassTreeElement(PsiElement psiElement) {
            this.psiElement = psiElement;
            this.newComponentItem = psiElement instanceof PsiNewExpression ? classWicketIdReferences.getNewComponentItem((PsiNewExpression) psiElement) : null;
        }

        public Object getValue() {
            return psiElement;
        }

        public void navigate(boolean requestFocus) {
            PsiNavigateUtil.navigate(newComponentItem != null ? newComponentItem.getWicketIdExpression() : psiElement);
        }

        public boolean canNavigate() {
            return true;
        }

        public boolean canNavigateToSource() {
            return true;
        }

        public ItemPresentation getPresentation() {
            return newComponentItem != null ? newComponentItem : EMPTY_PRESENTATION;
        }

        public TreeElement[] getChildren() {
            if (children == null) {
                List<PsiNewExpression> addedComponents = classWicketIdReferences.getAdded(psiElement);
                if (addedComponents != null) {
                    children = new TreeElement[addedComponents.size()];
                    for (int i = 0, addedComponentsSize = addedComponents.size(); i < addedComponentsSize; i++) {
                        PsiNewExpression addedComponent = addedComponents.get(i);
                        children[i] = new ClassTreeElement(addedComponent);
                    }
                } else {
                    children = new TreeElement[0];
                }
            }
            return children;
        }
    }
}
