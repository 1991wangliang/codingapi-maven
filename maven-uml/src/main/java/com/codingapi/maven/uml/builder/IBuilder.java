package com.codingapi.maven.uml.builder;


import com.codingapi.maven.uml.define.ModelDefinition;

import java.io.IOException;

public interface IBuilder {

    void appendModel(ModelDefinition modelDefinition) throws IOException;

}
