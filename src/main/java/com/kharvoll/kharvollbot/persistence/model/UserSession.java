package com.kharvoll.kharvollbot.persistence.model;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class UserSession extends BaseModel {

    private Long userId;

    @Builder.Default
    private ObjectNode sessionParameters = JsonNodeFactory.instance.objectNode();


}
