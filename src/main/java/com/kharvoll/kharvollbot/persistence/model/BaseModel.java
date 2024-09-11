package com.kharvoll.kharvollbot.persistence.model;

import com.kharvoll.kharvollbot.persistence.listener.IdGeneratorListener;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@EntityListeners({IdGeneratorListener.class})

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public abstract class BaseModel {

    @Id
    private String id;

}
