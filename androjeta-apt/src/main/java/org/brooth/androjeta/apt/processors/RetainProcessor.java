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
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.brooth.androjeta.retain.Retain;
import org.brooth.androjeta.retain.RetainMetacode;
import org.brooth.jeta.apt.RoundContext;
import org.brooth.jeta.apt.processors.AbstractProcessor;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author Oleg Khalidov (brooth@gmail.com)
 */
public class RetainProcessor extends AbstractProcessor {

    public RetainProcessor() {
        super(Retain.class);
    }

    @Override
    public boolean process(TypeSpec.Builder builder, RoundContext roundContext) {
        ClassName masterClassName = ClassName.get(roundContext.metacodeContext().masterElement());
        builder.addSuperinterface(ParameterizedTypeName.get(
                ClassName.get(RetainMetacode.class), masterClassName));

        MethodSpec.Builder saveMethodBuilder = MethodSpec.
                methodBuilder("applySaveRetains")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(masterClassName, "master")
                .addParameter(ClassName.bestGuess("android.os.Bundle"), "bundle");

        MethodSpec.Builder restoreMethodBuilder = MethodSpec.
                methodBuilder("applyRestoreRetains")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(masterClassName, "master")
                .addParameter(ClassName.bestGuess("android.os.Bundle"), "bundle");

        for (Element element : roundContext.elements()) {
            String fieldName = element.getSimpleName().toString();
            String fieldType = processingContext.processingEnv().getTypeUtils().erasure(element.asType()).toString();
            String methodPostfix = getMethodPostfix(element);
            String key = roundContext.metacodeContext().masterElement().getSimpleName().toString() + "_" + fieldName;

            saveMethodBuilder.addStatement("bundle.put$L($S, master.$L)",
                    methodPostfix, key, fieldName);

            restoreMethodBuilder.addStatement("master.$L = ($L) bundle.get$L($S)",
                    fieldName, fieldType, methodPostfix, key);
        }

        builder.addMethod(saveMethodBuilder.build());
        builder.addMethod(restoreMethodBuilder.build());

        return false;
    }

    private String getMethodPostfix(Element element) {
        Elements elements = processingContext.processingEnv().getElementUtils();
        Types types = processingContext.processingEnv().getTypeUtils();

        TypeKind typeKind = element.asType().getKind();
        if (typeKind.isPrimitive()) {
            switch (typeKind) {
                case INT:
                    return "Int";

                default:
                    return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, typeKind.name());
            }
        }

        if (types.isAssignable(element.asType(), elements.getTypeElement("android.os.Parcelable").asType())) {
            return "Parcelable";

        } else if (types.isAssignable(element.asType(), elements.getTypeElement("java.lang.String").asType())) {
            return "String";

        } else {
            return "Serializable";
        }
    }
}
