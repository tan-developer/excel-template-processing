package com.excel.poiAndJxls.intergration.dto;

import com.excel.poiAndJxls.constant.ApiPath;
import com.excel.poiAndJxls.dto.ChartQueryResponse;
import com.excel.poiAndJxls.exception.NotFoundException;
import com.excel.poiAndJxls.exception.UnauthorizeException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class SuperSetRest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HttpServletRequest request;


    public ChartQueryResponse getCharById (String chartId) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizeException("NO AUTH HEADER 😡");
        }
        Map<String , Object> params = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(authorizationHeader.substring(7));

        HttpEntity<String> entity = new HttpEntity(params, headers);
        ResponseEntity<ChartQueryResponse> rest = null;
        try {
            rest = restTemplate.exchange(ApiPath.GET_DATA_BY_CHART_ID , HttpMethod.GET , entity , ChartQueryResponse.class , chartId);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatusCode.valueOf(401)) {
                throw new UnauthorizeException("Lew Lew");
            }
        }

        if (rest.hasBody()) {
            return rest.getBody();
        }

        throw new NotFoundException("ERROR WHILE CALLING SUPERSET");
    }
}
