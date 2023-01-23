package wicketforge

import com.intellij.codeHighlighting.Pass
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import wicketforge.highlighting.WicketForgeHighlightingPassFactory

class WicketForgeService : StartupActivity {
    private var highlightingPassFactory = WicketForgeHighlightingPassFactory()

    override fun runActivity(project: Project) {
        // register wicketforge highlighting pass to text editors
        TextEditorHighlightingPassRegistrar
            .getInstance(project)
            .registerTextEditorHighlightingPass(
                highlightingPassFactory,
                null,
                intArrayOf(Pass.UPDATE_ALL),
                false,
                -1
            )
    }
}
