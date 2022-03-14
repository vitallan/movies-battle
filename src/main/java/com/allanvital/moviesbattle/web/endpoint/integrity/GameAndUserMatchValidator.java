package com.allanvital.moviesbattle.web.endpoint.integrity;

import com.allanvital.moviesbattle.web.endpoint.exception.GameNotFoundException;
import com.allanvital.moviesbattle.web.endpoint.exception.UserNotOwnerOfGameException;
import com.allanvital.moviesbattle.web.model.Game;
import com.allanvital.moviesbattle.web.repository.GameRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GameAndUserMatchValidator { //fixme find less intrusive way to validate in BattleEndpoint

    private GameRepository gameRepository;

    public GameAndUserMatchValidator(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public void validateGameIsFromUser(Integer gameId, Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        Optional<Game> game = gameRepository.findById(gameId);
        if(game.isEmpty()) {
            throw new GameNotFoundException("This game does not exist");
        }
        if(!game.get().getPlayer().equals(user.getUsername())) {
            throw new UserNotOwnerOfGameException("This game does not belong to this user");
        }
    }

}
