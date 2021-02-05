/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.dromara.soul.register.common.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * The type Meta data dto.
 *
 * @author xiaoyu
 */
@Data
@Builder
public class MetaDataDTO {
    
    private String appName;
    
    private String contextPath;
    
    private String path;
    
    private String pathDesc;
    
    private String rpcType;
    
    private String serviceName;
    
    private String methodName;
    
    private String ruleName;
    
    private String parameterTypes;
    
    private String rpcExt;
    
    private boolean enabled;
    
    private String host;
    
    private Integer port;
    
    private Boolean writeMetaData;
    
    private List<String> pluginNames;
}