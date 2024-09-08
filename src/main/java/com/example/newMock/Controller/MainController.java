package com.example.newMock.Controller;

import com.example.newMock.Model.RequestDTO;
import com.example.newMock.Model.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

@RestController
public class MainController {

    private Logger log = LoggerFactory.getLogger(MainController.class);

    ObjectMapper mapper = new ObjectMapper();

    @PostMapping(
            value = "/info/postBalances",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public Object postBalances(@RequestBody RequestDTO requestDTO) {

        try{
            String clientId = requestDTO.getClientId();
            char firstDigit = clientId.charAt(0);

            BigDecimal maxLimit;
            String RquId = requestDTO.getRqUID();
            String currency;

            if(firstDigit=='8'){
               maxLimit = new BigDecimal(2000);
                currency= "US";
            } else if(firstDigit=='9'){
                maxLimit = new BigDecimal(1000);
                currency= "EU";
            }else{
                maxLimit = new BigDecimal(50000);
                currency= "RUB";
            }

            Random random = new Random();
            BigDecimal balanceRough = maxLimit.multiply(BigDecimal.valueOf(random.nextDouble()));
            // Округляю до целого числа и наличие двух нулей после запятой(согласно примеру из задания)
            BigDecimal balance = balanceRough.setScale(0, RoundingMode.HALF_UP).setScale(2);
//          Округлю до сотых(копейки, центы, евроценты).Более реалистичный случай:).
//          BigDecimal balance = balanceRough.setScale(2, RoundingMode.HALF_UP);

            ResponseDTO responseDTO = new ResponseDTO();

            responseDTO.setRqUID(RquId);
            responseDTO.setClientId(clientId);
            responseDTO.setAccount(requestDTO.getAccount());
            responseDTO.setCurrency(currency);
            responseDTO.setBalance(balance);
            responseDTO.setMaxLimit(maxLimit);

            log.error("******* RequestDTO *******" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestDTO));
            log.error("******* ResponseDTO ******" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseDTO));

            return  responseDTO;

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
