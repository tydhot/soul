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

package org.dromara.soul.plugin.ratelimiter.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Collections;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.dromara.soul.common.dto.PluginData;
import org.dromara.soul.common.enums.PluginEnum;
import org.dromara.soul.common.utils.GsonUtils;
import org.dromara.soul.plugin.base.utils.Singleton;
import org.dromara.soul.plugin.ratelimiter.config.RateLimiterConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

import com.google.common.collect.Sets;

/**
 * RateLimiterPluginDataHandler test.
 *
 * @author wyc192273
 */
@RunWith(MockitoJUnitRunner.class)
public final class RateLimiterPluginDataHandlerTest {

    private static final String LOCALHOST = "localhost";

    private static final String PASSWORD_TEST_VALUE = "password";

    private static final String MASTER_TEST_VALUE = "master";

    private static final int DATABASE_TEST_VALUE = 1;

    private static final int PORT_TEST_VALUE_1 = 2181;

    private static final int PORT_TEST_VALUE_2 = 2182;

    private static final int DEFAULT_MAX_IDLE = 8;

    private static final int DEFAULT_MAX_ACTIVE = 8;

    private static final int DEFAULT_MIN_IDLE = 0;

    private RateLimiterPluginDataHandler rateLimiterPluginDataHandler;

    @Before
    public void setUp() {
        this.rateLimiterPluginDataHandler = new RateLimiterPluginDataHandler();
    }

    /**
     * handlerPlugin Singleton.INST init test case.
     */
    @Test
    public void handlerPluginTest() {
        RateLimiterConfig rateLimiterConfig = generateRateLimiterConfig(generateDefaultUrl());
        PluginData pluginData = new PluginData();
        pluginData.setEnabled(true);
        pluginData.setConfig(GsonUtils.getInstance().toJson(rateLimiterConfig));
        rateLimiterPluginDataHandler.handlerPlugin(pluginData);
        Assert.assertEquals(rateLimiterConfig, Singleton.INST.get(RateLimiterConfig.class));
        Assert.assertNotNull(Singleton.INST.get(ReactiveRedisTemplate.class));
    }

