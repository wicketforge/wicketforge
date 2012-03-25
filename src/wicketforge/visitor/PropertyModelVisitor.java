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
package wicketforge.visitor;

import com.intellij.psi.*;
import wicketforge.WicketForgeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class PropertyModelVisitor extends JavaRecursiveElementVisitor {

    private PsiClass internalClass;
    private List<CompletionResult> results = new ArrayList<CompletionResult>();

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

        PsiExpression obj = expressions[0];
        if (obj instanceof PsiNewExpression) {
            PsiNewExpression newExpression = (PsiNewExpression) obj;
            PsiType type = newExpression.getType();
            initInternalClass(type);
        }
        else if (obj instanceof PsiReferenceExpression) {
            PsiReferenceExpression refexp = (PsiReferenceExpression) obj;
            PsiType type = refexp.getType();
            initInternalClass(type);
        }

        initCompletions(expressions[1]);
    }

    private void initInternalClass(PsiType type) {
        if (type instanceof PsiClassType) {
            PsiClassType classType = (PsiClassType) type;
            internalClass = classType.resolve();
            if (internalClass != null && WicketForgeUtil.isWicketModel(internalClass)) {
                PsiType[] parameters = classType.getParameters();
                if (parameters.length == 1 && parameters[0] instanceof PsiClassType) {
                    PsiClassType paramClassType = (PsiClassType) parameters[0];
                    internalClass = paramClassType.resolve();
                }
            }
        }
    }

    private void initCompletions(PsiExpression expression) {
        if (internalClass == null || !(expression instanceof PsiLiteralExpression)) {
            return;
        }

        List<String> expressionElements = initExpressions(expression);
        String last = expressionElements.get(expressionElements.size() - 1);

        String currentExpression = "";

        PsiClass targetClass = internalClass;
        for (String element : expressionElements) {
            PsiMethod[] methods;
            if (element.equals(last)) {
                methods = targetClass.getAllMethods();
                findCompletions(element, methods, currentExpression);
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
                            currentExpression = String.format("%s.%s", currentExpression, propertyName(method.getName()));
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

    private void findCompletions(String element, PsiMethod[] methods, String currentExpression) {
        for (PsiMethod method : methods) {
            String methodName = method.getName();
            if (methodNameMatches(methodName, element)) {
                results.add(new CompletionResult(getCompletionResultKey(currentExpression, propertyName(methodName)), String.format("%s:%s", methodName, method.getReturnTypeNoResolve().getCanonicalText())));
            }
        }
    }

    private String getCompletionResultKey(String currentExpression, String property) {
        if (currentExpression.length() > 0) {
            return String.format("%s.%s", currentExpression, property);
        }
        else {
            return property;
        }
    }

    public List<CompletionResult> getResults() {
        return results;
    }

    private boolean methodNameMatches(String methodName, String element) {
        return !methodName.equals("getClass") &&
               (methodName.startsWith(propertyGetter(element)) || methodName.startsWith(propertyIser(element)));

    }

    private String propertyGetter(String s) {
        if (s == null || s.length() == 0) {
            return "get";
        }
        return String.format("get%C%s", s.charAt(0), s.substring(1, s.length()));
    }

    private String propertyIser(String s) {
        if (s == null || s.length() == 0) {
            return "is";
        }
        return String.format("is%C%s", s.charAt(0), s.substring(1, s.length()));
    }

    private String propertyName(String s) {
        String property;
        if (s.startsWith("is")) {
            property = s.substring(2, s.length());
        }
        else {
            property = s.substring(3, s.length());
        }
        return String.format("%c%s", Character.toLowerCase(property.charAt(0)), property.substring(1, property.length()));
    }
}
