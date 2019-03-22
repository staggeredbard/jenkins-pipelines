node {
    stage('Clone') {
        git credentialsId: "${params.CREDENTIALS_ID}", url: "${params.REPO_URL}", branch: "${params.BRANCH}"
        script {
            sh 'git remote add upstream ${params.UPSTREAM}'
            sh 'git fetch upstream'
            sh 'git merge upstream/${params.BRANCH}'
            sshagent(credentials:["${params.CREDENTIALS_ID}"]) {
                sh 'git push origin ${params.BRANCH}'
            }
        }
    }
}
