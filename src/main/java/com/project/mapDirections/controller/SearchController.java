package com.project.mapDirections.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLException;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.RequiredArgsConstructor;
import reactor.netty.http.client.HttpClient;


@Controller
@RequestMapping(value = "/")
@RequiredArgsConstructor
public class SearchController {

	@ResponseBody
	@PostMapping("/findPath")
	public Map<String, Object> findPath(@RequestBody Map<String, Object> param) {
		Map<String, Object> mp = new HashMap<String, Object>();
		
		String uriPath = "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving";
	      
      	//String start = "126.844856,37.5407361";
     	//String goal = "126.8980711,37.5763214";
        
		System.out.println("param : " + param);
		
		String avoidtoll = param.get("avoidtoll").toString();
        String option = avoidtoll.equals("false") ? "trafast" : "traavoidtoll";
        
        String start = param.get("start").toString();
        String goal = param.get("goal").toString();
        String wayPoints = param.get("wayPoints").toString();
        
        HttpClient httpClient = HttpClient.create().secure(t -> {
        
            try {
            
            	t.sslContext(SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build());
            
            } catch (SSLException e) {
            
                System.out.println(e);
            
            }
        });

        WebClient client = WebClient.builder()
                .baseUrl(uriPath)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();



        // X-NCP-APIGW-API-KEY-ID
        String clientId = "0gylw8lear";
        // X-NCP-APIGW-API-KEY
        String clientSecret = "IEh9Jb8bvSKZbpuTQrRe4xbqHG7VJYHBtcMjbZvo";

        String response = "";
        
        if (wayPoints.equals("")) {
        	response = client.get().uri(uriBuilder -> uriBuilder.path("")
                .queryParam("start", start)
                .queryParam("goal", goal)
                .queryParam("option",option)
                .build())
                .header("X-NCP-APIGW-API-KEY-ID",clientId)
                .header("X-NCP-APIGW-API-KEY",clientSecret)
                .retrieve() 			  // 응답을 받게 하되,
                .bodyToMono(String.class) // 응답 값을 하나만,
                .block(); 				  // 동기로 받으려 한다.
        } else {
        	response = client.get().uri(uriBuilder -> uriBuilder.path("")
                    .queryParam("start", start)
                    .queryParam("goal", goal)
                    .queryParam("waypoints", wayPoints)
                    .queryParam("option",option)
                    .build())
                    .header("X-NCP-APIGW-API-KEY-ID",clientId)
                    .header("X-NCP-APIGW-API-KEY",clientSecret)
                    .retrieve() 			  // 응답을 받게 하되,
                    .bodyToMono(String.class) // 응답 값을 하나만,
                    .block(); 				  // 동기로 받으려 한다.
        }
        

        
        
        Gson gson = new Gson();
        Map<String, Object> resData = gson.fromJson(response, new TypeToken<Map<String, Object>>(){}.getType());
        
        ObjectMapper mapper = new ObjectMapper();
        
        try {

            // convert JSON string to Map
            Map<String, Object> map = mapper.readValue(response, Map.class);

			// it works
            //Map<String, String> map = mapper.readValue(json, new TypeReference<Map<String, String>>() {});

            System.out.println(map.get("message"));
            
            for (Map.Entry<String, Object> entry : map.entrySet()) {
            	//System.out.println(entry.getKey());
            	//System.out.println(entry.getValue());
            	
            	mp.put(entry.getKey(), entry.getValue());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        
		return mp;
	}

}
