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

import com.intellij.ide.actions.CreateElementActionBase;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import wicketforge.Constants;
import wicketforge.WicketForgeUtil;
import wicketforge.action.ui.AbstractCreateDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * WicketClassBaseAction
 */
public abstract class WicketClassBaseAction extends CreateElementActionBase {

    protected WicketClassBaseAction(String text, String description) {
        super(text, description, Constants.WICKET_ICON);
    }

    @Override
    protected void checkBeforeCreate(String newName, PsiDirectory directory) throws IncorrectOperationException {
    }

    @NotNull
    @Override
    protected PsiElement[] create(String newName, PsiDirectory directory) throws Exception {
        PsiElement[] elements = new PsiElement[1];
        elements[0] = JavaDirectoryService.getInstance().createClass(directory, newName);
        return elements;
    }

    protected class ActionRunnableImpl implements AbstractCreateDialog.ActionRunnable {
        private final Project project;
        private final PsiDirectory psiDirectory;
        private String templateName;
        private SmartPsiElementPointer[] createdElements;

        public ActionRunnableImpl(final Project project, final PsiDirectory directory, final String templateName) {
            this.project = project;
            this.psiDirectory = directory;
            this.templateName = templateName;
            createdElements = new SmartPsiElementPointer[0];
        }

        public boolean run(@NotNull final String inputString, @NotNull final String extendsClass, final boolean hasMarkup, @NotNull final PsiDirectory markupDirectory) {
            try {
                JavaDirectoryService.getInstance().checkCreateClass(psiDirectory, inputString);
            } catch (IncorrectOperationException e) {
                Messages.showMessageDialog(project, filterMessage(e.getMessage()), getErrorTitle(), Messages.getErrorIcon());
                return false;
            }

            final Exception[] exception = new Exception[1];

            final Runnable command = new Runnable() {
                public void run() {
                    // TODO -> localhistoryAction disabled because IDEA10 has different interface than IDEA9. think about: do we really need a localhistory here?
//                    LocalHistoryAction action = LocalHistoryAction.NULL;

                    //noinspection EmptyFinallyBlock
                    try {
//                        action = LocalHistory.startAction(project, getActionName(psiDirectory, inputString));
                        final JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
                        final PsiClass superClass = facade.findClass(extendsClass, GlobalSearchScope.allScope(project));
                        assert superClass != null;
                        final PsiElement[] psiElements = create(inputString, psiDirectory);
                        final PsiElementFactory factory = facade.getElementFactory();
                        final PsiJavaCodeReferenceElement extendingClass = factory.createClassReferenceElement(superClass);
                        ApplicationManager.getApplication().runWriteAction(new Runnable() {
                            public void run() {
                                try {
                                    PsiClass clazz = (PsiClass) psiElements[0];
                                    if (extendingClass != null) {
                                        clazz.getExtendsList().add(extendingClass);
                                    }
                                    /*
                                    if (WicketForgeUtil.isWicketPanel(clazz)) {
                                        PsiMethod method = superClass.getConstructors()[0];
                                        if (method != null) {
                                            OverrideImplementUtil.overrideOrImplement(clazz, method);
                                        }
                                    }
                                    */

                                    if (hasMarkup) {
                                        WicketForgeUtil.createFileFromTemplate(WicketForgeUtil.getMarkupFileName(clazz), markupDirectory, templateName);
                                    }
                                }
                                catch (IncorrectOperationException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        createdElements = new SmartPsiElementPointer[psiElements.length];
                        SmartPointerManager manager = SmartPointerManager.getInstance(project);
                        for (int i = 0; i < createdElements.length; i++) {
                            createdElements[i] = manager.createSmartPsiElementPointer(psiElements[i]);
                        }
                    }
                    catch (Exception ex) {
                        exception[0] = ex;
                    }
                    finally {
//                        action.finish();
                    }
                }
            };

            CommandProcessor.getInstance().executeCommand(project, command, getCommandName(), null);

            if (exception[0] != null) {
                String errorMessage = filterMessage(exception[0].getMessage());
                if (errorMessage == null || errorMessage.length() == 0) {
                    errorMessage = exception[0].toString();
                }

                Messages.showMessageDialog(project, errorMessage, getErrorTitle(), Messages.getErrorIcon());
            }

            return createdElements.length != 0;
        }

        @NotNull
        public final PsiElement[] getCreatedElements() {
            List<PsiElement> elts = new ArrayList<PsiElement>();
            for (SmartPsiElementPointer pointer : createdElements) {
                final PsiElement elt = pointer.getElement();
                if (elt != null) {
                    elts.add(elt);
                }
            }
            return elts.toArray(new PsiElement[elts.size()]);
        }
    }
}
