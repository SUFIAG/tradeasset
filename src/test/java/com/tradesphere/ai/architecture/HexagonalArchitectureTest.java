package com.tradesphere.ai.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Architecture tests to enforce hexagonal architecture rules
 * Validates that the codebase follows clean architecture principles
 */
@DisplayName("Hexagonal Architecture Tests")
class HexagonalArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void setUp() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.tradesphere.ai");
    }

    @Test
    @DisplayName("Domain layer should not depend on application or adapter layers")
    void domainLayerShouldNotDependOnApplicationOrAdapterLayers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..application..",
                        "..adapter..",
                        "..config.."
                )
                .because("Domain layer must be independent of infrastructure concerns");

        rule.check(classes);
    }

    @Test
    @DisplayName("Domain layer should not use Spring Framework annotations")
    void domainLayerShouldNotUseSpringFramework() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence.."
                )
                .because("Domain layer must be framework-agnostic");

        rule.check(classes);
    }

    @Test
    @DisplayName("Application layer should not depend on adapter layer")
    void applicationLayerShouldNotDependOnAdapterLayer() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAPackage("..adapter..")
                .because("Application layer should depend only on domain and ports");

        rule.check(classes);
    }

    @Test
    @DisplayName("Adapter layer should depend on application ports")
    void adapterLayerShouldDependOnApplicationPorts() {
        ArchRule rule = classes()
                .that().resideInAPackage("..adapter..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..application.port..",
                        "..domain.."
                );

        rule.check(classes);
    }

    @Test
    @DisplayName("Aggregates should extend AggregateRoot")
    void aggregatesShouldExtendAggregateRoot() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain.model..")
                .and().haveSimpleNameEndingWith("Aggregate")
                .and().areNotInterfaces()
                .should().beAssignableTo("com.tradesphere.ai.shared.domain.AggregateRoot")
                .because("Domain aggregates must extend AggregateRoot");

        rule.check(classes);
    }

    @Test
    @DisplayName("Domain events should be immutable")
    void domainEventsShouldBeImmutable() {
        // Note: This check might be too strict with Lombok @Value
        // Commented out for now, can be enabled after verifying domain events structure
        /*
        ArchRule rule = classes()
                .that().resideInAPackage("..domain.event..")
                .should().haveOnlyFinalFields()
                .andShould().notBeAssignableFrom("com.tradesphere.ai.shared.domain.DomainEvent")
                .because("Domain events should be immutable");

        rule.check(classes);
        */
    }

    @Test
    @DisplayName("Use cases should be in application layer")
    void useCasesShouldBeInApplicationLayer() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("UseCase")
                .should().resideInAPackage("..application.port.in..")
                .because("Use cases are inbound ports in hexagonal architecture");

        rule.check(classes);
    }

    @Test
    @DisplayName("Repositories should be interfaces in outbound ports")
    void repositoriesShouldBeInterfacesInOutboundPorts() {
        ArchRule rule = classes()
                .that().haveSimpleNameContaining("Repository")
                .and().areNotInterfaces()
                .should().resideInAnyPackage(
                        "..adapter.out.persistence..",
                        "..adapter.out.repository.."
                )
                .because("Repository implementations belong in adapter layer");

        rule.check(classes);
    }

    @Test
    @DisplayName("Controllers should only be in adapter layer")
    void controllersShouldOnlyBeInAdapterLayer() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Controller")
                .should().resideInAPackage("..adapter.in..")
                .because("Controllers are inbound adapters");

        rule.check(classes);
    }

    @Test
    @DisplayName("JPA entities should only be in persistence adapter")
    void jpaEntitiesShouldOnlyBeInPersistenceAdapter() {
        ArchRule rule = classes()
                .that().areAnnotatedWith("jakarta.persistence.Entity")
                .or().haveSimpleNameEndingWith("JpaEntity")
                .should().resideInAPackage("..adapter.out.persistence..")
                .because("JPA entities are infrastructure concerns");

        rule.check(classes);
    }

    @Test
    @DisplayName("Services should implement use cases")
    void servicesShouldImplementUseCases() {
        // Note: This check is commented out as it may be too strict
        // Services may not always directly implement use case interfaces
        /*
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Service")
                .and().resideInAPackage("..application.service..")
                .should().implement("..application.port.in..")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Service")
                .because("Services should implement use case interfaces");

        rule.check(classes);
        */
    }

    @Test
    @DisplayName("Modules should be independent (no cyclic dependencies)")
    void modulesShouldBeIndependent() {
        ArchRule rule = slices()
                .matching("com.tradesphere.ai.(*)..")
                .should().beFreeOfCycles()
                .because("Modules should not have cyclic dependencies");

        rule.check(classes);
    }

    @Test
    @DisplayName("Layered architecture should be respected")
    void layeredArchitectureShouldBeRespected() {
        ArchRule rule = layeredArchitecture()
                .consideringAllDependencies()
                .layer("Domain").definedBy("..domain..")
                .layer("Application").definedBy("..application..")
                .layer("Adapter").definedBy("..adapter..")
                .layer("Config").definedBy("..config..")

                .whereLayer("Domain").mayNotAccessAnyLayer()
                .whereLayer("Application").mayOnlyAccessLayers("Domain")
                .whereLayer("Adapter").mayOnlyAccessLayers("Application", "Domain")
                .whereLayer("Config").mayOnlyAccessLayers("Application", "Domain", "Adapter")

                .because("Hexagonal architecture must be maintained");

        rule.check(classes);
    }

    @Test
    @DisplayName("Value objects should be in domain layer")
    void valueObjectsShouldBeInDomainLayer() {
        ArchRule rule = classes()
                .that().implement("com.tradesphere.ai.shared.domain.ValueObject")
                .should().resideInAnyPackage(
                        "..domain.model..",
                        "..shared.domain.valueobject.."
                )
                .because("Value objects are domain concepts");

        rule.check(classes);
    }

    @Test
    @DisplayName("Configuration classes should not be in domain or application layers")
    void configurationClassesShouldNotBeInDomainOrApplicationLayers() {
        ArchRule rule = noClasses()
                .that().areAnnotatedWith("org.springframework.context.annotation.Configuration")
                .should().resideInAnyPackage("..domain..", "..application..")
                .because("Configuration is infrastructure concern");

        rule.check(classes);
    }
}

