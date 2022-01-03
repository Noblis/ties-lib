/*
 * Copyright 2019 Noblis, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.noblis.ties.data_binding;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "key",
        "value"
})
public class OtherInformation {

    @JsonProperty("key")
    private String key;

    @JsonProperty("value")
    private Object value;

    /**
     * @return
     */
    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    /**
     * @param key
     */
    @JsonProperty("key")
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return
     */
    @JsonProperty("value")
    public Object getValue() {
        return value;
    }

    /**
     * @param value
     */
    @JsonProperty("value")
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * @param value
     */
    @JsonIgnore
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @param value
     */
    @JsonIgnore
    public void setValue(Integer value) {
        this.value = value;
    }

    /**
     * @param value
     */
    @JsonIgnore
    public void setValue(Float value) {
        this.value = value;
    }

    /**
     * @param value
     */
    @JsonIgnore
    public void setValue(Double value) {
        this.value = value;
    }

    /**
     * @param value
     */
    @JsonIgnore
    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(key)
                .append(value)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof OtherInformation) == false) {
            return false;
        }
        OtherInformation rhs = ((OtherInformation) other);
        return new EqualsBuilder()
                .append(key, rhs.key)
                .append(value, rhs.value)
                .isEquals();
    }
}
