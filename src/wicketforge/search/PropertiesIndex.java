package wicketforge.search;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileContent;
import com.intellij.util.indexing.ID;
import com.intellij.util.xml.NanoXmlUtil;
import com.intellij.util.xml.XmlFileHeader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PropertiesIndex extends WicketResourceIndexExtension {
    public static final ID<String, Void> NAME = ID.create("WicketPropertiesIndex");

    @Nullable
    @Override
    protected WicketIndexUtil.ResourceInfo getResourceInfo(FileContent inputData) {
        return WicketIndexUtil.getClassNameFromProperties(inputData.getFile(), inputData.getProject());
    }

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
    public static PsiFile[] getAllFilesByClass(@NotNull final Project project, @NotNull final PsiClass psiClass, @NotNull final GlobalSearchScope scope) {
        return getFilesByClass(NAME, project, psiClass, scope, true);
    }

    @Nullable
    public static PsiFile getBaseFileByClass(@NotNull final Project project, @NotNull final PsiClass psiClass, @NotNull final GlobalSearchScope scope) {
        PsiFile[] files = getFilesByClass(NAME, project, psiClass, scope, false);
        return files.length > 0 ? files[0] : null;
    }
}
