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

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiCallExpression;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class ClassWicketIdHierarchy {
    private static final Logger LOG = Logger.getInstance("#wicketforge.psi.hierarchy.ClassWicketIdHierarchy");

    private Map<String, ClassWicketIdItem> wicketIdPathMap;

    @NotNull
    public static ClassWicketIdHierarchy create(@NotNull PsiClass psiClass) {
        return new ClassWicketIdHierarchy(psiClass);
    }

    private ClassWicketIdHierarchy(@NotNull final PsiClass psiClass) {
        this.wicketIdPathMap = new HashMap<String, ClassWicketIdItem>();

        ClassWicketIdItem root = new ClassWicketIdItem("", null);
        this.wicketIdPathMap.put("", root);

        ClassWicketIdReferences classWicketIdReferences = ClassWicketIdReferences.build(psiClass);

        List<PsiCallExpression> addedComponents = classWicketIdReferences.getAdded(psiClass);
        if (addedComponents != null) {
            addRecursive(classWicketIdReferences, new StringBuilder(), root, psiClass, addedComponents, 0);
        }
    }

    private void addRecursive(@NotNull ClassWicketIdReferences classWicketIdReferences,
                              @NotNull StringBuilder path,
                              @NotNull ClassWicketIdItem parent,
                              @NotNull PsiElement parentElement,
                              @Nullable List<PsiCallExpression> addedComponents,
                              int depth) {
        if (depth++ > 50) {
            LOG.error("Deep addRecursive", path.toString());
            return;
        }
        
        if (addedComponents != null) {
            for (PsiCallExpression callExpression : addedComponents) {
                // fix issue 95: add the component to itself (no sense) but crash wicketforge
                if (callExpression.equals(parentElement)) {
                    continue;
                }
                ClassWicketIdNewComponentItem newComponentItem = classWicketIdReferences.getNewComponentItem(callExpression);
                if (newComponentItem != null && !parent.contains(newComponentItem)) {
                    int length = path.length();
                    try {
                        path.append(Constants.HIERARCHYSEPARATOR).append(newComponentItem.getWicketId());

                        ClassWicketIdItem child = findOrCreateChild(path, parent, newComponentItem.getWicketId());

                        child.getNewComponentItems().add(newComponentItem);

                        addRecursive(classWicketIdReferences, path, child, callExpression, classWicketIdReferences.getAdded(callExpression), depth);
                    } finally {
                        path.setLength(length);
                    }
                }
            }
        }
        // add also components of superclass (if any... ex: MyPage.java -> inner class MyMarkupContainer -> label myMarkupContainer )
        PsiClass superClass = null;
        if (parentElement instanceof PsiClass) {
            superClass = ((PsiClass) parentElement).getSuperClass();
        } else if (parentElement instanceof PsiCallExpression) {
            PsiCallExpression callExpression = (PsiCallExpression) parentElement;
            ClassWicketIdNewComponentItem newComponentItem = classWicketIdReferences.getNewComponentItem(callExpression);
            if (newComponentItem != null) {
                superClass = newComponentItem.getBaseClassToCreate();
            }
        }
        if (superClass != null) {
            List<PsiCallExpression> superAddedComponents = classWicketIdReferences.getAdded(superClass);
            if (superAddedComponents != null) {
                addRecursive(classWicketIdReferences, path, parent, superClass, superAddedComponents, depth);
            }
        }
    }

    @NotNull
    private ClassWicketIdItem findOrCreateChild(@NotNull StringBuilder path, @NotNull ClassWicketIdItem parent, @NotNull String wicketId) {
        ClassWicketIdItem child = parent.findChild(wicketId);
        if (child == null) {
            child = new ClassWicketIdItem(wicketId, parent);
            wicketIdPathMap.put(path.toString(), child);
        }
        return child;
    }

    @NotNull
    public Map<String, ClassWicketIdItem> getWicketIdPathMap() {
        return wicketIdPathMap;
    }
}
