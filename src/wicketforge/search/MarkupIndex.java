package wicketforge.search;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.ID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MarkupIndex extends WicketResourceIndexExtension {
    public static final ID<String, Void> NAME = ID.create("WicketMarkupIndex");

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
    public static PsiFile[] getAllFilesByClass(@NotNull final PsiClass psiClass, @NotNull final GlobalSearchScope scope) {
        return getFilesByClass(NAME, psiClass, scope, true);
    }

    @Nullable
    public static PsiFile getBaseFileByClass(@NotNull final PsiClass psiClass, @NotNull final GlobalSearchScope scope) {
        PsiFile[] files = getFilesByClass(NAME, psiClass, scope, false);
        return files.length > 0 ? files[0] : null;
    }
}
