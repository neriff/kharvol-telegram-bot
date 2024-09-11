package com.kharvoll.kharvollbot.persistence.listener;

import com.kharvoll.kharvollbot.persistence.model.BaseModel;
import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.PrePersist;

public class IdGeneratorListener {

    private static final ULID ULID_GENERATOR = new ULID();

    @PrePersist
    public void setId(BaseModel model) {
        if (model.getId() == null) {
            model.setId(ULID_GENERATOR.nextULID());
        }
    }


}
