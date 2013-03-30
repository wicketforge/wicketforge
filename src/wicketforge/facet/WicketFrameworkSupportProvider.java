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
import com.intellij.framework.library.DownloadableLibraryService;
import com.intellij.framework.library.FrameworkSupportWithLibrary;
import com.intellij.ide.util.frameworkSupport.FrameworkSupportConfigurableBase;
import com.intellij.ide.util.frameworkSupport.FrameworkSupportModel;
import com.intellij.ide.util.frameworkSupport.FrameworkSupportProviderBase;
import com.intellij.ide.util.frameworkSupport.FrameworkVersion;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.libraries.CustomLibraryDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.library.WicketLibraryType;

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
    public FrameworkSupportConfigurableBase createConfigurable(@NotNull FrameworkSupportModel model) {
        return new WicketFrameworkSupportConfigurable(this, model, getVersions(), getVersionLabelText());
    }

    private static class WicketFrameworkSupportConfigurable extends FrameworkSupportConfigurableBase implements FrameworkSupportWithLibrary {
        private WicketFrameworkSupportConfigurable(FrameworkSupportProviderBase frameworkSupportProvider, FrameworkSupportModel model, @NotNull List<FrameworkVersion> versions, @Nullable String versionLabelText) {
            super(frameworkSupportProvider, model, versions, versionLabelText);
        }

        @Override
        @NotNull
        public CustomLibraryDescription createLibraryDescription() {
            return DownloadableLibraryService.getInstance().createDescriptionForType(WicketLibraryType.class);
        }

        @Override
        public boolean isLibraryOnly() {
            return false;
        }
    }
}