    /**
     * parts parse result null test case.
     */
    @Test(expected = Exception.class)
    public void redisStandaloneConfigurationErrorTest()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = getRedisStandaloneConfigurationMethod();
        RateLimiterConfig rateLimiterConfig = new RateLimiterConfig();
        method.invoke(rateLimiterPluginDataHandler, rateLimiterConfig);
    }

    /**
     * redisStandaloneConfiguration property test case.
     */
    @Test
    public void redisStandaloneConfigurationPropertiesTest()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = getRedisStandaloneConfigurationMethod();
        RedisStandaloneConfiguration configuration = (RedisStandaloneConfiguration) method.invoke(rateLimiterPluginDataHandler,
                generateRateLimiterConfig(generateDefaultUrl()));
        Assert.assertEquals(DATABASE_TEST_VALUE, configuration.getDatabase());
        Assert.assertEquals(LOCALHOST, configuration.getHostName());
        Assert.assertEquals(PORT_TEST_VALUE_1, configuration.getPort());
        Assert.assertEquals(RedisPassword.of(PASSWORD_TEST_VALUE), configuration.getPassword());
    }

    /**
     * redisStandaloneConfiguration property test case.
     */
    @Test
    public void redisRedisClusterConfigurationPropertiesTest()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String url = "localhost:2181;localhost:2182";

        Method method = getRedisClusterConfigurationMethod();
        RedisClusterConfiguration configuration = (RedisClusterConfiguration) method.invoke(rateLimiterPluginDataHandler,
                generateRateLimiterConfig(url));
        Assert.assertEquals(RedisPassword.of(PASSWORD_TEST_VALUE), configuration.getPassword());
        Assert.assertEquals(Collections.unmodifiableSet(Sets.newHashSet(generateRedisNode(PORT_TEST_VALUE_1),
                generateRedisNode(PORT_TEST_VALUE_2))), configuration.getClusterNodes());
    }

    /**
     * genericObjectPoolConfig property test case.
     */
    @Test
    public void getPoolConfigPropertyTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Duration duration = Duration.ofHours(1);
        Method method = getGetPoolConfigMethod();
        RateLimiterConfig rateLimiterConfig = new RateLimiterConfig();
        rateLimiterConfig.setMaxWait(duration);
        GenericObjectPoolConfig poolConfig = (GenericObjectPoolConfig) method.invoke(rateLimiterPluginDataHandler,
                rateLimiterConfig);
        Assert.assertEquals(DEFAULT_MAX_IDLE, poolConfig.getMaxIdle());
        Assert.assertEquals(DEFAULT_MAX_ACTIVE, poolConfig.getMaxTotal());
        Assert.assertEquals(DEFAULT_MIN_IDLE, poolConfig.getMinIdle());
        Assert.assertEquals(duration.toMillis(), poolConfig.getMaxWaitMillis());
    }

    /**
     * redisSentinelConfiguration property test case.
     */
    @Test
    public void redisSentinelConfigurationPropertyTest()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String url = "localhost:2181;localhost:2182";
        Method method = getRedisSentinelConfigurationMethod();
        RedisSentinelConfiguration configuration = (RedisSentinelConfiguration) method.invoke(rateLimiterPluginDataHandler,
                generateRateLimiterConfig(url));
        Assert.assertEquals(DATABASE_TEST_VALUE, configuration.getDatabase());
        Assert.assertEquals(RedisPassword.of(PASSWORD_TEST_VALUE), configuration.getPassword());
        Assert.assertEquals(Collections.unmodifiableSet(Sets.newHashSet(generateRedisNode(PORT_TEST_VALUE_1),
                generateRedisNode(PORT_TEST_VALUE_2))), configuration.getSentinels());
    }

    /**
     * pluginNamed test.
     */
    @Test
    public void pluginNamedTest() {
        Assert.assertEquals(PluginEnum.RATE_LIMITER.getName(), rateLimiterPluginDataHandler.pluginNamed());
    }

    /**
     * protected method redisStandaloneConfiguration  get.
     */
    private Method getRedisStandaloneConfigurationMethod() throws NoSuchMethodException {
        Method method = RateLimiterPluginDataHandler.class.getDeclaredMethod("redisStandaloneConfiguration",
                RateLimiterConfig.class);
        method.setAccessible(true);
        return method;
    }

    /**
     * private method redisClusterConfiguration  get.
     */
    private Method getRedisClusterConfigurationMethod() throws NoSuchMethodException {
        Method method = RateLimiterPluginDataHandler.class.getDeclaredMethod("redisClusterConfiguration",
                RateLimiterConfig.class);
        method.setAccessible(true);
        return method;
    }

    /**
     * private method getPoolConfig  get.
     */
    private Method getGetPoolConfigMethod() throws NoSuchMethodException {
        Method method = RateLimiterPluginDataHandler.class.getDeclaredMethod("getPoolConfig",
                RateLimiterConfig.class);
        method.setAccessible(true);
        return method;
    }

    /**
     * private method redisSentinelConfiguration  get.
     */
    private Method getRedisSentinelConfigurationMethod() throws NoSuchMethodException {
        Method method = RateLimiterPluginDataHandler.class.getDeclaredMethod("redisSentinelConfiguration",
                RateLimiterConfig.class);
        method.setAccessible(true);
        return method;
    }

    /**
     * url generate by host and port.
     */
    private String generateDefaultUrl() {
        return LOCALHOST + ":" + PORT_TEST_VALUE_1;
    }

    /**
     * generate redisNode.
     */
    private RedisNode generateRedisNode(final int port) {
        return new RedisNode(LOCALHOST, port);
    }

    /**
     * generate RateLimiterConfig.
     */
    private RateLimiterConfig generateRateLimiterConfig(final String url) {
        RateLimiterConfig rateLimiterConfig = new RateLimiterConfig();
        rateLimiterConfig.setDatabase(DATABASE_TEST_VALUE);
        rateLimiterConfig.setUrl(url);
        rateLimiterConfig.setMaster(MASTER_TEST_VALUE);
        rateLimiterConfig.setPassword(PASSWORD_TEST_VALUE);
        return rateLimiterConfig;
    }
}
