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
package org.brooth.androjeta.apt;

import org.brooth.androjeta.apt.processors.FindViewProcessor;
import org.brooth.androjeta.apt.processors.RetainProcessor;
import org.brooth.jeta.apt.JetaProcessor;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

/**
 * @author Oleg Khalidov (brooth@gmail.com)
 */
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_5)
public class AndrojetaProcessor extends JetaProcessor {

    @Override
    protected void addProcessors() {
        addProcessor(new FindViewProcessor());
        addProcessor(new RetainProcessor());
        super.addProcessors();
    }
}
