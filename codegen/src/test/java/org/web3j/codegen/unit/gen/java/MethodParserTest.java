/*
 * Copyright 2019 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.codegen.unit.gen.java;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.squareup.javapoet.MethodSpec;
import org.junit.jupiter.api.Test;

import org.web3j.codegen.unit.gen.MethodFilter;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MethodParserTest extends JavaTestSetup {
    @Test
    public void testThatDeployMethodWasGenerated() {

        Optional<Method> deployMethod =
                filteredMethods.stream().filter(m -> m.getName().equals("deploy")).findAny();
        MethodSpec deployMethodSpec =
                new MethodParser(deployMethod.get(), greeterContractClass, "deploy")
                        .getMethodSpec();
        assertEquals(
                "@org.junit.jupiter.api.BeforeAll\n"
                        + "static void deploy(org.web3j.protocol.Web3j web3j, org.web3j.tx.TransactionManager transactionManager, org.web3j.tx.gas.ContractGasProvider contractGasProvider) throws java.lang.Exception {\n"
                        + "  greeter = org.web3j.test.contract.Greeter.deploy(web3j, transactionManager, contractGasProvider, \"REPLACE_ME\").send();\n"
                        + "}\n",
                deployMethodSpec.toString());
    }

    @Test
    public void testThatNewGreetingMethodWasGenerated() {

        Optional<Method> deployMethod =
                filteredMethods.stream().filter(m -> m.getName().equals("newGreeting")).findAny();
        MethodSpec deployMethodSpec =
                new MethodParser(deployMethod.get(), greeterContractClass, "newGreeting")
                        .getMethodSpec();
        assertEquals(
                "org.web3j.protocol.core.methods.response.TransactionReceipt transactionReceiptVar = greeter.newGreeting(\"REPLACE_ME\").send();\n"
                        + "org.junit.jupiter.api.Assertions.assertTrue(transactionReceiptVar.isStatusOK());\n",
                deployMethodSpec.code.toString());
    }

    @Test
    public void testGeneratedDuplicateGreetingMethods() {
        List<MethodSpec> allMethodSpecs =
                MethodFilter.generateMethodSpecsForEachTest(greeterContractClass);

        // Filter all MethodSpecs for those related to "greet" methods
        List<MethodSpec> greetMethodSpecs =
                allMethodSpecs.stream()
                        .filter(methodSpec -> methodSpec.name.startsWith("greet"))
                        .collect(Collectors.toList());

        assertTrue(
                greetMethodSpecs.stream().anyMatch(methodSpec -> methodSpec.name.equals("greet")));
        assertTrue(
                greetMethodSpecs.stream().anyMatch(methodSpec -> methodSpec.name.equals("greet1")));
        assertEquals(2, greetMethodSpecs.size());
    }

    @Test
    public void testGetDeploymentBinaryMethodNotGenerated() {
        List<MethodSpec> allMethodSpecs =
                MethodFilter.generateMethodSpecsForEachTest(greeterContractClass);

        // Filter all MethodSpecs for those related to "getDeploymentBinary" method
        List<MethodSpec> getDeploymentBinaryMethodSpecs =
                allMethodSpecs.stream()
                        .filter(methodSpec -> methodSpec.name.startsWith("getDeploymentBinary"))
                        .collect(Collectors.toList());

        // Ensure no MethodSpecs were generated for getDeploymentBinary method
        assertEquals(
                0,
                getDeploymentBinaryMethodSpecs.size(),
                "MethodSpec list should not contain getDeploymentBinary method");
    }
}
