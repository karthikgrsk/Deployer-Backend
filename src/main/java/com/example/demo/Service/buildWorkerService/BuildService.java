package com.example.demo.Service.buildWorkerService;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BuildService {

    private static final Logger logger = LoggerFactory.getLogger(BuildService.class);

    @Autowired
    private UploadBuildService uploadBuildService;

    @Autowired
    private s3DowloadService s3Download;

    public void processDeployment(String id) throws Exception {

        logger.info("Starting deployment process for id: {}", id);

        s3Download.downloadFolder(id);

        File projectDir = new File("/app/workspace/" + id);
        File nodeModules = new File(projectDir,"node_modules");
        //System.out.println("Line 28: Project dir: " + projectDir.getAbsolutePath());

        printFiles(projectDir, 0);


        File packageJson = new File(projectDir, "package.json");

        // Validate project directory exists
        if (!projectDir.exists()) {
            logger.error("Project directory does not exist: {}", projectDir.getAbsolutePath());
            throw new Exception("Project directory not found: " + projectDir.getAbsolutePath());
        }

        if (packageJson.exists()) {
            // npm install
            logger.info("Running npm install in directory: {}", projectDir.getAbsolutePath());

            if(!nodeModules.exists()){
            ProcessBuilder install = new ProcessBuilder("npm", "install","--legacy-peer-deps");
            install.directory(projectDir);
            install.inheritIO();

            Process installProcess = install.start();
            int installExitCode = installProcess.waitFor();

            if (installExitCode != 0) {
                logger.error("npm install failed with exit code: {}", installExitCode);
                throw new Exception("npm install failed with exit code: " + installExitCode);
            }

            logger.info("npm install completed successfully");
           }
            // npm run build
            logger.info("Running npm run build in directory: {}", projectDir.getAbsolutePath());

            ProcessBuilder build = new ProcessBuilder("npm", "run", "build", "--", "--base=/" + id + "/");
            build.directory(projectDir);
            build.inheritIO();

            Process buildProcess = build.start();
            int buildExitCode = buildProcess.waitFor();

            if (buildExitCode != 0) {
                logger.error("npm run build failed with exit code: {}", buildExitCode);
                throw new Exception("npm run build failed with exit code: " + buildExitCode);
            }

            logger.info("npm run build completed successfully");
        } else {
            System.out.println("No package.json found");
            System.out.println("Deploying static project directly");
        }

        logger.info("Uploading build artifacts for id: {}", id);

        uploadBuildService.uploadBuildFolder(id);

        logger.info("Deployment process completed successfully for id: {}", id);
    }

    private void printFiles(File dir, int level) {

        if (dir == null || !dir.exists()) {
            return;
        }

        File[] files = dir.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {

            for (int i = 0; i < level; i++) {
                System.out.print("  ");
            }

            System.out.println(file.getName());

            if (file.isDirectory()) {
                printFiles(file, level + 1);
            }
        }
    }

}
