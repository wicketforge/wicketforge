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
package wicketforge.psi.hierarchy;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.SmartList;
import com.intellij.util.containers.Stack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.util.WicketPsiUtil;

import java.util.*;

class ClassWicketIdReferences {
    private final Map<PsiElement, List<PsiCallExpression>> addMap;
    private final Map<PsiCallExpression, ClassWicketIdNewComponentItem> newComponentItemMap;

    private ClassWicketIdReferences(@NotNull Map<PsiElement, List<PsiCallExpression>> addMap, @NotNull Map<PsiCallExpression, ClassWicketIdNewComponentItem> newComponentItemMap) {
        this.addMap = addMap;
        this.newComponentItemMap = newComponentItemMap;
    }

    /**
     * @param psiElement PsiClass or PsiCallExpression reference from a WicketMarkup component
     * @return List with all added wicket components (PsiCallExpression)
     */
    @Nullable
    public List<PsiCallExpression> getAdded(@Nullable PsiElement psiElement) {
        return addMap.get(psiElement);
    }

    /**
     * @param callExpression
     * @return
     */
    @Nullable
    public ClassWicketIdNewComponentItem getNewComponentItem(@Nullable PsiCallExpression callExpression) {
        return newComponentItemMap.get(callExpression);
    }

    /**
     * @return
     */
    public boolean containsClass(@NotNull PsiClass psiClass) {
        return addMap.containsKey(psiClass);
    }

    public static ClassWicketIdReferences build(@NotNull final PsiClass psiClass) {
        return build(psiClass, true);
    }

