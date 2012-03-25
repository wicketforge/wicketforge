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
package wicketforge.psi.hierarchy.ui;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.newStructureView.TreeActionsOwner;
import com.intellij.ide.structureView.newStructureView.TreeModelWrapper;
import com.intellij.ide.util.treeView.AbstractTreeBuilder;
import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.ide.util.treeView.smartTree.SmartTreeStructure;
import com.intellij.ide.util.treeView.smartTree.TreeElementWrapper;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ex.IdeFocusTraversalPolicy;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.ui.TreeUIHelper;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.WicketForgeUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

/**
 */
public class WicketHierarchyDialog extends DialogWrapper implements TreeActionsOwner {
    private StructureViewModel structureViewModel;
    private Project project;
    private TreeModelWrapper treeModelWrapper;
    private boolean disposed;
    private Tree tree;

    private WicketHierarchyDialog(StructureViewModel structureViewModel, @NotNull Editor editor, @NotNull Project project, @Nullable Navigatable navigatable) {
        super(project, true);
        this.structureViewModel = structureViewModel;
        this.project = project;

        init();
    }

    @Override
    protected void dispose() {
        treeModelWrapper.dispose();
        disposed = true;
        super.dispose();
    }

    protected String getDimensionServiceKey() {
      return "WicketForge.WicketHierarchyDialog";
    }

    @Override
    protected Border createContentPaneBorder() {
        // we dont want border here
        return null;
    }

    @Override
    protected JComponent createSouthPanel() {
        // no buttons
        return null;
    }

    public JComponent getPreferredFocusedComponent() {
      return IdeFocusTraversalPolicy.getPreferredFocusedComponent(tree);
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        treeModelWrapper = new TreeModelWrapper(structureViewModel, this);

        SmartTreeStructure treeStructure = new SmartTreeStructure(project, treeModelWrapper) {
            public void rebuildTree() {
                if (!disposed) {
                    super.rebuildTree();
                }
            }

            public boolean isToBuildChildrenInBackground(final Object element) {
                return getRootElement() == element;
            }

            protected TreeElementWrapper createTree() {
                return new TreeElementWrapper(project, myModel.getRoot(), myModel);
            }
            
        };

        final DefaultTreeModel model = new DefaultTreeModel(new DefaultMutableTreeNode(treeStructure.getRootElement()));
        tree = new Tree(model);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        AbstractTreeBuilder myAbstractTreeBuilder = new AbstractTreeBuilder(tree, (DefaultTreeModel) tree.getModel(), treeStructure, null) {

        };
        tree.setCellRenderer(new NodeRenderer());
        TreeUIHelper.getInstance().installTreeSpeedSearch(tree);

        JPanel treePanel = new JPanel(new BorderLayout());
        treePanel.setPreferredSize(new Dimension(400, 500));
        treePanel.add(new JScrollPane(myAbstractTreeBuilder.getTree()), BorderLayout.CENTER);

        panel.add(treePanel, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        return panel;
    }

    public void setActionActive(String name, boolean state) {
    }

    public boolean isActionActive(String name) {
        return true;
    }

    /**
     *
     * @param psiFile
     * @param editor
     * @param project
     * @param navigatable
     * @param currentElement
     */
    public static void show(@NotNull PsiFile psiFile, @NotNull Editor editor, @NotNull Project project, @Nullable Navigatable navigatable, @NotNull PsiElement currentElement) {
        StructureViewModel viewModel = null;
        if (psiFile instanceof XmlFile) {
            viewModel = new WicketMarkupStructureTreeModel(psiFile);
        } else if (psiFile instanceof PsiJavaFile) {
            PsiClass psiClass = WicketForgeUtil.getParentWicketClass(currentElement);
            if (psiClass != null) {
                viewModel = new WicketClassStructureTreeModel(psiFile, psiClass);
            }
        }
        if (viewModel != null) {
            WicketHierarchyDialog dialog = new WicketHierarchyDialog(viewModel, editor, project, navigatable);
            VirtualFile virtualFile = psiFile.getVirtualFile();
            if (virtualFile != null) {
                // TODO add navigation etc... currently only for debug... if finished also add to idea menu (see plugin.xml)-->
                dialog.setTitle("FOR DEBUG ONLY " + virtualFile.getPresentableUrl());
            }
            dialog.show();
        } else {
            HintManager.getInstance().showErrorHint(editor, "No Wicket Hierarchy available");
        }
    }
}
