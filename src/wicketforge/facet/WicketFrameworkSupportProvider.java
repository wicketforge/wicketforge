/*
 * Copyright 2013 The WicketForge-Team
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
package wicketforge.facet;

import com.intellij.facet.ui.FacetBasedFrameworkSupportProvider;
import com.intellij.ide.util.frameworkSupport.FrameworkVersion;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import wicketforge.facet.ui.WicketVersion;

import java.util.List;

public class WicketFrameworkSupportProvider extends FacetBasedFrameworkSupportProvider<WicketForgeFacet> {
    public WicketFrameworkSupportProvider() {
        super(WicketForgeFacetType.INSTANCE);
    }

    @Override
    protected void setupConfiguration(WicketForgeFacet facet, ModifiableRootModel rootModel, FrameworkVersion version) {
        //
    }

    @Override
    public String getTitle() {
        return "Wicket";
    }

    @NotNull
    @Override
    public List<FrameworkVersion> getVersions() {
        List<FrameworkVersion> list = new SmartList<FrameworkVersion>();
        for (WicketVersion wicketVersion : WicketVersion.values()) {
            list.add(new FrameworkVersion(wicketVersion.getName(), "wicket", wicketVersion.getCore()));
        }
        return list;
    }
}
