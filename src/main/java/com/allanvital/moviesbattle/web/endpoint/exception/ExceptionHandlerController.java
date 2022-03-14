package com.allanvital.moviesbattle.web.endpoint.exception;

import com.allanvital.moviesbattle.web.endpoint.resource.ExceptionResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { GameIsClosedException.class })
    public ResponseEntity<Object> handleGameIsClosed(GameIsClosedException exception) {
        String response = exception.getMessage();
        return new ResponseEntity<>(new ExceptionResource(response), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { GameNotFoundException.class })
    public ResponseEntity<Object> handleGameNotFound(GameNotFoundException exception) {
        String response = exception.getMessage();
        return new ResponseEntity<>(new ExceptionResource(response), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { UserNotOwnerOfGameException.class })
    public ResponseEntity<Object> handleUserNowOwnerOfGame(UserNotOwnerOfGameException exception) {
        String response = exception.getMessage();
        return new ResponseEntity<>(new ExceptionResource(response), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { InvalidIdAnswerException.class })
    public ResponseEntity<Object> handleInvalidIdAnswerException(InvalidIdAnswerException exception) {
        String response = exception.getMessage();
        return new ResponseEntity<>(new ExceptionResource(response), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { BattleNotFoundException.class })
    public ResponseEntity<Object> handleBattleNotFound(BattleNotFoundException exception) {
        String response = exception.getMessage();
        return new ResponseEntity<>(new ExceptionResource(response), HttpStatus.NOT_FOUND);
    }

}
