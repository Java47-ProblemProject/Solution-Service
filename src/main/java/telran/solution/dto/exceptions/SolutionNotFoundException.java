package telran.solution.dto.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
@ResponseStatus(HttpStatus.NOT_FOUND)
public class SolutionNotFoundException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = -6695760736015419739L;

}
