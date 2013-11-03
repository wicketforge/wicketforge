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
package wicketforge;

import com.intellij.codeInsight.intention.IntentionManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import wicketforge.intention.*;

/**
 *
 */
public class WicketForgeProjectComponent implements ProjectComponent {
    @SuppressWarnings({"UnusedDeclaration"})
    public WicketForgeProjectComponent(Project project) {
    }

    @Override
    public void initComponent() {
        IntentionManager intentionManager = IntentionManager.getInstance();
        intentionManager.registerIntentionAndMetaData(new AddMarkupPageIntention(), Constants.INTENSION_INSPECTION_GROUPNAME);
        intentionManager.registerIntentionAndMetaData(new AddMarkupPanelIntention(), Constants.INTENSION_INSPECTION_GROUPNAME);
        intentionManager.registerIntentionAndMetaData(new AddMarkupBorderIntention(), Constants.INTENSION_INSPECTION_GROUPNAME);
        intentionManager.registerIntentionAndMetaData(new AddPropertiesIntention(), Constants.INTENSION_INSPECTION_GROUPNAME);
        intentionManager.registerIntentionAndMetaData(new AddPropertiesXMLIntention(), Constants.INTENSION_INSPECTION_GROUPNAME);
    }

    @Override
    public void disposeComponent() {
    }

    @Override
    @NotNull
    public String getComponentName() {
        return "WicketForge Project Component";
    }

    @Override
    public void projectOpened() {
    }

    @Override
    public void projectClosed() {
    }

}
