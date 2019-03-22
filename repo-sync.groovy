node {
    branch = "${params.BRANCH}"
    upstream = "${params.UPSTREAM}"
    stage('Clone') {
        git credentialsId: "${params.CREDENTIALS_ID}", url: "${params.REPO_URL}", branch: "${params.BRANCH}"
    }
    stage('Fetch'){
        script {
            sh "git remote add upstream $upstream | echo \"\""
            sh 'git fetch upstream'
        }
    }
    stage('Merge'){
        script {
            sh "git merge upstream/$branch"
        }
    }
    stage('Push') {
        script {
            sshagent(credentials:["${params.CREDENTIALS_ID}"]) {
                sh 'git push origin $branch'
            }
        }
    }
}
