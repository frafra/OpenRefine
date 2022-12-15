/*

Copyright 2010, Google Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

    * Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
copyright notice, this list of conditions and the following disclaimer
in the documentation and/or other materials provided with the
distribution.
    * Neither the name of Google Inc. nor the names of its
contributors may be used to endorse or promote products derived from
this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,           
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY           
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package org.openrefine.model;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import org.openrefine.model.recon.ReconConfig;
import org.openrefine.util.ParsingUtilities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Holds the metadata for a single column. Fields are immutable, copy the column with the provided methods to change its
 * fields.
 *
 */
public class ColumnMetadata implements Serializable {

    private static final long serialVersionUID = 8531948502713567634L;

    final private String _originalName;
    final private String _name;
    final private ReconConfig _reconConfig;

    @JsonCreator
    public ColumnMetadata(
            @JsonProperty("originalName") String originalName,
            @JsonProperty("name") String name,
            @JsonProperty("reconConfig") ReconConfig reconConfig) {
        _originalName = originalName;
        _name = name == null ? originalName : name;
        _reconConfig = reconConfig;
    }

    public ColumnMetadata(String name) {
        this(name, name, null);
    }

    @JsonProperty("originalName")
    public String getOriginalHeaderLabel() {
        return _originalName;
    }

    public ColumnMetadata withName(String name) {
        return new ColumnMetadata(_originalName, name, _reconConfig);
    }

    @JsonProperty("name")
    public String getName() {
        return _name;
    }

    public ColumnMetadata withReconConfig(ReconConfig config) {
        return new ColumnMetadata(_originalName, _name, config);
    }

    @JsonProperty("reconConfig")
    @JsonInclude(Include.NON_NULL)
    public ReconConfig getReconConfig() {
        return _reconConfig;
    }

    public void save(Writer writer) {
        try {
            ParsingUtilities.defaultWriter.writeValue(writer, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static public ColumnMetadata load(String s) throws Exception {
        return ParsingUtilities.mapper.readValue(s, ColumnMetadata.class);
    }

    @Override
    public String toString() {
        return String.format("[ColumnMetadata: %s, %s, %s]", _name, _originalName, _reconConfig);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof ColumnMetadata)) {
            return false;
        }
        ColumnMetadata metadata = (ColumnMetadata) other;
        return (_name.equals(metadata.getName()) &&
                _originalName.equals(metadata.getOriginalHeaderLabel()) &&
                ((_reconConfig == null && metadata.getReconConfig() == null)
                        || (_reconConfig != null && _reconConfig.equals(metadata.getReconConfig()))));
    }

    @Override
    public int hashCode() {
        return _name.hashCode() + 87 * _originalName.hashCode();
    }
}
