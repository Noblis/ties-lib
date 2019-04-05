// Generated by delombok at Tue Apr 30 16:25:30 EDT 2019
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"annotations", "supplementalDescriptions"})
public class Assertions {
    @JsonProperty("annotations")
    private List<Annotation> annotations = null;
    @JsonProperty("supplementalDescriptions")
    private List<SupplementalDescription> supplementalDescriptions = null;

    @java.lang.SuppressWarnings("all")
    public Assertions() {
    }

    @java.lang.SuppressWarnings("all")
    public Assertions(final List<Annotation> annotations, final List<SupplementalDescription> supplementalDescriptions) {
        this.annotations = annotations;
        this.supplementalDescriptions = supplementalDescriptions;
    }

    @java.lang.SuppressWarnings("all")
    public List<Annotation> getAnnotations() {
        return this.annotations;
    }

    @java.lang.SuppressWarnings("all")
    public List<SupplementalDescription> getSupplementalDescriptions() {
        return this.supplementalDescriptions;
    }

    @java.lang.SuppressWarnings("all")
    public void setAnnotations(final List<Annotation> annotations) {
        this.annotations = annotations;
    }

    @java.lang.SuppressWarnings("all")
    public void setSupplementalDescriptions(final List<SupplementalDescription> supplementalDescriptions) {
        this.supplementalDescriptions = supplementalDescriptions;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof Assertions)) return false;
        final Assertions other = (Assertions) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$annotations = this.getAnnotations();
        final java.lang.Object other$annotations = other.getAnnotations();
        if (this$annotations == null ? other$annotations != null : !this$annotations.equals(other$annotations)) return false;
        final java.lang.Object this$supplementalDescriptions = this.getSupplementalDescriptions();
        final java.lang.Object other$supplementalDescriptions = other.getSupplementalDescriptions();
        if (this$supplementalDescriptions == null ? other$supplementalDescriptions != null : !this$supplementalDescriptions.equals(other$supplementalDescriptions)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof Assertions;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $annotations = this.getAnnotations();
        result = result * PRIME + ($annotations == null ? 43 : $annotations.hashCode());
        final java.lang.Object $supplementalDescriptions = this.getSupplementalDescriptions();
        result = result * PRIME + ($supplementalDescriptions == null ? 43 : $supplementalDescriptions.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "Assertions(annotations=" + this.getAnnotations() + ", supplementalDescriptions=" + this.getSupplementalDescriptions() + ")";
    }
}