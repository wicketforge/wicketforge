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

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.*;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.Constants;
import wicketforge.WicketForgeUtil;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ClassItem implements ItemPresentation {
    private String wicketId;
    private List<NewComponentReference> references;
    private List<ClassItem> childs;

    ClassItem(@NotNull String wicketId) {
        this.wicketId = wicketId;
        this.references = new SmartList<NewComponentReference>();
    }

    @Nullable
    ClassItem findChild(@NotNull String wicketId) {
        if (childs != null) {
            for (ClassItem child : childs) {
                if (wicketId.equals(child.wicketId)) {
                    return child;
                }
            }
        }
        return null;
    }

    void addChild(@NotNull ClassItem child) {
        if (childs == null) {
            childs = new ArrayList<ClassItem>();
        }
        childs.add(child);
    }

    @NotNull
    public String getWicketId() {
        return wicketId;
    }

    @NotNull
    public List<NewComponentReference> getReferences() {
        return references;
    }

    @NotNull
    public List<ClassItem> getChilds() {
        return childs == null ? Collections.<ClassItem>emptyList() : childs;
    }

    /* ItemPresentation */

    public String getPresentableText() {
        return wicketId;
    }

    private String location;
    public String getLocationString() {
        if (location == null) {
            boolean first = true;
            StringBuilder sb = new StringBuilder();
            for (NewComponentReference reference : references) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append("new ").append(reference.getBaseClassToCreate().getName()).append("(...)");
                first = false;
            }
            location = sb.toString();
        }
        return location;
    }

    public Icon getIcon() {
        return getIcon(false);
    }

    public Icon getIcon(boolean open) {
        return Constants.WICKET_COMPONENT_ICON;
    }

    public TextAttributesKey getTextAttributesKey() {
        return null;
    }

    /**
     *
     */
    public static final class NewComponentReference {
        private PsiNewExpression newExpression;
        private PsiExpression wicketIdExpression;
        private String wicketId;
        private PsiClass baseClassToCreate;

        private NewComponentReference() {
        }

        @Nullable
        static NewComponentReference create(@NotNull PsiNewExpression newExpression) {
            NewComponentReference result = new NewComponentReference();

            result.newExpression = newExpression;
            result.wicketIdExpression = WicketForgeUtil.getWicketIdExpressionFromArguments(newExpression);
            if (result.wicketIdExpression == null) {
                return null;
            }
            result.wicketId = WicketForgeUtil.getWicketIdFromExpression(result.wicketIdExpression);
            if (result.wicketId == null) {
                return null;
            }
            PsiJavaCodeReferenceElement referenceElement = newExpression.getClassOrAnonymousClassReference();
            if (referenceElement == null) {
                return null;
            }
            PsiElement resolvedElement = referenceElement.resolve();
            if (!(resolvedElement instanceof PsiClass)) {
                return null;
            }
            result.baseClassToCreate = (PsiClass) resolvedElement;
            return result;
        }

        @NotNull
        public PsiNewExpression getNewExpression() {
            return newExpression;
        }

        @NotNull
        public PsiExpression getWicketIdExpression() {
            return wicketIdExpression;
        }

        @NotNull
        public String getWicketId() {
            return wicketId;
        }

        /**
         * @return  Class to be created (base class on anonymous creation)
         */
        @NotNull
        public PsiClass getBaseClassToCreate() {
            return baseClassToCreate;
        }
    }
}