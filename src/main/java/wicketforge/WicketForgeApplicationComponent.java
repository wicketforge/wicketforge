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

import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.ServiceManager;
import org.jetbrains.annotations.NotNull;
import wicketforge.inspection.ClassWicketIdInspection;
import wicketforge.inspection.EmptySrcAttributeInspection;
import wicketforge.inspection.MarkupWicketIdInspection;

/**
 *
 */
public class WicketForgeApplicationComponent implements ApplicationComponent, InspectionToolProvider {

    public static WicketForgeApplicationComponent get() {
      return ServiceManager.getService(WicketForgeApplicationComponent.class);
    }

    public WicketForgeApplicationComponent() {
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }

    @Override
    @NotNull
    public String getComponentName() {
        return "WicketForgeApplicationComponent";
    }

    @Override
    public Class[] getInspectionClasses() {
        return new Class[]{MarkupWicketIdInspection.class, ClassWicketIdInspection.class, EmptySrcAttributeInspection.class};
    }
}
