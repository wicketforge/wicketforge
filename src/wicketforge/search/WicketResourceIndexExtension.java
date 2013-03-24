package wicketforge.search;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.indexing.*;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;
import wicketforge.util.ResourceInfo;

import java.util.*;

abstract class WicketResourceIndexExtension extends ScalarIndexExtension<String> implements FileBasedIndex.InputFilter, DataIndexer<String, Void, FileContent>  {
    private final EnumeratorStringDescriptor keyDescriptor = new EnumeratorStringDescriptor();
    private static final char LOCALIZEDFILE_INDEXMARKER = '#';

    // TODO check add onchange in configuration and work with messageBus to recreate Indeces

    @NotNull
    @Override
    public final Map<String, Void> map(FileContent inputData) {
        ResourceInfo resourceInfo = ResourceInfo.from(inputData.getFile(), inputData.getProject());
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

    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return keyDescriptor;
    }

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
        return 2;
    }

    @NotNull
    protected static PsiFile[] getFilesByClass(@NotNull ID<String, Void> indexId, @NotNull final PsiClass psiClass, @NotNull final GlobalSearchScope scope, boolean all) {
        String name = psiClass.getQualifiedName();
        if (name == null) {
            return PsiFile.EMPTY_ARRAY;
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
