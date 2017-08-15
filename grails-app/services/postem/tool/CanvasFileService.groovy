package postem.tool

import grails.converters.JSON
import grails.core.GrailsApplication
import grails.plugins.rest.client.RestResponse
import grails.transaction.Transactional
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.springframework.core.io.InputStreamResource
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.multipart.MultipartFile
import postem.tool.canvas.File
import postem.tool.canvas.UploadParams

@Transactional
class CanvasFileService {

    def restClient
    GrailsApplication grailsApplication

    private def notifyCanvas(String fileName, Boolean uploadAsLocked, String courseId, String userId) {
        def canvasBaseURL = grailsApplication.config.getProperty('canvas.canvasBaseUrl')
        def oauthToken = grailsApplication.config.getProperty('canvas.oauthToken')
        def resp = restClient.post(canvasBaseURL + '/api/v1/courses/' + courseId + '/files?as_user_id=' + userId){
            auth('Bearer ' + oauthToken)
            json{
                name = fileName
                parent_folder_path = "postem"
                locked = uploadAsLocked
            }
        }
        UploadParams uploadParams = new UploadParams(uploadUrl: resp.json.upload_url, awsAccessKeyId: resp.json.upload_params.AWSAccessKeyId,
                fileName: resp.json.upload_params.Filename, key: resp.json.upload_params.key, acl: resp.json.upload_params.acl, policy: resp.json.upload_params.Policy,
                signature: resp.json.upload_params.Signature, successAccessRedirect: resp.json.upload_params.success_action_redirect, contentType: resp.json.upload_params.'content-type')
        return resp
    }

    private def awsUpload(uploadParams, MultipartFile multipartFile) {
        String uploadUrl = uploadParams.json.upload_url
        JSONObject params = uploadParams.json.upload_params
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<String, Object>()
        params.each {k,v ->
            form.add(k.toString(),v)
        }
        form.add("file", new InputStreamResource(multipartFile.getInputStream()))
        def resp = restClient.post(uploadUrl){
            contentType "multipart/form-data"
            body(form)
        }
        JSON.parse(resp)
        return resp
    }

    def upload(MultipartFile multipartFile, Boolean uploadAsLocked, Boolean releaseFeedback, String courseId, String fileTitle, String userId){
        def oauthToken = grailsApplication.config.getProperty('canvas.oauthToken')
        def uploadParams = notifyCanvas(fileTitle, uploadAsLocked, courseId, userId)
        def awsUploadResponse = awsUpload(uploadParams, multipartFile)
        def resp = restClient.post(awsUploadResponse.headers.getLocation().toString()){
            auth('Bearer ' + oauthToken)
        }
        String fileId = resp.json.id
        if(releaseFeedback){
            hideFile(fileId, true)
        }
        else{
            hideFile(fileId, false)
        }
    }

    def listFiles(String courseId){
        def canvasBaseURL = grailsApplication.config.getProperty('canvas.canvasBaseUrl')
        def oauthToken = grailsApplication.config.getProperty('canvas.oauthToken')
        def resp = restClient.get(canvasBaseURL + '/api/v1/courses/' + courseId + '/folders/by_path/postem'){
            auth('Bearer ' + oauthToken)
        }
        if(resp.status != 200){
            def createFolderResp = restClient.post(canvasBaseURL + '/api/v1/courses/' + courseId + '/folders'){
                auth('Bearer ' + oauthToken)
                json{
                    name = 'postem'
                    locked = true
                    parent_folder_path = '/'
                }
            }
            return fetchFiles(createFolderResp.json.id)
        }
        JSONArray respArr = (JSONArray)resp.json
        def folderId = -1
        for(jsonObj in respArr){
            if(jsonObj.name == 'postem'){
                folderId = jsonObj.id
                break
            }
        }
        return fetchFiles(folderId)
    }

    private def fetchFiles(Long folderId){
        def canvasBaseURL = grailsApplication.config.getProperty('canvas.canvasBaseUrl')
        def oauthToken = grailsApplication.config.getProperty('canvas.oauthToken')
        List<File> fileList = new ArrayList<>()
        def resp = restClient.get(canvasBaseURL + '/api/v1/folders/' + folderId + '/files?include[]=user'){
            auth('Bearer ' + oauthToken)
        }
        populateFileList(resp, fileList)

        def nextPage = canvasNextPage(resp)
        while(nextPage != null){
            resp = restClient.get(nextPage){
                auth('Bearer ' + oauthToken)
            }
            populateFileList(resp, fileList)
            nextPage = canvasNextPage(resp)
        }

        return fileList
    }

