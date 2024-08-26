package com.airtribe.NewsAggregator.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.airtribe.NewsAggregator.config.AppConfig;

@Service
public class NewsService {

    @Autowired
    private AppConfig apiConfig;

    public Object fetchNews(String[] preferences) {
        RestTemplate restTemplate = new RestTemplate();
        String query = String.join(" OR ", preferences);
        String url = String.format("%s?q=%s&apiKey=%s", apiConfig.getApiUrl(), query, apiConfig.getApiKey());

        return restTemplate.getForObject(url, Object.class);
    }


}
