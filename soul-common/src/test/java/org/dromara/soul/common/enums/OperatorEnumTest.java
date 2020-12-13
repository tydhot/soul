/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.soul.common.enums;

import org.dromara.soul.common.exception.SoulException;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

/**
 * Test Cases for OperatorEnum.
 *
 * @author mcnultyboy
 */
public final class OperatorEnumTest {

    /**
     * test acquireSupport method.
     */
    @Test
    public void testAcquireSupport() {
        List<OperatorEnum> enums = OperatorEnum.acquireSupport();
        assertTrue(enums.contains(OperatorEnum.MATCH));
        assertTrue(enums.contains(OperatorEnum.EQ));
        assertTrue(enums.contains(OperatorEnum.REGEX));
        assertTrue(enums.contains(OperatorEnum.LIKE));
        assertFalse(enums.contains(OperatorEnum.GT));
        assertFalse(enums.contains(OperatorEnum.LT));
    }

    /**
     * test getOperatorEnumByAlias method.
     */
    @Test
    public void testAcquireByNameInvalid() {
        assertEquals(OperatorEnum.MATCH, OperatorEnum.getOperatorEnumByAlias("match"));
        assertEquals(OperatorEnum.EQ, OperatorEnum.getOperatorEnumByAlias("="));
        assertEquals(OperatorEnum.REGEX, OperatorEnum.getOperatorEnumByAlias("regEx"));
        assertEquals(OperatorEnum.LIKE, OperatorEnum.getOperatorEnumByAlias("like"));
    }

    /**
     * test getOperatorEnumByAlias method with GT exception.
     */
    @Test(expected = SoulException.class)
    public void testAcquireByNameInvalidWithGTException() {
        OperatorEnum.getOperatorEnumByAlias(">");
    }

    /**
     * test getOperatorEnumByAlias method with LT exception.
     */
    @Test(expected = SoulException.class)
    public void testAcquireByNameInvalidWithLTException() {
        OperatorEnum.getOperatorEnumByAlias("<");
    }

    /**
     * test getOperatorEnumByAlias method with misspelling exception.
     */
    @Test(expected = SoulException.class)
    public void testAcquireByNameInvalidWithMisspellingException() {
        OperatorEnum.getOperatorEnumByAlias("nike");
    }
}
