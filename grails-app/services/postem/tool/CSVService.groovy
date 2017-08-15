package postem.tool

import com.opencsv.CSVReader
import grails.transaction.Transactional
import org.springframework.web.multipart.MultipartFile
import postem.tool.canvas.File

@Transactional
class CSVService {

    def parseFile(String url){
        URL canvasFile = new URL(url)
        BufferedReader reader = new BufferedReader(new InputStreamReader(canvasFile.openStream()))
        CSVReader csvReader = new CSVReader(reader)
        def resultMap = new HashMap()
        String [] nextLine
        def counter = 0
        while ((nextLine = csvReader.readNext()) != null) {
            // nextLine[] is an array of values from the line
            if(counter == 0){
                resultMap.put('headers', nextLine)
            }
            else{
                resultMap.put(nextLine[0],nextLine)
            }
            counter++
        }
        reader.close()
        return resultMap
    }

    def parseFileForUser(String url, String user){
        URL canvasFile = new URL(url)
        BufferedReader reader = new BufferedReader(new InputStreamReader(canvasFile.openStream()))
        CSVReader csvReader = new CSVReader(reader)
        def resultMap = new HashMap()
        String [] nextLine
        def counter = 0
        while ((nextLine = csvReader.readNext()) != null) {
            // nextLine[] is an array of values from the line
            if(counter == 0){
                resultMap.put('headers', nextLine)
            }
            else if(nextLine[0] == user){
                resultMap.put(nextLine[0],nextLine)
                break
            }
            counter++
        }
        reader.close()
        return resultMap
    }

    def filterByUser(List<File> fileList, String user){
        List<File> filteredList = new ArrayList<>()
        for(int i = 0; i < fileList.size(); i++){
            File file = fileList.get(i)
            if(file.hidden && isUserInFile(file, user)){
                filteredList.add(file)
            }
        }
        return filteredList
    }

    private def isUserInFile(File file, String user){
        URL canvasFile = new URL(file.url)
        BufferedReader reader = new BufferedReader(new InputStreamReader(canvasFile.openStream()))
        CSVReader csvReader = new CSVReader(reader)
        String [] nextLine
        while ((nextLine = csvReader.readNext()) != null) {
            if(nextLine[0] == user){
                return true
            }
        }
        reader.close()
        return false
    }

    def validateFile(MultipartFile file, users){
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))
        CSVReader csvReader = new CSVReader(reader)
        String [] nextLine
        def badUsers = []
        //skip headers
        csvReader.readNext()
        def rowCounter = 1
        while ((nextLine = csvReader.readNext()) != null) {
            rowCounter++
            if(!users.contains(nextLine[0])){
                if(nextLine[0].isAllWhitespace()){
                    badUsers.add('Row ' + rowCounter + ': Login ID is blank')
                }
                else{
                    badUsers.add('Row ' + rowCounter + ': Login ID is not enrolled')
                }
            }
        }
        reader.close()
        return badUsers
    }
}
