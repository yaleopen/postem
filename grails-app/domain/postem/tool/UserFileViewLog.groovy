package postem.tool

class UserFileViewLog {

    String loginId
    String fileId
    Date lastViewed

    static mapping = {
        version false
    }

    static constraints = {
        loginId blank: false
        fileId blank: false
        lastViewed blank: false
    }
}