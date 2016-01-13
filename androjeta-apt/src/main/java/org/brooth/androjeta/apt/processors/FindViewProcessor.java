/*
 * Copyright 2015 Oleg Khalidov
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.brooth.androjeta.apt.processors;

import com.google.common.base.CaseFormat;
import com.squareup.javapoet.*;
import org.brooth.androjeta.ui.FindView;
import org.brooth.androjeta.ui.FindViewMetacode;
import org.brooth.jeta.apt.ProcessingContext;
import org.brooth.jeta.apt.RoundContext;
import org.brooth.jeta.apt.processors.AbstractProcessor;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.util.Locale;

/**
 * @author Oleg Khalidov (brooth@gmail.com)
 */
public class FindViewProcessor extends AbstractProcessor {

    private String appPackage;

    public FindViewProcessor() {
        super(FindView.class);
    }

    @Override
    public void init(ProcessingContext processingContext) {
        super.init(processingContext);

        if (!processingContext.processingProperties().containsKey("application.package"))
            throw new IllegalStateException("Option 'application.package' not presented, set it in jeta.properties " +
                    "in order to use @FindView");

        appPackage = processingContext.processingProperties().getProperty("application.package");
    }

    @Override
    public boolean process(TypeSpec.Builder builder, RoundContext roundContext) {
        ClassName masterClassName = ClassName.get(roundContext.metacodeContext().masterElement());
        builder.addSuperinterface(ParameterizedTypeName.get(
                ClassName.get(FindViewMetacode.class), masterClassName));

        MethodSpec.Builder methodBuilder = MethodSpec.
                methodBuilder("applyFindViews")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(masterClassName, "master")
                .addParameter(ClassName.bestGuess("android.app.Activity"), "activity");

        for (Element element : roundContext.elements()) {
            String fieldName = element.getSimpleName().toString();

            methodBuilder.addStatement("master.$L = ($T) activity.findViewById($L)",
                    fieldName, TypeName.get(element.asType()), getResName(element, roundContext));
        }

        builder.addMethod(methodBuilder.build());

        return false;
    }

    private String getResName(Element element, RoundContext roundContext) {
        FindView annotation = element.getAnnotation(FindView.class);
        if (annotation.value() != -1)
            return String.valueOf(annotation.value());

        String resName = !annotation.name().isEmpty() ? annotation.name() :
                CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, roundContext.metacodeContext().masterElement()
                        .getSimpleName().toString() + '_' + element.getSimpleName().toString());

        return String.format(Locale.ENGLISH, "%s.R.id.%s", appPackage, resName);
    }
}
