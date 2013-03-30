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

import com.intellij.ide.structureView.StructureView;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.util.FileStructurePopup;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.ui.PlaceHolder;
import org.jetbrains.annotations.NotNull;
import wicketforge.psi.hierarchy.ClassStructureTreeModel;
import wicketforge.psi.hierarchy.MarkupStructureTreeModel;

/**
 */
public class ViewWicketStructureAction extends AnAction {
    private static final String PLACE = "WicketViewPopup";

    @Override
    public void actionPerformed(AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        Project project = PlatformDataKeys.PROJECT.getData(dataContext);
        if (project == null) {
            return;
        }
        Editor editor = PlatformDataKeys.EDITOR.getData(dataContext);
        if (editor == null) {
            return;
        }
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null) {
            return;
        }
        int offset = editor.getCaretModel().getOffset();
        if (offset <= 0) {
            return;
        }
        PsiElement currentElement = psiFile.findElementAt(offset);
        if (currentElement == null) {
            return;
        }
        StructureViewModel viewModel;
        if (psiFile instanceof XmlFile) {
            viewModel = new MarkupStructureTreeModel((XmlFile) psiFile);
        } else if (psiFile instanceof PsiJavaFile) {
            viewModel = new ClassStructureTreeModel((PsiJavaFile) psiFile);
        } else {
            return;
        }
        FileEditor fileEditor = PlatformDataKeys.FILE_EDITOR.getData(dataContext);
        if (fileEditor == null) {
            return;
        }
        final StructureViewBuilder structureViewBuilder = fileEditor.getStructureViewBuilder();
        if (structureViewBuilder == null) {
            return;
        }
        StructureView structureView = structureViewBuilder.createStructureView(fileEditor, project);

        FileStructurePopup popup = createPopup(editor, project, viewModel, structureView/*, PlatformDataKeys.NAVIGATABLE.getData(dataContext)*/);
        popup.setTitle(psiFile.getName());
        popup.show();
    }

    @NotNull
    private static FileStructurePopup createPopup(final Editor editor, Project project, StructureViewModel model, StructureView structureView/*, @Nullable Navigatable navigatable*/) {
        if (model instanceof PlaceHolder) {
            ((PlaceHolder) model).setPlace(PLACE);
        }
        return createStructureViewPopup(model, editor, project, structureView/*, navigatable*/);
    }

    @NotNull
    private static FileStructurePopup createStructureViewPopup(final StructureViewModel structureViewModel, final Editor editor, final Project project, final @NotNull Disposable auxDisposable/*, @Nullable final Navigatable navigatable*/) {
        return new FileStructurePopup(structureViewModel, editor, project, auxDisposable, true);
    }


    @Override
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        DataContext dataContext = e.getDataContext();
        Project project = PlatformDataKeys.PROJECT.getData(dataContext);
        if (project == null) {
            presentation.setEnabled(false);
            return;
        }
        Editor editor = PlatformDataKeys.EDITOR.getData(dataContext);
        if (editor == null) {
            presentation.setEnabled(false);
            return;
        }

        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null) {
            presentation.setEnabled(false);
            return;
        }
        final VirtualFile virtualFile = psiFile.getVirtualFile();

        if (virtualFile == null) {
            presentation.setEnabled(false);
            return;
        }

        if (!(psiFile instanceof XmlFile) && !(psiFile instanceof PsiJavaFile)) {
            presentation.setEnabled(false);
            return;
        }

        presentation.setEnabled(true);
    }
}
