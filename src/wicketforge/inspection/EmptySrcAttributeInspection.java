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
import com.intellij.codeInspection.*;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.XmlRecursiveElementVisitor;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import wicketforge.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class EmptySrcAttributeInspection extends XmlSuppressableInspectionTool {

    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile psiFile, @NotNull InspectionManager manager, boolean b) {
        EmptySrcAttributeVisitor visitor = new EmptySrcAttributeVisitor(manager);
        psiFile.accept(visitor);
        return visitor.getProblemDescriptors();
    }

    @Override
    @Nls
    @NotNull
    public String getDisplayName() {
        return "Wicket Empty Src Attribute Inspection";
    }

    @Override
    @Nls
    @NotNull
    public String getGroupDisplayName() {
        return Constants.INTENSION_INSPECTION_GROUPNAME;
    }

    @Override
    @NotNull
    public String getShortName() {
        return "WicketForgeEmptySrcAttributeInspection";
    }

    @Override
    @NotNull
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.ERROR;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    /**
     *
     */
    private static class EmptySrcAttributeVisitor extends XmlRecursiveElementVisitor {
        private List<ProblemDescriptor> problemDescriptors = new ArrayList<ProblemDescriptor>();
        private InspectionManager manager;

        public EmptySrcAttributeVisitor(InspectionManager manager) {
            this.manager = manager;
        }

        @Override
        public void visitXmlAttribute(XmlAttribute attribute) {
            super.visitXmlAttribute(attribute);

            if ("src".equals(attribute.getName())) {
                XmlAttributeValue attributeValue = attribute.getValueElement();
                if (attributeValue != null && StringUtil.isEmpty(attributeValue.getValue())) {
                    problemDescriptors.add(manager.createProblemDescriptor(attribute, "Empty src attribute will result in extra call to server",
                            (LocalQuickFix) null, ProblemHighlightType.GENERIC_ERROR, true));
                }
            }
        }

        public ProblemDescriptor[] getProblemDescriptors() {
            return problemDescriptors.toArray(new ProblemDescriptor[problemDescriptors.size()]);
        }
    }
}
