package postem.tool

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(UserFileViewLog)
class UserFileViewLogSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "completed user_file_view_log creation"() {
        given: "a complete new user_file_view_log"
            def userFileViewLog = new UserFileViewLog(loginId: 'abc123', fileId: '123456', lastViewed: new Date())
        expect: "we can save a complete user_file_view_log"
            true == userFileViewLog.validate()
    }

    void "can't save a user_file_view_log without a loginId"(){
        given: "a user_file_view_log without a loginId"
            def userFileViewLog = new UserFileViewLog(loginId: '', fileId: '123456', lastViewed: new Date())
        expect: "we can't save the user_file_view_log"
            false == userFileViewLog.validate()
    }
}
