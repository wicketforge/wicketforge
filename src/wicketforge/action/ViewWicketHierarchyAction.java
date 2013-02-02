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
import com.intellij.pom.Navigatable;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlFile;
import com.intellij.ui.PlaceHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.WicketForgeUtil;
import wicketforge.psi.hierarchy.ui.WicketClassStructureTreeModel;
import wicketforge.psi.hierarchy.ui.WicketMarkupStructureTreeModel;

/**
 */
public class ViewWicketHierarchyAction extends AnAction {
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
        StructureViewModel viewModel = null;
        if (psiFile instanceof XmlFile) {
            viewModel = new WicketMarkupStructureTreeModel(psiFile);
        } else if (psiFile instanceof PsiJavaFile) {
            PsiClass psiClass = WicketForgeUtil.getParentWicketClass(currentElement);
            if (psiClass != null) {
                viewModel = new WicketClassStructureTreeModel(psiFile, psiClass);
            }
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

        FileStructurePopup popup = createPopup(editor, project, PlatformDataKeys.NAVIGATABLE.getData(dataContext), viewModel, structureView);
        if (popup != null) {
          final VirtualFile virtualFile = psiFile.getVirtualFile();
          if (virtualFile != null) {
            popup.setTitle(virtualFile.getName());
          }
          popup.show();
        }
    }

    private static final String PLACE = "WicketViewPopup";

    @Nullable
    public static FileStructurePopup createPopup(final Editor editor, Project project, @Nullable Navigatable navigatable, StructureViewModel model,  StructureView structureView) {
      if (model instanceof PlaceHolder) {
        //noinspection unchecked
        ((PlaceHolder)model).setPlace(PLACE);
      }

      return createStructureViewPopup(model, editor, project, navigatable, structureView);
    }

    public static FileStructurePopup createStructureViewPopup(final StructureViewModel structureViewModel,
                                                                     final Editor editor,
                                                                     final Project project,
                                                                     final Navigatable navigatable,
                                                                     final @NotNull Disposable alternativeDisposable) {
      return new FileStructurePopup(structureViewModel, editor, project, alternativeDisposable, true);
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
