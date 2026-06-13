pipeline {
    agent any

    parameters {
        choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'], description: 'Select browser for test execution')
        choice(name: 'ENVIRONMENT', choices: ['prod', 'stage', 'dev'], description: 'Select environment')
        booleanParam(name: 'RUN_SMOKE', defaultValue: true, description: 'Include Smoke Test Suite')
        booleanParam(name: 'RUN_REGRESSION', defaultValue: false, description: 'Include Full Regression Suite')
    }

    environment {
        // Ensure Maven is configured in Jenkins Global Tool Configuration with the name 'Maven'
        MAVEN_HOME = tool 'Maven'
        PATH = "${env.MAVEN_HOME}\\bin;${env.PATH}"
    }

    stages {
        stage('Initialize') {
            steps {
                echo "🚀 Initializing Pipeline for ${params.ENVIRONMENT} on ${params.BROWSER}..."
                bat 'mvn clean'
            }
        }

        stage('Smoke Tests') {
            when {
                expression { params.RUN_SMOKE }
            }
            steps {
                echo "🔥 Running Smoke Test Suite..."
                // catchError ensures that if tests fail, the pipeline continues to the next stage
                catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                    bat "mvn test -DsuiteXmlFile=src/test/resources/testng-smoke.xml -Dbrowser=${params.BROWSER}"
                }
            }
        }

        stage('Regression Tests') {
            when {
                expression { params.RUN_REGRESSION }
            }
            steps {
                echo "🧪 Running Full Regression Suite..."
                catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                    bat "mvn test -DsuiteXmlFile=src/test/resources/testng.xml -Dbrowser=${params.BROWSER}"
                }
            }
        }
    }

    post {
        always {
            echo "📊 Generating Final Allure Report..."
            // Using the specific Allure tool name to avoid ambiguity
            allure includeProperties: false, jdk: '', results: [[path: 'allure-results']], commandline: 'Allure 2.27'
            
            echo "📸 Archiving failure artifacts..."
            archiveArtifacts artifacts: 'build/screenshots/*.png', allowEmptyArchive: true
        }
        unstable {
            echo "⚠️ Build completed with some test failures."
        }
        failure {
            echo "❌ Build failed due to system or compilation error."
        }
    }
}
