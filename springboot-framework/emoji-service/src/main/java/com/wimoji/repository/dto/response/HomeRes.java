package com.wimoji.repository.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeRes {
    String uid;
    String eid;
    String content;
    String latitude;
    String longitude;
    String dongCode;
    double dis;
}
