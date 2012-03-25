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
import com.intellij.facet.ui.libraries.FacetLibrariesValidator;
import com.intellij.facet.ui.libraries.LibraryInfo;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.LanguageLevelProjectExtension;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.facet.WicketForgeFacet;
import wicketforge.facet.WicketForgeSupportModel;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * WicketFeaturesEditor
 */
public class WicketFeaturesEditor extends FacetEditorTab {
    private JPanel myMainPanel;
    private JComboBox myVersionComboBox;
    private JCheckBox myDateTimeCheckBox;
    private JCheckBox myExtensionsCheckBox;
    private JCheckBox mySpringCheckBox;
    private JCheckBox myVelocityCheckBox;
    private JCheckBox myJMXCheckBox;
    private JCheckBox mySpringAnnotationsCheckBox;
    private JCheckBox myAuthRolesCheckBox;
    private JCheckBox myGuiceCheckBox;
    private JPanel myExtraFeatures;
    private JTable resourcePathTable;
    private JPanel resourcePathToolbar;

    private WicketForgeFacet wicketForgeFacet;
    private FacetLibrariesValidator librariesValidator;

    private List<VirtualFilePointer> items;

    private WicketVersion myVersion;
    private Ref<Boolean> myDateTimeSupport = Ref.create(false);
    private Ref<Boolean> myExtensionsSupport = Ref.create(false);
    private Ref<Boolean> myVelocitySupport = Ref.create(false);
    private Ref<Boolean> mySpringSupport = Ref.create(false);
    private Ref<Boolean> myJmxSupport = Ref.create(false);
    private Ref<Boolean> mySpringAnnotSupport = Ref.create(false);
    private Ref<Boolean> myGuiceSupport = Ref.create(false);
    private Ref<Boolean> myAuthRolesSupport = Ref.create(false);
    private List<CheckBoxReference> myCheckBoxes = new ArrayList<CheckBoxReference>();

    private LibraryInfo[] myLastLibraryInfos = LibraryInfo.EMPTY_ARRAY;

