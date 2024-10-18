package com.logistic.platform.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.logistic.platform.models.Location;


@Service
public class KafkaService {

    // @Autowired
    // private KafkaTemplate<String,List<Double>>kafkaTemplate;

    // public boolean updateLocation(List<Double> location)
    // {

    //         this.kafkaTemplate.send(com.logistic.platform.Helper.AppConstants.LOCATION_TOPIC_NAME,location);
    //         return true;
    // }
         @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public Location getDriverLocation(String driverId) {
        String key = "driver:" + driverId + ":location";
        String value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            String[] parts = value.split(",");
            return new Location(driverId, Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Long.parseLong(parts[2]));
        }
        return null;
    }
}