    private static def populateFileList(RestResponse resp, List<File> fileList){
        JSONArray respArr = (JSONArray)resp.json
        for(jsonObj in respArr){
            String formattedFileName = jsonObj.display_name
            if(formattedFileName.contains('.csv')){
                formattedFileName = formattedFileName.take(formattedFileName.lastIndexOf('.'))
            }
            File file = new File(fileId: jsonObj.id, displayName: formattedFileName, fileName: jsonObj.filename, modifiedBy: jsonObj.user.display_name, updatedAt: Date.parse("yyyy-MM-dd'T'HH:mm:ss'Z'", (String)jsonObj.updated_at), locked: jsonObj.locked, hidden: jsonObj.hidden, url: jsonObj.url)
            fileList.add(file)
        }
    }

    def deleteFile(String fileId){
        def canvasBaseURL = grailsApplication.config.getProperty('canvas.canvasBaseUrl')
        def oauthToken = grailsApplication.config.getProperty('canvas.oauthToken')
        restClient.delete(canvasBaseURL + '/api/v1/files/' + fileId){
            auth('Bearer ' + oauthToken)
        }
    }

    def hideFile(String fileId, Boolean hide){
        def canvasBaseURL = grailsApplication.config.getProperty('canvas.canvasBaseUrl')
        def oauthToken = grailsApplication.config.getProperty('canvas.oauthToken')
        restClient.put(canvasBaseURL + '/api/v1/files/' + fileId){
            auth('Bearer ' + oauthToken)
            json{
                hidden = hide
            }
        }
    }

    def listUserLogins(String courseId){
        def users = []
        def canvasBaseURL = grailsApplication.config.getProperty('canvas.canvasBaseUrl')
        def oauthToken = grailsApplication.config.getProperty('canvas.oauthToken')
        def resp = restClient.get(canvasBaseURL + '/api/v1/courses/' + courseId + '/users?enrollment_type[]=student&enrollment_state[]=active&per_page=100'){
            auth('Bearer ' + oauthToken)
        }
        populateUserLogins(resp, users)

        def nextPage = canvasNextPage(resp)
        while(nextPage != null){
            resp = restClient.get(nextPage){
                auth('Bearer ' + oauthToken)
            }
            populateUserLogins(resp, users)
            nextPage = canvasNextPage(resp)
        }

        return users
    }

    private static def populateUserLogins(RestResponse resp, users){
        JSONArray respArr = (JSONArray)resp.json
        for(jsonObj in respArr){
            users.add(jsonObj.login_id)
        }
    }

    def listUserDetails(String courseId){
        def users = []
        def canvasBaseURL = grailsApplication.config.getProperty('canvas.canvasBaseUrl')
        def oauthToken = grailsApplication.config.getProperty('canvas.oauthToken')
        def resp = restClient.get(canvasBaseURL + '/api/v1/courses/' + courseId + '/users?enrollment_type[]=student&enrollment_state[]=active&per_page=100'){
            auth('Bearer ' + oauthToken)
        }
        populateUserDetails(resp, users)

        def nextPage = canvasNextPage(resp)
        while(nextPage != null){
            resp = restClient.get(nextPage){
                auth('Bearer ' + oauthToken)
            }
            populateUserDetails(resp, users)
            nextPage = canvasNextPage(resp)
        }

        return users
    }

    private static def populateUserDetails(RestResponse resp, users){
        JSONArray respArr = (JSONArray)resp.json
        for(jsonObj in respArr){
            String sortableName = jsonObj.sortable_name
            String[] splitName = sortableName.split(',')
            def user = [jsonObj.login_id, splitName[0].trim(), splitName[1].trim()]
            users.add(user)
        }
    }

    def canvasNextPage(RestResponse resp){
        String linkHeader = resp.headers.getFirst('Link')
        String nextLink = null
        if(linkHeader != null){
            String[] links = linkHeader.split(',')
            for(link in links){
                String[] linkParts = link.split(';')
                String relVal = linkParts[0]
                String relType = linkParts[1]
                if(relType.contains('next')){
                    nextLink = relVal.substring(1,relVal.length()-1)
                    break
                }
            }
        }
        return nextLink
    }

    def updateFileName(String fileId, String fileName){
        def canvasBaseURL = grailsApplication.config.getProperty('canvas.canvasBaseUrl')
        def oauthToken = grailsApplication.config.getProperty('canvas.oauthToken')
        restClient.put(canvasBaseURL + '/api/v1/files/' + fileId){
            auth('Bearer ' + oauthToken)
            json{
                name = fileName
                on_duplicate = 'rename'
            }
        }
    }
}
