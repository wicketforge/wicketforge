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

import com.intellij.lang.properties.psi.PropertiesElementFactory;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.WicketForgeUtil;
import wicketforge.action.ui.ExtractHtmlTextDialog;
import wicketforge.templates.WicketTemplates;

/**
 */
public class ExtractHtmlTextActionHandler extends EditorWriteActionHandler {

    private static ExtractHtmlTextActionHandler INSTANCE;

    public static ExtractHtmlTextActionHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExtractHtmlTextActionHandler();
        }
        return INSTANCE;
    }

    public void executeWriteAction(final Editor editor, DataContext dataContext) {
        final Project project = editor.getProject();
        if (project == null) {
            return;
        }
        final PsiFile psiFile = DataKeys.PSI_FILE.getData(dataContext);
        if (psiFile == null) {
            return;
        }
        PsiDirectory psiDirectory = psiFile.getContainingDirectory();
        if (psiDirectory == null) {
            return;
        }
        int offset = editor.getCaretModel().getOffset();
        if (offset <= 0) {
            return;
        }
        PsiElement psiElement = psiFile.findElementAt(offset);
        if (psiElement == null) {
            return;
        }
        final PsiClass psiClass;
        if (StdFileTypes.JAVA.equals(psiFile.getFileType())) {
            psiClass = WicketForgeUtil.getParentWicketClass(psiElement);
        } else if (StdFileTypes.HTML.equals(psiFile.getFileType())) {
            psiClass = WicketForgeUtil.getMarkupClass(psiFile);
        } else {
            return;
        }

        SelectionModel selection = editor.getSelectionModel();

        String selectedText = selection.getSelectedText();
        if (selectedText == null) {
            Messages.showErrorDialog(project, "No text was selected", "Extract Text");
            return;
        }

        ExtractHtmlTextDialog.ActionRunnable actionRunnable = new ExtractHtmlTextDialog.ActionRunnable() {
            public boolean run(@Nullable final PropertiesFile pFile, @NotNull final PsiDirectory destinationDirectory, final @NotNull String key, final @NotNull String value) {
                return ApplicationManager.getApplication().runWriteAction(new Computable<Boolean>() {
                    public Boolean compute() {
                        PropertiesFile propertiesFile = pFile;

                        // if not set, we create for the component one...
                        if (propertiesFile == null) {
                            PsiElement element = WicketForgeUtil.createFileFromTemplate(WicketForgeUtil.getPropertiesFileName(psiClass), destinationDirectory, WicketTemplates.WICKET_PROPERTIES);
                            if (element == null) {
                                Messages.showErrorDialog(project, "Could not create properties file.", "Extract Text");
                                return false;
                            }
                            propertiesFile = (PropertiesFile) element;
                        }

                        // add 
                        propertiesFile.addProperty(PropertiesElementFactory.createProperty(project, key, value));

                        // replace in html with wicket:message
                        if (psiFile instanceof XmlFile) {
                            String startTag = String.format("<wicket:message key=\"%s\">", key);
                            String endTag = "</wicket:message>";
                            CaretModel caret = editor.getCaretModel();
                            SelectionModel selection = editor.getSelectionModel();
                            int start = selection.getSelectionStart();
                            int end = selection.getSelectionEnd();
                            selection.removeSelection();

                            caret.moveToOffset(start);
                            EditorModificationUtil.insertStringAtCaret(editor, startTag);
                            // move the caret to the end of the selection
                            caret.moveToOffset(startTag.length() + end);
                            EditorModificationUtil.insertStringAtCaret(editor, endTag);
                        }

                        return true;
                    }
                });
            }
        };
        ExtractHtmlTextDialog dialog = new ExtractHtmlTextDialog(project, actionRunnable, "Extract Text", psiClass, psiDirectory, selectedText);
        dialog.show();
    }
}


