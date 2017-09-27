currentBuild.displayName = ROM + '-' + DEVICE
def BUILD_TREE = "/home/jenkins/" + ROM

node {
    stage('Sync'){
        sh '''#!/bin/bash
            cd '''+BUILD_TREE+'''
            repo sync -d -c -j20 --force-sync
            . build/envsetup.sh
            breakfast $DEVICE
        '''
      }
      stage('Clean'){
        sh '''#!/bin/bash
          cd '''+BUILD_TREE+'''
          make clean
        '''
      }
      stage('Build'){
        sh '''#!/bin/bash +e
            cd '''+BUILD_TREE+'''
            . build/envsetup.sh
            export USE_CCACHE=1
            export PATH=$HOME/.bin:$PATH
            export CCACHE_DIR=/home/jenkins/.ccache
            ./prebuilts/sdk/tools/jack-admin list-server && ./prebuilts/sdk/tools/jack-admin kill-server
            export JACK_SERVER_VM_ARGUMENTS="-Dfile.encoding=UTF-8 -XX:+TieredCompilation -Xmx12g"
            ./prebuilts/sdk/tools/jack-admin start-server
            brunch $DEVICE || true
            echo "BUILD DONE"
            ./prebuilts/sdk/tools/jack-admin list-server && ./prebuilts/sdk/tools/jack-admin kill-server || true
        '''
      }
      stage('Upload to www'){
         sh '''#!/bin/bash
            case ''' + ROM + ''' in
                "RR7.1" ) 
                    export repertoire="ResurrectionRemix"
                    export file="RR"
                    ;;
                "RR6.0" ) 
                    export repertoire="ResurrectionRemix"
                    export file="ResurrectionRemix"
                    ;;
                "AICP7.1")
                    export repertoire="aicp"
                    export file="aicp_"
                    ;;
                "LOS14.1")
                    export repertoire="LineageOS"
                    export file="lineage-"
                    ;;
            esac
            mkdir -p /home/build/genesixx/$repertoire/''' + DEVICE + '''
            cp ''' + BUILD_TREE + '''/out/target/product/''' + DEVICE +'''/$file*''' + DEVICE + '''*.zip /home/build/genesixx/$repertoire/''' + DEVICE
      }
}
