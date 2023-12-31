package telran.solution.kafka;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import telran.solution.kafka.kafkaDataDto.solutionDataDto.SolutionServiceDataDto;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    @Setter
    private SolutionServiceDataDto solutionData;

    @Bean
    public Supplier<SolutionServiceDataDto> sendData() {
        return () -> {
            if (solutionData != null) {
                SolutionServiceDataDto sentMessage = solutionData;
                solutionData = null;
                return sentMessage;
            }
            return null;
        };
    }
}
