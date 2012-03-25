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
package wicketforge.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.facet.autodetecting.FacetDetector;
import com.intellij.facet.autodetecting.FacetDetectorRegistry;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.VirtualFilePattern;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.Constants;

import javax.swing.*;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

/**
 * WicketForgeFacetType
 */
public class WicketForgeFacetType extends FacetType<WicketForgeFacet, WicketForgeFacetConfiguration> {

    public static final String STRING_ID = "wicketforge";
    public static final String PRESENTABLE_NAME = "Wicket";
    public static final FacetTypeId<WicketForgeFacet> ID = new FacetTypeId<WicketForgeFacet>(STRING_ID);
    public static final WicketForgeFacetType INSTANCE = new WicketForgeFacetType();

    public WicketForgeFacetType() {
        super(ID, STRING_ID, PRESENTABLE_NAME);
    }

    public WicketForgeFacetConfiguration createDefaultConfiguration() {
        return new WicketForgeFacetConfiguration();
    }

    public WicketForgeFacet createFacet(@NotNull Module module, String name, @NotNull WicketForgeFacetConfiguration configuration, @Nullable Facet underlyingFacet) {
        return new WicketForgeFacet(this, module, name, configuration, underlyingFacet);
    }

    public boolean isSuitableModuleType(ModuleType moduleType) {
        return (moduleType instanceof JavaModuleType);
    }

    public boolean isOnlyOneFacetAllowed() {
        return true;
    }

    public Icon getIcon() {
        return Constants.WICKET_ICON;
    }

    @Override
    public void registerDetectors(FacetDetectorRegistry<WicketForgeFacetConfiguration> registry) {
        VirtualFilePattern pattern = PlatformPatterns.virtualFile().with(new PatternCondition<VirtualFile>("containsText") {
            public boolean accepts(@NotNull final VirtualFile virtualFile, final ProcessingContext context) {
                try {
                    return VfsUtil.loadText(virtualFile).contains("xmlns:wicket");
                } catch (IOException e) {
                    return false;
                }
            }
        });
        registry.registerUniversalDetector(StdFileTypes.HTML, pattern, new WicketForgeFacetDetector());
    }

    private static class WicketForgeFacetDetector extends FacetDetector<VirtualFile, WicketForgeFacetConfiguration> {
        private WicketForgeFacetDetector() {
            super("wicketforge-detector");
        }

        public WicketForgeFacetConfiguration detectFacet(final VirtualFile source, final Collection<WicketForgeFacetConfiguration> existentFacetConfigurations) {
            Iterator<WicketForgeFacetConfiguration> iterator = existentFacetConfigurations.iterator();
            if (iterator.hasNext()) {
                return iterator.next();
            }
            return new WicketForgeFacetConfiguration();
        }
    }

}
