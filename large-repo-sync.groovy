node {
    branch = "${params.BRANCH}"
    currentBuild.displayName = branch
    upstream = "${params.UPSTREAM}"
    repoUrl = "${params.REPO_URL}"
    depth = "${params.CLONE_DEPTH}"
    stage('Clone') {
        sshagent(credentials:["${params.CREDENTIALS_ID}"]) {
          sh "git clone --depth $depth $repoUrl -b $branch"
          sh "git clean  -d  -f ."
        }
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
                sh "git push --set-upstream origin $branch | echo \"setting upstream push branch $branch\""
                sh "git push origin $branch"
            }
        }
    }
}
