/*
 * Copyright 2014 The WicketForge-Team
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
package wicketforge.intention;

import static com.intellij.codeInsight.AnnotationUtil.CHECK_EXTERNAL;

import org.jetbrains.annotations.NotNull;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInsight.intention.AddAnnotationFix;
import com.intellij.codeInsight.intention.AddAnnotationPsiFix;
import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;

import wicketforge.Constants;
import wicketforge.util.WicketPsiUtil;

/**
 * AddComponentFactoryIntention
 */
public class AddComponentFactoryIntention extends BaseIntentionAction {

    private static final String TOADD = Constants.WICKETFORGE_COMPONENT_FACTORY;
    private static final String[] TOREMOVE = ArrayUtil.EMPTY_STRING_ARRAY;

    @NotNull
    @Override
    public String getFamilyName() {
        return "Annotate as @ComponentFactory";
    }

    // include not in project files
    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        int position = editor.getCaretModel().getOffset();
        PsiElement element = file.findElementAt(position);

        if (element == null || !element.isValid()) {
            return false;
        }
        if (!PsiUtil.isLanguageLevel5OrHigher(element)) {
            return false;
        }
        final PsiModifierListOwner owner;
//        if (!element.getManager().isInProject(element) || CodeStyleSettingsManager.getSettings(project).USE_EXTERNAL_ANNOTATIONS) {
            owner = AddAnnotationPsiFix.getContainer(file, position);
//        } else {
//            return false;
//        }
        if (owner == null) {
            return false;
        }
//        if (TOREMOVE.length > 0 && AnnotationUtil.isAnnotated(owner, TOREMOVE[0], false, false)) {
//            return false;
//        }
        setText(AddAnnotationPsiFix.calcText(owner, TOADD));
        if (AnnotationUtil.isAnnotated(owner, TOADD, (CHECK_EXTERNAL | AnnotationUtil.CHECK_INFERRED))) {
            return false;
        }

        // only methods
        if (!(owner instanceof PsiMethod)) {
            return false;
        }
        PsiMethod method = (PsiMethod) owner;
        // get return type (exclude cosntructors)
        PsiType returnType = method.getReturnType();
        if (returnType == null || method.isConstructor()) {
            return false;
        }
        // only wicket component result
        PsiClass returnClass = PsiUtil.resolveClassInClassTypeOnly(returnType);
        if (returnClass == null || !WicketPsiUtil.isWicketComponent(returnClass)) {
            return false;
        }
        // only if first parameter is a String
        PsiParameter[] parameters = method.getParameterList().getParameters();
        return parameters.length > 0 && parameters[0].getType().equalsToText(CommonClassNames.JAVA_LANG_STRING);
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        int position = editor.getCaretModel().getOffset();

        PsiModifierListOwner owner = AddAnnotationPsiFix.getContainer(file, position);
        if (owner == null || !owner.isValid()) {
            return;
        }
        AddAnnotationFix fix = new AddAnnotationFix(TOADD, owner, TOREMOVE);
        fix.invoke(project, editor, file);
    }
}
