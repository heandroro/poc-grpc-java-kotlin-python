import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureTest {

    private final JavaClasses classes = new ClassFileImporter()
        .importPackages("com.example.app");

    // ============================================================
    // Dependências entre camadas (Hexagonal)
    // ============================================================

    @Test
    void domain_should_not_depend_on_infrastructure() {
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
            .check(classes);
    }

    @Test
    void domain_should_not_depend_on_adapters() {
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("..adapter..")
            .check(classes);
    }

    @Test
    void application_should_not_depend_on_infrastructure() {
        noClasses().that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
            .check(classes);
    }

    @Test
    void services_should_not_depend_on_controllers() {
        noClasses().that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAPackage("..adapter.web..")
            .check(classes);
    }

    // ============================================================
    // Convenções de localização de classes
    // ============================================================

    @Test
    void controllers_should_reside_in_adapter_web() {
        classes().that().areAnnotatedWith(RestController.class)
            .should().resideInAPackage("..adapter.web..")
            .check(classes);
    }

    @Test
    void jpa_entities_should_reside_in_adapter_persistence() {
        classes().that().areAnnotatedWith(jakarta.persistence.Entity.class)
            .should().resideInAPackage("..adapter.persistence..")
            .check(classes);
    }

    @Test
    void configuration_classes_should_reside_in_infrastructure() {
        classes().that().areAnnotatedWith(org.springframework.context.annotation.Configuration.class)
            .and().areNotAnnotatedWith(org.springframework.boot.test.context.TestConfiguration.class)
            .should().resideInAPackage("..infrastructure..")
            .check(classes);
    }

    // ============================================================
    // Sem dependências Spring no domínio
    // ============================================================

    @Test
    void domain_classes_should_not_use_spring_annotations() {
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("org.springframework..")
            .check(classes);
    }

    // ============================================================
    // Arquitetura Hexagonal completa (ArchUnit Onion)
    // ============================================================

    @Test
    void hexagonal_architecture() {
        Architectures.onionArchitecture()
            .domainModels("..domain..")
            .applicationServices("..application..")
            .adapter("web",         "..adapter.web..")
            .adapter("persistence", "..adapter.persistence..")
            .adapter("messaging",   "..adapter.messaging..")
            .check(classes);
    }

    // ============================================================
    // Sem dependências cíclicas
    // ============================================================

    @Test
    void no_cycles_between_packages() {
        com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices()
            .matching("com.example.app.(*)..")
            .should().beFreeOfCycles()
            .check(classes);
    }
}
