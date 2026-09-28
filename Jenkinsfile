pipeline {

    agent any

    options {
        // We explicitly checkout in the Checkout stage.
        skipDefaultCheckout(true)

        // Prevent two deployments from running at the same time.
        disableConcurrentBuilds()

        // Useful Jenkins console timestamps.
        timestamps()

        // Prevent Jenkins from keeping unlimited old builds.
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    environment {

        AWS_REGION = 'us-east-2'

        AWS_ACCOUNT_ID = '116948313842'

        ECR_REPOSITORY = 'transaction-management'

        // Application EC2 instance.
        // Jenkins EC2 is NOT this instance.
        APP_INSTANCE_ID = 'i-0970ca6d4e38d9148'

        ECR_REGISTRY =
            '116948313842.dkr.ecr.us-east-2.amazonaws.com'
    }
w

    stages {

        // ============================================================
        // 1. CHECKOUT
        // ============================================================

        stage('Checkout') {

            steps {

                checkout scm

                sh '''
                    echo "======================================"
                    echo "Git Information"
                    echo "======================================"

                    git log -1 --oneline
                '''
            }
        }


        // ============================================================
        // 2. PREPARE VERSION
        // ============================================================

        stage('Prepare Image') {

    when {
        anyOf {
            branch 'main'
        }
    }
            steps {

                script {

                    env.GIT_SHORT_SHA = sh(
                        script: 'git rev-parse --short HEAD',
                        returnStdout: true
                    ).trim()


                    /*
                     Example:

                     Jenkins build = 9
                     Git commit    = a7bc123

                     Image tag:
                     9-a7bc123
                    */

                    env.IMAGE_TAG =
                        "${BUILD_NUMBER}-${GIT_SHORT_SHA}"


                    env.IMAGE_URI =
                        "${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}"


                    echo "======================================"
                    echo "Docker Image Information"
                    echo "======================================"

                    echo "Build Number : ${BUILD_NUMBER}"
                    echo "Git Commit   : ${GIT_SHORT_SHA}"
                    echo "Image Tag    : ${IMAGE_TAG}"
                    echo "Image URI    : ${IMAGE_URI}"
                }
            }
        }


        // ============================================================
        // 3. BACKEND UNIT TESTS
        // ============================================================

        stage('Backend Unit Tests') {

            steps {

                dir('backend') {

                    sh '''
                        echo "======================================"
                        echo "Running Spring Boot Unit Tests"
                        echo "======================================"

                        chmod +x mvnw

                        ./mvnw test
                    '''
                }
            }
        }


        // ============================================================
        // 4. PACKAGE SPRING BOOT
        // ============================================================

        stage('Backend Package') {

            steps {

                dir('backend') {

                    sh '''
                        echo "======================================"
                        echo "Packaging Spring Boot Application"
                        echo "======================================"

                        ./mvnw clean package -DskipTests

                        echo ""
                        echo "Generated artifacts:"

                        ls -lh target/*.jar
                    '''
                }
            }
        }


        // ============================================================
        // 5. DOCKER BUILD
        // ============================================================

        stage('Docker Build') {
                when {
                    anyOf {
                        branch 'main'
                    }
                }
            steps {

                /*
                 IMPORTANT:

                 Dockerfile is located inside backend/.

                 Therefore Jenkins changes into backend first.

                 Docker context becomes:

                 backend/

                 So this works correctly:

                 COPY target/*.jar app.jar
                */

                dir('backend') {

                    sh '''
                        echo "======================================"
                        echo "Building Docker Image"
                        echo "======================================"

                        echo "Image:"
                        echo "${IMAGE_URI}"

                        docker build \
                          --pull \
                          -t "${IMAGE_URI}" \
                          .
                    '''
                }
            }
        }


        // ============================================================
        // 6. LOGIN TO AMAZON ECR
        // ============================================================

        stage('ECR Login') {
            when {
                branch 'main'
            }
            steps {

                sh '''
                    echo "======================================"
                    echo "Logging into Amazon ECR"
                    echo "======================================"

                    aws ecr get-login-password \
                      --region "${AWS_REGION}" |
                    docker login \
                      --username AWS \
                      --password-stdin \
                      "${ECR_REGISTRY}"
                '''
            }
        }


        // ============================================================
        // 7. PUSH VERSIONED IMAGE
        // ============================================================

        stage('Push Image') {
            when {
                branch 'main'
            }
            steps {

                sh '''
                    echo "======================================"
                    echo "Pushing Docker Image to ECR"
                    echo "======================================"

                    docker push "${IMAGE_URI}"
                '''
            }
        }


        // ============================================================
        // 8. VERIFY IMAGE EXISTS IN ECR
        // ============================================================

        stage('Verify ECR Image') {
            when {
                branch 'main'
            }
            steps {

                sh '''
                    echo "======================================"
                    echo "Verifying Image in ECR"
                    echo "======================================"

                    aws ecr describe-images \
                      --region "${AWS_REGION}" \
                      --repository-name "${ECR_REPOSITORY}" \
                      --image-ids imageTag="${IMAGE_TAG}" \
                      --query 'imageDetails[0].imageTags' \
                      --output table
                '''
            }
        }


        // ============================================================
        // 9. DEPLOY USING AWS SYSTEMS MANAGER
        // ============================================================

        stage('Deploy to EC2') {
            when {
                branch 'main'
            }
            steps {

                script {

                    echo "======================================"
                    echo "Sending Deployment Command Through SSM"
                    echo "======================================"


                    env.COMMAND_ID = sh(

                        script: """
                            aws ssm send-command \
                              --region "${AWS_REGION}" \
                              --instance-ids "${APP_INSTANCE_ID}" \
                              --document-name "AWS-RunShellScript" \
                              --parameters 'commands=["/opt/transaction/deploy-backend.sh ${IMAGE_URI} ${AWS_REGION}"]' \
                              --query "Command.CommandId" \
                              --output text
                        """,

                        returnStdout: true

                    ).trim()


                    echo "SSM Command ID: ${COMMAND_ID}"
                }
            }
        }


        // ============================================================
        // 10. WAIT FOR DEPLOYMENT
        // ============================================================

        stage('Wait for Deployment') {
            when {
                branch 'main'
            }
            steps {

                script {

                    echo "Waiting for application deployment..."


                    /*
                     AWS waiter returns a non-zero status if the
                     remote deployment fails.

                     We capture it instead of immediately killing
                     the Jenkins pipeline so that we can retrieve
                     the SSM logs.
                    */

                    env.SSM_WAIT_RESULT = sh(

                        script: """
                            aws ssm wait command-executed \
                              --region "${AWS_REGION}" \
                              --command-id "${COMMAND_ID}" \
                              --instance-id "${APP_INSTANCE_ID}"
                        """,

                        returnStatus: true

                    ).toString()


                    echo "SSM waiter result: ${SSM_WAIT_RESULT}"
                }
            }
        }


        // ============================================================
        // 11. GET DEPLOYMENT RESULT
        // ============================================================

        stage('Deployment Result') {
            when {
                branch 'main'
            }
            steps {

                script {

                    env.DEPLOYMENT_STATUS = sh(

                        script: """
                            aws ssm get-command-invocation \
                              --region "${AWS_REGION}" \
                              --command-id "${COMMAND_ID}" \
                              --instance-id "${APP_INSTANCE_ID}" \
                              --query Status \
                              --output text
                        """,

                        returnStdout: true

                    ).trim()


                    echo "======================================"
                    echo "Deployment Status"
                    echo "======================================"

                    echo "${DEPLOYMENT_STATUS}"


                    /*
                     Print complete output whether success or failure.
                    */

                    sh """
                        aws ssm get-command-invocation \
                          --region "${AWS_REGION}" \
                          --command-id "${COMMAND_ID}" \
                          --instance-id "${APP_INSTANCE_ID}" \
                          --query '{Status:Status,Output:StandardOutputContent,Error:StandardErrorContent}'
                    """


                    if (env.DEPLOYMENT_STATUS != 'Success') {

                        error(
                            "Deployment failed. " +
                            "SSM Status: ${env.DEPLOYMENT_STATUS}"
                        )
                    }
                }
            }
        }


        // ============================================================
        // 12. FINAL REMOTE HEALTH CHECK
        // ============================================================

        stage('Verify Application Health') {
            when {
                branch 'main'
            }
            steps {

                script {

                    env.HEALTH_COMMAND_ID = sh(

                        script: """
                            aws ssm send-command \
                              --region "${AWS_REGION}" \
                              --instance-ids "${APP_INSTANCE_ID}" \
                              --document-name "AWS-RunShellScript" \
                              --parameters 'commands=["curl -fsS http://localhost:8081/actuator/health"]' \
                              --query "Command.CommandId" \
                              --output text
                        """,

                        returnStdout: true

                    ).trim()


                    sh """
                        aws ssm wait command-executed \
                          --region "${AWS_REGION}" \
                          --command-id "${HEALTH_COMMAND_ID}" \
                          --instance-id "${APP_INSTANCE_ID}"
                    """


                    sh """
                        echo "======================================"
                        echo "Application Health"
                        echo "======================================"

                        aws ssm get-command-invocation \
                          --region "${AWS_REGION}" \
                          --command-id "${HEALTH_COMMAND_ID}" \
                          --instance-id "${APP_INSTANCE_ID}" \
                          --query StandardOutputContent \
                          --output text
                    """
                }
            }
        }
    }


    // ================================================================
    // PIPELINE RESULT
    // ================================================================

    post {

        success {

            echo "======================================"
            echo "CI/CD SUCCESS"
            echo "======================================"

            echo "Build Number : ${BUILD_NUMBER}"
            echo "Git Commit   : ${GIT_SHORT_SHA}"
            echo "Image        : ${IMAGE_URI}"
            echo "Instance     : ${APP_INSTANCE_ID}"
        }


        failure {

            echo "======================================"
            echo "CI/CD FAILED"
            echo "======================================"

            echo "Check the Jenkins stage that failed."
        }


        always {

            sh '''
                echo "Cleaning unused Jenkins Docker layers..."

                docker image prune -f || true
            '''
        }
    }
}