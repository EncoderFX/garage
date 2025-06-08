package com.garage.simulation;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garage.dto.GarageDto;
import com.garage.dto.SpotDto;
import com.garage.feing.Webhook;

import feign.Feign;
import feign.gson.GsonEncoder;

@Service
public class Simulator {

    private static List<String> plates = Stream.of("HRU5861", "MVK3537", "MVC3709", 
                                                    "LVJ3198", "HZX9160", "HOW3903", 
                                                    "JLS9733", "MUK1398", "JFP1187",
                                                    "NAY6415", "MWD1946", "EIS4519",
                                                    "LPV8362", "MWE8920", "JJU0575")
            .collect(Collectors.toList());

    //@Scheduled(cron = "*/10 * * * * ?")
    public static void garageEntryEvents() {
        Webhook wh = Feign.builder().encoder(new GsonEncoder())
                .target(Webhook.class, "http://localhost:3003/");
        
        plates.forEach(p->{
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("license_plate", p);
            map.put("entry_time", LocalDate.now().atTime(new Random().ints(1, 5)
                    .findFirst()
                    .getAsInt(), new Random().ints(1, 59)
                    .findFirst()
                    .getAsInt()).toString());
            map.put("event_type", "ENTRY");
            wh.sendEvent(map);
        });
        
        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/data.json");
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
            JsonNode spotsNode = root.at("/spots");
            List<SpotDto> spotsDto = mapper.convertValue(spotsNode, new TypeReference<List<SpotDto>>(){});
            
            AtomicInteger count=new AtomicInteger(0);
            spotsDto.forEach(s->{
                String p = plates.get(count.intValue());
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("license_plate", p);
                map.put("lat", s.getLat());
                map.put("lng", s.getLng());
                map.put("event_type", "PARKED");
                wh.sendEvent(map);
                count.getAndIncrement();
            });
        }catch (Exception e) {
           System.out.println(e);
        }
        
    }
    
    public static void main(String[] args) {
        garageEntryEvents();
    }
    

}
