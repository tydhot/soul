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

package org.dromara.soul.admin.config;

import com.alibaba.fastjson.JSONObject;
import org.dromara.soul.admin.AbstractConfigurationTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Test cases for WebConfig.
 *
 * @author Yejiajun
 */
public final class WebConfigTest extends AbstractConfigurationTest {

    @Test
    public void testAddCorsMappings() throws Exception {
        CorsRegistry registry = new CorsRegistry();
        WebConfig webConfig = new WebConfig();
        webConfig.addCorsMappings(registry);
        Assert.assertEquals(getCorsConfigurationsString(registry), getCorsConfigurationsString(corsRegistryJSONStringExtendBuild()));
    }

    private CorsRegistry corsRegistryJSONStringExtendBuild() {
        CorsRegistry registry = new CorsRegistry();
        registry.addMapping("/**")
                .allowedHeaders("Access-Control-Allow-Origin",
                        "*",
                        "Access-Control-Allow-Methods",
                        "POST, GET, OPTIONS, PUT, DELETE",
                        "Access-Control-Allow-Headers",
                        "Origin, X-Requested-With, Content-Type, Accept")
                .allowedOrigins("*")
                .allowedMethods("*");
        return registry;
    }

    private String getCorsConfigurationsString(final CorsRegistry registry) throws Exception {
        Class registryClass = registry.getClass();
        Method getCorsConfigurationsMethod = registryClass.getDeclaredMethod("getCorsConfigurations");
        getCorsConfigurationsMethod.setAccessible(true);
        Map invokeResult = (Map) getCorsConfigurationsMethod.invoke(registry);
        return JSONObject.toJSONString(invokeResult);
    }
}
