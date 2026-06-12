package com.modern.automation.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Utility to manage cleanup of test artifacts like screenshots and allure results.
 */
@Slf4j
public class CleanupUtils {

    private static final String ARCHIVE_PATH = "test-archives/";
    private static final int MAX_RUNS_TO_KEEP = 3;

    /**
     * Archives current results and screenshots, then cleans up old archives.
     */
    public static void performCleanup() {
        archivePreviousRun();
        purgeOldArchives();
    }

    private static void archivePreviousRun() {
        String timestamp = DateUtils.getCurrentTimestamp();
        File archiveDir = new File(ARCHIVE_PATH + "run_" + timestamp);

        boolean hasContent = false;
        
        // Move Allure Results
        File allureResults = new File("allure-results");
        if (allureResults.exists() && allureResults.list().length > 0) {
            try {
                FileUtils.moveDirectory(allureResults, new File(archiveDir, "allure-results"));
                hasContent = true;
                log.info("Archived allure-results to {}", archiveDir.getPath());
            } catch (IOException e) {
                log.error("Failed to archive allure-results", e);
            }
        }

        // Move Screenshots
        File screenshots = new File("build/screenshots");
        if (screenshots.exists() && screenshots.list().length > 0) {
            try {
                FileUtils.moveDirectory(screenshots, new File(archiveDir, "screenshots"));
                hasContent = true;
                log.info("Archived screenshots to {}", archiveDir.getPath());
            } catch (IOException e) {
                log.error("Failed to archive screenshots", e);
            }
        }

        // If nothing was archived, delete the empty archive dir if it was created
        if (!hasContent && archiveDir.exists()) {
            archiveDir.delete();
        }
    }

    private static void purgeOldArchives() {
        File archiveRoot = new File(ARCHIVE_PATH);
        if (!archiveRoot.exists()) return;

        File[] runs = archiveRoot.listFiles(File::isDirectory);
        if (runs == null || runs.length <= MAX_RUNS_TO_KEEP) return;

        // Sort by name (which includes timestamp yyyyMMdd_HHmmss)
        Arrays.sort(runs, Comparator.comparing(File::getName));

        int foldersToDelete = runs.length - MAX_RUNS_TO_KEEP;
        for (int i = 0; i < foldersToDelete; i++) {
            try {
                FileUtils.deleteDirectory(runs[i]);
                log.info("Deleted old archive: {}", runs[i].getPath());
            } catch (IOException e) {
                log.error("Failed to delete old archive: {}", runs[i].getPath(), e);
            }
        }
    }
}
