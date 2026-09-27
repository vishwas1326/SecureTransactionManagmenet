pipeline {

    agent any

    environment {
        AWS_REGION = 'us-east-2'

        AWS_ACCOUNT_ID = '116948313842'

        ECR_REPOSITORY = 'transaction-management'
        APP_INSTANCE_ID = 'i-0970ca6d4e38d9148'
    }
    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Unit Tests') {
            steps {
                sh '''
                    chmod +x mvnw
                    ./mvnw test
                '''
            }
        }

        stage('Package') {
            steps {
                sh '''
                    ./mvnw clean package -DskipTests
                '''
            }
        }

        stage('Prepare Image') {
            steps {
                script {
                    env.IMAGE_TAG = "${BUILD_NUMBER}"
                    env.IMAGE_URI =
                        "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY}:${IMAGE_TAG}"

                    echo "Docker image:"
                    echo "${env.IMAGE_URI}"
                }
            }
        }
        stage('Docker Build') {
            steps {
                sh '''
                    docker build \
                      -t ${IMAGE_URI} \
                      .
                '''
            }
        }
        stage('ECR Login') {
            steps {
                sh '''
                    aws ecr get-login-password \
                      --region ${AWS_REGION} |
                    docker login \
                      --username AWS \
                      --password-stdin \
                      ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com
                '''
            }
        }
        stage('Push Image') {
            steps {
                sh '''
                    docker push ${IMAGE_URI}
                '''
            }
        }
       stage('Deploy') {

            steps {

                script {

                    env.COMMAND_ID = sh(
                        script: """
                            aws ssm send-command \
                              --region ${AWS_REGION} \
                              --instance-ids "${APP_INSTANCE_ID}" \
                              --document-name "AWS-RunShellScript" \
                              --parameters 'commands=["sudo /opt/transaction/deploy-backend.sh ${IMAGE_URI} ${AWS_REGION}"]' \
                              --query "Command.CommandId" \
                              --output text
                        """,
                        returnStdout: true
                    ).trim()

                    echo "SSM Command ID: ${env.COMMAND_ID}"
                }


                sh '''
                    echo "Waiting for deployment..."

                    aws ssm wait command-executed \
                      --region ${AWS_REGION} \
                      --command-id ${COMMAND_ID} \
                      --instance-id ${APP_INSTANCE_ID}
                '''


                script {

                    def deploymentStatus = sh(
                        script: """
                            aws ssm get-command-invocation \
                              --region ${AWS_REGION} \
                              --command-id ${COMMAND_ID} \
                              --instance-id ${APP_INSTANCE_ID} \
                              --query Status \
                              --output text
                        """,
                        returnStdout: true
                    ).trim()

                    echo "Deployment status: ${deploymentStatus}"

                    if (deploymentStatus != "Success") {

                        sh """
                            aws ssm get-command-invocation \
                              --region ${AWS_REGION} \
                              --command-id ${COMMAND_ID} \
                              --instance-id ${APP_INSTANCE_ID}
                        """

                        error("Application deployment failed")
                    }
                }
            }
        }


        stage('Deployment Output') {

            steps {

                sh '''
                    aws ssm get-command-invocation \
                      --region ${AWS_REGION} \
                      --command-id ${COMMAND_ID} \
                      --instance-id ${APP_INSTANCE_ID} \
                      --query '{Status:Status,Output:StandardOutputContent,Error:StandardErrorContent}'
                '''
            }
        }
    }


    post {

        success {
            echo "===================================="
            echo "CI/CD SUCCESS"
            echo "Deployed image: ${IMAGE_URI}"
            echo "===================================="
        }

        failure {
            echo "===================================="
            echo "CI/CD FAILED"
            echo "Check the failed Jenkins stage."
            echo "===================================="
        }

        always {
            sh '''
                docker image prune -f || true
            '''
        }
    }
}