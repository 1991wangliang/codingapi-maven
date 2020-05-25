package com.codingapi.maven.uml.define;

import com.codingapi.maven.uml.annotation.ValueObjectModel;
import lombok.Data;

import java.util.Objects;

@ValueObjectModel(title = "关系定义")
@Data
public class RelationDefinition {
    private String relLeft;
    private String rel;
    private String relRight;

    public static RelationDefinition of(String left, String rel, String right) {
        RelationDefinition relationDefinition = new RelationDefinition();
        relationDefinition.setRel(rel);
        relationDefinition.setRelLeft(left);
        relationDefinition.setRelRight(right);
        return relationDefinition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelationDefinition that = (RelationDefinition) o;
        return Objects.equals(relLeft, that.relLeft) &&
                Objects.equals(rel, that.rel) &&
                Objects.equals(relRight, that.relRight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relLeft, rel, relRight);
    }
}
