package com.source.malaysiancustom;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.source.malaysiancustom");

        noClasses()
            .that()
            .resideInAnyPackage("com.source.malaysiancustom.service..")
            .or()
            .resideInAnyPackage("com.source.malaysiancustom.repository..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..com.source.malaysiancustom.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses);
    }
}
