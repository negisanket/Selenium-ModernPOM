pipeline {
    agent any

    parameters {
        choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'], description: 'Select browser for test execution')
        choice(name: 'ENVIRONMENT', choices: ['prod', 'stage', 'dev'], description: 'Select environment')
    }

    environment {
        MAVEN_HOME = tool 'Maven' // Ensure Maven is configured in Jenkins Global Tool Configuration
        PATH = "${env.MAVEN_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Setup & Cleanup') {
            steps {
                echo "Initializing Pipeline for ${params.ENVIRONMENT} on ${params.BROWSER}..."
                sh 'mvn clean'
            }
        }

        stage('Smoke Tests') {
            steps {
                echo "Running Smoke Test Suite..."
                // We use -Dbrowser to override the XML value if needed, or rely on framework default
                sh "mvn test -DsuiteXmlFile=src/test/resources/testng-smoke.xml -Dbrowser=${params.BROWSER}"
            }
            post {
                always {
                    allure includeProperties: false, jdk: '', results: [[path: 'allure-results']]
                }
            }
        }

        stage('Regression Tests') {
            when {
                expression { currentBuild.result == null || currentBuild.result == 'SUCCESS' }
            }
            steps {
                echo "Running Full Regression Suite..."
                // Exclude smokeTest group if you want "Regression other than smoke"
                // Or just run the full testng.xml
                sh "mvn test -DsuiteXmlFile=src/test/resources/testng.xml -Dbrowser=${params.BROWSER}"
            }
        }
    }

    post {
        always {
            echo "Generating Final Allure Report..."
            allure includeProperties: false, jdk: '', results: [[path: 'allure-results']]
            
            echo "Archiving failure artifacts..."
            archiveArtifacts artifacts: 'build/screenshots/*.png', allowEmptyArchive: true
        }
        failure {
            echo "Pipeline failed. Please check the Allure report and screenshots."
        }
    }
}
