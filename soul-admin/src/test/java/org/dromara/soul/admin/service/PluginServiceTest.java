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

package org.dromara.soul.admin.service;

import org.apache.commons.lang3.StringUtils;
import org.dromara.soul.admin.dto.BatchCommonDTO;
import org.dromara.soul.admin.dto.PluginDTO;
import org.dromara.soul.admin.entity.PluginDO;
import org.dromara.soul.admin.entity.RuleDO;
import org.dromara.soul.admin.entity.SelectorDO;
import org.dromara.soul.admin.mapper.PluginMapper;
import org.dromara.soul.admin.mapper.RuleConditionMapper;
import org.dromara.soul.admin.mapper.SelectorConditionMapper;
import org.dromara.soul.admin.mapper.SelectorMapper;
import org.dromara.soul.admin.mapper.RuleMapper;
import org.dromara.soul.admin.page.CommonPager;
import org.dromara.soul.admin.page.PageParameter;
import org.dromara.soul.admin.query.PluginQuery;
import org.dromara.soul.admin.query.SelectorQuery;
import org.dromara.soul.admin.query.SelectorConditionQuery;
import org.dromara.soul.admin.query.RuleQuery;
import org.dromara.soul.admin.query.RuleConditionQuery;
import org.dromara.soul.admin.service.impl.PluginServiceImpl;
import org.dromara.soul.admin.vo.PluginVO;
import org.dromara.soul.common.dto.PluginData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

