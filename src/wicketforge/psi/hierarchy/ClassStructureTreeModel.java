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
import com.intellij.psi.PsiCallExpression;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.util.PsiNavigateUtil;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import wicketforge.util.WicketPsiUtil;

import java.util.List;

/**
 */
public class ClassStructureTreeModel extends TextEditorBasedStructureViewModel {
    private StructureViewTreeElement root;
    private static final TreeElement[] EMPTY_TREE_ELEMENTS = new TreeElement[0];

    public ClassStructureTreeModel(@NotNull PsiJavaFile psiJavaFile) {
        super(psiJavaFile);
        root = new JavaFileTreeElement(psiJavaFile);
    }

    @Override
    @NotNull
    public StructureViewTreeElement getRoot() {
        return root;
    }

    @Override
    protected boolean isSuitable(PsiElement element) {
        if (element instanceof PsiCallExpression) {
            for (TreeElement treeElement : getRoot().getChildren()) {
                if (treeElement instanceof ClassTreeElement) {
                    return ((ClassTreeElement) treeElement).classWicketIdReferences.getNewComponentItem((PsiCallExpression) element) != null;
                }
            }
        } else if (element instanceof PsiClass) {
            return WicketPsiUtil.isWicketComponentWithAssociatedMarkup((PsiClass) element);
        }
        return super.isSuitable(element);
    }

    private static class JavaFileTreeElement implements StructureViewTreeElement {
        private PsiJavaFile psiJavaFile;

        private JavaFileTreeElement(@NotNull PsiJavaFile psiJavaFile) {
            this.psiJavaFile = psiJavaFile;
        }

        @Override
        public Object getValue() {
            return psiJavaFile;
        }

        @Override
        public void navigate(boolean requestFocus) {
            psiJavaFile.navigate(requestFocus);
        }

        @Override
        public boolean canNavigate() {
            return psiJavaFile.canNavigate();
        }

        @Override
        public boolean canNavigateToSource() {
            return psiJavaFile.canNavigateToSource();
        }

        @NotNull
        @Override
        public ItemPresentation getPresentation() {
            return psiJavaFile.getPresentation();
        }

        private TreeElement[] children;

        @NotNull
        @Override
        public TreeElement[] getChildren() {
            if (children == null) {
                List<TreeElement> list = new SmartList<TreeElement>();
                for (PsiClass psiClass : psiJavaFile.getClasses()) {
                    if (WicketPsiUtil.isWicketComponentWithAssociatedMarkup(psiClass)) {
                        list.add(new ClassTreeElement(psiClass, ClassWicketIdReferences.build(psiClass, false)));
                    }
                }
                children = list.isEmpty() ? EMPTY_TREE_ELEMENTS : list.toArray(new TreeElement[list.size()]);
            }
            return children;
        }
    }

    private static class ClassTreeElement implements StructureViewTreeElement {
        private TreeElement[] children;
        final ClassWicketIdReferences classWicketIdReferences;
        final PsiClass psiClass;

        private ClassTreeElement(@NotNull PsiClass psiClass, @NotNull ClassWicketIdReferences classWicketIdReferences) {
            this.psiClass = psiClass;
            this.classWicketIdReferences = classWicketIdReferences;
        }

        @Override
        public Object getValue() {
            return psiClass;
        }

        @Override
        public void navigate(boolean requestFocus) {
            psiClass.navigate(requestFocus);
        }

        @Override
        public boolean canNavigate() {
            return psiClass.canNavigate();
        }

        @Override
        public boolean canNavigateToSource() {
            return psiClass.canNavigateToSource();
        }

        @NotNull
        @Override
        public ItemPresentation getPresentation() {
            return psiClass.getPresentation();
        }

        @NotNull
        @Override
        public TreeElement[] getChildren() {
            if (children == null) {
                List<TreeElement> list = new SmartList<TreeElement>();
                List<PsiCallExpression> addedComponents = classWicketIdReferences.getAdded(psiClass);
                if (addedComponents != null) {
                    for (PsiCallExpression addedComponent : addedComponents) {
                        list.add(new WicketIdTreeElement(addedComponent));
                    }
                }
                for (PsiClass aClass : psiClass.getAllInnerClasses()) {
                    if (classWicketIdReferences.containsClass(aClass) && WicketPsiUtil.isWicketComponentWithAssociatedMarkup(aClass)) {
                        list.add(new ClassTreeElement(aClass, classWicketIdReferences));
                    }
                }
                children = list.isEmpty() ? EMPTY_TREE_ELEMENTS : list.toArray(new TreeElement[list.size()]);
            }
            return children;
        }

        private class WicketIdTreeElement implements StructureViewTreeElement {
            private TreeElement[] children;
            private final PsiCallExpression psiElement;
            private final ClassWicketIdNewComponentItem newComponentItem;

            private WicketIdTreeElement(PsiCallExpression psiElement) {
                this.psiElement = psiElement;
                this.newComponentItem = classWicketIdReferences.getNewComponentItem(psiElement);
            }

            @Override
            public Object getValue() {
                return psiElement;
            }

            @Override
            public void navigate(boolean requestFocus) {
                PsiNavigateUtil.navigate(newComponentItem != null ? newComponentItem.getWicketIdExpression() : psiElement);
            }

            @Override
            public boolean canNavigate() {
                return true;
            }

            @Override
            public boolean canNavigateToSource() {
                return true;
            }

            @NotNull
            @Override
            public ItemPresentation getPresentation() {
                return newComponentItem;
            }

            @NotNull
            @Override
            public TreeElement[] getChildren() {
                if (children == null) {
                    List<PsiCallExpression> addedComponents = classWicketIdReferences.getAdded(psiElement);
                    if (addedComponents != null) {
                        children = new TreeElement[addedComponents.size()];
                        for (int i = 0, addedComponentsSize = addedComponents.size(); i < addedComponentsSize; i++) {
                            PsiCallExpression addedComponent = addedComponents.get(i);
                            children[i] = new WicketIdTreeElement(addedComponent);
                        }
                    } else {
                        children = EMPTY_TREE_ELEMENTS;
                    }
                }
                return children;
            }
        }
    }
}
