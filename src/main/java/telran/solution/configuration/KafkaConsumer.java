package telran.solution.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import telran.solution.dto.accounting.ProfileDto;
import telran.solution.dto.problems.ProblemDto;

import java.util.function.Consumer;

@Getter
@Configuration
public class KafkaConsumer {
    @Setter
    ProfileDto profile;
    ProblemDto problem;

    @Bean
    protected Consumer<ProfileDto> receiveProfile() {
        return data -> {
            System.out.println("received profile :" + profile);
            this.profile = data;
        };
    }

    @Bean
    protected Consumer<ProblemDto> receiveProblem() {
        return data -> {
            System.out.println(" Problem : " + data);
            this.problem = data;
        };
    }
}
