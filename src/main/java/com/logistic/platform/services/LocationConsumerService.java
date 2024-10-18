package com.logistic.platform.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.logistic.platform.models.Location;

@Service
public class LocationConsumerService {

     private static final int BATCH_SIZE = 100;
    private static final long MAX_WAIT_TIME_MS = 5000;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private List<Location> locationBuffer = new ArrayList<>();
    private long lastProcessTime = System.currentTimeMillis();

    @KafkaListener(topics = "driver-locations", groupId = "location-tracking-group")
    public void consumeLocationUpdate(List<ConsumerRecord<String, Location>> records) {
        for (ConsumerRecord<String, Location> record : records) {
            locationBuffer.add(record.value());
        }

        if (locationBuffer.size() >= BATCH_SIZE || 
            System.currentTimeMillis() - lastProcessTime > MAX_WAIT_TIME_MS) {
            processBatch();
        }
    }

    private void processBatch() {
        if (locationBuffer.isEmpty()) {
            return;
        }

        Map<String, String> updates = new HashMap<>();
        for (Location location : locationBuffer) {
            String key = "driver:" + location.getDriverId() + ":location";
            String value = location.getLatitude() + "," + location.getLongitude() + "," + location.getTimestamp();
            updates.put(key, value);
        }

        redisTemplate.opsForValue().multiSet(updates);

        locationBuffer.clear();
        lastProcessTime = System.currentTimeMillis();
    }
}
