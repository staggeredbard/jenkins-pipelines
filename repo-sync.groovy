node {
    stage('Clone') {
        git credentialsId: 'SSS', url: "${params.REPO_URL}", branch: "${params.BRANCH}"
        script {
          sh 'ls -al'
        }
    }
}
