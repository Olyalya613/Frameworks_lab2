package ua.edu.catalog.web;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureTest {
    @Test
    void coreMustNotDependOnServletOrJdbc() {
        JavaClasses core = new ClassFileImporter().importPackages("ua.edu.catalog.core");
        noClasses().should().dependOnClassesThat().resideInAnyPackage(
                "jakarta.servlet..", "javax.servlet..", "java.sql..").check(core);
    }
}
