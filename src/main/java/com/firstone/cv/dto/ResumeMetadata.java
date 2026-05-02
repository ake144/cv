package com.firstone.cv.dto;

import java.time.LocalDateTime;

public interface ResumeMetadata {
    Long getId();

    String getJobTitle();

    String getJobUrl();

    String getAtsScore();

    LocalDateTime getCreatedAt();
}
