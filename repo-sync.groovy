node {
    stage('Clone') {
        git credentialsId: "${params.CREDENTIALS_ID}", url: "${params.REPO_URL}", branch: "${params.BRANCH}"
        script {
          sh 'ls -al'
        }
    }
}
