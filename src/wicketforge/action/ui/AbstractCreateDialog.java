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
package wicketforge.action.ui;

import com.intellij.ide.IdeBundle;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.WicketForgeUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractCreateDialog extends DialogWrapper {
    private JTextField classNameTextField;
    private JComboBox extendsComboBox;
    private JCheckBox createAssociatedMarkupFileCheckBox;
    private JPanel contentPane;
    private JPanel chooseDifferentDestinationFolderPanel;
    private JCheckBox chooseDifferentDestinationFolderCheckBox;

    private ActionRunnable actionRunnable;
    private Project project;
    private Module module;
    private PsiDirectory markupDirectory;
    private PsiPackage psiPackage;

    AbstractCreateDialog(@NotNull Project project, @NotNull ActionRunnable actionRunnable, @NotNull String title, @NotNull PsiDirectory directory) {
        super(project, false);

        this.actionRunnable = actionRunnable;
        this.project = project;
        this.module = ModuleUtil.findModuleForPsiElement(directory);
        this.markupDirectory = directory;
        this.psiPackage = JavaDirectoryService.getInstance().getPackage(directory);

        init();
        setTitle(title);
    }

    protected void init() {
        super.init();

        setResizable(true);
        setModal(true);

        PsiClass psiClass = getDefaultClass(project);
        if (psiClass != null) {
            extendsComboBox.setModel(new PsiClassListModel(psiClass, ClassInheritorsSearch.search(psiClass).findAll()));
        }

        // if we have only 1 destination, we dont offer a different folder selection
        if (WicketForgeUtil.getResourceRoots(module).length < 2) {
            chooseDifferentDestinationFolderPanel.setVisible(false);
        } else {
            createAssociatedMarkupFileCheckBox.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    chooseDifferentDestinationFolderCheckBox.setEnabled(createAssociatedMarkupFileCheckBox.isSelected());
                }
            });
            chooseDifferentDestinationFolderCheckBox.setEnabled(createAssociatedMarkupFileCheckBox.isSelected());
        }
    }

    @Nullable
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    protected void doOKAction() {
        String inputString = classNameTextField.getText().trim();
        String extendsClass = extendsComboBox.getSelectedItem() instanceof String ? (String) extendsComboBox.getSelectedItem() : "";
        boolean createMarkup = createAssociatedMarkupFileCheckBox.isSelected();

        if (module != null && psiPackage != null && createMarkup && chooseDifferentDestinationFolderCheckBox.isSelected()) {
            PsiDirectory directory = WicketForgeUtil.selectTargetDirectory(psiPackage.getQualifiedName(), project, module);
            if (directory == null) {
                return; // aborted
            }
            markupDirectory = directory;
        }

        if (validateInput(inputString, extendsClass) && actionRunnable.run(inputString, extendsClass, createMarkup, markupDirectory)) {
            super.doOKAction();
        }
    }

    private boolean validateInput(@NotNull String inputString, @NotNull String extendsClass) {
        if (inputString.length() == 0) {
            setErrorText(IdeBundle.message("error.name.should.be.specified"));
            return false;
        }
        if (extendsClass.length() == 0) {
            setErrorText("Invalid base class");
            return false;
        }

        setErrorText(null);
        return true;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return classNameTextField;
    }

    @Nullable
    protected abstract PsiClass getDefaultClass(@NotNull final Project project);

    private static class PsiClassListModel extends AbstractListModel implements ComboBoxModel {
        private String selectedClassName;
        private ArrayList<String> classNames;

        PsiClassListModel(@NotNull PsiClass defaultClass, @NotNull Collection<PsiClass> classes) {
            classNames = new ArrayList<String>();

            for (PsiClass clazz : classes) {
                if (!clazz.hasModifierProperty(PsiModifier.FINAL)) {
                    classNames.add(clazz.getQualifiedName());
                }
            }

            selectedClassName = defaultClass.getQualifiedName();
        }

        public int getSize() {
            return classNames.size();
        }

        public Object getElementAt(int i) {
            return classNames.get(i);
        }

        public void setSelectedItem(Object o) {
            selectedClassName = (String) o;
            fireContentsChanged(this, 0, getSize());
        }

        public Object getSelectedItem() {
            return selectedClassName;
        }
    }

    public static interface ActionRunnable {
        boolean run(@NotNull String inputString, @NotNull String extendsClass, boolean hasMarkup, @NotNull PsiDirectory markupDirectory);
    }
}
