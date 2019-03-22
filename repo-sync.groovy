node {
    stage('Clone') {
        git credentialsId: "${params.CREDENTIALS_ID}", url: "${params.REPO_URL}", branch: "${params.BRANCH}"
    }
    stage('Fetch'){
        script {
            sh 'git remote add upstream ${params.UPSTREAM}'
            sh 'git fetch upstream'
        }
    }
    stage('Merge'){
        script {
            sh 'git merge upstream/${params.BRANCH}'
        }
    }
    stage('Push') {
        script {
            sshagent(credentials:["${params.CREDENTIALS_ID}"]) {
                sh 'git push origin ${params.BRANCH}'
            }
        }
    }
}
