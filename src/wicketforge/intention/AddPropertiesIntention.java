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
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import wicketforge.WicketForgeUtil;
import wicketforge.templates.WicketTemplates;

/**
 * Class level intention used to create properties files for Wicket page and panel components.
 */
public class AddPropertiesIntention extends AddMarkupIntention {

    @NotNull
    public String getText() {
        return "Create Properties File";
    }

    @NotNull
    public String getFamilyName() {
        return "Create Properties File";
    }

    @Override
    protected PsiFile getResourceFile(@NotNull PsiClass psiClass) {
        return WicketForgeUtil.getPropertiesFile(psiClass);
    }

    @NotNull
    @Override
    protected String getResourceFileName(@NotNull PsiClass psiClass) {
        return WicketForgeUtil.getPropertiesFileName(psiClass);
    }

    @NotNull
    @Override
    protected String getTemplateName() {
        return WicketTemplates.WICKET_PROPERTIES;
    }

    protected boolean isApplicableForClass(@NotNull PsiClass psiClass) {
        return WicketForgeUtil.isWicketComponentWithAssociatedMarkup(psiClass);
    }
}