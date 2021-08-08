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
package wicketforge.intention;

import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;
import wicketforge.search.MarkupIndex;
import wicketforge.templates.WicketTemplates;
import wicketforge.util.WicketFilenameUtil;
import wicketforge.util.WicketPsiUtil;

/**
 * AddMarkupPanelIntention
 */
public class AddMarkupPanelIntention extends AddMarkupIntention {

    @Override
    @NotNull
    public String getText() {
        return "Create Markup Panel";
    }

    @Override
    @NotNull
    public String getFamilyName() {
        return "Create Markup Panel";
    }

    @Override
    protected boolean hasResourceFile(@NotNull PsiClass psiClass) {
        return MarkupIndex.getBaseFile(psiClass) != null;
    }

    @NotNull
    @Override
    protected String getResourceFileName(@NotNull PsiClass psiClass) {
        return WicketFilenameUtil.getMarkupFilename(psiClass);
    }

    @NotNull
    @Override
    protected String getTemplateName() {
        return WicketTemplates.WICKET_PANEL_HTML;
    }

    @Override
    protected boolean isApplicableForClass(@NotNull PsiClass psiClass) {
        return WicketPsiUtil.isWicketPanel(psiClass);
    }
}
