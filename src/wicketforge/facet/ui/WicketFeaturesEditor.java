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

import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.facet.WicketForgeFacet;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * WicketFeaturesEditor
 */
public class WicketFeaturesEditor extends FacetEditorTab {
    private JPanel myMainPanel;
    private JTable resourcePathTable;
    private JPanel resourcePathToolbar;

    private WicketForgeFacet wicketForgeFacet;

    private List<VirtualFilePointer> items;

    public WicketFeaturesEditor(@NotNull FacetEditorContext editorContext) {
        this.wicketForgeFacet = (WicketForgeFacet) editorContext.getFacet();

        this.items = new ArrayList<VirtualFilePointer>(wicketForgeFacet.getResourcePaths());

        init();
    }

    public void reset() {
        items.clear();
        items.addAll(wicketForgeFacet.getResourcePaths());

        updateTable();
    }

    public boolean isModified() {
        return !items.equals(wicketForgeFacet.getResourcePaths());
    }

    public void apply() throws ConfigurationException {
        wicketForgeFacet.setResourcePaths(items);
    }

    public JComponent createComponent() {
        return myMainPanel;
    }

    public void disposeUIResources() {
    }

    @Nls
    public String getDisplayName() {
        return "Wicket Features";
    }

    private void init() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        // buttonAdd
        actionGroup.add(new AnAction("Add", "Add new item...", IconLoader.getIcon("/general/add.png")) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                doAdd();
            }
        });

        // buttonRemove
        actionGroup.add(new AnAction("Remove", "Remove item...", IconLoader.getIcon("/general/remove.png")) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                doRemove();
            }

            @Override
            public void update(AnActionEvent e) {
                e.getPresentation().setEnabled(resourcePathTable.getSelectedRow() >= 0);
            }
        });

        // buttonEdit
        actionGroup.add(new AnAction("Edit", "Edit item...", IconLoader.getIcon("/actions/editSource.png")) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                doEdit();
            }

            @Override
            public void update(AnActionEvent e) {
                e.getPresentation().setEnabled(resourcePathTable.getSelectedRow() >= 0);
            }
        });

        // buttonCopy
        actionGroup.add(new AnAction("Copy", "Copy item...", IconLoader.getIcon("/general/copy.png")) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                doCopy();
            }

            @Override
            public void update(AnActionEvent e) {
                e.getPresentation().setEnabled(resourcePathTable.getSelectedRow() >= 0);
            }
        });

        actionGroup.addSeparator();

        // buttonMoveUp
        actionGroup.add(new AnAction("Move Up", "Move item up...", IconLoader.getIcon("/actions/moveUp.png")) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                doMove(true);
            }

            @Override
            public void update(AnActionEvent e) {
                e.getPresentation().setEnabled(resourcePathTable.getSelectedRow() > 0);
            }
        });

        // buttonMoveDown
        actionGroup.add(new AnAction("Move Down", "Move item down...", IconLoader.getIcon("/actions/moveDown.png")) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                doMove(false);
            }

            @Override
            public void update(AnActionEvent e) {
                int selectedRow = resourcePathTable.getSelectedRow();
                e.getPresentation().setEnabled(selectedRow >= 0 && selectedRow < (resourcePathTable.getModel().getRowCount() - 1));
            }
        });

        resourcePathToolbar.add(ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, actionGroup, true).getComponent());
    }

    /**
     * called from idea's form designer -> Custom Create Components
     */
    private void createUIComponents() {
        // table
        resourcePathTable = new JTable(new AbstractTableModel() {
            public int getRowCount() {
                return items.size();
            }

            public int getColumnCount() {
                return 1;
            }

            public Object getValueAt(int rowIndex, int columnIndex) {
                return items.get(rowIndex).getPresentableUrl();
            }

            public String getColumnName(int column) {
                return "Path";
            }
        });
        resourcePathTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                // we have to init here with null
                setForeground(null);
                // show as selected only if enabled and never show focused (because only cell is focused...)
                Component c = super.getTableCellRendererComponent(table, value, table.isEnabled() && isSelected, false, row, column);
                // if is enabled and item have error -> set to red (need to be done after getTableCellRendererComponent -> so selected items also will be red...)
                if (table.isEnabled()) {
                    VirtualFile virtualFile = items.get(row).getFile();
                    if (virtualFile == null || !virtualFile.isValid() || !virtualFile.exists()) {
                        setForeground(Color.RED);
                    }
                }
                //
                return c;
            }
        });
        resourcePathTable.setPreferredScrollableViewportSize(new Dimension(-1, -1)); // need for +idea9
        resourcePathTable.setRowSelectionAllowed(true);
        resourcePathTable.setColumnSelectionAllowed(false);
        resourcePathTable.getTableHeader().setReorderingAllowed(false);
        resourcePathTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resourcePathTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // edit on double click
                    doEdit();
                }
            }
        });
    }

    /**
     * UI Update Table
     */
    private void updateTable() {
        resourcePathTable.revalidate();
        resourcePathTable.repaint();
    }

    /**
     * @param index
     */
    private void setSelectedRow(int index) {
        if (rowInRange(index)) {
            resourcePathTable.getSelectionModel().setSelectionInterval(index, index);
        } else {
            resourcePathTable.clearSelection();
        }
    }

    /**
     *
     */
    private boolean rowInRange(int row) {
        return row >= 0 && row < resourcePathTable.getModel().getRowCount();
    }

    /**
     *
     */
    private void doAdd() {
        showEditDialog("Add", null, new Runnable() {
            public void run(@NotNull String resourcePathUrl) {
                items.add(VirtualFilePointerManager.getInstance().create(resourcePathUrl, wicketForgeFacet.getModule(), null));
                setSelectedRow(items.size() - 1);
                updateTable();
            }
        });
    }

    /**
     *
     */
    private void doCopy() {
        int selectedRow = resourcePathTable.getSelectedRow();
        if (rowInRange(selectedRow)) {
            showEditDialog("Copy", items.get(selectedRow).getPresentableUrl(), new Runnable() {
                public void run(@NotNull String resourcePathUrl) {
                    items.add(VirtualFilePointerManager.getInstance().create(resourcePathUrl, wicketForgeFacet.getModule(), null));
                    setSelectedRow(items.size() - 1);
                    updateTable();
                }
            });
        }
    }

    /**
     *
     */
    private void doEdit() {
        final int selectedRow = resourcePathTable.getSelectedRow();
        if (rowInRange(selectedRow)) {
            showEditDialog("Edit", items.get(selectedRow).getPresentableUrl(), new Runnable() {
                public void run(@NotNull String resourcePathUrl) {
                    items.remove(selectedRow);
                    items.add(selectedRow, VirtualFilePointerManager.getInstance().create(resourcePathUrl, wicketForgeFacet.getModule(), null));
                    setSelectedRow(selectedRow);
                    updateTable();
                }
            });
        }
    }

    /**
     *
     */
    private void doRemove() {
        int selectedRow = resourcePathTable.getSelectedRow();
        if (rowInRange(selectedRow)) {
            items.remove(selectedRow);
            setSelectedRow(Math.min(selectedRow, items.size() - 1));
            updateTable();
        }
    }

    /**
     *
     */
    private void doMove(boolean up) {
        int selectedRow = resourcePathTable.getSelectedRow();
        int newPosition = selectedRow + (up ? -1 : 1);
        if (rowInRange(newPosition)) {
            items.add(newPosition, items.remove(selectedRow));
            setSelectedRow(newPosition);
            updateTable();
        }
    }
    /**
     * {@inheritDoc}
     */
    private void showEditDialog(@NotNull String title, @Nullable String resourcePath, @NotNull Runnable runnable) {
        WicketAlternateResourcePathItemDialog.showDialog(wicketForgeFacet.getModule().getProject(), title, resourcePath == null ? "" : resourcePath, runnable);
    }

    /**
     * Runnable for {@link #showEditDialog}
     */
    public static interface Runnable {
        void run(@NotNull String resourcePathUrl);
    }
}
