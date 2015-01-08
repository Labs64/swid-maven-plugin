/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.labs64.mojo.swid;

import org.apache.commons.lang3.StringUtils;

/**
 * Represents the details of the software identifier group.
 */
public class RegId {

    private String name;
    private String unique_id;
    private String regid;
    private String tag_creator_regid;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getUnique_id() {
        if (StringUtils.isBlank(unique_id) && StringUtils.isNotBlank(name)) {
            return name;
        }
        return unique_id;
    }

    public void setUnique_id(final String unique_id) {
        this.unique_id = unique_id;
    }

    public String getRegid() {
        return regid;
    }

    public void setRegid(final String regid) {
        this.regid = regid;
    }

    public String getTag_creator_regid() {
        if (StringUtils.isBlank(tag_creator_regid) && StringUtils.isNotBlank(regid)) {
            return regid;
        }
        return tag_creator_regid;
    }

    public void setTag_creator_regid(final String tag_creator_regid) {
        this.tag_creator_regid = tag_creator_regid;
    }

}
