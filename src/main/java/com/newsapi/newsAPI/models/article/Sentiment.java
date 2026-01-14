package com.newsapi.newsAPI.models.article;

import org.springframework.data.mongodb.core.mapping.Field;

 class Item {
    private String label;
    @Field("score")
    private double score;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}

public class Sentiment {
    private String predicted_label;
    private Item[] probabilities;

    public String getPredicted_label() {
        return predicted_label;
    }

    public void setPredicted_label(String predicted_label) {
        this.predicted_label = predicted_label;
    }

    public Item[] getProbabilities() {
        return probabilities;
    }
}