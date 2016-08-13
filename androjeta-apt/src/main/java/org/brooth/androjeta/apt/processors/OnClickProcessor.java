/*
 * Copyright 2016 Oleg Khalidov
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
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.brooth.androjeta.ui.OnClick;
import org.brooth.androjeta.ui.OnClickMetacode;
import org.brooth.androjeta.ui.OnLongClick;
import org.brooth.jeta.apt.RoundContext;
import org.brooth.jeta.apt.processors.AbstractProcessor;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * @author Oleg Khalidov (brooth@gmail.com)
 */
public class OnClickProcessor extends AbstractProcessor {

    private String appPackage;

    public OnClickProcessor() {
        super(OnClick.class);
    }

    @Override
    public Set<TypeElement> collectElementsAnnotatedWith() {
        Set<TypeElement> set = new HashSet<>(2);
        Elements elementUtils = processingContext.processingEnv().getElementUtils();
        set.add(elementUtils.getTypeElement(OnClick.class.getCanonicalName()));
        set.add(elementUtils.getTypeElement(OnLongClick.class.getCanonicalName()));
        return set;
    }

    @Override
    public boolean process(TypeSpec.Builder builder, RoundContext roundContext) {
        if (appPackage == null) {
            if (!processingContext.processingProperties().containsKey("application.package"))
                throw new IllegalStateException("Option 'application.package' not presented," +
                        " set it in jeta.properties in order to use @OnClick/@OnLongClick in " +
                        roundContext.metacodeContext().masterElement().getQualifiedName().toString());
            appPackage = processingContext.processingProperties().getProperty("application.package");
        }

        ClassName masterClassName = ClassName.get(roundContext.metacodeContext().masterElement());
        builder.addSuperinterface(ParameterizedTypeName.get(
                ClassName.get(OnClickMetacode.class), masterClassName));

        MethodSpec.Builder methodBuilder = MethodSpec.
                methodBuilder("applyOnClicks")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(masterClassName, "master", Modifier.FINAL)
                .addParameter(ClassName.bestGuess("android.app.Activity"), "activity");

        ClassName viewClassName = ClassName.bestGuess("android.view.View");

        int idx = 0;
        for (Element element : roundContext.elements()) {
            String varName = "view" + (idx++);
            String methodName = element.getSimpleName().toString();

            String callbackCallStr = ((ExecutableElement) element).getParameters().isEmpty() ?
                    "master.$N()" : "master.$N(view)";

            OnClick onClickAnnotation = element.getAnnotation(OnClick.class);
            if (onClickAnnotation != null) {
                TypeSpec listenerTypeSpec = TypeSpec.anonymousClassBuilder("")
                        .addSuperinterface(ClassName.bestGuess("android.view.View.OnClickListener"))
                        .addMethod(MethodSpec.methodBuilder("onClick")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(viewClassName, "view")
                                .returns(void.class)
                                .addStatement(callbackCallStr, methodName)
                                .build())
                        .build();

                methodBuilder.addStatement("$T $L = ($T) activity.findViewById($L)",
                        viewClassName, varName, viewClassName,
                        getResName(element, onClickAnnotation.value(), onClickAnnotation.name(), "onClick", roundContext))
                        .beginControlFlow("if($L != null)", varName)
                        .addStatement("$L.setOnClickListener($L);", varName, listenerTypeSpec)
                        .endControlFlow();
            }

            OnLongClick onLongClickAnnotation = element.getAnnotation(OnLongClick.class);
            if (onLongClickAnnotation != null) {
                MethodSpec.Builder clickMethodBuilder = MethodSpec.methodBuilder("onLongClick")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(viewClassName, "view")
                        .returns(boolean.class);

                if (((ExecutableElement) element).getReturnType().getKind().name().equals("boolean")) {
                    clickMethodBuilder.addStatement("return " + callbackCallStr, methodName);

                } else {
                    clickMethodBuilder.addStatement(callbackCallStr, methodName)
                            .addStatement("return true");
                }

                TypeSpec listenerTypeSpec = TypeSpec.anonymousClassBuilder("")
                        .addSuperinterface(ClassName.bestGuess("android.view.View.OnLongClickListener"))
                        .addMethod(clickMethodBuilder.build())
                        .build();

                methodBuilder.addStatement("$T $L = ($T) activity.findViewById($L)",
                        viewClassName, varName, viewClassName,
                        getResName(element, onLongClickAnnotation.value(), onLongClickAnnotation.name(), "onLongClick", roundContext))
                        .beginControlFlow("if($L != null)", varName)
                        .addStatement("$L.setOnLongClickListener($L);", varName, listenerTypeSpec)
                        .endControlFlow();
            }
        }

        builder.addMethod(methodBuilder.build());

        return false;
    }

    private String getResName(Element element, int value, String name, String prefix, RoundContext roundContext) {
        if (value != -1)
            return String.valueOf(value);

        String resName = !name.isEmpty() ? name :
                CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, roundContext.metacodeContext().masterElement()
                        .getSimpleName().toString() + '_' +
                        CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL,
                                element.getSimpleName().toString().replace(prefix, "")));

        return String.format(Locale.ENGLISH, "%s.R.id.%s", appPackage, resName);
    }
}
