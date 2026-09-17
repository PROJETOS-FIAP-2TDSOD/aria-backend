package com.fiap.aria_backend.service;

import com.fiap.aria_backend.model.Idea;
import com.fiap.aria_backend.model.IdeaStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GamificationService {

    private static final int SUBMIT_IDEA = 10;
    private static final int APPROVED_IDEA = 50;
    private static final int HIGH_SCORE_BONUS = 20;
    private static final int PROJECT_CREATED = 100;
    private static final int HIGH_SCORE_THRESHOLD = 80;

    public int calculatePoints(List<Idea> ideas) {
        int submitted = ideas.size();
        long approved = ideas.stream().filter(i -> i.getStatus() == IdeaStatus.APROVADA).count();
        long highScore = ideas.stream().filter(i -> scoreOf(i) >= HIGH_SCORE_THRESHOLD).count();
        long inProject = ideas.stream().filter(i -> i.getStatus() == IdeaStatus.EM_PROJETO).count();

        return (int) (submitted * SUBMIT_IDEA
                + approved * APPROVED_IDEA
                + highScore * HIGH_SCORE_BONUS
                + inProject * PROJECT_CREATED);
    }

    public List<String> calculateBadgeIds(List<Idea> ideas) {
        List<String> badges = new ArrayList<>();

        if (!ideas.isEmpty()) {
            badges.add("first_idea");
        }
        if (ideas.size() >= 5) {
            badges.add("innovator_5");
        }
        if (ideas.stream().anyMatch(i -> i.getStatus() == IdeaStatus.APROVADA)) {
            badges.add("approved_idea");
        }
        if (ideas.stream().anyMatch(i -> scoreOf(i) >= HIGH_SCORE_THRESHOLD)) {
            badges.add("high_scorer");
        }
        if (ideas.stream().anyMatch(i -> i.getStatus() == IdeaStatus.EM_PROJETO)) {
            badges.add("project_creator");
        }

        return badges;
    }

    private int scoreOf(Idea idea) {
        return idea.getScore() != null ? idea.getScore() : 0;
    }
}