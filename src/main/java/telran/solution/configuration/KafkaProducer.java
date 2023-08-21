package telran.solution.configuration;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import telran.solution.dto.accounting.ProfileDto;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final StreamBridge streamBridge;
    @Setter
    private ProfileDto profile;
    @Setter
    private String solutionIdToProblem;

    @Bean
    public Supplier<ProfileDto> sendUpdatedProfile() {
        return () -> {
            if (profile != null) {
                streamBridge.send("sendUpdatedProfile-out-0", profile);
                ProfileDto sentMessage = profile;
                profile = null;
                return sentMessage;
            }
            return null;
        };
    }

    @Bean
    public Supplier<String> sendSolutionIdToProblem() {
        return () -> {
            if (solutionIdToProblem != null) {
                streamBridge.send("sendSolutionIdToProblem-out-0", solutionIdToProblem);
                String sentMessage = solutionIdToProblem;
                solutionIdToProblem = null;
                return sentMessage;
            }
            return null;
        };
    }


}
