pipeline {
    agent any

    parameters {
        choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'], description: 'Select browser for test execution')
        choice(name: 'ENVIRONMENT', choices: ['prod', 'stage', 'dev'], description: 'Select environment')
    }

    environment {
        // Ensure Maven is configured in Jenkins Global Tool Configuration with the name 'Maven'
        MAVEN_HOME = tool 'Maven'
        // For Windows, the path separator is semicolon and bin is in the MAVEN_HOME
        PATH = "${env.MAVEN_HOME}\\bin;${env.PATH}"
    }

    stages {
        stage('Setup & Cleanup') {
            steps {
                echo "Initializing Pipeline for ${params.ENVIRONMENT} on ${params.BROWSER}..."
                bat 'mvn clean'
            }
        }

        stage('Smoke Tests') {
            steps {
                echo "Running Smoke Test Suite..."
                bat "mvn test -DsuiteXmlFile=src/test/resources/testng-smoke.xml -Dbrowser=${params.BROWSER}"
            }
            post {
                always {
                    // Explicitly using 'Allure 2.27' to resolve ambiguity
                    allure includeProperties: false, jdk: '', results: [[path: 'allure-results']], commandline: 'Allure 2.27'
                }
            }
        }

        stage('Regression Tests') {
            when {
                expression { currentBuild.result == null || currentBuild.result == 'SUCCESS' }
            }
            steps {
                echo "Running Full Regression Suite..."
                bat "mvn test -DsuiteXmlFile=src/test/resources/testng.xml -Dbrowser=${params.BROWSER}"
            }
        }
    }

    post {
        always {
            echo "Generating Final Allure Report..."
            allure includeProperties: false, jdk: '', results: [[path: 'allure-results']], commandline: 'Allure 2.27'
            
            echo "Archiving failure artifacts..."
            archiveArtifacts artifacts: 'build/screenshots/*.png', allowEmptyArchive: true
        }
        failure {
            echo "Pipeline failed. Please check the Allure report and screenshots."
        }
    }
}
