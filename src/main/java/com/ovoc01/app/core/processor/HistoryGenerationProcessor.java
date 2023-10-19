package com.ovoc01.app.core.processor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;

@SupportedAnnotationTypes("com.ovoc01.app.core.annotation.preprocessor.GenerateProcessor")
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class HistoryGenerationProcessor extends AbstractProcessor{

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("Tato ilay izy");
        return true;
    }
    
}
