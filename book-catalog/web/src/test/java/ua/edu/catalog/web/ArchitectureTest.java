package ua.edu.catalog.web;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

class ArchitectureTest {
    private final JavaClasses classes = new ClassFileImporter().importPackages("ua.edu.catalog");

    @Test
    void webMustNotDependOnPersistence() {
        noClasses().that().resideInAPackage("..web..")
                .should().dependOnClassesThat().resideInAnyPackage("..persistence..")
                .check(classes);
    }

    @Test
    void coreMustNotDependOnServletOrJdbc() {
        noClasses().that().resideInAPackage("..core..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "jakarta.servlet..", "javax.servlet..", "java.sql..")
                .check(classes);
    }

    @Test
    void controllersMustBeOnlyInWeb() {
        classes().that().haveSimpleNameEndingWith("Servlet")
                .should().resideInAPackage("..web..")
                .check(classes);
    }

    @Test
    void concreteRepositoriesMustBeOnlyInPersistence() {
        classes().that().haveSimpleNameEndingWith("Repository")
                .and().areNotInterfaces()
                .should().resideInAPackage("..persistence..")
                .check(classes);
    }
}
