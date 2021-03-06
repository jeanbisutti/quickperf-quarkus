/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2020-2020 the original author or authors.
 */

package org.quickperf.quarkus.quarkustest.controller;

import io.quarkus.test.junit.QuarkusTestExtension;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.*;
import org.quickperf.SystemProperties;
import org.quickperf.annotation.DisableGlobalAnnotations;
import org.quickperf.junit5.QuickPerfTestExtension;
import org.quickperf.jvm.allocation.AllocationUnit;
import org.quickperf.jvm.annotations.HeapSize;
import org.quickperf.sql.annotation.ExpectSelect;

import static io.restassured.RestAssured.given;

@HeapSize(value = 64, unit = AllocationUnit.MEGA_BYTE) // we need this to force the test to be executed in a new JVM
public class PlayerControllerTest {
    @RegisterExtension @Order(1) // quickperf extension needs to be first
    QuickPerfTestExtension quickPerfTestExtension = new QuickPerfTestExtension();

    @RegisterExtension @Order(2) // quarkus extension second, and needs to be static as it starts the application on test instance creation
    static QuarkusTestExtension quarkusTestExtension = buildQuarkusExtensionInNewJVM();

    static private QuarkusTestExtension buildQuarkusExtensionInNewJVM() {
        if(SystemProperties.TEST_CODE_EXECUTING_IN_NEW_JVM.evaluate()) {
            return new QuarkusTestExtension();
        }
        return new QuarkusTestExtensionDoingNothing(); // Extension doing nothing
    }


    @DisableGlobalAnnotations // Global annotations are used in the PlayerServiceTest
                              // They are disabled here to use explicit annotations instead
    @ExpectSelect(1)
    @Test
    public void should_find_all_players() {
        given()
                .when().get("/players")
                .then()
                .statusCode(200)
                .body("size()", CoreMatchers.is(2));
    }

}