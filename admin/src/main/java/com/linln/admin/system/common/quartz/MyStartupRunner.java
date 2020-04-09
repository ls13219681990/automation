package com.linln.admin.system.common.quartz;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MyStartupRunner implements CommandLineRunner {


    protected static final Logger logger = LoggerFactory.getLogger(MyStartupRunner.class);


    @Autowired
    public CronSchedulerJob scheduleJobs;

    @Override
    public void run(String... args) throws Exception {
       scheduleJobs.scheduleJobs();
        logger.info(">>>>>>>>>>>>>>>定时任务开始执行<<<<<<<<<<<<<");
    }



}