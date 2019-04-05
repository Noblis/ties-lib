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

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"assertionId", "assertionReferenceId", "assertionReferenceIdLabel", "system", "informationType", "sha256DataHash", "dataSize", "dataRelativeUri", "securityTag"})
public class SupplementalDescriptionDataFile extends SupplementalDescription {
    @JsonProperty("sha256DataHash")
    private String sha256DataHash;
    @JsonProperty("dataSize")
    private Long dataSize;
    @JsonProperty("dataRelativeUri")
    private String dataRelativeUri;

    @java.lang.SuppressWarnings("all")
    public SupplementalDescriptionDataFile() {
    }

    @java.lang.SuppressWarnings("all")
    public SupplementalDescriptionDataFile(final String sha256DataHash, final Long dataSize, final String dataRelativeUri) {
        this.sha256DataHash = sha256DataHash;
        this.dataSize = dataSize;
        this.dataRelativeUri = dataRelativeUri;
    }

    @java.lang.SuppressWarnings("all")
    public String getSha256DataHash() {
        return this.sha256DataHash;
    }

    @java.lang.SuppressWarnings("all")
    public Long getDataSize() {
        return this.dataSize;
    }

    @java.lang.SuppressWarnings("all")
    public String getDataRelativeUri() {
        return this.dataRelativeUri;
    }

    @java.lang.SuppressWarnings("all")
    public void setSha256DataHash(final String sha256DataHash) {
        this.sha256DataHash = sha256DataHash;
    }

    @java.lang.SuppressWarnings("all")
    public void setDataSize(final Long dataSize) {
        this.dataSize = dataSize;
    }

    @java.lang.SuppressWarnings("all")
    public void setDataRelativeUri(final String dataRelativeUri) {
        this.dataRelativeUri = dataRelativeUri;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof SupplementalDescriptionDataFile)) return false;
        final SupplementalDescriptionDataFile other = (SupplementalDescriptionDataFile) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        if (!super.equals(o)) return false;
        final java.lang.Object this$sha256DataHash = this.getSha256DataHash();
        final java.lang.Object other$sha256DataHash = other.getSha256DataHash();
        if (this$sha256DataHash == null ? other$sha256DataHash != null : !this$sha256DataHash.equals(other$sha256DataHash)) return false;
        final java.lang.Object this$dataSize = this.getDataSize();
        final java.lang.Object other$dataSize = other.getDataSize();
        if (this$dataSize == null ? other$dataSize != null : !this$dataSize.equals(other$dataSize)) return false;
        final java.lang.Object this$dataRelativeUri = this.getDataRelativeUri();
        final java.lang.Object other$dataRelativeUri = other.getDataRelativeUri();
        if (this$dataRelativeUri == null ? other$dataRelativeUri != null : !this$dataRelativeUri.equals(other$dataRelativeUri)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof SupplementalDescriptionDataFile;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final java.lang.Object $sha256DataHash = this.getSha256DataHash();
        result = result * PRIME + ($sha256DataHash == null ? 43 : $sha256DataHash.hashCode());
        final java.lang.Object $dataSize = this.getDataSize();
        result = result * PRIME + ($dataSize == null ? 43 : $dataSize.hashCode());
        final java.lang.Object $dataRelativeUri = this.getDataRelativeUri();
        result = result * PRIME + ($dataRelativeUri == null ? 43 : $dataRelativeUri.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "SupplementalDescriptionDataFile(sha256DataHash=" + this.getSha256DataHash() + ", dataSize=" + this.getDataSize() + ", dataRelativeUri=" + this.getDataRelativeUri() + ")";
    }
}
