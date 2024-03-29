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

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import wicketforge.Constants;
import icons.WicketForgeIcons;

/**
 * CreatePageDialog
 */
public class CreatePageDialog extends AbstractCreateDialog {

    public CreatePageDialog(@NotNull Project project, @NotNull ActionRunnable actionRunnable, @NotNull String title, @NotNull PsiDirectory directory) {
        super(project, actionRunnable, title, directory);
    }

    @Override
    protected PsiClass getDefaultClass(@NotNull Project project) {
        return JavaPsiFacade.getInstance(project).findClass(Constants.WICKET_PAGE, GlobalSearchScope.allScope(project));
    }
}
