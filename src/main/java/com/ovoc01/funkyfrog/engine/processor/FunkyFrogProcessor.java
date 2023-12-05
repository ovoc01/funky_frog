package com.ovoc01.funkyfrog.engine.processor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;

import com.google.auto.service.AutoService;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

@SupportedAnnotationTypes("com.ovoc01.funkyfrog.core.annotation.preprocessor.FunkyFrogMapping")
@SupportedSourceVersion(javax.lang.model.SourceVersion.RELEASE_17)
@AutoService(javax.annotation.processing.Processor.class)
public class FunkyFrogProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // System.out.println(
        // Thread.currentThread().getContextClassLoader().getResource("").getPath());
        ClassPool classPool = ClassPool.getDefault();
        try {
            classPool.insertClassPath(System.getProperty("user.dir"));
            classPool.insertClassPath("/target/classes");
            //classPool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
            System.out.println(System.getProperty("user.dir") + "/target/classes");
            CtClass ctClass = classPool.get("com.example.javassist.Rectangle");
            System.out.println(ctClass.getName());
            
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        
        return true;
    }

    private String getClasspath(RoundEnvironment roundEnv) {
        Elements elements = processingEnv.getElementUtils();
        StringBuilder classpath = new StringBuilder();

        for (Element element : roundEnv.getRootElements()) {
            TypeElement typeElement = (TypeElement) element;
            String binaryName = elements.getBinaryName(typeElement).toString();
            String classpathEntry = binaryName.replace('.', '/');
            classpath.append(classpathEntry);
            break;
        }

        return classpath.toString();
    }

}
