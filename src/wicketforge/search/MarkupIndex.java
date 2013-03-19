package wicketforge.search;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.indexing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MarkupIndex extends WicketResourceIndexExtension {
    public static final ID<String, Void> NAME = ID.create("WicketMarkupIndex");

    @Nullable
    @Override
    protected WicketIndexUtil.ResourceInfo getResourceInfo(FileContent inputData) {
        return WicketIndexUtil.getClassNameFromMarkup(inputData.getFile(), inputData.getProject());
    }

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return NAME;
    }

    @Override
    public boolean acceptInput(VirtualFile file) {
        return StdFileTypes.HTML.equals(file.getFileType());
    }

    @NotNull
    public static PsiFile[] getAllFilesByClass(@NotNull final Project project, @NotNull final PsiClass psiClass, @NotNull final GlobalSearchScope scope) {
        return getFilesByClass(NAME, project, psiClass, scope, true);
    }

    @Nullable
    public static PsiFile getBaseFileByClass(@NotNull final Project project, @NotNull final PsiClass psiClass, @NotNull final GlobalSearchScope scope) {
        PsiFile[] files = getFilesByClass(NAME, project, psiClass, scope, false);
        return files.length > 0 ? files[0] : null;
    }
}
