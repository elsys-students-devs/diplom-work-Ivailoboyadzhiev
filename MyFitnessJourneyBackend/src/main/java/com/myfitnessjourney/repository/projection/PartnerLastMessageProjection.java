package com.myfitnessjourney.repository.projection;

import java.time.LocalDateTime;

public interface PartnerLastMessageProjection {

    Long getPartnerId();

    LocalDateTime getLastMessageAt();
}
