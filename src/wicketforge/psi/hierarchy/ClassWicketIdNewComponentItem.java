/*
 * Copyright 2013 The WicketForge-Team
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
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.Constants;
import wicketforge.WicketForgeUtil;

import javax.swing.*;

public final class ClassWicketIdNewComponentItem implements ItemPresentation {
    private PsiNewExpression newExpression;
    private PsiExpression wicketIdExpression;
    private String wicketId;
    private PsiClass baseClassToCreate;

    private ClassWicketIdNewComponentItem() {
    }

    @Nullable
    static ClassWicketIdNewComponentItem create(@NotNull PsiNewExpression newExpression) {
        ClassWicketIdNewComponentItem result = new ClassWicketIdNewComponentItem();

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

    @Override
    public String getPresentableText() {
        return wicketId;
    }

    @Override
    public String getLocationString() {
        return /*"new " + */getBaseClassToCreate().getName()/* + "(...)"*/;
    }

    @Override
    public Icon getIcon(boolean open) {
        return Constants.WICKET_COMPONENT_ICON;
    }
}
