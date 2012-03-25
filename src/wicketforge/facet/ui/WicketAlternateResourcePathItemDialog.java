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
package wicketforge.facet.ui;

import com.intellij.ide.util.BrowseFilesListener;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.FieldPanel;
import com.intellij.ui.InsertPathAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;

/**
 *
 */
final class WicketAlternateResourcePathItemDialog extends DialogWrapper {
    private JPanel topPanel;
    private JPanel resourcePathPanel;
    private FieldPanel resourcePathTextFieldPanel;

    /**
     *
     */
    private WicketAlternateResourcePathItemDialog(@NotNull final Project project, @NotNull String title, @NotNull String resourcePath) {
        super(true);
        setTitle(title);
        init();
        // resourcePathTextFieldPanel
        final JTextField textField = new JTextField();
        final FileChooserDescriptor resourcePathsChooserDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);
        InsertPathAction.addTo(textField, resourcePathsChooserDescriptor);
        resourcePathsChooserDescriptor.setHideIgnored(false);
        BrowseFilesListener listener = new BrowseFilesListener(textField, "", "Alternate Resource Path", resourcePathsChooserDescriptor) {
            @Override
            protected VirtualFile getFileToSelect() {
                VirtualFile result = super.getFileToSelect();
                if (result == null) {
                    result = project.getBaseDir();
                }
                return result;
            }
        };
        resourcePathTextFieldPanel = new FieldPanel(textField, null, null, listener, new Runnable() {
            public void run() {
                changed();
            }
        });
        resourcePathTextFieldPanel.setText(resourcePath);
        resourcePathTextFieldPanel.getTextField().addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                resourcePathTextFieldPanel.getTextField().select(0, resourcePathTextFieldPanel.getText().length());
            }
        });
        resourcePathPanel.add(resourcePathTextFieldPanel);
        //
        changed();
    }

    private void changed() {
        String resourcePath = resourcePathTextFieldPanel.getText();
        // OK enabled if we have input...
        setOKActionEnabled(resourcePath.length() > 0);
        // Show error
        setErrorText(resourcePath.length() == 0 || new File(resourcePath).exists() ? null : "Path not found.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JComponent createCenterPanel() {
        return topPanel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent getPreferredFocusedComponent() {
        return resourcePathTextFieldPanel.getTextField();
    }

    /**
     * 
     */
    public static void showDialog(@NotNull Project project, @NotNull String title, @NotNull String resourcePath, @NotNull WicketFeaturesEditor.Runnable runnable) {
        WicketAlternateResourcePathItemDialog dialog = new WicketAlternateResourcePathItemDialog(project, title, resourcePath);
        dialog.show();
        if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
            String resourcePathNew = dialog.resourcePathTextFieldPanel.getText();
            if (!resourcePathNew.equals(resourcePath)) {
                runnable.run(VfsUtil.pathToUrl(resourcePathNew));
            }
        }
    }
}
