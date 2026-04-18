package com.firstone.cv.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Service
public class GrokService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${xai.api.key}")
    private String xaiApiKey;

    @Value("${xai.model:grok-3-mini}")
    private String xaiModel;

    private static final String XAI_API_URL = "https://api.x.ai/v1/chat/completions";

    public String optimizeResume(String originalResume, String jobDescription) {

        String prompt = """
                You are an expert ATS Resume Slayer.
                Optimize this resume for the job description below.
                Return valid JSON only (no markdown, no extra text):
                {
                  "jobTitle": "Extracted Job Title",
                  "optimizedResume": "...",
                  "atsScore": "92%",
                  "trapsFixed": ["Fixed thing 1", "Fixed thing 2"],
                  "missingSkills": ["Python", "AWS"],
                  "roadmap": "Week 1: ...\\nWeek 2: ...\\nFree courses: ..."
                }
                Resume:
                """ + originalResume + """

                Job Description:
                """ + jobDescription;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + xaiApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "model", xaiModel,
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.7,
                "max_tokens", 4000);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(XAI_API_URL, request, Map.class);
        Map<String, Object> choice = (Map<String, Object>) ((List<?>) response.getBody().get("choices")).get(0);
        Map<String, Object> message = (Map<String, Object>) choice.get("message");
        String content = (String) message.get("content");

        return content;
    }
}
