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

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import wicketforge.Constants;
import wicketforge.facet.WicketForgeFacet;

/**
 * WicketActionGroup
 */
public class WicketActionGroup extends DefaultActionGroup {
    public WicketActionGroup() {
        super("WicketForge", true);
        getTemplatePresentation().setDescription("Wicket");
        getTemplatePresentation().setIcon(Constants.WICKET_ICON);
    }

    public void update(AnActionEvent e) {
        e.getPresentation().setVisible(isUnderSourceRoots(e));
    }

    private static boolean isUnderSourceRoots(final AnActionEvent e) {
        final IdeView view = e.getData(DataKeys.IDE_VIEW);
        if (view != null) {
            final Module module = e.getData(DataKeys.MODULE);
            // let user create page/panel when we have a wicket-lib (so we can detect new facet)
            if (module != null && WicketForgeFacet.isLibraryPresent(module)) {
                ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(module.getProject()).getFileIndex();
                for (PsiDirectory dir : view.getDirectories()) {
                    if (projectFileIndex.isInSourceContent(dir.getVirtualFile()) && JavaDirectoryService.getInstance().getPackage(dir) != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
