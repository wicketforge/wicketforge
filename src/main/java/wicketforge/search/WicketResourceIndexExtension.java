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
package wicketforge.search;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.indexing.*;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.intellij.util.messages.MessageBus;

import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import wicketforge.facet.WicketForgeFacetConfiguration;
import wicketforge.util.WicketPsiUtil;

import java.util.*;

abstract class WicketResourceIndexExtension extends ScalarIndexExtension<String> implements FileBasedIndex.InputFilter, DataIndexer<String, Void, FileContent>  {
    private final EnumeratorStringDescriptor keyDescriptor = new EnumeratorStringDescriptor();
    private static final char LOCALIZEDFILE_INDEXMARKER = '#';

    protected WicketResourceIndexExtension() throws NotImplementedException{
    }

    protected WicketResourceIndexExtension(@NotNull MessageBus messageBus) {
        messageBus.connect().subscribe(WicketForgeFacetConfiguration.ADDITIONAL_PATHS_CHANGED, new Runnable() {
            @Override
            public void run() {
                FileBasedIndex.getInstance().requestRebuild(getName());
            }
        });
    }

    @NotNull
    @Override
    public Map<String, Void> map(FileContent inputData) {
        ResourceInfo resourceInfo = ResourceInfo.from(inputData);
        if (resourceInfo == null) {
            return Collections.emptyMap();
        } else if (resourceInfo.locale == null) {
            return Collections.singletonMap(resourceInfo.qualifiedName, null);
        } else {
            return Collections.singletonMap(resourceInfo.qualifiedName + LOCALIZEDFILE_INDEXMARKER, null);
        }
    }

    @NotNull
    @Override
    public DataIndexer<String, Void, FileContent> getIndexer() {
        return this;
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return keyDescriptor;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return this;
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @NotNull
    protected static PsiFile[] getFilesByClass(@NotNull ID<String, Void> indexId, @NotNull final PsiClass psiClass, boolean all) {
        String name = psiClass.getQualifiedName();
        if (name == null) {
            return PsiFile.EMPTY_ARRAY;
        }

        GlobalSearchScope scope;
        if (WicketPsiUtil.isInLibrary(psiClass)) {
            scope = ProjectScope.getLibrariesScope(psiClass.getProject());
        } else {
            Module module = ModuleUtil.findModuleForPsiElement(psiClass);
            if (module == null) {
                return PsiFile.EMPTY_ARRAY;
            }
            scope = WicketSearchScope.resourcesInModuleWithDependenciesAndLibraries(module);
        }

        final Collection<VirtualFile> files = FileBasedIndex.getInstance().getContainingFiles(indexId, name, scope);
        if (all) {
            files.addAll(FileBasedIndex.getInstance().getContainingFiles(indexId, name + LOCALIZEDFILE_INDEXMARKER, scope));
        }
        if (files.isEmpty()) {
            return PsiFile.EMPTY_ARRAY;
        }
        List<PsiFile> result = new ArrayList<PsiFile>();
        PsiManager manager = PsiManager.getInstance(psiClass.getProject());
        for (VirtualFile file : files) {
            if (file.isValid()) {
                PsiFile psiFile = manager.findFile(file);
                if (psiFile != null) {
                    result.add(psiFile);
                }
            }
        }
        return PsiUtilCore.toPsiFileArray(result);
    }


}