/**
 * Test cases for PluginService.
 *
 * @author wuliendan
 *
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public final class PluginServiceTest {

    @InjectMocks
    private PluginServiceImpl pluginService;

    @Mock
    private PluginMapper pluginMapper;

    @Mock
    private SelectorMapper selectorMapper;

    @Mock
    private RuleMapper ruleMapper;

    @Mock
    private RuleConditionMapper ruleConditionMapper;

    @Mock
    private SelectorConditionMapper selectorConditionMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Before
    public void setUp() {
        pluginService = new PluginServiceImpl(pluginMapper, selectorMapper, selectorConditionMapper,
                ruleMapper, ruleConditionMapper, eventPublisher);
    }

    @Test
    public void testCreateOrUpdate() {
        publishEvent();
        testCreate();
        testUpdate();
    }

    @Test
    public void testDelete() {
        PluginDO pluginDO = buildPluginDO("123");
        when(pluginMapper.selectById("123")).thenReturn(pluginDO);
        when(pluginMapper.delete("123")).thenReturn(1);
        final List<String> ids = Collections.singletonList(pluginDO.getId());
        testSelectorDelete();
        String msg = pluginService.delete(ids);
        assertEquals(msg, StringUtils.EMPTY);
    }

    @Test
    public void testEnable() {
        publishEvent();
        BatchCommonDTO batchCommonDTO = new BatchCommonDTO();
        batchCommonDTO.setEnabled(false);
        batchCommonDTO.setIds(Collections.singletonList("123"));
        given(this.pluginMapper.updateEnable(any())).willReturn(1);
        assertThat(this.pluginService.enabled(batchCommonDTO.getIds(), batchCommonDTO.getEnabled()), equalTo(StringUtils.EMPTY));
    }

    @Test
    public void testFindById() {
        PluginDO pluginDO = buildPluginDO();
        given(this.pluginMapper.selectById(eq("123"))).willReturn(pluginDO);
        PluginVO pluginVO = this.pluginService.findById("123");
        assertNotNull(pluginVO);
        assertEquals(pluginDO.getId(), pluginVO.getId());
    }

    @Test
    public void testListByPage() {
        PageParameter pageParameter = new PageParameter();
        pageParameter.setPageSize(5);
        pageParameter.setTotalCount(10);
        pageParameter.setTotalPage(pageParameter.getTotalCount() / pageParameter.getPageSize());
        PluginQuery pluginQuery = new PluginQuery("sofa", pageParameter);
        given(this.pluginMapper.countByQuery(pluginQuery)).willReturn(10);
        List<PluginDO> pluginDOList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            PluginDO pluginDO = buildPluginDO(String.valueOf(i));
            pluginDOList.add(pluginDO);
        }
        given(this.pluginMapper.selectByQuery(pluginQuery)).willReturn(pluginDOList);
        final CommonPager<PluginVO> pluginDOCommonPager = this.pluginService.listByPage(pluginQuery);
        assertEquals(pluginDOCommonPager.getDataList().size(), pluginDOList.size());
    }

    @Test
    public void testListAll() {
        PluginDO pluginDO = buildPluginDO("123");
        List<PluginDO> pluginDOList = Collections.singletonList(pluginDO);
        given(this.pluginMapper.selectAll()).willReturn(pluginDOList);
        List<PluginData> dataList = this.pluginService.listAll();
        assertNotNull(dataList);
        assertEquals(pluginDOList.size(), dataList.size());
    }

    private void publishEvent() {
        PluginDO pluginDO = buildPluginDO();
        given(this.pluginMapper.selectById("123")).willReturn(pluginDO);
    }

    private void testCreate() {
        PluginDTO pluginDTO = buildPluginDTO("");
        when(pluginMapper.insert(any())).thenReturn(1);
        assertEquals(this.pluginService.createOrUpdate(pluginDTO), StringUtils.EMPTY);
    }

    private void testUpdate() {
        PluginDO pluginDO = buildPluginDO();
        when(pluginMapper.selectByName(any())).thenReturn(pluginDO);

        PluginDTO pluginDTO = new PluginDTO();
        pluginDTO.setId("123");
        pluginDTO.setName("test");
        when(pluginMapper.update(any())).thenReturn(1);
        assertEquals(this.pluginService.createOrUpdate(pluginDTO), StringUtils.EMPTY);
    }

    private void testSelectorDelete() {
        final List<SelectorDO> selectorDOList = new ArrayList<>();
        SelectorQuery selectorQuery = new SelectorQuery("123", null);
        when(selectorMapper.selectByQuery(selectorQuery)).thenReturn(selectorDOList);
        for (int i = 0; i < selectorDOList.size(); i++) {
            SelectorDO selectorDO = selectorDOList.get(i);
            final List<RuleDO> ruleDOList = new ArrayList<>();
            RuleQuery ruleQuery = new RuleQuery(selectorDO.getId(), null);
            when(ruleMapper.selectByQuery(ruleQuery)).thenReturn(ruleDOList);
            for (int j = 0; j < ruleDOList.size(); j++) {
                RuleDO ruleDO = ruleDOList.get(i);
                when(ruleMapper.delete(ruleDO.getId())).thenReturn(1);
                when(ruleConditionMapper.deleteByQuery(new RuleConditionQuery(ruleDO.getId()))).thenReturn(1);
            }
            when(selectorMapper.delete(selectorDO.getId())).thenReturn(1);
            when(selectorConditionMapper.deleteByQuery(new SelectorConditionQuery(selectorDO.getId()))).thenReturn(1);
        }
    }

    private PluginDTO buildPluginDTO() {
        return buildPluginDTO("123");
    }

    private PluginDTO buildPluginDTO(final String id) {
        PluginDTO pluginDTO = new PluginDTO();
        if (StringUtils.isNotBlank(id)) {
            pluginDTO.setId(id);
        }
        pluginDTO.setName("test");
        pluginDTO.setConfig("{\"protocol\":\"zookeeper\",\"register\":\"127.0.0.1:2181\"}");
        pluginDTO.setRole(0);
        pluginDTO.setEnabled(true);
        return pluginDTO;
    }

    private PluginDO buildPluginDO() {
        PluginDO pluginDO = PluginDO.buildPluginDO(buildPluginDTO());
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        pluginDO.setDateCreated(now);
        pluginDO.setDateUpdated(now);
        return pluginDO;
    }

    private PluginDO buildPluginDO(final String id) {
        PluginDTO pluginDTO = new PluginDTO();
        if (StringUtils.isNotBlank(id)) {
            pluginDTO.setId(id);
        }
        pluginDTO.setName("test");
        pluginDTO.setRole(1);
        PluginDO pluginDO = PluginDO.buildPluginDO(pluginDTO);
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        pluginDO.setDateCreated(now);
        pluginDO.setDateUpdated(now);
        return pluginDO;
    }
}
