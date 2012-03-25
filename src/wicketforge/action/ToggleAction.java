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
package wicketforge.action;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.PsiNavigateUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.WicketForgeUtil;
import wicketforge.facet.WicketForgeFacet;
import wicketforge.templates.WicketTemplates;

/**
 */
public class ToggleAction extends AnAction {
    @Override
    public void update(AnActionEvent e) {
        final PsiFile psiFile = e.getData(DataKeys.PSI_FILE);
        // let user toggle when we have a wicket facet or we are in a lib (could be wicket lib)
        e.getPresentation().setEnabled(WicketForgeFacet.hasFacetOrIsFromLibrary(psiFile));
    }

    public void actionPerformed(AnActionEvent event) {
        Editor editor = event.getData(DataKeys.EDITOR);
        if (editor == null) {
            return;
        }

        PsiFile psiFile = event.getData(DataKeys.PSI_FILE);
        if (psiFile == null) {
            return;
        }

        if (!WicketForgeFacet.hasFacetOrIsFromLibrary(psiFile)) {
            return;
        }

        if (psiFile instanceof XmlFile) {
            PsiClass psiClass = WicketForgeUtil.getMarkupClass(psiFile);
            if (psiClass == null) {
                HintManager.getInstance().showInformationHint(editor, "No corresponding java class found");
            } else {
                PsiFile classFile =  psiClass.getContainingFile();
                // if file alredy open just navigate to file (and not to class) so caret position gets not changed (issue 92)
                if (classFile != null) {
                    VirtualFile vf = classFile.getVirtualFile();
                    if (vf != null && FileEditorManager.getInstance(psiClass.getProject()).isFileOpen(vf)) {
                        classFile.navigate(true);
                        return;
                    }
                }
                psiClass.navigate(true);
            }
        } else if (psiFile instanceof PsiJavaFile) {
            PsiClass psiClass = null;
            // look for wicket class at caret
            int offset = editor.getCaretModel().getOffset();
            if (offset >= 0) {
                PsiElement currentElement = psiFile.findElementAt(offset);
                if (currentElement != null) {
                    psiClass = WicketForgeUtil.getParentWicketClass(currentElement);
                }
            }
            // if not found -> look for (first) class of file
            if (psiClass == null) {
                PsiClass[] psiClasses = ((PsiJavaFile) psiFile).getClasses();
                if (psiClasses.length > 0) {
                    psiClass = WicketForgeUtil.getParentWicketClass(psiClasses[0]);
                }
            }

            if (psiClass == null) {
                HintManager.getInstance().showErrorHint(editor, "No wicket page/panel found");
                return;
            }

            // get markupFile
            PsiElement markupFile = WicketForgeUtil.getMarkupFile(psiClass);
            if (markupFile == null) {
                // no markup file found -> ask to create one
                final Module module = ModuleUtil.findModuleForPsiElement(psiFile);
                if (module != null && !WicketForgeFacet.isFromLibrary(psiClass)) {
                    markupFile = createMarkup(module, psiFile, psiClass);
                    if (markupFile == null) { // cancel
                        return;
                    }
                }
            }
            if (markupFile != null) {
                PsiNavigateUtil.navigate(markupFile);
            } else {
                HintManager.getInstance().showErrorHint(editor, "No corresponding markup file found");
            }
        }
    }

    @Nullable
    private PsiElement createMarkup(@NotNull Module module, @NotNull PsiFile psiFile, @NotNull PsiClass psiClass) {
        PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(psiFile.getContainingDirectory());
        if (psiPackage == null) {
            return null;
        }

        String templateName = null;
        if (WicketForgeUtil.isWicketPage(psiClass)) {
            templateName = WicketTemplates.WICKET_PAGE_HTML;
        } else if (WicketForgeUtil.isWicketPanel(psiClass)) {
            templateName = WicketTemplates.WICKET_PANEL_HTML;
        }
        if (templateName != null &&
                Messages.showYesNoDialog(module.getProject(),
                        String.format("Create a new markup for '%s'?", psiClass.getQualifiedName()),
                        "Create markup",
                        Messages.getQuestionIcon()) == 0
                ) {
            PsiDirectory directory = WicketForgeUtil.selectTargetDirectory(psiPackage.getQualifiedName(), module.getProject(), module);
            if (directory != null) {
                // create
                return WicketForgeUtil.createFileFromTemplate(WicketForgeUtil.getMarkupFileName(psiClass), directory, templateName);
            }
        }
        return null;
    }
}
