node {
    stage('Clone') {
        git credentialsId: 'SSS', url: "${params.REPO_URL}", branch: 'release'
        script {
          sh 'ls -al'
        }
    }
}
