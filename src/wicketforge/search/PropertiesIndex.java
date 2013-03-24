package wicketforge.search;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.ID;
import com.intellij.util.xml.NanoXmlUtil;
import com.intellij.util.xml.XmlFileHeader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PropertiesIndex extends WicketResourceIndexExtension {
    public static final ID<String, Void> NAME = ID.create("WicketPropertiesIndex");

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return NAME;
    }

    @Override
    public boolean acceptInput(VirtualFile file) {
        FileType fileType = file.getFileType();
        if (StdFileTypes.PROPERTIES.equals(fileType)) {
            return true;
        }
        if (StdFileTypes.XML.equals(fileType)) {
            XmlFileHeader fileHeader = NanoXmlUtil.parseHeader(file);
            //noinspection ConstantConditions
            if (fileHeader != null && "properties".equals(fileHeader.getRootTagLocalName())) {
                return true;
            }
        }
        return false;
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
