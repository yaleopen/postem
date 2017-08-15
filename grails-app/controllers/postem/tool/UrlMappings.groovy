package postem.tool

import org.apache.tomcat.util.http.fileupload.FileUploadBase

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:"/error")
        "500"(controller: "instructor", action: 'handleSizeLimitExceededException', exception: FileUploadBase.SizeLimitExceededException)
        "404"(view:'/notFound')
    }
}
