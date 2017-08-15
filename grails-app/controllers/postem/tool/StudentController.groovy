package postem.tool

class StudentController {

    def canvasFileService
    def CSVService

    def index() {
        def fileList = canvasFileService.listFiles((String)session.courseId)
        def filteredList = CSVService.filterByUser(fileList,(String)session.user)
        [courseFiles: filteredList]
    }

    def viewFile(){
        String fileURL = params.fileURL
        String fileId = params.fileId
        //log user viewing file
        def lastViewed = UserFileViewLog.findByFileIdAndLoginId(fileId,(String)session.user)
        if(lastViewed != null){
            lastViewed.lastViewed = new Date()
        }
        else{
            lastViewed = new UserFileViewLog(loginId: session.user, fileId: fileId, lastViewed: new Date())
        }
        lastViewed.save()
        def parsedFileMap = CSVService.parseFileForUser(fileURL,(String)session.user)
        def headerArray = parsedFileMap.get('headers')
        parsedFileMap.remove('headers')
        [headers: headerArray, contents: parsedFileMap.values()]
    }
}
