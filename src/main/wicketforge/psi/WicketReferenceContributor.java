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
package wicketforge.psi;

import com.intellij.patterns.*;
import com.intellij.psi.*;
import wicketforge.Constants;
import wicketforge.psi.references.ClassWicketIdReferenceProvider;
import wicketforge.psi.references.MarkupWicketIdReferenceProvider;

/**
 */
public class WicketReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {

        {// java -> new Component("..." ...)
            ElementPattern<PsiLiteralExpression> pattern = StandardPatterns.or(
                    //
                    PsiJavaPatterns.psiElement(PsiLiteralExpression.class).withParent(PsiExpressionList.class).withSuperParent(2, PsiCallExpression.class),
                    // for Anonymous create like Link's...
                    PsiJavaPatterns.psiElement(PsiLiteralExpression.class).withParent(PsiExpressionList.class).withSuperParent(2, PsiAnonymousClass.class).withSuperParent(3, PsiCallExpression.class)
            );
            registrar.registerReferenceProvider(pattern, new ClassWicketIdReferenceProvider());
        }

        {// html -> wicket:id
            XmlAttributeValuePattern pattern = XmlPatterns.xmlAttributeValue(XmlPatterns.xmlAttribute().withName(Constants.WICKET_ID));
            registrar.registerReferenceProvider(pattern, new MarkupWicketIdReferenceProvider());
        }
    }
}
