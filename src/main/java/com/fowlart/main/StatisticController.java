package com.fowlart.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fowlart.main.dto.BotVisitorDto;
import com.fowlart.main.in_mem_catalog.Item;
import com.fowlart.main.state.BotVisitorService;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/statistic")
public class StatisticController {


    private final static Logger logger = LoggerFactory.getLogger(StatisticController.class);

    private final BotVisitorService botVisitorService;

    private final String gmailAccName;

    private final String hostName;

    public StatisticController(BotVisitorService botVisitorService,
                               @Value("${app.bot.email.gmail.user}") String gmailAccName,
                               @Value("${app.bot.host.url}") String hostName) {
        this.botVisitorService = botVisitorService;
        this.gmailAccName = gmailAccName;
        this.hostName = hostName;
    }

    // root mapping
    @GetMapping("/")
    String getRoot(@RequestHeader Map<String, String> headers) {
        return getAllVisitorList(headers);
    }

    @GetMapping("/all-visitors")
    public String getAllVisitorList(@RequestHeader Map<String, String> headers) {

        var pleaseLogin = "You are not admin! Please login with Google account!" +
                "  <button onClick=\"javascript:window.location.href='hostname/.auth/login/google/callback'\">Login</button>".replaceAll("hostname", hostName);

        // check if the request is from the admin
        var googleAccessToken = headers.get("x-ms-token-google-access-token");
        logger.info("googleAccessToken: " + googleAccessToken);

        if (StringUtils.hasText(googleAccessToken)) {

            Unirest.setTimeouts(0, 0);

            try {
                var response = Unirest
                        .get("https://oauth2.googleapis.com/tokeninfo?access_token=" + googleAccessToken)
                        .asString();
                logger.info(response.getBody());
                // convert body to JSON
                var json = new ObjectMapper().readTree(response.getBody());
                var email = json.get("email");

                if (Objects.isNull(email) || !gmailAccName.equals(email.asText())) {
                    return pleaseLogin;
                }
            } catch (UnirestException | JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            return pleaseLogin;
        }

        final ObjectMapper mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);

        var visitors = botVisitorService.getAllVisitors();
        return visitors.stream().map(botVisitor -> {

            var bucketConverted = botVisitor.getBucket()
                    .stream()
                    .map(Item::name)
                    .collect(Collectors.toSet());


            try {
                return mapper.writeValueAsString(new BotVisitorDto(botVisitor.getName(),
                        bucketConverted,
                        botVisitor.getPhoneNumber(), botVisitor.getUser()
                        .getFirstName(), botVisitor.getUser().getLastName()));

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        }).reduce((s1, s2) -> s1 + "<br/>" + s2).orElse("None");
    }
}
