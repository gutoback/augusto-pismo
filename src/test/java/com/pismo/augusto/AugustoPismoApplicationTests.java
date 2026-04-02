package com.pismo.augusto;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@Tag("test-integration")
@Tag("test-unit")
@TestPropertySource(locations = "classpath:application-test.properties")
class AugustoPismoApplicationTests {

    @Test
    void contextLoads() {
    }
    @Test
    void verifyApplicationModules() {
        ApplicationModules modules = ApplicationModules.of(AugustoPismoApplication.class);
        modules.verify();
    }

    @Test
    void createModuleDocumentation() {
        ApplicationModules modules = ApplicationModules.of(AugustoPismoApplication.class);
        new Documenter(modules)
                .writeDocumentation()
                .writeIndividualModulesAsPlantUml()
                .writeModuleCanvases();
    }

}
