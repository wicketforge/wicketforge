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
import wicketforge.util.WicketPsiUtil;

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
        result.wicketIdExpression = WicketPsiUtil.getWicketIdExpressionFromArguments(newExpression);
        if (result.wicketIdExpression == null) {
            return null;
        }
        result.wicketId = WicketPsiUtil.getWicketIdFromExpression(result.wicketIdExpression);
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

    @Nullable
    @Override
    public Icon getIcon(boolean unused) {
        return getIcon(baseClassToCreate);
    }

    private static Icon getIcon(@Nullable PsiClass classToCreate) {
        // simply name check should be enough
        while (classToCreate != null) {
            String name = classToCreate.getName();

            if ("Form".equals(name)) {
                return Constants.ICON_CLASS_FORM;
            }
            if ("AbstractChoice".equals(name)) {
                return Constants.ICON_CLASS_SELECT;
            }
            if ("CheckBox".equals(name)) {
                return Constants.ICON_CLASS_CHECKBOX;
            }
            if ("Radio".equals(name)) {
                return Constants.ICON_CLASS_RADIO;
            }
            if ("Image".equals(name) || "ContextImage".equals(name)) {
                return Constants.ICON_CLASS_RADIO;
            }
            if ("Label".equals(name) || "MultiLineLabel".equals(name) || "FormComponentLabel".equals(name)) {
                return Constants.ICON_CLASS_LABEL;
            }
            if ("AbstractLink".equals(name)) {
                return Constants.ICON_CLASS_LINK;
            }
            if ("Button".equals(name)) {
                return Constants.ICON_CLASS_BUTTON;
            }
            if ("TextArea".equals(name)) {
                return Constants.ICON_CLASS_TEXTAREA;
            }
            if ("AbstractTextComponent".equals(name)) {
                return Constants.ICON_CLASS_TEXTFIELD;
            }
            if ("AbstractRepeater".equals(name)) {
                return Constants.ICON_CLASS_REPEATER;
            }
            if ("Panel".equals(name)) {
                return Constants.ICON_CLASS_PANEL;
            }
            if ("Border".equals(name)) {
                return Constants.ICON_CLASS_BORDER;
            }
            if ("FormComponentPanel".equals(name)) {
                return Constants.ICON_CLASS_FORMCOMPONENTPANEL;
            }
            if ("FormComponent".equals(name)) {
                return Constants.ICON_CLASS_FORMCOMPONENT;
            }
            if ("WebMarkupContainer".equals(name)) {
                return Constants.ICON_CLASS_WEBMARKUPCONTAINER;
            }

            classToCreate = classToCreate.getSuperClass();
        }
        return Constants.ICON_CLASS_;
    }
}
