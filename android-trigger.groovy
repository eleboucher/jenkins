def date = new Date(System.currentTimeMillis())

String getDevices() { ['curl', '-s', 'https://raw.githubusercontent.com/genesixx/jenkins/master/build-targets'].execute().text }

node
{
    try {
        String[] lines = getDevices().split("\n");
        //def lines = getDevices().tokenize('\n')
        for (String line : lines)
        {
          String[] data = line.split(' ')
          if (data[2].equals("W") && date.getDay() == data[3].toInteger())
          {
              echo "Kicking off a build for ${data[1]}"
              build job: 'build', parameters: [
                  string(name: 'ROM' , value: (data[0] == null) ? "LOS14.1" : data[0]),
                  string(name: 'DEVICE', value: (data[1] == null ) ? "WHY" : data[1])
                  ], propagate: false, wait: false
                sleep 2
          }
          else if (data[2].equals("N")) {
              echo "Kicking off a build for ${data[1]}"
              build job: 'build', parameters: [
                  string(name: 'ROM' , value: (data[0] == null) ? "LOS14.1" : data[0]),
                  string(name: 'DEVICE', value: (data[1] == null ) ? "WHY" : data[1])
                  ], propagate: false, wait: false
                sleep 2
          }
        }
    } catch (e) {
    currentBuild.result = "FAILED"
    throw e
  }
}
