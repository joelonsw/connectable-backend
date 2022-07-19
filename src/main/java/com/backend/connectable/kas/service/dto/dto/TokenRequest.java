package com.backend.connectable.kas.service.dto.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class TokenRequest {
    private String to;
    private String id;
    private String uri;

    @Builder
    public TokenRequest(String to, String id, String uri) {
        this.to = to;
        this.id = id;
        this.uri = uri;
    }
}
