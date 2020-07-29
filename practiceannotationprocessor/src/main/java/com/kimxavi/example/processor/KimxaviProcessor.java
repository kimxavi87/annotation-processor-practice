package com.kimxavi.example.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

// META-INF/services
@AutoService(Processor.class)
public class KimxaviProcessor extends AbstractProcessor {
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(KimxaviProcessorAnnotation.class.getName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(KimxaviProcessorAnnotation.class);
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "annotations " + elements.toString());

        for (Element element : elements) {
            Name name = element.getSimpleName();
            if (element.getKind() != ElementKind.INTERFACE) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "ERROR " + name);
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Processing " + name);

                TypeElement typeElement = (TypeElement) element;
                ClassName className = ClassName.get(typeElement);

                MethodSpec methodSpec = MethodSpec.methodBuilder("show")
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return $S", "kimxavi")
                        .returns(String.class)
                        .build();

                TypeSpec typeSpec = TypeSpec.classBuilder("ShowService")
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(methodSpec)
                        .addSuperinterface(className)
                        .build();

                Filer filer = processingEnv.getFiler();

                JavaFile javaFile = JavaFile.builder(className.packageName(), typeSpec)
                        .addFileComment("This is generated from annotation processor $S", "kimxavi")
                        .build();

                try {
                    javaFile.writeTo(filer);
                } catch (IOException e) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "ERROR " + e);
                }
            }
        }

        return true;
    }
}
