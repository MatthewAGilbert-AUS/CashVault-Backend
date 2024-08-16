package team3.cashvault.services;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CurrencyConversionService {

    private final String apiUrl = "https://api.freecurrencyapi.com/v1/latest?apikey=fca_live_b6unduOpp8bzBDL0srDvBROR3gt18FAQLPatyxT5";


   // Method to fetch exchange rates from the API
   public Map<String, BigDecimal> fetchExchangeRates() throws IOException {
    URL url = new URL(apiUrl);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");

    int responseCode = connection.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_OK) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseJson = objectMapper.readTree(connection.getInputStream());
        JsonNode dataNode = responseJson.get("data");

        // Iterate over the data node to extract exchange rates
        Map<String, BigDecimal> exchangeRates = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = dataNode.fields();
        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = fieldsIterator.next();
            String currency = entry.getKey();
            BigDecimal rate = entry.getValue().decimalValue();
            exchangeRates.put(currency, rate);
        }
        return exchangeRates;
    } else {
        throw new IOException("Failed to fetch exchange rates. Response code: " + responseCode);
    }
}

// Method to convert currencies and deduct commission
public BigDecimal convertCurrency(String sourceCurrency, String targetCurrency, BigDecimal amount)throws IOException {
    Map<String, BigDecimal> exchangeRates = fetchExchangeRates();
    BigDecimal sourceRate= exchangeRates.get(sourceCurrency);
    BigDecimal exchangeRate = exchangeRates.get(targetCurrency);
    if (exchangeRate == null) {
        throw new IllegalArgumentException("Invalid target currency");
    }
   // Convert the amount from target into AUD for return
   BigDecimal convertedAmount = amount.divide(exchangeRate, 8, RoundingMode.HALF_UP);
   convertedAmount = convertedAmount.multiply(sourceRate);

    // Deduct 1% commission
    BigDecimal commission = convertedAmount.multiply(BigDecimal.valueOf(0.01));
    return convertedAmount.add(commission);
}
}