node {
    branch = "${params.BRANCH}"
    upstream = "${params.UPSTREAM}"
    stage('Clone') {
        git credentialsId: "${params.CREDENTIALS_ID}", url: "${params.REPO_URL}", branch: "${params.BRANCH}"
    }
    stage('Fetch'){
        script {
            sh "git remote add upstream $upstream | echo \"setting remote upstream\""
            sshagent(credentials:["${params.CREDENTIALS_ID}"]) {
                sh 'git fetch upstream'
            }
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
                sh 'git push --set-upstream origin ${params.BRANCH} | echo \"setting upstream push branch ${params.BRANCH}\"'
                sh 'git push origin $branch'
            }
        }
    }
}
