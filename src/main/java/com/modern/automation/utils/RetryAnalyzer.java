package com.modern.automation.utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RetryAnalyzer to handle flaky tests.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger logger = LoggerFactory.getLogger(RetryAnalyzer.class);
    private int count = 0;
    private static final int MAX_RETRY_COUNT = 1;

    @Override
    public boolean retry(ITestResult result) {
        if (count < MAX_RETRY_COUNT) {
            count++;
            logger.info("Retrying test '{}' with status {} for the {} time(s).", 
                    result.getName(), getStatusName(result.getStatus()), count);
            return true;
        }
        return false;
    }

    private String getStatusName(int status) {
        switch (status) {
            case ITestResult.SUCCESS: return "SUCCESS";
            case ITestResult.FAILURE: return "FAILURE";
            case ITestResult.SKIP: return "SKIP";
            default: return "UNKNOWN";
        }
    }
}
