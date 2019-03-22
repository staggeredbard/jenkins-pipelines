node {
    stage('Build') {
        echo "Cloning : ${params.REPO_URL}"
        git credentialsId: 'Jenkins', url: "${params.REPO_URL}"
        script {
            pom = readMavenPom file: 'pom.xml'
            currentBuild.displayName = pom.version
            env.current = pom.version
        }
        withSonarQubeEnv('Sonar') {
            def mvn_version='Maven'
            withEnv( ["PATH+MAVEN=${tool mvn_version}/bin"] ) {
                sh 'mvn clean install sonar:sonar'
            }
        } // SonarQube taskId is automatically attached to the pipeline context
    }
}
 
// No need to occupy a node
stage("Quality Gate") {
    timeout(time: 10, unit: 'MINUTES') { // Just in case something goes wrong, pipeline will be killed after a timeout
        def qg = waitForQualityGate() // Reuse taskId previously collected by withSonarQubeEnv
        if (qg.status != 'OK') {
            error "Pipeline aborted due to quality gate failure: ${qg.status}"
        }
    }
}

// Release input which presents a prompt with 3 version inputs
node {
    stage('Release') {
        echo "Do release : "+"${params.RELEASE}".toString().equals("true").toString()
        if ("${params.RELEASE}".toString().equals("true")) {
            timeout(time: 10, unit: 'MINUTES') {
                def mvn_version='Maven'
                withEnv( ["PATH+MAVEN=${tool mvn_version}/bin"] ) {
                    int zero = 0
                    int firstDot = env.current.indexOf(".")
                    int lastDot = env.current.lastIndexOf(".")
                    revision = env.current.replaceAll("-SNAPSHOT","")
                    minorVersion = Integer.toString(Integer.parseInt(env.current.substring(firstDot+1,lastDot))+1)
                    minor = env.current.substring(zero,firstDot)+"."+minorVersion+".0"
                    majorVersion = Integer.toString(Integer.parseInt(env.current.substring(0,firstDot))+1)
                    major = majorVersion+".0.0"
                    echo "Presenting versions - {Revision : "+revision+"},{Minor : "+minor+"},{Major : "+major+"}"
                    choice = new ChoiceParameterDefinition('Param name', [revision, minor, major] as String[], 'Description')
                    releaseVersion = input message : "Select Release version", parameters: [choice]
                    lastDot = releaseVersion.lastIndexOf(".")
                    developmentRevision = Integer.toString(Integer.parseInt(releaseVersion.substring(releaseVersion.lastIndexOf(".")+1, releaseVersion.length()))+1)+"-SNAPSHOT"
                    developmentVersion = releaseVersion.substring(0,lastDot+1)+developmentRevision
                    echo "Releasing - {Release version : "+releaseVersion+"},{Development version : "+developmentVersion+"}"
                    sh "git config --global user.email mail@mailserver.com"
                    sh "git config --global user.name addUserName"
                    sshagent(credentials:['Jenkins']) {
                        sh "mvn -DdevelopmentVersion=$developmentVersion -DreleaseVersion=$releaseVersion -Dresume=false release:prepare release:perform"
                    }
                }
            }
        }
    }
}
