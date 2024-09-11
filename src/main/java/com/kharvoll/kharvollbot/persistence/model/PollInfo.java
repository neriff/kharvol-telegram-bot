package com.kharvoll.kharvollbot.persistence.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
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
public class PollInfo extends BaseModel {

    private Integer messageId;

    private Boolean forwarded;

    private String pollConfigId;

}
