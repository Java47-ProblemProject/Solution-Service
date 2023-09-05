package telran.solution.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import telran.solution.dto.exceptions.ExceptionDto;
import telran.solution.dto.exceptions.SolutionNotFoundException;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(SolutionNotFoundException.class)
    public ResponseEntity<Object> handleProfileExistsException(SolutionNotFoundException ex) {
        ExceptionDto exceptionDto = new ExceptionDto(HttpStatus.BAD_REQUEST.value(), "Bad Request");
        exceptionDto.setMessage("Solution is not exists.");
        exceptionDto.setPath("/problem/*");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDto);
    }
}
