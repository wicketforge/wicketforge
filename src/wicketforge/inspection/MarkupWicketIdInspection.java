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
package wicketforge.inspection;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.XmlSuppressableInspectionTool;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.XmlElementVisitor;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import wicketforge.Constants;
import wicketforge.psi.references.MarkupWicketIdReference;

/**
 */
public class MarkupWicketIdInspection extends XmlSuppressableInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new XmlElementVisitor() {
            @Override
            public void visitXmlAttribute(XmlAttribute attribute) {
                super.visitXmlAttribute(attribute);
                if (Constants.WICKET_ID.equals(attribute.getName())) {
                    XmlAttributeValue attributeValue = attribute.getValueElement();
                    if (attributeValue != null && attributeValue.getTextLength() > 0) {
                        for (PsiReference reference : attributeValue.getReferences()) {
                            if (reference instanceof MarkupWicketIdReference && ((MarkupWicketIdReference) reference).multiResolve(false).length == 0) {
                                holder.registerProblem(holder.getManager().createProblemDescriptor(attributeValue, "Wicket id reference problem",
                                        (LocalQuickFix) null, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, true));
                            }
                        }
                    }
                }
            }
        };
    }

    @Override
    @Nls
    @NotNull
    public String getGroupDisplayName() {
        return Constants.INTENSION_INSPECTION_GROUPNAME;
    }

    @Override
    @Nls
    @NotNull
    public String getDisplayName() {
        return "Wicket HTML ID Inspection";
    }

    @Override
    @NonNls
    @NotNull
    public String getShortName() {
        return "WicketForgeHtmlIdInspection";
    }

    @Override
    @NotNull
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WARNING;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
