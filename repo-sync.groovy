node {
    stage('Clone') {
        git credentialsId: 'Jenkins', url: "${params.REPO_URL}", branch: 'release'
        script {
          sh 'ls -al'
        }
    }
}
