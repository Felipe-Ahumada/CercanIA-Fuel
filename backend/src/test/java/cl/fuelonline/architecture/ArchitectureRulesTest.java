package cl.fuelonline.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

class ArchitectureRulesTest {

    static JavaClasses classes;

    @BeforeAll
    static void load() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("cl.fuelonline");
    }

    // ── Regla 1: Controllers no inyectan Repositorios directamente ────────────
    @Test
    void controllers_must_not_depend_on_repositories() {
        noClasses()
                .that().resideInAPackage("..api..")
                .should().dependOnClassesThat()
                .resideInAPackage("..domain.repository..")
                .check(classes);
    }

    // ── Regla 2: Domain no depende de Application ni Api ─────────────────────
    @Test
    void domain_must_not_depend_on_application_or_api() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..application..", "..api..")
                .check(classes);
    }

    // ── Regla 3: Entidades JPA solo en domain/model ───────────────────────────
    @Test
    void jpa_entities_must_reside_in_domain_model() {
        classes()
                .that().areAnnotatedWith("jakarta.persistence.Entity")
                .should().resideInAPackage("..domain.model..")
                .check(classes);
    }

    // ── Regla 4: transaction no importa repositorios de finance ──────────────
    @Test
    void transaction_must_not_import_finance_repositories() {
        noClasses()
                .that().resideInAPackage("cl.fuelonline.transaction..")
                .and().resideOutsideOfPackage("cl.fuelonline.transaction.domain..")
                .should().dependOnClassesThat()
                .resideInAPackage("cl.fuelonline.finance.domain.repository..")
                .check(classes);
    }

    // ── Regla 5: chat no importa repositorios de otros módulos ───────────────
    @Test
    void chat_must_not_import_foreign_repositories() {
        noClasses()
                .that().resideInAPackage("cl.fuelonline.chat..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "cl.fuelonline.user.domain.repository..",
                        "cl.fuelonline.transaction.domain.repository..")
                .check(classes);
    }

    // ── Regla 6: catalog no depende de ningún módulo de negocio ──────────────
    @Test
    void catalog_must_not_depend_on_business_modules() {
        noClasses()
                .that().resideInAPackage("cl.fuelonline.catalog..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "cl.fuelonline.user..",
                        "cl.fuelonline.finance..",
                        "cl.fuelonline.station..",
                        "cl.fuelonline.transaction..",
                        "cl.fuelonline.chat..")
                .check(classes);
    }

    // ── Regla 7: station no importa entidades de user/finance/transaction ─────
    @Test
    void station_domain_must_not_import_user_or_finance_entities() {
        noClasses()
                .that().resideInAPackage("cl.fuelonline.station.domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "cl.fuelonline.user.domain..",
                        "cl.fuelonline.finance.domain..",
                        "cl.fuelonline.transaction.domain..")
                .check(classes);
    }
}