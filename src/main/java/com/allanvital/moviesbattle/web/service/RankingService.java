package com.allanvital.moviesbattle.web.service;

import com.allanvital.moviesbattle.web.model.Battle;
import com.allanvital.moviesbattle.web.model.Game;
import com.allanvital.moviesbattle.web.model.User;
import com.allanvital.moviesbattle.web.repository.BattleRepository;
import com.allanvital.moviesbattle.web.repository.GameRepository;
import com.allanvital.moviesbattle.web.repository.UserRepository;
import com.allanvital.moviesbattle.web.service.pojo.UserRanking;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class RankingService {

    private UserRepository userRepository;
    private GameRepository gameRepository;
    private BattleRepository battleRepository;

    public RankingService(UserRepository userRepository, GameRepository gameRepository, BattleRepository battleRepository) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.battleRepository = battleRepository;
    }

    public List<UserRanking> getRanking() {
        List<UserRanking> userRankings = new LinkedList<>();
        Iterable<User> users = userRepository.findAll();
        for(User user : users) {
            List<Game> games = gameRepository.findByPlayer(user.getUsername());
            int totalBattles = 0;
            int correctAnswers = 0;
            for(Game game : games) {
                List<Battle> battles = battleRepository.findByGame(game);
                totalBattles += battles.size();
                for(Battle battle : battles) {
                    if(battle.playerAnsweredCorrectly()) {
                        correctAnswers++;
                    }
                }
            }
            if(totalBattles == 0) {
                totalBattles = 1;
            }
            double accuracy = correctAnswers * 100.0 / totalBattles;
            double scoreboard = totalBattles * accuracy;
            userRankings.add(new UserRanking(user.getUsername(), scoreboard));
        }
        userRankings.sort(Collections.reverseOrder());
        return userRankings;
    }

}
