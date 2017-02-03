package wicketforge.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Fold/Shorten wicket's LambdaModel.of with getter and setter
 * <p>
 * See about folding builder here:
 * http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support/folding_builder.html
 *
 * @author Andrew Kondratev
 */
public class LambdaModelFoldingBuilder extends FoldingBuilderEx {
    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean b) {
        FoldingGroup group = FoldingGroup.newGroup("lambdaModel");
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        Collection<PsiImportStatement> importStatements = PsiTreeUtil.findChildrenOfType(root, PsiImportStatement.class);
        Collection<PsiMethodCallExpression> expressions = PsiTreeUtil.findChildrenOfType(root, PsiMethodCallExpression.class);


        // Is wicket lambda model imported into this class?
        boolean lambdaModelImported = importStatements.parallelStream()
                .anyMatch(statement -> "org.apache.wicket.model.LambdaModel".equals(statement.getQualifiedName()));

        if (lambdaModelImported) {
            expressions.stream()
                    .filter(expression -> "LambdaModel.of".equals(expression.getMethodExpression().getQualifiedName()))
                    .forEach(expression -> {
                        PsiExpression[] args = expression.getArgumentList().getExpressions();
                        // LambdaModel.of with model getter and setter should have exactly 3 params
                        if (args.length != 3) {
                            return;
                        }
                        PsiExpression modelDef = args[0];
                        PsiExpression getterDef = args[1];
                        PsiExpression setterDef = args[2];
                        String getterStr = getterDef.getText();
                        String setterStr = setterDef.getText();
                        PsiType modelDefType = modelDef.getType();
                        boolean isGet = getterStr.contains("::get") && getterStr.replace("::get", "/").equals(setterStr.replace("::set", "/"));
                        boolean isBoolIs = getterStr.contains("::is") && getterStr.replace("::is", "/").equals(setterStr.replace("::set", "/"));

                        // First param is assignable to IModel and following two looks like getter and setter
                        if (
                                modelDefType == null ||
                                        !PsiType.getTypeByName("org.apache.wicket.model.IModel", root.getProject(), root.getResolveScope()).isAssignableFrom(modelDefType) ||
                                        !(isGet || isBoolIs)
                                ) {
                            return;
                        }

                        // Add folding descriptor if we've got here
                        descriptors.add(new FoldGetSetDescriptor(
                                expression.getNode(),
                                new TextRange(getterDef.getTextRange().getStartOffset(), setterDef.getTextRange().getEndOffset()),
                                group,
                                expression,
                                getterStr,
                                isBoolIs
                        ));
                    });
        }
        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode astNode) {
        return "...";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode astNode) {
        return true;
    }

    static class FoldGetSetDescriptor extends FoldingDescriptor {

        final PsiMethodCallExpression expression;
        final String getterStr;
        final boolean isBoolIs;

        FoldGetSetDescriptor(@NotNull ASTNode node, @NotNull TextRange range, @Nullable FoldingGroup group, PsiMethodCallExpression expression, String getterStr, boolean isBoolIs) {
            super(node, range, group);
            this.expression = expression;
            this.getterStr = getterStr;
            this.isBoolIs = isBoolIs;
        }

        @Nullable
        @Override
        public String getPlaceholderText() {
            if (isBoolIs) {
                // Shorten is and set into one is/set
                return getterStr.replace("::is", "::is/set");
            } else {
                // Shorten get and set into one get/set
                return getterStr.replace("::get", "::get/set");
            }
        }
    }
}