    public static ClassWicketIdReferences build(@NotNull final PsiClass psiClass, final boolean onlyThisMarkupContainer) {
        final Map<PsiElement, List<PsiCallExpression>> componentAddMap = new HashMap<PsiElement, List<PsiCallExpression>>(); // Key: PsiClass or PsiCallExpression reference from a WicketMarkup component
        final Map<PsiElement, List<PsiCallExpression>> componentReplaceMap = new HashMap<PsiElement, List<PsiCallExpression>>();
        psiClass.accept(new JavaRecursiveElementVisitor() {
            private MarkupReferences markupReferences = new MarkupReferences();

            @Override
            public void visitClass(PsiClass aClass) {
                if (onlyThisMarkupContainer && !aClass.equals(psiClass) && WicketPsiUtil.isWicketComponentWithAssociatedMarkup(aClass)) {
                    return; // we do not visit inner classes that have own markup
                }
                if (!(aClass instanceof PsiAnonymousClass) && WicketPsiUtil.isMarkupContainer(aClass)) {
                    markupReferences.pushCurrent(new SmartList<PsiElement>(aClass));
                    super.visitClass(aClass);
                    markupReferences.popCurrent();
                } else {
                    super.visitClass(aClass);
                }
            }

            @Override
            public void visitNewExpression(PsiNewExpression expression) {
                PsiClass aClass = expression.getAnonymousClass();
                if (aClass != null && WicketPsiUtil.isMarkupContainer(aClass)) {
                    markupReferences.pushCurrent(new SmartList<PsiElement>(expression));
                    super.visitNewExpression(expression);
                    markupReferences.popCurrent();
                } else {
                    super.visitNewExpression(expression);
                }
            }

            @Override
            public void visitMethod(PsiMethod method) {
                // if we have 'populateItem' method () -> we add parameter variable to our var stack, so item.add(...)
                // could be resolved to ListView/Loop hierarchy.
                // We dont need to check if its populateItem from a specific class (ex ListView) because first visitCallExpression
                // checks if add is from a MarkupContainer -> then he tries to resolve variable. If we have a variable in our stack
                // that is not from wicket populateItem, this does not matter.
                // (Normally we would check this but AbstractRepeater has only a onPopulate method. Only Loop and ListView have populateItem
                // these are not inherited. So we dont make this (security) check for now...)
                if ("populateItem".equals(method.getName())) {
                    PsiParameter[] parameters = method.getParameterList().getParameters();
                    if (parameters.length > 0) {
                        markupReferences.put(parameters[0], markupReferences.getCurrent());
                        markupReferences.pushCurrent(null); // enhancement 80 -> we have no currentMarkupReference inside populateItem -> should use item.add that can be resolved...
                        super.visitMethod(method);
                        markupReferences.popCurrent();
                        return; // in this case super already done...
                    }
                }
                super.visitMethod(method);
            }

            @Override
            public void visitCallExpression(PsiCallExpression callExpression) {
                // first super, so assignement adds could be resolved, ex: add(link = new Link(...))
                super.visitCallExpression(callExpression);

                if (!(callExpression instanceof PsiMethodCallExpression)) {
                    return;
                }
                PsiMethod method = callExpression.resolveMethod();
                if (method == null) {
                    return;
                }
                PsiClass methodCallClass = method.getContainingClass();
                if (methodCallClass == null) {
                    return;
                }
                String methodName = method.getName();

                Map<PsiElement, List<PsiCallExpression>> currentComponentMap;
                if (ArrayUtil.contains(methodName, "add", "addOrReplace", "autoAdd", "replace", "addToBorder", "replaceInBorder") && WicketPsiUtil.isMarkupContainer(methodCallClass)) {
                    currentComponentMap = componentAddMap;
                } else if (ArrayUtil.contains(methodName, "replaceWith") && WicketPsiUtil.isWicketComponent(methodCallClass)) {
                    currentComponentMap = componentReplaceMap;
                } else {
                    return;
                }

                // the markupReference class for the given add(...) etc...
                List<? extends PsiElement> markupReferenceList = null;
                // if call expression has a reference we got to search for it. ex: link.add(...) or MyPage.this.add(...)
                PsiReferenceExpression callMethodReference = PsiTreeUtil.getRequiredChildOfType(callExpression, PsiReferenceExpression.class);
                PsiElement element = PsiTreeUtil.getChildOfAnyType(callMethodReference, PsiReferenceExpression.class, PsiThisExpression.class);
                if (element instanceof PsiReferenceExpression) {
                    // ex: link.add(...)
                    element = ((PsiReferenceExpression) element).resolve();
                    if (element instanceof PsiVariable) {
                        markupReferenceList = new SmartList<PsiElement>(markupReferences.get((PsiVariable) element));
                        if (currentComponentMap != componentReplaceMap) {
                            for (Iterator<? extends PsiElement> iterator = markupReferenceList.iterator(); iterator.hasNext(); ) {
                                PsiElement markupReference = iterator.next();
                                if (markupReference instanceof PsiCallExpression) { // check instanceOf,  markupReference can also be PsiClass (issue 67)
                                    // this one will be our markupReference
                                    PsiClass classToBeCreated = WicketPsiUtil.getClassToBeCreated((PsiCallExpression) markupReference);
                                    // just to be sure our markupReference is not one with own markup (ex: someone could add components to an instance of an inner panel, bad practice but possible)
                                    // except borders, because they are WicketComponentWithAssociatedMarkup but add goes to BodyContainer of Border
                                    if (classToBeCreated != null && !classToBeCreated.equals(psiClass) && WicketPsiUtil.isWicketComponentWithAssociatedMarkup(classToBeCreated) && !WicketPsiUtil.isWicketBorder(classToBeCreated)) {
                                        iterator.remove();
                                    }
                                }
                            }
                        }
                    }
                } else if (element instanceof PsiThisExpression) {
                    // ex: MyPage.this.add(...)
                    element = ((PsiThisExpression) element).getQualifier();
                    if (element == null) {
                        // this.add(...) -> current
                        markupReferenceList = markupReferences.getCurrent();
                    } else {
                        // MyPage.this.add(...) -> resolve PsiClass references by this...
                        element = ((PsiJavaCodeReferenceElement) element).resolve();
                        if (element instanceof PsiClass) {
                            markupReferenceList = new SmartList<PsiElement>(element);
                        }
                    }
                } else {
                    // no reference -> so add to our current
                    markupReferenceList = markupReferences.getCurrent();
                }

                // no markupReference to add -> return
                if (markupReferenceList == null || markupReferenceList.isEmpty()) {
                    return;
                }

                // go thru all call argument expressions
                PsiExpressionList callExpressionList = callExpression.getArgumentList();
                if (callExpressionList != null) {
                    for (PsiElement markupReference : markupReferenceList) {
                        List<PsiCallExpression> addList = currentComponentMap.get(markupReference);
                        for (PsiExpression callParameterExpression : callExpressionList.getExpressions()) {
                            // resolve expressions for new wicket component
                            List<PsiCallExpression> callExpressions = resolveExpressionNewWicketComponent(callParameterExpression);
                            if (callExpressions.size() > 0) {
                                // and add to its
                                if (addList == null) {
                                    addList = callExpressions;
                                    currentComponentMap.put(markupReference, addList);
                                } else {
                                    addList.addAll(callExpressions);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void visitField(PsiField field) {
                super.visitField(field);
                // if field has an initializer...
                PsiExpression initializer = field.getInitializer();
                if (initializer != null) {
                    // put this into our var container
                    markupReferences.put(field, resolveExpressionNewWicketComponent(initializer));
                }
            }

            @Override
            public void visitDeclarationStatement(PsiDeclarationStatement statement) {
                super.visitDeclarationStatement(statement);
                for (PsiElement element : statement.getDeclaredElements()) {
                    // if is variable and has an initializer...
                    if (element instanceof PsiVariable) {
                        PsiExpression initializer = ((PsiVariable) element).getInitializer();
                        if (initializer != null) {
                            // put this into our var container
                            markupReferences.put((PsiVariable) element, resolveExpressionNewWicketComponent(initializer));
                        }
                    }
                }
            }

            @Override
            public void visitAssignmentExpression(PsiAssignmentExpression expression) {
                super.visitAssignmentExpression(expression);
                PsiExpression leftExpression = expression.getLExpression();
                if (leftExpression instanceof PsiReference) {
                    PsiElement resolvedElement = ((PsiReference) leftExpression).resolve();
                    if (resolvedElement instanceof PsiVariable) {
                        PsiExpression initializer = expression.getRExpression();
                        if (initializer != null) {
                            // put assigned expression to our var container
                            markupReferences.put((PsiVariable) resolvedElement, resolveExpressionNewWicketComponent(initializer));
                        }
                    }
                }
            }

            /**
             *
             * @param expression
             * @return referenced PsiCallExpression's (if they are Wicket Components)
             */
            @NotNull
            private List<PsiCallExpression> resolveExpressionNewWicketComponent(@Nullable PsiExpression expression) {
                List<PsiCallExpression> list = new SmartList<PsiCallExpression>();
                if (expression instanceof PsiConditionalExpression) {
                    List<PsiCallExpression> callExpressions = resolveExpressionNewWicketComponentInternal(((PsiConditionalExpression) expression).getThenExpression());
                    if (callExpressions != null) {
                        list.addAll(callExpressions);
                    }
                    callExpressions = resolveExpressionNewWicketComponentInternal(((PsiConditionalExpression) expression).getElseExpression());
                    if (callExpressions != null) {
                        list.addAll(callExpressions);
                    }
                } else {
                    List<PsiCallExpression> callExpressions = resolveExpressionNewWicketComponentInternal(expression);
                    if (callExpressions != null) {
                        list.addAll(callExpressions);
                    }
                }
                return list;
            }

            /**
             *
             */
            @Nullable
            private List<PsiCallExpression> resolveExpressionNewWicketComponentInternal(@Nullable PsiExpression expression) {
                // get new Expression/Variable from method chaining ex: new Label(...).setOutputMarkupId(true).setEnabled(true);
                if (expression instanceof PsiMethodCallExpression) {
                    PsiMethodCallExpression methodCallExpression = null;
                    while (expression instanceof PsiMethodCallExpression) {
                        methodCallExpression = (PsiMethodCallExpression) expression;
                        // check for ComponentFactory (like DateTextField.forDatePattern(...))
                        PsiClass classToBeCreated = WicketPsiUtil.getClassToBeCreated(methodCallExpression);
                        if (classToBeCreated != null && WicketPsiUtil.isWicketComponent(classToBeCreated)) {
                            return new SmartList<PsiCallExpression>(methodCallExpression);
                        }
                        PsiElement element = expression.getFirstChild();
                        if (element instanceof PsiReferenceExpression) {
                            element = element.getFirstChild();
                            if (element instanceof PsiExpression) {
                                expression = (PsiExpression) element;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    // check if chaining-method returns wicketcomponent (issue 69)
                    if (expression instanceof PsiReferenceExpression) {
                        PsiMethod method = methodCallExpression.resolveMethod();
                        if (method == null) {
                            return null;
                        }
                        PsiClass returnClass = PsiUtil.resolveClassInClassTypeOnly(method.getReturnType());
                        if (returnClass == null || !WicketPsiUtil.isWicketComponent(returnClass) || "get".equals(method.getName())) {
                            return null;
                        }
                    }
                }
                // get Variable from assignement ex: add(link = new Link(...))
                if (expression instanceof PsiAssignmentExpression) {
                    expression = ((PsiAssignmentExpression) expression).getLExpression();
                }

                // resolve
                if (expression instanceof PsiReference) {
                    // if it's a reference -> find possibly callExpression from our var stack
                    PsiElement resolvedElement = ((PsiReference) expression).resolve();
                    if (resolvedElement instanceof PsiVariable) {
                        List<PsiCallExpression> result = new SmartList<PsiCallExpression>();
                        for (PsiElement element : markupReferences.get((PsiVariable) resolvedElement)) {
                            if (element instanceof PsiCallExpression) {
                                result.add((PsiCallExpression) element);
                            }
                        }
                        return result;
                    }
                } else if (expression instanceof PsiCallExpression) {
                    // check if its a new wicket component
                    PsiClass classToBeCreated = WicketPsiUtil.getClassToBeCreated((PsiCallExpression) expression);
                    if (classToBeCreated != null && WicketPsiUtil.isWicketComponent(classToBeCreated)) {
                        return new SmartList<PsiCallExpression>((PsiCallExpression) expression);
                    }
                }
                return null;
            }
        });

        // merge all componentReplaceMap into componentAddMap
        for (Map.Entry<PsiElement, List<PsiCallExpression>> entry : componentReplaceMap.entrySet()) {
            // we need callExpression
            PsiElement key = entry.getKey();
            if (key instanceof PsiCallExpression) {
                PsiCallExpression callExpression = (PsiCallExpression) key;
                for (List<PsiCallExpression> list : componentAddMap.values()) {
                    if (list.contains(callExpression)) {
                        list.addAll(entry.getValue());
                    }
                }
            }
        }

        // put all new wicket component expressions to a list as ClassWicketIdNewComponentItem
        Map<PsiCallExpression, ClassWicketIdNewComponentItem> newComponentItemMap = new HashMap<PsiCallExpression, ClassWicketIdNewComponentItem>();
        for (List<PsiCallExpression> list : componentAddMap.values()) {
            for (PsiCallExpression callExpression : list) {
                if (!newComponentItemMap.containsKey(callExpression)) {
                    ClassWicketIdNewComponentItem newComponentItem = ClassWicketIdNewComponentItem.create(callExpression);
                    if (newComponentItem != null) {
                        newComponentItemMap.put(callExpression, newComponentItem);
                    }
                }
            }
        }

        return new ClassWicketIdReferences(componentAddMap, newComponentItemMap);
    }

    private static final class MarkupReferences {
        private Stack<List<? extends PsiElement>> currentStack = new Stack<List<? extends PsiElement>>();
        private Map<PsiVariable, List<? extends PsiElement>> variableMap = new HashMap<PsiVariable, List<? extends PsiElement>>();

        /**
         * Get PsiClass or PsiCallExpression as MarkupContainer of WicketComponent from variable.
         *
         * @param variable  PsiVariable
         * @return          PsiClass, PsiCallExpression of WicketComponent or null.
         */
        @NotNull
        private List<? extends PsiElement> get(@NotNull PsiVariable variable) {
            List<? extends PsiElement> result = variableMap.get(variable);
            return result == null ? Collections.<PsiElement>emptyList() : result;
        }

        /**
         * Put assigned PsiClass or PsiCallExpression of a WicketComponent to variable into our variableMap.
         *
         * @param variable      PsiVariable
         * @param elements       PsiClass or PsiCallExpression
         */
        private void put(@NotNull PsiVariable variable, @Nullable List<? extends PsiElement> elements) {
            if (elements == null || elements.isEmpty()) {
                // remove variable
                variableMap.remove(variable);
            } else {
                // ...add to our stack
                variableMap.put(variable, elements);
            }
        }

        /**
         * current is -> the current reference for add(...) -> PsiClass or PsiCallExpression (for anonymous classes)
         * @param current
         */
        private void pushCurrent(@Nullable List<? extends PsiElement> current) {
            currentStack.push(current);
        }

        @Nullable
        private List<? extends PsiElement> popCurrent() {
            return currentStack.pop();
        }

        @Nullable
        private List<? extends PsiElement> getCurrent() {
            return currentStack.isEmpty() ? null : currentStack.peek();
        }
    }
}
