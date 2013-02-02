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
package wicketforge.completion;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.WicketForgeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
class PropertyModelVisitor extends JavaRecursiveElementVisitor {
    private List<PropertyExpression> results = new ArrayList<PropertyExpression>();

    @Override
    public void visitNewExpression(PsiNewExpression expression) {
        PsiExpressionList expressionList = expression.getArgumentList();
        if (expressionList != null) {
            PsiExpression[] expressions = expressionList.getExpressions();
            init(expressions);
        }
    }

    private void init(PsiExpression[] expressions) {
        // we're only going to complete on property models with two args
        if (expressions.length != 2) {
            return;
        }

        PsiExpression objectExpression = expressions[0];
        PsiExpression propertyExpression = expressions[1];

        if (objectExpression instanceof PsiNewExpression || objectExpression instanceof PsiReferenceExpression) {

            PsiClass objectClass = getObjectClass(objectExpression.getType());

            findMatches(objectClass, propertyExpression);
        }
    }

    @Nullable
    private PsiClass getObjectClass(@Nullable PsiType type) {
        PsiClass result = null;
        if (type instanceof PsiClassType) {
            PsiClassType classType = (PsiClassType) type;
            result = classType.resolve();
            if (result != null && WicketForgeUtil.isWicketModel(result)) {
                PsiType[] parameters = classType.getParameters();
                if (parameters.length == 1 && parameters[0] instanceof PsiClassType) {
                    PsiClassType paramClassType = (PsiClassType) parameters[0];
                    result = paramClassType.resolve();
                }
            }
        }
        return result;
    }

    private void findMatches(@Nullable PsiClass objectClass, @Nullable PsiExpression expression) {
        if (objectClass == null || !(expression instanceof PsiLiteralExpression)) {
            return;
        }

        List<String> expressionElements = initExpressions(expression);
        String last = expressionElements.get(expressionElements.size() - 1);

        String currentExpression = "";

        PsiClass targetClass = objectClass;
        for (String element : expressionElements) {
            PsiMethod[] methods;
            if (element.equals(last)) {
                methods = targetClass.getAllMethods();
                findMatchingMethods(element, methods, currentExpression);
            }
            else {
                methods = targetClass.findMethodsByName(propertyGetter(element), true);
                if (methods.length == 1) {
                    PsiMethod method = methods[0];
                    PsiType type = method.getReturnType();
                    if (type != null && !(type instanceof PsiPrimitiveType)) {
                        targetClass = ((PsiClassType) type).resolve();
                        if (targetClass == null) {
                            return;
                        }

                        if (currentExpression.length() > 0) {
                            currentExpression = currentExpression + "." + propertyName(method.getName());
                        } else {
                            currentExpression = propertyName(method.getName());
                        }
                    }
                }
            }
        }
    }

    private List<String> initExpressions(PsiExpression expression) {
        PsiLiteralExpression literal = (PsiLiteralExpression) expression;
        String literalText = literal.getText();
        String text = literalText.substring(1, literalText.indexOf("IntellijIdeaRulezzz"));

        boolean addExtraElement = (text.length() == 0 || text.charAt(text.length() - 1) == '.');
        String[] expressionElements = text.split("\\.");
        List<String> l = new ArrayList<String>(Arrays.asList(expressionElements));

        if (addExtraElement) {
            l.add("");
        }

        return l;
    }

    private void findMatchingMethods(String element, PsiMethod[] methods, String currentExpression) {
        for (PsiMethod method : methods) {
            String methodName = method.getName();
            if (methodNameMatches(methodName, element)) {
                PsiType psiType = method.getReturnTypeNoResolve();
                if (psiType != null) {
                    methodName = methodName + ":" + psiType.getCanonicalText();
                }
                results.add(new PropertyExpression(getCompletionResultKey(currentExpression, propertyName(methodName)), methodName));
            }
        }
    }

    private String getCompletionResultKey(String currentExpression, String property) {
        if (currentExpression.length() > 0) {
            return currentExpression + "." + property;
        } else {
            return property;
        }
    }

    @NotNull
    public List<PropertyExpression> getResults() {
        return results;
    }

    private boolean methodNameMatches(String methodName, String element) {
        return !methodName.equals("getClass") &&
               (methodName.startsWith(propertyGetter(element)) || methodName.startsWith(propertyIser(element)));

    }

    private static String propertyGetter(String s) {
        return s == null || s.length() == 0 ? "get" : "get" + StringUtil.capitalize(s);
    }

    private static String propertyIser(String s) {
        return s == null || s.length() == 0 ? "is" : "is" + StringUtil.capitalize(s);
    }

    private String propertyName(@NotNull String s) {
        String property;
        if (s.startsWith("is")) {
            property = s.substring(2);
        } else {
            property = s.substring(3);
        }
        return StringUtil.decapitalize(property);
    }

    /**
     * Visitor result
     */
    public static class PropertyExpression {
        private String expression;
        private String methodName;

        private PropertyExpression(@NotNull String expression, @NotNull String methodName) {
            this.expression = expression;
            this.methodName = methodName;
        }

        @NotNull
        public String getExpression() {
            return expression;
        }

        @NotNull
        public String getMethodName() {
            return methodName;
        }
    }
}
