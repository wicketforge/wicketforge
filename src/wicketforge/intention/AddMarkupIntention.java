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
package wicketforge.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.WicketForgeUtil;
import wicketforge.facet.WicketForgeFacet;

/**
 * AddMarkupIntention
 */
abstract class AddMarkupIntention implements IntentionAction {

    public boolean startInWriteAction() {
        return true;
    }

    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        if (!(file instanceof PsiJavaFile)) {
            return false;
        }

        int offset = editor.getCaretModel().getOffset();

        PsiElement element = file.findElementAt(offset);
        if (element == null) {
            return false;
        }

        element = element.getParent();
        if (element == null || !(element instanceof PsiClass)) {
            return false;
        }

        if (WicketForgeFacet.isFromLibrary(element)) {
            return false;
        }

        PsiClass psiClass = (PsiClass) element;

        return  psiClass.getName() != null && // add..intention needs a name for resource (ex anonymous classes dont have) (issue 54)
                WicketForgeFacet.isLibraryPresent(ModuleUtil.findModuleForPsiElement(element)) && // let user create page/panel when we have a wicket-lib (so we can detect new facet)
                isApplicableForClass(psiClass) && 
                getResourceFile(psiClass) == null;
    }

    public void invoke(@NotNull final Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = file.findElementAt(offset);
        if (element != null) {
            element = element.getParent();
            if (!(element instanceof PsiClass)) {
                return;
            }
            PsiDirectory fileDirectory = file.getContainingDirectory();
            if (fileDirectory == null) {
                return;
            }
            Module module = ModuleUtil.findModuleForPsiElement(fileDirectory);
            if (module == null) {
                return;
            }
            PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(fileDirectory);
            if (psiPackage == null) {
                return;
            }

            PsiDirectory directory = WicketForgeUtil.selectTargetDirectory(psiPackage.getQualifiedName(), project, module);
            if (directory != null) {
                WicketForgeUtil.createFileFromTemplate(getResourceFileName((PsiClass) element), directory, getTemplateName());
            }
        }
    }

    @Nullable
    protected abstract PsiFile getResourceFile(@NotNull PsiClass psiClass);

    @NotNull
    protected abstract String getResourceFileName(@NotNull PsiClass psiClass);

    @NotNull
    protected abstract String getTemplateName();

    protected abstract boolean isApplicableForClass(@NotNull PsiClass psiClass);
}
