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
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiCallExpression;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.Constants;
import wicketforge.util.WicketPsiUtil;

import javax.swing.*;

public final class ClassWicketIdNewComponentItem implements ItemPresentation {
    private final PsiCallExpression callExpression;
    private final PsiExpression wicketIdExpression;
    private final String wicketId;
    private final PsiClass baseClassToCreate;

    private ClassWicketIdNewComponentItem(@NotNull PsiCallExpression callExpression, @NotNull PsiExpression wicketIdExpression, @NotNull String wicketId, @NotNull PsiClass baseClassToCreate) {
        this.callExpression = callExpression;
        this.wicketIdExpression = wicketIdExpression;
        this.wicketId = wicketId;
        this.baseClassToCreate = baseClassToCreate;
    }

    @Nullable
    static ClassWicketIdNewComponentItem create(@NotNull PsiCallExpression callExpression) {
        PsiExpression wicketIdExpression = WicketPsiUtil.getWicketIdExpressionFromArguments(callExpression);
        if (wicketIdExpression == null) {
            return null;
        }
        String wicketId = WicketPsiUtil.getWicketIdFromExpression(wicketIdExpression);
        if (wicketId == null) {
            return null;
        }
        PsiClass classToBeCreated = WicketPsiUtil.getClassToBeCreated(callExpression);
        if (classToBeCreated instanceof PsiAnonymousClass) {
            classToBeCreated = PsiUtil.resolveClassInType(((PsiAnonymousClass) classToBeCreated).getBaseClassType());
        }
        if (classToBeCreated == null) {
            return null;
        }
        return new ClassWicketIdNewComponentItem(callExpression, wicketIdExpression, wicketId, classToBeCreated);
    }

    @NotNull
    public PsiCallExpression getCallExpression() {
        return callExpression;
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
