package com.airtribe.NewsAggregator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.airtribe.NewsAggregator.service.impl.NewsService;

@RestController
public class NewsController {
	
	@Autowired
    private NewsService newsService;

    @GetMapping("/api/news")
    public Object getNews(@RequestParam(value = "preferences", defaultValue = "") String[] preferences) {
        return newsService.fetchNews(preferences);
    }
}