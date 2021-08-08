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
package wicketforge.facet;

import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager;
import com.intellij.ui.*;
import com.intellij.ui.components.JBList;
import com.intellij.util.PlatformIcons;
import com.intellij.util.SmartList;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * WicketFacetEditorTab
 */
class WicketFacetEditorTab extends FacetEditorTab {
    private AdditionalPathPanel additionalPathPanel;

    public WicketFacetEditorTab(@NotNull FacetEditorContext editorContext) {
        super();
        this.additionalPathPanel = new AdditionalPathPanel(editorContext);
    }

    @Override
    public void reset() {
        additionalPathPanel.reset();
    }

    @Override
    public boolean isModified() {
        return additionalPathPanel.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        additionalPathPanel.apply();
        ApplicationManager.getApplication().getMessageBus().syncPublisher(WicketForgeFacetConfiguration.ADDITIONAL_PATHS_CHANGED).run();
    }

    @Override
    public JComponent createComponent() {
        return additionalPathPanel;
    }

    @Override
    public void disposeUIResources() {
        additionalPathPanel = null;
    }

    @Override
    @Nls
    public String getDisplayName() {
        return "Wicket";
    }

    /**
     *
     */
    private static class AdditionalPathPanel extends JPanel {
        private final FacetEditorContext editorContext;
        private final WicketForgeFacet wicketForgeFacet;
        private DefaultListModel additionalPathModel = new DefaultListModel(); // List of VirtualFilePointer

        public AdditionalPathPanel(@NotNull FacetEditorContext editorContext) {
            super(new BorderLayout());
            this.editorContext = editorContext;
            this.wicketForgeFacet = (WicketForgeFacet) editorContext.getFacet();

            reset(); // fill current items into model

            final JBList listComponent = new JBList(additionalPathModel);
            listComponent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            listComponent.setCellRenderer(new AdditionalPathListCellRenderer());
            listComponent.getEmptyText().setText("No additional paths defined");
            listComponent.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        doEdit(listComponent.getSelectedIndex());
                    }
                }
            });
            JPanel panel = ToolbarDecorator.createDecorator(listComponent)
                    .setAddAction(new AnActionButtonRunnable() {
                        @Override
                        public void run(AnActionButton button) {
                            doEdit(-1);
                        }
                    }).setEditAction(new AnActionButtonRunnable() {
                        @Override
                        public void run(AnActionButton button) {
                            doEdit(listComponent.getSelectedIndex());
                        }
                    }).setRemoveAction(new AnActionButtonRunnable() {
                        @Override
                        public void run(AnActionButton button) {
                            ListUtil.removeSelectedItems(listComponent);
                        }
                    }).setToolbarPosition(ActionToolbarPosition.TOP).createPanel();
            UIUtil.addBorder(panel, IdeBorderFactory.createTitledBorder("Additional Resource Search Path", false));
            add(panel, BorderLayout.CENTER);
        }

        public void reset() {
            additionalPathModel.clear();
            for (VirtualFilePointer filePointer : wicketForgeFacet.getResourcePaths()) {
                additionalPathModel.addElement(filePointer);
            }
        }

        public boolean isModified() {
            List<VirtualFilePointer> originalList = wicketForgeFacet.getResourcePaths();
            if (originalList.size() != additionalPathModel.size()) {
                return true;
            }
            for (int i = 0, originalListSize = originalList.size(); i < originalListSize; i++) {
                if (!originalList.get(i).equals(additionalPathModel.get(i))) {
                    return true;
                }
            }
            return false;
        }

        public void apply() {
            List<VirtualFilePointer> list = new SmartList<VirtualFilePointer>();
            for (int i = 0, n = additionalPathModel.size(); i < n; i++) {
                list.add((VirtualFilePointer) additionalPathModel.get(i));
            }
            wicketForgeFacet.setResourcePaths(list);
        }

        private void doEdit(int index) {
            FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);
            fileChooserDescriptor.setHideIgnored(false);
            VirtualFile virtualFile = FileChooser.chooseFile(fileChooserDescriptor, editorContext.getProject(), index >= 0 ? ((VirtualFilePointer) additionalPathModel.get(index)).getFile() : null);
            if (virtualFile != null) {
                VirtualFilePointer filePointer = VirtualFilePointerManager.getInstance().create(virtualFile, wicketForgeFacet.getModule(), null);
                if (index >= 0) {
                    additionalPathModel.set(index, filePointer);
                } else {
                    additionalPathModel.addElement(filePointer);
                }
            }
        }
    }

    private static class AdditionalPathListCellRenderer extends ColoredListCellRenderer {
        @Override
        protected void customizeCellRenderer(JList list, Object value, int index, boolean selected, boolean hasFocus) {
            if (value instanceof VirtualFilePointer) {
                VirtualFilePointer filePointer = (VirtualFilePointer) value;
                setIcon(PlatformIcons.DIRECTORY_CLOSED_ICON);
                append(filePointer.getPresentableUrl(), filePointer.isValid() ? SimpleTextAttributes.REGULAR_ATTRIBUTES : SimpleTextAttributes.ERROR_ATTRIBUTES, null);
            }
        }
    }
}
