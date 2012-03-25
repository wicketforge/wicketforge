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

import com.intellij.psi.XmlRecursiveElementVisitor;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import wicketforge.Constants;

@Deprecated
public class WicketMarkupVisitor extends XmlRecursiveElementVisitor {
    @Override
    public void visitXmlAttribute(XmlAttribute attribute) {
        super.visitXmlAttribute(attribute);
        if (Constants.WICKET_ID.equals(attribute.getName())) {
            XmlAttributeValue attributeValue = attribute.getValueElement();
            if (attributeValue != null) {
                visitWicketId(attribute, attributeValue);
            }
        }
    }

    public void visitWicketId(XmlAttribute attribute, XmlAttributeValue attributeValue) {
        //
    }
}
