package com.example.demo.Service.buildWorkerService;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.worker.enabled", havingValue = "true")
public class workerService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(workerService.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private BuildService buildService;

    @Override
    public void run(String... args) throws Exception {

        logger.info("Worker service started - listening for build jobs on 'build-queue'");

        while (true) {

            String id = redisTemplate.opsForList().rightPop("build-queue" ,5, TimeUnit.SECONDS);
            //waits 5sec for the new job
            
            if (id != null) {
                logger.info("Processing job with id: {}", id);

                try {
                    redisTemplate.opsForHash()
                            .put("status", id, "building");

                    logger.debug("Status updated to 'building' for id: {}", id);
                    
                    buildService.processDeployment(id); 
                    
                    redisTemplate.opsForHash()
                            .put("status", id, "deployed");

                    logger.info("Job completed successfully for id: {}", id);

                } catch (Exception e) {

                    logger.error("Job failed for id: {} - Error: {}", id, e.getMessage(), e);

                    redisTemplate.opsForHash()
                            .put("status", id, "failed");
                }

                Thread.sleep(3000);

            }
        }

    }
}