    public WicketFeaturesEditor(@NotNull FacetEditorContext editorContext, final FacetLibrariesValidator librariesValidator) {
        this.librariesValidator = librariesValidator;
        this.wicketForgeFacet = (WicketForgeFacet) editorContext.getFacet();

        this.items = new ArrayList<VirtualFilePointer>(wicketForgeFacet.getResourcePaths());

        initCheckboxes();
        init();

        myVersionComboBox.setModel(new EnumComboBoxModel<WicketVersion>(WicketVersion.class));
        myVersionComboBox.setEnabled(myVersion == null);
        if (myVersion == null) {
            myVersion = WicketVersion.HIGHEST_STABLE;
        }

        myVersionComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                selectVersion(getSelectedVersion());
            }
        });

        mySpringCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (mySpringCheckBox.isSelected() && getSelectedVersion().isAtLeast(WicketVersion.WICKET_1_4)) {
                    mySpringAnnotationsCheckBox.setEnabled(true);
                    mySpringAnnotationsCheckBox.setSelected(mySpringAnnotSupport.get());
                }
                else {
                    mySpringAnnotationsCheckBox.setEnabled(false);
                    mySpringAnnotationsCheckBox.setSelected(false);
                }
            }
        });

        LanguageLevel languageLevel = LanguageLevelProjectExtension.getInstance(editorContext.getProject()).getLanguageLevel();
        myExtraFeatures.setVisible(languageLevel.isAtLeast(LanguageLevel.JDK_1_5));

        addCheckboxesListeners();
    }

    private WicketVersion getSelectedVersion() {
        return (WicketVersion) myVersionComboBox.getModel().getSelectedItem();
    }

    private void initCheckboxes() {
        myCheckBoxes.add(new CheckBoxReference(myDateTimeCheckBox, myDateTimeSupport));
        myCheckBoxes.add(new CheckBoxReference(myExtensionsCheckBox, myExtensionsSupport));
        myCheckBoxes.add(new CheckBoxReference(mySpringCheckBox, mySpringSupport));
        myCheckBoxes.add(new CheckBoxReference(myVelocityCheckBox, myVelocitySupport));
        myCheckBoxes.add(new CheckBoxReference(myJMXCheckBox, myJmxSupport));
        myCheckBoxes.add(new CheckBoxReference(mySpringAnnotationsCheckBox, mySpringAnnotSupport));
        myCheckBoxes.add(new CheckBoxReference(myGuiceCheckBox, myGuiceSupport));
        myCheckBoxes.add(new CheckBoxReference(myAuthRolesCheckBox, myAuthRolesSupport));
    }

    private void addCheckboxesListeners() {
        for (final CheckBoxReference element : myCheckBoxes) {
            element.getCheckBox().addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent event) {
                    updateRequiredLibraries();
                }
            });
        }
    }

    private void updateRequiredLibraries() {
        final LibraryInfo[] libraries = getRequiredLibraries();
        if (!Arrays.equals(libraries, myLastLibraryInfos)) {
            librariesValidator.setRequiredLibraries(libraries);
            myLastLibraryInfos = libraries;
        }
    }

    private void selectVersion(final WicketVersion wicketVersion) {
        myDateTimeCheckBox.setEnabled(true);
        myDateTimeCheckBox.setSelected(myDateTimeSupport.get());

        myExtensionsCheckBox.setEnabled(true);
        myExtensionsCheckBox.setSelected(myExtensionsSupport.get());

        mySpringCheckBox.setEnabled(true);
        mySpringCheckBox.setSelected(mySpringSupport.get());

        myGuiceCheckBox.setEnabled(true);
        myGuiceCheckBox.setSelected(myGuiceSupport.get());

        myVelocityCheckBox.setEnabled(true);
        myVelocityCheckBox.setSelected(myVelocitySupport.get());

        myJMXCheckBox.setEnabled(true);
        myJMXCheckBox.setSelected(myJmxSupport.get());

        mySpringAnnotationsCheckBox.setEnabled(mySpringSupport.get() && wicketVersion.getSpringAnnotations() != null);
        mySpringAnnotationsCheckBox.setSelected(mySpringSupport.get() && mySpringAnnotSupport.get() && wicketVersion.getSpringAnnotations() != null);

        myAuthRolesCheckBox.setEnabled(true);
        myAuthRolesCheckBox.setSelected(myAuthRolesSupport.get());
    }

    public void reset() {
        for (final CheckBoxReference element : myCheckBoxes) {
            element.reset();
        }

        myVersionComboBox.setSelectedItem(myVersion);

        items.clear();
        items.addAll(wicketForgeFacet.getResourcePaths());

        updateRequiredLibraries();
        updateTable();
    }

    public boolean isModified() {
        for (final CheckBoxReference element : myCheckBoxes) {
            if (element.isModified()) {
                return true;
            }
        }

        return myVersionComboBox.getSelectedItem() != myVersion || !items.equals(wicketForgeFacet.getResourcePaths());
    }

    public void apply() throws ConfigurationException {
        for (final CheckBoxReference element : myCheckBoxes) {
            element.apply();
        }

        myVersion = getSelectedVersion();

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

    @NotNull
    private LibraryInfo[] getRequiredLibraries() {
        LibraryInfo[] libs = myVersion.getCore();
        if (myDateTimeCheckBox.isSelected() && myVersion.getDateTime() != null) {
            libs = ArrayUtil.mergeArrays(libs, myVersion.getDateTime(), LibraryInfo.class);
        }

        if (myVelocityCheckBox.isSelected() && myVersion.getVelocity() != null) {
            libs = ArrayUtil.mergeArrays(libs, myVersion.getVelocity(), LibraryInfo.class);
        }

        if (myExtensionsCheckBox.isSelected()) {
            libs = ArrayUtil.mergeArrays(libs, myVersion.getExtensions(), LibraryInfo.class);
        }

        if (mySpringCheckBox.isSelected()) {
            libs = ArrayUtil.mergeArrays(libs, myVersion.getSpring(), LibraryInfo.class);
        }

        if (myJMXCheckBox.isSelected()) {
            libs = ArrayUtil.mergeArrays(libs, myVersion.getJmx(), LibraryInfo.class);
        }

        if (mySpringAnnotationsCheckBox.isSelected()) {
            libs = ArrayUtil.mergeArrays(libs, myVersion.getSpringAnnotations(), LibraryInfo.class);
        }

        if (myAuthRolesCheckBox.isSelected()) {
            libs = ArrayUtil.mergeArrays(libs, myVersion.getAuthRoles(), LibraryInfo.class);
        }

        if (myGuiceCheckBox.isSelected()) {
            libs = ArrayUtil.mergeArrays(libs, myVersion.getGuice(), LibraryInfo.class);
        }

        return libs;
    }

    private void init() {
        WicketForgeSupportModel model = WicketForgeSupportModel.createModel(wicketForgeFacet.getModule());

        myVersion = model.getVersion();
        myDateTimeSupport.set(model.isDateTime());
        myExtensionsSupport.set(model.isExtensions());
        myVelocitySupport.set(model.isVelocity());
        mySpringSupport.set(model.isSpring());
        myJmxSupport.set(model.isJmx());
        mySpringAnnotSupport.set(model.isSpringAnnot());
        myAuthRolesSupport.set(model.isAuthRoles());
        myGuiceSupport.set(model.isGuice());

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

    private class CheckBoxReference {

        private final JCheckBox myCheckBox;
        private final Ref<Boolean> myRef;

        public CheckBoxReference(JCheckBox checkBox, Ref<Boolean> ref) {
            myCheckBox = checkBox;
            myRef = ref;
        }

        public boolean isModified() {
            return myCheckBox.isSelected() != myRef.get();
        }

        public void apply() {
            myRef.set(myCheckBox.isSelected());
        }

        public void reset() {
            myCheckBox.setSelected(myRef.get());
        }

        public JCheckBox getCheckBox() {
            return myCheckBox;
        }
    }
}
