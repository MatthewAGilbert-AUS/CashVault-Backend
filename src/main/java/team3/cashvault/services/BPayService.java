package team3.cashvault.services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;



@Service
public class BPayService {
    private static final String BPAY_API_URL = "https://sandbox.api.bpaygroup.com.au/payments/v1/biller/";

    // Method to lookup biller code
    public String lookupBillerCode(String billerCode) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        // Make a GET request to the BPay API to lookup the biller code
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
            BPAY_API_URL + billerCode, HttpMethod.GET, entity, String.class);

        // Check the response status code and return the response body
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            return null;
        }
    }

}


